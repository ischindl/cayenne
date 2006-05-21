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
package org.apache.cayenne.profile.servlet;

import java.io.File;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.access.DbGenerator;
import org.objectstyle.cayenne.conf.Configuration;
import org.objectstyle.cayenne.util.LocalizedStringsHandler;
import org.objectstyle.cayenne.util.Util;

/**
 * A filter that sets up DB schema.
 */
public class ProfileFilter extends WebApplicationContextFilter {

    protected Log logger = LogFactory.getLog(getClass());

    public synchronized void init(FilterConfig config) throws ServletException {

        // remove old database
        File dbDir = new File("target/regression-db");
        if (dbDir.isDirectory()) {
            Util.delete(dbDir.getAbsolutePath(), true);
        }

        // start Cayenne stack
        super.init(config);

        String cayenneVersion = LocalizedStringsHandler.getString("cayenne.version");
        if (cayenneVersion == null) {
            cayenneVersion = "unknown";
        }
        logger.info("Started Cayenne version - '" + cayenneVersion + "'");

        // create fresh database
        DataDomain domain = Configuration.getSharedConfiguration().getDomain();
        DataNode node = domain.getNode("regression-profile");
        DbGenerator generator = new DbGenerator(node.getAdapter(), domain
                .getMap("regression-profile"));
        try {
            generator.runGenerator(node.getDataSource());
        }
        catch (Exception e) {
            throw new ServletException("Error generating schema", e);
        }

    }
}
