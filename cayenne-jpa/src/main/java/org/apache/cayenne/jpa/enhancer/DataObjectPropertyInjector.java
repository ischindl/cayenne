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


package org.apache.cayenne.jpa.enhancer;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;

public class DataObjectPropertyInjector extends PropertyInjector {

    static final String OBJECT_ID_PROPERTY = "objectId";
    static final String PERSISTENCE_STATE_PROPERTY = "persistenceState";
    static final String OBJECT_CONTEXT_PROPERTY = "objectContext";

    static final String SNAPSHOT_VERSION_PROPERTY = "snapshotVersion";

    static final String[] PROPERTIES = new String[] {
            OBJECT_ID_PROPERTY, PERSISTENCE_STATE_PROPERTY, OBJECT_CONTEXT_PROPERTY,
            SNAPSHOT_VERSION_PROPERTY
    };

    static final Class[] TYPES = new Class[] {
            ObjectId.class, Integer.TYPE, ObjectContext.class, Long.TYPE
    };

    public DataObjectPropertyInjector() {
        super(PROPERTIES, TYPES);
    }
}
