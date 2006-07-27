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


package org.apache.cayenne.jpa.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton object storing shared provider information.
 * 
 * @author Andrus Adamchik
 */
public class JpaProviderContext {

    protected static Map context;

    public static synchronized Object getObject(String key) {
        return context == null ? null : context.get(key);
    }

    public static synchronized void setObject(String key, Object value) {

        if (context == null) {

            if (value == null) {
                return;
            }

            context = new HashMap();
        }

        context.put(key, value);
    }
}
