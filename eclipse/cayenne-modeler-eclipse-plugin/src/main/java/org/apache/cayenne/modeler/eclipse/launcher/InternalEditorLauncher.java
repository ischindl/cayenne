package org.apache.cayenne.modeler.eclipse.launcher;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginMain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InternalEditorLauncher implements IEditorLauncher {
	private static Log logger = LogFactory.getLog(InternalEditorLauncher.class);

	public void open(IPath filePath) {
		File datamap = filePath.toFile();

		File project = getParentProject(datamap);
		if (project != null) {
			String[] args = new String[] { project.toString() };
			PluginMain plugin = new PluginMain(args);
			plugin.start();
		}
	}

	protected File getParentProject(File datamap) {

		File dir = datamap.getParentFile();
		String datamapName = datamap.getName().replace(".map.xml", "");

		// need to search on 2 directory levels: file directory and directory up
		for (int i = 0; i < 2; i++) {
			if (dir != null) {
				FilenameFilter filter = new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return name.matches("cayenne-.*xml");
					}
				};

				File[] files = dir.listFiles(filter);

				for (File file : files) {
					if (containsDataMap(file, datamapName)) {
						return file;
					}
				}
				
				dir = dir.getParentFile();
			}
		}

		return null;
	}

	protected boolean containsDataMap(File projectFile, String datamapName) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(projectFile);

			NodeList maps = doc.getElementsByTagName("map");

			for (int i = 0; i < maps.getLength(); i++) {
				Node map = maps.item(i);
				if (datamapName.equals(
						map.getAttributes().getNamedItem("name").getNodeValue())) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Error searching for parent project.");
		}

		return false;
	}

}
