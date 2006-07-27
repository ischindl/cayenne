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


package org.apache.cayenne.conf;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.cayenne.access.DataContext;

/**
 * <p>
 * <code>WebApplicationContextFilter</code> is another helper class to help integrate
 * Cayenne with web applications. The implementation is similar to
 * <code>org.apache.cayenne.conf.WebApplicationContextProvider</code> however it
 * allows for integration with containers that support version 2.3 of the servlet
 * specification for example Tomcat 4.x. via the usage of filers.
 * </p>
 * <p>
 * Whenever this filter is processed it attempts bind a <code>DataContext</code> to the
 * thread. It retrieves the <code>DataContext</code> via the
 * <code>BasicServletConfiguration.getDefaultDataContext()</code> and binds it to the
 * thread using <code>DataContext.bindThreadDataContext()</code> method. If the session
 * has not been created this filter will also create a new session.
 * </p>
 * <p>
 * During initialization (init() method) this filter initializes the
 * <code>BasicServletConfiguration</code> with the servlet context via the
 * initializeConfiguration() method
 * </p>
 * <p>
 * This filter can be installed in the web container as a &quot;filter&quot; as follows:
 * 
 * <pre>
 *          &lt;filter&gt;
 *              &lt;filter-name&gt;WebApplicationContextFilter&lt;/filter-name&gt;
 *              &lt;filter-class&gt;org.apache.cayenne.conf.WebApplicationContextFilter&lt;/filter-class&gt;
 *         &lt;/filter&gt;
 * </pre>
 * 
 * Then the mapping needs to be created to direct all or some requests to be processed by
 * the filter as follows:
 * 
 * <pre>
 *         &lt;filter-mapping&gt;
 *              &lt;filter-name&gt;WebApplicationContextFilter&lt;/filter-name&gt;
 *              &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *          &lt;/filter-mapping&gt;
 * </pre>
 * 
 * The above example the filter would be applied to all the servlets and static content
 * pages in the Web application, because every request URI matches the '/*' URL pattern.
 * The problem with this mapping however is the fact that this filter will run for every
 * request made, whether for images and/or static content and dynamic content request.
 * This maybe detrimental to performance. Hence the mapping url patter should be set
 * accordingly.
 * </p>
 * 
 * @author Gary Jarrel
 */
public class WebApplicationContextFilter implements Filter {

    private static Logger logger = Logger.getLogger(WebApplicationContextFilter.class);

    /**
     * Does nothing. As per the servlet specification, gets called by the container when
     * the filter is taken out of service.
     */
    public void destroy() {
        // empty
    }

    /**
     * Initializes the <code>BasicServletConfiguration</code> via the
     * initializeConfiguration() method. Also saves the FilterConfing to a private local
     * variable for possible later access. This method is part of the <code>Filter</code>
     * interface and is called by the container when the filter is placed into service.
     */
    public synchronized void init(FilterConfig config) throws ServletException {
        ServletUtil.initializeSharedConfiguration(config.getServletContext());
    }

    /**
     * Retrieves the <code>DataContext</code> bound to the <code>HttpSession</code>
     * via <code>BasicServletConfiguration.

     * getDefaultContext()</code>, and binds it to
     * the current thread.
     */
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("start WebApplicationContextFilter.doFilter. URL - "
                    + ((HttpServletRequest) request).getRequestURL());
        }

        if (request instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) request).getSession(true);
            DataContext dataContext = ServletUtil.getSessionContext(session);

            if (dataContext == null) {
                logger.debug("DataContext was null. Throwing Exception");

                throw new ServletException("DataContext was null and could "
                        + "not be bound to thread.");
            }

            DataContext.bindThreadDataContext(dataContext);
            logger.debug("DataContext bound, continuing in chain");
        }
        else {
            logger.debug("requests that are not HttpServletRequest are not supported..");
        }

        try {
            chain.doFilter(request, response);
        }
        finally {
            DataContext.bindThreadDataContext(null);
        }
    }
}
