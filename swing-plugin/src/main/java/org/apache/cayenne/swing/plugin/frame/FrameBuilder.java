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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.cayenne.swing.CayenneSwingException;
import org.objectstyle.cayenne.util.Util;
import org.platonos.pluginengine.Plugin;
import org.platonos.pluginengine.logging.LoggerLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FrameBuilder {

    protected FramePlugin framePlugin;

    public FrameBuilder(FramePlugin framePlugin) {
        this.framePlugin = framePlugin;
    }

    public void addActions(Plugin plugin, String xmlPath) {
        InputStream actionXML = plugin
                .getPluginClassLoader()
                .getResourceAsStream(xmlPath);
        if (actionXML == null) {
            throw new CayenneSwingException("No action XML file found at " + xmlPath);
        }

        Document doc;
        try {
            doc = XMLUtil.newBuilder().parse(actionXML);
        }
        catch (Exception e) {
            throw new CayenneSwingException(
                    "Error parsing action XML '" + xmlPath + "'",
                    e);
        }

        List children = XMLUtil.getChildren(doc.getDocumentElement(), "action");
        Iterator it = children.iterator();
        while (it.hasNext()) {
            addAction(plugin, (Element) it.next());
        }
    }

    /**
     * Adds an action configuring it from plugin properties.
     */
    protected void addAction(Plugin plugin, Element actionXML) {

        String key = actionXML.getAttribute("name");
        if (key == null) {
            throw new CayenneSwingException("No 'name' attribute in action XML");
        }

        String actionClassName = actionXML.getAttribute("class");
        if (actionClassName == null) {
            throw new CayenneSwingException("No 'class' attribute in action XML");
        }

        Action action;
        try {
            action = (Action) Class.forName(
                    actionClassName,
                    true,
                    plugin.getPluginClassLoader()).newInstance();
        }
        catch (Exception e) {
            framePlugin.getPluginEngine().getLogger().log(
                    LoggerLevel.WARNING,
                    "Error instantiating action '" + actionClassName + "'",
                    e);

            // create noop disabled action
            action = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                }
            };
            action.setEnabled(false);
        }

        List accelerators = XMLUtil.getChildren(actionXML, "accelerator");
        if (!accelerators.isEmpty()) {
            KeyStroke accelerator = processAcceleratorKey((Element) accelerators.get(0));
            action.putValue(Action.ACCELERATOR_KEY, accelerator);
        }

        action.putValue(Action.NAME, plugin.replaceToken("$$"
                + key
                + '.'
                + Action.NAME
                + "$$"));
        action.putValue(Action.SHORT_DESCRIPTION, plugin.replaceToken("$$"
                + key
                + '.'
                + Action.SHORT_DESCRIPTION
                + "$$"));

        action.putValue(Action.LONG_DESCRIPTION, plugin.replaceToken("$$"
                + key
                + '.'
                + Action.LONG_DESCRIPTION
                + "$$"));

        framePlugin.getActionMap().put(key, action);
    }

    protected KeyStroke processAcceleratorKey(Element acceleratorXML) {

        // extract key code...
        String key = XMLUtil.getText(acceleratorXML);
        if (Util.isEmptyString(key)) {
            throw new CayenneSwingException("No key stroke text in accelerator XML");
        }

        KeyStroke ks = KeyStroke.getKeyStroke(key);
        boolean menu = "true".equalsIgnoreCase(acceleratorXML.getAttribute("menu"));

        if (menu) {
            ks = KeyStroke.getKeyStroke(ks.getKeyCode(), ks.getModifiers()
                    | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), ks
                    .isOnKeyRelease());
        }

        return ks;
    }

    /**
     * Adds a menu contributed by plugin.
     */
    public void addMenus(Plugin plugin, String xmlPath) {
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
            JComponent child = processMenu(plugin, (Element) it.next(), 1);
            if (child != null) {
                menu.add(child);
            }
        }
    }

    /**
     * Recursively loads menus from the DOM tree.
     */
    protected JComponent processMenu(Plugin plugin, Element menuXML, int depth) {

        if ("true".equalsIgnoreCase(menuXML.getAttribute("separator"))) {
            return null;
        }

        List children = XMLUtil.getChildren(menuXML, "menu");

        Action action = framePlugin.getActionMap().get(menuXML.getAttribute("action"));
        String key = menuXML.getAttribute("name");

        JMenuItem menu = (children.isEmpty() && depth > 1)
                ? new JMenuItem()
                : new JMenu();

        if (action != null) {
            menu.setAction(action);
        }
        else if (key != null) {
            menu.setText(plugin.replaceToken("$$" + key + "$$"));
        }

        depth++;
        Iterator it = children.iterator();
        while (it.hasNext()) {
            JComponent component = processMenu(plugin, (Element) it.next(), depth);

            if (component != null) {
                menu.add(component);
            }
            else if (menu instanceof JMenu) {
                ((JMenu) menu).addSeparator();
            }
        }

        // disable empty menus
        if (children.isEmpty() && action == null) {
            menu.setEnabled(false);
        }

        return menu;
    }
}
