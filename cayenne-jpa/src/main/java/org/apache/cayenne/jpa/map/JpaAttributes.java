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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.cayenne.util.TreeNodeChild;

/**
 * An attribute container.
 * 
 * @author Andrus Adamchik
 */
public class JpaAttributes {

    protected Collection<JpaId> ids;
    protected JpaEmbeddedId embeddedId;
    protected Collection<JpaBasic> basicAttributes;
    protected Collection<JpaVersion> versionAttributes;
    protected Collection<JpaManyToOne> manyToOneRelationships;
    protected Collection<JpaOneToMany> oneToManyRelationships;
    protected Collection<JpaOneToOne> oneToOneRelationships;
    protected Collection<JpaManyToMany> manyToManyRelationships;
    protected Collection<JpaEmbedded> embeddedAttributes;
    protected Collection<JpaTransient> transientAttributes;

    /**
     * Returns combined count of all attributes and relationships.
     */
    public int size() {
        int size = 0;

        if (embeddedId != null) {
            size++;
        }

        if (ids != null) {
            size += ids.size();
        }
        if (basicAttributes != null) {
            size += basicAttributes.size();
        }
        if (versionAttributes != null) {
            size += versionAttributes.size();
        }
        if (manyToOneRelationships != null) {
            size += manyToOneRelationships.size();
        }
        if (oneToManyRelationships != null) {
            size += oneToManyRelationships.size();
        }
        if (oneToOneRelationships != null) {
            size += oneToOneRelationships.size();
        }
        if (manyToManyRelationships != null) {
            size += manyToManyRelationships.size();
        }
        if (embeddedAttributes != null) {
            size += embeddedAttributes.size();
        }
        if (transientAttributes != null) {
            size += transientAttributes.size();
        }
        return size;
    }

    public JpaId getId(String idName) {
        if (idName == null) {
            throw new IllegalArgumentException("Null id name");
        }

        if (ids != null) {
            for (JpaId id : ids) {
                if (idName.equals(id.getName())) {
                    return id;
                }
            }
        }

        return null;
    }

    /**
     * Returns a JpaAttribute for a given property name
     */
    public JpaBasic getBasicAttribute(String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Null attribute name");
        }

        if (basicAttributes != null) {
            for (JpaBasic attribute : basicAttributes) {
                if (attributeName.equals(attribute.getName())) {
                    return attribute;
                }
            }
        }

        return null;
    }

    public JpaManyToOne getManyToOneRelationship(String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Null attribute name");
        }

        if (manyToOneRelationships != null) {
            for (JpaManyToOne attribute : manyToOneRelationships) {
                if (attributeName.equals(attribute.getName())) {
                    return attribute;
                }
            }
        }

        return null;
    }

    public JpaOneToMany getOneToManyRelationship(String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Null attribute name");
        }

        if (oneToManyRelationships != null) {
            for (JpaOneToMany attribute : oneToManyRelationships) {
                if (attributeName.equals(attribute.getName())) {
                    return attribute;
                }
            }
        }

        return null;
    }

    @TreeNodeChild(type = JpaId.class)
    public Collection<JpaId> getIds() {
        if (ids == null) {
            ids = new ArrayList<JpaId>();
        }

        return ids;
    }

    public JpaEmbeddedId getEmbeddedId() {
        return embeddedId;
    }

    public void setEmbeddedId(JpaEmbeddedId embeddedId) {
        this.embeddedId = embeddedId;
    }

    public Collection<JpaBasic> getBasicAttributes() {
        if (basicAttributes == null) {
            basicAttributes = new ArrayList<JpaBasic>();
        }
        return basicAttributes;
    }

    public Collection<JpaEmbedded> getEmbeddedAttributes() {
        if (embeddedAttributes == null) {
            embeddedAttributes = new ArrayList<JpaEmbedded>();
        }
        return embeddedAttributes;
    }

    public Collection<JpaManyToMany> getManyToManyRelationships() {
        if (manyToManyRelationships == null) {
            manyToManyRelationships = new ArrayList<JpaManyToMany>();
        }
        return manyToManyRelationships;
    }

    public Collection<JpaManyToOne> getManyToOneRelationships() {
        if (manyToOneRelationships == null) {
            manyToOneRelationships = new ArrayList<JpaManyToOne>();
        }
        return manyToOneRelationships;
    }

    public Collection<JpaOneToMany> getOneToManyRelationships() {
        if (oneToManyRelationships == null) {
            oneToManyRelationships = new ArrayList<JpaOneToMany>();
        }
        return oneToManyRelationships;
    }

    public Collection<JpaOneToOne> getOneToOneRelationships() {
        if (oneToOneRelationships == null) {
            oneToOneRelationships = new ArrayList<JpaOneToOne>();
        }
        return oneToOneRelationships;
    }

    public Collection<JpaTransient> getTransientAttributes() {
        if (transientAttributes == null) {
            transientAttributes = new ArrayList<JpaTransient>();
        }
        return transientAttributes;
    }

    public Collection<JpaVersion> getVersionAttributes() {
        if (versionAttributes == null) {
            versionAttributes = new ArrayList<JpaVersion>();
        }
        return versionAttributes;
    }
}
