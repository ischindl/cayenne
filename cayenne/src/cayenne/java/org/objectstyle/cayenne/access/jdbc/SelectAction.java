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
package org.objectstyle.cayenne.access.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.objectstyle.cayenne.access.OperationObserver;
import org.objectstyle.cayenne.access.QueryLogger;
import org.objectstyle.cayenne.access.ResultIterator;
import org.objectstyle.cayenne.access.trans.SelectTranslator;
import org.objectstyle.cayenne.access.util.DistinctResultIterator;
import org.objectstyle.cayenne.dba.DbAdapter;
import org.objectstyle.cayenne.map.EntityResolver;
import org.objectstyle.cayenne.query.SelectQuery;

/**
 * A SQLAction that handles SelectQuery execution.
 * 
 * @since 1.2
 * @author Andrei Adamchik
 */
public class SelectAction extends BaseSQLAction {

    protected SelectQuery query;

    public SelectAction(SelectQuery query, DbAdapter adapter,
            EntityResolver entityResolver) {
        super(adapter, entityResolver);
        this.query = query;
    }

    protected SelectTranslator createTranslator(Connection connection) {
        SelectTranslator translator = new SelectTranslator();
        translator.setQuery(query);
        translator.setAdapter(adapter);
        translator.setEntityResolver(getEntityResolver());
        translator.setConnection(connection);
        return translator;
    }

    public void performAction(Connection connection, OperationObserver observer)
            throws SQLException, Exception {

        long t1 = System.currentTimeMillis();

        SelectTranslator translator = createTranslator(connection);
        PreparedStatement prepStmt = translator.createStatement(query.getLoggingLevel());
        ResultSet rs = prepStmt.executeQuery();

        RowDescriptor descriptor = new RowDescriptor(
                translator.getResultColumns(),
                getAdapter().getExtendedTypes());
        JDBCResultIterator workerIterator = new JDBCResultIterator(
                connection,
                prepStmt,
                rs,
                descriptor,
                query.getFetchLimit());

        ResultIterator it = workerIterator;

        // wrap result iterator if distinct has to be suppressed
        if (translator.isSuppressingDistinct()) {
            it = new DistinctResultIterator(workerIterator, translator.getRootDbEntity());
        }

        // TODO: Should do something about closing ResultSet and PreparedStatement in this
        // method, instead of relying on DefaultResultIterator to do that later

        if (!observer.isIteratedResult()) {
            // note that we don't need to close ResultIterator
            // since "dataRows" will do it internally
            List resultRows = it.dataRows(true);
            QueryLogger.logSelectCount(query.getLoggingLevel(), resultRows.size(), System
                    .currentTimeMillis()
                    - t1);

            observer.nextDataRows(query, resultRows);
        }
        else {
            try {
                workerIterator.setClosingConnection(true);
                observer.nextDataRows(translator.getQuery(), it);
            }
            catch (Exception ex) {
                it.close();
                throw ex;
            }
        }
    }
}