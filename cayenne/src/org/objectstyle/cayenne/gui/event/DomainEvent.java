package org.objectstyle.cayenne.gui.event;
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

import java.util.*;
import org.objectstyle.cayenne.map.*;
import org.objectstyle.cayenne.access.*;


/** Events pertaining to Domain status change. 
  * We don't need to store Domain information in this type of
    event as it is always currentDomain. */
public class DomainEvent extends EventObject
{
	/** Domain changed. Display or re-display it.*/
	public static final int CHANGE 	= 1;
	/** New domain created. Display it or add to the list, if applicable.*/
	public static final int ADD		= 2;
	/** Domain removed. Display another one and/or remove this one from lists.*/
	public static final int REMOVE	= 3;
	
	private int	id = CHANGE;
	private DataDomain domain;
	private String oldName;
	private String newName;
	
	/** Domain property(-ies) changed. */
	public DomainEvent(Object src, DataDomain temp_domain)
	{
		super(src);
		domain = temp_domain;
		oldName = newName = temp_domain.getName();
	}

	/** DataDomain added or removed. */
	public DomainEvent(Object src, DataDomain temp_domain, int temp_id)
	{
		this(src, temp_domain);
		id = temp_id;
	}

	/** DataDomain name changed.*/
	public DomainEvent(Object src, DataDomain temp_domain, String old_name)
	{
		this(src, temp_domain, CHANGE);
		oldName = old_name;
	}
	
	/** Get domain (obj or db). */
	public DataDomain getDomain() {return domain;}
	/** Get the type of the event.
	 *  @return CHANGE, ADD or REMOVE.*/
	public int getId() {return id;}
	/** Return the old domain name. Used only in CHANGE event. */
	public String getOldName() {return oldName;}
	
	/** Returns the new domain name. Used only in CHANGE event.*/
	public String getNewName() {return newName;}
}