package org.apache.cayenne.testdo.testmap.auto;

/** Class _MeaningfulGeneratedColumnTestEntity was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public abstract class _MeaningfulGeneratedColumnTestEntity extends org.apache.cayenne.CayenneDataObject {

    public static final String GENERATED_COLUMN_PROPERTY = "generatedColumn";
    public static final String NAME_PROPERTY = "name";

    public static final String GENERATED_COLUMN_PK_COLUMN = "GENERATED_COLUMN";

    public void setGeneratedColumn(Integer generatedColumn) {
        writeProperty("generatedColumn", generatedColumn);
    }
    public Integer getGeneratedColumn() {
        return (Integer)readProperty("generatedColumn");
    }
    
    
    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
}
