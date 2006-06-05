/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.cayenne.swing.plugin.frame;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.cayenne.swing.CayenneSwingException;
import org.platonos.pluginengine.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FrameMenuBuilder {

    protected FramePlugin framePlugin;

    public FrameMenuBuilder(FramePlugin framePlugin) {
        this.framePlugin = framePlugin;
    }

    /**
     * Adds a menu contributed by plugin.
     */
    public void addMenu(Plugin plugin, String xmlPath) {
        JFrame frame = framePlugin.getFrameController().getFrame();

        JMenuBar menu = frame.getJMenuBar();
        if (menu == null) {
            menu = new JMenuBar();
            frame.setJMenuBar(menu);
        }

        InputStream menuXML = plugin.getPluginClassLoader().getResourceAsStream(xmlPath);
        if (menuXML == null) {
            throw new CayenneSwingException("No menu XML file found at " + xmlPath);
        }

        Document doc;
        try {
            doc = XMLUtil.newBuilder().parse(menuXML);
        }
        catch (Exception e) {
            throw new CayenneSwingException("Error parsing menu XML '" + xmlPath + "'", e);
        }

        List children = XMLUtil.getChildren(doc.getDocumentElement(), "menu");
        Iterator it = children.iterator();
        while (it.hasNext()) {
            menu.add(processMenu((Element) it.next()));
        }

    }

    /**
     * Recursively loads menus from the DOM tree.
     */
    protected JMenuItem processMenu(Element menuXML) {

        List children = XMLUtil.getChildren(menuXML, "menu");

        Action action = framePlugin.getActionMap().get(menuXML.getAttribute("action"));
        String key = menuXML.getAttribute("name");

        JMenuItem menu = (children.isEmpty()) ? new JMenuItem() : new JMenu();

        if (action != null) {
            menu.setAction(action);
        }
        else if (key != null) {
            menu.setText(key);
        }

        Iterator it = children.iterator();
        while (it.hasNext()) {
            menu.add(processMenu((Element) it.next()));
        }

        return menu;
    }
}
