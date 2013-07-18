package org.apache.cayenne.testdo.testmap.auto;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _ReturnTypesMap1 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ReturnTypesMap1 extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String BIGINT_COLUMN_PROPERTY = "bigintColumn";
    @Deprecated
    public static final String BIT_COLUMN_PROPERTY = "bitColumn";
    @Deprecated
    public static final String BOOLEAN_COLUMN_PROPERTY = "booleanColumn";
    @Deprecated
    public static final String CHAR_COLUMN_PROPERTY = "charColumn";
    @Deprecated
    public static final String DATE_COLUMN_PROPERTY = "dateColumn";
    @Deprecated
    public static final String DECIMAL_COLUMN_PROPERTY = "decimalColumn";
    @Deprecated
    public static final String DOUBLE_COLUMN_PROPERTY = "doubleColumn";
    @Deprecated
    public static final String FLOAT_COLUMN_PROPERTY = "floatColumn";
    @Deprecated
    public static final String INTEGER_COLUMN_PROPERTY = "integerColumn";
    @Deprecated
    public static final String LONGVARCHAR_COLUMN_PROPERTY = "longvarcharColumn";
    @Deprecated
    public static final String NUMERIC_COLUMN_PROPERTY = "numericColumn";
    @Deprecated
    public static final String REAL_COLUMN_PROPERTY = "realColumn";
    @Deprecated
    public static final String SMALLINT_COLUMN_PROPERTY = "smallintColumn";
    @Deprecated
    public static final String TIME_COLUMN_PROPERTY = "timeColumn";
    @Deprecated
    public static final String TIMESTAMP_COLUMN_PROPERTY = "timestampColumn";
    @Deprecated
    public static final String TINYINT_COLUMN_PROPERTY = "tinyintColumn";
    @Deprecated
    public static final String VARCHAR_COLUMN_PROPERTY = "varcharColumn";

    public static final String AAAID_PK_COLUMN = "AAAID";

    public static final Property<Long> BIGINT_COLUMN = new Property<Long>("bigintColumn");
    public static final Property<Boolean> BIT_COLUMN = new Property<Boolean>("bitColumn");
    public static final Property<Boolean> BOOLEAN_COLUMN = new Property<Boolean>("booleanColumn");
    public static final Property<String> CHAR_COLUMN = new Property<String>("charColumn");
    public static final Property<Date> DATE_COLUMN = new Property<Date>("dateColumn");
    public static final Property<BigDecimal> DECIMAL_COLUMN = new Property<BigDecimal>("decimalColumn");
    public static final Property<Double> DOUBLE_COLUMN = new Property<Double>("doubleColumn");
    public static final Property<Float> FLOAT_COLUMN = new Property<Float>("floatColumn");
    public static final Property<Integer> INTEGER_COLUMN = new Property<Integer>("integerColumn");
    public static final Property<String> LONGVARCHAR_COLUMN = new Property<String>("longvarcharColumn");
    public static final Property<BigDecimal> NUMERIC_COLUMN = new Property<BigDecimal>("numericColumn");
    public static final Property<Float> REAL_COLUMN = new Property<Float>("realColumn");
    public static final Property<Short> SMALLINT_COLUMN = new Property<Short>("smallintColumn");
    public static final Property<Date> TIME_COLUMN = new Property<Date>("timeColumn");
    public static final Property<Date> TIMESTAMP_COLUMN = new Property<Date>("timestampColumn");
    public static final Property<Byte> TINYINT_COLUMN = new Property<Byte>("tinyintColumn");
    public static final Property<String> VARCHAR_COLUMN = new Property<String>("varcharColumn");

    public void setBigintColumn(Long bigintColumn) {
        writeProperty("bigintColumn", bigintColumn);
    }
    public Long getBigintColumn() {
        return (Long)readProperty("bigintColumn");
    }

    public void setBitColumn(Boolean bitColumn) {
        writeProperty("bitColumn", bitColumn);
    }
    public Boolean getBitColumn() {
        return (Boolean)readProperty("bitColumn");
    }

    public void setBooleanColumn(Boolean booleanColumn) {
        writeProperty("booleanColumn", booleanColumn);
    }
    public Boolean getBooleanColumn() {
        return (Boolean)readProperty("booleanColumn");
    }

    public void setCharColumn(String charColumn) {
        writeProperty("charColumn", charColumn);
    }
    public String getCharColumn() {
        return (String)readProperty("charColumn");
    }

    public void setDateColumn(Date dateColumn) {
        writeProperty("dateColumn", dateColumn);
    }
    public Date getDateColumn() {
        return (Date)readProperty("dateColumn");
    }

    public void setDecimalColumn(BigDecimal decimalColumn) {
        writeProperty("decimalColumn", decimalColumn);
    }
    public BigDecimal getDecimalColumn() {
        return (BigDecimal)readProperty("decimalColumn");
    }

    public void setDoubleColumn(Double doubleColumn) {
        writeProperty("doubleColumn", doubleColumn);
    }
    public Double getDoubleColumn() {
        return (Double)readProperty("doubleColumn");
    }

    public void setFloatColumn(Float floatColumn) {
        writeProperty("floatColumn", floatColumn);
    }
    public Float getFloatColumn() {
        return (Float)readProperty("floatColumn");
    }

    public void setIntegerColumn(Integer integerColumn) {
        writeProperty("integerColumn", integerColumn);
    }
    public Integer getIntegerColumn() {
        return (Integer)readProperty("integerColumn");
    }

    public void setLongvarcharColumn(String longvarcharColumn) {
        writeProperty("longvarcharColumn", longvarcharColumn);
    }
    public String getLongvarcharColumn() {
        return (String)readProperty("longvarcharColumn");
    }

    public void setNumericColumn(BigDecimal numericColumn) {
        writeProperty("numericColumn", numericColumn);
    }
    public BigDecimal getNumericColumn() {
        return (BigDecimal)readProperty("numericColumn");
    }

    public void setRealColumn(Float realColumn) {
        writeProperty("realColumn", realColumn);
    }
    public Float getRealColumn() {
        return (Float)readProperty("realColumn");
    }

    public void setSmallintColumn(Short smallintColumn) {
        writeProperty("smallintColumn", smallintColumn);
    }
    public Short getSmallintColumn() {
        return (Short)readProperty("smallintColumn");
    }

    public void setTimeColumn(Date timeColumn) {
        writeProperty("timeColumn", timeColumn);
    }
    public Date getTimeColumn() {
        return (Date)readProperty("timeColumn");
    }

    public void setTimestampColumn(Date timestampColumn) {
        writeProperty("timestampColumn", timestampColumn);
    }
    public Date getTimestampColumn() {
        return (Date)readProperty("timestampColumn");
    }

    public void setTinyintColumn(Byte tinyintColumn) {
        writeProperty("tinyintColumn", tinyintColumn);
    }
    public Byte getTinyintColumn() {
        return (Byte)readProperty("tinyintColumn");
    }

    public void setVarcharColumn(String varcharColumn) {
        writeProperty("varcharColumn", varcharColumn);
    }
    public String getVarcharColumn() {
        return (String)readProperty("varcharColumn");
    }

}
