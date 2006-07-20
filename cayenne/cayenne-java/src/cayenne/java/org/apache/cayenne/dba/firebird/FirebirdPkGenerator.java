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

package org.apache.cayenne.dba.firebird;

import java.util.List;

import org.apache.cayenne.dba.JdbcPkGenerator;

public class FirebirdPkGenerator extends JdbcPkGenerator {

    public FirebirdPkGenerator() {
        super();
    }
    
    protected String pkCreateString(String entName) {
        // added ; and so I can execute the DDL Script at once
        return super.pkCreateString(entName) + ";";
	}

    protected String pkDeleteString(List dbEntities) {
        // added ; and so I can execute the DDL Script at once
        return super.pkDeleteString(dbEntities) + ";";
    }
    
    protected String pkTableCreateString() {
        // added ; and so I can execute the DDL Script at once
        return super.pkTableCreateString() + ";";
    }
}
