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
package org.apache.cayenne.swing.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.platonos.pluginengine.PluginEngine;
import org.platonos.pluginengine.PluginEngineException;
import org.platonos.pluginengine.logging.ILogger;
import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * An extension of PluginEngine that allows to store arbitrary attributes in the
 * engine and also load plugins from CLASSPATH.
 * 
 * @author andrus
 */
public class CayennePluginEngine extends PluginEngine {

	public static final String PLUGIN_LOCATION = "plugin.xml";

	protected Map attributes = new HashMap();

	public CayennePluginEngine(String uid, ILogger logger) {
		super(uid, logger);
	}

	public CayennePluginEngine(String uid) {
		super(uid);
	}

	/**
	 * Loads all plugin descriptors that are available to the current
	 * ClassLoader.
	 */
	public void loadBundledPlugins() {
		Enumeration urls;

		try {
			urls = Thread.currentThread().getContextClassLoader().getResources(
					PLUGIN_LOCATION);

		} catch (IOException e) {
			throw new RuntimeException(
					"Error reading plugins infor from ClassLoader", e);
		}

		while (urls.hasMoreElements()) {
			URL pluginURL = (URL) urls.nextElement();
			try {
				// TODO, andrus, 6/5/2006 - plugins loaded in this fashion will
				// only be able to resolve classes from the same filesystem
				// folder as plugin.xml. Will need a specialized ClassLoader for
				// this case.
				loadPluginXML(pluginURL);
			} catch (PluginEngineException ex) {
				getLogger().log(LoggerLevel.SEVERE,
						"Error loading Plugin archive from URL: " + pluginURL,
						ex);
			}
		}
	}

	/**
	 * Returns a previously stored attribute for a given key or null if no
	 * attribute matches the key.
	 */
	public Object getAttribute(String key) {
		if (key == null) {
			throw new NullPointerException("Invalid argument: key");
		}

		return (String) attributes.get(key);
	}

	public void setAttribute(String key, Object value) {
		if (key == null) {
			throw new NullPointerException("Invalid argument: key");
		}

		attributes.put(key, value);
	}

	public void removeAttribute(String key) {
		if (key == null) {
			throw new NullPointerException("Invalid argument: key");
		}

		attributes.remove(key);
	}
}
