package org.objectstyle.art.auto;

/** Class _BooleanTest was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _BooleanTest extends org.objectstyle.cayenne.CayenneDataObject {

    public static final String BOOLEAN_COLUMN_PROPERTY = "booleanColumn";

    public static final String ID_PK_COLUMN = "ID";

    public void setBooleanColumn(Boolean booleanColumn) {
        writeProperty("booleanColumn", booleanColumn);
    }
    public Boolean getBooleanColumn() {
        return (Boolean)readProperty("booleanColumn");
    }
    
    
}
