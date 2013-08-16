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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.dialog.LogConsole;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginActionManager;
import org.apache.cayenne.modeler.undo.CayenneUndoManager;
import org.apache.cayenne.modeler.util.AdapterMapping;
import org.apache.cayenne.modeler.util.CayenneDialog;
import org.apache.cayenne.swing.BindingFactory;

import org.scopemvc.controller.basic.ViewContext;
import org.scopemvc.controller.swing.SwingContext;
import org.scopemvc.core.View;
import org.scopemvc.util.UIStrings;
import org.scopemvc.view.swing.SwingView;

/**
 * A <code>PluginApplication</code> is a subclass of <code>Application</code>
 * which is more specific for the <code>CayenneModeler</code> Eclipse
 * Integration.
 */

public class PluginApplication extends Application {
	
	protected boolean started;

	/**
	 * <code>CayenneModelerController</code> of the main application frame. In
	 * this we are referring Eclipse Integration specific implementation of
	 * <code>CayenneModelerController</code> which is
	 * <code>PluginModelerController</code>
	 */

	/**
	 * Returns Action Manager of PlugingApplication object in the form of
	 * <code>ActionManager</code>
	 * 
	 * @return a <code>ActionManager</code> object that indicates the Action
	 *         Manager of this component
	 * @see Application#getInjector
	 */
	@Override
	public PluginActionManager getActionManager() {
		return injector.getInstance(PluginActionManager.class);

	}

	/**
	 * Returns CayenneModelerController of PlugingApplication object in the form
	 * of <code>PluginModelerController</code>
	 * 
	 * @return a <code>PluginModelerController</code> object that indicates the
	 *         CayenneModelerController of this component
	 */
	@Override
	public CayenneModelerController getFrameController() {
		return frameController;
	}

	/**
	 * Specific Implementation of startup method of <code>Application</code> for
	 * Cayenne Eclipse Integration. Starts the PlugingApplication for
	 * CayanneModeler.
	 */
	@Override
	public void startup() {
		if (started) {
			return;
		}
		
		initPreferences();
		initClassLoader();
		this.bindingFactory = new BindingFactory();
		this.adapterMapping = new AdapterMapping();
		UIStrings.setPropertiesName(DEFAULT_MESSAGE_BUNDLE);
		ViewContext.clearThreadContext();

		this.undoManager = new CayenneUndoManager(this);
		// Assign Eclipse plugin specific implementation
		this.frameController = new PluginModelerController(this);
		ViewContext.setGlobalContext(new ModelerContext(frameController
				.getFrame()));
		// open up
		frameController.startupAction();
		LogConsole.getInstance().showConsoleIfNeeded();
		getFrameController().getFrame().setVisible(true);
		started = true;
	}

	final class ModelerContext extends SwingContext {

		JFrame frame;

		public ModelerContext(JFrame frame) {
			this.frame = frame;
		}

		@Override
		protected void showViewInPrimaryWindow(SwingView view) {
		}

		/**
		 * Creates closeable dialogs.
		 */
		@Override
		protected void showViewInDialog(SwingView inView) {
			Window parentWindow = getDefaultParentWindow();

			final CayenneDialog dialog;
			if (parentWindow instanceof Dialog) {
				dialog = new CayenneDialog((Dialog) parentWindow);
			} else {
				dialog = new CayenneDialog((Frame) parentWindow);
			}

			if (inView.getTitle() != null) {
				dialog.setTitle(inView.getTitle());
			}
			if (inView.getDisplayMode() == SwingView.MODAL_DIALOG) {
				dialog.setModal(true);
			} else {
				dialog.setModal(false);
			}
			dialog.setResizable(inView.isResizable());

			setupWindow(dialog.getRootPane(), inView, true);
			dialog.toFront();
		}

		/**
		 * Overrides super implementation to allow using Scope together with
		 * normal Swing code that CayenneModeler already has.
		 */
		@Override
		public JRootPane findRootPaneFor(View view) {
			JRootPane pane = super.findRootPaneFor(view);

			if (pane != null) {
				return pane;
			}

			if (((SwingView) view).getDisplayMode() != SwingView.PRIMARY_WINDOW) {
				return pane;
			}

			return frame.getRootPane();
		}

		@Override
		protected Window getDefaultParentWindow() {
			return frame;
		}
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public void toFront() {
		getFrameController().getFrame().toFront();
	}

}