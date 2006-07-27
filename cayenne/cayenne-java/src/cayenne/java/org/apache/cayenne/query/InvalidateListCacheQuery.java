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
package org.apache.cayenne.query;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.EntityResolver;

/**
 * A Query that allows to clear list caches. Lists to invalidate are located by matching a
 * either a query name or an array of "groups" (one common "group key" is ObjEntity name).
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
public class InvalidateListCacheQuery implements Query {

    protected String queryNameKey;
    protected String[] groupKeys;
    protected boolean cascade;

    // needed for hessian serialization
    private InvalidateListCacheQuery() {

    }

    /**
     * Creates a new InvalidateCacheQuery. Either "queryNameKey" or "groupKeys" parameter
     * (or both) will have to be specified; otherwise the query makes no sense.
     * 
     * @param queryNameKey a string that matches a cache key of a single query.
     * @param cascade whether to invalidate cache in the local ObjectContext, or to
     *            propagate the operation through the entire stack.
     */
    public InvalidateListCacheQuery(String queryNameKey, String[] groupKeys,
            boolean cascade) {

        if (queryNameKey == null && (groupKeys == null || groupKeys.length == 0)) {
            throw new IllegalArgumentException(
                    "Either \"queryNameKey\" or \"groupKeys\" parameter (or both) must be specified");
        }

        this.queryNameKey = queryNameKey;
        this.groupKeys = groupKeys;
        this.cascade = cascade;
    }

    public boolean isCascade() {
        return cascade;
    }

    public QueryMetadata getMetaData(EntityResolver resolver) {
        return new BaseQueryMetadata();
    }

    public String getName() {
        return null;
    }

    public void route(QueryRouter router, EntityResolver resolver, Query substitutedQuery) {
        // noop
    }

    public SQLAction createSQLAction(SQLActionVisitor visitor) {
        throw new CayenneRuntimeException("Unsupported operation");
    }

    public String[] getGroupKeys() {
        return groupKeys;
    }

    public void setGroupKeys(String[] groupKeys) {
        this.groupKeys = groupKeys;
    }

    public String getQueryNameKey() {
        return queryNameKey;
    }

    public void setQueryNameKey(String queryNameKey) {
        this.queryNameKey = queryNameKey;
    }
}
