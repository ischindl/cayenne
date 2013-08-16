package org.apache.cayenne.modeler.eclipse.extensions.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CayenneContentProvider implements ITreeContentProvider {
	
	private CayenneViewModel model;

	public Object[] getElements(Object arg0) {
		return model.getProjects().toArray();
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer arg0, Object oldInput, Object newInput) {
		this.model = (CayenneViewModel)newInput;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof WorkspaceProject) {
			WorkspaceProject project = (WorkspaceProject)parentElement;
			return project.getCayenneProjects().toArray();
		}
		else if (parentElement instanceof CayenneProjectFile) {
			CayenneProjectFile project = (CayenneProjectFile)parentElement;
			return project.getDatamaps().toArray();
		}
		return null;
	}

	public Object getParent(Object arg0) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof WorkspaceProject;
	}

}
