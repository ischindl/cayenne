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


package org.apache.cayenne.jpa.instrument;

import java.lang.instrument.Instrumentation;

import org.apache.cayenne.jpa.spi.JpaPersistenceProvider;

/**
 * An instrumentation agent that configures a
 * {@link org.apache.cayenne.jpa.spi.JpaUnitFactory} that will load JPA class enhancers in
 * the main Instrumentation instance.
 * <p>
 * To enable CayenneAgent (and hence class enhancers in the Java SE environment), start
 * the JVM with the "-javaagent:" option. E.g.:
 * 
 * <pre>
 *         java -javaagent:/path/to/cayenne-jpa-3.0.jar org.example.Main
 * </pre>
 * 
 * @author Andrus Adamchik
 */
public class CayenneAgent {

    // do not use the actual Java class to prevent dependencies from loading too early..;
    // using a unit test to ensure that the factory name is valid.
    static final String FACTORY_CLASS = "org.apache.cayenne.jpa.instrument.InstrumentingUnitFactory";

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("*** CayenneAgent starting...");
        InstrumentingUnit.setInstrumentation(instrumentation);

        // TODO: andrus, 5/1/2006 - add explicit debugging option to the agent
        
        // This can be used to debug enhancer:
        // instrumentation.addTransformer(new ClassFileTransformer() {
        //
        // public byte[] transform(
        // ClassLoader loader,
        // String className,
        // Class<?> classBeingRedefined,
        // ProtectionDomain protectionDomain,
        // byte[] classfileBuffer) throws IllegalClassFormatException {
        //
        // if (className.indexOf("jpa") > 0) {
        // System.out.println("*** className..."
        // + className
        // + ", loader: "
        // + loader);
        // }
        // return null;
        // }
        // });

        System.setProperty(JpaPersistenceProvider.UNIT_FACTORY_PROPERTY, FACTORY_CLASS);
    }
}
