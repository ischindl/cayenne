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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerController;

import org.apache.cayenne.modeler.ModelerConstants;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.dialog.codegen.CodeGeneratorController;
import org.apache.cayenne.modeler.dialog.codegen.GeneratorTabController;
import org.apache.cayenne.modeler.dialog.validator.ValidatorDialog;
import org.apache.cayenne.modeler.eclipse.Activator;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.action.PluginExitAction;
import org.apache.cayenne.modeler.editor.EditorView;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.cayenne.modeler.pref.ComponentGeometry;
import org.apache.cayenne.modeler.util.FileFilters;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.eclipse.core.runtime.Path;

/**
 * A <code>PluginModelerController</code> is a subclass of
 * <code>CayenneModelerController</code> which is more specific for the
 * <code>CayenneModeler</code> Eclipse Integration.PluginMain class responsible
 * for starting CayenneModeler. This as the Controller of the main application
 * frame.
 */
public class PluginModelerController extends CayenneModelerController {

	/**
	 * Constructor of <code>PluginModelerController</code>
	 * 
	 * @param application
	 *            PluginApplication object relevant to the controller.
	 */
	public PluginModelerController(PluginApplication application) {
		// since super class constructor is setting application we need to set
		// it here
		this.application = application;
		this.frame = new PluginModelerFrame(
				application.getActionManager(),
				application.getInjector());

		application.getInjector().getInstance(PlatformInitializer.class)
				.setupMenus(frame);
		this.projectController = new PluginProjectController(this);

	}

	public void setFrame(PluginModelerFrame frame) {
		this.frame = frame;
	}

	/**
	 * In case of saving a Cayenne project from Cayenne modeler this method
	 * execute java class Generation and Update Eclipse working space.
	 */
	@Override
	public void projectSavedAction() {

		DataMap dataMap = application.getFrameController()
				.getProjectController().getCurrentDataMap();
		if (dataMap != null) {

			CodeGeneratorController codeGenerateController = new CodeGeneratorController(
					getApplication().getFrameController(), dataMap);
			GeneratorTabController generatorSelector = new GeneratorTabController(
					codeGenerateController);

			ClassGenerationAction generator = generatorSelector.getGenerator();
			generator.addEmbeddables(dataMap.getEmbeddables());
			generator.addEntities(dataMap.getObjEntities());

			if (generator != null) {
				try {
					generator.execute();
					JOptionPane.showMessageDialog(this.getView(),
							"Java Classes are Successfully Updated ");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this.getView(),
							"Error generating classes - " + e.getMessage());
				}
			}
		}
		// Update Eclipse workspace
		Activator.getDefault().updateWorkspace(
				new Path(projectController.getProject()
						.getConfigurationResource().getURL().getPath()));
		super.projectSavedAction();

	}

	@Override
	protected void initBindings() {

		frame.addWindowListener(new WindowAdapter() {
			// PluginExit Action is set for project exit.
			public void windowClosing(WindowEvent e) {
				getApplication().getActionManager()
						.getAction(PluginExitAction.class).exit();
			}
		});

		new DropTarget(frame, new DropTargetAdapter() {

			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(dtde.getDropAction());
				Transferable transferable = dtde.getTransferable();
				dtde.dropComplete(processDropAction(transferable));
			}
		});

		ComponentGeometry geometry = new ComponentGeometry(frame.getClass(),
				null);
		geometry.bind(frame, 650, 550, 0);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean processDropAction(Transferable transferable) {
		List<File> fileList;
		try {
			fileList = (List) transferable
					.getTransferData(DataFlavor.javaFileListFlavor);
		} catch (Exception e) {
			return false;
		}

		File transferFile = fileList.get(0);

		if (transferFile.isFile()) {

			FileFilter filter = FileFilters.getApplicationFilter();

			if (filter.accept(transferFile)) {
				ActionEvent e = new ActionEvent(transferFile,
						ActionEvent.ACTION_PERFORMED, "OpenProject");
				Application.getInstance().getActionManager()
						.getAction(OpenProjectAction.class).actionPerformed(e);
				return true;
			}
		}

		return false;
	}

	@Override
	public void projectOpenedAction(Project project) {
		projectController.setProject(project);
		frame.setView(new EditorView(projectController));
		projectController.projectOpened();
		application.getActionManager().projectOpened();

		// do status update AFTER the project is actually opened...
		if (project.getConfigurationResource() == null) {
			updateStatus("New project created...");
			frame.setTitle(ModelerConstants.TITLE + "- [New]");
		} else {
			updateStatus("Project opened...");
			frame.setTitle(ModelerConstants.TITLE + " - "
					+ project.getConfigurationResource().getURL().getPath());
		}

		// update preferences
		if (project.getConfigurationResource() != null) {
			getLastDirectory().setDirectory(
					new File(project.getConfigurationResource().getURL()
							.getPath()));
			frame.fireRecentFileListChanged();
		}

		// for validation purposes combine load failures with post-load
		// validation (not
		// sure if that'll cause duplicate messages?).
		List<ValidationFailure> allFailures = new ArrayList<ValidationFailure>();
		Collection<ValidationFailure> loadFailures = project
				.getConfigurationTree().getLoadFailures();

		if (!loadFailures.isEmpty()) {
			// mark project as unsaved
			project.setModified(true);
			projectController.setDirty(true);
			allFailures.addAll(loadFailures);
		}

		ProjectValidator projectValidator = getApplication().getInjector()
				.getInstance(ProjectValidator.class);
		ValidationResult validationResult = projectValidator.validate(project
				.getRootNode());
		allFailures.addAll(validationResult.getFailures());
		if (!allFailures.isEmpty()) {
			ValidatorDialog.showDialog(frame, validationResult.getFailures());
		}
	}
}
