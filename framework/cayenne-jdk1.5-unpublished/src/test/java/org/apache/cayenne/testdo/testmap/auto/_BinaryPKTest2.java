package org.apache.cayenne.testdo.testmap.auto;

/** Class _BinaryPKTest2 was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public abstract class _BinaryPKTest2 extends org.apache.cayenne.CayenneDataObject {

    public static final String DETAIL_NAME_PROPERTY = "detailName";
    public static final String TO_BINARY_PKMASTER_PROPERTY = "toBinaryPKMaster";

    public static final String ID_PK_COLUMN = "ID";

    public void setDetailName(String detailName) {
        writeProperty("detailName", detailName);
    }
    public String getDetailName() {
        return (String)readProperty("detailName");
    }
    
    
    public void setToBinaryPKMaster(org.apache.cayenne.testdo.testmap.BinaryPKTest1 toBinaryPKMaster) {
        setToOneTarget("toBinaryPKMaster", toBinaryPKMaster, true);
    }

    public org.apache.cayenne.testdo.testmap.BinaryPKTest1 getToBinaryPKMaster() {
        return (org.apache.cayenne.testdo.testmap.BinaryPKTest1)readProperty("toBinaryPKMaster");
    } 
    
    
}
