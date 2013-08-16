package org.apache.cayenne.modeler.eclipse.extensions.actions;

import org.apache.cayenne.di.Injector;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.eclipse.Activator;
import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginApplication;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ModelerToolbarAction implements IWorkbenchWindowActionDelegate {

	public void run(IAction arg0) {
		Injector injector = Activator.getDefault().getInjector();
		
		// if Modeler is not started then doing nothing
		if (injector == null) {
			return;
		}
		
		PluginApplication application = (PluginApplication)injector.getInstance(Application.class);
		if (application != null && application.isStarted()) {
			application.toFront();
		}
	}

	public void selectionChanged(IAction arg0, ISelection arg1) {
		// noop
	}

	public void dispose() {
		// noop
	}

	public void init(IWorkbenchWindow arg0) {
		// noop
	}

}
