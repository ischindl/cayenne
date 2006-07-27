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

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.transform.ClassEmitterTransformer;

/**
 * Loads common persistent fields.
 * 
 * @author Andrus Adamchik
 */
public class PropertyInjector extends ClassEmitterTransformer {

    protected String[] names;
    protected Type[] types;

    public PropertyInjector(String[] names, Class[] types) {
        this.names = names;
        this.types = new Type[types.length];
        for (int i = 0; i < types.length; i++) {
            this.types[i] = Type.getType(types[i]);
        }
    }

    @Override
    public void begin_class(
            int version,
            int access,
            String className,
            Type superType,
            Type[] interfaces,
            String sourceFile) {

        super.begin_class(version, access, className, superType, interfaces, sourceFile);
        EmitUtils.add_properties(this, names, types);
    }
}
