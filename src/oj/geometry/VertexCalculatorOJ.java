/*
 * VertexCalculatorOJ.java
 * fully documented
 */
package oj.geometry;

import ij.IJ;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.util.Stack;
import oj.project.LocationOJ;
import oj.project.YtemOJ;

/**
 * This class works with a stack of 3D locations to obtain geometric results
 * like partial path, crosspoint of two lines, area of a polygon, inner/outer
 * circle of a triangle etc. (At this moment, only path and partialpath and
 * relPartialPathe is implemented.) After calculation, the result is put into a
 * slot of scalarResult or vertexResult. Calculation is aware of voxelsize.
 * Later, the vertex calculator will also be used for Turtlegraphics.
 *
 * @author norbert
 */
public class VertexCalculatorOJ extends Stack {

    private LocationOJ vertexScale = new LocationOJ(1.0, 1.0, 1.0);
    public boolean threeD = false;
    private LocationOJ innerCircleCenter = new LocationOJ(0, 0, 0);
    public double innerCircleCenterX;
    public double innerCircleCenterY;
    public double innerCircleRadius;
    private LocationOJ outerCircleCenter = new LocationOJ(0, 0, 0);
    public double outerCircleCenterX;
    public double outerCircleCenterY;
    public double outerCircleRadius;
    public double partialPath = 0;
    public double totalLength = 0;
    public double relPartialPath = 0;
    public double minSpark = 0;
    public double signedMinSpark = 0;
    public int leftEdge = 0;//if leftEdge == rightEdge, impact is on the edge
    public int rightEdge = 0;
    public double impactX = 0;
    public double impactY = 0;
    public double sparkDev = 0; //
    public LocationOJ crossPoint = new LocationOJ(0, 0, 0);//of two lines
    // private LocationOJ dropPoint = new LocationOJ(0, 0, 0);//where drop line meets line
    public double height = 0;//height of triangle
    public double angle = 0;
    public double deviation = 0;
    public double orientation = 0;
    public double area = 0;
    public double perimeter = 0;
    public double partialPositionX = 0;
    public double partialPositionY = 0;
    double nan = Double.NaN;

//    public VertexCalculatorOJ() {
//    }
    /**
     * Pushes all points of ytem onto the vertex stack
     *
     * @param ytem
     */
    public void pushYtem(YtemOJ ytem) {
        for (int pt = 0; pt < ytem.getLocationsCount(); pt++) {
            LocationOJ loc = ytem.getLocation(pt);
            if (threeD) {
                push(new LocationOJ(loc.x, loc.y, loc.z));//15.5.2009
            } else {
                push(new LocationOJ(loc.x, loc.y, 0));//6.4.2012
            }
        }
    }

    /**
     * Pushes a 3D location onto the vertex stack
     *
     * @param loc
     */
    public void pushLocation(LocationOJ loc) {
        if (threeD) {
            push(new LocationOJ(loc.x, loc.y, loc.z));//15.5.2009
        } else {
            push(new LocationOJ(loc.x, loc.y, 0));//6.4.2012
        }
    }

    /**
     * For applying three scale factors (we have the problem when to scale)
     *
     */
    public void setVertexScale(LocationOJ loc) {
        vertexScale = loc;
    }

    /**
     * Four 2D points are used to define two lines p1-p2 and p3-p4. Returns the
     * crosspoint of the two lines, or NaN if they are parallel. Result is a 3D
     * point, with z being set to NaN.
     */
    public LocationOJ calcCrossPoint(double p1h, double p1v, double p2h, double p2v, double p3h, double p3v, double p4h, double p4v) {

        double a1 = 0;
        double a2 = 0.0;
        double b1 = 0;
        double b2 = 0;
        double dx1;
        double dx2;

        LocationOJ result = new LocationOJ(nan, nan, nan);
        dx1 = (p2h - p1h);
        dx2 = p4h - p3h;
        if (dx1 == 0 && dx2 == 0) {
            return result;
        }
        if (dx1 != 0.0) {
            a1 = (p2v - p1v) / dx1;
            b1 = p1v - p1h * a1;
        }

        if (dx2 != 0.0) {
            a2 = (p4v - p3v) / dx2;
            b2 = p3v - p3h * a2;
        }

        if (dx1 != 0.0 && dx2 != 0.0) {
            if (a1 == a2) {
                return result; //lines are parallel
            } else {
                result.x = (float) (-(b1 - b2) / (a1 - a2));
                result.y = (float) (a1 * result.x + b1);//changed to float 17.4.2010
                result.z = (float) nan;
            }
        }
        if (dx1 == 0.0) {
            result.x = (float) p1h;
            result.y = (float) (a2 * result.x + b2);
        }
        if (dx2 == 0) {
            result.x = (float) p3h;
            result.y = (float) (a1 * result.x + b1);
        }

        return result;
    }

    /**
     * Clears the Java Stack
     *
     * @param dim not used here
     */
    public void init(String dim) {
        clear();//the stack
        threeD = false;
    }

    /**
     * No operation so far
     *
     */
    public void clearZ() {
        if (true) {
            return;
        }
        for (int index = 0; index < this.size(); index++) {
            LocationOJ loc = (LocationOJ) get(index);
            loc.z = 0;
            this.set(index, loc);
        }
    }

    /**
     * Performs a geometric calculation from the top of the vertex stack, or the
     * entire vertex stack. For example if algorithm is "orientation", the top
     * two entries are regarded as two points on a line, whose orientation is
     * put into class variable orientation.
     *
     * @param algorithm
     */
    public void calc(String algorithm) {
        algorithm = algorithm.toLowerCase();
        if (!threeD) {//set all z to zero //15.5.2009
            clearZ();
        }
        LocationOJ aa;
        LocationOJ bb;
        LocationOJ cc;
        LocationOJ dd;

        boolean isRelPPath = algorithm.equalsIgnoreCase("relPartialPath");
        boolean isPerimeter = algorithm.equalsIgnoreCase("perimeter");
        boolean isTotalPath = algorithm.equalsIgnoreCase("totalPath");
        boolean isAbsPPath = algorithm.equalsIgnoreCase("partialPath");
        boolean isCrossPt = algorithm.equalsIgnoreCase("crosspoint");
        boolean isOuterCircle = algorithm.equalsIgnoreCase("outerCircle");
        boolean isInnerCircle = algorithm.equalsIgnoreCase("innerCircle");
        boolean isDeviation = algorithm.equalsIgnoreCase("deviation");
        boolean isHeight = algorithm.equalsIgnoreCase("height");
        boolean isArea = algorithm.equalsIgnoreCase("area");
        boolean isAngle = algorithm.equalsIgnoreCase("angle");
        boolean isOrientation = algorithm.equalsIgnoreCase("orientation");
        boolean isPartialPosition = algorithm.startsWith("partialposition");

        TriangleOJ tri = new TriangleOJ();
        if (isOrientation) {
            if (size() < 2) {
                return;
            }
            double x1 = ((LocationOJ) get(size() - 2)).getX();
            double y1 = ((LocationOJ) get(size() - 2)).getY();
            double x2 = ((LocationOJ) get(size() - 1)).getX();
            double y2 = ((LocationOJ) get(size() - 1)).getY();
            orientation = Math.atan2(y2 - y1, x2 - x1) * 180.0 / Math.PI;
            return;
        }

        if (isDeviation || isAngle) {
            if (size() < 3) {
                return;
            }
            double x0 = ((LocationOJ) get(size() - 3)).getX();
            double y0 = ((LocationOJ) get(size() - 3)).getY();
            double x1 = ((LocationOJ) get(size() - 2)).getX();
            double y1 = ((LocationOJ) get(size() - 2)).getY();
            double x2 = ((LocationOJ) get(size() - 1)).getX();
            double y2 = ((LocationOJ) get(size() - 1)).getY();

            double angle1 = Math.atan2(y1 - y0, x1 - x0) - Math.atan2(y2 - y1, x2 - x1);

            angle1 *= 180.0 / Math.PI;
            angle = 180 - Math.abs(angle1);
            angle1 = -angle1; //clockwise is positive, ccw is negative
            while (angle1 <= -180.0) {
                angle1 += 360;
            }
            while (angle1 > 180.0) {
                angle1 -= 360;
            }
            //scalarResult[0] = angle;
            deviation = angle1;
            return;
        }

        if (isHeight) {
            aa = (LocationOJ) get(0);
            bb = (LocationOJ) get(1);
            cc = (LocationOJ) get(2);
            tri.calcTriangle(aa, cc, bb);
            height = tri.hc;
            return;
        }

        if (isRelPPath || isAbsPPath) {
            cc = (LocationOJ) peek(); //point to be projected is on the top of stack
            int maxIndex = this.size() - 2;
            for (int index = 0; index < maxIndex; index++) {
                aa = (LocationOJ) get(index);
                bb = (LocationOJ) get(index + 1);
                tri.calcTriangle(aa, bb, cc, vertexScale, index);
            }
            partialPath = tri.getMinCcaAccu();
            totalLength = tri.getCcAccu();
            minSpark = tri.getMinSpark();
            signedMinSpark = tri.getSignedMinSpark();//19.8.2010
            relPartialPath = partialPath / totalLength;
            leftEdge = tri.getLeftEdge();
            rightEdge = tri.getRightEdge();
            impactX = tri.getImpactX();
            impactY = tri.getImpactY();

            return;
        }

        if (isTotalPath || isPerimeter || isPartialPosition) {
            double subLength = 0;
            double pPosX = nan;
            double pPosY = nan;
            double fraction = 0;
            int passes = 1;
            if (isPartialPosition) {
                passes = 2;
                String subStr = algorithm.replace('%', ' ');
                int index = subStr.indexOf(" ");
                subStr = subStr.substring(index);
                fraction = Double.parseDouble(subStr);
                if (algorithm.contains("%")) {
                    fraction *= 0.01;
                }
                if (fraction < 0) {
                    fraction = 0;
                }
                if (fraction > 1) {
                    fraction = 1;
                }
            }


            for (int pass = 1; pass <= passes; pass++) {
                if (pass == 2) {
                    subLength = fraction * totalLength;
                }
                double len = 0;
                int maxIndex = this.size();
                for (int index = 0; index < maxIndex; index++) {
                    aa = (LocationOJ) get(index);
                    if (index < maxIndex - 1) {
                        bb = (LocationOJ) get(index + 1);
                    } else {
                        bb = (LocationOJ) get(0);
                    }
                    if (index < maxIndex - 1 || isPerimeter) {
                        double thisSegment = Math.sqrt(sqr(aa.x - bb.x) + sqr(aa.y - bb.y) + sqr(aa.z - bb.z));

                        len = len + thisSegment;
                        if (pass == 2) {
                            double delta = len - subLength;
                            if (delta >= 0 && Double.isNaN(pPosX)) {
                                pPosX = bb.x - delta / thisSegment * (bb.x - aa.x);
                                pPosY = bb.y - delta / thisSegment * (bb.y - aa.y);
                            }

                        }
                    }
                }
                if (isPerimeter) {
                    perimeter = len;
                }
                if (isTotalPath || isPartialPosition) {
                    totalLength = len;
                }
                if (isPartialPosition) {
                    partialPositionX = pPosX;
                    partialPositionY = pPosY;
                }
            }
            return;
        }

        if (isCrossPt) {
            aa = (LocationOJ) get(0);
            bb = (LocationOJ) get(1);
            cc = (LocationOJ) get(2);
            dd = (LocationOJ) get(3);
            crossPoint = calcCrossPoint(aa.getX(), aa.getY(), bb.getX(), bb.getY(), cc.getX(), cc.getY(), dd.getX(), dd.getY());
            return;
        }

        if (isArea) {
            area = calcArea();
            return;

        }
        if (isInnerCircle || isOuterCircle) {
            double Ax = ((LocationOJ) get(0)).getX();
            double Ay = ((LocationOJ) get(0)).getY();
            double Bx = ((LocationOJ) get(1)).getX();
            double By = ((LocationOJ) get(1)).getY();
            double Cx = ((LocationOJ) get(2)).getX();
            double Cy = ((LocationOJ) get(2)).getY();
            if (isOuterCircle) {
                double midABx = (Bx + Ax) / 2;
                double midABy = (By + Ay) / 2;
                double midCBx = (Cx + Bx) / 2;
                double midCBy = (Cy + By) / 2;
                double dropABx = midABx + By - Ay;
                double dropABy = midABy - (Bx - Ax);
                double dropCBx = midCBx + Cy - By;
                double dropCBy = midCBy - (Cx - Bx);
                //vertexResult[0] = calcCrossPoint(midABx, midABy, dropABx, dropABy, midCBx, midCBy, dropCBx, dropCBy);
                outerCircleCenter = calcCrossPoint(midABx, midABy, dropABx, dropABy, midCBx, midCBy, dropCBx, dropCBy);
                outerCircleCenterX = outerCircleCenter.x;
                outerCircleCenterY = outerCircleCenter.y;
                double dx = outerCircleCenter.x - Ax;
                double dy = outerCircleCenter.y - Ay;
                //scalarResult[0] = Math.sqrt(dx * dx + dy * dy);
                outerCircleRadius = Math.sqrt(dx * dx + dy * dy);
            }
            /*
             *              A (0)
             *             / \
             *      sideC /   \sideB
             *           /  m  \
             *          /       \
             *        B(1)-------C(2)
             *            sideA
             **/
            if (isInnerCircle) {
                double sideC = Math.sqrt(sqr(Bx - Ax) + sqr(By - Ay));
                double sideB = Math.sqrt(sqr(Cx - Ax) + sqr(Cy - Ay));
                double sideA = Math.sqrt(sqr(Cx - Bx) + sqr(Cy - By));

                //divide angle C:
                double bSissorAx = Cx + (Ax - Cx) * sideA / sideB;
                double bSissorAy = Cy + (Ay - Cy) * sideA / sideB;
                double angleDividerCx = (Bx + bSissorAx) / 2;
                double angleDividerCy = (By + bSissorAy) / 2;

                //divide angle B:
                double cSissorAx = Bx + (Ax - Bx) * sideA / sideC;
                double cSissorAy = By + (Ay - By) * sideA / sideC;
                double angleDividerBx = (Cx + cSissorAx) / 2;
                double angleDividerBy = (Cy + cSissorAy) / 2;

                innerCircleCenter = calcCrossPoint(Cx, Cy, angleDividerCx, angleDividerCy, Bx, By, angleDividerBx, angleDividerBy);
                //vertexResult[0] = innerCircleCenter;
                innerCircleCenterX = innerCircleCenter.x;
                innerCircleCenterY = innerCircleCenter.y;
                double mx = innerCircleCenterX; //midpoint of inner circle
                double my = innerCircleCenterY;
                double sideBM = Math.sqrt(sqr(Bx - mx) + sqr(By - my));
                double sideCM = Math.sqrt(sqr(Cx - mx) + sqr(Cy - my));
                double s = (sideA + sideBM + sideCM) / 2; //half perimeter
                double area1 = Math.sqrt(s * (s - sideA) * (s - sideBM) * (s - sideCM));
                double rad = area1 / sideA * 2;
                innerCircleRadius = rad;

            }
            return;
        }
    }

    /**
     * squares two numbers
     *
     * @param val
     * @return
     */
    public double sqr(double val) {
        return val * val;
    }

    /**
     * Calculates the area of a polygon according to formula from John Russ.
     * Note that sub-areas are subtracted if polygon has the shape of an "8".
     *
     * @return polygon area
     */
    public double calcArea() {
        int size = this.size();
        if (size <= 2) {
            return 0;
        }
        double sumAreas = 0.0;
        double sumVolumesX = 0.0;
        double sumVolumesY = 0.0;
        double sumX;
        double sumY;
        double deltaX;
        double deltaY;
        LocationOJ prevLoc = (LocationOJ) this.get(size - 1);
        for (int i = 0; i < size; i++) {
            sumX = ((LocationOJ) this.get(i)).getX() + prevLoc.getX();
            sumY = ((LocationOJ) this.get(i)).getY() + prevLoc.getY();
            deltaX = ((LocationOJ) this.get(i)).getX() - prevLoc.getX();
            deltaY = ((LocationOJ) this.get(i)).getY() - prevLoc.getY();

            sumVolumesX = sumVolumesX + sumX * sumX * deltaY; //Eq 3. p 489
            sumVolumesY = sumVolumesY + sumY * sumY * deltaX; //Eq 3. p 489
            sumAreas = sumAreas + sumX * deltaY; //Eq 4. p 490}
            prevLoc = (LocationOJ) this.get(i);
        }
        return Math.abs(sumAreas / 2);
    }

    public static double[] getSegmentLengths(double[] x, double[] y) {
        int len = x.length;
        double[] segLengths = new double[len];
        for (int mid = 0; mid < len; mid++) {
            int right = (mid + 1) % len;
            double dx = x[right] - x[mid];
            double dy = y[right] - y[mid];
            segLengths[mid] = Math.sqrt(dx * dx + dy * dy);
        }
        return segLengths;//segLengths[len - 1] is closing segment: to be omitted if it is a segmented line
    }

    public static double[] getSegmentAngles(double[] x, double[] y) {
        int len = x.length;
        double[] angles = new double[len];
        for (int mid = 0; mid < len; mid++) {
            int right = (mid + 1) % len;
            double dx = x[right] - x[mid];
            double dy = -(y[right] - y[mid]);
            angles[mid] = 180.0 / Math.PI *Math.atan2(dy, dx);
        }
        return angles;//angles[len - 1] is angle of closing segment: to be omitted if it is a segmented line
    }

    public static double[] getVertexAngles(double[] x, double[] y) {
        int len = x.length;
        double[] vAngles = new double[len];

        for (int mid = 0; mid < len; mid++) {
            int left = (mid + len - 1) % len;
            int right = (mid + 1) % len;
            double dotprod = (x[right] - x[mid]) * (x[left] - x[mid]) + (y[right] - y[mid]) * (y[left] - y[mid]);
            double crossprod = (x[right] - x[mid]) * (y[left] - y[mid]) - (y[right] - y[mid]) * (x[left] - x[mid]);
            double phi = 180.0 / Math.PI * Math.atan2(crossprod, dotprod);
            vAngles[mid] = phi;
        }
        return vAngles;//vAngles[0] at starting point to be omitted if it is a segmented line
    }

    //for segmented lines, not polygons
    public static double[] getSectors(float[] xxf, float[] yyf, double rad) {
        int len = xxf.length;
        double[] xx = new double[len];
        double[] yy = new double[len];
        for (int jj = 0; jj < xxf.length; jj++) {
            xx[jj] = xxf[jj];
            yy[jj] = yyf[jj];
        }
        double pi = Math.PI;
        double[] vAngles = getVertexAngles(xx, yy);
        double[] segAngles = getSegmentAngles(xx, yy);
        double phiSym, phiArc, phi;
        double dx, dy;//, px, py;
        for (int jj = 1; jj < len - 1; jj++) {

            phiSym = (segAngles[jj - 1] + segAngles[jj]) / 2;
            phiArc = 180 - vAngles[jj];
            int nPoints = 3;
            float[] arcPointsX = new float[nPoints + 1];
            float[] arcPointsY = new float[nPoints + 1];
            for (int pt = 0; pt < nPoints; pt++) {
                phi = phiSym - phiArc / 2 + pt * phiArc / (nPoints - 1);
                dx = rad * Math.cos(phi / 180 * pi);
                dy = rad * -Math.sin(phi / 180 * pi);
                arcPointsX[pt] = (float) (xx[jj] + dx);
                arcPointsY[pt] = (float) (yy[jj] + dy);
            }
            arcPointsX[nPoints] = (float) (xx[jj]);
            arcPointsY[nPoints] = (float) (yy[jj]);
            PolygonRoi polygon = new PolygonRoi(arcPointsX, arcPointsY, nPoints + 1, Roi.POLYGON);
            IJ.getImage().setRoi(polygon, true);
        }

        return null;
    }
}
