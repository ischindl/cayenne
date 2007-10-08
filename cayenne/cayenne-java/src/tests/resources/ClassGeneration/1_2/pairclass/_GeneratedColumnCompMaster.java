package org.apache.art.auto;

import java.util.List;

import org.apache.art.GeneratedColumnCompKey;
import org.apache.cayenne.CayenneDataObject;

/** 
 * Class _GeneratedColumnCompMaster was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually, 
 * since it may be overwritten next time code is regenerated. 
 * If you need to make any customizations, please use subclass. 
 */
public class _GeneratedColumnCompMaster extends CayenneDataObject {

    public static final String NAME_PROPERTY = "name";
    public static final String TO_DETAIL_PROPERTY = "toDetail";

    public static final String ID_PK_COLUMN = "ID";

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
    public void addToToDetail(GeneratedColumnCompKey obj) {
        addToManyTarget("toDetail", obj, true);
    }
    public void removeFromToDetail(GeneratedColumnCompKey obj) {
        removeToManyTarget("toDetail", obj, true);
    }
    public List getToDetail() {
        return (List)readProperty("toDetail");
    }
    
    
}
