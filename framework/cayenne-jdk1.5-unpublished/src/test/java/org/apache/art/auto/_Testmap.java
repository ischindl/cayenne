package org.apache.art.auto;

import java.util.List;

import org.apache.art.Artist;
import org.apache.art.Gallery;
import org.apache.art.Painting;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.NamedQuery;

/**
 * This class was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public class _Testmap {

    public List<Painting> performObjectQuery(ObjectContext context , Artist artist) {
        String[] parameters = {
            "artist",
        };

        Object[] values = {
            artist,
        };

        return context.performQuery(new NamedQuery("ObjectQuery", parameters, values));
    }

    public List<Artist> performParameterizedQueryWithLocalCache(ObjectContext context , String name) {
        String[] parameters = {
            "name",
        };

        Object[] values = {
            name,
        };

        return context.performQuery(new NamedQuery("ParameterizedQueryWithLocalCache", parameters, values));
    }

    public List<Artist> performParameterizedQueryWithSharedCache(ObjectContext context , String name) {
        String[] parameters = {
            "name",
        };

        Object[] values = {
            name,
        };

        return context.performQuery(new NamedQuery("ParameterizedQueryWithSharedCache", parameters, values));
    }

    public List<Artist> performQueryWithLocalCache(ObjectContext context ) {
        return context.performQuery(new NamedQuery("QueryWithLocalCache"));
    }

    public List<Artist> performQueryWithOrdering(ObjectContext context ) {
        return context.performQuery(new NamedQuery("QueryWithOrdering"));
    }

    public List<Gallery> performQueryWithPrefetch(ObjectContext context ) {
        return context.performQuery(new NamedQuery("QueryWithPrefetch"));
    }

    public List<Artist> performQueryWithQualifier(ObjectContext context , String param1) {
        String[] parameters = {
            "param1",
        };

        Object[] values = {
            param1,
        };

        return context.performQuery(new NamedQuery("QueryWithQualifier", parameters, values));
    }

    public List<Artist> performQueryWithSharedCache(ObjectContext context ) {
        return context.performQuery(new NamedQuery("QueryWithSharedCache"));
    }
}