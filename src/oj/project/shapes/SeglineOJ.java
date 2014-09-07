/*
 * SeglineOJ.java
 * fully documented 14.3.2010
 *
 * subclassing YtemOJ
 *
 */package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;


public class SeglineOJ extends YtemOJ {

    private static final long serialVersionUID = -3958118581920543187L;

    public SeglineOJ() {
    }

    public SeglineOJ(String definition) {
        this.definition = definition;
    }

    public SeglineOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_SEGLINE;
    }

    public boolean contains(double x, double y, double z) {
        return false;
    }

    public boolean contains(LocationOJ p) {
        return false;
    }
}
