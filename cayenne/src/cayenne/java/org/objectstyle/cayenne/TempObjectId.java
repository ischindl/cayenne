/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors
 * of the software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowlegement:
 *    "This product includes software developed by independent contributors
 *    and hosted on ObjectStyle Group web site (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, email
 *    "andrus at objectstyle dot org".
 * 
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    or "Cayenne", nor may "ObjectStyle" or "Cayenne" appear in their
 *    names without prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals and hosted on ObjectStyle Group web site.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 */
package org.objectstyle.cayenne;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An ObjectId for new objects that hasn't been committed to the external data store. On
 * commit, a TempObjectId is replaced with a permanent ObjectId tied to a primary key of
 * an object in the external data store.
 * 
 * @author Andrei Adamchik
 */
public class TempObjectId extends ObjectId {

    protected byte[] key;

    /**
     * Creates a non-portable temporary ObjectId that should be replaced by a permanent id
     * once a corresponding object is committed.
     */
    public TempObjectId(Class objectClass) {
        super(objectClass, null);
    }

    /**
     * Create a TempObjectId with a binary unique key. This id is "portable" in that it
     * can be used across virtual machines to identify the same object.
     * 
     * @since 1.2
     */
    public TempObjectId(Class objectClass, byte[] key) {
        super(objectClass, null);
        this.key = key;
    }
    
    /**
     * Returns a binary unique key for this id.
     * 
     * @since 1.2
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * TempObjectId equality condition is based on object reference comparison. This is
     * possible since each object in a "new" state is unique, and TempObjectId is only
     * used for "new" objects.
     */
    public boolean equals(Object object) {
        // non-portable id
        if (key == null) {
            return object == this;
        }

        if (this == object) {
            return true;
        }

        if (!(object instanceof TempObjectId)) {
            return false;
        }

        TempObjectId id = (TempObjectId) object;
        return new EqualsBuilder()
                .append(objectClass.getName(), id.objectClass.getName())
                .append(key, id.key)
                .isEquals();
    }
    
    public int hashCode() {
        if (this.hashCode == Integer.MIN_VALUE) {
            // build and cache hashCode
            HashCodeBuilder builder = new HashCodeBuilder(15, 37);

            // use the class name because two ObjectId's should be equal
            // even if their objClass'es were loaded by different class loaders.
            builder.append(objectClass.getName().hashCode());

            if (key != null) {
                builder.append(key);
            }

            this.hashCode = builder.toHashCode();
        }

        return this.hashCode;
    }

    /**
     * Returns an empty map if there is no replacement id available, or a snapshot of a
     * replacement id otherwise. Note that if a replacement id map is returned there is no
     * guarantee that it is complete and has all keys.
     */
    public Map getIdSnapshot() {
        return (replacementIdMap == null) ? Collections.EMPTY_MAP : replacementIdMap;
    }

    /**
     * Always returns <code>true</code>.
     */
    public boolean isTemporary() {
        return true;
    }
}