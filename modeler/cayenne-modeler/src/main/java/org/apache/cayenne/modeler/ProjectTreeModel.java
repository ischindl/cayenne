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

package org.apache.cayenne.modeler;

import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.cayenne.project.Project;

/**
 * ProjectTreeModel is a model of Cayenne project tree.
 */
public class ProjectTreeModel extends DefaultTreeModel {

   
    /**
     * Constructor for ProjectTreeModel.
     */
    public ProjectTreeModel(Project project) {
        super(ProjectTreeFactory.wrapProjectNode(project.getRootNode()));
    }

    /**
     * Re-inserts a tree node to preserve the correct ordering of items. Assumes that the
     * tree is already ordered, except for one node.
     */
    public void positionNode(
            MutableTreeNode parent,
            DefaultMutableTreeNode treeNode,
            Comparator comparator) {

        if (treeNode == null) {
            return;
        }

        if (parent == null && treeNode != getRoot()) {
            parent = (MutableTreeNode) treeNode.getParent();
            if (parent == null) {
                parent = getRootNode();
            }
        }

        Object object = treeNode.getUserObject();

        if (parent != null) {
            int len = parent.getChildCount();
            int ins = -1;
            int rm = -1;

            for (int i = 0; i < len; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
                        .getChildAt(i);

                // remember to remove node
                if (node == treeNode) {
                    rm = i;
                    continue;
                }

                // no more insert checks
                if (ins >= 0) {
                    continue;
                }

                // ObjEntities go before DbEntities
                if (comparator.compare(object, node.getUserObject()) <= 0) {
                    ins = i;
                }
            }

            if (ins < 0) {
                ins = len;
            }

            if (rm == ins) {
                return;
            }

            // remove
            if (rm >= 0) {
                removeNodeFromParent(treeNode);
                if (rm < ins) {
                    ins--;
                }
            }

            // insert
            insertNodeInto(treeNode, parent, ins);
        }
    }

    /**
     * Returns root node cast into DefaultMutableTreeNode.
     */
    public DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) super.getRoot();
    }

    public DefaultMutableTreeNode getNodeForObjectPath(Object[] path) {
        if (path == null || path.length == 0) {
            return null;
        }

        DefaultMutableTreeNode currentNode = getRootNode();

        // adjust for root node being in the path
        int start = 0;
        if (currentNode.getUserObject() == path[0]) {
            start = 1;
        }

        for (int i = start; i < path.length; i++) {
            DefaultMutableTreeNode foundNode = null;
            Enumeration children = currentNode.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) children
                        .nextElement();
                if (child.getUserObject() == path[i]) {
                    foundNode = child;
                    break;
                }
            }

            if (foundNode == null) {
                return null;
            }
            else {
                currentNode = foundNode;
            }
        }

        return currentNode;
    }
}
