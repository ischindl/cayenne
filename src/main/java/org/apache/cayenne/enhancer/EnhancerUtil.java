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
package org.apache.cayenne.enhancer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper for converting method names to property names.
 * 
 * @author Andrus Adamchik
 * @since 3.0
 */
// duplicated from JpaClassDescriptor.
class EnhancerUtil {

    private static final Pattern GETTER_PATTERN = Pattern
            .compile("^(is|get)([A-Z])(.*)$");

    private static final Pattern SETTER_PATTERN = Pattern.compile("^set([A-Z])(.*)$");

    static String propertyNameForGetter(String getterName) {
        Matcher getMatch = GETTER_PATTERN.matcher(getterName);
        if (getMatch.matches()) {
            return getMatch.group(2).toLowerCase() + getMatch.group(3);
        }

        return null;
    }

    static String propertyNameForSetter(String setterName) {
        Matcher setMatch = SETTER_PATTERN.matcher(setterName);

        if (setMatch.matches()) {
            return setMatch.group(1).toLowerCase() + setMatch.group(2);
        }

        return null;
    }
}
