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
package org.objectstyle.cayenne.gui.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectstyle.cayenne.map.*;

/** 
 * Provides utility methods to access DataMap, Entities, etc.
 * For example, setName() in Attribute requires changing
 * the keys in attribute Maps in Entities. 
 * 
 * @author Misha Sengaout
 * @author Andrei Adamchik
 */
public class MapUtil {

	public static void setObjEntityName(
		DataMap map,
		ObjEntity entity,
		String new_name) {
		String old_name = entity.getName();
		// If name hasnt change, just return
		if (old_name != null && old_name.equals(new_name)) {
			return;
		}
		entity.setName(new_name);
		map.removeObjEntity(old_name);
		map.addObjEntity(entity);
	}

	public static void setDbEntityName(
		DataMap map,
		DbEntity entity,
		String new_name) {
		String old_name = entity.getName();
		// If name hasnt change, just return
		if (old_name != null && old_name.equals(new_name)) {
			return;
		}
		entity.setName(new_name);
		map.removeDbEntity(old_name);
		map.addDbEntity(entity);
	}

	/** Changes the name of the attribute in all places in DataMap. */
	public static void setAttributeName(Attribute attrib, String newName) {

		Entity entity = attrib.getEntity();
		entity.removeAttribute(attrib.getName());
		attrib.setName(newName);
		entity.addAttribute(attrib);
	}

	/** Changes the name of the attribute in all places in DataMap. */
	public static void setRelationshipName(
		Entity entity,
		Relationship rel,
		String newName) {

		if (rel == null || rel != entity.getRelationship(rel.getName())) {
			return;
		}

		entity.removeRelationship(rel.getName());
		rel.setName(newName);
		entity.addRelationship(rel);
	}

	/**
	 * Cleans any mappings of ObjEntities, ObjAttributes, 
	 * ObjRelationship to the corresponding Db* objects that not longer
	 * exist.
	 */
	public static void cleanObjMappings(DataMap map) {
		Iterator ents = map.getObjEntitiesAsList().iterator();
		while (ents.hasNext()) {
			ObjEntity ent = (ObjEntity) ents.next();
			DbEntity dbEnt = ent.getDbEntity();

			// the whole entity mapping is invalid
			if (dbEnt != null && map.getDbEntity(dbEnt.getName()) != dbEnt) {
				clearDbMapping(ent);
				continue;
			}

			// check indiv. attributes
			Iterator atts = ent.getAttributeList().iterator();
			while (atts.hasNext()) {
				ObjAttribute att = (ObjAttribute) atts.next();
				DbAttribute dbAtt = att.getDbAttribute();
				if (dbAtt != null) {
					if (dbEnt.getAttribute(dbAtt.getName()) != dbAtt) {
						att.setDbAttribute(null);
					}
				}
			}

			// check indiv. relationships
			Iterator rels = ent.getRelationshipList().iterator();
			while (rels.hasNext()) {
				ObjRelationship rel = (ObjRelationship) rels.next();

				Iterator dbRels =
					new ArrayList(rel.getDbRelationshipList()).iterator();
				while (dbRels.hasNext()) {
					DbRelationship dbRel = (DbRelationship) dbRels.next();
					Entity srcEnt = dbRel.getSourceEntity();
					if (srcEnt == null
						|| map.getDbEntity(srcEnt.getName()) != srcEnt
						|| srcEnt.getRelationship(dbRel.getName()) != dbRel) {
						rel.removeDbRelationship(dbRel);
					}
				}
			}

		}
	}

	/** 
	 * Clears all the mapping between this obj entity and 
	 * its current db entity. Clears mapping between 
	 * entities, attributes and relationships. 
	 */
	public static void clearDbMapping(ObjEntity entity) {
		DbEntity db_entity = entity.getDbEntity();
		if (db_entity == null) {
			return;
		}

		Iterator it = entity.getAttributeMap().values().iterator();
		while (it.hasNext()) {
			ObjAttribute objAttr = (ObjAttribute) it.next();
			DbAttribute dbAttr = objAttr.getDbAttribute();
			if (null != dbAttr) {
				objAttr.setDbAttribute(null);
			}
		}

		Iterator rel_it = entity.getRelationshipList().iterator();
		while (rel_it.hasNext()) {
			ObjRelationship obj_rel = (ObjRelationship) rel_it.next();
			obj_rel.clearDbRelationships();
		}
		entity.setDbEntity(null);
	}
}