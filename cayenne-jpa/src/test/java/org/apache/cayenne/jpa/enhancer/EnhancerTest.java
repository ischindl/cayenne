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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.cayenne.jpa.map.AccessType;
import org.apache.cayenne.jpa.map.JpaClassDescriptor;
import org.apache.cayenne.jpa.map.JpaEntityMap;
import org.apache.cayenne.jpa.spi.JpaUnitClassLoader;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.property.PropertyUtils;

public class EnhancerTest extends TestCase {

    static final String E1 = "org.apache.cayenne.jpa.entity.cayenne.MockCayenneEntity1";
    static final String E2 = "org.apache.cayenne.jpa.entity.cayenne.MockCayenneEntity2";
    static final String ET1 = "org.apache.cayenne.jpa.entity.cayenne.MockCayenneTargetEntity1";
    static final String ET2 = "org.apache.cayenne.jpa.entity.cayenne.MockCayenneTargetEntity2";

    protected EnhancingClassLoader loader;

    @Override
    protected void setUp() throws Exception {
        Collection<String> managedClasses = new ArrayList<String>();
        managedClasses.add(E1);
        managedClasses.add(E2);
        managedClasses.add(ET1);
        managedClasses.add(ET2);
        
        JpaEntityMap entityMap = new JpaEntityMap();
        entityMap.setAccess(AccessType.FIELD);

        ClassLoader helper = new JpaUnitClassLoader(Thread
                .currentThread()
                .getContextClassLoader());

        Map<String, JpaClassDescriptor> map = new HashMap<String, JpaClassDescriptor>();
        map.put(E1, new JpaClassDescriptor(entityMap, Class.forName(E1, true, helper)));
        map.put(E2, new JpaClassDescriptor(entityMap, Class.forName(E2, true, helper)));
        map.put(ET1, new JpaClassDescriptor(entityMap, Class.forName(ET1, true, helper)));
        map.put(ET2, new JpaClassDescriptor(entityMap, Class.forName(ET2, true, helper)));

        loader = new EnhancingClassLoader(new CglibEnhancer(map), managedClasses);
    }

    public void testClassLoading() throws Exception {

        Class e1Class = Class.forName(E1, true, loader);
        assertNotNull(e1Class);
        assertEquals(E1, e1Class.getName());

        assertTrue(DataObject.class.isAssignableFrom(e1Class));

        Method persistenceStateGetter = e1Class.getMethod("getPersistenceState");
        assertNotNull(persistenceStateGetter);
        assertFalse(Modifier.isAbstract(persistenceStateGetter.getModifiers()));
        assertNotNull(e1Class.getMethod("setPersistenceState", Integer.TYPE));
    }

    public void testDataObject() throws Exception {

        Class e1Class = Class.forName(E1, true, loader);
        Object object = e1Class.newInstance();

        assertTrue(object instanceof DataObject);
        DataObject p = (DataObject) object;

        int state = PersistenceState.DELETED;
        p.setPersistenceState(state);
        assertEquals(PersistenceState.DELETED, p.getPersistenceState());

        ObjectId id = new ObjectId("X", "R", 55);
        p.setObjectId(id);
        assertSame(id, p.getObjectId());
    }

    public void testPropertyInterception() throws Exception {

        Class e1Class = Class.forName(E1, true, loader);
        Object object = e1Class.newInstance();
        // DataObject dataObject = (DataObject) object;

        assertEquals(new Integer(0), PropertyUtils.getProperty(object, "attribute2"));
        PropertyUtils.setProperty(object, "attribute2", new Integer(4));
        assertEquals(new Integer(4), PropertyUtils.getProperty(object, "attribute2"));

        // assertEquals(new Integer(4), dataObject.readProperty("attribute2"));
    }
}
