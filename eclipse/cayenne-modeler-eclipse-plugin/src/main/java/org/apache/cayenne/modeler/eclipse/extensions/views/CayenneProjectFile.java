package org.apache.cayenne.modeler.eclipse.extensions.views;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CayenneProjectFile {
	
	private static Log logger = LogFactory.getLog(CayenneProjectFile.class);
	
	private File path;
	private String name;
	
	private List<CayenneDatamapFile> datamaps;
	private List<String> datamapNames;
	
	public CayenneProjectFile(String name, File path) {
		this.name = name;
		this.path = path;
		this.datamapNames = getDatamapNames();
	}
	
	public String getName() {
		return name;
	}
	
	public File getPath() {
		return path;
	}
	
	public List<CayenneDatamapFile> getDatamaps() {
		if (datamaps != null) {
			return datamaps;
		}
		
		datamaps = new ArrayList<CayenneDatamapFile>();
		List<File> datamapFiles = new ArrayList<File>();
		getDatamapFiles(path.getParentFile(), datamapFiles);
		
		for (File file : datamapFiles) {
			datamaps.add(new CayenneDatamapFile(file.getName(), file, this));
		}
		
		return datamaps;
	}
	
	protected void getDatamapFiles(File rootDir, List<File> results) {
		File dir = rootDir;
		
		if (dir != null) {
			FilenameFilter datamapFilter = new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
					return name.endsWith(".map.xml") && datamapNames.contains(name.substring(0, name.length() - 8));
				}
			};
			
			FileFilter folderFilter = new FileFilter() {
				
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
			
			for (File file : dir.listFiles(datamapFilter)) {
				results.add(file);
			}
			
			// searching in one more directory level down
			for (File subdir : dir.listFiles(folderFilter)) {
				for (File file : subdir.listFiles(datamapFilter)) {
					results.add(file);
				}
			}
		}
	}
	
	protected List<String> getDatamapNames() {
		List<String> datamapNames = new ArrayList<String>();
		
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(path);

			NodeList maps = doc.getElementsByTagName("map");

			for (int i = 0; i < maps.getLength(); i++) {
				datamapNames.add(
						maps.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
		} catch (Exception e) {
			logger.error(name + ": error searching for datamaps.");
		}
		
		return datamapNames;
	}

}
