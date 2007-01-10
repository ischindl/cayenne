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

package org.apache.cayenne.dba;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.util.Util;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** 
 * TypesHandler provides JDBC-RDBMS types mapping. Loads types info from 
 * an XML file.
 * 
 * @author Andrei Adamchik
 */
public class TypesHandler {
    private static Logger logObj = Logger.getLogger(TypesHandler.class);

    private static Map handlerMap = new HashMap();

    protected Map typesMap;

    /**
     * @since 1.1
     */
    public static TypesHandler getHandler(URL typesConfig) {
        synchronized (handlerMap) {
            TypesHandler handler = (TypesHandler) handlerMap.get(typesConfig);

            if (handler == null) {
                handler = new TypesHandler(typesConfig);
                handlerMap.put(typesConfig, handler);
            }

            return handler;
        }
    }

    /**
     * Creates new TypesHandler loading configuration info from the XML
     * file specified as <code>typesConfigPath</code> parameter.
     * 
     * @since 1.1
     */
    public TypesHandler(URL typesConfig) {
        try {
            InputStream in = typesConfig.openStream();

            try {
                XMLReader parser = Util.createXmlReader();
                TypesParseHandler ph = new TypesParseHandler();
                parser.setContentHandler(ph);
                parser.setErrorHandler(ph);
                parser.parse(new InputSource(in));

                typesMap = ph.getTypes();
            }
            catch (Exception ex) {
                throw new CayenneRuntimeException(
                    "Error creating TypesHandler '" + typesConfig + "'.",
                    ex);
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException ioex) {
                }
            }
        }
        catch (IOException ioex) {
            throw new CayenneRuntimeException(
                "Error opening config file '" + typesConfig + "'.",
                ioex);
        }
    }

    public String[] externalTypesForJdbcType(int type) {
        return (String[]) typesMap.get(new Integer(type));
    }

    /** 
     * Helper class to load types data from XML.
     */
    final class TypesParseHandler extends DefaultHandler {
        private static final String JDBC_TYPE_TAG = "jdbc-type";
        private static final String DB_TYPE_TAG = "db-type";
        private static final String NAME_ATTR = "name";

        private Map types = new HashMap();
        private List currentTypes = new ArrayList();
        private int currentType = TypesMapping.NOT_DEFINED;

        public Map getTypes() {
            return types;
        }

        public void startElement(
            String namespaceURI,
            String localName,
            String qName,
            Attributes atts)
            throws SAXException {
            if (JDBC_TYPE_TAG.equals(localName)) {
                currentTypes.clear();
                String strType = atts.getValue("", NAME_ATTR);

                // convert to Types int value
                try {
                    currentType = Types.class.getDeclaredField(strType).getInt(null);
                }
                catch (Exception ex) {
                    currentType = TypesMapping.NOT_DEFINED;
                    logObj.info("type not found: '" + strType + "', ignoring.");
                }
            }
            else if (DB_TYPE_TAG.equals(localName)) {
                currentTypes.add(atts.getValue("", NAME_ATTR));
            }
        }

        public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
            if (JDBC_TYPE_TAG.equals(localName)
                && currentType != TypesMapping.NOT_DEFINED) {
                String[] typesAsArray = new String[currentTypes.size()];
                types.put(new Integer(currentType), currentTypes.toArray(typesAsArray));
            }
        }
    }
}
