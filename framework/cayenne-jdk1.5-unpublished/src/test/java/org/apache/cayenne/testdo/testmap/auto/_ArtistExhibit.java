package org.apache.cayenne.testdo.testmap.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.testdo.testmap.Artist;
import org.apache.cayenne.testdo.testmap.Exhibit;

/**
 * Class _ArtistExhibit was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ArtistExhibit extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String TO_ARTIST_PROPERTY = "toArtist";
    @Deprecated
    public static final String TO_EXHIBIT_PROPERTY = "toExhibit";

    public static final String ARTIST_ID_PK_COLUMN = "ARTIST_ID";
    public static final String EXHIBIT_ID_PK_COLUMN = "EXHIBIT_ID";

    public static final Property<Artist> TO_ARTIST = new Property<Artist>("toArtist");
    public static final Property<Exhibit> TO_EXHIBIT = new Property<Exhibit>("toExhibit");

    public void setToArtist(Artist toArtist) {
        setToOneTarget("toArtist", toArtist, true);
    }

    public Artist getToArtist() {
        return (Artist)readProperty("toArtist");
    }


    public void setToExhibit(Exhibit toExhibit) {
        setToOneTarget("toExhibit", toExhibit, true);
    }

    public Exhibit getToExhibit() {
        return (Exhibit)readProperty("toExhibit");
    }


}
