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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.QueryMetadata;
import org.apache.commons.collections.map.LRUMap;

/**
 * A default implementation of the {@link QueryCache} interface that stores data in a
 * non-expiring LRUMap.
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
public class MapQueryCache implements QueryCache, Serializable {

    public static final int DEFAULT_CACHE_SIZE = 2000;

    protected Map map;

    public MapQueryCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    public MapQueryCache(int maxSize) {
        this.map = new LRUMap(maxSize);
    }

    public List get(QueryMetadata metadata) {
        String key = metadata.getCacheKey();
        if (key == null) {
            return null;
        }

        CacheEntry entry;
        synchronized (this) {
            entry = (CacheEntry) map.get(key);
        }

        return (entry != null) ? entry.list : null;
    }

    public void put(QueryMetadata metadata, List results) {
        String key = metadata.getCacheKey();
        if (key != null) {

            CacheEntry entry = new CacheEntry();
            entry.list = results;

            ObjEntity entity = metadata.getObjEntity();
            if (entity != null) {
                entry.entityName = entity.getName();
            }

            synchronized (this) {
                map.put(key, entry);
            }
        }
    }

    public void remove(String key) {
        if (key != null) {
            synchronized (this) {
                map.remove(key);
            }
        }
    }

    public void removeGroup(String groupKey) {
        if (groupKey != null) {
            synchronized (this) {
                Iterator it = map.values().iterator();
                while (it.hasNext()) {
                    CacheEntry entry = (CacheEntry) it.next();
                    if (groupKey.equals(entry.entityName)) {
                        it.remove();
                    }
                }
            }
        }
    }
    
    public void clear() {
        synchronized (this) {
            map.clear();
        }
    }

    public int size() {
        return map.size();
    }

    final class CacheEntry {

        List list;
        String entityName;
    }
}
