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
package org.objectstyle.cayenne.gui.datamap;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;

import org.objectstyle.cayenne.dba.TypesMapping;
import org.objectstyle.cayenne.gui.PanelFactory;
import org.objectstyle.cayenne.gui.event.*;
import org.objectstyle.cayenne.gui.util.CayenneTable;
import org.objectstyle.cayenne.map.*;

/** 
 * Detail view of the DbEntity attributes. 
 * 
 * @author Michael Misha Shengaout 
 * @author Andrei Adamchik
 */
public class DbAttributePane
	extends JPanel
	implements
		DbEntityDisplayListener,
		ListSelectionListener,
		DbAttributeListener,
		ExistingSelectionProcessor,
		ActionListener,
		TableModelListener {

	protected Mediator mediator;
	protected JTable table;
	protected JButton editParams;

	public DbAttributePane(Mediator temp_mediator) {
		super();
		mediator = temp_mediator;
		mediator.addDbEntityDisplayListener(this);
		mediator.addDbAttributeListener(this);

		// Create and layout components
		init();

		editParams.addActionListener(this);
	}

	private void init() {
		setLayout(new BorderLayout());

		// Create table with two columns and no rows.
		table = new CayenneTable();
		editParams = new JButton("Edit Parameters");
		JPanel panel =
			PanelFactory.createTablePanel(table, new JButton[] { editParams });
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editParams) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				DbAttribute attr =
					((DbAttributeTableModel) table.getModel()).getAttribute(
						row);
						
				EditDerivedParamsDialog dialog =
					new EditDerivedParamsDialog((DerivedDbAttribute)attr);
				dialog.show();
				dialog.dispose();
			}
		}
	}

	/**
	 * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
	 */
	public void tableChanged(TableModelEvent e) {
		processExistingSelection();
	}

	public void valueChanged(ListSelectionEvent e) {
		processExistingSelection();
	}

	public void processExistingSelection() {
		DbAttribute att = null;
		if (table.getSelectedRow() >= 0) {
			DbAttributeTableModel model =
				(DbAttributeTableModel) table.getModel();
			att = model.getAttribute(table.getSelectedRow());
			editParams.setEnabled(att instanceof DerivedDbAttribute);
		}
		AttributeDisplayEvent ev =
			new AttributeDisplayEvent(
				this,
				att,
				mediator.getCurrentDbEntity(),
				mediator.getCurrentDataMap(),
				mediator.getCurrentDataDomain());

		mediator.fireDbAttributeDisplayEvent(ev);
	}

	private void stopEditing() {
		// Stop whatever editing may be taking place
		int col_index = table.getEditingColumn();
		if (col_index >= 0) {
			TableColumn col = table.getColumnModel().getColumn(col_index);
			col.getCellEditor().stopCellEditing();
		}
	}

	public void dbAttributeChanged(AttributeEvent e) {
	}

	public void dbAttributeAdded(AttributeEvent e) {
		rebuildTable((DbEntity) e.getEntity());
	}

	public void dbAttributeRemoved(AttributeEvent e) {
		DbAttributeTableModel model = (DbAttributeTableModel) table.getModel();
		model.removeAttribute(e.getAttribute());
	}

	public void currentDbEntityChanged(EntityDisplayEvent e) {
		DbEntity entity = (DbEntity) e.getEntity();
		if (entity != null && e.isEntityChanged()) {
			rebuildTable(entity);
		}
	}

	protected void rebuildTable(DbEntity ent) {
		editParams.setVisible(ent instanceof DerivedDbEntity);
		editParams.setEnabled(false);

		DbAttributeTableModel model =
			(ent instanceof DerivedDbEntity)
				? new DerivedDbAttributeTableModel(ent, mediator, this)
				: new DbAttributeTableModel(ent, mediator, this);
		table.setModel(model);
		table.setRowHeight(25);
		table.setRowMargin(3);
		TableColumn col =
			table.getColumnModel().getColumn(model.nameColumnInd());
		col.setMinWidth(150);

		col = table.getColumnModel().getColumn(model.typeColumnInd());
		col.setMinWidth(90);

		JComboBox comboBox = new JComboBox(TypesMapping.getDatabaseTypes());
		comboBox.setEditable(true);
		col.setCellEditor(new DefaultCellEditor(comboBox));

		table.getSelectionModel().addListSelectionListener(this);
		model.addTableModelListener(this);
	}
}