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

import java.util.Iterator;
import java.util.StringTokenizer;

import org.platonos.pluginengine.Plugin;
import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * Starts a plugin shell for a Swing application.
 */
public class Launcher {

	public static final String COMMAND_LINE_ATTRIBUTE = "command.line";

	static final String ENGINE_NAME = "Cayenne Plugin Engine";

	/**
	 * A property specifying a location of the plugins directory. It can be a
	 * comma separated list of directories. If not set, current directory is
	 * used.
	 */
	public static final String PLUGINS_DIR_PROPERTY = "plugins.dirs";

	public static void main(String[] args) {

		CayennePluginEngine pluginEngine = new CayennePluginEngine(ENGINE_NAME);
		pluginEngine.setAttribute(COMMAND_LINE_ATTRIBUTE, args);

		// load plugins from ClassPath
		pluginEngine.loadBundledPlugins();

		// load plugins from extra directories
		String pluginDirectories = System.getProperty(PLUGINS_DIR_PROPERTY);
		if (pluginDirectories != null) {
			StringTokenizer toks = new StringTokenizer(pluginDirectories, ",");
			while (toks.hasMoreTokens()) {
				pluginEngine.loadPlugins(toks.nextToken());
			}
		}

		pluginEngine.start();

		boolean hasStartedPlugins = false;
		Iterator it = pluginEngine.getPlugins().iterator();
		while (it.hasNext()) {
			Plugin p = (Plugin) it.next();
			if (p.isStarted()) {
				hasStartedPlugins = true;
				break;
			}
		}

		// either no plugins configured, all all of them failed to start.
		if (!hasStartedPlugins) {
			pluginEngine.getLogger().log(LoggerLevel.INFO,
					"No plugins started, exiting", null);
			
			// must explicitly kill all UI threads
			System.exit(0);
		}
	}
}
