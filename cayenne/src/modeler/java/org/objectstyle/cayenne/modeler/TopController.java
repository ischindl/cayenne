/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2004, Andrei (Andrus) Adamchik and individual authors
 * of the software. All rights reserved.
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
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowlegement:
 *    "This product includes software developed by independent contributors
 *    and hosted on ObjectStyle Group web site (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, email
 *    "andrus at objectstyle dot org".
 * 
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    or "Cayenne", nor may "ObjectStyle" or "Cayenne" appear in their
 *    names without prior written permission.
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
 * individuals and hosted on ObjectStyle Group web site.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 */
package org.objectstyle.cayenne.modeler;

import java.awt.BorderLayout;

import org.objectstyle.cayenne.modeler.dialog.validator.ValidatorDialog;
import org.objectstyle.cayenne.modeler.editor.EditorView;
import org.objectstyle.cayenne.modeler.util.RecentFileMenu;
import org.objectstyle.cayenne.project.Project;
import org.objectstyle.cayenne.project.validator.Validator;
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
    protected CayenneModelerFrame mainFrame;

    /**
     * Constructor for TopController.
     */
    public TopController(CayenneModelerFrame view) {
        this.mainFrame = view;
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
        RecentFileMenu recentFileMenu = mainFrame.getRecentFileMenu();
        recentFileMenu.rebuildFromPreferences();
        recentFileMenu.setEnabled(recentFileMenu.getMenuComponentCount() > 0);
        
        if (mainFrame.getView() != null) {
            mainFrame.getContentPane().remove(mainFrame.getView());
            mainFrame.setView(null);
        }
        
        // repaint is needed, since sometimes there is a 
        // trace from menu left on the screen
        mainFrame.repaint();
        mainFrame.updateTitle();

        // --- update model
        getTopModel().setCurrentProject(null);

        // --- propagate control to child controllers
        control.markUnmatched();
        eventController.handleControl(control);

        control.markUnmatched();
        actionController.handleControl(control);

        control.markUnmatched();
        statusController.handleControl(control);
    }

    /**
     * Handles project opening control. Updates main frame, then delegates
     * control to child controllers.
     */
    protected void projectOpened(Control control) {
        // sanity check 
        if (!(control.getParameter() instanceof Project)) {
            return;
        }

        Project project = (Project) control.getParameter();

        // update model
        getTopModel().setCurrentProject(project);

        // update main view
        mainFrame.setView(new EditorView(eventController));
        mainFrame.getContentPane().add(
            mainFrame.getView(),
            BorderLayout.CENTER);
        mainFrame.validate();
        mainFrame.updateTitle();

        // --- propagate control to child controllers
        control.markUnmatched();
        eventController.handleControl(control);

        control.markUnmatched();
        actionController.handleControl(control);

        control.markUnmatched();
        statusController.handleControl(control);

        // --- check for load errors
        if (project.getLoadStatus().hasFailures()) {
            // mark project as unsaved
            project.setModified(true);
            eventController.setDirty(true);

            // show warning dialog
            ValidatorDialog.showDialog(
                mainFrame,
                eventController,
                new Validator(project, project.getLoadStatus()));
        }
    }

    public void setStatusBarView(StatusBarView view) {
        statusController.setView(view);
    }

    protected void doHandleControl(Control control) throws ControlException {
        if (control.matchesID(PROJECT_OPENED_ID)) {
            projectOpened(control);
        } else if (control.matchesID(PROJECT_CLOSED_ID)) {
            projectClosed(control);
        } else if (control.matchesID(DATA_DOMAIN_SELECTED_ID)) {
            control.markUnmatched();
            actionController.handleControl(control);
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
