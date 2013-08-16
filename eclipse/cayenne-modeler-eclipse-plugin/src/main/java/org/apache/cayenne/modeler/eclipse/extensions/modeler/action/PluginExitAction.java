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

package org.apache.cayenne.modeler.eclipse.extensions.modeler.action;

import java.awt.event.ActionEvent;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.action.ExitAction;
import org.apache.cayenne.modeler.dialog.LogConsole;
import org.apache.cayenne.pref.RenamedPreferences;

/**
 * A <code>PluginExitAction</code> is a subclass of <code>ExitAction</code>
 * which is more specific for the <code>CayenneModeler</code> Eclipse
 * Integration.
 */
public class PluginExitAction extends ExitAction {

	/**
	 * Constructor for PluginExitAction.
	 * 
	 * @param application
	 *            PluginApplication object which is running relevant to the
	 *            PluginExitAction
	 */
	public PluginExitAction(Application application) {
		super(application);

	}

	@Override
	public void performAction(ActionEvent e) {
		this.exit();
	}

	/**
	 * This method is executed when we close a project.
	 */
	@Override
	public void exit() {
		System.out.println("Shuting Down Cayenne Modeler");

		if (!checkSaveOnClose()) {
			return;
		}

		LogConsole.getInstance().stopLogging();
		RenamedPreferences.removeNewPreferences();

		application.getFrameController().getView().setVisible(false);
		closeProject(false);
	}
}