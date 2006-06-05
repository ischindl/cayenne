package org.apache.cayenne.swing.plugin.frame;

import java.util.Iterator;
import java.util.List;

import javax.swing.ActionMap;

import org.platonos.pluginengine.Extension;
import org.platonos.pluginengine.PluginLifecycle;

/**
 * A plugin that starts an empty closeable frame intended for customization by extension.
 * 
 * @author Andrus Adamchik
 */
public class FramePlugin extends PluginLifecycle {

    public static final String QUIT_ACTION = "frame.action.quit";

    public static final String FRAME_BUILDERS_EXT = "frameBuilders";

    protected FrameController frameController;
    protected ActionMap actionMap;

    protected void start() {

        frameController = new FrameController(this);
        actionMap = new ActionMap();

        List extensions = getExtensionPoint(FRAME_BUILDERS_EXT).getExtensions();

        initActions(extensions);
        initMenus(extensions);
        initFrame(extensions);

        frameController.startup();
    }

    protected void initActions(List extensions) {

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initActions(this);
        }

        // add default close action
        if (actionMap.get(QUIT_ACTION) == null) {
            actionMap.put(QUIT_ACTION, frameController.getDefaultShutdownAction());
        }
    }

    protected void initMenus(List extensions) {

        Iterator it = extensions.iterator();
        while (it.hasNext()) {
            Extension extension = (Extension) it.next();
            FrameBuilderExtension ext = (FrameBuilderExtension) extension
                    .getExtensionInstance();

            ext.initMenus(this);
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

    public ActionMap getActionMap() {
        return actionMap;
    }

    public void setActionMap(ActionMap actionMap) {
        this.actionMap = actionMap;
    }

    public FrameController getFrameController() {
        return frameController;
    }

    public void setFrameController(FrameController frameController) {
        this.frameController = frameController;
    }
}
