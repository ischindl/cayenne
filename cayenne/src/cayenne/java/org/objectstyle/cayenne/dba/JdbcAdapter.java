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

package org.objectstyle.cayenne.dba;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.access.BatchInterpreter;
import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.access.OperationSorter;
import org.objectstyle.cayenne.access.QueryTranslator;
import org.objectstyle.cayenne.access.trans.DeleteBatchQueryBuilder;
import org.objectstyle.cayenne.access.trans.DeleteTranslator;
import org
    .objectstyle
    .cayenne
    .access
    .trans
    .FlattenedRelationshipDeleteTranslator;
import org
    .objectstyle
    .cayenne
    .access
    .trans
    .FlattenedRelationshipInsertTranslator;
import org.objectstyle.cayenne.access.trans.InsertBatchQueryBuilder;
import org.objectstyle.cayenne.access.trans.InsertTranslator;
import org.objectstyle.cayenne.access.trans.ProcedureTranslator;
import org.objectstyle.cayenne.access.trans.QualifierTranslatorFactory;
import org.objectstyle.cayenne.access.trans.SelectTranslator;
import org.objectstyle.cayenne.access.trans.SqlModifyTranslator;
import org.objectstyle.cayenne.access.trans.SqlSelectTranslator;
import org.objectstyle.cayenne.access.trans.UpdateBatchQueryBuilder;
import org.objectstyle.cayenne.access.trans.UpdateTranslator;
import org.objectstyle.cayenne.access.types.ByteArrayType;
import org.objectstyle.cayenne.access.types.CharType;
import org.objectstyle.cayenne.access.types.ExtendedTypeMap;
import org.objectstyle.cayenne.access.types.UtilDateType;
import org.objectstyle.cayenne.map.DbAttribute;
import org.objectstyle.cayenne.map.DbAttributePair;
import org.objectstyle.cayenne.map.DbEntity;
import org.objectstyle.cayenne.map.DbRelationship;
import org.objectstyle.cayenne.map.DerivedDbEntity;
import org.objectstyle.cayenne.query.DeleteQuery;
import org.objectstyle.cayenne.query.FlattenedRelationshipDeleteQuery;
import org.objectstyle.cayenne.query.FlattenedRelationshipInsertQuery;
import org.objectstyle.cayenne.query.InsertQuery;
import org.objectstyle.cayenne.query.ProcedureQuery;
import org.objectstyle.cayenne.query.Query;
import org.objectstyle.cayenne.query.SelectQuery;
import org.objectstyle.cayenne.query.SqlModifyQuery;
import org.objectstyle.cayenne.query.SqlSelectQuery;
import org.objectstyle.cayenne.query.UpdateQuery;

/**
 * A generic DbAdapter implementation.
 * Can be used as a default adapter or as
 * a superclass of a concrete adapter implementation.
 *
 * @author Andrei Adamchik
 */
public class JdbcAdapter implements DbAdapter {
    private static Logger logObj = Logger.getLogger(JdbcAdapter.class);

    protected PkGenerator pkGenerator;
    protected TypesHandler typesHandler;
    protected ExtendedTypeMap extendedTypes;
    protected QualifierTranslatorFactory qualifierFactory;
    protected BatchInterpreter insertBatchInterpreter;
    protected BatchInterpreter deleteBatchInterpreter;
    protected BatchInterpreter updateBatchInterpreter;

    /**
     * Returns an array of all available DbAdapter subclass names.
     * This should probably better be in DbAdapter but interfaces cannot
     * contain implementations..
     * 
     * @return String[] array of DbAdapter subclass names
     */
    public static String[] availableAdapterClassNames() {
        return new String[] {
            DbAdapter.JDBC,
            DbAdapter.HSQLDB,
            DbAdapter.MYSQL,
            DbAdapter.ORACLE,
            DbAdapter.POSTGRES,
            DbAdapter.SYBASE };
    }

    public JdbcAdapter() {
        // create Pk generator
        pkGenerator = createPkGenerator();
        typesHandler = TypesHandler.getHandler(this.getClass());
        extendedTypes = new ExtendedTypeMap();
        configureExtendedTypes(extendedTypes);

        qualifierFactory = new QualifierTranslatorFactory();
    }

    /**
     * Installs appropriate ExtendedTypes as converters for passing values
     * between JDBC and Java layers. Called from default constructor.
     */
    protected void configureExtendedTypes(ExtendedTypeMap map) {
        // Create a default CHAR handler with some generic settings.
        // Subclasses may need to install their own CharType or reconfigure
        // this one to work better with the target database.
        map.registerType(new CharType(false, true));

        // enable java.util.Dates as "persistent" values
        map.registerType(new UtilDateType());

        // enable "small" BLOBs
        map.registerType(new ByteArrayType(false, true));
    }

    /**
     * Creates and returns a primary key generator. This factory
     * method should be overriden by JdbcAdapter subclasses to
     * provide custom implementations of PKGenerator.
     */
    protected PkGenerator createPkGenerator() {
        return new JdbcPkGenerator();
    }

    /** Returns primary key generator associated with this DbAdapter. */
    public PkGenerator getPkGenerator() {
        return pkGenerator;
    }

    public QueryTranslator getQueryTranslator(Query query) throws Exception {
        Class queryClass = queryTranslatorClass(query);

        try {
            QueryTranslator t = (QueryTranslator) queryClass.newInstance();
            t.setQuery(query);
            t.setAdapter(this);
            return t;
        } catch (Exception ex) {
            throw new CayenneRuntimeException(
                "Can't load translator class: " + queryClass);
        }
    }

    /**
     * Returns a class of the query translator that
     * should be used to translate the query <code>q</code>
     * to SQL. Exists mainly for the benefit of subclasses
     * that can override this method providing their own translator.
     */
    protected Class queryTranslatorClass(Query q) {
        if (q == null) {
            throw new NullPointerException("Null query.");
        } else if (q instanceof SelectQuery) {
            return SelectTranslator.class;
        } else if (q instanceof UpdateQuery) {
            return UpdateTranslator.class;
        } else if (q instanceof FlattenedRelationshipInsertQuery) {
            return FlattenedRelationshipInsertTranslator.class;
        } else if (q instanceof InsertQuery) {
            return InsertTranslator.class;
        } else if (q instanceof FlattenedRelationshipDeleteQuery) {
            return FlattenedRelationshipDeleteTranslator.class;
        } else if (q instanceof DeleteQuery) {
            return DeleteTranslator.class;
        } else if (q instanceof SqlSelectQuery) {
            return SqlSelectTranslator.class;
        } else if (q instanceof SqlModifyQuery) {
            return SqlModifyTranslator.class;
        } else if (q instanceof ProcedureQuery) {
            return ProcedureTranslator.class;
        } else {
            throw new CayenneRuntimeException(
                "Unrecognized query class..." + q.getClass().getName());
        }
    }

    /** Returns true. */
    public boolean supportsFkConstraints() {
        return true;
    }

    /**
     * Returns a SQL string to drop a table corresponding
     * to <code>ent</code> DbEntity.
     */
    public String dropTable(DbEntity ent) {
        return "DROP TABLE " + ent.getFullyQualifiedName();
    }

    /**
     * Returns a SQL string that can be used to create database table
     * corresponding to <code>ent</code> parameter.
     */
    public String createTable(DbEntity ent) {
        // later we may support view creation
        // for derived DbEntities
        if (ent instanceof DerivedDbEntity) {
            throw new CayenneRuntimeException(
                "Can't create table for derived DbEntity '"
                    + ent.getName()
                    + "'.");
        }

        StringBuffer buf = new StringBuffer();
        buf.append("CREATE TABLE ").append(ent.getFullyQualifiedName()).append(
            " (");

        // columns
        Iterator it = ent.getAttributeList().iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (first) {
                first = false;
            } else {
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
            if (TypesMapping.supportsLength(at.getType())) {
                int len = at.getMaxLength();
                int prec =
                    TypesMapping.isDecimal(at.getType())
                        ? at.getPrecision()
                        : -1;

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

            if (at.isMandatory())
                buf.append(" NOT");

            buf.append(" NULL");
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
        buf.append(')');
        return buf.toString();
    }

    /**
     * Returns a SQL string that can be used to create
     * a foreign key constraint for the relationship.
     */
    public String createFkConstraint(DbRelationship rel) {
        StringBuffer buf = new StringBuffer();
        StringBuffer refBuf = new StringBuffer();

        buf
            .append("ALTER TABLE ")
            .append(((DbEntity) rel.getSourceEntity()).getFullyQualifiedName())
            .append(" ADD FOREIGN KEY (");

        Iterator jit = rel.getJoins().iterator();
        boolean first = true;
        while (jit.hasNext()) {
            DbAttributePair join = (DbAttributePair) jit.next();
            if (!first) {
                buf.append(", ");
                refBuf.append(", ");
            } else
                first = false;

            buf.append(join.getSource().getName());
            refBuf.append(join.getTarget().getName());
        }

        buf
            .append(") REFERENCES ")
            .append(((DbEntity) rel.getTargetEntity()).getFullyQualifiedName())
            .append(" (")
            .append(refBuf.toString())
            .append(')');
        return buf.toString();
    }

    public String[] externalTypesForJdbcType(int type) {
        return typesHandler.externalTypesForJdbcType(type);
    }

    /** Returns null - by default no operation sorter is used. */
    public OperationSorter getOpSorter(DataNode node) {
        return null;
    }

    public ExtendedTypeMap getExtendedTypes() {
        return extendedTypes;
    }

    public QualifierTranslatorFactory getQualifierFactory() {
        return qualifierFactory;
    }

    public DbAttribute buildAttribute(
        String name,
        String typeName,
        int type,
        int size,
        int precision,
        boolean allowNulls) {

        DbAttribute attr = new DbAttribute();
        attr.setName(name);
        attr.setType(type);
        attr.setMandatory(!allowNulls);

        if (size >= 0) {
            attr.setMaxLength(size);
        }

        if (precision >= 0) {
            attr.setPrecision(precision);
        }

        return attr;
    }

    public String tableTypeForTable() {
        return "TABLE";
    }

    public String tableTypeForView() {
        return "VIEW";
    }

    public BatchInterpreter getInsertBatchInterpreter() {
        if (insertBatchInterpreter == null) {
            insertBatchInterpreter = new BatchInterpreter();
            insertBatchInterpreter.setAdapter(this);
            insertBatchInterpreter.setQueryBuilder(
                new InsertBatchQueryBuilder(this));
        }
        return insertBatchInterpreter;
    }

    public BatchInterpreter getDeleteBatchInterpreter() {
        if (deleteBatchInterpreter == null) {
            deleteBatchInterpreter = new BatchInterpreter();
            deleteBatchInterpreter.setAdapter(this);
            deleteBatchInterpreter.setQueryBuilder(
                new DeleteBatchQueryBuilder(this));
        }
        return deleteBatchInterpreter;
    }

    public BatchInterpreter getUpdateBatchInterpreter() {
        if (updateBatchInterpreter == null) {
            updateBatchInterpreter = new BatchInterpreter();
            updateBatchInterpreter.setAdapter(this);
            updateBatchInterpreter.setQueryBuilder(
                new UpdateBatchQueryBuilder(this));
        }
        return updateBatchInterpreter;
    }

    /**
      * @deprecated Since 1.0Beta1 'getExtendedTypes' is used since this method
      * name is confusing.
      */
    public ExtendedTypeMap getTypeConverter() {
        return getExtendedTypes();
    }

}