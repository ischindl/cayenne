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

package org.apache.cayenne.exp;

import java.io.PrintWriter;

/**
 * Generic ternary expression. Describes expression in a form: "<tt>doSomething(operand1, operand2, operand3)</tt>".
 * SQL example of ternary expression is BETWEEN expression.
 * 
 * @deprecated since 1.2
 */
public class TernaryExpression extends Expression {

    protected Object operand0;
    protected Object operand1;
    protected Object operand2;

    public TernaryExpression() {
    }

    public TernaryExpression(int type) {
        this.type = type;
    }

    public Expression notExp() {
        Expression exp = ExpressionFactory.expressionOfType(Expression.NOT);
        exp.setOperand(0, this);
        return exp;
    }

    public Object evaluate(Object o) {
        return ASTCompiler.compile(this).evaluateASTChain(o);
    }

    protected void flattenTree() {

    }

    protected boolean pruneNodeForPrunedChild(Object prunedChild) {
        return true;
    }

    public final int getOperandCount() {
        return 3;
    }

    /**
     * Creates a copy of this expression node, without copying children.
     * 
     * @since 1.1
     */
    public Expression shallowCopy() {
        return new TernaryExpression(type);
    }

    public Object getOperand(int index) {
        if (index == 0)
            return operand0;
        else if (index == 1)
            return operand1;
        else if (index == 2)
            return operand2;

        throw new IllegalArgumentException(
                "Invalid operand index for TernaryExpression: " + index);
    }

    public void setOperand(int index, Object value) {
        if (index == 0) {
            operand0 = value;
            return;
        }
        else if (index == 1) {
            operand1 = value;
            return;
        }
        else if (index == 2) {
            operand2 = value;
            return;
        }

        throw new IllegalArgumentException(
                "Invalid operand index for TernaryExpression: " + index);
    }

    /**
     * @since 1.1
     */
    public void encodeAsString(PrintWriter pw) {
        for (int i = 0; i < getOperandCount(); i++) {
            if (i > 0 || getOperandCount() == 1) {
                pw.print(" ");
                pw.print(expName());
                pw.print(" ");
            }

            Object op = getOperand(i);
            if (op == null) {
                pw.print("<null>");
            }
            else if (op instanceof String) {
                pw.print("'" + op + "'");
            }
            else if (op instanceof Expression) {
                pw.print('(');
                ((Expression) op).encodeAsString(pw);
                pw.print(')');
            }
            else {
                pw.print(String.valueOf(op));
            }
        }
    }
}
