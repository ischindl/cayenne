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
package org.apache.cayenne.configuration;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;

/**
 * A descriptor of a DataChannel normally loaded from XML configuration.
 * 
 * @since 3.1
 */
public class DataChannelDescriptor implements ConfigurationNode, Serializable,
        XMLSerializable {

    protected String name;
    protected Map<String, String> properties;
    protected Collection<DataMap> dataMaps;
    protected Collection<DataNodeDescriptor> nodeDescriptors;
    protected Resource configurationSource;
    protected String defaultNodeName;

    public DataChannelDescriptor() {
        properties = new HashMap<String, String>();
        dataMaps = new ArrayList<DataMap>(5);
        nodeDescriptors = new ArrayList<DataNodeDescriptor>(3);
    }

	public String relativize(URI base, URI child) {
		if (base.getHost() != null && child.getHost() != null
				&& !base.getHost().equalsIgnoreCase(child.getHost())) {
			return child.toString();
		}
		// Normalize paths to remove . and .. segments
		base = base.normalize();
		child = child.normalize();

		// Split paths into segments
		String[] bParts = base.getPath().split("\\/");
		String[] cParts = child.getPath().split("\\/");

		// Discard trailing segment of base path
		if (bParts.length > 0 && !base.getPath().endsWith("/")) {
			bParts = Arrays.copyOf(bParts, bParts.length - 1);
		}

		// Remove common prefix segments
		int i = 0;
		while (i < bParts.length && i < cParts.length
				&& bParts[i].equals(cParts[i])) {
			i++;
		}

		// Construct the relative path
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < (bParts.length - i); j++) {
			sb.append("../");
		}
		for (int j = i; j < cParts.length; j++) {
			if (j != i) {
				sb.append("/");
			}
			sb.append(cParts[j]);
		}

		return sb.toString();
	}

    public void encodeAsXML(XMLEncoder encoder) {

        encoder.print("<domain");
        encoder.printProjectVersion();
        encoder.println(">");

        encoder.indent(1);
        boolean breakNeeded = false;

        if (!properties.isEmpty()) {
            breakNeeded = true;

            List<String> keys = new ArrayList<String>(properties.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                encoder.printProperty(key, properties.get(key));
            }
        }

        if (!dataMaps.isEmpty()) {
            if (breakNeeded) {
                encoder.println();
            }
            else {
                breakNeeded = true;
            }

            List<DataMap> maps = new ArrayList<DataMap>(this.dataMaps);
            Collections.sort(maps);

            for (DataMap dataMap : maps) {

                encoder.print("<map");
                encoder.printAttribute("name", dataMap.getName().trim());
				if (getConfigurationSource() != null
						&& dataMap.getConfigurationSource() != null)
					try {
						URL s = getConfigurationSource().getURL();
						URL s1 = dataMap.getConfigurationSource().getURL();
						String x = relativize(s.toURI(), s1.toURI());
						encoder.printAttribute("path", x);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                encoder.println("/>");
            }
        }

        if (!nodeDescriptors.isEmpty()) {
            if (breakNeeded) {
                encoder.println();
            }
            else {
                breakNeeded = true;
            }

            List<DataNodeDescriptor> nodes = new ArrayList<DataNodeDescriptor>(
                    nodeDescriptors);
            Collections.sort(nodes);
            encoder.print(nodes);
        }

        encoder.indent(-1);
        encoder.println("</domain>");
    }

    public <T> T acceptVisitor(ConfigurationNodeVisitor<T> visitor) {
        return visitor.visitDataChannelDescriptor(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Collection<DataMap> getDataMaps() {
        return dataMaps;
    }

    public DataMap getDataMap(String name) {
        for (DataMap map : dataMaps) {
            if (name.equals(map.getName())) {
                return map;
            }
        }
        return null;
    }

    public Collection<DataNodeDescriptor> getNodeDescriptors() {
        return nodeDescriptors;
    }

    public DataNodeDescriptor getNodeDescriptor(String name) {
        for (DataNodeDescriptor node : nodeDescriptors) {
            if (name.equals(node.getName())) {
                return node;
            }
        }

        return null;
    }

    public Resource getConfigurationSource() {
        return configurationSource;
    }

    public void setConfigurationSource(Resource configurationSource) {
        this.configurationSource = configurationSource;
    }

    /**
     * Returns the name of the DataNode that should be used as the default if a DataMap is
     * not explicitly linked to a node.
     */
    public String getDefaultNodeName() {
        return defaultNodeName;
    }

    public void setDefaultNodeName(String defaultDataNodeName) {
        this.defaultNodeName = defaultDataNodeName;
    }
}
