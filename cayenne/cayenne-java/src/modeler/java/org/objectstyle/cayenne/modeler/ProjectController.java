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
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.map.DataMap;
import org.objectstyle.cayenne.map.DbAttribute;
import org.objectstyle.cayenne.map.DbEntity;
import org.objectstyle.cayenne.map.DbRelationship;
import org.objectstyle.cayenne.map.ObjAttribute;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.map.ObjRelationship;
import org.objectstyle.cayenne.map.Procedure;
import org.objectstyle.cayenne.map.ProcedureParameter;
import org.objectstyle.cayenne.map.event.AttributeEvent;
import org.objectstyle.cayenne.map.event.DataMapEvent;
import org.objectstyle.cayenne.map.event.DataMapListener;
import org.objectstyle.cayenne.map.event.DataNodeEvent;
import org.objectstyle.cayenne.map.event.DataNodeListener;
import org.objectstyle.cayenne.map.event.DbAttributeListener;
import org.objectstyle.cayenne.map.event.DbEntityListener;
import org.objectstyle.cayenne.map.event.DbRelationshipListener;
import org.objectstyle.cayenne.map.event.DomainEvent;
import org.objectstyle.cayenne.map.event.DomainListener;
import org.objectstyle.cayenne.map.event.EntityEvent;
import org.objectstyle.cayenne.map.event.ObjAttributeListener;
import org.objectstyle.cayenne.map.event.ObjEntityListener;
import org.objectstyle.cayenne.map.event.ObjRelationshipListener;
import org.objectstyle.cayenne.map.event.ProcedureEvent;
import org.objectstyle.cayenne.map.event.ProcedureListener;
import org.objectstyle.cayenne.map.event.ProcedureParameterEvent;
import org.objectstyle.cayenne.map.event.ProcedureParameterListener;
import org.objectstyle.cayenne.map.event.QueryEvent;
import org.objectstyle.cayenne.map.event.QueryListener;
import org.objectstyle.cayenne.map.event.RelationshipEvent;
import org.objectstyle.cayenne.modeler.action.NavigateBackwardAction;
import org.objectstyle.cayenne.modeler.action.NavigateForwardAction;
import org.objectstyle.cayenne.modeler.action.RevertAction;
import org.objectstyle.cayenne.modeler.action.SaveAction;
import org.objectstyle.cayenne.modeler.event.AttributeDisplayEvent;
import org.objectstyle.cayenne.modeler.event.DataMapDisplayEvent;
import org.objectstyle.cayenne.modeler.event.DataMapDisplayListener;
import org.objectstyle.cayenne.modeler.event.DataNodeDisplayEvent;
import org.objectstyle.cayenne.modeler.event.DataNodeDisplayListener;
import org.objectstyle.cayenne.modeler.event.DbAttributeDisplayListener;
import org.objectstyle.cayenne.modeler.event.DbEntityDisplayListener;
import org.objectstyle.cayenne.modeler.event.DbRelationshipDisplayListener;
import org.objectstyle.cayenne.modeler.event.DisplayEvent;
import org.objectstyle.cayenne.modeler.event.DomainDisplayEvent;
import org.objectstyle.cayenne.modeler.event.DomainDisplayListener;
import org.objectstyle.cayenne.modeler.event.EntityDisplayEvent;
import org.objectstyle.cayenne.modeler.event.ObjAttributeDisplayListener;
import org.objectstyle.cayenne.modeler.event.ObjEntityDisplayListener;
import org.objectstyle.cayenne.modeler.event.ObjRelationshipDisplayListener;
import org.objectstyle.cayenne.modeler.event.ProcedureDisplayEvent;
import org.objectstyle.cayenne.modeler.event.ProcedureDisplayListener;
import org.objectstyle.cayenne.modeler.event.ProcedureParameterDisplayEvent;
import org.objectstyle.cayenne.modeler.event.ProcedureParameterDisplayListener;
import org.objectstyle.cayenne.modeler.event.QueryDisplayEvent;
import org.objectstyle.cayenne.modeler.event.QueryDisplayListener;
import org.objectstyle.cayenne.modeler.event.RelationshipDisplayEvent;
import org.objectstyle.cayenne.modeler.pref.DataMapDefaults;
import org.objectstyle.cayenne.modeler.pref.DataNodeDefaults;
import org.objectstyle.cayenne.modeler.util.CayenneController;
import org.objectstyle.cayenne.modeler.util.CircularArray;
import org.objectstyle.cayenne.pref.Domain;
import org.objectstyle.cayenne.project.Project;
import org.objectstyle.cayenne.query.Query;
import org.objectstyle.cayenne.util.IDUtil;

/**
 * A controller that works with the project tree, tracking selection and dispatching
 * project events.
 * <p>
 * TODO: Refactor the event model, so that events are generic and contain "path" to a
 * project node in question. After this is done, EventController should no longer maintain
 * the selection model (currentXYZ ivars), rather it should update internal model.
 * </p>
 */
public class ProjectController extends CayenneController {

    /*
     * A snapshot of the current state of the project controller. This was added so that
     * we could support history of recent objects.
     */
    public class ControllerState {

        private boolean isRefiring;
        private DisplayEvent event;
        private DataDomain domain;
        private DataNode node;
        private DataMap map;
        private ObjEntity objEntity;
        private DbEntity dbEntity;
        private ObjAttribute objAttr;
        private DbAttribute dbAttr;
        private ObjRelationship objRel;
        private DbRelationship dbRel;
        private Procedure procedure;
        private ProcedureParameter procedureParameter;
        private Query query;

        public ControllerState() {
            domain = null;
            node = null;
            map = null;

            objEntity = null;
            objAttr = null;
            objRel = null;

            dbEntity = null;
            dbAttr = null;
            dbRel = null;

            procedure = null;
            procedureParameter = null;

            query = null;

            event = null;
            isRefiring = false;
        }

        /*
         * Used to determine if the val ControllerState is equivalent, which means if the
         * event is refired again, will it end up in the same place on the screen. This
         * get's a bit messy at the end, because of inheritance heirarchy issues.
         */
        public boolean isEquivalent(ControllerState val) {
            if (val == null)
                return false;

            if (event instanceof EntityDisplayEvent
                    && val.event instanceof EntityDisplayEvent) {
                if (((EntityDisplayEvent) val.event).getEntity() instanceof ObjEntity) {
                    return objEntity == val.objEntity;
                }
                else {
                    return dbEntity == val.dbEntity;
                }
            }
            else if (event instanceof ProcedureDisplayEvent
                    && val.event instanceof ProcedureDisplayEvent) {
                return procedure == val.procedure;
            }
            else if (event instanceof QueryDisplayEvent
                    && val.event instanceof QueryDisplayEvent) {
                return query == val.query;
            }
            else if (event.getClass() == DataMapDisplayEvent.class
                    && event.getClass() == val.event.getClass()) {
                return map == val.map;
            }
            else if (event.getClass() == DataNodeDisplayEvent.class
                    && event.getClass() == val.event.getClass()) {
                return node == val.node;
            }
            else if (event.getClass() == DomainDisplayEvent.class
                    && event.getClass() == val.event.getClass()) {
                return domain == val.domain;
            }
            return false;
        }
    }

    protected EventListenerList listenerList;
    protected boolean dirty;

    protected Project project;
    protected Domain projectPreferences;

    protected ControllerState currentState;
    protected CircularArray controllerStateHistory;
    protected int maxHistorySize = 20;

    public ProjectController(CayenneModelerController parent) {
        super(parent);
        this.listenerList = new EventListenerList();
        controllerStateHistory = new CircularArray(maxHistorySize);
        currentState = new ControllerState();
    }

    public Component getView() {
        return parent.getView();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project currentProject) {
        this.project = currentProject;
        this.projectPreferences = null;
    }

    /**
     * Returns top preferences Domain for the application.
     */
    public Domain getApplicationPreferenceDomain() {
        return getApplication().getPreferenceDomain();
    }

    /**
     * Returns top preferences Domain for the current project, throwing an exception if no
     * project is selected.
     */
    public Domain getPreferenceDomainForProject() {
        Project project = getProject();
        if (project == null) {
            throw new CayenneRuntimeException("No Project selected");
        }

        if (projectPreferences == null) {
            String key = project.isLocationUndefined() ? new String(IDUtil
                    .pseudoUniqueByteSequence16()) : project
                    .getMainFile()
                    .getAbsolutePath();

            projectPreferences = getApplicationPreferenceDomain().getSubdomain(
                    Project.class).getSubdomain(key);
        }

        return projectPreferences;
    }

    /**
     * Returns top preferences Domain for the current project, throwing an exception if no
     * project is selected.
     */
    public Domain getPreferenceDomainForDataDomain() {
        DataDomain dataDomain = getCurrentDataDomain();
        if (dataDomain == null) {
            throw new CayenneRuntimeException("No DataDomain selected");
        }

        return getPreferenceDomainForProject()
                .getSubdomain(DataDomain.class)
                .getSubdomain(dataDomain.getName());
    }

    /**
     * Returns preferences object for the current DataMap. If no preferences exist for the
     * current DataMap, a new Preferences object is created. If no DataMap is currently
     * selected, an exception is thrown.
     */
    public DataMapDefaults getDataMapPreferences() {
        DataMap map = getCurrentDataMap();
        if (map == null) {
            throw new CayenneRuntimeException("No DataMap selectd");
        }

        return (DataMapDefaults) getPreferenceDomainForDataDomain().getDetail(
                map.getName(),
                DataMapDefaults.class,
                true);
    }

    /**
     * Returns preferences object for the current DataMap, throwing an exception if no
     * DataMap is selected.
     */
    public DataNodeDefaults getDataNodePreferences() {
        DataNode node = getCurrentDataNode();
        if (node == null) {
            throw new CayenneRuntimeException("No DataNode selected");
        }

        return (DataNodeDefaults) getPreferenceDomainForDataDomain().getDetail(
                node.getName(),
                DataNodeDefaults.class,
                true);
    }

    public void projectOpened() {
        CayenneModelerFrame frame = (CayenneModelerFrame) getView();
        addDataNodeDisplayListener(frame);
        addDataMapDisplayListener(frame);
        addObjEntityDisplayListener(frame);
        addDbEntityDisplayListener(frame);
        addObjAttributeDisplayListener(frame);
        addDbAttributeDisplayListener(frame);
        addObjRelationshipDisplayListener(frame);
        addDbRelationshipDisplayListener(frame);
        addQueryDisplayListener(frame);
        addProcedureDisplayListener(frame);
        addProcedureParameterDisplayListener(frame);
    }

    public void reset() {
        clearState();
        setDirty(false);
        listenerList = new EventListenerList();
        controllerStateHistory.clear();
    }

    /*
     * Allow the user to change the default history size. TODO When a user changes their
     * preferences it should call this method. I don't know how the preferences work, so I
     * will leave this to someone else to do. Garry
     */
    public void setHistorySize(int newSize) {
        controllerStateHistory.resize(newSize);
    }

    public boolean isDirty() {
        return dirty;
    }

    /** Resets all current models to null. */
    private void clearState() {
        // don't clear if we are refiring events for history navigation
        if (currentState.isRefiring)
            return;

        currentState = new ControllerState();
    }

    private void saveState(DisplayEvent e) {
        if (!controllerStateHistory.contains(currentState)) {
            currentState.event = e;
            controllerStateHistory.add(currentState);
        }
    }

    protected void refreshNamespace() {
        DataDomain domain = getCurrentDataDomain();
        if (domain != null) {
            domain.getEntityResolver().clearCache();
        }
    }

    private void removeFromHistory(EventObject e) {
        int count = controllerStateHistory.size();
        ArrayList removeList = new ArrayList();

        for (int i = 0; i < count; i++) {
            ControllerState cs = (ControllerState) controllerStateHistory.get(i);

            EventObject csEvent = cs.event;

            if (csEvent == null) {
                continue;
            }

            if (e instanceof EntityEvent && csEvent instanceof EntityDisplayEvent) {
                if (((EntityEvent) e).getEntity() == ((EntityDisplayEvent) csEvent)
                        .getEntity()) {
                    removeList.add(cs);
                }
            }
            else if (e instanceof ProcedureEvent
                    && csEvent instanceof ProcedureDisplayEvent) {
                if (((ProcedureEvent) e).getProcedure() == ((ProcedureDisplayEvent) csEvent)
                        .getProcedure()) {
                    removeList.add(cs);
                }
            }
            else if (e instanceof QueryEvent && csEvent instanceof QueryDisplayEvent) {
                if (((QueryEvent) e).getQuery() == ((QueryDisplayEvent) csEvent)
                        .getQuery()) {
                    removeList.add(cs);
                }
            }
            else if (e instanceof DataMapEvent && csEvent instanceof DataMapDisplayEvent) {
                if (((DataMapEvent) e).getDataMap() == ((DataMapDisplayEvent) csEvent)
                        .getDataMap()) {
                    removeList.add(cs);
                }
            }
            else if (e instanceof DataNodeEvent
                    && csEvent instanceof DataNodeDisplayEvent) {
                if (((DataNodeEvent) e).getDataNode() == ((DataNodeDisplayEvent) csEvent)
                        .getDataNode()) {
                    removeList.add(cs);
                }
            }
            else if (e instanceof DomainEvent && csEvent instanceof DomainDisplayEvent) {
                if (((DomainEvent) e).getDomain() == ((DomainDisplayEvent) csEvent)
                        .getDomain()) {
                    removeList.add(cs);
                }
            }
        }
        Iterator it = removeList.iterator();
        while (it.hasNext()) {
            controllerStateHistory.remove(it.next());
        }
    }

    public DataNode getCurrentDataNode() {
        return currentState.node;
    }

    public DataDomain getCurrentDataDomain() {
        return currentState.domain;
    }

    public DataMap getCurrentDataMap() {
        return currentState.map;
    }

    public ObjEntity getCurrentObjEntity() {
        return currentState.objEntity;
    }

    public DbEntity getCurrentDbEntity() {
        return currentState.dbEntity;
    }

    public ObjAttribute getCurrentObjAttribute() {
        return currentState.objAttr;
    }

    public DbAttribute getCurrentDbAttribute() {
        return currentState.dbAttr;
    }

    public ObjRelationship getCurrentObjRelationship() {
        return currentState.objRel;
    }

    public DbRelationship getCurrentDbRelationship() {
        return currentState.dbRel;
    }

    public Query getCurrentQuery() {
        return currentState.query;
    }

    public Procedure getCurrentProcedure() {
        return currentState.procedure;
    }

    public ProcedureParameter getCurrentProcedureParameter() {
        return currentState.procedureParameter;
    }

    public void addDomainDisplayListener(DomainDisplayListener listener) {
        listenerList.add(DomainDisplayListener.class, listener);
    }

    public void addDomainListener(DomainListener listener) {
        listenerList.add(DomainListener.class, listener);
    }

    public void addDataNodeDisplayListener(DataNodeDisplayListener listener) {
        listenerList.add(DataNodeDisplayListener.class, listener);
    }

    public void addDataNodeListener(DataNodeListener listener) {
        listenerList.add(DataNodeListener.class, listener);
    }

    public void addDataMapDisplayListener(DataMapDisplayListener listener) {
        listenerList.add(DataMapDisplayListener.class, listener);
    }

    public void addDataMapListener(DataMapListener listener) {
        listenerList.add(DataMapListener.class, listener);
    }

    public void addDbEntityListener(DbEntityListener listener) {
        listenerList.add(DbEntityListener.class, listener);
    }

    public void addObjEntityListener(ObjEntityListener listener) {
        listenerList.add(ObjEntityListener.class, listener);
    }

    public void addDbEntityDisplayListener(DbEntityDisplayListener listener) {
        listenerList.add(DbEntityDisplayListener.class, listener);
    }

    public void addObjEntityDisplayListener(ObjEntityDisplayListener listener) {
        listenerList.add(ObjEntityDisplayListener.class, listener);
    }

    public void addDbAttributeListener(DbAttributeListener listener) {
        listenerList.add(DbAttributeListener.class, listener);
    }

    public void addDbAttributeDisplayListener(DbAttributeDisplayListener listener) {
        listenerList.add(DbAttributeDisplayListener.class, listener);
    }

    public void addObjAttributeListener(ObjAttributeListener listener) {
        listenerList.add(ObjAttributeListener.class, listener);
    }

    public void addObjAttributeDisplayListener(ObjAttributeDisplayListener listener) {
        listenerList.add(ObjAttributeDisplayListener.class, listener);
    }

    public void addDbRelationshipListener(DbRelationshipListener listener) {
        listenerList.add(DbRelationshipListener.class, listener);
    }

    public void addDbRelationshipDisplayListener(DbRelationshipDisplayListener listener) {
        listenerList.add(DbRelationshipDisplayListener.class, listener);
    }

    public void addObjRelationshipListener(ObjRelationshipListener listener) {
        listenerList.add(ObjRelationshipListener.class, listener);
    }

    public void addObjRelationshipDisplayListener(ObjRelationshipDisplayListener listener) {
        listenerList.add(ObjRelationshipDisplayListener.class, listener);
    }

    public void addQueryDisplayListener(QueryDisplayListener listener) {
        listenerList.add(QueryDisplayListener.class, listener);
    }

    public void addQueryListener(QueryListener listener) {
        listenerList.add(QueryListener.class, listener);
    }

    public void addProcedureDisplayListener(ProcedureDisplayListener listener) {
        listenerList.add(ProcedureDisplayListener.class, listener);
    }

    public void addProcedureListener(ProcedureListener listener) {
        listenerList.add(ProcedureListener.class, listener);
    }

    public void addProcedureParameterListener(ProcedureParameterListener listener) {
        listenerList.add(ProcedureParameterListener.class, listener);
    }

    public void addProcedureParameterDisplayListener(
            ProcedureParameterDisplayListener listener) {
        listenerList.add(ProcedureParameterDisplayListener.class, listener);
    }

    public void fireDomainDisplayEvent(DomainDisplayEvent e) {
        boolean changed = e.getDomain() != currentState.domain;
        if (!changed) {
            changed = currentState.node != null
                    || currentState.map != null
                    || currentState.dbEntity != null
                    || currentState.objEntity != null
                    || currentState.procedure != null
                    || currentState.query != null;
        }

        if (!e.isRefired()) {
            e.setDomainChanged(changed);
            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(DomainDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            DomainDisplayListener temp = (DomainDisplayListener) list[i];
            temp.currentDomainChanged(e);
        }

        getApplication().getActionManager().domainSelected();
    }

    /**
     * Informs all listeners of the DomainEvent. Does not send the event to its
     * originator.
     */
    public void fireDomainEvent(DomainEvent e) {
        setDirty(true);

        if (e.getId() == DomainEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(DomainListener.class);
        for (int i = 0; i < list.length; i++) {
            DomainListener temp = (DomainListener) list[i];
            switch (e.getId()) {
                case DomainEvent.ADD:
                    temp.domainAdded(e);
                    break;
                case DomainEvent.CHANGE:
                    temp.domainChanged(e);
                    break;
                case DomainEvent.REMOVE:
                    temp.domainRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid DomainEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireDataNodeDisplayEvent(DataNodeDisplayEvent e) {
        boolean changed = e.getDataNode() != currentState.node;

        if (!changed) {
            changed = currentState.map != null
                    || currentState.dbEntity != null
                    || currentState.objEntity != null
                    || currentState.procedure != null
                    || currentState.query != null;
        }

        if (!e.isRefired()) {
            e.setDataNodeChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.node = e.getDataNode();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(DataNodeDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ((DataNodeDisplayListener) list[i]).currentDataNodeChanged(e);
        }
    }

    /**
     * Informs all listeners of the DataNodeEvent. Does not send the event to its
     * originator.
     */
    public void fireDataNodeEvent(DataNodeEvent e) {
        setDirty(true);

        if (e.getId() == DataNodeEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(DataNodeListener.class);
        for (int i = 0; i < list.length; i++) {
            DataNodeListener temp = (DataNodeListener) list[i];
            switch (e.getId()) {
                case DataNodeEvent.ADD:
                    temp.dataNodeAdded(e);
                    break;
                case DataNodeEvent.CHANGE:
                    temp.dataNodeChanged(e);
                    break;
                case DataNodeEvent.REMOVE:
                    temp.dataNodeRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid DataNodeEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireDataMapDisplayEvent(DataMapDisplayEvent e) {
        boolean changed = e.getDataMap() != currentState.map;
        if (!changed) {
            changed = currentState.dbEntity != null
                    || currentState.objEntity != null
                    || currentState.procedure != null
                    || currentState.query != null;
        }

        if (!e.isRefired()) {
            e.setDataMapChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.node = e.getDataNode();
                currentState.map = e.getDataMap();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(DataMapDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            DataMapDisplayListener temp = (DataMapDisplayListener) list[i];
            temp.currentDataMapChanged(e);
        }
    }

    /**
     * Informs all listeners of the DataMapEvent. Does not send the event to its
     * originator.
     */
    public void fireDataMapEvent(DataMapEvent e) {
        setDirty(true);

        if (e.getId() == DataMapEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(DataMapListener.class);
        for (int i = 0; i < list.length; i++) {
            DataMapListener listener = (DataMapListener) list[i];
            switch (e.getId()) {
                case DataMapEvent.ADD:
                    listener.dataMapAdded(e);
                    break;
                case DataMapEvent.CHANGE:
                    listener.dataMapChanged(e);
                    break;
                case DataMapEvent.REMOVE:
                    listener.dataMapRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid DataMapEvent type: "
                            + e.getId());
            }
        }
    }

    /**
     * Informs all listeners of the EntityEvent. Does not send the event to its
     * originator.
     */
    public void fireObjEntityEvent(EntityEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == EntityEvent.CHANGE) {
            currentState.map.objEntityChanged(e);
        }

        if (e.getId() == EntityEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(ObjEntityListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjEntityListener temp = (ObjEntityListener) list[i];
            switch (e.getId()) {
                case EntityEvent.ADD:
                    temp.objEntityAdded(e);
                    break;
                case EntityEvent.CHANGE:
                    temp.objEntityChanged(e);
                    break;
                case EntityEvent.REMOVE:
                    temp.objEntityRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid EntityEvent type: "
                            + e.getId());
            }
        }
    }

    /**
     * Informs all listeners of the EntityEvent. Does not send the event to its
     * originator.
     */
    public void fireDbEntityEvent(EntityEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == EntityEvent.CHANGE) {
            currentState.map.dbEntityChanged(e);
        }

        if (e.getId() == EntityEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(DbEntityListener.class);
        for (int i = 0; i < list.length; i++) {
            DbEntityListener temp = (DbEntityListener) list[i];
            switch (e.getId()) {
                case EntityEvent.ADD:
                    temp.dbEntityAdded(e);
                    break;
                case EntityEvent.CHANGE:
                    temp.dbEntityChanged(e);
                    break;
                case EntityEvent.REMOVE:
                    temp.dbEntityRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid EntityEvent type: "
                            + e.getId());
            }
        }
    }

    /**
     * Informs all listeners of the ProcedureEvent. Does not send the event to its
     * originator.
     */
    public void fireQueryEvent(QueryEvent e) {
        setDirty(true);

        if (e.getId() == QueryEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(QueryListener.class);
        for (int i = 0; i < list.length; i++) {
            QueryListener listener = (QueryListener) list[i];
            switch (e.getId()) {
                case QueryEvent.ADD:
                    listener.queryAdded(e);
                    break;
                case QueryEvent.CHANGE:
                    listener.queryChanged(e);
                    break;
                case QueryEvent.REMOVE:
                    listener.queryRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ProcedureEvent type: "
                            + e.getId());
            }
        }
    }

    /**
     * Informs all listeners of the ProcedureEvent. Does not send the event to its
     * originator.
     */
    public void fireProcedureEvent(ProcedureEvent e) {
        setDirty(true);

        if (e.getId() == ProcedureEvent.REMOVE) {
            refreshNamespace();
            removeFromHistory(e);
        }

        EventListener[] list = listenerList.getListeners(ProcedureListener.class);
        for (int i = 0; i < list.length; i++) {
            ProcedureListener listener = (ProcedureListener) list[i];
            switch (e.getId()) {
                case ProcedureEvent.ADD:
                    listener.procedureAdded(e);
                    break;
                case ProcedureEvent.CHANGE:
                    listener.procedureChanged(e);
                    break;
                case ProcedureEvent.REMOVE:
                    listener.procedureRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ProcedureEvent type: "
                            + e.getId());
            }
        }
    }

    /**
     * Informs all listeners of the ProcedureEvent. Does not send the event to its
     * originator.
     */
    public void fireProcedureParameterEvent(ProcedureParameterEvent e) {
        setDirty(true);

        EventListener[] list = listenerList
                .getListeners(ProcedureParameterListener.class);
        for (int i = 0; i < list.length; i++) {
            ProcedureParameterListener listener = (ProcedureParameterListener) list[i];
            switch (e.getId()) {
                case EntityEvent.ADD:
                    listener.procedureParameterAdded(e);
                    break;
                case EntityEvent.CHANGE:
                    listener.procedureParameterChanged(e);
                    break;
                case EntityEvent.REMOVE:
                    listener.procedureParameterRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Invalid ProcedureParameterEvent type: " + e.getId());
            }
        }
    }

    public void fireNavigationEvent(EventObject e) {
        Object source = e.getSource();
        if (source == null)
            return;

        int size = controllerStateHistory.size();
        if (size == 0)
            return;

        int i = controllerStateHistory.indexOf(currentState);
        ControllerState cs = null;
        if (size == 1) {
            cs = (ControllerState) controllerStateHistory.get(0);
        }
        else if (source instanceof NavigateForwardAction) {
            int counter = 0;
            while (true) {
                if (i < 0) {
                    // a new state got created without it being saved.
                    // just move to the beginning of the list
                    cs = (ControllerState) controllerStateHistory.get(0);
                }
                else if (i + 1 < size) {
                    // move forward
                    cs = (ControllerState) controllerStateHistory.get(i + 1);
                }
                else {
                    // wrap around
                    cs = (ControllerState) controllerStateHistory.get(0);
                }
                if (!cs.isEquivalent(currentState)) {
                    break;
                }

                // if it doesn't find it within 5 tries it is probably stuck in a loop
                if (++counter > 5) {
                    break;
                }
                i++;
            }
        }
        else if (source instanceof NavigateBackwardAction) {
            int counter = 0;
            while (true) {
                if (i < 0) {
                    // a new state got created without it being saved.
                    try {
                        cs = (ControllerState) controllerStateHistory.get(size - 2);
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        cs = (ControllerState) controllerStateHistory.get(size - 1);
                    }
                }
                else if (i - 1 >= 0) {
                    // move to the previous one
                    cs = (ControllerState) controllerStateHistory.get(i - 1);
                }
                else {
                    // wrap around
                    cs = (ControllerState) controllerStateHistory.get(size - 1);
                }
                if (!cs.isEquivalent(currentState)) {
                    break;
                }
                // if it doesn't find it within 5 tries it is probably stuck in a loop
                if (++counter > 5) {
                    break;
                }
                i--;
            }
        }

        // reset the current state to the one we just navigated to
        currentState = cs;
        DisplayEvent de = cs.event;
        if (de == null)
            return;

        // make sure that isRefiring is turned off prior to exiting this routine
        // this flag is used to tell the controller to not create new states
        // when we are refiring the event that we saved earlier
        currentState.isRefiring = true;

        // the order of the following is checked in most specific to generic because
        // of the inheritance heirarchy
        de.setRefired(true);
        if (de instanceof EntityDisplayEvent) {
            EntityDisplayEvent ede = (EntityDisplayEvent) de;
            ede.setEntityChanged(true);
            if (ede.getEntity() instanceof ObjEntity) {
                fireObjEntityDisplayEvent(ede);
            }
            else if (ede.getEntity() instanceof DbEntity) {
                fireDbEntityDisplayEvent(ede);
            }
        }
        else if (de instanceof ProcedureDisplayEvent) {
            ProcedureDisplayEvent pde = (ProcedureDisplayEvent) de;
            pde.setProcedureChanged(true);
            fireProcedureDisplayEvent(pde);
        }
        else if (de instanceof QueryDisplayEvent) {
            QueryDisplayEvent qde = (QueryDisplayEvent) de;
            qde.setQueryChanged(true);
            fireQueryDisplayEvent(qde);
        }
        else if (de instanceof DataMapDisplayEvent) {
            DataMapDisplayEvent dmde = (DataMapDisplayEvent) de;
            dmde.setDataMapChanged(true);
            fireDataMapDisplayEvent(dmde);
        }
        else if (de instanceof DataNodeDisplayEvent) {
            DataNodeDisplayEvent dnde = (DataNodeDisplayEvent) de;
            dnde.setDataNodeChanged(true);
            fireDataNodeDisplayEvent(dnde);
        }
        else if (de instanceof DomainDisplayEvent) {
            DomainDisplayEvent dde = (DomainDisplayEvent) de;
            dde.setDomainChanged(true);
            fireDomainDisplayEvent(dde);
        }

        // turn off refiring
        currentState.isRefiring = false;
    }

    public void fireObjEntityDisplayEvent(EntityDisplayEvent e) {
        boolean changed = e.getEntity() != currentState.objEntity;

        if (!e.isRefired()) {
            e.setEntityChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.node = e.getDataNode();
                currentState.map = e.getDataMap();
                currentState.objEntity = (ObjEntity) e.getEntity();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(ObjEntityDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjEntityDisplayListener temp = (ObjEntityDisplayListener) list[i];
            temp.currentObjEntityChanged(e);
        }
    }

    public void fireQueryDisplayEvent(QueryDisplayEvent e) {
        boolean changed = e.getQuery() != currentState.query;

        if (!e.isRefired()) {
            e.setQueryChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.query = e.getQuery();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(QueryDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            QueryDisplayListener listener = (QueryDisplayListener) list[i];
            listener.currentQueryChanged(e);
        }
    }

    public void fireProcedureDisplayEvent(ProcedureDisplayEvent e) {
        boolean changed = e.getProcedure() != currentState.procedure;

        if (!e.isRefired()) {
            e.setProcedureChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.procedure = e.getProcedure();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(ProcedureDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ProcedureDisplayListener listener = (ProcedureDisplayListener) list[i];
            listener.currentProcedureChanged(e);
        }
    }

    public void fireProcedureParameterDisplayEvent(ProcedureParameterDisplayEvent e) {
        boolean changed = e.getProcedureParameter() != currentState.procedureParameter;

        if (changed) {
            if (currentState.procedure != e.getProcedure()) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.procedure = e.getProcedure();
            }
            currentState.procedureParameter = e.getProcedureParameter();
        }

        EventListener[] list = listenerList
                .getListeners(ProcedureParameterDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ProcedureParameterDisplayListener listener = (ProcedureParameterDisplayListener) list[i];
            listener.currentProcedureParameterChanged(e);
        }
    }

    public void fireDbEntityDisplayEvent(EntityDisplayEvent e) {
        boolean changed = e.getEntity() != currentState.dbEntity;
        if (!e.isRefired()) {
            e.setEntityChanged(changed);

            if (changed) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.node = e.getDataNode();
                currentState.map = e.getDataMap();
                currentState.dbEntity = (DbEntity) e.getEntity();
            }
        }

        if (changed) {
            saveState(e);
        }

        EventListener[] list = listenerList.getListeners(DbEntityDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            DbEntityDisplayListener temp = (DbEntityDisplayListener) list[i];
            temp.currentDbEntityChanged(e);
        }
    }

    /** Notifies all listeners of the change(add, remove) and does the change. */
    public void fireDbAttributeEvent(AttributeEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == AttributeEvent.CHANGE) {
            currentState.map.dbAttributeChanged(e);
        }

        EventListener[] list = listenerList.getListeners(DbAttributeListener.class);
        for (int i = 0; i < list.length; i++) {
            DbAttributeListener temp = (DbAttributeListener) list[i];
            switch (e.getId()) {
                case AttributeEvent.ADD:
                    temp.dbAttributeAdded(e);
                    break;
                case AttributeEvent.CHANGE:
                    temp.dbAttributeChanged(e);
                    break;
                case AttributeEvent.REMOVE:
                    temp.dbAttributeRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid AttributeEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireDbAttributeDisplayEvent(AttributeDisplayEvent e) {
        boolean changed = e.getAttribute() != currentState.dbAttr;

        if (changed) {
            if (e.getEntity() != currentState.dbEntity) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.dbEntity = (DbEntity) e.getEntity();
            }
            currentState.dbAttr = (DbAttribute) e.getAttribute();
        }

        EventListener[] list = listenerList
                .getListeners(DbAttributeDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            DbAttributeDisplayListener temp = (DbAttributeDisplayListener) list[i];
            temp.currentDbAttributeChanged(e);
        }
    }

    /** Notifies all listeners of the change (add, remove) and does the change. */
    public void fireObjAttributeEvent(AttributeEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == AttributeEvent.CHANGE) {
            currentState.map.objAttributeChanged(e);
        }

        EventListener[] list = listenerList.getListeners(ObjAttributeListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjAttributeListener temp = (ObjAttributeListener) list[i];
            switch (e.getId()) {
                case AttributeEvent.ADD:
                    temp.objAttributeAdded(e);
                    break;
                case AttributeEvent.CHANGE:
                    temp.objAttributeChanged(e);
                    break;
                case AttributeEvent.REMOVE:
                    temp.objAttributeRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid AttributeEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireObjAttributeDisplayEvent(AttributeDisplayEvent e) {
        boolean changed = e.getAttribute() != currentState.objAttr;

        if (changed) {
            if (e.getEntity() != currentState.objEntity) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.objEntity = (ObjEntity) e.getEntity();
            }
            currentState.objAttr = (ObjAttribute) e.getAttribute();
        }

        EventListener[] list = listenerList
                .getListeners(ObjAttributeDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjAttributeDisplayListener temp = (ObjAttributeDisplayListener) list[i];
            temp.currentObjAttributeChanged(e);
        }
    }

    /** Notifies all listeners of the change(add, remove) and does the change. */
    public void fireDbRelationshipEvent(RelationshipEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == RelationshipEvent.CHANGE) {
            currentState.map.dbRelationshipChanged(e);
        }

        EventListener[] list = listenerList.getListeners(DbRelationshipListener.class);
        for (int i = 0; i < list.length; i++) {
            DbRelationshipListener temp = (DbRelationshipListener) list[i];
            switch (e.getId()) {
                case RelationshipEvent.ADD:
                    temp.dbRelationshipAdded(e);
                    break;
                case RelationshipEvent.CHANGE:
                    temp.dbRelationshipChanged(e);
                    break;
                case RelationshipEvent.REMOVE:
                    temp.dbRelationshipRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid RelationshipEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireDbRelationshipDisplayEvent(RelationshipDisplayEvent e) {
        boolean changed = e.getRelationship() != currentState.dbRel;
        e.setRelationshipChanged(changed);

        if (changed) {
            if (e.getEntity() != currentState.dbEntity) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.dbEntity = (DbEntity) e.getEntity();
            }
            currentState.dbRel = (DbRelationship) e.getRelationship();
        }

        EventListener[] list = listenerList
                .getListeners(DbRelationshipDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            DbRelationshipDisplayListener temp = (DbRelationshipDisplayListener) list[i];
            temp.currentDbRelationshipChanged(e);
        }
    }

    /** Notifies all listeners of the change(add, remove) and does the change. */
    public void fireObjRelationshipEvent(RelationshipEvent e) {
        setDirty(true);

        if (currentState.map != null && e.getId() == RelationshipEvent.CHANGE) {
            currentState.map.objRelationshipChanged(e);
        }

        EventListener[] list = listenerList.getListeners(ObjRelationshipListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjRelationshipListener temp = (ObjRelationshipListener) list[i];
            switch (e.getId()) {
                case RelationshipEvent.ADD:
                    temp.objRelationshipAdded(e);
                    break;
                case RelationshipEvent.CHANGE:
                    temp.objRelationshipChanged(e);
                    break;
                case RelationshipEvent.REMOVE:
                    temp.objRelationshipRemoved(e);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid RelationshipEvent type: "
                            + e.getId());
            }
        }
    }

    public void fireObjRelationshipDisplayEvent(RelationshipDisplayEvent e) {
        boolean changed = e.getRelationship() != currentState.objRel;
        e.setRelationshipChanged(changed);

        if (changed) {
            if (e.getEntity() != currentState.objEntity) {
                clearState();
                currentState.domain = e.getDomain();
                currentState.map = e.getDataMap();
                currentState.objEntity = (ObjEntity) e.getEntity();
            }
            currentState.objRel = (ObjRelationship) e.getRelationship();
        }

        EventListener[] list = listenerList
                .getListeners(ObjRelationshipDisplayListener.class);
        for (int i = 0; i < list.length; i++) {
            ObjRelationshipDisplayListener temp = (ObjRelationshipDisplayListener) list[i];
            temp.currentObjRelationshipChanged(e);
        }
    }

    public void addDataMap(Object src, DataMap map) {
        addDataMap(src, map, true);
    }

    public void addDataMap(Object src, DataMap map, boolean makeCurrent) {

        // new map was added.. link it to domain (and node if possible)
        currentState.domain.addMap(map);

        if (currentState.node != null && !currentState.node.getDataMaps().contains(map)) {
            currentState.node.addDataMap(map);
            fireDataNodeEvent(new DataNodeEvent(this, currentState.node));
            currentState.domain.reindexNodes();
        }

        fireDataMapEvent(new DataMapEvent(src, map, DataMapEvent.ADD));
        if (makeCurrent) {
            fireDataMapDisplayEvent(new DataMapDisplayEvent(
                    src,
                    map,
                    currentState.domain,
                    currentState.node));
        }
    }

    public void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;

            application.getAction(SaveAction.getActionName()).setEnabled(dirty);
            application.getAction(RevertAction.getActionName()).setEnabled(dirty);

            if (dirty) {
                CayenneModelerController parent = (CayenneModelerController) getParent();
                parent.projectModifiedAction();
            }
        }
    }
}