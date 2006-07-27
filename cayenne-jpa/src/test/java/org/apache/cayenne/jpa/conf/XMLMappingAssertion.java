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

package org.apache.cayenne.jpa.conf;

import org.apache.cayenne.jpa.map.AccessType;
import org.apache.cayenne.jpa.map.JpaEmbeddable;
import org.apache.cayenne.jpa.map.JpaEntity;
import org.apache.cayenne.jpa.map.JpaEntityMap;

class XMLMappingAssertion extends MappingAssertion {

    @Override
    public void testEntityMap(JpaEntityMap entityMap) throws Exception {

        assertNotNull(entityMap);
        assertEquals("Test Description", entityMap.getDescription());
        assertEquals("default_package", entityMap.getPackageName());
        assertEquals("default_catalog", entityMap.getCatalog());
        assertEquals(AccessType.FIELD, entityMap.getAccess());

        super.testEntityMap(entityMap);
    }

    @Override
    protected void assertEntity1(JpaEntity entity1) {
        super.assertEntity1(entity1);
        assertSame(AccessType.PROPERTY, entity1.getAccess());
    }

    @Override
    protected void assertEmbeddable1(JpaEmbeddable embeddable1) {
        super.assertEmbeddable1(embeddable1);
        assertSame(AccessType.FIELD, embeddable1.getAccess());
    }
}
