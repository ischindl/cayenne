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


package org.apache.cayenne.jpa.map;

public class JpaMappedSuperclass extends JpaAbstractEntity {

    /**
     * Returns a guranateed non-null JpaEntityListeners instance owned by this mapped
     * superclass.
     */
    // note that per orm_1_0.xsd, mapped superclass can have only a single listener which
    // seems to be an error in the spec, while annotations allow setting multiple
    // listeners
    public JpaEntityListeners getEntityListeners() {
        if (entityListeners == null) {
            entityListeners = new JpaEntityListeners();
        }

        return entityListeners;
    }
}
