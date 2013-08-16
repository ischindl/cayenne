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

package org.apache.cayenne.modeler.eclipse;

import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.bindings.PluginModelerModule;
import org.apache.cayenne.project.CayenneProjectModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Log logger = LogFactory.getLog(Activator.class);

	/**
	 * The shared instance.
	 */
	private static Activator plugin;
	public static final String PLUGIN_ID = "org.apache.cayenne.modeler.eclipse.plugin";

	protected Injector injector;
	protected String projectName;

	/**
	 * The constructor.
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance of the plug-in.
	 */
	public static Activator getDefault() {
		return plugin == null ? new Activator() : plugin;
	}

	public void updateWorkspace(IPath path) {

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project = root.getFileForLocation(path).getProject();
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e) {
			logger.error("Error updating workspace.");
		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Injector getInjector(String projectName) {
		if (!projectName.equals(this.projectName)) {

			if (injector != null) {
				Application app = injector.getInstance(Application.class);
				app.getFrameController().projectClosedAction();
				app.getFrameController().getFrame().dispose();
			}

			injector = DIBootstrap.createInjector(
					new ServerModule("CayenneModeler"),
					new CayenneProjectModule(),
					new PluginModelerModule());

			this.projectName = projectName;
		}
		return injector;
	}

	public Injector getInjector() {
		return injector;
	}
}
