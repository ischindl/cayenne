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

package org.apache.cayenne.xml;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.cayenne.unit.CayenneTestResources;

/**
 * @author Kevin J. Menard, Jr.
 */
public class XMLEncoderTst extends TestCase {

    static final String XML_DATA_DIR = "xmlcoding/";

    public void testObjectWithNullProperties() throws Exception {
        XMLEncoder encoder = new XMLEncoder();

        TestObject test = new TestObject();
        test.setName(null);
        test.encodeAsXML(encoder);
    }

    public void testEncodeSimpleCollection() throws Exception {
        XMLEncoder encoder = new XMLEncoder();

        TestObject test = new TestObject();
        test.addChild(new TestObject("Bill", 98, true));
        test.addChild(new TestObject("Sue", 45, false));

        encoder.setRoot("Test", test.getClass().getName());
        encoder.encodeProperty("children", test.getChildren());
        String result = encoder.nodeToString(encoder.getRootNode(false));

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "encoded-simple-collection.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }

        assertEquals(comp.toString(), result);
    }

    public void testEncodeComplexCollection() throws Exception {
        XMLEncoder encoder = new XMLEncoder();
        TestObject obj1 = new TestObject();
        obj1.setName("George");
        obj1.addChild(new TestObject("Bill", 62, true));
        obj1.addChild(new TestObject("Sue", 8, true));

        TestObject obj2 = new TestObject("Joe", 31, false);
        obj2.addChild(new TestObject("Harry", 23, false));

        obj1.addChild(obj2);

        String result = encoder.encode("TestObjects", obj1);

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "encoded-complex-collection.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }

        // there are differences in attribute order encoding, so there can be more than
        // one valid output depending on the parser used...

        if (!comp.toString().equals(result)) {
            in = new BufferedReader(new InputStreamReader(CayenneTestResources
                    .getResource(XML_DATA_DIR + "encoded-complex-collection-alt1.xml")));
            comp = new StringBuffer();
            while (in.ready()) {
                comp.append(in.readLine()).append("\n");
            }
        }
        assertEquals(comp.toString(), result);
    }

    public void testSimpleMapping() throws Exception {
        XMLEncoder encoder = new XMLEncoder(CayenneTestResources.getResourceURL(
                XML_DATA_DIR + "simple-mapping.xml").toExternalForm());
        TestObject test = new TestObject();
        test.setAge(57);
        test.setName("George");
        test.setOpen(false);

        String result = encoder.encode(test);

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "simple-mapped.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }
        assertEquals(comp.toString(), result);
    }

    public void testCollectionMapping() throws Exception {
        XMLEncoder encoder = new XMLEncoder(CayenneTestResources.getResourceURL(
                XML_DATA_DIR + "collection-mapping.xml").toExternalForm());
        TestObject george = new TestObject();
        george.setName("George");
        george.addChild(new TestObject("Bill", 34, true));

        TestObject sue = new TestObject("Sue", 31, false);
        sue.addChild(new TestObject("Mike", 3, true));
        george.addChild(sue);

        String result = encoder.encode(george);

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "collection-mapped.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }

        assertEquals(comp.toString(), result);
    }

    public void testEncodeDataObjectsList() throws Exception {
        List dataObjects = new ArrayList();

        dataObjects.add(new TestObject("George", 5, true));
        dataObjects.add(new TestObject("Mary", 28, false));
        dataObjects.add(new TestObject("Joe", 31, true));

        String xml = new XMLEncoder().encode("EncodedTestList", dataObjects);

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "data-objects-encoded.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }

        assertEquals(comp.toString(), xml);
    }

    public void testDataObjectsListMapping() throws Exception {
        List dataObjects = new ArrayList();

        dataObjects.add(new TestObject("George", 5, true));
        dataObjects.add(new TestObject("Mary", 28, false));
        dataObjects.add(new TestObject("Joe", 31, true));

        String xml = new XMLEncoder(CayenneTestResources.getResourceURL(
                XML_DATA_DIR + "simple-mapping.xml").toExternalForm()).encode(
                "EncodedTestList",
                dataObjects);

        BufferedReader in = new BufferedReader(new InputStreamReader(CayenneTestResources
                .getResource(XML_DATA_DIR + "data-objects-mapped.xml")));
        StringBuffer comp = new StringBuffer();
        while (in.ready()) {
            comp.append(in.readLine()).append("\n");
        }

        assertEquals(comp.toString(), xml);
    }
}
