/*
 * AngleOJ.java
 * fully documented 14.3.2010
 *
 * subclassing YtemOJ
 
 */
package oj.project.shapes;

import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class AngleOJ extends YtemOJ {

    private static final long serialVersionUID = 7279081455079842210L;

    public AngleOJ() {
    }

    public AngleOJ(String definition) {
        this.definition = definition;
    }

    public AngleOJ(String definition, int stackIndex) {
        this.stackIndex = stackIndex;
        this.definition = definition;
    }

    public int getType() {
        return YtemDefOJ.YTEM_TYPE_ANGLE;
    }

    public boolean contains(double x, double y, double z) {
        return false;
    }

    public boolean contains(LocationOJ p) {
        return false;
    }
}
