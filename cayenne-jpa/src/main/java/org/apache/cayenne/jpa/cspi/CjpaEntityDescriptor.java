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


package org.apache.cayenne.jpa.cspi;

import java.util.Iterator;
import java.util.Map;

import org.apache.cayenne.map.EntityDescriptor;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.property.ClassDescriptor;
import org.apache.cayenne.property.FieldAccessor;
import org.apache.cayenne.property.ListProperty;
import org.apache.cayenne.property.PropertyAccessException;
import org.apache.cayenne.property.PropertyAccessor;

class CjpaEntityDescriptor extends EntityDescriptor {

    public CjpaEntityDescriptor(ObjEntity entity, ClassDescriptor superclassDescriptor) {
        super(entity, superclassDescriptor);
    }

    @Override
    protected void compileRelationships(EntityResolver resolver, Map allDescriptors) {
        super.compileRelationships(resolver, allDescriptors);

        // override all ListProperties with a customized version...

        Iterator it = allDescriptors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() instanceof ListProperty) {

                String name = entry.getKey().toString();
                ListProperty overridenProperty = (ListProperty) entry.getValue();

                PropertyAccessor accessor = new CjpaCollectionFieldAccessor(
                        objectClass,
                        name,
                        null);

                ObjRelationship relationship = (ObjRelationship) entity
                        .getRelationship(name);

                entry.setValue(new CjpaCollectionProperty(this, overridenProperty
                        .getTargetDescriptor(), accessor, relationship
                        .getReverseRelationshipName()));
            }
        }
    }

    /**
     * Overrides super to return field accessor, regardless of whether the class is a
     * DataObject or not.
     */
    @Override
    protected PropertyAccessor makeAccessor(String propertyName, Class propertyType)
            throws PropertyAccessException {
        try {
            return new FieldAccessor(objectClass, propertyName, propertyType);
        }
        catch (Throwable th) {

            throw new PropertyAccessException("Can't create accessor for property '"
                    + propertyName
                    + "' of class '"
                    + objectClass.getName()
                    + "'", null, null);
        }
    }
}
