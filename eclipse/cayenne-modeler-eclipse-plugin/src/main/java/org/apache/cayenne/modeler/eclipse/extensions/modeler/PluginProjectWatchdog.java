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

import java.util.List;
import java.awt.Frame;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.apache.cayenne.configuration.XMLDataMapLoader;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.ProjectWatchdog;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.dialog.codegen.CodeGeneratorController;
import org.apache.cayenne.modeler.dialog.codegen.GeneratorTabController;
import org.apache.cayenne.modeler.eclipse.Activator;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.ProjectLoader;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.URLResource;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Path;

/**
 * A <code>PluginProjectWatchdog</code> is a subclass of
 * <code>ProjectWatchdog</code> which is more specific for the
 * <code>CayenneModeler</code> Eclipse Integration. ProjectWatchdog class is
 * responsible for tracking changes in cayenne.xml and other Cayenne project
 * files.
 */
public class PluginProjectWatchdog extends ProjectWatchdog {

	private static Log logger = LogFactory.getLog(PluginProjectWatchdog.class);

	public PluginProjectWatchdog(ProjectController mediator) {
		super(mediator);
	}

	/**
	 * When a external change occurs this method is executed. For eclipse plugin
	 * we do not show confirmation window and just update the project in cayenne
	 * modeler
	 */
	@Override
	protected void doOnChange(FileInfo fileInfo) {
		// Currently we are reloading all project on a change
		if (mediator.getProject() != null) {

			File fileDirectory = new File(mediator.getProject()
					.getConfigurationResource().getURL().getPath());
			Application.getInstance().getActionManager()
					.getAction(OpenProjectAction.class)
					.openProject(fileDirectory);
			if (isValidExternalModifications(fileDirectory)) {
				generateJavaClasses(fileDirectory);
			}
		}

	}

	/**
	 * If the external modifications (from Eclipse IDE) are valid returns true
	 * otherwise returns false.
	 * 
	 * @param file
	 *            XML files related to the cayenne project.
	 */
	private boolean isValidExternalModifications(File file) {

		try {
			CayenneModelerController controller = Application.getInstance()
					.getFrameController();
			controller.addToLastProjListAction(file.getAbsolutePath());

			URL url = file.toURI().toURL();
			Resource rootSource = new URLResource(url);

			Project project = Application.getInstance().getInjector()
					.getInstance(ProjectLoader.class).loadProject(rootSource);

			mediator.setProject(project);
			List<ValidationFailure> allFailures = new ArrayList<ValidationFailure>();
			Collection<ValidationFailure> loadFailures = project
					.getConfigurationTree().getLoadFailures();

			if (!loadFailures.isEmpty()) {
				allFailures.addAll(loadFailures);
			}

			ProjectValidator projectValidator = Application.getInstance()
					.getInjector().getInstance(ProjectValidator.class);
			ValidationResult validationResult = projectValidator
					.validate(project.getRootNode());
			allFailures.addAll(validationResult.getFailures());

			if (!allFailures.isEmpty()) {
				logger.error("Exernal Modification of Cayenne Project are invalid.");
				return false;
			} else {
				logger.info("Re Generated the java classes acoording to the modifications.");
				return true;
			}

		} catch (Exception e) {
			logger.warn("Error loading project file.", e);
			ErrorDebugDialog.guiWarning(e, "Error loading project");
			return false;
		}

	}

	/**
	 * Re generate the java classes according to the changes in XML
	 * files.(Modification are done through the Eclipse IDE). If the
	 * modifications are invalid we are not re generating java classes and shows
	 * a proper error window.
	 */
	private void generateJavaClasses(File file) {

		XMLDataMapLoader datamapLoader = new XMLDataMapLoader();
		DataMap dataMap = null;
		try {
			dataMap = datamapLoader.load(mediator.getProject()
					.getConfigurationResource());
			JOptionPane.showMessageDialog(new Frame(), "THIS IS FOR TESTING..."
					+ dataMap.getLocation());

		} catch (Exception e) {
			logger.warn("Error loading project file.", e);
		}

		if (dataMap != null) {

			CodeGeneratorController codeGenerateController = new CodeGeneratorController(
					Application.getInstance().getFrameController(), dataMap);
			GeneratorTabController generatorSelector = new GeneratorTabController(
					codeGenerateController);

			ClassGenerationAction generator = generatorSelector.getGenerator();
			generator.addEmbeddables(dataMap.getEmbeddables());
			generator.addEntities(dataMap.getObjEntities());

			if (generator != null) {
				try {
					generator.execute();
					JOptionPane.showMessageDialog(Application.getInstance()
							.getFrameController().getView(),
							"Java Classes are Sucessfuly Updated ");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Application.getInstance()
							.getFrameController().getView(),
							"Error generating classes - " + e.getMessage());
				}
			}
		}
		// Update Eclipse workspace
		Activator.getDefault().updateWorkspace(
				new Path(mediator.getProject().getConfigurationResource()
						.getURL().getPath()));
	}
}
