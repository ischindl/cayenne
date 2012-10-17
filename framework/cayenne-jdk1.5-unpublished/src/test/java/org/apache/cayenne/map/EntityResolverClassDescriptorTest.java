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

package org.apache.cayenne.map;

import static org.mockito.Mockito.mock;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.reflect.ArcProperty;
import org.apache.cayenne.reflect.ClassDescriptor;
import org.apache.cayenne.reflect.LazyClassDescriptorDecorator;
import org.apache.cayenne.reflect.MockClassDescriptorFactory;
import org.apache.cayenne.reflect.PropertyDescriptor;
import org.apache.cayenne.testdo.mt.MtTable1;
import org.apache.cayenne.testdo.mt.MtTable2;
import org.apache.cayenne.unit.di.client.ClientCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;

@UseServerRuntime(ClientCase.MULTI_TIER_PROJECT)
public class EntityResolverClassDescriptorTest extends ClientCase {

    @Inject
    private ServerRuntime runtime;

    public void testServerDescriptorCaching() {
        EntityResolver resolver = runtime.getDataDomain().getEntityResolver();
        resolver.getClassDescriptorMap().clearDescriptors();

        ClassDescriptor descriptor = resolver.getClassDescriptor("MtTable1");
        assertNotNull(descriptor);
        assertSame(descriptor, resolver.getClassDescriptor("MtTable1"));
        resolver.getClassDescriptorMap().clearDescriptors();

        ClassDescriptor descriptor1 = resolver.getClassDescriptor("MtTable1");
        assertNotNull(descriptor1);
        assertNotSame(descriptor, descriptor1);
    }

    public void testServerDescriptorFactory() {
        EntityResolver resolver = runtime.getDataDomain().getEntityResolver();
        resolver.getClassDescriptorMap().clearDescriptors();

        ClassDescriptor descriptor = mock(ClassDescriptor.class);
        MockClassDescriptorFactory factory = new MockClassDescriptorFactory(
                descriptor);
        resolver.getClassDescriptorMap().addFactory(factory);
        try {
            ClassDescriptor resolved = resolver.getClassDescriptor("MtTable1");
            assertNotNull(resolved);
            resolved = ((LazyClassDescriptorDecorator) resolved).getDescriptor();
            assertSame(descriptor, resolved);
        }
        finally {
            resolver.getClassDescriptorMap().removeFactory(factory);
        }
    }

    public void testArcProperties() {
        EntityResolver resolver = runtime.getDataDomain().getEntityResolver();
        resolver.getClassDescriptorMap().clearDescriptors();

        ClassDescriptor descriptor = resolver.getClassDescriptor("MtTable1");
        assertNotNull(descriptor);

        PropertyDescriptor p = descriptor.getProperty(MtTable1.TABLE2ARRAY_PROPERTY);
        assertTrue(p instanceof ArcProperty);

        ClassDescriptor target = ((ArcProperty) p).getTargetDescriptor();
        assertNotNull(target);
        assertSame(resolver.getClassDescriptor("MtTable2"), target);
        assertNotNull(((ArcProperty) p).getComplimentaryReverseArc());
        assertEquals(MtTable2.TABLE1_PROPERTY, ((ArcProperty) p)
                .getComplimentaryReverseArc()
                .getName());
    }
}
