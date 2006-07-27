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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.Signature;
import net.sf.cglib.transform.ClassEmitterTransformer;

import org.apache.cayenne.CayenneRuntimeException;

/**
 * A class transformer that delegates execution of the interface methods to the
 * corresponding static delegate methods. Delegate method must be static and have the same
 * name and return type as the interface method. It should have one extra parameter - the
 * interface instance. E.g. MyInterface "String doSomething(int)" method will be mapped to
 * a "static String doSomething(MyInterface, int)" delegate method.
 * 
 * @author Andrus Adamchik
 */
public class InterfaceMethodInjector extends ClassEmitterTransformer {

    protected Class delegatedInterface;
    protected Type staticDelegate;
    protected List<Signature> interfaceMethods;
    protected List<Signature> delegateMethods;

  

    public InterfaceMethodInjector(Class delegatedInterface, Class staticDelegate,
            Collection<String> excludedMethods) {
        this.staticDelegate = Type.getType(staticDelegate);
        this.delegatedInterface = delegatedInterface;

        Method[] methods = delegatedInterface.getDeclaredMethods();
        interfaceMethods = new ArrayList<Signature>(methods.length);
        delegateMethods = new ArrayList<Signature>(methods.length);
        for (Method m : methods) {

            if (!excludedMethods.contains(m.getName())) {
                interfaceMethods.add(CglibEnhancer.compileSignature(m));
                delegateMethods.add(mapDelegateMethod(staticDelegate, m));
            }
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

        interfaces = addInterface(interfaces, delegatedInterface);
        super.begin_class(version, access, className, superType, interfaces, sourceFile);
    }

    @Override
    public void end_class() {
        addDelegateMethods();
        super.end_class();
    }

    protected Signature mapDelegateMethod(Class staticDelegate, Method method) {
        Class[] params = method.getParameterTypes();
        Class[] delegateParams;

        if (params.length > 0) {
            delegateParams = new Class[params.length + 1];
            delegateParams[0] = delegatedInterface;
            System.arraycopy(params, 0, delegateParams, 1, params.length);
        }
        else {
            delegateParams = new Class[] {
                delegatedInterface
            };
        }

        Method delegateMethod;
        try {
            delegateMethod = staticDelegate.getMethod(method.getName(), delegateParams);
        }
        catch (NoSuchMethodException e) {
            throw new CayenneRuntimeException("Can't match interface method '"
                    + method.getName()
                    + "'");
        }

        if (!Modifier.isStatic(delegateMethod.getModifiers())) {
            throw new CayenneRuntimeException("Delegate method must be static '"
                    + method.getName()
                    + "'");
        }

        if (!method.getReturnType().isAssignableFrom(delegateMethod.getReturnType())) {
            throw new CayenneRuntimeException(
                    "Inompatible return type of the delegate method '"
                            + method.getName()
                            + "'");
        }

        return CglibEnhancer.compileSignature(delegateMethod);
    }

    /**
     * Adds a delegated interface unless it is already declared for the class.
     */
    protected Type[] addInterface(Type[] interfaces, Class iface) {
        String name = iface.getName();

        for (Type type : interfaces) {
            if (name.equals(type.getClassName())) {
                return interfaces;
            }
        }

        Type[] newInterfaces = new Type[interfaces.length + 1];
        System.arraycopy(interfaces, 0, newInterfaces, 1, interfaces.length);
        newInterfaces[0] = Type.getType(iface);
        return newInterfaces;
    }

    protected void addDelegateMethods() {

        // TODO: andrus, 5/1/2006 - check if the interface is partially implemented...
        for (int i = 0; i < interfaceMethods.size(); i++) {

            CodeEmitter e = begin_method(
                    Constants.ACC_PUBLIC,
                    interfaceMethods.get(i),
                    null,
                    null);

            e.load_this();
            e.load_args();
            e.invoke_static(staticDelegate, delegateMethods.get(i));
            e.return_value();
            e.end_method();
        }
    }
}
