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

package org.objectstyle.cayenne.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.objectstyle.cayenne.gui.Editor;
import org.objectstyle.cayenne.gui.event.Mediator;
import org.objectstyle.cayenne.gui.util.CayenneToolbarButton;
import org.objectstyle.cayenne.gui.util.GUIErrorHandler;

/**
 * Superclass of CayenneModeler actions that implements support 
 * for some common functionality, exception handling, etc.
 * 
 * @author Andrei Adamchik
 */
public abstract class CayenneAction extends AbstractAction {
	/** Defines path to the images. */
	public static final String RESOURCE_PATH = "org/objectstyle/cayenne/gui/";

	public CayenneAction(String name) {
		super(name);
		super.putValue(Action.DEFAULT, name);

		Icon icon = createIcon();
		if (icon != null) {
			super.putValue(Action.SMALL_ICON, icon);
		}

		KeyStroke accelerator = getAcceleratorKey();
		if (accelerator != null) {
			super.putValue(Action.ACCELERATOR_KEY, accelerator);
		}
		
		setEnabled(false);
	}

	/** 
	 * Changes the name of this action, propagating the change
	 * to all widgets using this action.
	 */
	public void setName(String newName) {
		super.putValue(Action.NAME, newName);
	}

	/**
	 * Returns keyboard shortcut for this action. Default
	 * implementation returns <code>null</code>.
	 */
	public KeyStroke getAcceleratorKey() {
		return null;
	}

	/**
	 * Returns the name of the icon that should be used
	 * for buttons. Name will be reolved relative to
	 * <code>RESOURCE_PATH</code>. Default implementation
	 * returns <code>null</code>.
	 */
	public String getIconName() {
		return null;
	}

	/**
	 * Creates and returns an ImageIcon that can be used 
	 * for buttons that rely on this action.
	 * Returns <code>null</code> if <code>getIconName</code>
	 * returns <code>null</code>.
	 */
	public Icon createIcon() {
		String name = getIconName();
		return (name != null)
			? new ImageIcon(
				getClass().getClassLoader().getResource(RESOURCE_PATH + name))
			: null;
	}

	/**
	 * Returns the key under which this action should be stored in the
	 * ActionMap.
	 */
	public String getKey() {
		return (String) super.getValue(Action.DEFAULT);
	}

	/**
	 * Subclasses must implement this method instead of <code>actionPerformed</code>
	 * to allow for exception handling.
	 */
	public abstract void performAction(ActionEvent e);

	/** 
	 * Returns shared CayenneModeler mediator.
	 */
	public Mediator getMediator() {
		return Editor.getFrame().getMediator();
	}
	
	public void setMediator(Mediator mediator) {
		Editor.getFrame().setMediator(mediator);
	}

	/**
	 * Internally calls <code>performAction</code>.
	 * Traps exceptions that ocurred during action processing.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			performAction(e);
		} catch (Throwable th) {
			GUIErrorHandler.guiException(th);
		}
	}

	/**
	 * Factory method that creates a menu item hooked up
	 * to this action.
	 */
	public JMenuItem buildMenu() {
		return new JMenuItem(this);
	}

	/**
	 * Factory method that creates a button hooked up
	 * to this action.
	 */
	public JButton buildButton() {
		return new CayenneToolbarButton(this);
	}
}
