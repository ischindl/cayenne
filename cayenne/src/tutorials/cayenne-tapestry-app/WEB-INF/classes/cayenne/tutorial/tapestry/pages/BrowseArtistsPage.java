package cayenne.tutorial.tapestry.pages;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.query.Ordering;
import org.objectstyle.cayenne.query.SelectQuery;

import cayenne.tutorial.tapestry.domain.Artist;
import cayenne.tutorial.tapestry.domain.Painting;

/**
 * Page that shows a list of artists with their paintings.
 * 
 * @author Eric Schneider
 */
public abstract class BrowseArtistsPage extends ApplicationPage {

    // properties are defined as abstract setters and getters
    // and are declared in BrowseArtistsPage.page file
    public abstract void setArtist(Artist value);
    public abstract Artist getArtist();

    public abstract void setPainting(Painting value);
    public abstract Painting getPainting();

    public abstract void setArtistList(List value);
    public abstract List getArtistList();

    public boolean getIsOnDisplay() {
        return (getPainting().getToGallery() != null);
    }

    public void addPaintingAction(IRequestCycle cycle) {
        AddPaintingPage nextPage = (AddPaintingPage) cycle.getPage("AddPaintingPage");

        nextPage.setArtist(getArtist());

        cycle.activate(nextPage);
    }

    public void submitPaintingToGalleryAction(IRequestCycle cycle) {
        ChooseGalleryPage nextPage =
            (ChooseGalleryPage) cycle.getPage("ChooseGalleryPage");

        nextPage.setPainting(getPainting());
        cycle.activate(nextPage);
    }

    public void pageBeginRender(PageEvent event) {

        // refetch artists only if they are not initialized
        if (getArtistList() == null) {
            DataContext ctxt = getVisitDataContext();

            SelectQuery query = new SelectQuery(Artist.class);
            Ordering ordering = new Ordering("artistName", Ordering.ASC);
            query.addOrdering(ordering);

            setArtistList(ctxt.performQuery(query));
        }
    }
}
