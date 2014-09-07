/*
 * RoiOJ.java
 * fully documented 14.3.2010
 *
 * subclassing YtemOJ
 */
package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class RoiOJ extends YtemOJ {

    private static final long serialVersionUID = -7558098201187378826L;

    public RoiOJ() {
    }

    public RoiOJ(String definition) {
        this.definition = definition;
    }

    public RoiOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_ROI;
    }

    public boolean contains(double x, double y, double z) {
        return false;
    }

    public boolean contains(LocationOJ p) {
        return false;
    }
}
