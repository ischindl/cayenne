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

import java.util.Iterator;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.types.CharType;
import org.apache.cayenne.access.types.ExtendedTypeMap;
import org.apache.cayenne.dba.JdbcAdapter;
import org.apache.cayenne.dba.PkGenerator;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.DerivedDbEntity;

/**
 * DbAdapter implementation for <a href="http://www.firebirdsql.org">Firebird RDBMS</a>.
 * Sample <a target="_top" href="../../../../../../../developerguide/unit-tests.html">connection
 * settings</a> to use with Firebird are shown below:
 *
 * <pre>
 * test-firebird.cayenne.adapter = org.apache.cayenne.dba.firebird.FirebirdAdapter
 * test-firebird.jdbc.username = sysdba
 * test-firebird.jdbc.password = masterkey
 * test-firebird.jdbc.url = jdbc:firebirdsql:[host[/port]/]<database>
 * test-firebird.jdbc.driver = org.firebirdsql.jdbc.FBDriver
 * </pre>
 *
 * @author Heiko Wenzel
 */
public class FirebirdAdapter extends JdbcAdapter {   
	
	public FirebirdAdapter() {
		super();
	}

    public String createTable(DbEntity ent) {
        if (ent instanceof DerivedDbEntity) {
            throw new CayenneRuntimeException(
            "Can't create table for derived DbEntity '" + ent.getName() + "'.");
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append("CREATE TABLE ").append(ent.getFullyQualifiedName()).append(" (");
        
        // columns
        Iterator it = ent.getAttributes().iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }
            
            DbAttribute at = (DbAttribute) it.next();
            
            // attribute may not be fully valid, do a simple check
            if (at.getType() == TypesMapping.NOT_DEFINED) {
                throw new CayenneRuntimeException(
                "Undefined type for attribute '"
                + ent.getFullyQualifiedName()
                + "."
                + at.getName()
                + "'.");
            }
            
            String[] types = externalTypesForJdbcType(at.getType());
            if (types == null || types.length == 0) {
                throw new CayenneRuntimeException(
                "Undefined type for attribute '"
                + ent.getFullyQualifiedName()
                + "."
                + at.getName()
                + "': "
                + at.getType());
            }
            
            String type = types[0];
            buf.append(at.getName()).append(' ').append(type);
            
            // append size and precision (if applicable)
            if (typeSupportsLength(at.getType())) {
                int len = at.getMaxLength();
                int prec = TypesMapping.isDecimal(at.getType()) ? at.getPrecision() : -1;
                
                if (type.compareTo("double precision") == 0)  // double precision returns with len = 15
                    len = 0;
                
                // sanity check
                if (prec > len) {
                    prec = -1;
                }
                
                if (len > 0) {
                    buf.append('(').append(len);
                    
                    if (prec >= 0) {
                        buf.append(", ").append(prec);
                    }
                    buf.append(')');
                }
            }
            
            if (at.isMandatory()) {
                buf.append(" NOT NULL");
            }
            
        }
        
        // primary key clause
        Iterator pkit = ent.getPrimaryKey().iterator();
        if (pkit.hasNext()) {
            if (first)
                first = false;
            else
                buf.append(", ");
            
            buf.append("PRIMARY KEY (");
            boolean firstPk = true;
            while (pkit.hasNext()) {
                if (firstPk)
                    firstPk = false;
                else
                    buf.append(", ");
                
                DbAttribute at = (DbAttribute) pkit.next();
                buf.append(at.getName());
            }
            buf.append(')');
        }
        buf.append(");");  // added ; and so I can execute the DDL Script at once
        return buf.toString();
    }
    
    private boolean typeSupportsLength(int type) {
        // "bytea" type does not support length
        String[] externalTypes = externalTypesForJdbcType(type);
        if (externalTypes != null && externalTypes.length > 0) {
            for (int i = 0; i < externalTypes.length; i++) {
                if ("blob sub_type 0".equalsIgnoreCase(externalTypes[i])) {
                    return false;
                }
            }
        }

        return TypesMapping.supportsLength(type);
    }
    
    /**
     * Returns a SQL string that can be used to create
     * a foreign key constraint for the relationship.
     */
    public String createFkConstraint(DbRelationship rel) {
        // added ; and so I can execute the DDL Script at once
        return super.createFkConstraint(rel) + ";";
    }
    
    public String dropTable(DbEntity ent) {
        // added ; and so I can execute the DDL Script at once
        return super.dropTable(ent) + ";";
    }
    
    protected void configureExtendedTypes(ExtendedTypeMap map) {
        super.configureExtendedTypes(map);
        map.registerType(new CharType(true, false));
    }
    
    /**
     * @see JdbcAdapter#createPkGenerator()
     */
    protected PkGenerator createPkGenerator() {
        return new FirebirdPkGenerator();
    } 
    
    // TODO: Does Firebird support something like RTRIM?... It really needs it.
/*    public QualifierTranslator getQualifierTranslator(QueryAssembler queryAssembler) {
        return new TrimmingQualifierTranslator(queryAssembler, "");
    } */
}
