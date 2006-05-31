/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors
 * of the software. All rights reserved.
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
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowlegement:
 *    "This product includes software developed by independent contributors
 *    and hosted on ObjectStyle Group web site (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, email
 *    "andrus at objectstyle dot org".
 * 
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    or "Cayenne", nor may "ObjectStyle" or "Cayenne" appear in their
 *    names without prior written permission.
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
 * individuals and hosted on ObjectStyle Group web site.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 */
package org.objectstyle.cayenne;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.map.DbAttribute;
import org.objectstyle.cayenne.map.DbEntity;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.query.ObjectIdQuery;
import org.objectstyle.cayenne.query.Query;

/**
 * A collection of utility methods to work with DataObjects.
 * <p>
 * <i>DataObjects and Primary Keys: All methods that allow to extract primary key values
 * or use primary keys to find objects are provided for convenience. Still the author's
 * belief is that integer sequential primary keys are meaningless in the object model and
 * are pure database artifacts. Therefore relying heavily on direct access to PK provided
 * via this class (or other such Cayenne API) is not a clean design practice in many
 * cases, and sometimes may actually lead to security issues. </i>
 * </p>
 * 
 * @since 1.1
 * @author Andrei Adamchik
 */
public final class DataObjectUtils {

    /**
     * Returns an int primary key value for a DataObject. Only works for single column
     * numeric primary keys. If a DataObjects is transient or has an ObjectId that can not
     * be converted to an int PK, an exception is thrown.
     */
    public static int intPKForObject(Persistent dataObject) {
        Object value = pkForObject(dataObject);

        if (!(value instanceof Number)) {
            throw new CayenneRuntimeException("PK is not a number: "
                    + dataObject.getObjectId());
        }

        return ((Number) value).intValue();
    }

    /**
     * Returns a primary key value for a DataObject. Only works for single column primary
     * keys. If a DataObjects is transient or has a compound ObjectId, an exception is
     * thrown.
     */
    public static Object pkForObject(Persistent dataObject) {
        Map pk = extractObjectId(dataObject);

        if (pk.size() != 1) {
            throw new CayenneRuntimeException("Expected single column PK, got "
                    + pk.size()
                    + " columns, ID: "
                    + pk);
        }

        Map.Entry pkEntry = (Map.Entry) pk.entrySet().iterator().next();
        return pkEntry.getValue();
    }

    /**
     * Returns a primary key map for a DataObject. This method is the most generic out of
     * all methods for primary key retrieval. It will work for all possible types of
     * primary keys. If a DataObjects is transient, an exception is thrown.
     */
    public static Map compoundPKForObject(Persistent dataObject) {
        return Collections.unmodifiableMap(extractObjectId(dataObject));
    }

    static Map extractObjectId(Persistent dataObject) {
        if (dataObject == null) {
            throw new IllegalArgumentException("Null DataObject");
        }

        ObjectId id = dataObject.getObjectId();
        if (!id.isTemporary()) {
            return id.getIdSnapshot();
        }

        // replacement ID is more tricky... do some sanity check...
        if (id.isReplacementIdAttached()) {
            DbEntity entity = dataObject
                    .getObjectContext()
                    .getEntityResolver()
                    .lookupDbEntity(dataObject);

            if (entity != null && entity.isFullReplacementIdAttached(id)) {
                return id.getReplacementIdMap();
            }
        }

        throw new CayenneRuntimeException("Can't get primary key from temporary id.");
    }

    /**
     * Returns an object matching an int primary key. If the object is mapped to use
     * non-integer PK or a compound PK, CayenneRuntimeException is thrown.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            Class dataObjectClass,
            int pk) {
        return objectForPK(context, buildId(context, dataObjectClass, new Integer(pk)));
    }

    /**
     * Returns an object matching an Object primary key. If the object is mapped to use a
     * compound PK, CayenneRuntimeException is thrown.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            Class dataObjectClass,
            Object pk) {

        return objectForPK(context, buildId(context, dataObjectClass, pk));
    }

    /**
     * Returns an object matching a primary key. PK map parameter should use database PK
     * column names as keys.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            Class dataObjectClass,
            Map pk) {

        ObjEntity entity = context.getEntityResolver().lookupObjEntity(dataObjectClass);
        if (entity == null) {
            throw new CayenneRuntimeException("Non-existent ObjEntity for class: "
                    + dataObjectClass);
        }

        return objectForPK(context, new ObjectId(entity.getName(), pk));
    }

    /**
     * Returns an object matching an int primary key. If the object is mapped to use
     * non-integer PK or a compound PK, CayenneRuntimeException is thrown.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            String objEntityName,
            int pk) {
        return objectForPK(context, buildId(context, objEntityName, new Integer(pk)));
    }

    /**
     * Returns an object matching an Object primary key. If the object is mapped to use a
     * compound PK, CayenneRuntimeException is thrown.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            String objEntityName,
            Object pk) {
        return objectForPK(context, buildId(context, objEntityName, pk));
    }

    /**
     * Returns an object matching a primary key. PK map parameter should use database PK
     * column names as keys.
     * <p>
     * If this object is already cached in the ObjectStore, it is returned without a
     * query. Otherwise a query is built and executed against the database.
     * </p>
     * 
     * @see #objectForPK(ObjectContext, ObjectId)
     */
    public static DataObject objectForPK(
            ObjectContext context,
            String objEntityName,
            Map pk) {
        if (objEntityName == null) {
            throw new IllegalArgumentException("Null ObjEntity name.");
        }

        return objectForPK(context, new ObjectId(objEntityName, pk));
    }

    /**
     * Returns an object matching ObjectId. If this object is already cached in the
     * ObjectStore, it is returned without a query. Otherwise a query is built and
     * executed against the database.
     * 
     * @return A DataObject that matched the id, null if no matching objects were found
     * @throws CayenneRuntimeException if more than one object matched ObjectId.
     */
    public static DataObject objectForPK(ObjectContext context, ObjectId id) {
        return (DataObject) DataObjectUtils.objectForQuery(context, new ObjectIdQuery(
                id,
                false,
                ObjectIdQuery.CACHE));
    }

    /**
     * Returns a DataObject or a DataRow that is a result of a given query. If query
     * returns more than one object, an exception is thrown. If query returns no objects,
     * null is returned.
     * 
     * @since 1.2
     */
    public static Object objectForQuery(ObjectContext context, Query query) {
        List objects = context.performQuery(query);

        if (objects.size() == 0) {
            return null;
        }
        else if (objects.size() > 1) {
            throw new CayenneRuntimeException(
                    "Expected zero or one object, instead query matched: "
                            + objects.size());
        }

        return objects.get(0);
    }

    static ObjectId buildId(ObjectContext context, String objEntityName, Object pk) {
        if (pk == null) {
            throw new IllegalArgumentException("Null PK");
        }

        if (objEntityName == null) {
            throw new IllegalArgumentException("Null ObjEntity name.");
        }

        ObjEntity entity = context.getEntityResolver().getObjEntity(objEntityName);
        if (entity == null) {
            throw new CayenneRuntimeException("Non-existent ObjEntity: " + objEntityName);
        }

        DbEntity dbEntity = entity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + entity.getName());
        }

        List pkAttributes = dbEntity.getPrimaryKey();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = (DbAttribute) pkAttributes.get(0);
        return new ObjectId(objEntityName, attr.getName(), pk);
    }

    static ObjectId buildId(ObjectContext context, Class dataObjectClass, Object pk) {
        if (pk == null) {
            throw new IllegalArgumentException("Null PK");
        }

        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Null DataObject class.");
        }

        ObjEntity entity = context.getEntityResolver().lookupObjEntity(dataObjectClass);
        if (entity == null) {
            throw new CayenneRuntimeException("Unmapped DataObject Class: "
                    + dataObjectClass.getName());
        }

        DbEntity dbEntity = entity.getDbEntity();
        if (dbEntity == null) {
            throw new CayenneRuntimeException("No DbEntity for ObjEntity: "
                    + entity.getName());
        }

        List pkAttributes = dbEntity.getPrimaryKey();
        if (pkAttributes.size() != 1) {
            throw new CayenneRuntimeException("PK contains "
                    + pkAttributes.size()
                    + " columns, expected 1.");
        }

        DbAttribute attr = (DbAttribute) pkAttributes.get(0);
        return new ObjectId(entity.getName(), attr.getName(), pk);
    }

    // not intended for instantiation
    private DataObjectUtils() {
    }
}