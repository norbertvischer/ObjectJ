/*
 * PointOJ.java
 * fully documented 14.3.2010
 *
 * subclassing YtemOJ
 */
package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class PointOJ extends YtemOJ {

    private static final long serialVersionUID = 6056647205485814665L;

    public PointOJ() {
    }

    public PointOJ(String definition) {
        this.definition = definition;
    }

    public PointOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_POINT;
    }

    public boolean contains(double x, double y, double z) {
        return contains(new LocationOJ(x, y, z));
    }

    public boolean contains(LocationOJ p) {
        return locations.indexOf(p) >= 0;
    }
}
