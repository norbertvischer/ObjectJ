
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import ij.measure.*;
import ij.plugin.frame.RoiManager;
import ij.util.*;

/**
 * Elliptical fourier descriptors according to Kuhl&Giardina 1982
 * by Norbert Vischer ++++++
 * 
 * 
 */
public class EFA_ implements PlugInFilter {

    ImagePlus imp;
    ImageProcessor ip;
    int nFDs = 10;// number Fourier descriptors
    int restoreFDs = nFDs;// number Fourier descriptors for resrtoration
    String restorePattern = "";//e.g. "3" = harmonics 1..3; "#101" = 1st and 3rd harmonic
    private double[] srcRoiX; // input contour x coordinates
    private double[] srcRoiY; // input contour y coordinates
    private int nPoints; // Number of points on input contour 
    public double[] A, B, C, D; // The normalized Elliptic Fourier Descriptors 
    double tt[]; //travel distance along perimeter
    double T = 0; //perimeter
    double PI = Math.PI;
    double[] xOutD; //restored x coordinates
    double[] yOutD; //restored x coordinates
    int k50 = 50;
    float[] major = new float[k50];
    float[] minor = new float[k50];
    float[] alpha = new float[k50];
    PolygonRoi[] restoredRois = new PolygonRoi[k50];
    //int[] xOutI; //restored x coordinates
    int[] yOutI; //restored y coordinates
    double A0 = 0;
    double C0 = 0;
    float[] centerSrc, centerOut;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_ALL + ROI_REQUIRED;
    }

    public void run(ImageProcessor ip) {
        this.ip = ip;
        String options = Macro.getOptions();
        if ((options != null) && (options.length() != 0)) {
            if (parseOptions(options) == false) {
                return;
            }
        }
        PolygonRoi roi = (PolygonRoi) imp.getRoi();
        Rectangle rect = roi.getBounds();


        int[] xp = roi.getXCoordinates();
        int[] yp = roi.getYCoordinates();


        //remove zero-length segments

        int len = xp.length;
        int kk = 1;
        for (int jj = 1; jj < len; jj++) {
            if ((xp[jj] != xp[jj - 1]) || (yp[jj] != yp[jj - 1])) {
                xp[kk] = xp[jj];
                yp[kk] = yp[jj];
                kk++;
            }
        }
        while ((xp[0] == xp[kk - 1]) && (yp[0] == yp[kk - 1])) {
            kk--;
        }
        nPoints = kk;

        srcRoiX = new double[nPoints];
        srcRoiY = new double[nPoints];
        for (int i = 0; i < nPoints; i++) {
            srcRoiX[i] = (double) (rect.x + xp[i]);
            srcRoiY[i] = (double) (rect.y + yp[i]);
        }
        centerSrc = PolygonCenterOfMass(srcRoiX, srcRoiY);

        computeSchmittbuhlFD();
        if (restoreFDs > 0) {
            restoreSchmittbuhlPolygon();
        }
        displayResults();
    }

    public void displayResults() {
        ResultsTable rt = new ResultsTable();
        rt.setPrecision(6);
        rt.reset();
        //int row = 0;
        for (int i = 1; i <= nFDs; i++) {

            rt.incrementCounter();
            rt.addValue("A", A[i] * T);
            rt.addValue("B", B[i] * T);
            rt.addValue("C", C[i] * T);
            rt.addValue("D", D[i] * T);

            rt.addValue("Major", major[i]);
            rt.addValue("Minor", minor[i]);
            rt.addValue("Angle", alpha[i]);


        }
        rt.show("EFD-Results");
    }

    /**
     * Example: 
     * nFDs = 6;
     * run("EFA ", "Number="+nFDs +" restorePattern=6"); //restore all 6
     * run("EFA ", "Number="+nFDs +" restorePattern=#101");//restore 1 and 3 
     */
    private boolean parseOptions(String options) {//parses the macro options
        boolean ok = true;
        nFDs = 20;
        restoreFDs = 0;//default
        String[] s = options.split(" ");
        String[] term;
        for (int ii = 0; ii < s.length; ii++) {
            term = s[ii].split("=");
            if (term.length > 1) {
                if (term[0].equalsIgnoreCase("Number")) {
                    nFDs = (int) Tools.parseDouble(term[1], Double.POSITIVE_INFINITY);
                }
                else if (term[0].equalsIgnoreCase("Restore")) {
                    
                        restoreFDs = (int) Tools.parseDouble(term[1], Double.POSITIVE_INFINITY);
                    
                } else {

                    IJ.log("oops! unrecognized argument: " + s[ii]);
                }
            }
        } //ii-loop
        if (restoreFDs > nFDs) {
            restoreFDs = nFDs;
        }
        return ok;
    }

    private void computeSchmittbuhlFD() {// the fourier descriptors

        A = new double[nFDs +1];
        B = new double[nFDs +1];
        C = new double[nFDs +1];
        D = new double[nFDs+1];
        double dxx[] = new double[nPoints];//the dx array
        double dyy[] = new double[nPoints];//the dy array
        double dtt[] = new double[nPoints];//the dt array
        double thisT = 0;
        tt = new double[nPoints];//travelled path along perimeter

        for (int jj = 0; jj < nPoints; jj++) {//use extra arrays to precompute dx, dy, dt and tt
            dxx[jj] = srcRoiX[jj] - srcRoiX[(jj + 1) % nPoints];
            dyy[jj] = srcRoiY[jj] - srcRoiY[(jj + 1) % nPoints];
            double dt = Math.sqrt(dxx[jj] * dxx[jj] + dyy[jj] * dyy[jj]);
            dtt[jj] = dt;
            tt[jj] = thisT;
            thisT += dt;
        }
        T = thisT;


        for (int jj = 1; jj <= nFDs; jj++) {//step through each FD
            for (int ii = 0; ii < nPoints; ii++) {//step through each point
                double omega_1 = jj * (2 * PI * tt[ii] / T);
                double omega_2 = jj * (2 * PI * tt[(nPoints + ii + 1) % nPoints] / T);
                double dCos = Math.cos(omega_1) - Math.cos(omega_2);
                double dSin = Math.sin(omega_1) - Math.sin(omega_2);
                A[jj] += dxx[ii] / dtt[ii] * dCos;
                B[jj] += dxx[ii] / dtt[ii] * dSin;
                C[jj] += dyy[ii] / dtt[ii] * dCos;
                D[jj] += dyy[ii] / dtt[ii] * dSin;
            }//ii-loop through the number of points


            A[jj] = A[jj] / (2 * jj * jj * PI * PI);
            B[jj] = B[jj] / (2 * jj * jj * PI * PI);
            C[jj] = C[jj] / (2 * jj * jj * PI * PI);
            D[jj] = D[jj] / (2 * jj * jj * PI * PI);


        }//k-loop through the number of coeffs


    }//computeEllipticFD
    /*
     * We use k50 harmonics and put all outlines and all ellipses 
     * into arrays.
     * Then, the outlines (with increasing detail) are stored
     * in the roi manager.
     * The ellipses are not stored yet ...
     */

    private void restoreSchmittbuhlPolygon() {
        double x = 0, y = 0;
        xOutD = new double[nPoints];
        yOutD = new double[nPoints];

        double[][] sumX = new double[k50][nPoints];
        double[][] sumY = new double[k50][nPoints];
        double[][] harmX = new double[k50][nPoints];
        double[][] harmY = new double[k50][nPoints];
        boolean yes = false;

        for (int harm = 1; harm < restoreFDs; harm++) {//harm loop
            for (int pt = 0; pt < nPoints; pt++) {//point loop

                if (restorePattern.equals("")) {
                    yes = true;
                } else {
                    yes = restorePattern.length() > (nPoints / 2) && restorePattern.charAt(harm) == '1';
                }
                if (yes) {
                    double phi = 2 * PI * tt[pt] / T * harm;
                    double cosTerm = Math.cos(phi);
                    double sinTerm = Math.sin(phi);
                    double dx = T * (A[harm] * cosTerm + B[harm] * sinTerm);
                    double dy = T * (C[harm] * cosTerm + D[harm] * sinTerm);
                    xOutD[pt] += dx;
                    yOutD[pt] += dy;

                    sumX[harm][pt] = (float) xOutD[pt];
                    sumY[harm][pt] = (float) yOutD[pt];
                    harmX[harm][pt] = (float) dx;
                    harmY[harm][pt] = (float) dy;
                }

            }//point loop
            adjustCenter(sumX[harm], sumY[harm]);
            restoredRois[harm] = toFloatPolygon(sumX[harm], sumY[harm]);
        }//harm loop


        for (int harmCount = 0; harmCount < k50; harmCount++) {
            double maxRad = 0;
            double minRad = 1e99;
            double angle = 0;
            for (int jj = 0; jj < nPoints; jj++) {


                double px = harmX[harmCount][jj];
                double py = harmY[harmCount][jj];
                double rad = Math.sqrt(px * px + py * py);
                if ((rad) > maxRad) {
                    maxRad = rad;
                    angle = Math.atan2(-py, px);
                }
                if (rad < minRad) {
                    minRad = rad;
                }
                harmX[harmCount][jj] += centerSrc[0];
                harmY[harmCount][jj] += centerSrc[1];
            }
            major[harmCount] = 2 * (float) maxRad;
            minor[harmCount] = 2 * (float) minRad;
            alpha[harmCount] = (float) (180.0 / Math.PI * angle);
        }
        RoiManager roiMan = RoiManager.getInstance();


        for (int harmCount = 0; harmCount < k50; harmCount++) {
            if (restoredRois[harmCount] != null) {
                roiMan.addRoi(restoredRois[harmCount]);
            }
        }

        imp.setRoi(restoredRois[1]);
        imp.show();
    }

    public float[] doubleToFloat(double[] dd) {
        float[] ff = new float[dd.length];
        for (int jj = 0; jj < dd.length; jj++) {
            ff[jj] = (float) dd[jj];
        }
        return ff;
    }

    PolygonRoi toFloatPolygon(double[] xx, double[] yy) {
        //PolygonRoi roi = new PolygonRoi(doubleToFloat(xx), doubleToFloat(yy), yy.length, Roi.POLYGON);
        PolygonRoi roi = new PolygonRoi(doubleToFloat(xx), doubleToFloat(yy), yy.length, Roi.FREEROI);
        return roi;

    }

    void adjustCenter(double[] xx, double[] yy) {
        float[] centerDest = PolygonCenterOfMass(xx, yy);
        for (int ii = 0; ii < xx.length; ii++) {
            xx[ii] += (centerSrc[0] - centerDest[0]);
            yy[ii] += (centerSrc[1] - centerDest[1]);
        }

    }

    ;

    /** METHODS TO CALCULATE THE AREA AND CENTROID OF A POLYGON
    INSERT THEM INTO THE CORRESPONDING CLASS 
    http://paulbourke.net/geometry/polyarea/**/
    public double SignedPolygonArea(double[] xx, double[] yy) {
        int N = xx.length;
        Polygon P;
        int i, j;
        double area = 0;

        for (i = 0; i < N; i++) {
            j = (i + 1) % N;
            area += xx[i] * yy[j];
            area -= yy[i] * xx[j];
        }
        area /= 2.0;
        //return (Math.abs(area));
        return area;
    }

    /* CENTROID */
    public float[] PolygonCenterOfMass(double[] xx, double[] yy) {
        int N = xx.length;
        float cx = 0, cy = 0;
        float area = (float) SignedPolygonArea(xx, yy);
        float[] res = new float[2];
        int i, j;

        double factor = 0;
        for (i = 0; i < N; i++) {
            j = (i + 1) % N;
            factor = (xx[i] * yy[j] - xx[j] * yy[i]);
            cx += (xx[i] + xx[j]) * factor;
            cy += (yy[i] + yy[j]) * factor;
        }
        area *= 6.0f;
        factor = 1 / area;
        cx *= factor;
        cy *= factor;
        res[0] = cx;
        res[1] = cy;
        return res;
    }
}
