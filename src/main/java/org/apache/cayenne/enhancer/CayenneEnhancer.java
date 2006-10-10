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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Map;

import org.apache.cayenne.Persistent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * A ClassFileTransformer that enhances a POJO into a persistent object that can be used
 * with Cayenne. More specifically, it ensures that the object implements
 * {@link Persistent} interface and invokes callbacks from the accessor methods.
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
public class CayenneEnhancer implements ClassFileTransformer {

    protected Map<String, Collection<String>> persistentPropertiesByClass;

    public CayenneEnhancer(Map<String, Collection<String>> persistentPropertiesByClass) {
        this.persistentPropertiesByClass = persistentPropertiesByClass;
    }

    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, true);

        ClassVisitor visitor = createVisitor(className, writer);
        if (visitor == null) {
            // per JPA spec if no transformation occured, we must return null
            return null;
        }

        reader.accept(visitor, true);
        return writer.toByteArray();
    }

    /**
     * Builds a chain of ASM visitors.
     */
    protected ClassVisitor createVisitor(String className, ClassWriter writer) {
        Collection<String> properties = persistentPropertiesByClass.get(className);
        if (properties == null || properties.isEmpty()) {
            return null;
        }

        return new PersistentClassVisitor(writer, properties);
    }
}
