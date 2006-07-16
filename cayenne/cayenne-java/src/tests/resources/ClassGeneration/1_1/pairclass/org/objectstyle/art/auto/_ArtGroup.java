package org.objectstyle.art.auto;

import java.util.List;

/** Class _ArtGroup was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _ArtGroup extends org.objectstyle.cayenne.CayenneDataObject {

    public static final String NAME_PROPERTY = "name";
    public static final String ARTIST_ARRAY_PROPERTY = "artistArray";
    public static final String CHILD_GROUPS_ARRAY_PROPERTY = "childGroupsArray";
    public static final String TO_PARENT_GROUP_PROPERTY = "toParentGroup";

    public static final String GROUP_ID_PK_COLUMN = "GROUP_ID";

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
    public void addToArtistArray(org.objectstyle.art.Artist obj) {
        addToManyTarget("artistArray", obj, true);
    }
    public void removeFromArtistArray(org.objectstyle.art.Artist obj) {
        removeToManyTarget("artistArray", obj, true);
    }
    public List getArtistArray() {
        return (List)readProperty("artistArray");
    }
    
    
    public void addToChildGroupsArray(org.objectstyle.art.ArtGroup obj) {
        addToManyTarget("childGroupsArray", obj, true);
    }
    public void removeFromChildGroupsArray(org.objectstyle.art.ArtGroup obj) {
        removeToManyTarget("childGroupsArray", obj, true);
    }
    public List getChildGroupsArray() {
        return (List)readProperty("childGroupsArray");
    }
    
    
    public void setToParentGroup(org.objectstyle.art.ArtGroup toParentGroup) {
        setToOneTarget("toParentGroup", toParentGroup, true);
    }

    public org.objectstyle.art.ArtGroup getToParentGroup() {
        return (org.objectstyle.art.ArtGroup)readProperty("toParentGroup");
    } 
    
    
}
