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

import java.io.File;

import javax.swing.SwingUtilities;

import org.apache.cayenne.di.Injector;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.Main;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.eclipse.Activator;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A <code>PluginMain</code> is a subclass of <code>Main</code> which is more
 * specific for the <code>CayenneModeler</code> Eclipse Integration.PluginMain
 * class responsible for starting CayenneModeler.
 */
public class PluginMain extends Main {

	private static Log logger = LogFactory.getLog(PluginMain.class);

	/**
	 * Constructor of <code>PluginMain</code>
	 * 
	 * @param args
	 *            FIle location of Cayenne Project file.
	 * @see Main#main
	 */
	public PluginMain(String[] args) {
		super(args);
	}

	/**
	 * Main method that starts the CayenneModeler.
	 */
	public void start() {
		try {
			launch();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * launch method that starts the CayenneModeler.
	 */
	@Override
	protected void launch() {
		logger.info("Starting CayenneModeler.");
		logger.info("JRE v." + System.getProperty("java.version") + " at "
				+ System.getProperty("java.home"));
		
		final File project = initialProjectFromArgs();

		final Injector injector = Activator.getDefault().getInjector(project.getPath());
		// init look and feel before starting any Swing classes...
		injector.getInstance(PlatformInitializer.class).initLookAndFeel();

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				PluginApplication application = (PluginApplication) injector
						.getInstance(Application.class);
				PluginApplication.setInstance(application);

				if (application.isStarted()) {
					application.toFront();
				} else {
					application.startup();

					if (project != null) {
						new OpenProjectAction(application).openProject(project);
					}
				}
			}
		});

	}
}