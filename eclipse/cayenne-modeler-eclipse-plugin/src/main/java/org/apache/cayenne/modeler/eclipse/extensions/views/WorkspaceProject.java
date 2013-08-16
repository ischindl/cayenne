package org.apache.cayenne.modeler.eclipse.extensions.views;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceProject {
	
	private String name;
	private File rootDir;
	
	private List<CayenneProjectFile> cayenneProjects;
	
	public WorkspaceProject(String name, File rootDir) {
		this.name = name;
		this.rootDir = rootDir;
	}
	
	public String getName() {
		return name;
	}
	
	public File getRootDir() {
		return rootDir;
	}
	
	public List<CayenneProjectFile> getCayenneProjects() {
		
		if (cayenneProjects != null) {
			return cayenneProjects;
		}
		
		cayenneProjects = new ArrayList<CayenneProjectFile>();
		List<File> cayenneProjectPaths = new ArrayList<File>();
		getCayenneProjects(rootDir, cayenneProjectPaths);
		
		for (File path : cayenneProjectPaths) {
			cayenneProjects.add(new CayenneProjectFile(path.getName(), path));
		}
		
		return cayenneProjects;
	}

	private void getCayenneProjects(File projectDir, List<File> results) {
		File dir = projectDir;
		
		if (dir != null) {
			FilenameFilter projectFilter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.matches("cayenne-.*xml");
				}
			};
			
			for (File file : dir.listFiles(projectFilter)) {
				results.add(file);
			}
			
			FileFilter folderFilter = new FileFilter() {
				
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
			
			for (File subdir : dir.listFiles(folderFilter)) {
				if (!"target".equals(subdir.getName())) {
					getCayenneProjects(subdir, results);
				}
			}
		}
	}
	
	
}
