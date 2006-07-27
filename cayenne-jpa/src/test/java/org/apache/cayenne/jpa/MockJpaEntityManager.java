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


package org.apache.cayenne.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class MockJpaEntityManager extends JpaEntityManager {

    public MockJpaEntityManager(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    protected void persistInternal(Object entity) {
    }

    @Override
    protected <T> T mergeInternal(T entity) {
        return null;
    }

    @Override
    protected void removeInternal(Object entity) {
    }

    @Override
    protected <T> T findInternal(Class<T> entityClass, Object primaryKey) {
        return null;
    }

    @Override
    protected void flushInternal() {
    }

    @Override
    protected void refreshInternal(Object entity) {
    }

    @Override
    protected boolean containsInternal(Object entity) {
        return false;
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        checkClosed();

        return null;
    }

    @Override
    public Query createNamedQuery(String name) {
        checkClosed();

        return null;
    }

    @Override
    public void joinTransaction() {
    }
}
