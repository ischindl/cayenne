/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002-2003 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.cayenne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.EntityResolver;
import org.objectstyle.cayenne.access.util.RelationshipFault;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.map.ObjRelationship;
import org.objectstyle.cayenne.util.PropertyComparator;

/**
 * A CayenneDataObject is a default implementation of DataObject interface.
 * It is normally used as a superclass of Cayenne persistent objects.
 *
 * @author Andrei Adamchik
 */
public class CayenneDataObject implements DataObject {
    private static Logger logObj = Logger.getLogger(CayenneDataObject.class);

    protected ObjectId objectId;
    protected transient int persistenceState = PersistenceState.TRANSIENT;
    protected transient DataContext dataContext;
    protected Map props = new HashMap();

    /** Returns a data context this object is registered with, or null
     * if this object has no associated DataContext */
    public DataContext getDataContext() {
        return dataContext;
    }

    public void setDataContext(DataContext ctxt) {
        dataContext = ctxt;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public int getPersistenceState() {
        return persistenceState;
    }

    public void setPersistenceState(int newState) {
        persistenceState = newState;
    }

    public Object readNestedProperty(String path) {
        Object object = null;
        CayenneDataObject dataObject = this;
        String[] tokenized = tokenizePath(path);
        int length = tokenized.length;
        
        for (int i = 0; i < length; i++) {
            
            object = dataObject.readSimpleProperty(tokenized[i]);

            if (object == null) {
                return null;
            }
            else if (object instanceof CayenneDataObject) {
                dataObject = (CayenneDataObject) object;
            }
            else if (i + 1 < length) {
                throw new CayenneRuntimeException("Invalid path: " + path);
            }
        }

        return object;
    }
    
    private static final String[] tokenizePath(String path) {
        if (path == null) {
            throw new NullPointerException("Null property path.");
        }

        if (path.length() == 0) {
            throw new IllegalArgumentException("Empty property path.");
        }

        // take a shortcut for simple properties
        if (path.indexOf(".") < 0) {
            return new String[] { path };
        }
        
        StringTokenizer tokens = new StringTokenizer(path, ".");
        int length = tokens.countTokens();
        String[] tokenized = new String[length];
        for(int i = 0; i < length; i++) {
            tokenized[i] = tokens.nextToken();
        }
        
        return tokenized;
    }
    
    private final Object readSimpleProperty(String property) {
        // side effect - resolves HOLLOW object
        Object object = readProperty(property);

        // if a null value is returned, 
        // there is still a chance to find a non-persistent property
        // via reflection
        if (object == null && !props.containsKey(property)) {
            try {
                object = PropertyComparator.readProperty(property, this);
            }
            catch (IllegalAccessException e) {
                throw new CayenneRuntimeException(
                    "Error reading property '" + property + "'.",
                    e);
            }
            catch (InvocationTargetException e) {
                throw new CayenneRuntimeException(
                    "Error reading property '" + property + "'.",
                    e);
            }
            catch (NoSuchMethodException e) {
                // ignoring, no such property exists
            }
        }
        
        return object;
    }

    protected Object readProperty(String propName) {
        if (persistenceState == PersistenceState.HOLLOW) {
            try {
                dataContext.refetchObject(objectId);
            }
            catch (Exception ex) {
                // TODO: add some sort of delegate method here. Quietly
                // making object TRANSIENT doesn't seem right
                logObj.info("Error refetching object, making transient.", ex);
                setPersistenceState(PersistenceState.TRANSIENT);
            }
        }

        Object object = readPropertyDirectly(propName);

        // must resolve faults immediately
        if (object instanceof RelationshipFault) {
            // for now assume we just have to-one faults...
            // after all to-many are represented by ToManyList
            object = ((RelationshipFault) object).resolveToOne();
            writePropertyDirectly(propName, object);
        }

        return object;
    }

    public Object readPropertyDirectly(String propName) {
        return props.get(propName);
    }

    protected void writeProperty(String propName, Object val) {
        if (persistenceState == PersistenceState.HOLLOW) {
            try {
                dataContext.refetchObject(objectId);
                persistenceState = PersistenceState.MODIFIED;
            }
            catch (Exception ex) {
                // TODO: add some sort of delegate method here. Quietly
                // making object TRANSIENT doesn't seem right
                logObj.info("Error refetching object, making transient.", ex);
                setPersistenceState(PersistenceState.TRANSIENT);
            }
        }
        else if (persistenceState == PersistenceState.COMMITTED) {
            persistenceState = PersistenceState.MODIFIED;
        }

        writePropertyDirectly(propName, val);
    }

    public void writePropertyDirectly(String propName, Object val) {
        props.put(propName, val);
    }

	/**
	 * @deprecated Since 1.0.1 this method is no longer needed, since "readProperty(String)" 
	 * supports to-one dependent targets.
	 */
	public DataObject readToOneDependentTarget(String relName) {
		return (DataObject) readProperty(relName);
	}

    public void removeToManyTarget(String relName, DataObject val, boolean setReverse) {
        ObjRelationship relationship = this.getRelationshipNamed(relName);
        //Only delete the internal object if we should "setReverse" (or rather, if we aren't not setting the reverse).
        //This kind of doubles up the meaning of that flag, so we may need to add another?
        if (relationship.isFlattened() && setReverse) {
            if (relationship.isReadOnly()) {
                throw new CayenneRuntimeException(
                    "Cannot modify (remove from) the read-only relationship " + relName);
            }
            //Handle removing from a flattened relationship
            dataContext.registerFlattenedRelationshipDelete(this, relationship, val);
        }

        //Now do the rest of the normal handling (regardless of whether it was flattened or not)
        List relList = (List) readProperty(relName);
        relList.remove(val);
        if (persistenceState == PersistenceState.COMMITTED) {
            persistenceState = PersistenceState.MODIFIED;
        }

        if (val != null && setReverse) {
            unsetReverseRelationship(relName, val);
        }
    }

    public void addToManyTarget(String relName, DataObject val, boolean setReverse) {
        if ((val != null) && (dataContext != val.getDataContext())) {
            throw new CayenneRuntimeException(
                "Cannot add object to relationship "
                    + relName
                    + " because it is in a different DataContext");
        }
        ObjRelationship relationship = this.getRelationshipNamed(relName);
        if (relationship == null) {
            throw new CayenneRuntimeException(
                "Cannot add object to relationship "
                    + relName
                    + " because there is no relationship by that name");
        }
        //Only create the internal object if we should "setReverse" (or rather, if we aren't not setting the reverse).
        //This kind of doubles up the meaning of that flag, so we may need to add another?
        if (relationship.isFlattened() && setReverse) {
            if (relationship.isReadOnly()) {
                throw new CayenneRuntimeException(
                    "Cannot modify (add to) the read-only relationship " + relName);
            }
            //Handle adding to a flattened relationship
            dataContext.registerFlattenedRelationshipInsert(this, relationship, val);
        }

        //Now do the rest of the normal handling (regardless of whether it was flattened or not)
        List relList = (List) readProperty(relName);
        relList.add(val);
        if (persistenceState == PersistenceState.COMMITTED) {
            persistenceState = PersistenceState.MODIFIED;
        }

        if (val != null && setReverse)
            setReverseRelationship(relName, val);
    }

	/**
	 * @deprecated Since 1.0.1 this method is no longer needed, since 
	 * "setToOneTarget(String, DataObject, boolean)" supports dependent targets 
	 * as well.
	 */
	public void setToOneDependentTarget(String relName, DataObject val) {
		setToOneTarget(relName, val, true);
	}

    public void setToOneTarget(String relName, DataObject val, boolean setReverse) {
        // 1: val==null... dataContext of value is unobtainable, and hence irrelevant
        if ((val != null)
            && (dataContext != val.getDataContext())) {
            throw new CayenneRuntimeException(
                "Cannot set object as destination of relationship "
                    + relName
                    + " because it is in a different DataContext");
        }

        Object oldTarget = readPropertyDirectly(relName);
        if (oldTarget == val) {
            return;
        }

        ObjRelationship relationship = this.getRelationshipNamed(relName);
        if (relationship.isFlattened()) {
            if (relationship.isReadOnly()) {
                throw new CayenneRuntimeException(
                    "Cannot modify the read-only flattened relationship " + relName);
            }
            
            // Handle adding to a flattened relationship
            dataContext.registerFlattenedRelationshipInsert(this, relationship, val);
        }
        
               
        if (setReverse) {
            // unset old reverse relationship
            if (oldTarget instanceof DataObject)
                unsetReverseRelationship(relName, (DataObject) oldTarget);

            // set new reverse relationship
            if (val != null)
                setReverseRelationship(relName, val);
        }

        writeProperty(relName, val);
    }

    private ObjRelationship getRelationshipNamed(String relName) {
        return (ObjRelationship) dataContext
            .getEntityResolver()
            .lookupObjEntity(this)
            .getRelationship(relName);
    }

    /**
     * Initializes reverse relationship from object <code>val</code>
     * to this object.
     *
     * @param relName name of relationship from this object
     * to <code>val</code>.
     */
    protected void setReverseRelationship(String relName, DataObject val) {
        ObjRelationship rel =
            (ObjRelationship) dataContext
                .getEntityResolver()
                .lookupObjEntity(objectId.getObjClass())
                .getRelationship(relName);
        ObjRelationship revRel = rel.getReverseRelationship();
        if (revRel != null) {
            if (revRel.isToMany())
                val.addToManyTarget(revRel.getName(), this, false);
            else
                val.setToOneTarget(revRel.getName(), this, false);
        }
    }

    /** 
     * Removes current object from reverse relationship of object
     * <code>val</code> to this object.
     */
    protected void unsetReverseRelationship(String relName, DataObject val) {
        Class aClass = objectId.getObjClass();
        EntityResolver resolver = dataContext.getEntityResolver();
        ObjEntity entity = resolver.lookupObjEntity(aClass);

        if (entity == null) {
            String className = (aClass != null) ? aClass.getName() : "<null>";
            throw new IllegalStateException(
                "DataObject's class is unmapped: " + className);
        }

        ObjRelationship rel = (ObjRelationship) entity.getRelationship(relName);
        ObjRelationship revRel = rel.getReverseRelationship();
        if (revRel != null) {
            if (revRel.isToMany())
                val.removeToManyTarget(revRel.getName(), this, false);
            else
                val.setToOneTarget(revRel.getName(), null, false);
        }
    }

    public Map getCommittedSnapshot() {
        return dataContext.getObjectStore().getSnapshot(getObjectId());
    }

    public Map getCurrentSnapshot() {
        return dataContext.takeObjectSnapshot(this);
    }

    /** A variation of  "toString" method, that may be more efficient in some cases.
     *  For example when printing a list of objects into the same String. */
    public StringBuffer toStringBuffer(StringBuffer buf, boolean fullDesc) {
        // log all properties
        buf.append('{');

        if (fullDesc)
            appendProperties(buf);

        buf
            .append("<oid: ")
            .append(objectId)
            .append("; state: ")
            .append(PersistenceState.persistenceStateName(persistenceState))
            .append(">}\n");
        return buf;
    }

    protected void appendProperties(StringBuffer buf) {
        buf.append("[");
        Iterator it = props.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            buf.append('\t').append(key).append(" => ");
            Object val = props.get(key);

            if (val instanceof CayenneDataObject) {
                ((CayenneDataObject) val).toStringBuffer(buf, false);
            }
            else if (val instanceof List) {
                buf.append('(').append(val.getClass().getName()).append(')');
            }
            else
                buf.append(val);

            buf.append('\n');
        }

        buf.append("]");
    }

    public String toString() {
        return toStringBuffer(new StringBuffer(), true).toString();
    }

    /**
     * Default implementation does nothing.
     *
     * @see org.objectstyle.cayenne.DataObject#fetchFinished()
     */
    public void fetchFinished() {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(persistenceState);

        switch (persistenceState) {
            //New, modified or transient - write the whole shebang
            //The other states (committed, hollow, deleted) all need just ObjectId
            case PersistenceState.TRANSIENT :
            case PersistenceState.NEW :
            case PersistenceState.MODIFIED :
                out.writeObject(props);
                break;
        }

        out.writeObject(objectId);
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        this.persistenceState = in.readInt();

        switch (persistenceState) {
            case PersistenceState.TRANSIENT :
            case PersistenceState.NEW :
            case PersistenceState.MODIFIED :
                props = (Map) in.readObject();
                break;
            case PersistenceState.COMMITTED :
            case PersistenceState.HOLLOW :
            case PersistenceState.DELETED :
                this.persistenceState = PersistenceState.HOLLOW;
                //props will be populated when required (readProperty called)
                props = new HashMap();
                break;
        }

        this.objectId = (ObjectId) in.readObject();
        // dataContext will be set *IFF* the datacontext it came from is also
        // deserialized.  Setting of datacontext is handled by the datacontext itself
    }
}
