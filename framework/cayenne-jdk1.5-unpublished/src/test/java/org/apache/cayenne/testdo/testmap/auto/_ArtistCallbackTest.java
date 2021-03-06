package org.apache.cayenne.testdo.testmap.auto;

/** Class _ArtistCallbackTest was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public abstract class _ArtistCallbackTest extends org.apache.cayenne.CayenneDataObject {

    public static final String ARTIST_NAME_PROPERTY = "artistName";
    public static final String DATE_OF_BIRTH_PROPERTY = "dateOfBirth";


    public void setArtistName(String artistName) {
        writeProperty("artistName", artistName);
    }
    public String getArtistName() {
        return (String)readProperty("artistName");
    }
    
    
    public void setDateOfBirth(java.util.Date dateOfBirth) {
        writeProperty("dateOfBirth", dateOfBirth);
    }
    public java.util.Date getDateOfBirth() {
        return (java.util.Date)readProperty("dateOfBirth");
    }
    
    
}
