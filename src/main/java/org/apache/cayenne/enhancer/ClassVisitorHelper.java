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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A helper for the ASM ClassVisitor that encapsulates common class enhancement
 * operations.
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
class ClassVisitorHelper {

    private String fieldPrefix = "$cay_";
    private ClassVisitor classVisitor;
    private Type currentClass;

    ClassVisitorHelper(ClassVisitor classVisitor) {
        this.classVisitor = classVisitor;
    }

    Type getCurrentClass() {
        return currentClass;
    }

    String getPropertyField(String propertyName) {
        return fieldPrefix + propertyName;
    }

    void reset(String className) {
        // assuming no primitives or arrays
        this.currentClass = Type.getType("L" + className + ";");
    }

    String[] addInterface(String[] interfaces, Class newInterface) {

        String name = Type.getInternalName(newInterface);
        if (interfaces == null || interfaces.length == 0) {
            return new String[] {
                name
            };
        }

        String[] expandedInterfaces = new String[interfaces.length + 1];
        expandedInterfaces[0] = name;
        System.arraycopy(interfaces, 0, expandedInterfaces, 1, interfaces.length);

        return expandedInterfaces;
    }

    void createProperty(Class type, String name) {
        createProperty(type, name, false);
    }

    void createProperty(Class type, String name, boolean isTransient) {
        Type asmType = Type.getType(type);

        int access = Opcodes.ACC_PROTECTED;
        if (isTransient) {
            access += Opcodes.ACC_TRANSIENT;
        }

        createField(name, asmType, access);
        createGetter(name, asmType);
        createSetter(name, asmType);
    }

    private void createSetter(String propertyName, Type asmType) {

        String methodName = "set" + Character.toUpperCase(propertyName.charAt(0));
        if (propertyName.length() > 1) {
            methodName += propertyName.substring(1);
        }

        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, methodName, "("
                + asmType.getDescriptor()
                + ")V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        // TODO: andrus, 10/9/2006 other opcodes
        if ("I".equals(asmType.getDescriptor())) {
            mv.visitVarInsn(Opcodes.ILOAD, 1);
        }
        else {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
        }

        mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                currentClass.getInternalName(),
                getPropertyField(propertyName),
                asmType.getDescriptor());
        mv.visitInsn(Opcodes.RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", currentClass.getDescriptor(), null, l0, l1, 0);
        mv.visitLocalVariable(propertyName, asmType.getDescriptor(), null, l0, l1, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void createGetter(String propertyName, Type asmType) {

        String prefix = "boolean".equals(asmType.getClassName()) ? "is" : "get";
        String methodName = prefix + Character.toUpperCase(propertyName.charAt(0));
        if (propertyName.length() > 1) {
            methodName += propertyName.substring(1);
        }

        MethodVisitor mv = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, methodName, "()"
                + asmType.getDescriptor(), null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(
                Opcodes.GETFIELD,
                currentClass.getInternalName(),
                getPropertyField(propertyName),
                asmType.getDescriptor());

        // TODO: andrus, 10/9/2006 other return opcodes
        if ("I".equals(asmType.getDescriptor())) {
            mv.visitInsn(Opcodes.IRETURN);
        }
        else {
            mv.visitInsn(Opcodes.ARETURN);
        }

        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", currentClass.getDescriptor(), null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void createField(String propertyName, Type asmType, int access) {
        FieldVisitor fv = classVisitor.visitField(
                access,
                getPropertyField(propertyName),
                asmType.getDescriptor(),
                null,
                null);
        fv.visitEnd();
    }
}
