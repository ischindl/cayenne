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

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.cayenne.di.Injector;

import org.apache.cayenne.modeler.action.ActionManager;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerFrame;
import org.apache.cayenne.modeler.action.AboutAction;
import org.apache.cayenne.modeler.action.ConfigurePreferencesAction;
import org.apache.cayenne.modeler.action.CopyAction;
import org.apache.cayenne.modeler.action.CreateDataMapAction;
import org.apache.cayenne.modeler.action.CreateDbEntityAction;
import org.apache.cayenne.modeler.action.CreateEmbeddableAction;
import org.apache.cayenne.modeler.action.CreateNodeAction;
import org.apache.cayenne.modeler.action.CreateObjEntityAction;
import org.apache.cayenne.modeler.action.CreateProcedureAction;
import org.apache.cayenne.modeler.action.CreateQueryAction;
import org.apache.cayenne.modeler.action.CutAction;
import org.apache.cayenne.modeler.action.DocumentationAction;
import org.apache.cayenne.modeler.action.GenerateCodeAction;
import org.apache.cayenne.modeler.action.GenerateDBAction;
import org.apache.cayenne.modeler.action.ImportDBAction;
import org.apache.cayenne.modeler.action.ImportDataMapAction;
import org.apache.cayenne.modeler.action.ImportEOModelAction;
import org.apache.cayenne.modeler.action.InferRelationshipsAction;
import org.apache.cayenne.modeler.action.MigrateAction;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.ObjEntitySyncAction;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.action.PasteAction;
import org.apache.cayenne.modeler.action.ProjectAction;
import org.apache.cayenne.modeler.action.RedoAction;
import org.apache.cayenne.modeler.action.RemoveAction;
import org.apache.cayenne.modeler.action.RevertAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.action.ShowLogConsoleAction;
import org.apache.cayenne.modeler.action.UndoAction;
import org.apache.cayenne.modeler.action.ValidateAction;
import org.apache.cayenne.modeler.dialog.LogConsole;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.action.PluginExitAction;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.action.PluginProjectAction;

/**
 * A <code>PluginModelerFrame</code> is a subclass of
 * <code>CayenneModelerFrame</code> which is more specific for the
 * <code>CayenneModeler</code> Eclipse Integration.
 */
public class PluginModelerFrame extends CayenneModelerFrame {
	
	protected Injector injector;

	/**
	 * Constructor of <code>PluginModelerFrame</code>
	 * 
	 * @param actionManager
	 *            ActionManager which Stores a map of modeler actions, and deals
	 *            with those actions
	 */
	public PluginModelerFrame(ActionManager actionManager, Injector injector) {
		super(actionManager);
		
		this.injector = injector;
	}

	/**
	 * Menu creation Specific to the Eclipse integration project.
	 */
	@Override
	protected void initMenus() {
		getContentPane().setLayout(new BorderLayout());

		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu projectMenu = new JMenu("Project");
		JMenu toolMenu = new JMenu("Tools");
		JMenu helpMenu = new JMenu("Help");

		fileMenu.setMnemonic(KeyEvent.VK_F);
		editMenu.setMnemonic(KeyEvent.VK_E);
		projectMenu.setMnemonic(KeyEvent.VK_P);
		toolMenu.setMnemonic(KeyEvent.VK_T);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		fileMenu.add(getAction(NewProjectAction.class).buildMenu());
		fileMenu.add(getAction(OpenProjectAction.class).buildMenu());
		
		// Set PluginProjectAction to file menu
		fileMenu.add(getAction(PluginProjectAction.class).buildMenu());
		fileMenu.add(getAction(ImportDataMapAction.class).buildMenu());
		fileMenu.addSeparator();
		fileMenu.add(getAction(SaveAction.class).buildMenu());
		fileMenu.add(getAction(SaveAsAction.class).buildMenu());
		fileMenu.add(getAction(RevertAction.class).buildMenu());
		fileMenu.addSeparator();

		// Disable Unwanted Actions for Eclipse Plugin project
		disableUnwantedMenuItems(fileMenu);

		editMenu.add(getAction(UndoAction.class).buildMenu());
		editMenu.add(getAction(RedoAction.class).buildMenu());
		editMenu.add(getAction(CutAction.class).buildMenu());
		editMenu.add(getAction(CopyAction.class).buildMenu());
		editMenu.add(getAction(PasteAction.class).buildMenu());

		fileMenu.add(getAction(PluginExitAction.class).buildMenu());

		projectMenu.add(getAction(ValidateAction.class).buildMenu());
		projectMenu.addSeparator();
		projectMenu.add(getAction(CreateNodeAction.class).buildMenu());
		projectMenu.add(getAction(CreateDataMapAction.class).buildMenu());

		projectMenu.add(getAction(CreateObjEntityAction.class).buildMenu());
		projectMenu.add(getAction(CreateEmbeddableAction.class).buildMenu());
		projectMenu.add(getAction(CreateDbEntityAction.class).buildMenu());

		projectMenu.add(getAction(CreateProcedureAction.class).buildMenu());
		projectMenu.add(getAction(CreateQueryAction.class).buildMenu());

		projectMenu.addSeparator();
		projectMenu.add(getAction(ObjEntitySyncAction.class).buildMenu());
		projectMenu.addSeparator();
		projectMenu.add(getAction(RemoveAction.class).buildMenu());

		toolMenu.add(getAction(ImportDBAction.class).buildMenu());
		toolMenu.add(getAction(InferRelationshipsAction.class).buildMenu());
		toolMenu.add(getAction(ImportEOModelAction.class).buildMenu());
		toolMenu.addSeparator();
		toolMenu.add(getAction(GenerateCodeAction.class).buildMenu());
		toolMenu.add(getAction(GenerateDBAction.class).buildMenu());
		toolMenu.add(getAction(MigrateAction.class).buildMenu());

		// Menu for opening Log console
		toolMenu.addSeparator();

		logMenu = getAction(ShowLogConsoleAction.class).buildCheckBoxMenu();

		if (!LogConsole.getInstance().getConsoleProperty(
				LogConsole.DOCKED_PROPERTY)
				&& LogConsole.getInstance().getConsoleProperty(
						LogConsole.SHOW_CONSOLE_PROPERTY)) {
			LogConsole.getInstance().setConsoleProperty(
					LogConsole.SHOW_CONSOLE_PROPERTY, false);
		}

		updateLogConsoleMenu();
		toolMenu.add(logMenu);

		toolMenu.addSeparator();
		toolMenu.add(getAction(ConfigurePreferencesAction.class).buildMenu());

		helpMenu.add(getAction(AboutAction.class).buildMenu());
		helpMenu.add(getAction(DocumentationAction.class).buildMenu());

		JMenuBar menuBar = new JMenuBar();

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(projectMenu);
		menuBar.add(toolMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

	}

	private <T extends Action> T getAction(Class<T> type) {

		return actionManager.getAction(type);
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		
		if (e.getID() == WindowEvent.WINDOW_CLOSED) {
			PluginApplication app = (PluginApplication)injector.getInstance(
					Application.class);
			app.setStarted(false);
		}
	}

	private void disableUnwantedMenuItems(JMenu fileMenu) {

		for (int i = 0; i < fileMenu.getItemCount(); i++) {
			JMenuItem item = fileMenu.getItem(i);
			if (item != null) {
				String name = item.getText();
				if (NewProjectAction.getActionName().equals(name)
						|| OpenProjectAction.getActionName().equals(name)
						|| ProjectAction.getActionName().equals(name)
						|| "Recent Projects".equals(name)) {
					item.setEnabled(false);
				}
			}
		}
	}
}
