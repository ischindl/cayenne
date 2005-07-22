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
package org.objectstyle.cayenne.map;

import java.util.Collection;
import java.util.Iterator;

import org.objectstyle.cayenne.exp.Expression;
import org.objectstyle.cayenne.exp.ExpressionFactory;
import org.objectstyle.cayenne.unit.CayenneTestCase;

public class EntityTst extends CayenneTestCase {

    public void testName() {
        Entity entity = new MockEntity();
        String tstName = "tst_name";
        entity.setName(tstName);
        assertEquals(tstName, entity.getName());
    }

    public void testAttribute() {
        Entity entity = new MockEntity();
        Attribute attribute = new MockAttribute("tst_name");

        entity.addAttribute(attribute);
        assertSame(attribute, entity.getAttribute(attribute.getName()));

        // attribute must have its entity switched to our entity.
        assertSame(entity, attribute.getEntity());

        // remove attribute
        entity.removeAttribute(attribute.getName());
        assertNull(entity.getAttribute(attribute.getName()));
    }

    public void testRelationship() {
        Entity entity = new MockEntity();
        Relationship rel = new MockRelationship("tst_name");

        entity.addRelationship(rel);
        assertSame(rel, entity.getRelationship(rel.getName()));

        // attribute must have its entity switched to our entity.
        assertSame(entity, rel.getSourceEntity());

        // remove attribute
        entity.removeRelationship(rel.getName());
        assertNull(entity.getRelationship(rel.getName()));
    }

    public void testResolveBadObjPath1() {
        // test invalid expression path
        Expression pathExpr = ExpressionFactory.expressionOfType(Expression.OBJ_PATH);
        pathExpr.setOperand(0, "invalid.invalid");

        // itertator should be returned, but when trying to read 1st component,
        // it should throw an exception....
        ObjEntity galleryEnt = getObjEntity("Gallery");
        Iterator it = galleryEnt.resolvePathComponents(pathExpr);
        assertTrue(it.hasNext());

        try {
            it.next();
            fail();
        }
        catch (Exception ex) {
            // exception expected
        }
    }

    public void testResolveBadObjPath2() {
        // test invalid expression type
        Expression badPathExpr = ExpressionFactory.expressionOfType(Expression.IN);
        badPathExpr.setOperand(0, "a.b.c");
        ObjEntity galleryEnt = getObjEntity("Gallery");

        try {
            galleryEnt.resolvePathComponents(badPathExpr);
            fail();
        }
        catch (Exception ex) {
            // exception expected
        }
    }

    public void testResolveObjPath1() {
        Expression pathExpr = ExpressionFactory.expressionOfType(Expression.OBJ_PATH);
        pathExpr.setOperand(0, "galleryName");

        ObjEntity galleryEnt = getObjEntity("Gallery");
        Iterator it = galleryEnt.resolvePathComponents(pathExpr);

        // iterator must contain a single ObjAttribute
        assertNotNull(it);
        assertTrue(it.hasNext());
        ObjAttribute next = (ObjAttribute) it.next();
        assertNotNull(next);
        assertFalse(it.hasNext());
        assertSame(galleryEnt.getAttribute("galleryName"), next);
    }

    public void testRemoveAttribute() {
        Entity entity = new MockEntity();

        entity.setName("test");
        ObjAttribute attribute1 = new ObjAttribute("a1");
        ObjAttribute attribute2 = new ObjAttribute("a2");

        entity.addAttribute(attribute1);
        entity.addAttribute(attribute2);

        Collection attributes = entity.getAttributes();
        assertEquals(2, attributes.size());

        entity.removeAttribute("a1");
        attributes = entity.getAttributes();
        assertEquals(1, attributes.size());
    }
}
