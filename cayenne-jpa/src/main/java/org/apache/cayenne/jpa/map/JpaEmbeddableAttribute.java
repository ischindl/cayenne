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

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.apache.cayenne.util.TreeNodeChild;

/**
 * An attribute that belongs to {@link org.apache.cayenne.jpa.map.JpaEmbeddable}.
 * 
 * @author Andrus Adamchik
 */
public class JpaEmbeddableAttribute extends JpaAttribute {

    protected boolean lob;
    protected JpaBasic basic;
    protected TemporalType temporal;
    protected JpaColumn column;
    protected EnumType enumerated;

    @TreeNodeChild
    public JpaBasic getBasic() {
        return basic;
    }

    public void setBasic(JpaBasic basic) {
        this.basic = basic;
    }

    public JpaColumn getColumn() {
        return column;
    }

    public void setColumn(JpaColumn column) {
        this.column = column;
    }

    public EnumType getEnumerated() {
        return enumerated;
    }

    public void setEnumerated(EnumType enumerated) {
        this.enumerated = enumerated;
    }

    public TemporalType getTemporal() {
        return temporal;
    }

    public void setTemporal(TemporalType temporal) {
        this.temporal = temporal;
    }

    public boolean isLob() {
        return lob;
    }

    public void setLob(boolean lob) {
        this.lob = lob;
    }

    /**
     * A special setter used by XML decoder to indicate lob flag presence.
     */
    public void setLobTrue(String string) {
        setLob(true);
    }
}
