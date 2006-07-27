/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/


package org.apache.cayenne.jpa.cspi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.jpa.JpaEntityManager;

public class CjpaEntityManager extends JpaEntityManager {

    private DataContext context;

    public CjpaEntityManager(DataContext context, EntityManagerFactory factory,
            Map parameters) {
        super(factory);
        this.context = context;
    }

    @Override
    protected void persistInternal(Object entity) {
        checkEntityType(entity);
        context.registerNewObject((DataObject) entity);
    }

    @Override
    protected <T> T mergeInternal(T entity) {
        checkEntityType(entity);
        Persistent dao = (Persistent) entity;
        return (T) context.localObject(dao.getObjectId(), dao);
    }

    @Override
    protected void removeInternal(Object entity) {
        checkEntityType(entity);
        context.deleteObject((Persistent) entity);
    }

    @Override
    protected <T> T findInternal(Class<T> entityClass, Object primaryKey) {
        return (T) DataObjectUtils.objectForPK(context, entityClass, primaryKey);
    }

    @Override
    protected void flushInternal() {
        try {
            context.commitChanges();
        }
        catch (CayenneRuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    protected void refreshInternal(Object entity) {
        // TODO: Andrus, 2/10/2006 - implement
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    protected boolean containsInternal(Object entity) {
        checkEntityType(entity);

        Persistent p = (Persistent) entity;
        return p.getObjectContext() == context;
    }

    @Override
    public Query createNamedQuery(String name) {
        checkClosed();

        return new CjpaQuery(context, name);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        checkClosed();
        checkEntityType(resultClass);

        return new CjpaNativeQuery(context, sqlString, resultClass);
    }

    @Override
    public void joinTransaction() {
        // TODO: andrus, 7/24/2006 - noop
    }

    protected void checkEntityType(Object entity) throws IllegalArgumentException {
        if (!(entity instanceof Persistent)) {
            throw new IllegalArgumentException("entity must be Persistent");
        }
    }
}
