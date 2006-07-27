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


package org.apache.cayenne.jpa.bridge;

import junit.framework.TestCase;

import org.apache.cayenne.jpa.conf.EntityMapAnnotationLoader;
import org.apache.cayenne.jpa.conf.EntityMapDefaultsProcessor;
import org.apache.cayenne.jpa.conf.EntityMapLoaderContext;
import org.apache.cayenne.jpa.entity.cayenne.MockCayenneEntity1;
import org.apache.cayenne.jpa.entity.cayenne.MockCayenneEntity2;
import org.apache.cayenne.jpa.entity.cayenne.MockCayenneEntityMap1;
import org.apache.cayenne.jpa.entity.cayenne.MockCayenneTargetEntity1;
import org.apache.cayenne.jpa.entity.cayenne.MockCayenneTargetEntity2;
import org.apache.cayenne.jpa.map.JpaEntityMap;
import org.apache.cayenne.jpa.spi.MockPersistenceUnitInfo;
import org.apache.cayenne.map.DataMap;

public class DataMapConverterTest extends TestCase {

    public void testDataMapDefaults() {
        EntityMapLoaderContext context = new EntityMapLoaderContext(
                new MockPersistenceUnitInfo());
        JpaEntityMap jpaMap = context.getEntityMap();
        jpaMap.setPackageName("p1");
        jpaMap.setSchema("s1");

        // TODO: unsupported by DataMap
        // jpaMap.setCatalog("c1");

        DataMap cayenneMap = new DataMapConverter().toDataMap("n1", context);
        assertEquals("n1", cayenneMap.getName());
        assertEquals("p1", cayenneMap.getDefaultPackage());
        assertEquals("s1", cayenneMap.getDefaultSchema());
    }

    /**
     * @see org.apache.cayenne.jpa.conf.EntityMapAnnotationLoaderTest#testLoadClassMapping()
     */
    public void testLoadClassMapping() throws Exception {
        EntityMapLoaderContext context = new EntityMapLoaderContext(
                new MockPersistenceUnitInfo());
        EntityMapAnnotationLoader loader = new EntityMapAnnotationLoader(context);

        loader.loadClassMapping(MockCayenneEntity1.class);
        loader.loadClassMapping(MockCayenneEntity2.class);
        loader.loadClassMapping(MockCayenneTargetEntity1.class);
        loader.loadClassMapping(MockCayenneTargetEntity2.class);

        loader.loadClassMapping(MockCayenneEntityMap1.class);

        // apply defaults before conversion
        new EntityMapDefaultsProcessor().applyDefaults(context);

        assertFalse("Found conflicts: " + context.getConflicts(), context
                .getConflicts()
                .hasFailures());

        DataMap dataMap = new DataMapConverter().toDataMap("n1", context);
        assertFalse("Found DataMap conflicts: " + context.getConflicts(), context
                .getConflicts()
                .hasFailures());

        new DataMapMappingAssertion().testDataMap(dataMap);
    }
}
