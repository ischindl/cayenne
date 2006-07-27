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
package org.apache.cayenne.query;

import junit.framework.TestCase;

import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.remote.hessian.service.HessianUtil;

public class InvalidateListCacheQueryTst extends TestCase {


    public void testSerializabilityWithHessian() throws Exception {
        InvalidateListCacheQuery o = new InvalidateListCacheQuery("XXX", new String[] {"a", "b"}, true);
        Object clone = HessianUtil.cloneViaClientServerSerialization(
                o,
                new EntityResolver());

        assertTrue(clone instanceof InvalidateListCacheQuery);
        InvalidateListCacheQuery c1 = (InvalidateListCacheQuery) clone;

        assertNotSame(o, c1);
        assertEquals("XXX", c1.getQueryNameKey());
        assertEquals(2, c1.getGroupKeys().length);
        assertEquals("a", c1.getGroupKeys()[0]);
        assertEquals("b", c1.getGroupKeys()[1]);
        assertTrue(c1.isCascade());
    }
}
