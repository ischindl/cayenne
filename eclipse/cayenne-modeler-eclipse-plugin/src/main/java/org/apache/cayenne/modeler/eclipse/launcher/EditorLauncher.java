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

package org.apache.cayenne.modeler.eclipse.launcher;

import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginMain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ui.IEditorLauncher;

public class EditorLauncher implements IEditorLauncher {
	
	private static Log logger = LogFactory.getLog(EditorLauncher.class);

	public void open(IPath file) {
		logger.info("Launch Successful : file Location = "
				+ file.toString());

		String[] args = new String[] { file.toString() };
		PluginMain plugin = new PluginMain(args);
		plugin.start();

	}

}