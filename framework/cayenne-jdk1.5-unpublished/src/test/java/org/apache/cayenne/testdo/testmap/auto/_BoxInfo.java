package org.apache.cayenne.testdo.testmap.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.testdo.testmap.Box;

/**
 * Class _BoxInfo was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _BoxInfo extends CayenneDataObject {

    public static final String COLOR_PROPERTY = "color";
    public static final String BOX_PROPERTY = "box";

    public static final String ID_PK_COLUMN = "ID";

    public void setColor(String color) {
        writeProperty("color", color);
    }
    public String getColor() {
        return (String)readProperty("color");
    }

    public void setBox(Box box) {
        setToOneTarget("box", box, true);
    }

    public Box getBox() {
        return (Box)readProperty("box");
    }


}
