package org.apache.cayenne.swing.plugin.frame;


public interface FrameBuilderExtension {

    void initActions(FramePlugin plugin);

    void initMenus(FramePlugin plugin);

    void initFrame(FramePlugin plugin);
}
