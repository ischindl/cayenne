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
package org.apache.cayenne.cache;

import java.util.List;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.QueryMetadata;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * A {@link QueryCache} implementation based on OpenSymphony OSCache.
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
public class OSQueryCache implements QueryCache {

    protected GeneralCacheAdministrator cache;
    protected int refreshPeriod;
    protected String cronExpression;

    public OSQueryCache(int refreshPeriod, String cronExpression) {
        this.cache = new GeneralCacheAdministrator();
        this.refreshPeriod = refreshPeriod;
        this.cronExpression = cronExpression;
    }

    public List get(QueryMetadata metadata) {
        String key = metadata.getCacheKey();
        if (key == null) {
            return null;
        }

        try {
            return (List) cache.getFromCache(key, refreshPeriod, cronExpression);
        }
        catch (NeedsRefreshException e) {
            cache.cancelUpdate(key);
            return null;
        }
    }

    public void put(QueryMetadata metadata, List results) {
        String key = metadata.getCacheKey();
        if (key != null) {

            ObjEntity entity = metadata.getObjEntity();

            String[] groups = entity != null ? new String[] {
                entity.getName()
            } : null;

            cache.putInCache(key, results, groups);
        }
    }

    public void remove(String key) {
        if (key != null) {
            cache.removeEntry(key);
        }
    }

    public void removeGroup(String groupKey) {
        if (groupKey != null) {
            cache.flushGroup(groupKey);
        }
    }

    public void clear() {
        cache.flushAll();
    }

    public int size() {
        return cache.getCache().getSize();
    }
}
