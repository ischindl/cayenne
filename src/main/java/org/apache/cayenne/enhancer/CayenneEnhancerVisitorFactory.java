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
package org.apache.cayenne.enhancer;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.objectweb.asm.ClassVisitor;

/**
 * A ClassFileTransformer that performs enhancement based on the metadata from Cayenne
 * DataMap. POJOs are enhanced into persistent objects that can be used with Cayenne. More
 * specifically, CayenneEnhancer ensures that the object implements {@link Persistent}
 * interface and invokes callbacks from the accessor methods.
 * 
 * @since 3.0
 * @author Andrus Adamchik
 */
public class CayenneEnhancerVisitorFactory implements EnhancerVisitorFactory {

    protected Map<String, ObjEntity> entitiesByClass;

    public CayenneEnhancerVisitorFactory(EntityResolver entityResolver) {
        indexEntities(entityResolver);
    }

    protected void indexEntities(EntityResolver entityResolver) {
        // EntityResolver doesn't have an index by class name, (let alone using
        // "internal" class names with slashes as keys), so we have to build it
        // manually

        this.entitiesByClass = new HashMap<String, ObjEntity>();
        for (Object object : entityResolver.getObjEntities()) {
            ObjEntity entity = (ObjEntity) object;

            // transform method must use internal class names (a/b/c), however for some
            // reason in some invironments (e.g. Mac, Eclipse) it uses a.b.c. Handle both
            // cases here...
            entitiesByClass.put(entity.getClassName(), entity);
            entitiesByClass.put(entity.getClassName().replace('.', '/'), entity);
        }
    }

    public ClassVisitor createVisitor(String className, ClassVisitor out) {
        ObjEntity entity = entitiesByClass.get(className);
        if (entity == null) {
            return null;
        }

        // create enhancer chain
        PersistentInterfaceVisitor e1 = new PersistentInterfaceVisitor(out);
        PersistentAccessorVisitor e2 = new PersistentAccessorVisitor(e1, entity);
        return e2;
    }
}
