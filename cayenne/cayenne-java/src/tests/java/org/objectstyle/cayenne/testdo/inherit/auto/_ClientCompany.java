package org.objectstyle.cayenne.testdo.inherit.auto;

import java.util.List;

/** Class _ClientCompany was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _ClientCompany extends org.objectstyle.cayenne.CayenneDataObject {

    public static final String NAME_PROPERTY = "name";
    public static final String REPRESENTATIVES_PROPERTY = "representatives";

    public static final String CLIENT_COMPANY_ID_PK_COLUMN = "CLIENT_COMPANY_ID";

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
    public void addToRepresentatives(org.objectstyle.cayenne.testdo.inherit.CustomerRepresentative obj) {
        addToManyTarget("representatives", obj, true);
    }
    public void removeFromRepresentatives(org.objectstyle.cayenne.testdo.inherit.CustomerRepresentative obj) {
        removeToManyTarget("representatives", obj, true);
    }
    public List getRepresentatives() {
        return (List)readProperty("representatives");
    }
    
    
}
