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
package org.objectstyle.cayenne.unit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectstyle.cayenne.dba.DbAdapter;
import org.objectstyle.cayenne.map.DataMap;
import org.objectstyle.cayenne.map.Procedure;

/**
 * @author Andrei Adamchik
 */
public class SybaseStackAdapter extends AccessStackAdapter {

    /**
     * Constructor for SybaseDelegate.
     * 
     * @param adapter
     */
    public SybaseStackAdapter(DbAdapter adapter) {
        super(adapter);
    }

    public boolean supportsStoredProcedures() {
        return true;
    }

    public void createdTables(Connection con, DataMap map) throws Exception {
        Procedure proc = map.getProcedure("cayenne_tst_select_proc");
        if (proc != null && proc.getDataMap() == map) {
            executeDDL(con, "sybase", "create-select-sp.sql");
            executeDDL(con, "sybase", "create-update-sp.sql");
            executeDDL(con, "sybase", "create-out-sp.sql");
        }
    }

    public void willDropTables(Connection con, DataMap map, Collection tablesToDrop)
            throws Exception {
        super.willDropTables(con, map, tablesToDrop);

        Procedure proc = map.getProcedure("cayenne_tst_select_proc");
        if (proc != null && proc.getDataMap() == map) {
            executeDDL(con, "sybase", "drop-select-sp.sql");
            executeDDL(con, "sybase", "drop-update-sp.sql");
            executeDDL(con, "sybase", "drop-out-sp.sql");
        }
    }

    protected void dropConstraints(Connection con, String tableName) throws Exception {
        List names = new ArrayList(3);
        Statement select = con.createStatement();

        try {
            ResultSet rs = select.executeQuery("SELECT t0.name "
                    + "FROM sysobjects t0, sysconstraints t1, sysobjects t2 "
                    + "WHERE t0.id = t1.constrid and t1.tableid = t2.id and t2.name = '"
                    + tableName
                    + "'");
            try {

                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            select.close();
        }

        for (int i = 0; i < names.size(); i++) {
            executeDDL(con, "alter table "
                    + tableName
                    + " drop constraint "
                    + names.get(i));
        }
    }

    public boolean supportsLobs() {
        return true;
    }

    public boolean handlesNullVsEmptyLOBs() {
        // TODO Sybase handling of this must be fixed
        return false;
    }

}