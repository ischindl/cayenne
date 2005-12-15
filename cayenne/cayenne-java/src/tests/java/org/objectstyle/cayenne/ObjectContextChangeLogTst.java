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

import junit.framework.TestCase;

import org.objectstyle.cayenne.graph.CompoundDiff;
import org.objectstyle.cayenne.graph.GraphDiff;
import org.objectstyle.cayenne.graph.NodeCreateOperation;
import org.objectstyle.cayenne.opp.hessian.HessianUtil;
import org.objectstyle.cayenne.util.Util;

public class ObjectContextChangeLogTst extends TestCase {

    public void testReset() {
        ObjectContextChangeLog recorder = new ObjectContextChangeLog();
        assertNotNull(recorder.getDiffs());
        assertTrue(recorder.getDiffs().isNoop());

        recorder.addOperation(new NodeCreateOperation(new Object()));
        assertNotNull(recorder.getDiffs());
        assertFalse(recorder.getDiffs().isNoop());

        recorder.reset();
        assertNotNull(recorder.getDiffs());
        assertTrue(recorder.getDiffs().isNoop());

        // now test that a diff stored before "clear" is not affected by 'clear'
        recorder.addOperation(new NodeCreateOperation(new Object()));
        GraphDiff diff = recorder.getDiffs();
        assertFalse(diff.isNoop());

        recorder.reset();
        assertFalse(diff.isNoop());
    }

    public void testGetDiffs() {
        // assert that after returning, the diffs array won't get modified by operation
        // recorder
        ObjectContextChangeLog recorder = new ObjectContextChangeLog();
        recorder.addOperation(new NodeCreateOperation(new Object()));
        CompoundDiff diff = (CompoundDiff) recorder.getDiffs();
        assertEquals(1, diff.getDiffs().size());

        recorder.addOperation(new NodeCreateOperation(new Object()));
        assertEquals(1, diff.getDiffs().size());

        CompoundDiff diff2 = (CompoundDiff) recorder.getDiffs();
        assertEquals(2, diff2.getDiffs().size());
    }

    public void testGetDiffsSerializable() throws Exception {
        ObjectContextChangeLog recorder = new ObjectContextChangeLog();
        recorder.addOperation(new NodeCreateOperation(new ObjectId("test")));
        CompoundDiff diff = (CompoundDiff) recorder.getDiffs();

        Object clone = Util.cloneViaSerialization(diff);
        assertNotNull(clone);
        assertTrue(clone instanceof CompoundDiff);

        CompoundDiff d1 = (CompoundDiff) clone;
        assertEquals(1, d1.getDiffs().size());
    }

    public void testGetDiffsSerializableWithHessian() throws Exception {
        ObjectContextChangeLog recorder = new ObjectContextChangeLog();
        
        // id must be a serializable object
        recorder.addOperation(new NodeCreateOperation("id-string"));
        CompoundDiff diff = (CompoundDiff) recorder.getDiffs();

        Object clone = HessianUtil.cloneViaHessianSerialization(diff);
        assertNotNull(clone);
        assertTrue(clone instanceof CompoundDiff);

        CompoundDiff d1 = (CompoundDiff) clone;
        assertEquals(1, d1.getDiffs().size());
    }
}
