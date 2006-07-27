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

package org.apache.cayenne.wocompat;

import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;

/**
 * An EOObjAttribute is a mapping descriptor of a Java class property with added
 * fields for WebObjects EOModel.
 *
 * @author Dario Bagatto
 */
public class EOObjAttribute extends ObjAttribute {

    // flag whether this attribute is read only.
    protected boolean readOnly;


    public EOObjAttribute() {
        super();
    }


    public EOObjAttribute(String name) {
        super(name);
    }


    public EOObjAttribute(String name, String type, ObjEntity entity) {
        super(name, type, entity);
    }


    /**
     * Sets the read only state of this attribute.
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Get the read only state of this attribute
     * @return read only state of this attribute
     */
    public boolean getReadOnly() {
        return readOnly;
    }

}
