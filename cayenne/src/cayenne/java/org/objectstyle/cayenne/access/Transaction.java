/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002-2004 The ObjectStyle Group
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
package org.objectstyle.cayenne.access;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectstyle.cayenne.CayenneException;

/**
 * Class responsible for transaction management within Cayenne.
 * 
 * @author Andrei Adamchik
 * @since 1.1
 */
public abstract class Transaction {
    private static final Transaction NO_TRANSACTION = new Transaction() {
        public void begin() {

        }

        public void addConnection(Connection connection) {

        }

        public void commit() {

        }

        public void rollback() {

        }
    };

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_COMMITTING = 2;
    public static final int STATUS_COMMITTED = 3;
    public static final int STATUS_ROLLEDBACK = 4;
    public static final int STATUS_ROLLING_BACK = 5;
    public static final int STATUS_NO_TRANSACTION = 6;
    public static final int STATUS_MARKED_ROLLEDBACK = 7;

    protected List connections;
    protected int status;
    protected TransactionDelegate delegate;

    /**
     * Factory method returning a new transaction instance that would
     * propagate commit/rollback to participating connections. Connections
     * will be closed when commit or rollback is called.
     */
    public static Transaction internalTransaction(TransactionDelegate delegate) {
        return new InternalTransaction(delegate);
    }

    /**
     * Factory method returning a new transaction instance that would NOT
     * propagate commit/rollback to participating connections. Connections
     * will still be closed when commit or rollback is called.
     */
    public static Transaction externalTransaction(TransactionDelegate delegate) {
        return new ExternalTransaction(delegate);
    }

    /**
     * Factory method returning a transaction instance that does not alter the
     * state of participating connections in any way. Commit and rollback methods 
     * do not do anything.
     */
    public static Transaction noTransaction() {
        return NO_TRANSACTION;
    }

    /**
      * Creates new inactive transaction.
      */
    protected Transaction() {
        status = STATUS_NO_TRANSACTION;
    }

    public TransactionDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(TransactionDelegate delegate) {
        this.delegate = delegate;
    }

    public int getStatus() {
        return status;
    }

    public synchronized void setRollbackOnly() {
        setStatus(STATUS_MARKED_ROLLEDBACK);
    }

    public synchronized void setStatus(int status) {
        if (delegate != null
            && status == STATUS_MARKED_ROLLEDBACK
            && !delegate.willMarkAsRollbackOnly(this)) {
            return;
        }

        this.status = status;
    }

    public abstract void begin();

    public abstract void addConnection(Connection connection)
        throws IllegalStateException, SQLException, CayenneException;

    public abstract void commit()
        throws IllegalStateException, SQLException, CayenneException;

    public abstract void rollback()
        throws IllegalStateException, SQLException, CayenneException;

    // transaction managed by container, or no transaction at all.
    // this implementation will simply close the connections on commit
    // or rollback, and notify the delegate
    static class ExternalTransaction extends Transaction {
        ExternalTransaction() {
        }

        ExternalTransaction(TransactionDelegate delegate) {
            setDelegate(delegate);
        }

        public synchronized void begin() {
            status = STATUS_ACTIVE;
            // most Cayenne apps are single datanode, 
            // there will be few that have more than 2, esp. in a single tran
            connections = new ArrayList(2);
        }

        public synchronized void addConnection(Connection connection)
            throws IllegalStateException, SQLException, CayenneException {

            if (delegate != null && !delegate.willAddConnection(this, connection)) {
                return;
            }

            if (status != STATUS_ACTIVE) {
                throw new IllegalStateException("Transaction must be in 'active' state.");
            }

            if (!connections.contains(connection)) {
                // may need to fix connection properties
                fixConnectionState(connection);
                connections.add(connection);
            }
        }

        public void commit()
            throws IllegalStateException, SQLException, CayenneException {
            if (delegate != null && !delegate.willCommit(this)) {
                return;
            }

            try {
                if (status != STATUS_ACTIVE) {
                    throw new IllegalStateException("Transaction must be in 'active' state.");
                }

                processCommit();

                status = STATUS_COMMITTED;
                if (delegate != null) {
                    delegate.didCommit(this);
                }
            }
            finally {
                close();
            }
        }

        public void rollback()
            throws IllegalStateException, SQLException, CayenneException {
            if (delegate != null && !delegate.willRollback(this)) {
                return;
            }

            try {
                if (status != STATUS_ACTIVE) {
                    throw new IllegalStateException("Transaction must be in 'active' state.");
                }

                processRollback();

                status = STATUS_ROLLEDBACK;
                if (delegate != null) {
                    delegate.didRollback(this);
                }
            }
            finally {
                close();
            }
        }

        protected void fixConnectionState(Connection connection) throws SQLException {
            // NOOP
        }

        protected void processCommit() throws SQLException, CayenneException {
            // NOOP
        }

        protected void processRollback() throws SQLException, CayenneException {
            // NOOP
        }

        /**
         * Closes all connections associated with transaction.
         */
        protected void close() {
            Iterator it = connections.iterator();
            while (it.hasNext()) {
                try {

                    ((Connection) it.next()).close();
                }
                catch (Throwable th) {
                    // TODO: chain exceptions...
                    // ignore for now
                }
            }
        }
    }

    // transaction managed by Cayenne
    static class InternalTransaction extends ExternalTransaction {

        InternalTransaction(TransactionDelegate delegate) {
            super(delegate);
        }

        protected void fixConnectionState(Connection connection) throws SQLException {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        }

        protected void processCommit() throws SQLException, CayenneException {
            status = STATUS_COMMITTING;

            if (connections != null && connections.size() > 0) {
                Throwable deferredException = null;
                Iterator it = connections.iterator();
                while (it.hasNext()) {
                    Connection connection = (Connection) it.next();
                    try {

                        if (deferredException == null) {
                            connection.commit();
                        }
                        else {
                            // we must do a partial rollback if only to cleanup uncommitted 
                            // connections.
                            connection.rollback();
                        }

                    }
                    catch (Throwable th) {
                        // there is no such thing as "partial" rollback in real transactions,
                        // so we can't set any meaningful status.
                        // status = ?;
                        setRollbackOnly();

                        // stores last exception
                        // TODO: chain exceptions...
                        deferredException = th;
                    }
                }

                if (deferredException != null) {
                    if (deferredException instanceof SQLException) {
                        throw (SQLException) deferredException;
                    }
                    else {
                        throw new CayenneException(deferredException);
                    }
                }
            }
        }

        protected void processRollback() throws SQLException, CayenneException {
            status = STATUS_ROLLING_BACK;

            if (connections != null && connections.size() > 0) {
                Throwable deferredException = null;

                Iterator it = connections.iterator();
                while (it.hasNext()) {
                    Connection connection = (Connection) it.next();

                    try {
                        // continue with rollback even if an exception was thrown before
                        connection.rollback();
                    }
                    catch (Throwable th) {
                        // stores last exception
                        // TODO: chain exceptions...
                        deferredException = th;
                    }
                }

                if (deferredException != null) {
                    if (deferredException instanceof SQLException) {
                        throw (SQLException) deferredException;
                    }
                    else {
                        throw new CayenneException(deferredException);
                    }
                }
            }
        }
    }
}
