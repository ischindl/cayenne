/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002-2004 The ObjectStyle Group 
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
package org.objectstyle.cayenne.access;

import java.util.ArrayList;
import java.util.List;

import org.objectstyle.art.BitTest;
import org.objectstyle.art.SmallintTest;
import org.objectstyle.art.TinyintTest;
import org.objectstyle.cayenne.access.util.DefaultOperationObserver;
import org.objectstyle.cayenne.exp.Expression;
import org.objectstyle.cayenne.exp.ExpressionFactory;
import org.objectstyle.cayenne.query.SelectQuery;
import org.objectstyle.cayenne.query.SqlModifyQuery;
import org.objectstyle.cayenne.unittest.CayenneTestCase;

/**
 * @author Andrei Adamchik
 */
public class NumericTypesTst extends CayenneTestCase {
    protected DataContext context;

    protected void setUp() throws Exception {
        getDatabaseSetup().cleanTableData();
        context = createDataContext();
    }

    public void testShortInQualifier() throws Exception {
        // populate
        List inserts = new ArrayList(2);
        inserts.add(
            new SqlModifyQuery(
                SmallintTest.class,
                "insert into SMALLINT_TEST (ID, SMALLINT_COL) values (1, 9999)"));
        inserts.add(
            new SqlModifyQuery(
                SmallintTest.class,
                "insert into SMALLINT_TEST (ID, SMALLINT_COL) values (2, 3333)"));

        context.performQueries(inserts, new DefaultOperationObserver());

        // test
        Expression qual = ExpressionFactory.matchExp("smallintCol", new Short("9999"));
        List objects = context.performQuery(new SelectQuery(SmallintTest.class, qual));
        assertEquals(1, objects.size());

        SmallintTest object = (SmallintTest) objects.get(0);
        assertEquals(new Short("9999"), object.getSmallintCol());
    }

    public void testShortInInsert() throws Exception {
        SmallintTest object =
            (SmallintTest) context.createAndRegisterNewObject("SmallintTest");
        object.setSmallintCol(new Short("1"));
        context.commitChanges();
    }

    public void testTinyintInQualifier() throws Exception {
        // populate
        List inserts = new ArrayList(2);
        inserts.add(
            new SqlModifyQuery(
                TinyintTest.class,
                "insert into TINYINT_TEST (ID, TINYINT_COL) values (1, 81)"));
        inserts.add(
            new SqlModifyQuery(
                TinyintTest.class,
                "insert into TINYINT_TEST (ID, TINYINT_COL) values (2, 50)"));

        context.performQueries(inserts, new DefaultOperationObserver());

        // test
        Expression qual = ExpressionFactory.matchExp("tinyintCol", new Byte((byte) 81));
        List objects = context.performQuery(new SelectQuery(TinyintTest.class, qual));
        assertEquals(1, objects.size());

        TinyintTest object = (TinyintTest) objects.get(0);
        assertEquals(new Byte((byte) 81), object.getTinyintCol());
    }

    public void testTinyintInInsert() throws Exception {
        TinyintTest object =
            (TinyintTest) context.createAndRegisterNewObject("TinyintTest");
        object.setTinyintCol(new Byte((byte) 1));
        context.commitChanges();
    }

    public void testBooleanBit() throws Exception {

        // populate (testing insert as well)
        BitTest trueObject = (BitTest) context.createAndRegisterNewObject("BitTest");
        trueObject.setBitColumn(Boolean.TRUE);
        BitTest falseObject = (BitTest) context.createAndRegisterNewObject("BitTest");
        falseObject.setBitColumn(Boolean.FALSE);
        context.commitChanges();

        // this will clear cache as a side effect
        context = createDataContext();

        Expression qual = ExpressionFactory.matchExp("bitColumn", Boolean.TRUE);
        List objects = context.performQuery(new SelectQuery(BitTest.class, qual));
        assertEquals(1, objects.size());

        BitTest object = (BitTest) objects.get(0);
        assertEquals(Boolean.TRUE, object.getBitColumn());
    }

}
