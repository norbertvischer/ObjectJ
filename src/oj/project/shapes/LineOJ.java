/*
 * LineOJ.java
 * fully documented 14.3.2010
 *
 * subclassing YtemOJ
 */
package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class LineOJ extends YtemOJ {

    private static final long serialVersionUID = -6410679215391582974L;

    public LineOJ() {
    }

    public LineOJ(String definition) {
        this.definition = definition;
    }

    public LineOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_LINE;
    }

    public boolean contains(double x, double y, double z) {
        return false;
    }

    public boolean contains(LocationOJ p) {
        return false;
    }
}
