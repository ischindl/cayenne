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
package org.objectstyle.cayenne.access;

import java.util.Collections;
import java.util.List;

import org.objectstyle.art.ArtGroup;
import org.objectstyle.art.Artist;
import org.objectstyle.art.Painting;
import org.objectstyle.art.PaintingInfo;
import org.objectstyle.cayenne.DataObjectUtils;
import org.objectstyle.cayenne.PersistenceState;
import org.objectstyle.cayenne.query.SelectQuery;
import org.objectstyle.cayenne.unit.CayenneTestCase;

public class NestedDataContextWriteTst extends CayenneTestCase {

    public void testCommitChangesToParent() throws Exception {
        deleteTestData();
        createTestData("testFlushChanges");

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        // make sure we fetch in predictable order
        SelectQuery query = new SelectQuery(Artist.class);
        query.addOrdering(Artist.ARTIST_NAME_PROPERTY, true);
        List objects = childContext.performQuery(query);

        assertEquals(4, objects.size());

        Artist childNew = (Artist) childContext.newObject(Artist.class);
        childNew.setArtistName("NNN");

        Artist childModified = (Artist) objects.get(0);
        childModified.setArtistName("MMM");

        Artist childCommitted = (Artist) objects.get(1);

        Artist childHollow = (Artist) objects.get(3);
        childContext.invalidateObjects(Collections.singleton(childHollow));

        blockQueries();

        try {
            childContext.commitChangesToParent();

            // * all modified child objects must be in committed state now
            // * all modifications should be propagated to the parent
            // * no actual commit should occur.

            assertEquals(PersistenceState.COMMITTED, childNew.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childModified.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childCommitted.getPersistenceState());
            assertEquals(PersistenceState.HOLLOW, childHollow.getPersistenceState());

            Artist parentNew = (Artist) context.getObjectStore().getObject(
                    childNew.getObjectId());
            Artist parentModified = (Artist) context.getObjectStore().getObject(
                    childModified.getObjectId());
            Artist parentCommitted = (Artist) context.getObjectStore().getObject(
                    childCommitted.getObjectId());
            Artist parentHollow = (Artist) context.getObjectStore().getObject(
                    childHollow.getObjectId());

            assertNotNull(parentNew);
            assertEquals(PersistenceState.NEW, parentNew.getPersistenceState());
            assertEquals("NNN", parentNew.getArtistName());

            assertNotNull(parentModified);
            assertEquals(PersistenceState.MODIFIED, parentModified.getPersistenceState());
            assertEquals("MMM", parentModified.getArtistName());
            assertNotNull(context.getObjectStore().getChangesByObjectId().get(
                    parentModified.getObjectId()));

            assertNotNull(parentCommitted);
            assertEquals(PersistenceState.COMMITTED, parentCommitted
                    .getPersistenceState());

            assertNotNull(parentHollow);
            // TODO: we can assert that when we figure out how nested "invalidate" should
            // work
            // assertEquals(PersistenceState.HOLLOW, parentHollow.getPersistenceState());
        }
        finally {
            unblockQueries();
        }
    }

    public void testCommitChangesToParentDeleted() throws Exception {
        deleteTestData();
        createTestData("testFlushChanges");

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        // make sure we fetch in predictable order
        SelectQuery query = new SelectQuery(Artist.class);
        query.addOrdering(Artist.ARTIST_NAME_PROPERTY, true);
        List objects = childContext.performQuery(query);

        assertEquals(4, objects.size());

        // delete AND modify
        Artist childDeleted = (Artist) objects.get(2);
        childContext.deleteObject(childDeleted);
        childDeleted.setArtistName("DDD");

        // don't block queries - on delete Cayenne may need to resolve delete rules via
        // fetch
        childContext.commitChangesToParent();

        // * all modified child objects must be in committed state now
        // * all modifications should be propagated to the parent
        // * no actual commit should occur.

        assertEquals(PersistenceState.TRANSIENT, childDeleted.getPersistenceState());

        Artist parentDeleted = (Artist) context.getObjectStore().getObject(
                childDeleted.getObjectId());

        assertNotNull(parentDeleted);
        assertEquals(PersistenceState.DELETED, parentDeleted.getPersistenceState());
        assertEquals("DDD", parentDeleted.getArtistName());
    }

    public void testCommitChanges() throws Exception {
        deleteTestData();
        createTestData("testFlushChanges");

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        // make sure we fetch in predictable order
        SelectQuery query = new SelectQuery(Artist.class);
        query.addOrdering(Artist.ARTIST_NAME_PROPERTY, true);
        List objects = childContext.performQuery(query);

        assertEquals(4, objects.size());

        Artist childNew = (Artist) childContext.newObject(Artist.class);
        childNew.setArtistName("NNN");

        Artist childModified = (Artist) objects.get(0);
        childModified.setArtistName("MMM");

        Artist childCommitted = (Artist) objects.get(1);

        // delete AND modify
        Artist childDeleted = (Artist) objects.get(2);
        childContext.deleteObject(childDeleted);
        childDeleted.setArtistName("DDD");

        Artist childHollow = (Artist) objects.get(3);
        childContext.invalidateObjects(Collections.singleton(childHollow));

        childContext.commitChanges();

        assertEquals(PersistenceState.COMMITTED, childNew.getPersistenceState());
        assertEquals(PersistenceState.COMMITTED, childModified.getPersistenceState());
        assertEquals(PersistenceState.COMMITTED, childCommitted.getPersistenceState());
        assertEquals(PersistenceState.TRANSIENT, childDeleted.getPersistenceState());
        assertEquals(PersistenceState.HOLLOW, childHollow.getPersistenceState());

        Artist parentNew = (Artist) context.getObjectStore().getObject(
                childNew.getObjectId());
        Artist parentModified = (Artist) context.getObjectStore().getObject(
                childModified.getObjectId());
        Artist parentCommitted = (Artist) context.getObjectStore().getObject(
                childCommitted.getObjectId());
        Artist parentDeleted = (Artist) context.getObjectStore().getObject(
                childDeleted.getObjectId());
        Artist parentHollow = (Artist) context.getObjectStore().getObject(
                childHollow.getObjectId());

        assertNotNull(parentNew);
        assertEquals(PersistenceState.COMMITTED, parentNew.getPersistenceState());
        assertEquals("NNN", parentNew.getArtistName());

        assertNotNull(parentModified);
        assertEquals(PersistenceState.COMMITTED, parentModified.getPersistenceState());
        assertEquals("MMM", parentModified.getArtistName());
        assertNull(context.getObjectStore().getChangesByObjectId().get(
                parentModified.getObjectId()));

        assertNull("Deleted object should not be registered.", parentDeleted);

        assertNotNull(parentCommitted);
        assertEquals(PersistenceState.COMMITTED, parentCommitted.getPersistenceState());

        assertNotNull(parentHollow);
    }

    public void testCommitChangesToParent_MergeProperties() throws Exception {
        deleteTestData();
        createTestData("testCommitChangesToParent_MergeProperties");

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        // make sure we fetch in predictable order
        SelectQuery query = new SelectQuery(Painting.class);
        query.addOrdering(Painting.PAINTING_TITLE_PROPERTY, true);
        List objects = childContext.performQuery(query);

        assertEquals(6, objects.size());

        Painting childModifiedSimple = (Painting) objects.get(0);
        childModifiedSimple.setPaintingTitle("C_PT");

        Painting childModifiedToOne = (Painting) objects.get(1);
        childModifiedToOne.setToArtist(childModifiedSimple.getToArtist());

        Artist childModifiedToMany = ((Painting) objects.get(2)).getToArtist();

        // ensure painting array is fully resolved...
        childModifiedToMany.getPaintingArray().size();
        childModifiedToMany.addToPaintingArray((Painting) objects.get(3));

        blockQueries();

        Painting parentModifiedSimple = null;
        Artist parentModifiedToMany = null;
        try {

            childContext.commitChangesToParent();

            assertEquals(PersistenceState.COMMITTED, childModifiedSimple
                    .getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childModifiedToOne
                    .getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childModifiedToMany
                    .getPersistenceState());

            parentModifiedSimple = (Painting) context.getObjectStore().getObject(
                    childModifiedSimple.getObjectId());

            Painting parentModifiedToOne = (Painting) context.getObjectStore().getObject(
                    childModifiedToOne.getObjectId());

            parentModifiedToMany = (Artist) context.getObjectStore().getObject(
                    childModifiedToMany.getObjectId());

            assertNotNull(parentModifiedSimple);
            assertEquals(PersistenceState.MODIFIED, parentModifiedSimple
                    .getPersistenceState());
            assertEquals("C_PT", parentModifiedSimple.getPaintingTitle());
            assertNotNull(context.getObjectStore().getChangesByObjectId().get(
                    parentModifiedSimple.getObjectId()));

            assertNotNull(parentModifiedToOne);
            assertEquals(PersistenceState.MODIFIED, parentModifiedToOne
                    .getPersistenceState());
            assertNotNull(parentModifiedToOne.getToArtist());
            assertEquals(33001, DataObjectUtils.intPKForObject(parentModifiedToOne
                    .getToArtist()));
            assertNotNull(context.getObjectStore().getChangesByObjectId().get(
                    parentModifiedToOne.getObjectId()));

            // indirectly modified....
            assertNotNull(parentModifiedToMany);
            assertEquals(PersistenceState.MODIFIED, parentModifiedToMany
                    .getPersistenceState());
        }
        finally {
            unblockQueries();
        }

        // here query is expected, as the parent was hollow and its to-many relationship
        // is unresolved
        List paintings = parentModifiedToMany.getPaintingArray();
        assertEquals(2, paintings.size());
    }

    public void testCommitChangesToParentPropagatedKey() throws Exception {
        deleteTestData();

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        Painting childMaster = (Painting) childContext.newObject(Painting.class);
        childMaster.setPaintingTitle("Master");

        PaintingInfo childDetail1 = (PaintingInfo) childContext
                .newObject(PaintingInfo.class);
        childDetail1.setTextReview("Detail1");
        childDetail1.setPainting(childMaster);

        try {
            childContext.commitChangesToParent();

            assertEquals(PersistenceState.COMMITTED, childMaster.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childDetail1.getPersistenceState());

            Painting parentMaster = (Painting) context.getObjectStore().getObject(
                    childMaster.getObjectId());

            assertNotNull(parentMaster);
            assertEquals(PersistenceState.NEW, parentMaster.getPersistenceState());

            PaintingInfo parentDetail1 = (PaintingInfo) context
                    .getObjectStore()
                    .getObject(childDetail1.getObjectId());

            assertNotNull(parentDetail1);
            assertEquals(PersistenceState.NEW, parentDetail1.getPersistenceState());

            assertSame(parentMaster, parentDetail1.getPainting());
            assertSame(parentDetail1, parentMaster.getToPaintingInfo());
        }
        finally {
            unblockQueries();
        }
    }

    public void testCommitChangesToParentFlattened() throws Exception {
        deleteTestData();

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        Artist childO1 = (Artist) childContext.newObject(Artist.class);
        childO1.setArtistName("Master");

        ArtGroup childO2 = (ArtGroup) childContext.newObject(ArtGroup.class);
        childO2.setName("Detail1");
        childO2.addToArtistArray(childO1);

        assertEquals(1, childO1.getGroupArray().size());
        assertEquals(1, childO2.getArtistArray().size());

        try {
            childContext.commitChangesToParent();

            assertEquals(PersistenceState.COMMITTED, childO1.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childO2.getPersistenceState());

            Artist parentO1 = (Artist) context.getObjectStore().getObject(
                    childO1.getObjectId());

            assertNotNull(parentO1);
            assertEquals(PersistenceState.NEW, parentO1.getPersistenceState());

            ArtGroup parentO2 = (ArtGroup) context.getObjectStore().getObject(
                    childO2.getObjectId());

            assertNotNull(parentO2);
            assertEquals(PersistenceState.NEW, parentO2.getPersistenceState());

            assertEquals(1, parentO1.getGroupArray().size());
            assertEquals(1, parentO2.getArtistArray().size());
            assertTrue(parentO2.getArtistArray().contains(parentO1));
            assertTrue(parentO1.getGroupArray().contains(parentO2));
        }
        finally {
            unblockQueries();
        }
    }

    public void testCommitChangesToParentFlattenedMultipleFlush() throws Exception {
        deleteTestData();

        DataContext context = createDataContext();
        DataContext childContext = context.createChildDataContext();

        Artist childO1 = (Artist) childContext.newObject(Artist.class);
        childO1.setArtistName("o1");

        ArtGroup childO2 = (ArtGroup) childContext.newObject(ArtGroup.class);
        childO2.setName("o2");
        childO2.addToArtistArray(childO1);

        childContext.commitChangesToParent();

        ArtGroup childO3 = (ArtGroup) childContext.newObject(ArtGroup.class);
        childO3.setName("o3");
        childO1.addToGroupArray(childO3);

        assertEquals(2, childO1.getGroupArray().size());
        assertEquals(1, childO2.getArtistArray().size());
        assertEquals(1, childO3.getArtistArray().size());

        try {
            childContext.commitChangesToParent();

            assertEquals(PersistenceState.COMMITTED, childO1.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childO2.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childO3.getPersistenceState());

            Artist parentO1 = (Artist) context.getObjectStore().getObject(
                    childO1.getObjectId());

            assertNotNull(parentO1);
            assertEquals(PersistenceState.NEW, parentO1.getPersistenceState());

            ArtGroup parentO2 = (ArtGroup) context.getObjectStore().getObject(
                    childO2.getObjectId());

            assertNotNull(parentO2);
            assertEquals(PersistenceState.NEW, parentO2.getPersistenceState());

            ArtGroup parentO3 = (ArtGroup) context.getObjectStore().getObject(
                    childO3.getObjectId());

            assertNotNull(parentO3);
            assertEquals(PersistenceState.NEW, parentO3.getPersistenceState());

            assertEquals(2, parentO1.getGroupArray().size());
            assertEquals(1, parentO2.getArtistArray().size());
            assertEquals(1, parentO3.getArtistArray().size());
            assertTrue(parentO2.getArtistArray().contains(parentO1));
            assertTrue(parentO3.getArtistArray().contains(parentO1));
            assertTrue(parentO1.getGroupArray().contains(parentO2));
            assertTrue(parentO1.getGroupArray().contains(parentO3));
        }
        finally {
            unblockQueries();
        }

        childO1.removeFromGroupArray(childO2);

        try {
            childContext.commitChangesToParent();

            assertEquals(PersistenceState.COMMITTED, childO1.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childO2.getPersistenceState());
            assertEquals(PersistenceState.COMMITTED, childO3.getPersistenceState());

            Artist parentO1 = (Artist) context.getObjectStore().getObject(
                    childO1.getObjectId());

            assertNotNull(parentO1);
            assertEquals(PersistenceState.NEW, parentO1.getPersistenceState());

            ArtGroup parentO2 = (ArtGroup) context.getObjectStore().getObject(
                    childO2.getObjectId());

            assertNotNull(parentO2);
            assertEquals(PersistenceState.NEW, parentO2.getPersistenceState());

            ArtGroup parentO3 = (ArtGroup) context.getObjectStore().getObject(
                    childO3.getObjectId());

            assertNotNull(parentO3);
            assertEquals(PersistenceState.NEW, parentO3.getPersistenceState());

            assertEquals(1, parentO1.getGroupArray().size());
            assertEquals(0, parentO2.getArtistArray().size());
            assertEquals(1, parentO3.getArtistArray().size());

            assertTrue(parentO3.getArtistArray().contains(parentO1));
            assertTrue(parentO1.getGroupArray().contains(parentO3));
        }
        finally {
            unblockQueries();
        }
    }
}
