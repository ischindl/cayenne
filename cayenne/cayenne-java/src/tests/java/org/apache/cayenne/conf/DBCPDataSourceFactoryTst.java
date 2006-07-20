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

package org.apache.cayenne.conf;

import java.sql.Connection;
import java.util.Properties;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.cayenne.ConfigurationException;

import junit.framework.TestCase;

/**
 * @author Andrei Adamchik
 */
public class DBCPDataSourceFactoryTst extends TestCase {

    public void testStringProperty() {
        DBCPDataSourceFactory factory = new DBCPDataSourceFactory();
        Properties props = new Properties();
        props.put("a", "X");
        props.put("cayenne.dbcp.c", "Y");

        assertNull(factory.stringProperty(props, "a"));
        assertNull(factory.stringProperty(props, "b"));
        assertEquals("Y", factory.stringProperty(props, "c"));
    }

    public void testIntProperty() {
        DBCPDataSourceFactory factory = new DBCPDataSourceFactory();
        Properties props = new Properties();
        props.put("a", "10");
        props.put("cayenne.dbcp.b", "11");
        props.put("cayenne.dbcp.d", "**");

        assertEquals(11, factory.intProperty(props, "b", -1));
        assertEquals(-1, factory.intProperty(props, "a", -1));
        assertEquals(-1, factory.intProperty(props, "c", -1));
        assertEquals(-2, factory.intProperty(props, "d", -2));
    }

    public void testWhenExhaustedAction() throws Exception {
        DBCPDataSourceFactory factory = new DBCPDataSourceFactory();
        Properties props = new Properties();
        props.put("cayenne.dbcp.a", "1");
        props.put("cayenne.dbcp.b", "WHEN_EXHAUSTED_BLOCK");
        props.put("cayenne.dbcp.c", "WHEN_EXHAUSTED_GROW");
        props.put("cayenne.dbcp.d", "WHEN_EXHAUSTED_FAIL");
        props.put("cayenne.dbcp.e", "garbage");

        assertEquals(1, factory.whenExhaustedAction(props, "a", (byte) 100));
        assertEquals(GenericObjectPool.WHEN_EXHAUSTED_BLOCK, factory.whenExhaustedAction(
                props,
                "b",
                (byte) 100));
        assertEquals(GenericObjectPool.WHEN_EXHAUSTED_GROW, factory.whenExhaustedAction(
                props,
                "c",
                (byte) 100));
        assertEquals(GenericObjectPool.WHEN_EXHAUSTED_FAIL, factory.whenExhaustedAction(
                props,
                "d",
                (byte) 100));

        try {
            factory.whenExhaustedAction(props, "e", (byte) 100);
            fail("must throw on invalid key");
        }
        catch (ConfigurationException ex) {
            // expected
        }

        assertEquals(100, factory.whenExhaustedAction(props, "f", (byte) 100));
    }

    public void testTransactionIsolation() throws Exception {
        DBCPDataSourceFactory factory = new DBCPDataSourceFactory();
        Properties props = new Properties();
        props.put("cayenne.dbcp.a", "1");
        props.put("cayenne.dbcp.b", "TRANSACTION_NONE");
        props.put("cayenne.dbcp.c", "TRANSACTION_READ_UNCOMMITTED");
        props.put("cayenne.dbcp.d", "TRANSACTION_SERIALIZABLE");
        props.put("cayenne.dbcp.e", "garbage");

        assertEquals(1, factory.defaultTransactionIsolation(props, "a", (byte) 100));
        assertEquals(Connection.TRANSACTION_NONE, factory.defaultTransactionIsolation(
                props,
                "b",
                (byte) 100));
        assertEquals(Connection.TRANSACTION_READ_UNCOMMITTED, factory
                .defaultTransactionIsolation(props, "c", (byte) 100));
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, factory
                .defaultTransactionIsolation(props, "d", (byte) 100));

        try {
            factory.defaultTransactionIsolation(props, "e", (byte) 100);
            fail("must throw on invalid key");
        }
        catch (ConfigurationException ex) {
            // expected
        }

        assertEquals(100, factory.defaultTransactionIsolation(props, "f", (byte) 100));
    }
}
