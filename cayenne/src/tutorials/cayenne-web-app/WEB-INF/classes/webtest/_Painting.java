package webtest;

import org.objectstyle.cayenne.CayenneDataObject;

/** Class _Painting was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _Painting extends CayenneDataObject {

    public void setEstimatedPrice(java.math.BigDecimal estimatedPrice) {
        writeProperty("estimatedPrice", estimatedPrice);
    }
    public java.math.BigDecimal getEstimatedPrice() {
        return (java.math.BigDecimal)readProperty("estimatedPrice");
    }
    
    
    public void setPaintingTitle(String paintingTitle) {
        writeProperty("paintingTitle", paintingTitle);
    }
    public java.lang.String getPaintingTitle() {
        return (String)readProperty("paintingTitle");
    }
    
    
    public void setToGallery(webtest.Gallery toGallery) {
        setToOneTarget("toGallery", toGallery, true);
    }
    
    public webtest.Gallery getToGallery() {
        return (webtest.Gallery)readProperty("toGallery");
    } 
    
    
    public void setToArtist(webtest.Artist toArtist) {
        setToOneTarget("toArtist", toArtist, true);
    }
    
    public webtest.Artist getToArtist() {
        return (webtest.Artist)readProperty("toArtist");
    } 
    
    
}
