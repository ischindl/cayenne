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
package org.apache.cayenne.modeler.plugin;

import org.apache.cayenne.swing.plugin.frame.FrameBuilder;
import org.apache.cayenne.swing.plugin.frame.FrameBuilderExtension;
import org.apache.cayenne.swing.plugin.frame.FramePlugin;
import org.platonos.pluginengine.Plugin;
import org.platonos.pluginengine.PluginEngine;

/**
 * An extension that turns a generic frame provided by swing-plugin to a CayenneModeler
 * frame.
 * 
 * @author Andrus Adamchik
 */
public class ModelerFrameBuilderExtension implements FrameBuilderExtension {

    protected Plugin plugin;

    public ModelerFrameBuilderExtension() {
        this.plugin = PluginEngine.getPlugin(ModelerPlugin.class);
    }

    public void initFrame(FramePlugin plugin) {
        plugin.getFrameController().getFrame().setTitle(
                this.plugin.replaceToken("$$frame.title$$"));
    }

    public void initActions(FrameBuilder builder) {
        builder.addActions(this.plugin, "actions.xml");
    }

    public void initMenus(FrameBuilder builder) {
        builder.addMenus(this.plugin, "menus.xml");
    }

    public void initToolbars(FrameBuilder builder) {
        builder.addToolbars(this.plugin, "toolbars.xml");
    }
}
