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

import org.objectstyle.cayenne.access.ClientServerChannel;
import org.objectstyle.cayenne.remote.ClientChannel;
import org.objectstyle.cayenne.remote.service.LocalConnection;
import org.objectstyle.cayenne.testdo.mt.ClientMtTable1;
import org.objectstyle.cayenne.testdo.mt.ClientMtTable2;
import org.objectstyle.cayenne.unit.AccessStack;
import org.objectstyle.cayenne.unit.CayenneTestCase;
import org.objectstyle.cayenne.unit.CayenneTestResources;
import org.objectstyle.cayenne.validation.ValidationException;

public class CayenneContextValidationTst extends CayenneTestCase {

    protected AccessStack buildAccessStack() {
        return CayenneTestResources
                .getResources()
                .getAccessStack(MULTI_TIER_ACCESS_STACK);
    }

    public void testValidate() throws Exception {

        deleteTestData();
        DataChannel serverChannel = new ClientServerChannel(getDomain(), false);
        ClientChannel clientChannel = new ClientChannel(
                new LocalConnection(serverChannel),
                true);

        CayenneContext c = new CayenneContext(clientChannel);
        
        ClientMtTable1 o1 = (ClientMtTable1) c.newObject(ClientMtTable1.class);
        o1.setGlobalAttribute1("G1");
        o1.resetValidation(false);
        
        // this one is not validating
        ClientMtTable2 o2 = (ClientMtTable2) c.newObject(ClientMtTable2.class);
        o2.setTable1(o1);
        
        c.commitChanges();
        assertTrue(o1.isValidatedForInsert());
        assertFalse(o1.isValidatedForDelete());
        assertFalse(o1.isValidatedForUpdate());
        
        o1.resetValidation(false);
        o1.setGlobalAttribute1("G2");
        
        c.commitChanges();
        assertFalse(o1.isValidatedForInsert());
        assertFalse(o1.isValidatedForDelete());
        assertTrue(o1.isValidatedForUpdate());
        
        o1.resetValidation(false);
        c.deleteObject(o1);
        
        c.commitChanges();
        assertFalse(o1.isValidatedForInsert());
        assertTrue(o1.isValidatedForDelete());
        assertFalse(o1.isValidatedForUpdate());
        
        ClientMtTable1 o11 = (ClientMtTable1) c.newObject(ClientMtTable1.class);
        o11.setGlobalAttribute1("G1");
        o11.resetValidation(true);
        
        try {
            c.commitChanges();
            fail("Validation failure must have prevented commit");
        }
        catch (ValidationException e) {
           // expected
        }
    }
}