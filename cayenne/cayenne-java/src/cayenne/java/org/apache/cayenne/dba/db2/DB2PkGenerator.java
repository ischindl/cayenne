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


package org.apache.cayenne.dba.db2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.QueryLogger;
import org.apache.cayenne.dba.JdbcPkGenerator;
import org.apache.cayenne.map.DbEntity;

/**
 * PK Generator for IBM DB2 using sequences.
 * 
 * @author Mario Linke, Holger Hoffstaette
 */
public class DB2PkGenerator extends JdbcPkGenerator {

	public static final String SEQUENCE_PREFIX = "S_";

	public void createAutoPk(DataNode node, List dbEntities) throws Exception {
		List sequences = this.getExistingSequences(node);
		Iterator it = dbEntities.iterator();

		while (it.hasNext()) {
			DbEntity ent = (DbEntity) it.next();
			if (!sequences.contains(sequenceName(ent))) {
				this.runUpdate(node, this.createSequenceString(ent));
			}
		}
	}

	public List createAutoPkStatements(List dbEntities) {
		List list = new ArrayList();
		Iterator it = dbEntities.iterator();

		while (it.hasNext()) {
			DbEntity ent = (DbEntity) it.next();
			list.add(this.createSequenceString(ent));
		}

		return list;
	}
	
	public void dropAutoPk(DataNode node, List dbEntities) throws Exception {
		List sequences = this.getExistingSequences(node);
		
		Iterator it = dbEntities.iterator();
		while (it.hasNext()) {
			DbEntity ent = (DbEntity) it.next();
			if (sequences.contains(this.sequenceName(ent))) {
				this.runUpdate(node, this.dropSequenceString(ent));
			}
		}
	}

	public List dropAutoPkStatements(List dbEntities) {
		 List list = new ArrayList();
		 Iterator it = dbEntities.iterator();

		 while (it.hasNext()) {
			 DbEntity ent = (DbEntity) it.next();
			 list.add(this.dropSequenceString(ent));
		 }

		 return list;
	 }	
	
	/**
	 * Returns the sequence name for a given table name.
	 */
	protected String sequenceName(DbEntity ent) {
		String seqName = SEQUENCE_PREFIX + ent.getName();

		if (ent.getSchema() != null && ent.getSchema().length() > 0) {
			seqName = ent.getSchema() + "." + seqName;
		}

		return seqName;
	}	
	
	
	/**
	 * Creates SQL needed for creating a sequence.
	 */
	protected String createSequenceString(DbEntity ent) {
		StringBuffer buf = new StringBuffer();
		buf.append("CREATE SEQUENCE ")
			.append(this.sequenceName(ent))
			.append(" START WITH 200")
			.append(" INCREMENT BY ").append(getPkCacheSize())
			.append(" NO MAXVALUE ")
			.append(" NO CYCLE ")
			.append(" CACHE ").append(getPkCacheSize());
		return buf.toString();
	}	
	
	/**
	 * Creates SQL needed for dropping a sequence.
	 */
	protected String dropSequenceString(DbEntity ent) {
		return "DROP SEQUENCE " + this.sequenceName(ent) + " RESTRICT ";
	}
	
	/**
	 * Creates a new PK from a sequence returned by
	 * <code>
	 * SELECT NEXTVAL FOR sequence_name FROM SYSIBM.SYSDUMMY1 
	 * </code>
	 * SYSIBM.SYSDUMMY1 corresponds to DUAL in Oracle.
	 */
	protected int pkFromDatabase(DataNode node, DbEntity ent) throws Exception {

		String seq_name = sequenceName (ent);
		Connection con = node.getDataSource().getConnection();
		try {
		  Statement st = con.createStatement();
		  try {
		  	String pkQueryString = "SELECT NEXTVAL FOR "
		  							+ seq_name
		  							+ " FROM SYSIBM.SYSDUMMY1";
			QueryLogger.logQuery(pkQueryString, Collections.EMPTY_LIST);
			ResultSet rs = st.executeQuery(pkQueryString);
			try {
			  if (!rs.next()) {
				throw new CayenneRuntimeException(
					"Error in pkFromDatabase() for table "
					+ ent.getName()
					+ " / sequence "
					+ seq_name);
			  }
			  return rs.getInt(1);
			} finally {
			  rs.close();
			}
		  } finally {
			st.close();
		  }
		} finally {
		  con.close();
		}
	}	
	
	
	/**
	 * Returns a List of all existing, accessible sequences.
	 */
	protected List getExistingSequences(DataNode node) throws SQLException {
		Connection con = node.getDataSource().getConnection();
		try {
			Statement sel = con.createStatement();
			try {
				StringBuffer q = new StringBuffer();
				q.append("SELECT SEQNAME FROM SYSCAT.SEQUENCES WHERE SEQNAME")
					.append(" LIKE '")
					.append(SEQUENCE_PREFIX)
					.append("%'");

				ResultSet rs = sel.executeQuery(q.toString());
				try {
					List sequenceList = new ArrayList(32);
					while (rs.next()) {
						sequenceList.add(rs.getString(1));
					}
					return sequenceList;
				} finally {
					rs.close();
				}
			} finally {
				sel.close();
			}
		} finally {
			con.close();
		}
	}	
}
