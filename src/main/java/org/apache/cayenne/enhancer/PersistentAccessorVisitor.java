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

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.Relationship;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Accessor enhancer that enhances getters and setters mapped in a given {@link ObjEntity}.
 * 
 * @author Andrus Adamchik
 * @since 3.0
 */
public class PersistentAccessorVisitor extends AccessorVisitor {

    private ObjEntity entity;
    private EnhancementHelper helper;

    public PersistentAccessorVisitor(ClassVisitor visitor, ObjEntity entity) {
        super(visitor);
        this.entity = entity;
        this.helper = new EnhancementHelper(this);
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {

        helper.reset(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    protected MethodVisitor visitGetter(
            MethodVisitor mv,
            String property,
            Type propertyType) {

        if (entity.getAttribute(property) != null) {
            return new GetterVisitor(mv, helper, property);
        }

        Relationship r = entity.getRelationship(property);
        if (r != null && !r.isToMany()) {
            // inject fault flag field
            helper.createField(Boolean.TYPE, "faultResolved_" + property, true);
            return new GetterVisitor(mv, helper, property);
        }

        return mv;
    }

    @Override
    protected MethodVisitor visitSetter(
            MethodVisitor mv,
            String property,
            Type propertyType) {

        if (entity.getAttribute(property) != null) {
            return new SetterVisitor(mv, helper, property, propertyType);
        }

        return mv;
    }
}
