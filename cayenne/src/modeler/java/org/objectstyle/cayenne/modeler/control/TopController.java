/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.cayenne.modeler.control;

import org.objectstyle.cayenne.modeler.Editor;
import org.objectstyle.cayenne.modeler.model.TopModel;
import org.objectstyle.cayenne.modeler.view.StatusBarView;
import org.objectstyle.cayenne.project.Project;
import org.scopemvc.controller.basic.BasicController;
import org.scopemvc.core.Control;
import org.scopemvc.core.ControlException;

/**
 * TopController is the main controller object of the CayenneModeler.
 *  
 * @author Andrei Adamchik
 */
public class TopController extends ModelerController {
    protected StatusBarController statusController;
    protected EventController eventController;
    protected ActionController actionController;

    // should refactor to SPanel
    protected Editor view;

    /**
     * Constructor for TopController.
     */
    public TopController(Editor view) {
        this.view = view;
        setModel(new TopModel());

        statusController = new StatusBarController(this);
        eventController = new EventController(this);
        actionController = new ActionController(this);
    }

    /**
     * Action method invoked on project closing.
     */
    protected void projectClosed(Control control) {
        // --- update view
        view.getRecentFileMenu().rebuildFromPreferences();
        if (view.getView() != null) {
            view.getContentPane().remove(view.getView());
            view.setView(null);
        }
        // repaint is needed, since sometimes there is a 
        // trace from menu left on the screen
        view.repaint();
        view.updateTitle();

        // --- update model
        getTopModel().setCurrentProject(null);

        // --- propagate event to child controllers
        control.markUnmatched();
        eventController.handleControl(control);

        control.markUnmatched();
        actionController.handleControl(control);

        control.markUnmatched();
        statusController.handleControl(control);
    }

    public void projectOpened(Project project) {
        // update model
        getTopModel().setCurrentProject(project);

        // update main view
        view.projectOpened();

        // update bottom status bar
        if (project.isLocationUndefined()) {
            eventController.setDirty(true);
            statusController.handleControl(
                new Control(TopModel.STATUS_MESSAGE_KEY, "New project created..."));
        } else {
            statusController.handleControl(
                new Control(TopModel.STATUS_MESSAGE_KEY, "Project opened..."));
        }
    }

    public void setStatusBarView(StatusBarView view) {
        statusController.setView(view);
    }

    protected void doHandleControl(Control control) throws ControlException {
        // pass control to the child that knows how to handle it
        if (control.matchesID(TopModel.STATUS_MESSAGE_KEY)) {
            control.markUnmatched();
            statusController.doHandleControl(control);
        } else if (control.matchesID(PROJECT_CLOSED_ID)) {
            projectClosed(control);
        }
    }

    public EventController getEventController() {
        return eventController;
    }

    /**
     * Returns the child action controller.
     */
    public ActionController getActionController() {
        return actionController;
    }
}
