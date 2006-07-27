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

package org.apache.cayenne.dba.db2;

import junit.framework.TestCase;

import org.apache.cayenne.access.jdbc.SQLTemplateAction;
import org.apache.cayenne.dba.MockDbAdapter;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.MockSQLAction;
import org.apache.cayenne.query.SQLAction;
import org.apache.cayenne.query.SQLTemplate;

/**
 * @author Andrus Adamchik
 */
public class DB2ActionBuilderTst extends TestCase {

    public void testInterceptRawSQL() {
        DB2ActionBuilder builder = new DB2ActionBuilder(
                new MockDbAdapter(),
                new EntityResolver());

        SQLAction action = new MockSQLAction();
        assertSame(action, builder.interceptRawSQL(action));

        SQLTemplateAction rawSQLAction = new SQLTemplateAction(new SQLTemplate(), builder
                .getAdapter());

        rawSQLAction.setRemovingLineBreaks(false);
        assertFalse(rawSQLAction.isRemovingLineBreaks());

        assertSame(rawSQLAction, builder.interceptRawSQL(rawSQLAction));
        assertTrue(rawSQLAction.isRemovingLineBreaks());
    }

    public void testSqlAction() {
        DB2ActionBuilder builder = new DB2ActionBuilder(
                new MockDbAdapter(),
                new EntityResolver());

        SQLAction action = builder.sqlAction(new SQLTemplate());

        assertTrue(action instanceof SQLTemplateAction);
        assertTrue(((SQLTemplateAction) action).isRemovingLineBreaks());
    }

    public void testUpdateAction() {
        DB2ActionBuilder builder = new DB2ActionBuilder(
                new MockDbAdapter(),
                new EntityResolver());

        SQLAction action = builder.updateAction(new SQLTemplate());

        assertTrue(action instanceof SQLTemplateAction);
        assertTrue(((SQLTemplateAction) action).isRemovingLineBreaks());
    }
}
