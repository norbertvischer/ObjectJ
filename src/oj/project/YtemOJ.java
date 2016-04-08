/*
 * YtemOJ.java
 * abstract class to be extended by the 6 shape classes
 * fully documented 9.3.2010
 */
package oj.project;

import ij.IJ;
import ij.gui.Line;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import oj.util.UtilsOJ;

public abstract class YtemOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 4492830715353600943L;
    protected String definition;//name of ytem type, e.g. "Axis" in coli project
    protected int stackIndex = 0;
    protected transient boolean open;
    protected ArrayList<LocationOJ> locations = new ArrayList();
    protected byte[] roiBytes = null;//13.10.2014 not used yet, but must not be changed

    public void setIJRoi(Roi theRoi) {//15.3.2014 not used yet
        roiBytes = RoiEncoder.saveAsByteArray(theRoi);
    }

    public byte[] getRoiBytes() {//13.10.2014 not used yet
        return roiBytes;
    }

    public Roi getIJRoi() {//13.10.2014 not used yet

        String name = "Hello";
        RoiDecoder rd = null;
        Roi roi = null;
        if (roiBytes != null) {
            rd = new RoiDecoder(roiBytes, name);
        }
        try {
            roi = rd.getRoi();
        } catch (IOException e) {
        }
        return roi;
    }

    public void showIJRoi(Roi theRoi) {//13.10.2014 not used yet

    }

    /**
     * @return stack index (1-based ?)
     */
    public int getStackIndex() {
        return stackIndex;
    }

    public void setStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
        changed = true;
    }

    /**
     * Initialize if necessary after reading from file
     */
    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (locations == null) {
            locations = new ArrayList();
        }
        for (int i = 0; i < locations.size(); i++) {
            getLocation(i).initAfterUnmarshalling(this);
        }
    }

    public abstract int getType();

    /**
     * @return name of ytem type
     */
    public String getYtemDef() {
        return definition;
    }

    /**
     * set name of item type
     */
    public void setObjectDef(String definition) {
        this.definition = definition;
        changed = true;
    }

    public abstract boolean contains(double x, double y, double z);

    public abstract boolean contains(LocationOJ p);

    /**
     * adds a point to this ytem
     */
    public boolean add(LocationOJ location) {
        if (locations.add(location)) {
            location.setParent(this);
            changed = true;
            return true;
        }
        return false;
    }

    /**
     * @return n-th location of this ytem, 0-based
     */
    public LocationOJ getLocation(int index) {
        if (UtilsOJ.inRange(0, locations.size() - 1, index)) {
            return locations.get(index);
        } else {
            return new LocationOJ(Double.NaN, Double.NaN, Double.NaN);
        }
    }

    /**
     * @return roiPoints as array, or null if it is not a roi 15.3.2010
     */
//    public int[] getRoiSteps() {
//        return roiSteps;
//    }
    /**
     * replaces old location with new one, and sets new location's parent.
     *
     * @return old location (if sucessful), otherwise, a NaN location is
     * returned
     */
    public LocationOJ setLocation(int index, LocationOJ newLocation) {
        if (UtilsOJ.inRange(0, locations.size() - 1, index)) {
            LocationOJ old_location = locations.get(index);
            locations.set(index, newLocation);
            newLocation.setParent(this);
            changed = true;
            return old_location;
        }
        return new LocationOJ(Double.NaN, Double.NaN, Double.NaN);
    }

    /**
     * removes n-th location from ytem, 0-based
     */
    public void removelocationByIndex(int index) {
        locations.remove(index);
        changed = true;
    }

    /**
     * removes location from ytem
     */
    public boolean removeLocation(LocationOJ location) {
        if (locations.remove(location)) {
            changed = true;
            return true;
        }
        return false;
    }

    /**
     * @return number of points owned by ytem
     */
    public int getLocationsCount() {
        return locations.size();
    }

    /**
     * @return index of location
     */
    public int indexOf(LocationOJ location) {
        return locations.indexOf(location);
    }

    /**
     * duplicates Arraylist and returns it as an array
     */
    public LocationOJ[] toArray() {
        LocationOJ[] result = new LocationOJ[locations.size()];
        System.arraycopy(locations, 0, result, 0, locations.size());
        return result;
    }

    /**
     * @return ytem's x positions as double array
     */
    public double[] toXDArray() {
        int i;
        double[] result = new double[locations.size()];
        for (i = 0; i < locations.size(); i++) {
            result[i] = ((LocationOJ) locations.get(i)).x;
        }
        return result;
    }

    /**
     * @return ytem's y positions as double array
     */
    public double[] toYDArray() {
        int i;
        double[] result = new double[locations.size()];
        for (i = 0; i < locations.size(); i++) {
            result[i] = (locations.get(i)).y;
        }
        return result;
    }

    /**
     * @return ytem's x positions as double array
     */
    public float[] toXFArray() {
        int i;
        float[] result = new float[locations.size()];
        for (i = 0; i < locations.size(); i++) {
            result[i] = (locations.get(i)).x;
        }
        return result;
    }

    /**
     * @return ytem's y positions as double array
     */
    public float[] toYFArray() {
        int i;
        float[] result = new float[locations.size()];
        for (i = 0; i < locations.size(); i++) {
            result[i] = (locations.get(i)).y;
        }
        return result;
    }

    /**
     * @return ytem's z positions as double array
     */
    public LocationOJ[] toZDArray() {
        LocationOJ[] result = new LocationOJ[locations.size()];
        System.arraycopy(locations, 0, result, 0, locations.size());
        return result;
    }

    /**
     * @return ytem's x positions as array of integers (rounded)
     */
    public int[] toXArray() {
        int i;
        int[] result = new int[locations.size()];
        for (i = 0; i < result.length; i++) {
            double val = (locations.get(i)).x;
            result[i] = (int) Math.round(val);//20.3.2009
            //result[i] = (int) (locations.get(i)).x;
        }
        return result;
    }

    /**
     * @return ytem's y positions as array of integers (rounded)
     */
    public int[] toYArray() {
        int i;
        int[] result = new int[locations.size()];
        for (i = 0; i < result.length; i++) {
            double val = (locations.get(i)).y;
            result[i] = (int) Math.round(val);//20.3.2009
            //result[i] = (int) (locations.get(i)).y;
        }
        return result;
    }

    /**
     * @return ytem's z positions as array of integers (rounded)
     */
    public int[] toZArray() {
        int i;
        int[] result = new int[locations.size()];
        for (i = 0; i < result.length; i++) {
            double val = (locations.get(i)).z;
            result[i] = (int) Math.round(val);//20.3.2009
            //result[i] = (int) (locations.get(i)).z;
        }
        return result;
    }

    /**
     * @return ytem's x positions with magnification and offset for screen display
     */
    public int[] toXArray(int offset, double magnification) {
        int i;
        int[] result = new int[locations.size()];
        for (i = 0; i < result.length; i++) {
            //result[i] = (int) (((locations.get(i)).x - offset) * magnification);
            double val = (((locations.get(i)).x - offset) * magnification);
            result[i] = (int) Math.round(val);//20.3.2009
        }
        return result;
    }

    /**
     * @return ytem's y positions with magnification and offset for screen display
     */
    public int[] toYArray(int offset, double magnification) {
        int i;
        int[] result = new int[locations.size()];
        for (i = 0; i < result.length; i++) {
            //result[i] = (int) (((locations.get(i)).y - offset) * magnification);
            double val = (((locations.get(i)).y - offset) * magnification);
            result[i] = (int) Math.round(val);//20.3.2009
        }
        return result;
    }

    /**
     * @return parent cell
     */
    public CellOJ getCell() {
        if (parent != null) {
            return (CellOJ) parent;
        } else {
            return null;
        }
    }

    /**
     * Creates a roi from the currently selected ytem and cell
     */
    public void ytemToRoi() {//13.5.2014
        int type = getType();
//        CellOJ cell = OJ.getData().getCells().getSelectedCell();
//        
//        YtemOJ ytm = cell.getSelectedYtem();
//
//        YtemDefOJ ytem_def = OJ.getData().getYtemDefs().getYtemDefByName(ytm.getYtemDef());
        float[] xcoords = toXFArray();//13.5.2014
        float[] ycoords = toYFArray();
        Roi roi;
        boolean wrong = (xcoords.length < 2 && type != YtemDefOJ.YTEM_TYPE_POINT);
        wrong = wrong || (xcoords.length < 3 && type == YtemDefOJ.YTEM_TYPE_POLYGON);
        wrong = wrong || (xcoords.length < 3 && type == YtemDefOJ.YTEM_TYPE_ROI);
        if (wrong) {//20.1.2016
            IJ.getImage().killRoi();
            return;
        }
        switch (type) {
            case YtemDefOJ.YTEM_TYPE_ANGLE:
                roi = new PolygonRoi(xcoords, ycoords, Roi.POLYLINE);
                IJ.getImage().setRoi(roi);
                break;

            case YtemDefOJ.YTEM_TYPE_LINE:
                roi = new Line(xcoords[0], ycoords[0], xcoords[1], ycoords[1]);//13.5.2014
                IJ.getImage().setRoi(roi);
                break;

            case YtemDefOJ.YTEM_TYPE_POINT:
                roi = new PointRoi(xcoords[0], ycoords[0]);
                IJ.getImage().setRoi(roi);
                break;

            case YtemDefOJ.YTEM_TYPE_POLYGON:
                roi = new PolygonRoi(xcoords, ycoords, Roi.POLYGON);
                IJ.getImage().setRoi(roi);
                break;

            case YtemDefOJ.YTEM_TYPE_ROI:
                roi = new PolygonRoi(xcoords, ycoords, Roi.FREEROI);
                IJ.getImage().setRoi(roi);
                break;

            case YtemDefOJ.YTEM_TYPE_SEGLINE:
                roi = new PolygonRoi(xcoords, ycoords, Roi.POLYLINE);
                IJ.getImage().setRoi(roi);
                break;

            default:
        }
    }

    /**
     * @return smallest rectangle containing ytem's points
     */
    public Rectangle getRectangle() {
        if (locations.size() > 0) {
            double x1 = (locations.get(0)).getX();
            double y1 = (locations.get(0)).getY();
            double x2 = (locations.get(0)).getX();
            double y2 = (locations.get(0)).getY();
            for (int i = 0; i < locations.size(); i++) {
                x1 = Math.min(x1, (locations.get(i)).getX());
                y1 = Math.min(y1, (locations.get(i)).getY());
                x2 = Math.max(x2, (locations.get(i)).getX());
                y2 = Math.max(y2, (locations.get(i)).getY());
            }
            return new Rectangle((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
        } else {
            return null;
        }
    }

    /**
     * @return open flag
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * set open flag true or false
     */
    public void setOpen(boolean open) {
        this.open = open;
    }
}
