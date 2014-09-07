/*
 * PolygonOJ.java
 *
 * subclassing YtemOJ
 * fully documented 14.3.2010
 */
package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class PolygonOJ extends YtemOJ {

    private static final long serialVersionUID = -7661063749485272939L;

    public PolygonOJ() {
    }

    public PolygonOJ(String definition) {
        this.definition = definition;
    }

    public PolygonOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_POLYGON;
    }

    public boolean contains(double x, double y, double z) {
        return false;
    }

    public boolean contains(LocationOJ p) {
        return false;
    }
}
