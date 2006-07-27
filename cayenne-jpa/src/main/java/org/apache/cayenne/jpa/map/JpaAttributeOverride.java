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

import javax.persistence.AttributeOverride;

import org.apache.cayenne.util.TreeNodeChild;

public class JpaAttributeOverride {

    protected String name;
    protected JpaColumn column;

    public JpaAttributeOverride() {

    }

    public JpaAttributeOverride(AttributeOverride annotation) {
        name = annotation.name();

        if (annotation.column() != null) {
            column = new JpaColumn(annotation.column());
        }
    }

    @TreeNodeChild
    public JpaColumn getColumn() {
        return column;
    }

    public void setColumn(JpaColumn column) {
        this.column = column;
    }

    /**
     * Returns overriden attribute name.
     * <h3>Specification Documentation</h3>
     * <p>
     * <b>Description:</b> (Required) The name of the property in the embedded object
     * that is being mapped if property-based access is being used, or the name of the
     * field if field-based access is used.
     * </p>
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
