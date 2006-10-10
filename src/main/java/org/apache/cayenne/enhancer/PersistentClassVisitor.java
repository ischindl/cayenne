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
package org.apache.cayenne.enhancer;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.ObjEntity;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * ASM-based visitor that turns a pojo class into enahnced persistent object.
 * 
 * @author Andrus Adamchik
 * @since 3.0
 */
class PersistentClassVisitor extends ClassAdapter {

    private ObjEntity entity;
    private ClassVisitorHelper helper;

    PersistentClassVisitor(ClassVisitor visitor, ObjEntity entity) {
        super(visitor);
        this.entity = entity;
        this.helper = new ClassVisitorHelper(this);
    }

    /**
     * Handles injection of additional fields and Persistent interface properties.
     */
    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {

        helper.reset(name);
        interfaces = helper.addInterface(interfaces, Persistent.class);

        super.visit(version, access, name, signature, superName, interfaces);

        helper.createProperty(ObjectId.class, "objectId");
        helper.createProperty(ObjectContext.class, "objectContext", true);
        helper.createProperty(Integer.TYPE, "persistenceState");
    }

    /**
     * Handles getter and setter enhancements.
     */
    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        // TODO: andrus, 10/8/2006 - check method sig for real... just checking
        // the name is not enough

        String getProperty = EnhancerUtil.propertyNameForGetter(name);
        if (getProperty != null) {
            if (entity.getAttribute(getProperty) != null) {
                return new PersistentGetterVisitor(mv, helper, getProperty);
            }
        }
        else {
            String setProperty = EnhancerUtil.propertyNameForSetter(name);
            if (setProperty != null && entity.getAttribute(setProperty) != null) {
                return new PersistentSetterVisitor(mv, helper, setProperty);
            }
        }

        return mv;
    }
}
