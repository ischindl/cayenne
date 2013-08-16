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

package org.apache.cayenne.modeler.eclipse.extensions.modeler;

import java.util.Collection;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.ProjectWatchdog;
import org.apache.cayenne.project.Project;

/**
 * A <code>PluginProjectController</code> is a subclass of
 * <code>ProjectController</code> which is more specific for the
 * <code>CayenneModeler</code> Eclipse Integration.
 */
public class PluginProjectController extends ProjectController {

	private EntityResolver entityResolver;
	private PluginProjectWatchdog watchdog;

	/**
	 * Constructor of <code>PluginProjectController</code>
	 * 
	 * @param parent
	 *            controller for the plugin application frame.
	 */
	public PluginProjectController(CayenneModelerController parent) {
		super(parent);
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	/**
	 * returns instance of PluginProjectWatchdog class is responsible for
	 * tracking changes in cayenne.xml and other Cayenne project files
	 */
	public ProjectWatchdog getProjectWatcher() {
		return watchdog;
	}

	/**
	 * Start PluginProjectWatchdog which is checking the changes in Cayenne
	 * project when we modify the Cayenne related XML file from Eclipse IDE
	 */
	@Override
	public void setProject(Project currentProject) {
		if (this.project != currentProject) {

			this.project = currentProject;
			this.projectControllerPreferences = null;

			if (project == null) {
				this.entityResolver = null;

				if (watchdog != null) {
					watchdog.interrupt();
					watchdog = null;
				}
			} else {
				if (watchdog == null) {
					watchdog = new PluginProjectWatchdog(this);
					watchdog.start();
				}

				watchdog.reconfigure();

				entityResolver = new EntityResolver(
						((DataChannelDescriptor) currentProject.getRootNode())
								.getDataMaps());

				updateEntityResolver();
			}
		}
	}

	public void updateEntityResolver() {

		Collection<DataMap> dataMaps = ((DataChannelDescriptor) project
				.getRootNode()).getDataMaps();

		entityResolver.setDataMaps(dataMaps);

		for (DataMap dataMap : dataMaps) {
			dataMap.setNamespace(entityResolver);
		}
	}
}
