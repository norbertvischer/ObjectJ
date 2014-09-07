/*
 * SelectCellStateOJ.java
 */
package oj.processor.state;

import ij.gui.ImageCanvas;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import oj.OJ;
import oj.project.CellOJ;
import oj.project.LocationOJ;
import oj.project.YtemOJ;
import oj.project.results.QualifiersOJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.ToolStateProcessorOJ;
import oj.gui.tools.ToolManagerOJ;
import oj.plugin.GlassWindowOJ;

public class SelectCellStateOJ extends ToolStateAdaptorOJ {

    private enum Mode {

        SELECT, QUALIFY
    }
    private double yPos = 0;
    private double xPos = 0;
    private Mode stateMode = Mode.SELECT;
    private Cursor selectCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/SelectCellCursor32.png")), new Point(14, 8), "Select Cell");
    private Cursor qualifyCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/SelectCellCursorQ32.png")), new Point(14, 8), "Qualify Cell");

    public SelectCellStateOJ() {
        setCanvasCursor();
    }

    public int getToolState() {
        return ToolStateProcessorOJ.STATE_OBJECT_TOOL;
    }

    public void cleanup() {
        yPos = 0;
        xPos = 0;
        OJ.getDataProcessor().unselectCell();
    }

    public void mousePressed(String imageName, int sliceNo, double x, double y, int flags) {//sliceNo is 1-based
        super.mousePressed(imageName, sliceNo, x, y, flags);


        int imgIndex = OJ.getData().getImages().getIndexOfImage(imageName);
        Object[] closestPt = OJ.getData().getCells().closestPoint(imgIndex, x, y, sliceNo);
        CellOJ cell = (CellOJ) closestPt[0];
        YtemOJ ytm = (YtemOJ) closestPt[1];
        LocationOJ loc = (LocationOJ) closestPt[2];
        if (loc == null) {
            OJ.getDataProcessor().unselectCell();
        } else {
            double dd = ((Double) closestPt[3]).doubleValue();//distance mouse to point in pixels: not used here
            int cell_index = OJ.getData().getCells().indexOfCell(cell);
            int ytem_index = cell.indexOfYtem(ytm);
            int location_index = ytm.indexOf(loc);
            switch (stateMode) {
                case QUALIFY:
                    OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
                    cell.setQualified(!cell.isQualified());
                    OJ.getEventProcessor().fireCellChangedEvent();//30.8.2013
                    //setCanvasCursor();
                    break;
                default:
                    if ((xPos == x) && (yPos == y)) {
                        ToolManagerOJ.getInstance().selectTool("Marker");
                        ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).openCell(cell_index, ytem_index);
                    } else {
                        OJ.getDataProcessor().selectCell(cell_index);
                    }
                    xPos = x;
                    yPos = y;
            }
        }
        setCanvasCursor();
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        setCanvasCursor();
        if (GlassWindowOJ.exists()) {// &&  false) {
            GlassWindowOJ.getInstance().setCursor(selectCursor);
        }
    }

    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
        if (keyCode == KeyEvent.VK_Q) {
            stateMode = Mode.QUALIFY;
            setCanvasCursor();
        }
    }

    public void keyReleased(String imageName, int stackIndex, int keyCode, int flags) {
        if (keyCode == KeyEvent.VK_Q) {
            stateMode = Mode.SELECT;
        }
        setCanvasCursor();
    }

    public boolean isQualifyMode() {
        return (stateMode == Mode.QUALIFY);
    }

    public Cursor getDefaultCursor() {
        return selectCursor;
    }

    private void setCanvasCursor() {
        ImageCanvas ic = getCanvas();
        Cursor cursor = null;
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                switch (stateMode) {
                    case QUALIFY:
                        cursor =qualifyCursor;
                        ic.setCursor(qualifyCursor);
                    default:
                        cursor = selectCursor;
                }
            } else {
                cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            }
        }
        if (cursor != null){
           ic.setCursor(cursor);
           if (GlassWindowOJ.exists()) {
                GlassWindowOJ.getInstance().setCursor(cursor);
            }
        }
    }
}
