/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors
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

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.objectstyle.cayenne.modeler.action.CreateAttributeAction;
import org.objectstyle.cayenne.modeler.action.CreateDataMapAction;
import org.objectstyle.cayenne.modeler.action.CreateDbEntityAction;
import org.objectstyle.cayenne.modeler.action.CreateDerivedDbEntityAction;
import org.objectstyle.cayenne.modeler.action.CreateDomainAction;
import org.objectstyle.cayenne.modeler.action.CreateNodeAction;
import org.objectstyle.cayenne.modeler.action.CreateObjEntityAction;
import org.objectstyle.cayenne.modeler.action.CreateProcedureAction;
import org.objectstyle.cayenne.modeler.action.CreateProcedureParameterAction;
import org.objectstyle.cayenne.modeler.action.CreateQueryAction;
import org.objectstyle.cayenne.modeler.action.CreateRelationshipAction;
import org.objectstyle.cayenne.modeler.action.DbEntitySyncAction;
import org.objectstyle.cayenne.modeler.action.GenerateClassesAction;
import org.objectstyle.cayenne.modeler.action.GenerateDBAction;
import org.objectstyle.cayenne.modeler.action.ImportDBAction;
import org.objectstyle.cayenne.modeler.action.ImportDataMapAction;
import org.objectstyle.cayenne.modeler.action.ImportEOModelAction;
import org.objectstyle.cayenne.modeler.action.ObjEntitySyncAction;
import org.objectstyle.cayenne.modeler.action.ProjectAction;
import org.objectstyle.cayenne.modeler.action.RemoveAction;
import org.objectstyle.cayenne.modeler.action.RevertAction;
import org.objectstyle.cayenne.modeler.action.SaveAction;
import org.objectstyle.cayenne.modeler.action.SaveAsAction;
import org.objectstyle.cayenne.modeler.action.ValidateAction;
import org.objectstyle.cayenne.modeler.util.CayenneController;

/**
 * A controller that activates/decativate registered actions depending on current
 * selection.
 * 
 * @author Andrus Adamchik
 */
public class ActionController extends CayenneController {

    static final Collection PROJECT_ACTIONS = Arrays.asList(new String[] {
            CreateDomainAction.getActionName(), ProjectAction.getActionName(),
            ValidateAction.getActionName(), SaveAsAction.getActionName()
    });

    static final Collection DOMAIN_ACTIONS = new HashSet(PROJECT_ACTIONS);
    static {
        DOMAIN_ACTIONS.addAll(Arrays.asList(new String[] {
                ImportDataMapAction.getActionName(), CreateDataMapAction.getActionName(),
                RemoveAction.getActionName(), CreateNodeAction.getActionName(),
                ImportDBAction.getActionName(), ImportEOModelAction.getActionName()
        }));
    }

    static final Collection DATA_MAP_ACTIONS = new HashSet(DOMAIN_ACTIONS);
    static {
        DATA_MAP_ACTIONS.addAll(Arrays.asList(new String[] {
                GenerateClassesAction.getActionName(),
                CreateObjEntityAction.getActionName(),
                CreateDbEntityAction.getActionName(),
                CreateDerivedDbEntityAction.getActionName(),
                CreateQueryAction.getActionName(), CreateProcedureAction.getActionName(),
                GenerateDBAction.getActionName()
        }));
    }

    static final Collection OBJ_ENTITY_ACTIONS = new HashSet(DATA_MAP_ACTIONS);
    static {
        OBJ_ENTITY_ACTIONS.addAll(Arrays.asList(new String[] {
                ObjEntitySyncAction.getActionName(),
                CreateAttributeAction.getActionName(),
                CreateRelationshipAction.getActionName()
        }));
    }

    static final Collection DB_ENTITY_ACTIONS = new HashSet(DATA_MAP_ACTIONS);
    static {
        DB_ENTITY_ACTIONS.addAll(Arrays.asList(new String[] {
                CreateAttributeAction.getActionName(),
                CreateRelationshipAction.getActionName(),
                DbEntitySyncAction.getActionName()
        }));
    }

    static final Collection PROCEDURE_ACTIONS = new HashSet(DATA_MAP_ACTIONS);
    static {
        PROCEDURE_ACTIONS.addAll(Arrays.asList(new String[] {
            CreateProcedureParameterAction.getActionName()
        }));
    }

    public ActionController(Application application) {
        super(application);
    }

    public Component getView() {
        throw new UnsupportedOperationException("ActionController is 'headless'");
    }

    /**
     * Updates actions state to reflect an open project.
     */
    public void projectOpened() {
        processActionsState(PROJECT_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove");
    }

    public void projectClosed() {
        processActionsState(Collections.EMPTY_SET);
        application.getAction(RemoveAction.getActionName()).setName("Remove");
    }

    /**
     * Updates actions state to reflect DataDomain selecttion.
     */
    public void domainSelected() {
        processActionsState(DOMAIN_ACTIONS);
    }

    public void dataNodeSelected() {
        processActionsState(DOMAIN_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove DataNode");
    }

    public void dataMapSelected() {
        processActionsState(DATA_MAP_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove DataMap");
        // reset
        // getAction(CreateAttributeAction.getActionName()).setName("Create Attribute");
    }

    public void objEntitySelected() {
        processActionsState(OBJ_ENTITY_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove ObjEntity");
    }

    public void dbEntitySelected() {
        processActionsState(DB_ENTITY_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove DbEntity");
    }

    public void procedureSelected() {
        processActionsState(PROCEDURE_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove Procedure");
    }

    public void querySelected() {
        processActionsState(DATA_MAP_ACTIONS);
        application.getAction(RemoveAction.getActionName()).setName("Remove Query");
    }

    /**
     * Sets the state of all controlled actions, flipping it to "enabled" for all actions
     * in provided collection and to "disabled" for the rest.
     */
    protected void processActionsState(Collection namesOfEnabled) {

        // disable everything we can
        Object[] keys = application.getActionMap().allKeys();

        for (int i = 0; i < keys.length; i++) {

            // these two buttons are exceptions...
            if (keys[i].equals(SaveAction.getActionName())
                    || keys[i].equals(RevertAction.getActionName())) {
                continue;
            }

            application.getAction((String) keys[i]).setEnabled(
                    namesOfEnabled.contains(keys[i]));
        }
    }
}