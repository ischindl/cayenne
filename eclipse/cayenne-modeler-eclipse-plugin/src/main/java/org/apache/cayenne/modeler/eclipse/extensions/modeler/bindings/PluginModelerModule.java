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

package org.apache.cayenne.modeler.eclipse.extensions.modeler.bindings;

import org.apache.cayenne.di.Binder;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.action.ActionManager;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginActionManager;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginApplication;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.init.platform.GenericPlatformInitializer;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.cayenne.modeler.util.DefaultWidgetFactory;
import org.apache.cayenne.modeler.util.WidgetFactory;
import org.apache.cayenne.project.validation.DefaultProjectValidator;
import org.apache.cayenne.project.validation.ProjectValidator;

/**
 * A DI module for bootstrapping CayenneModeler services.
 */
public class PluginModelerModule extends CayenneModelerModule {

	/**
	 * Bind plugin specific Classes. So Those instances can be obtained from the
	 * Injector.
	 */
	public void configure(Binder binder) {
		binder.bind(PluginActionManager.class).to(PluginActionManager.class);
		binder.bind(ActionManager.class).to(PluginActionManager.class);
		binder.bind(Application.class).to(PluginApplication.class);
		binder.bind(PlatformInitializer.class).to(
				GenericPlatformInitializer.class);
		binder.bind(WidgetFactory.class).to(DefaultWidgetFactory.class);
		binder.bind(ProjectValidator.class).to(DefaultProjectValidator.class);
	}
}
