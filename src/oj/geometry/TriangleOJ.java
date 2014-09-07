package oj.geometry;

import oj.project.LocationOJ;

/**
 * Handles calculations in a triangle
 * 
 * 
 *  from A, B and C, closest line from C to A-B is calculated.
 *  :      C, Phi
 *  :     /|\
 *  : bb / | \ aa
 *  :   /  hc \
 *  :  /   |   \
 *  : A----P----B
 *  : cca   ccb
 *  :     cc
 *   
 */
public class TriangleOJ {

    double area;
    double aa;
    double bb;
    double cc;
    double phiC;
    double phiA;
    double phiB;
    double hc;
    double cca;
    double ccb;
    double spark;
    double minSpark;
    double signedMinSpark;
    double leftRight; //angle tells if triangle is left or righthanded
    double minLeftRight;
    double ccaAccu;
    double minCcaAccu;
    double ccAccu;
    boolean alphaGreater90;
    boolean betaGreater90;
    public int leftEdge;
    int rightEdge;
    int vertexIndex;
    LocationOJ locP = new LocationOJ();
    public double impactX = 0;
    public double impactY = 0;
    public double impactZ = 0;

    /** Creates a new instance of TriangleOJ */
    public TriangleOJ() {
        minSpark = 1e20;
        minCcaAccu = 1e20;
        ccaAccu = 0;
        ccAccu = 0;
    }

    public LocationOJ getLocationP() {
        return locP;
    }

    /**
     *
     * @return area of triangle
     */
    public double getArea() {
        return area;
    }
/**
 * see drawing at top of this doc
 */
    public double getAa() {
        return aa;
    }

    public double getBb() {
        return bb;
    }

    public int leftEdge() {
        return leftEdge;
    }

    public int rightEdge() {
        return rightEdge;
    }

    public double getImpactX() {
        return impactX;
    }

    public double getImpactY() {
        return impactY;
    }

    /**
     *
     * @return cc value
     */
    public double getCc() {
        return cc;
    }

    public double getHc() {
        return hc;
    }

    public double getCca() {
        return cca;
    }

    public double getCcb() {
        return ccb;
    }

    public double getPhiC() {
        return phiC;
    }

    public double getPhiA() {
        return phiA;
    }

    public double getPhiB() {
        return phiB;
    }

    public double getSpark() {
        return spark;
    }

    public double getCcAccu() {
        return ccAccu;
    }

    public double getCcaAccu() {
        return ccaAccu;
    }

    public double getMinSpark() {
        if (minSpark == 1e20) {
            return 0;
        }
        return minSpark;
    }

    public double getSignedMinSpark() {
        return signedMinSpark;
    }

    public double getMinCcaAccu() {
        return minCcaAccu;
    }

    public int getLeftEdge() {
        return leftEdge;
    }

    public double getMinLeftRight() {
        return minLeftRight;
    }

    public int getRightEdge() {
        return rightEdge;
    }

    void updateMin() {
        minSpark = spark;

        signedMinSpark = minSpark;
        if (leftRight < 0) {
            signedMinSpark = -signedMinSpark;
        }
        impactX = locP.getX();
        impactY = locP.getY();
        impactZ = locP.getZ();
        minCcaAccu = ccaAccu;
        minLeftRight = leftRight;
        if (alphaGreater90 || aa == 0) {//15.5.2009
            leftEdge = vertexIndex;
            rightEdge = vertexIndex;
        } else if (betaGreater90 || bb == 0) {//15.5.2009
            rightEdge = vertexIndex + 1;
            leftEdge = rightEdge;
        } else {
            leftEdge = vertexIndex;
            rightEdge = vertexIndex + 1;
        }
    }

    double sqr(double a) {
        return a * a;
    }

    double sqrt(double a) {
        return Math.sqrt(Math.abs(a));
    }

    /** scales the three corners, then calls other calcTriange */
    public void calcTriangle(LocationOJ locA, LocationOJ locB, LocationOJ locC, LocationOJ scale, int vertexIndex) {
        LocationOJ scaledLocA = new LocationOJ(locA.getX() * scale.getX(), locA.getY() * scale.getY(), locA.getZ() * scale.getZ());
        LocationOJ scaledLocB = new LocationOJ(locB.getX() * scale.getX(), locB.getY() * scale.getY(), locB.getZ() * scale.getZ());
        LocationOJ scaledLocC = new LocationOJ(locC.getX() * scale.getX(), locC.getY() * scale.getY(), locC.getZ() * scale.getZ());
        calcTriangle(scaledLocA, scaledLocB, scaledLocC, vertexIndex);
    }

    /** calculates height, angle, area etc of the triangle ABC,
     * of which A - B is the base;
     * @param locA etc no further scaling is applied
     */
    public void calcTriangle(LocationOJ locA, LocationOJ locB, LocationOJ locC) {
        calcTriangle(locA, locB, locC, -1);


    }

     /** calculates height, angle, area etc of the triangle ABC,
     * of which A - B is the base; vertexIndex is used for calculating leftEdge and rightEdge
     */
    public void calcTriangle(LocationOJ locA, LocationOJ locB, LocationOJ locC, int vertexIndex) {
        //indices A, B and C tell which points to use from the array
        double aa2 = sqr(locB.getX() - locC.getX()) + sqr(locB.getY() - locC.getY()) + sqr(locB.getZ() - locC.getZ());
        double bb2 = sqr(locA.getX() - locC.getX()) + sqr(locA.getY() - locC.getY()) + sqr(locA.getZ() - locC.getZ());
        double cc2 = sqr(locA.getX() - locB.getX()) + sqr(locA.getY() - locB.getY()) + sqr(locA.getZ() - locB.getZ());
        this.vertexIndex = vertexIndex;
        alphaGreater90 = false;
        betaGreater90 = false;
        aa = sqrt(aa2);
        bb = sqrt(bb2);

        if (cc2 < 1e-20) {
            locP.setX(locA.getX());
            locP.setY(locA.getY());
            locP.setZ(locA.getZ());
            spark = aa;
            cc = 0;
            cca = 0;
            ccb = 0;
            hc = aa;
            phiC = 0;
            alphaGreater90 = true;
            betaGreater90 = true;
            if (spark < minSpark) {
                minSpark = spark;
                //updateMin(); necessary? 20.8.2010
            }
            return;
        }

        cc = sqrt(cc2);

        double ss = (aa + bb + cc) / 2; //half perimeter
        area = sqrt(ss * (ss - aa) * (ss - bb) * (ss - cc)); //triangle area
        hc = 2 * area / cc;
        cca = sqrt(bb2 - sqr(hc));
        ccb = sqrt(aa2 - sqr(hc));

        if (cca > cc && bb > aa) {//hanging to the right
            locP.setX(locB.getX());
            locP.setY(locB.getY());
            locP.setZ(locB.getZ());
            spark = aa;
            cca = cc;
            ccb = 0;
            betaGreater90 = true;
        } else if (ccb > cc && bb < aa) {//hanging to the left
            locP.setX(locA.getX());
            locP.setY(locA.getY());
            locP.setZ(locA.getZ());
            spark = bb;
            ccb = cc;
            cca = 0;
            alphaGreater90 = true;
        } else {
            locP.setX(locA.getX() + cca / cc * (locB.getX() - locA.getX()));
            locP.setY(locA.getY() + cca / cc * (locB.getY() - locA.getY()));
            locP.setZ(locA.getZ() + cca / cc * (locB.getZ() - locA.getZ()));
            spark = hc;
        }
        ccaAccu += cca;
        ccAccu += cc;
        phiA = Math.atan(cca / hc);
        phiB = Math.atan(ccb / hc);
        double d1 = Math.abs(Math.abs(cca - ccb) - cc);
        double d2 = Math.abs(Math.abs(cca + ccb) - cc);
        if (d2 < d1) {
            phiC = phiA + phiB;
        } else {
            phiC = Math.abs(phiA - phiB);
        }
        phiC *= 180 / Math.PI;




        //left or right handed triangle?

        double orient1 = Math.atan2(locB.y - locA.y, locB.x - locA.x);
        double orient2 = Math.atan2(locC.y - locB.y, locC.x - locB.x);




        double deviation = (orient2 - orient1) * 180.0 / Math.PI;
        while (deviation <= -180.0) {
            deviation += 360;
        }
        while (deviation > 180.0) {
            deviation -= 360;
        }
        leftRight = deviation;// -180 .. 180


        if (spark < minSpark) {
            updateMin();
        }
        ccaAccu += ccb;
    }
}
