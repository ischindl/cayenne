package org.objectstyle.cayenne.testdo.inherit.auto;

import java.util.List;

/** Class _Employee was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _Employee extends org.objectstyle.cayenne.testdo.inherit.AbstractPerson {

    public static final String NAME_PROPERTY = "name";
    public static final String PERSON_TYPE_PROPERTY = "personType";
    public static final String SALARY_PROPERTY = "salary";
    public static final String ADDRESSES_PROPERTY = "addresses";
    public static final String TO_DEPARTMENT_PROPERTY = "toDepartment";

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
    
    
    public void setSalary(Float salary) {
        writeProperty("salary", salary);
    }
    public Float getSalary() {
        return (Float)readProperty("salary");
    }
    
    
    public void addToAddresses(org.objectstyle.cayenne.testdo.inherit.Address obj) {
        addToManyTarget("addresses", obj, true);
    }
    public void removeFromAddresses(org.objectstyle.cayenne.testdo.inherit.Address obj) {
        removeToManyTarget("addresses", obj, true);
    }
    public List getAddresses() {
        return (List)readProperty("addresses");
    }
    
    
    public void setToDepartment(org.objectstyle.cayenne.testdo.inherit.Department toDepartment) {
        setToOneTarget("toDepartment", toDepartment, true);
    }

    public org.objectstyle.cayenne.testdo.inherit.Department getToDepartment() {
        return (org.objectstyle.cayenne.testdo.inherit.Department)readProperty("toDepartment");
    } 
    
    
}
