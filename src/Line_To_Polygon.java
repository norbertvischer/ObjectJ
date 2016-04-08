
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;
import ij.gui.Roi;
import java.util.ArrayList;

public class Line_To_Polygon implements PlugIn {

    double lw = 10;
    boolean roundFlag = false;

    public void run(String arg) {
        if (showDialog()) {
            lineToPolygon(lw, roundFlag);
        }
    }

    boolean showDialog() {

        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            return false;
        }
        Roi roi = imp.getRoi();
        int typ = roi.getType();
        if (roi == null || !(typ == Roi.POLYLINE || typ == Roi.FREELINE)) {
            return false;
        }
        double currentLWidth = roi.getStrokeWidth();

        GenericDialog gd = new GenericDialog("Line To Area");
        gd.addNumericField("Line Width", currentLWidth, 0, 4, "(Zero = Use roi's line width)");
        String[] types = "Brush (round caps),Polygon,Polygon (round bends)".split(",");
        gd.addRadioButtonGroup("", types, 3, 1, types[0]);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }
        int type = gd.getNextChoiceIndex();
        if (type == 0);
        lw = gd.getNextNumber();
        return true;
    }

    public void lineToPolygon(double lw, boolean roundFlag) {
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            return;
        }
        Roi roi = imp.getRoi();
        int typ = roi.getType();
        if (roi == null || !(typ == Roi.POLYLINE || typ == Roi.FREELINE)) {
            //ImageJAccessOJ.InterpreterAccess.interpError("Roi of type PolyLine or FeeLine expected");
            return;
        }
        if (lw == 0) {
            lw = roi.getStrokeWidth();
        }
        FloatPolygon fp = roi.getFloatPolygon();
        //IJ.log("Converting a line into a polygon");

        int len = fp.npoints;
        double[] x = new double[len];
        double[] y = new double[len];
        for (int jj = 0; jj < len; jj++) {
            x[jj] = fp.xpoints[jj];
            y[jj] = fp.ypoints[jj];
        }
        double[] leftX = new double[len];

        double[] rightX = new double[len];
        double[] leftY = new double[len];
        double[] rightY = new double[len];
        double[] phiVV = new double[len];
        double PI = Math.PI;
        double halfdev = 0, firstPhi = 0, lastPhi = 0, phi3;

        for (int i = 1; i < len - 1; i++) {
            double dxPast = x[i - 1] - x[i];
            double dxFuture = x[i + 1] - x[i];
            double dyPast = y[i - 1] - y[i];
            double dyFuture = y[i + 1] - y[i];

            double dotProduct = dxFuture * dxPast + dyFuture * dyPast;
            double crossProduct = dxFuture * dyPast - dyFuture * dxPast;

            double phiV = Math.atan2(crossProduct, dotProduct);//phi of this vertex
            phiVV[i] = phiV;
            if (phiV >= 0) {
                halfdev = (PI - phiV) / 2; //Half deviation
            }
            if (phiV < 0) {
                halfdev = (-PI - phiV) / 2;
            }
            //IJ.log("phiV = " + (phiV * 180 / PI) + " halfdev = " + (halfdev * 180 / PI));
            double rad = Math.abs(lw / 2 / Math.cos((PI - phiV) / 2));
            double phiPast = Math.atan2(-dyPast, -dxPast);
            double phiMean = phiPast + halfdev;
            if (i == 1) {
                firstPhi = phiPast;
            }
            phi3 = phiMean + PI / 2;
            double dx = Math.cos(phi3) * rad;
            double dy = Math.sin(phi3) * rad;
            leftX[i] = x[i] + dx;
            leftY[i] = y[i] + dy;
            rightX[i] = x[i] - dx;
            rightY[i] = y[i] - dy;
            lastPhi = Math.atan2(dyFuture, dxFuture);
            // lastPhi = phiRight;
        }

        double dx = lw / 2 * Math.cos(firstPhi + PI / 2);//calc start face
        double dy = lw / 2 * Math.sin(firstPhi + PI / 2);
        leftX[0] = x[0] + dx;
        leftY[0] = y[0] + dy;
        rightX[0] = x[0] - dx;
        rightY[0] = y[0] - dy;

        dx = lw / 2 * Math.cos(lastPhi + PI / 2);//calc end face
        dy = lw / 2 * Math.sin(lastPhi + PI / 2);
        leftX[len - 1] = x[len - 1] + dx;
        leftY[len - 1] = y[len - 1] + dy;
        rightX[len - 1] = x[len - 1] - dx;
        rightY[len - 1] = y[len - 1] - dy;

        //if (true) {//make convex vertices round
        ArrayList<Double> leftXX = new ArrayList<Double>();
        ArrayList<Double> leftYY = new ArrayList<Double>();
        ArrayList<Double> rightXX = new ArrayList<Double>();
        ArrayList<Double> rightYY = new ArrayList<Double>();
        leftXX.add(leftX[0]);//begin face
        leftYY.add(leftY[0]);
        rightXX.add(rightX[0]);
        rightYY.add(rightY[0]);

        for (int jj = 1; jj < len - 1; jj++) {
            leftXX.add(leftX[jj]);
            leftYY.add(leftY[jj]);
            if (phiVV[jj] < 0) {
                leftXX.add(leftX[jj]);
                leftYY.add(leftY[jj]);
               leftXX.add(leftX[jj] + 3);
                leftYY.add(leftY[jj] + 3);
                leftXX.add(leftX[jj]);
                leftYY.add(leftY[jj]);

            }
            if (phiVV[jj] >= 0) {
                rightXX.add(rightX[jj]);
                rightYY.add(rightY[jj]);
               rightXX.add(rightX[jj] + 3);
                rightYY.add(rightY[jj] + 3);
                rightXX.add(rightX[jj]);
                rightYY.add(rightY[jj]);

            }
            rightXX.add(rightX[jj]);
            rightYY.add(rightY[jj]);

        }
        leftXX.add(leftX[len - 1]);//end face
        leftYY.add(leftY[len - 1]);
        rightXX.add(rightX[len - 1]);
        rightYY.add(rightY[len - 1]);

        // }
        int leftLen = leftXX.size();//becomes longer if 
        int rightLen = rightXX.size();//becomes longer if 
        float[] polygonX = new float[leftLen + rightLen];
        float[] polygonY = new float[leftLen + rightLen];
        for (int jj = 0; jj < leftLen; jj++) {
            polygonX[jj] = (float) (double) leftXX.get(jj);//go there on left side
            polygonY[jj] = (float) (double) leftYY.get(jj);
        }
        for (int jj = 0; jj < rightLen; jj++) {//return on right side
            polygonX[jj + leftLen] = (float) (double) rightXX.get(rightLen - jj - 1);
            polygonY[jj + leftLen] = (float) (double) rightYY.get(rightLen - jj - 1);
        }

        //  roi = null;
        roi = new PolygonRoi(polygonX, polygonY, Roi.POLYGON);
        roi.setStrokeWidth(1);
        imp.setRoi(roi, true);
    }

}
