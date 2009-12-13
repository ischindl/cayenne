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
import org.apache.cayenne.configuration.XMLPoolingDataSourceFactory;
import org.apache.cayenne.map.DataMap;

/**
 * A {@link ConfigurationNodeVisitor} that checks whether a given node should have its own
 * configuration file.
 * 
 * @since 3.1
 */
class ConfigurationSaveableGetter implements ConfigurationNodeVisitor<Boolean> {

    public Boolean visitDataChannelDescriptor(DataChannelDescriptor descriptor) {
        return Boolean.TRUE;
    }

    public Boolean visitDataMap(DataMap dataMap) {
        return Boolean.TRUE;
    }

    public Boolean visitDataNodeDescriptor(DataNodeDescriptor descriptor) {
        return XMLPoolingDataSourceFactory.class.getName().equals(
                descriptor.getDataSourceFactoryType());
    }
}
