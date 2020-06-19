/*
 * YtemToRoiStateOJ.java
 */
package oj.processor.state;

import ij.IJ;
import ij.gui.ImageCanvas;
import ij.gui.Line;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import oj.OJ;
import oj.project.CellOJ;
import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.ToolStateProcessorOJ;

public class YtemToRoiStateOJ extends ToolStateAdaptorOJ {

    Cursor defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/ObjectToRoiCursor32.png")), new Point(9, 9), "Object To ROI");

    public YtemToRoiStateOJ() {
        setCanvasCursor();
    }

    public int getToolState() {
        return ToolStateProcessorOJ.STATE_OBJECT_TOOL;
    }
    
    public void mousePressed(String imageName, int sliceNo, double x, double y, int flags) {
        super.mousePressed(imageName, sliceNo, x, y, flags);
        int imgIndex = OJ.getData().getImages().getIndexOfImage(imageName);
        Object[] closestPt = OJ.getData().getCells().closestPoint(imgIndex, x, y, sliceNo);
        CellOJ cell = (CellOJ) closestPt[0];
        YtemOJ ytm = (YtemOJ) closestPt[1];
        LocationOJ loc = (LocationOJ) closestPt[2];
        if (loc == null) {

        } else {ytm.ytemToRoi();
       
        setCanvasCursor();}
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        setCanvasCursor();
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    private void setCanvasCursor() {
        ImageCanvas ic = getCanvas();
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                ic.setCursor(defaultCursor);
            } else {
                ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }
}
