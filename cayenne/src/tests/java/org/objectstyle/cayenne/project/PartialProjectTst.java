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
package org.objectstyle.cayenne.project;

import java.io.File;

import org.objectstyle.cayenne.conf.Configuration;
import org.objectstyle.cayenne.unit.CayenneTestCase;
import org.objectstyle.cayenne.util.Util;

/**
 * @author Andrei Adamchik
 */
public class PartialProjectTst extends CayenneTestCase {
    protected File testProjectFile;
    protected PartialProject project;

    protected void setUp() throws Exception {
        super.setUp();
        
        // create new test directory, copy cayenne.xml in there
        File baseDir = super.getTestDir();
        for (int i = 1; i < 100; i++) {
            File tmpDir = new File(baseDir, "partial-project-" + i);
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdir()) {
                    throw new Exception("Can't create " + tmpDir);
                }

                testProjectFile = new File(tmpDir, Configuration.DEFAULT_DOMAIN_FILE);
                break;
            }
        }

        // copy cayenne.xml
        File src = new File(getTestResourcesDir(), "lightweight-cayenne.xml");
        if (!Util.copy(src, testProjectFile)) {
            throw new Exception("Can't copy from " + src);
        }

        project = new PartialProject(testProjectFile);
    }

    public void testParentFile() throws Exception {
        assertEquals(
            testProjectFile.getParentFile().getCanonicalFile(),
            project.getProjectDirectory().getCanonicalFile());
    }

    public void testProjectFile() throws Exception {
        ProjectFile f = project.findFile(project);
        assertNotNull(f);
        assertTrue(
            "Wrong main file type: " + f.getClass().getName(),
            f instanceof ApplicationProjectFile);
        assertNotNull(
            "Null delegate",
            ((ApplicationProjectFile) f).getSaveDelegate());
    }

    public void testMainFile() throws Exception {
        assertEquals(
            project.findFile(project).resolveFile(),
            project.getMainFile());
    }

    public void testDomains() throws Exception {
        assertEquals(2, project.getChildren().size());
    }

    public void testNodes() throws Exception {
        PartialProject.DomainMetaData d2 =
            (PartialProject.DomainMetaData) project.domains.get("d2");
        assertNotNull(d2);
        assertEquals(2, d2.nodes.size());
    }
    
    public void testSave() throws Exception {
    	if(!testProjectFile.delete()) {
    		throw new Exception("Can't delete project file: " + testProjectFile);
    	}
    	
    	PartialProject old = project;
    	old.save();
    	
    	assertTrue(testProjectFile.exists());
    	
    	// reinit shared project and run one of the other tests
        project = new PartialProject(testProjectFile);
        testNodes();
    }
}
