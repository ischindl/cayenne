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
package org.apache.cayenne.access;

import java.util.Map;

import org.apache.cayenne.Persistent;

/**
 * An {@link ObjectStore} which doesn't receive notifications 
 * on parent's {@link DataRowStore} events.
 * 
 *  @since 3.1
 */
public class NoSyncObjectStore extends ObjectStore {
    
    public NoSyncObjectStore(DataRowStore dataRowCache, Map<Object, Persistent> objectMap) {
        super(dataRowCache, objectMap);
    }
    
    @Override
    public void setDataRowCache(DataRowStore dataRowCache) {

        this.dataRowCache = dataRowCache;
        dataRowCacheSet = dataRowCache != null;
    }

}
