/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2004, Andrei (Andrus) Adamchik and individual authors
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
package org.objectstyle.cayenne.modeler.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.map.DbEntity;
import org.objectstyle.cayenne.map.DbJoin;
import org.objectstyle.cayenne.map.DbRelationship;
import org.objectstyle.cayenne.map.Relationship;
import org.objectstyle.cayenne.map.event.RelationshipEvent;
import org.objectstyle.cayenne.modeler.CayenneModelerFrame;
import org.objectstyle.cayenne.modeler.PanelFactory;
import org.objectstyle.cayenne.modeler.util.CayenneDialog;
import org.objectstyle.cayenne.modeler.util.CayenneTable;
import org.objectstyle.cayenne.modeler.util.CayenneWidgetFactory;
import org.objectstyle.cayenne.modeler.util.MapUtil;
import org.objectstyle.cayenne.modeler.util.ModelerUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Editor of DbRelationship joins.
 */
public class ResolveDbRelationshipDialog extends CayenneDialog {

    protected DbRelationship relationship;

    protected JLabel reverseNameLabel;
    protected JLabel reverseCheckLabel;
    protected JTextField name;
    protected JTextField reverseName;
    protected JCheckBox hasReverseDbRel;
    protected CayenneTable table;
    protected JButton addButton;
    protected JButton removeButton;
    protected JButton saveButton;
    protected JButton cancelButton;

    private boolean cancelPressed;

    public ResolveDbRelationshipDialog(DbRelationship relationship) {

        super(CayenneModelerFrame.getFrame(), "", true);

        initView();
        initController();
        initWithModel(relationship);

        this.pack();
        this.centerWindow();
    }

    /**
     * Creates graphical components.
     */
    private void initView() {

        // create widgets
        reverseNameLabel = CayenneWidgetFactory.createLabel("Reverse Relationship:");
        reverseCheckLabel = CayenneWidgetFactory.createLabel("Create Reverse:");
        name = CayenneWidgetFactory.createTextField(25);
        reverseName = CayenneWidgetFactory.createTextField(25);
        hasReverseDbRel = new JCheckBox("", false);
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        saveButton = new JButton("Done");
        cancelButton = new JButton("Cancel");

        table = new AttributeTable();
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // assemble
        getContentPane().setLayout(new BorderLayout());

        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(
                new FormLayout(
                        "right:max(50dlu;pref), 3dlu, fill:min(150dlu;pref), 3dlu, fill:min(150dlu;pref)",
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, top:14dlu, 3dlu, top:p:grow"));
        builder.setDefaultDialogBorder();

        builder.addSeparator("DbRelationship Information", cc.xywh(1, 1, 5, 1));
        builder.addLabel("Relationship:", cc.xy(1, 3));
        builder.add(name, cc.xywh(3, 3, 1, 1));
        builder.add(reverseNameLabel, cc.xy(1, 5));
        builder.add(reverseName, cc.xywh(3, 5, 1, 1));
        builder.add(reverseCheckLabel, cc.xy(1, 7));
        builder.add(hasReverseDbRel, cc.xywh(3, 7, 1, 1));

        builder.addSeparator("Joins", cc.xywh(1, 9, 5, 1));
        builder.add(new JScrollPane(table), cc.xywh(1, 11, 3, 3));
        builder.add(addButton, cc.xywh(5, 11, 1, 1));
        builder.add(removeButton, cc.xywh(5, 13, 1, 1));

        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
        getContentPane().add(PanelFactory.createButtonPanel(new JButton[] {
                saveButton, cancelButton
        }), BorderLayout.SOUTH);
    }

    private void initWithModel(DbRelationship relationship) {

        // sanity check
        if (relationship.getSourceEntity() == null) {
            throw new CayenneRuntimeException("Null source entity: " + relationship);
        }

        if (relationship.getTargetEntity() == null) {
            throw new CayenneRuntimeException("Null target entity: " + relationship);
        }

        if (relationship.getSourceEntity().getDataMap() == null) {
            throw new CayenneRuntimeException("Null DataMap: "
                    + relationship.getSourceEntity());
        }

        this.relationship = relationship;
        DbRelationship reverseRelationship = relationship.getReverseRelationship();

        if (reverseRelationship != null) {
            reverseCheckLabel.setText("Update Reverse:");
        }

        // init UI components
        setTitle("DbRelationship Info: "
                + relationship.getSourceEntity().getName()
                + " to "
                + relationship.getTargetEntityName());

        table.setModel(new DbJoinTableModel(relationship, getMediator(), this, true));
        TableColumn sourceColumn = table.getColumnModel().getColumn(
                DbJoinTableModel.SOURCE);
        sourceColumn.setMinWidth(150);
        JComboBox comboBox = CayenneWidgetFactory.createComboBox(ModelerUtil
                .getDbAttributeNames(getMediator(), (DbEntity) relationship
                        .getSourceEntity()), true);
        comboBox.setEditable(false);
        sourceColumn.setCellEditor(new DefaultCellEditor(comboBox));

        TableColumn targetColumn = table.getColumnModel().getColumn(
                DbJoinTableModel.TARGET);
        targetColumn.setMinWidth(150);
        comboBox = CayenneWidgetFactory.createComboBox(ModelerUtil.getDbAttributeNames(
                getMediator(),
                (DbEntity) relationship.getTargetEntity()), true);
        comboBox.setEditable(false);
        targetColumn.setCellEditor(new DefaultCellEditor(comboBox));

        if (reverseRelationship == null) {
            reverseName.setText(null);
            reverseName.setEnabled(false);
            reverseNameLabel.setEnabled(false);
            hasReverseDbRel.setSelected(false);
        }
        else {
            reverseNameLabel.setEnabled(true);
            reverseName.setEnabled(true);
            reverseName.setText(reverseRelationship.getName());
            hasReverseDbRel.setSelected(true);
        }

        name.setText(relationship.getName());
    }

    private void initController() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DbJoinTableModel model = (DbJoinTableModel) table.getModel();
                model.addRow(new DbJoin(relationship));
                table.select(model.getRowCount() - 1);
            }
        });

        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DbJoinTableModel model = (DbJoinTableModel) table.getModel();
                stopEditing();
                int row = table.getSelectedRow();
                model.removeRow(model.getJoin(row));
            }
        });

        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelPressed = false;
                save();
            }
        });

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelPressed = true;
                hide();
            }
        });

        hasReverseDbRel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!hasReverseDbRel.isSelected()) {
                    reverseName.setText(null);
                }

                reverseName.setEnabled(hasReverseDbRel.isSelected());
                reverseNameLabel.setEnabled(hasReverseDbRel.isSelected());
            }
        });
    }

    public boolean isCancelPressed() {
        return cancelPressed;
    }

    private void stopEditing() {
        // Stop whatever editing may be taking place
        int col_index = table.getEditingColumn();
        if (col_index >= 0) {
            TableColumn col = table.getColumnModel().getColumn(col_index);
            col.getCellEditor().stopCellEditing();
        }
    }

    private void save() {
        DbJoinTableModel model = (DbJoinTableModel) table.getModel();
        boolean updatingReverse = hasReverseDbRel.isSelected()
                && model.getObjectList().size() > 0
                && reverseName.getText().trim().length() > 0;

        String reverseName = this.reverseName.getText().trim();

        // check if reverse name is valid
        if (updatingReverse && !validateReverseName(reverseName)) {
            return;
        }

        // handle name update
        if (!name.getText().equals(relationship.getName())) {
            String oldName = relationship.getName();
            MapUtil.setRelationshipName(
                    relationship.getSourceEntity(),
                    relationship,
                    name.getText());

            getMediator().fireDbRelationshipEvent(
                    new RelationshipEvent(this, relationship, relationship
                            .getSourceEntity(), oldName));
        }

        model.commit();

        // check "to dep pk" setting,
        // maybe this is no longer valid
        if (relationship.isToDependentPK() && !relationship.isValidForDepPk()) {
            relationship.setToDependentPK(false);
        }

        // If new reverse DbRelationship was created, add it to the target
        // Don't create reverse with no joins - makes no sense...
        if (updatingReverse) {
            DbRelationship reverseRelationship = relationship.getReverseRelationship();

            // If didn't find anything, create reverseDbRel
            if (reverseRelationship == null) {
                reverseRelationship = new DbRelationship(reverseName);
                reverseRelationship.setSourceEntity(relationship.getTargetEntity());
                reverseRelationship.setTargetEntity(relationship.getSourceEntity());
                reverseRelationship.setToMany(!relationship.isToMany());
                relationship.getTargetEntity().addRelationship(reverseRelationship);

                // fire only if the relationship is to the same entity...
                // this is needed to update entity view...
                if (relationship.getSourceEntity() == relationship.getTargetEntity()) {
                    getMediator().fireDbRelationshipEvent(
                            new RelationshipEvent(
                                    this,
                                    reverseRelationship,
                                    reverseRelationship.getSourceEntity(),
                                    RelationshipEvent.ADD));
                }
            }
            else {
                reverseRelationship.setName(reverseName);
            }

            Collection revJoins = getReverseJoins(reverseRelationship);
            reverseRelationship.setJoins(revJoins);

            // check if joins map to a primary key of this entity
            if (!relationship.isToDependentPK()) {
                Iterator it = revJoins.iterator();
                if (it.hasNext()) {
                    boolean toDepPK = true;
                    while (it.hasNext()) {
                        DbJoin join = (DbJoin) it.next();
                        if (!join.getTarget().isPrimaryKey()) {
                            toDepPK = false;
                            break;
                        }
                    }

                    reverseRelationship.setToDependentPK(toDepPK);
                }
            }

        }

        getMediator()
                .fireDbRelationshipEvent(
                        new RelationshipEvent(this, relationship, relationship
                                .getSourceEntity()));
        hide();
    }

    private boolean validateReverseName(String newName) {
        Relationship existingReverse = relationship.getTargetEntity().getRelationship(
                newName);

        if (existingReverse != null
                && relationship.getReverseRelationship() != existingReverse) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is an existing relationship named \""
                            + newName
                            + "\". Select a different name.");
            return false;
        }

        return true;
    }

    private Collection getReverseJoins(DbRelationship reverse) {
        Collection joins = relationship.getJoins();

        if ((joins == null) || (joins.size() == 0)) {
            return Collections.EMPTY_LIST;
        }

        List reverseJoins = new ArrayList(joins.size());

        // Loop through the list of attribute pairs, create reverse pairs
        // and put them to the reverse list.
        Iterator it = joins.iterator();
        while (it.hasNext()) {
            DbJoin pair = (DbJoin) it.next();
            DbJoin reverseJoin = pair.createReverseJoin();

            // since reverse relationship is not yet initialized,
            // reverse join will not have it set automatically
            reverseJoin.setRelationship(reverse);
            reverseJoins.add(reverseJoin);
        }

        return reverseJoins;
    }

    final class AttributeTable extends CayenneTable {

        final Dimension preferredSize = new Dimension(203, 100);

        public Dimension getPreferredScrollableViewportSize() {
            return preferredSize;
        }
    }
}