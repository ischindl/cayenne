
package org.objectstyle.cayenne.query;
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

import org.objectstyle.cayenne.exp.*;
import org.objectstyle.cayenne.map.*;

public class DeleteQuery extends QualifiedQuery {
    /** Creates empty DeleteQuery. */
    public DeleteQuery() {}
    
    private void init(Object root, Expression qualifier) {
    	setRoot(root);
    	setQualifier(qualifier);
    }
    
    /**
     * Creates a DeleteQuery with null qualifier, for the specifed ObjEntity
     * @param root the ObjEntity this DeleteQuery is for.
     */
    public DeleteQuery(ObjEntity root) {
    	this(root, null);
    }
    
     /**
     * Creates a DeleteQuery  for the specifed ObjEntity with the given qualifier
     * @param root the ObjEntity this DeleteQuery is for.
     * @param qualifier an Expression indicating which objects should be deleted
     */
   public DeleteQuery(ObjEntity root, Expression qualifier) {
		init(root, qualifier);
    }
    
     /**
     * Creates a DeleteQuery with null qualifier, for the entity which uses the given class
     * @param root the Class of objects this DeleteQuery is for.
     */
   public DeleteQuery(Class rootClass) {
    	this(rootClass, null);
    }
    
	/**
	 * Creates a DeleteQuery for the entity which uses the given class,  with the given qualifier
	 * @param root the Class of objects this DeleteQuery is for.
     * @param qualifier an Expression indicating which objects should be deleted
     */
   public DeleteQuery(Class rootClass, Expression qualifier) {
    	init(rootClass, qualifier);
    }
    
    /** Creates DeleteQuery with <code>objEntityName</code> parameter. */
    public DeleteQuery(String objEntityName) {
        this(objEntityName, null);
    }
    
    /** Creates DeleteQuery with <code>objEntityName</code> and <code>qualifier</code> parameters. */
    public DeleteQuery(String objEntityName, Expression qualifier) {
		init(objEntityName, qualifier);
    }

    
    public int getQueryType() {
        return DELETE_QUERY;
    }
}
