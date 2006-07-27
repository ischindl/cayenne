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

import java.util.Map;

import net.sf.cglib.asm.Attribute;
import net.sf.cglib.asm.Type;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.Signature;
import net.sf.cglib.transform.ClassEmitterTransformer;

import org.apache.cayenne.jpa.JpaProviderException;
import org.apache.cayenne.jpa.map.JpaClassDescriptor;
import org.apache.cayenne.jpa.map.JpaPropertyDescriptor;
import org.apache.cayenne.Persistent;

/**
 * Injects persistence code to the property accessors.
 * 
 * @author Andrus Adamchik
 */
public class DataObjectAccessorInjector extends ClassEmitterTransformer {

    protected Map<String, JpaClassDescriptor> managedClasses;
    protected Type staticDelegate;
    protected Signature getterBeforeSignature;
    protected Signature setterBeforeSignature;
    protected Signature setterAfterSignature;

    public DataObjectAccessorInjector(Map<String, JpaClassDescriptor> managedClasses) {
        this.managedClasses = managedClasses;

        Class delegateClass = DataObjectDelegate.class;
        this.staticDelegate = Type.getType(delegateClass);

        try {
            this.getterBeforeSignature = CglibEnhancer.compileSignature(delegateClass
                    .getDeclaredMethod(
                            "beforeGetProperty",
                            Persistent.class,
                            String.class));
            this.setterBeforeSignature = CglibEnhancer.compileSignature(delegateClass
                    .getDeclaredMethod(
                            "beforeSetProperty",
                            Persistent.class,
                            String.class));
            this.setterAfterSignature = CglibEnhancer.compileSignature(delegateClass
                    .getDeclaredMethod(
                            "afterSetProperty",
                            Persistent.class,
                            String.class,
                            Object.class,
                            Object.class));
        }
        catch (Exception e) {
            throw new JpaProviderException("Error reflecting delegate methods", e);
        }
    }

    @Override
    public CodeEmitter begin_method(
            int access,
            Signature sig,
            Type[] exceptions,
            Attribute attrs) {

        if (sig.equals(Constants.SIG_STATIC)) {
            return super.begin_method(access, sig, exceptions, attrs);
        }

        String propertyName = JpaClassDescriptor.propertyNameForGetter(sig.getName());
        if (propertyName != null) {
            JpaPropertyDescriptor property = getProperty(propertyName);

            if (property != null) {
                return enhanceGetter(property, access, sig, exceptions, attrs);
            }
        }
        else {
            propertyName = JpaClassDescriptor.propertyNameForSetter(sig.getName());
            if (propertyName != null) {
                JpaPropertyDescriptor property = getProperty(propertyName);
                if (property != null) {
                    return enhanceSetter(property, access, sig, exceptions, attrs);
                }
            }
        }

        return super.begin_method(access, sig, exceptions, attrs);
    }

    protected JpaPropertyDescriptor getProperty(String propertyName) {
        String className = getClassType().getClassName();
        JpaClassDescriptor descriptor = managedClasses.get(className);

        if (descriptor == null) {
            throw new JpaProviderException("No descriptor for class: " + className);
        }

        return descriptor.getProperty(propertyName);
    }

    protected CodeEmitter enhanceGetter(
            JpaPropertyDescriptor property,
            int access,
            Signature sig,
            Type[] exceptions,
            Attribute attrs) {

        // TODO: andrus 5/4/2006 - how can we do that?
        return super.begin_method(access, sig, exceptions, attrs);
    }

    protected CodeEmitter enhanceSetter(
            JpaPropertyDescriptor property,
            int access,
            Signature sig,
            Type[] exceptions,
            Attribute attrs) {

        // TODO: andrus 5/4/2006 - how can we do that?
        return super.begin_method(access, sig, exceptions, attrs);
    }
}
