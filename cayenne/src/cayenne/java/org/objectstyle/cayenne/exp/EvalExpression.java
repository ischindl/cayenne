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
package org.objectstyle.cayenne.exp;

import java.util.*;

import org.apache.commons.beanutils.*;
import org.apache.log4j.*;
import org.objectstyle.cayenne.util.*;

/**
 * Class that performs in-memory Cayenne expressions evaluation.
 * 
 * @author Andrei Adamchik
 */
public class EvalExpression extends ExpressionTraversal {
	static Logger logObj = Logger.getLogger(EvalExpression.class);

	protected Expression exp;

	/**
	 * Constructor for EvalExpression.
	 */
	public EvalExpression(Expression exp) {
		this.exp = exp;
		this.setHandler(new EvalHandler());
	}

	/**
	 * Evaluates internally stored expression for an object.
	 * 
	 * @return <code>true</code> if object matches the expression,
	 * <code>false</code> otherwise.
	 */
	public boolean evaluate(Object o) {
		reinit(o);
		traverseExpression(exp);

		return ((EvalHandler) getHandler()).getMatch();
	}

	protected void reinit(Object o) {
		((EvalHandler) getHandler()).reinit(o);
	}

	/** 
	 * Stops early if needed.
	 */
	protected void traverseExpression(Object expObj, Expression parentExp) {
		super.traverseExpression(expObj, parentExp);
	}

	class EvalHandler extends TraversalHelper {
		protected List stack = new ArrayList(20);
		protected Object obj;

		public boolean getMatch() {
			return popBoolean();
		}

		/** 
		 * Resets handler to start processing a new expresson.
		 */
		protected void reinit(Object obj) {
			stack.clear();

			// default - evaluate to false
			push(false);

			this.obj = obj;
		}

		/** 
		 * Evaluates expression using values from the stack, pushes
		 * the result on the stack.
		 */
		public void endBinaryNode(Expression node, Expression parentNode) {
			int type = node.getType();
			if (type == Expression.EQUAL_TO) {
				Object v2 = pop();
				Object v1 = pop();
				push(Util.nullSafeEquals(v1, v2));
			} else if (type == Expression.AND) {
				boolean v2 = popBoolean();
				boolean v1 = popBoolean();
				push(v2 && v1);
			} else if (type == Expression.OR) {
				boolean v2 = popBoolean();
				boolean v1 = popBoolean();
				push(v2 || v1);
			} else {
				push(null);
			}
		}

		/** 
		 * Pushes leaf value on the stack. If leaf is an object expression,
		 * it is first evaluated, and the result is pushed on the stack.
		 */
		public void objectNode(Object leaf, Expression parentNode) {
			// push value on the stack
			if (parentNode.getType() == Expression.OBJ_PATH) {

				try {
					push(PropertyUtils.getProperty(obj, (String) leaf));
				} catch (Exception ex) {
					String msg = "Error reading property '" + leaf + "'.";
					logObj.warn(msg, ex);
					throw new ExpressionException(msg, ex);
				}
			} else {
				push(leaf);
			}
		}

		/** 
		 * Pops a value from the stack.
		 */
		public final Object pop() {
			return stack.remove(stack.size() - 1);
		}

		/** 
		 * Pops a value from the stack, converting it to boolean.
		 */
		public final boolean popBoolean() {
			Object obj = pop();
			return (obj != null) ? ((Boolean) obj).booleanValue() : false;
		}

		/** 
		 * Pops a value from the stack, converting it to int.
		 */
		public final int popInt() {
			return ((Integer) pop()).intValue();
		}

		/**
		 * Pushes a value to the stack.
		 */
		public final void push(Object obj) {
			stack.add(obj);
		}

		/**
		 * Pushes a boolean value to the stack.
		 */
		public final void push(boolean b) {
			stack.add(b ? Boolean.TRUE : Boolean.FALSE);
		}
	}
}
