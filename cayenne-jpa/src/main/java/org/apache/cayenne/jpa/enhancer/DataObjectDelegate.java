/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/


package org.apache.cayenne.jpa.enhancer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.types.ExtendedTypeMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.property.ClassDescriptor;
import org.apache.cayenne.property.Property;
import org.apache.cayenne.property.PropertyUtils;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

/**
 * A static delegate for DataObject callbacks. To obtain class descriptors,
 * DataObjectDelegate will use a DataObject context first, and then fall back to the
 * DataContext bound to the current thread. If none of these methods work, and exception
 * is thrown.
 * 
 * @author Andrus Adamchik
 */
// TODO: andrus, 5/2/2006 - going ahead, the delegate should only impement the validation
// methods, as property access by the framework should be done via ClassDescriptor.
public final class DataObjectDelegate {

    public static void beforeGetProperty(Persistent object, String propertyName) {
        ObjectContext context = object.getObjectContext();
        if (context != null) {
            context.prepareForAccess(object, propertyName);
        }
    }

    public static void beforeSetProperty(Persistent object, String propertyName) {
        ObjectContext context = object.getObjectContext();
        if (context != null) {
            context.prepareForAccess(object, propertyName);
        }
    }

    public static void afterSetProperty(
            Persistent object,
            String propertyName,
            Object oldValue,
            Object newValue) {

        ObjectContext context = object.getObjectContext();
        if (context != null) {
            context.propertyChanged(object, propertyName, oldValue, newValue);
        }
    }

    public static DataContext getDataContext(DataObject object) {
        ObjectContext context = object.getObjectContext();
        if (context == null || context instanceof DataContext) {
            return (DataContext) context;
        }

        throw new CayenneRuntimeException("ObjectContext is not a DataContext: "
                + context);
    }

    public static void setDataContext(DataObject object, DataContext dataContext) {
        object.setObjectContext(dataContext);
    }

    public static Object readNestedProperty(DataObject object, String path) {
        return PropertyUtils.getProperty(object, path);
    }

    /**
     * @since 1.1
     * @deprecated since 1.2 use 'getObjectContext().prepareForAccess(object)'
     */
    public static void resolveFault(DataObject object) {
        throw new UnsupportedOperationException("Not supported");
    }

    public static Object readProperty(DataObject object, String property) {
        return getPropertyDescriptor(object, property).readProperty(object);
    }

    public static Object readPropertyDirectly(DataObject object, String property) {
        return getPropertyDescriptor(object, property).readPropertyDirectly(object);
    }

    public static void writeProperty(DataObject object, String property, Object newValue) {
        Property p = getPropertyDescriptor(object, property);
        p.writeProperty(object, p.readPropertyDirectly(object), newValue);
    }

    public static void writePropertyDirectly(
            DataObject object,
            String property,
            Object newValue) {

        Property p = getPropertyDescriptor(object, property);
        p.writePropertyDirectly(object, p.readPropertyDirectly(object), newValue);
    }

    public static void removeToManyTarget(
            DataObject object,
            String relName,
            DataObject value,
            boolean setReverse) {

        throw new UnsupportedOperationException("Not supported");
    }

    public static void addToManyTarget(
            DataObject object,
            String relName,
            DataObject value,
            boolean setReverse) {
        throw new UnsupportedOperationException("Not supported");
    }

    public static void setToOneTarget(
            DataObject object,
            String relationshipName,
            DataObject value,
            boolean setReverse) {

        throw new UnsupportedOperationException("Not supported");
    }

    public static void fetchFinished(DataObject object) {
        // noop
    }

    public static long getSnapshotVersion(DataObject object) {
        throw new UnsupportedOperationException("Not supported");
    }

    public static void setSnapshotVersion(DataObject object, long snapshotVersion) {
        throw new UnsupportedOperationException("Not supported");
    }

    public static void validateForInsert(
            DataObject object,
            ValidationResult validationResult) {
        validateForSave(object, validationResult);
    }

    /**
     * Returns a Cayenne ClassDescriptor for the enhanced DataObject class. To obtain
     * class descriptors, uses a DataObject context first, and then falls back to the
     * DataContext bound to the current thread. If none of these methods work, and
     * exception is thrown.
     */
    protected static ClassDescriptor getDescriptor(DataObject enhancedObject)
            throws IllegalStateException, IllegalArgumentException {
        ObjectContext context = enhancedObject.getObjectContext();

        if (context == null) {

            try {
                context = DataContext.getThreadDataContext();
            }
            catch (IllegalStateException e) {
                // catch a generic exception trown by DataContext and rethrow a more
                // informative one
                throw new IllegalStateException("Object is not registered, and no "
                        + "DataContext is bound to the current thread. Object: "
                        + enhancedObject);
            }
        }

        String entityName;

        ObjectId oid = enhancedObject.getObjectId();
        if (oid != null) {
            entityName = oid.getEntityName();
        }
        else {
            ObjEntity entity = context.getEntityResolver().lookupObjEntity(
                    enhancedObject.getClass());
            entityName = entity.getName();
        }

        ClassDescriptor descriptor = context.getEntityResolver().getClassDescriptor(
                entityName);

        if (descriptor == null) {
            throw new IllegalArgumentException("No descriptor found for object "
                    + enhancedObject);
        }

        return descriptor;
    }

    protected static Property getPropertyDescriptor(
            DataObject enhancedObject,
            String property) {

        Property p = getDescriptor(enhancedObject).getProperty(property);

        if (p == null) {
            throw new IllegalArgumentException("No property descriptor '"
                    + property
                    + "' for object "
                    + enhancedObject);
        }

        return p;
    }

    public static void validateForUpdate(
            DataObject object,
            ValidationResult validationResult) {
        validateForSave(object, validationResult);
    }

    public static void validateForDelete(
            DataObject object,
            ValidationResult validationResult) {
        // does nothing
    }

    protected static void validateForSave(
            DataObject object,
            ValidationResult validationResult) {

        EntityResolver resolver = object.getObjectContext().getEntityResolver();
        ObjEntity objEntity = resolver.lookupObjEntity(object);
        if (objEntity == null) {
            throw new CayenneRuntimeException(
                    "No ObjEntity mapping found for DataObject "
                            + object.getClass().getName());
        }

        DataNode node = getDataContext(object).getParentDataDomain().lookupDataNode(
                objEntity.getDataMap());
        if (node == null) {
            throw new CayenneRuntimeException("No DataNode found for objEntity: "
                    + objEntity.getName());
        }

        ExtendedTypeMap types = node.getAdapter().getExtendedTypes();

        // validate mandatory attributes

        // handling a special case - meaningful mandatory FK... defer failures until
        // relationship validation is done... This is just a temporary solution, as
        // handling meaningful keys within the object lifecycle requires something more,
        // namely read/write methods for relationships and direct values should be
        // synchronous with each other..
        Map failedDbAttributes = null;

        ClassDescriptor descriptor = resolver.getClassDescriptor(objEntity.getName());
        Iterator attributes = objEntity.getAttributes().iterator();
        while (attributes.hasNext()) {
            ObjAttribute objAttribute = (ObjAttribute) attributes.next();
            DbAttribute dbAttribute = objAttribute.getDbAttribute();

            Object value = descriptor
                    .getDeclaredProperty(objAttribute.getName())
                    .readPropertyDirectly(object);
            if (dbAttribute.isMandatory()) {
                ValidationFailure failure = BeanValidationFailure.validateNotNull(
                        object,
                        objAttribute.getName(),
                        value);

                if (failure != null) {

                    if (failedDbAttributes == null) {
                        failedDbAttributes = new HashMap();
                    }

                    failedDbAttributes.put(dbAttribute.getName(), failure);
                    continue;
                }
            }

            if (value != null) {

                // TODO: should we pass null values for validation as well?
                // if so, class can be obtained from ObjAttribute...

                types.getRegisteredType(value.getClass()).validateProperty(
                        object,
                        objAttribute.getName(),
                        value,
                        dbAttribute,
                        validationResult);
            }
        }

        // validate mandatory relationships
        Iterator relationships = objEntity.getRelationships().iterator();
        while (relationships.hasNext()) {
            ObjRelationship relationship = (ObjRelationship) relationships.next();

            if (relationship.isSourceIndependentFromTargetChange()) {
                continue;
            }

            List dbRels = relationship.getDbRelationships();
            if (dbRels.isEmpty()) {
                // Wha?
                continue;
            }

            // if db relationship is not based on a PK and is based on mandatory
            // attributes, see if we have a target object set
            boolean validate = true;
            DbRelationship dbRelationship = (DbRelationship) dbRels.get(0);
            Iterator joins = dbRelationship.getJoins().iterator();
            while (joins.hasNext()) {
                DbJoin join = (DbJoin) joins.next();
                DbAttribute source = join.getSource();

                if (source.isMandatory()) {
                    // clear attribute failures...
                    if (failedDbAttributes != null && !failedDbAttributes.isEmpty()) {
                        failedDbAttributes.remove(source.getName());

                        // loop through all joins if there were previous mandatory

                        // attribute failures....
                        if (!failedDbAttributes.isEmpty()) {
                            continue;
                        }
                    }
                }
                else {
                    // do not validate if the relation is based on
                    // multiple keys with some that can be nullable.
                    validate = false;
                }
            }

            if (validate) {
                Object value = descriptor
                        .getDeclaredProperty(relationship.getName())
                        .readPropertyDirectly(object);
                ValidationFailure failure = BeanValidationFailure.validateNotNull(
                        object,
                        relationship.getName(),
                        value);

                if (failure != null) {
                    validationResult.addFailure(failure);
                    continue;
                }
            }

        }

        // deal with previously found attribute failures...
        if (failedDbAttributes != null && !failedDbAttributes.isEmpty()) {
            Iterator failedAttributes = failedDbAttributes.values().iterator();
            while (failedAttributes.hasNext()) {
                validationResult.addFailure((ValidationFailure) failedAttributes.next());
            }
        }
    }

}
