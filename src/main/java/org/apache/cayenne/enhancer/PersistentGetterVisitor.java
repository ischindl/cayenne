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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @since 3.0
 * @author Andrus Adamchik
 */
class PersistentGetterVisitor extends MethodAdapter {

    private ClassVisitorHelper helper;
    private String propertyName;

    PersistentGetterVisitor(MethodVisitor mv, ClassVisitorHelper helper,
            String propertyName) {
        super(mv);
        this.helper = helper;
        this.propertyName = propertyName;
    }

    @Override
    public void visitCode() {
        super.visitCode();

        String field = helper.getPropertyField("objectContext");
        Type objectContextType = Type.getType(ObjectContext.class);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(
                Opcodes.GETFIELD,
                helper.getCurrentClass().getInternalName(),
                field,
                objectContextType.getDescriptor());
        Label l1 = new Label();
        mv.visitJumpInsn(Opcodes.IFNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(42, l2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(
                Opcodes.GETFIELD,
                helper.getCurrentClass().getInternalName(),
                field,
                objectContextType.getDescriptor());
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(propertyName);
        mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                objectContextType.getInternalName(),
                "prepareForAccess",
                "(Lorg/apache/cayenne/Persistent;Ljava/lang/String;)V");
        mv.visitLabel(l1);
    }
}
