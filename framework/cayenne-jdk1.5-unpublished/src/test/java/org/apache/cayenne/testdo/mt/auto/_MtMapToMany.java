package org.apache.cayenne.testdo.mt.auto;

import java.util.Map;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.testdo.mt.MtMapToManyTarget;

/**
 * Class _MtMapToMany was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _MtMapToMany extends CayenneDataObject {

    public static final String TARGETS_PROPERTY = "targets";

    public static final String ID_PK_COLUMN = "ID";

    public void addToTargets(MtMapToManyTarget obj) {
        addToManyTarget(TARGETS_PROPERTY, obj, true);
    }
    public void removeFromTargets(MtMapToManyTarget obj) {
        removeToManyTarget(TARGETS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public Map<Object, MtMapToManyTarget> getTargets() {
        return (Map<Object, MtMapToManyTarget>)readProperty(TARGETS_PROPERTY);
    }


}
