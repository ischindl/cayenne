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
package org.objectstyle.cayenne.conf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.Assert;


/** Test setup for a certain setup of domain configuration. */
public class ConfigLoaderCase {
    protected int failedMaps;
    protected int failedDataSources;
    protected int failedAdapters;
    protected int failedMapRefs;
    protected int totalDomains;
    protected String configInfo;


    /** Evaluates test case built from this object state. */
    public void test(ConfigLoader loader) throws Exception {
        InputStream in = new ByteArrayInputStream(configInfo.getBytes());
        loader.loadDomains(in);
        RuntimeLoadDelegate delegate = (RuntimeLoadDelegate)loader.getDelegate();
        Assert.assertEquals(totalDomains, delegate.getDomains().size());
        Assert.assertEquals(failedMaps, delegate.getStatus().getFailedMaps().size());
        Assert.assertEquals(failedDataSources, delegate.getStatus().getFailedDataSources().size());
        Assert.assertEquals(failedAdapters, delegate.getStatus().getFailedAdapters().size());
        Assert.assertEquals(failedMapRefs, delegate.getStatus().getFailedMapRefs().size());
    }

    public void setTotalDomains(int totalDomains) {
        this.totalDomains = totalDomains;
    }

    public void setConfigInfo(String configInfo) {
        this.configInfo = configInfo;
    }

    public void setFailedMaps(int failedMaps) {
        this.failedMaps = failedMaps;
    }

    public void setFailedDataSources(int failedDataSources) {
        this.failedDataSources = failedDataSources;
    }

    public void setFailedAdapters(int failedAdapters) {
        this.failedAdapters = failedAdapters;
    }

    public void setFailedMapRefs(int failedMapRefs) {
        this.failedMapRefs = failedMapRefs;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\n===== DomainHelperCase ====\n")
        .append(configInfo);

        return buf.toString();
    }
}
