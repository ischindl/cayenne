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
package org.apache.cayenne.runtime;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.QueryLogger;
import org.apache.cayenne.configuration.DataNodeDescriptor;

/**
 * @since 3.1
 */
public class JNDIDataSourceFactory implements DataSourceFactory {

    public DataSource getDataSource(DataNodeDescriptor nodeDescriptor) {
        try {
            return loadViaJNDI(nodeDescriptor.getLocation());
        }
        catch (NamingException e) {
            QueryLogger.logConnectFailure(e);
            throw new CayenneRuntimeException(
                    "Error loading DataSource from JNDI for location '%s'",
                    e,
                    nodeDescriptor.getLocation());
        }
    }

    DataSource loadViaJNDI(String location) throws NamingException {
        QueryLogger.logConnect(location);

        Context context = new InitialContext();
        DataSource dataSource;
        try {
            Context envCtx = (Context) context.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup(location);
        }
        catch (NamingException namingEx) {
            // try looking up the location directly...
            dataSource = (DataSource) context.lookup(location);
        }

        QueryLogger.logConnectSuccess();
        return dataSource;
    }

}
