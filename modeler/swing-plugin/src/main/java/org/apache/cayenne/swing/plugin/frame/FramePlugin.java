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

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.platonos.pluginengine.Extension;
import org.platonos.pluginengine.PluginLifecycle;

/**
 * A plugin that starts an empty closeable frame intended for customization by extensions.
 * 
 * @author Andrus Adamchik
 */
public class FramePlugin extends PluginLifecycle {

    public static final String QUIT_ACTION = "action.quit";

    public static final String FRAME_BUILDERS_EXT = "frameBuilders";

    /**
     * A system property that defines application locale. If not set default system locae
     * is used. This is used mostly for localization debugging.
     */
    public static final String LOCALE_PROPERTY = "cayenne.ui.locale";

    protected FrameBuilder frameBuilder;
    protected FrameController frameController;

    /**
     * Changes default locale if {@link #LOCALE_PROPERTY} is set.
     */
    protected void initialize() {
        // change default locale
        String locale = System.getProperty(LOCALE_PROPERTY);
        if (locale != null) {

            StringTokenizer toks = new StringTokenizer(locale, "_");

            if (toks.hasMoreTokens()) {
                // note that default local parts must be empty strings, not nulls...
                String language = "";
                String country = "";
                String variant = "";

                language = toks.nextToken();

                if (toks.hasMoreTokens()) {
                    country = toks.nextToken();

                    if (toks.hasMoreTokens()) {
                        variant = toks.nextToken();
                    }
                }

                Locale.setDefault(new Locale(language, country, variant));
            }
        }
    }

    protected void start() {

        frameBuilder = new FrameBuilder(this);
        frameController = new FrameController(this);

        List extensions = getExtensionPoint(FRAME_BUILDERS_EXT).getExtensions();

        initActions(extensions);
        initMenus(extensions);
        initToolbars(extensions);
        initFrame(extensions);

        frameController.startup();
    }

    protected void initActions(List extensions) {

        // init default actions that can be later overriden by plugins
        frameBuilder.addActions(this.getPlugin(), "actions.xml");

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initActions(frameBuilder);
        }
    }

    protected void initMenus(List extensions) {

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initMenus(frameBuilder);
        }
    }

    protected void initToolbars(List extensions) {

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initToolbars(frameBuilder);
        }
    }

    protected void initFrame(List extensions) {

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initFrame(this);
        }
    }

    public FrameController getFrameController() {
        return frameController;
    }

    public void setFrameController(FrameController frameController) {
        this.frameController = frameController;
    }

    public FrameBuilder getFrameBuilder() {
        return frameBuilder;
    }
}
