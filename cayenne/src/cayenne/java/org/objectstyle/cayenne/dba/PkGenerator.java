/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
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
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.cayenne.dba;

import java.util.List;

import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.map.DbEntity;

/**
 * Defines methods to support automatic primary key generation.
 *
 * @author Andrei Adamchik
 */
public interface PkGenerator {

    /**
     * Generates necessary database objects to provide automatic primary
     * key support.
     *
     * @param node node that provides access to a DataSource.
     * @param dbEntities a list of entities that require primary key autogeneration support
     */
    public void createAutoPk(DataNode node, List dbEntities) throws Exception;

    /**
     * Returns a list of SQL strings needed to generates
     * database objects to provide automatic primary support
     * for the list of entities. No actual database operations
     * are performed.
     */
    public List createAutoPkStatements(List dbEntities);


    /**
     * Drops any common database objects associated with automatic primary
     * key generation process. This may be lookup tables, special stored
     * procedures or sequences.
     *
     * @param node node that provides access to a DataSource.
     * @param dbEntities a list of entities whose primary key autogeneration support
     * should be dropped.
     */
    public void dropAutoPk(DataNode node, List dbEntities) throws Exception;


    /**
     * Returns SQL string needed to drop database objects associated
     * with automatic primary key generation. No actual database
     * operations are performed.
     */
    public List dropAutoPkStatements(List dbEntities);



    /**
     * Generates new (unique and non-repeating) primary key for specified
     * DbEntity.
     *
     *  @param ent DbEntity for which automatic PK is generated.
     */
    public Object generatePkForDbEntity(DataNode dataNode, DbEntity ent)
        throws Exception;


    /**
     * Returns SQL string that can generate new (unique and non-repeating)
     * primary key for specified DbEntity. No actual database operations
     * are performed.
     */
    public String generatePkForDbEntityString(DbEntity ent);

    public void reset();

}