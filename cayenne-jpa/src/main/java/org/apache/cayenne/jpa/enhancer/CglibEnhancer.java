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

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.cglib.asm.Attribute;
import net.sf.cglib.asm.ClassReader;
import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.Type;
import net.sf.cglib.asm.attrs.Attributes;
import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.core.Signature;
import net.sf.cglib.transform.ClassReaderGenerator;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.transform.ClassTransformerChain;
import net.sf.cglib.transform.TransformingClassGenerator;

import org.apache.cayenne.jpa.map.JpaClassDescriptor;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;

/**
 * A JPA class transformer based on Cglib library.
 * 
 * @author Andrus Adamchik
 */
public class CglibEnhancer implements javax.persistence.spi.ClassTransformer {

    protected Map<String, JpaClassDescriptor> managedClasses;

    static Signature compileSignature(Method method) {

        Class[] params = method.getParameterTypes();
        Type[] types;
        if (params.length == 0) {
            types = Constants.TYPES_EMPTY;
        }
        else {
            types = new Type[params.length];
            for (int i = 0; i < params.length; i++) {
                types[i] = Type.getType(params[i]);
            }
        }

        return new Signature(method.getName(), Type.getReturnType(method), types);
    }

    public CglibEnhancer(Map<String, JpaClassDescriptor> managedClasses) {
        this.managedClasses = managedClasses;
    }

    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new DebuggingClassWriter(true);
        try {
            getGenerator(reader).generateClass(writer);
        }
        catch (Exception e) {
            throw new CayenneRuntimeException("Error transforming class '"
                    + className
                    + "'", e);
        }

        return writer.toByteArray();
    }

    protected ClassGenerator getGenerator(ClassReader reader) {
        ClassGenerator generator = new ClassReaderGenerator(
                reader,
                attributes(),
                skipDebug());
        return new TransformingClassGenerator(generator, createTransformer());
    }

    protected boolean skipDebug() {
        return false;
    }

    protected Attribute[] attributes() {
        return Attributes.getDefaultAttributes();
    }

    /**
     * Creates a chain of transformers to make DataObjects out of POJOs.
     */
    public ClassTransformer createTransformer() {

        ClassTransformer t1 = new DataObjectPropertyInjector();

        Collection<String> excludes = new ArrayList<String>();
        excludes.add("setSnapshotVersion");
        excludes.add("getSnapshotVersion");
        ClassTransformer t2 = new InterfaceMethodInjector(
                DataObject.class,
                DataObjectDelegate.class,
                excludes);
        ClassTransformer t3 = new DataObjectAccessorInjector(managedClasses);

        return new ClassTransformerChain(new ClassTransformer[] {
                t1, t2, t3
        });
    }
}
