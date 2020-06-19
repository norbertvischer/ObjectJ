/*
 * LocationOJ.java
 * fully documented 7.3.2010
 */
package oj.project;

import ij.IJ;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.io.Serializable;

/** This class handles Locations. A location is a 3D point that is part of an ytem */
public class LocationOJ extends BaseAdapterOJ implements Serializable {

    private static final long serialVersionUID = -9082767972659187211L;
    public float x;//changed to float 17.4.2010
    public float y;
    public float z;
    public transient ImageProcessor ip;//12.8.2013

    public LocationOJ() {
    }

    public LocationOJ(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    /** @return  x position in fractional pixels */
    public double getX() {
        return x;
    }

    /** @return  y position in fractional pixels */
    public double getY() {
        return y;
    }

    /** @return  z position, which normally corresponds to slice number,
     * but is fractional. Expected range in a 3-slice stack: 0.5 .. 3.5
     */
    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = (float)x;
        changed = true;
    }

    public void setY(double y) {
        this.y = (float)y;
        changed = true;
    }

    public void setZ(double z) {
        this.z = (float)z;
        changed = true;
    }

    public void setLocation(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        changed = true;
    }

    /** static metod to calc sqr of distance between two 3D locations */
    public static double distanceSq(double X1, double Y1, double Z1, double X2, double Y2, double Z2) {
        X1 -= X2;
        Y1 -= Y2;
        Z1 -= Z2;
        return X1 * X1 + Y1 * Y1 + Z1 * Z1;
    }

    /** static metod to calc distance between two 3D locations */
    public static double distance(double X1, double Y1, double Z1, double X2, double Y2, double Z2) {
        X1 -= X2;
        Y1 -= Y2;
        Z1 -= Z2;
        return Math.sqrt(X1 * X1 + Y1 * Y1 + Z1 * Z1);
    }

    /** @return the square of the distance to another 3D location */
    public double distanceSq(LocationOJ pt) {
        double PX = pt.getX() - this.getX();
        double PY = pt.getY() - this.getY();
        double PZ = pt.getZ() - this.getZ();
        return PX * PX + PY * PY + PZ * PZ;
    }

    /** @return the distance to another 3D location  */
    public double distance(double PX, double PY, double PZ) {
        PX -= getX();
        PY -= getY();
        PZ -= getZ();
        return Math.sqrt(PX * PX + PY * PY + PZ * PZ);
    }

    /** @return the distance to another 3D location */

    public double distance(LocationOJ pt) {
        double PX = pt.getX() - this.getX();
        double PY = pt.getY() - this.getY();
        double PZ = pt.getZ() - this.getZ();
        return Math.sqrt(PX * PX + PY * PY + PZ * PZ);
    }

    /**
     * @return true if this location is in the neighborhood of x, y.
     * "Neighborhood" is a user interface tolerance in screen pixels
     * when selecting a point.
     */
    boolean isLocationAt(double x, double y) {
        //here we need to be magnification dependent //n__21.1.2007
        final int radius = 6;
        double magnification = IJ.getImage().getWindow().getCanvas().getMagnification();

        int rad = (int) (radius / magnification);
        if (rad < 3) {
            rad = 3;
        }
        // Rectangle rect = new Rectangle((int)this.x-HEAD_SIZE/2,(int)this.y-HEAD_SIZE/2,HEAD_SIZE,HEAD_SIZE);
        Rectangle rect = new Rectangle((int) this.x - rad, (int) this.y - rad, rad * 2, rad * 2);
        return rect.contains(x, y);
    }
}
