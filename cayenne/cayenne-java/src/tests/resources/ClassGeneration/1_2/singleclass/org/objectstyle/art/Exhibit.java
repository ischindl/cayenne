package org.objectstyle.art;

import java.util.Date;
import java.util.List;

import org.objectstyle.cayenne.CayenneDataObject;

public class Exhibit extends CayenneDataObject {

    public static final String CLOSING_DATE_PROPERTY = "closingDate";
    public static final String OPENING_DATE_PROPERTY = "openingDate";
    public static final String ARTIST_EXHIBIT_ARRAY_PROPERTY = "artistExhibitArray";
    public static final String TO_GALLERY_PROPERTY = "toGallery";

    public static final String EXHIBIT_ID_PK_COLUMN = "EXHIBIT_ID";

    public void setClosingDate(Date closingDate) {
        writeProperty("closingDate", closingDate);
    }
    public Date getClosingDate() {
        return (Date)readProperty("closingDate");
    }
    
    
    public void setOpeningDate(Date openingDate) {
        writeProperty("openingDate", openingDate);
    }
    public Date getOpeningDate() {
        return (Date)readProperty("openingDate");
    }
    
    
    public void addToArtistExhibitArray(ArtistExhibit obj) {
        addToManyTarget("artistExhibitArray", obj, true);
    }
    public void removeFromArtistExhibitArray(ArtistExhibit obj) {
        removeToManyTarget("artistExhibitArray", obj, true);
    }
    public List getArtistExhibitArray() {
        return (List)readProperty("artistExhibitArray");
    }
    
    
    public void setToGallery(Gallery toGallery) {
        setToOneTarget("toGallery", toGallery, true);
    }
    public Gallery getToGallery() {
        return (Gallery)readProperty("toGallery");
    } 
    
    
}



