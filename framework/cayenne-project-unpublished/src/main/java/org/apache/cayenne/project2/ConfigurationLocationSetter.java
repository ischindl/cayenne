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
package org.apache.cayenne.project2;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;

/**
 * @since 3.1
 * @deprecated location is a redundant piece of information that we still keep in XML, but
 *             really need to remove.
 */
class ConfigurationLocationSetter implements ConfigurationNodeVisitor<Void> {

    private String location;

    ConfigurationLocationSetter(String location) {
        this.location = location;
    }

    public Void visitDataChannelDescriptor(DataChannelDescriptor node) {
        // noop
        return null;
    }

    public Void visitDataMap(DataMap node) {
        node.setLocation(location);
        return null;
    }

    public Void visitDataNodeDescriptor(DataNodeDescriptor node) {
        node.setLocation(location);
        return null;
    }
}
