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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import org.apache.cayenne.swing.BoundsBinding;
import org.objectstyle.cayenne.modeler.util.CayenneController;
import org.objectstyle.cayenne.swing.ObjectBinding;

public class FrameController extends CayenneController {

    protected JFrame frame;
    protected FramePlugin framePlugin;
    protected ObjectBinding boundsBinding;
    protected int newFrameOffset;

    public FrameController(FramePlugin framePlugin) {
        super((CayenneController) null);

        this.framePlugin = framePlugin;
        this.frame = new JFrame();
        this.newFrameOffset = 30;
    }

    public Component getView() {
        return frame;
    }

    public FramePlugin getFramePlugin() {
        return framePlugin;
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns an action that calls 'shutdownAction' on this controller.
     */
    public Action getDefaultShutdownAction() {
        return new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        };
    }

    public Action getShutdownAction() {
        Action action = framePlugin.getActionMap().get(FramePlugin.QUIT_ACTION);
        return action != null ? action : getDefaultShutdownAction();
    }

    /**
     * Starts the application frame.
     */
    public void startup() {

        if (frame.getTitle() == null || frame.getTitle().trim().length() == 0) {
            frame.setTitle(framePlugin.getPlugin().getName());
        }

        // make closeable frame
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                getShutdownAction().actionPerformed(new ActionEvent(this, 1, null));
            }
        });

        // setup preferred bounds offset...
        if (newFrameOffset > 0) {
            Preferences prefs = getPreferences();

            int xOffset = (int) (Math.random() * newFrameOffset);
            prefs.putInt("x", prefs.getInt("x", 0) + xOffset);

            int yOffset = (int) (Math.random() * newFrameOffset);
            prefs.putInt("y", prefs.getInt("y", 0) + yOffset);
        }

        boundsBinding = new BoundsBinding(frame, "bounds");
        boundsBinding.setContext(this);
        boundsBinding.updateView();
        frame.setVisible(true);
    }

    /**
     * Shuts down the application frame.
     */
    public void shutdown() {
        if (this.frame != null) {
            this.frame.dispose();
        }

        System.exit(0);
    }

    public Rectangle getBounds() {
        Preferences prefs = getPreferences();

        int x = prefs.getInt("x", 0);
        int y = prefs.getInt("y", 0);
        int w = prefs.getInt("w", 600);
        int h = prefs.getInt("h", 400);

        // TODO: andrus, 6/5/2006 - validate bounds

        return new Rectangle(x, y, w, h);
    }

    public void setBounds(Rectangle bounds) {
        Preferences prefs = getPreferences();

        prefs.putInt("w", frame.getWidth());
        prefs.putInt("h", frame.getHeight());
        prefs.putInt("x", frame.getX());
        prefs.putInt("y", frame.getY());
    }

    protected Preferences getPreferences() {
        return Preferences.userNodeForPackage(getClass()).node(frame.getTitle());
    }

    public int getNewFrameOffset() {
        return newFrameOffset;
    }

    public void setNewFrameOffset(int newFrameOffset) {
        this.newFrameOffset = newFrameOffset;
    }
}
