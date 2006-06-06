/*
 *  Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.cayenne.swing;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.objectstyle.cayenne.swing.BindingBase;
import org.objectstyle.cayenne.swing.BindingException;

/**
 * A binding that ties a component to a Rectangle shape.
 * 
 * @author Andrus Adamchik
 */
public class BoundsBinding extends BindingBase {

    protected Component component;

    public BoundsBinding(Component component, String propertyExpression) {
        super(propertyExpression);
        this.component = component;

        component.addComponentListener(new ComponentAdapter() {

            public void componentMoved(ComponentEvent e) {
                updateModel();
            }

            public void componentResized(ComponentEvent e) {
                updateModel();
            }
        });
    }

    public Component getView() {
        return component;
    }

    public void updateView() {
        Object value = getValue();

        if (value == null) {
            return;
        }

        if (!(value instanceof Rectangle)) {
            throw new BindingException("Expected a vlaue of class "
                    + Rectangle.class.getName()
                    + ", got: "
                    + value.getClass().getName());
        }

        Rectangle r = (Rectangle) value;

        modelUpdateDisabled = true;
        try {
            component.setBounds(r);
        }
        finally {
            modelUpdateDisabled = false;
        }
    }

    protected void updateModel() {
        setValue(component.getBounds());
    }
}
