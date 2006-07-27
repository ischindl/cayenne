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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javax.persistence.spi.ClassTransformer;

import org.apache.cayenne.jpa.spi.JpaProviderContext;
import org.apache.cayenne.jpa.spi.JpaUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A unit that loads all transformers into a shared
 * {@link org.apache.cayenne.jpa.instrument.InstrumentationContext} instance.
 * 
 * @author Andrus Adamchik
 */
public class InstrumentingUnit extends JpaUnit {

    static final String INSTRUMENTATION_KEY = "cayenne.jpa.instrumentation";

    public static Instrumentation getInstrumentation() {
        return (Instrumentation) JpaProviderContext.getObject(INSTRUMENTATION_KEY);
    }

    public static void setInstrumentation(Instrumentation instance) {
        JpaProviderContext.setObject(INSTRUMENTATION_KEY, instance);
    }

    protected Log logger;

    @Override
    public void addTransformer(final ClassTransformer transformer) {

        // sanity check
        if (getInstrumentation() == null) {
            getLogger().warn(
                    "*** No instrumentation instance present. "
                            + "Check the -javaagent: option");
            return;
        }

        // wrap in a ClassFileTransformer
        ClassFileTransformer transformerWrapper = new ClassFileTransformer() {

            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) throws IllegalClassFormatException {

                return transformer.transform(
                        loader,
                        className,
                        classBeingRedefined,
                        protectionDomain,
                        classfileBuffer);
            }
        };

        getInstrumentation().addTransformer(transformerWrapper);
    }

    protected Log getLogger() {
        if (logger == null) {
            logger = LogFactory.getLog(getClass());
        }

        return logger;
    }
}
