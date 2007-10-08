package org.apache.cayenne.testdo.inherit.auto;

/** Class _AbstractPerson was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _AbstractPerson extends org.apache.cayenne.CayenneDataObject {

    public static final String NAME_PROPERTY = "name";
    public static final String PERSON_TYPE_PROPERTY = "personType";

    public static final String PERSON_ID_PK_COLUMN = "PERSON_ID";

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
    public void setPersonType(String personType) {
        writeProperty("personType", personType);
    }
    public String getPersonType() {
        return (String)readProperty("personType");
    }
    
    
}
