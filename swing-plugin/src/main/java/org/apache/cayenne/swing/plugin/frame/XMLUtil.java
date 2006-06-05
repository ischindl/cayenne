/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.cayenne.swing.plugin.frame;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.Predicate;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: andrus, 6/5/2006 -this code is copied from non-public
// org.objectstyle.cayenne.xml.XMLUtil - merge it somehow or make it public...
class XMLUtil {

    static DocumentBuilderFactory sharedFactory;

    /**
     * Creates a new instance of DocumentBuilder using the default factory.
     */
    static DocumentBuilder newBuilder() throws CayenneRuntimeException {
        if (sharedFactory == null) {
            sharedFactory = DocumentBuilderFactory.newInstance();
        }

        try {
            return sharedFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new CayenneRuntimeException("Can't create DocumentBuilder", e);
        }
    }

    /**
     * Returns all elements among the direct children that have a matching name.
     */
    static List getChildren(Node node, final String name) {
        Predicate p = new Predicate() {

            public boolean evaluate(Object object) {
                if (object instanceof Element) {
                    Element e = (Element) object;
                    return name.equals(e.getNodeName());
                }

                return false;
            }
        };

        return allMatches(node.getChildNodes(), p);
    }
    
    private static List allMatches(NodeList list, Predicate predicate) {
        int len = list.getLength();
        List children = new ArrayList(len);

        for (int i = 0; i < len; i++) {
            Node node = list.item(i);
            if (predicate.evaluate(node)) {
                children.add(node);
            }
        }

        return children;
    }
}
