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

import org.apache.cayenne.di.Inject;

import org.apache.cayenne.modeler.Application;

import org.apache.cayenne.modeler.action.DefaultActionManager;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.action.PluginExitAction;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.action.PluginProjectAction;

/**
 * A <code>PluginActionManager</code> is a subclass of
 * <code>DefaultActionManager</code> which is more specific for the
 * <code>CayenneModeler</code> Eclipse PluginActionManager class responsible for
 * registering and removing actions which is required by the Eclipse Integration
 * plugin.
 */
public class PluginActionManager extends DefaultActionManager {

	/**
	 * Constructor of <code>PluginActionManager</code>
	 * 
	 * @param application
	 *            Application Instance which is running in the form of
	 *            PluginApplication.
	 */
	public PluginActionManager(@Inject Application application) {
		super(application);
		configureActions(application);
	}

	/**
	 * Configurations of the DefaultActionManager class's Action Collections is
	 * done here.
	 * 
	 * @param application
	 *            Application Instance which is running in the form
	 */

	private void configureActions(Application application) {

		addProjectAction(PluginProjectAction.class.getName());
		registerAction(new PluginProjectAction(application));
		registerAction(new PluginExitAction(application)).setAlwaysOn(true);

	}
}