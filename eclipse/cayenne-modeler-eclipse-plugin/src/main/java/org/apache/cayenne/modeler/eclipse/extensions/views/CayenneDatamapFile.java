package org.apache.cayenne.modeler.eclipse.extensions.views;

import java.io.File;

public class CayenneDatamapFile {
	
	private File path;
	private String name;
	
	private CayenneProjectFile parentProject;
	
	public CayenneDatamapFile(
			String name, File path, CayenneProjectFile parentProject) {
		this.name = name;
		this.path = path;
		this.parentProject = parentProject;
	}
	
	public String getName() {
		return name;
	}
	
	public File getPath() {
		return path;
	}
	
	public CayenneProjectFile getParentProject() {
		return parentProject;
	}

}
