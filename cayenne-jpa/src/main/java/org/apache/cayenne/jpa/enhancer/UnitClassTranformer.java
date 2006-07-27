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
import java.security.ProtectionDomain;
import java.util.Map;

import javax.persistence.spi.ClassTransformer;

import org.apache.cayenne.jpa.map.JpaClassDescriptor;

/**
 * A ClassTransformer decorator that passes through classes mentioned in the JpaEntityMap
 * to the wrapped transformer, letting all other classes to go untransformed.
 * 
 * @author Andrus Adamchik
 */
public class UnitClassTranformer implements ClassTransformer {

    protected ClassTransformer transformer;
    protected Map<String, JpaClassDescriptor> managedClasses;

    public UnitClassTranformer(Map<String, JpaClassDescriptor> managedClasses,
            ClassTransformer transformer) {
        this.transformer = transformer;
        this.managedClasses = managedClasses;
    }

    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {

        return isManagedClass(className) ? transformer.transform(
                loader,
                className,
                classBeingRedefined,
                protectionDomain,
                classfileBuffer) : null;
    }

    /**
     * Returns true if a classname os a part of an entity map. Note that the class name is
     * expected in the internal format, separated by "/", not ".".
     */
    protected boolean isManagedClass(String className) {
        return managedClasses.containsKey(className.replace('/', '.'));
    }
}
