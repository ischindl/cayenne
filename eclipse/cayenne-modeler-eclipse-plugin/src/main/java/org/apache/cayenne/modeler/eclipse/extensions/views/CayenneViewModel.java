package org.apache.cayenne.modeler.eclipse.extensions.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

public class CayenneViewModel {
	
	public List<WorkspaceProject> getProjects() {
		List<WorkspaceProject> projects = new ArrayList<WorkspaceProject>();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		for (IProject project : workspace.getRoot().getProjects()) {
			WorkspaceProject proj = new WorkspaceProject(
					project.getName(), 
					project.getLocation().toFile());
			projects.add(proj);
		}
		
		return projects;
	}

}
