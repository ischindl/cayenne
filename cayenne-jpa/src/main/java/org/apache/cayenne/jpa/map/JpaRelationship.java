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

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

public abstract class JpaRelationship extends JpaAttribute {

    protected String targetEntityName;
    protected FetchType fetch;
    protected Collection<CascadeType> cascades;

    public abstract boolean isToMany();

    public Collection<CascadeType> getCascades() {
        if (cascades == null) {
            // TODO: andrus, 4/20/2006 - replace with
            // ArrayList<CascadeType>() once CAY-520 gets implemented in Cayenne > 1.2
            cascades = new EnumList(CascadeType.class, CascadeType.values().length);
        }

        return cascades;
    }

    public FetchType getFetch() {
        return fetch;
    }

    public void setFetch(FetchType fetch) {
        this.fetch = fetch;
    }

    public String getTargetEntityName() {
        return targetEntityName;
    }

    public void setTargetEntityName(String targetEntityName) {
        this.targetEntityName = targetEntityName;
    }
}
