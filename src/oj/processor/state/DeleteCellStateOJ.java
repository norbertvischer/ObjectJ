/*
 * DeleteCellStateOJ.java
 */
package oj.processor.state;

import ij.gui.ImageCanvas;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import oj.OJ;
import oj.project.CellOJ;
import oj.project.LocationOJ;
import oj.project.YtemOJ;
import oj.graphics.CustomCanvasOJ;
import oj.gui.ShortcutManagerOJ;
import oj.plugin.GlassWindowOJ;
import oj.processor.ToolStateProcessorOJ;

public class DeleteCellStateOJ extends ToolStateAdaptorOJ {

    private enum Mode {

        CELL, YTEM, POINT
    }
    private Mode stateMode = Mode.CELL;
    private boolean exDeleteCellMode = false;
    Toolkit tk = Toolkit.getDefaultToolkit();
    private Cursor deleteCellCursor = tk.createCustomCursor(tk.createImage(getClass().getResource("/oj/processor/state/resources/DeleteCellCursor32.png")), new Point(9, 13), "Delete Cell");
    private Cursor deletePointCursor = tk.createCustomCursor(tk.createImage(getClass().getResource("/oj/processor/state/resources/DeletePointCursor32.png")), new Point(9, 13), "Delete Cell");
    private Cursor deleteYtemCursor = tk.createCustomCursor(tk.createImage(getClass().getResource("/oj/processor/state/resources/DeleteYtemCursor32.png")), new Point(9, 13), "Delete Cell");

    public DeleteCellStateOJ() {
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
        if (loc != null) {
            double dd = ((Double) closestPt[3]).doubleValue();//distance mouse to point in pixels: not used here
            int cell_index = OJ.getData().getCells().indexOfCell(cell);
            int ytem_index = cell.indexOfYtem(ytm);
            int location_index = ytm.indexOf(loc);

            updateState(flags);

            if (ytm.getLocationsCount() == 1 && stateMode == Mode.POINT) {//27.9.2009
                stateMode = Mode.YTEM;
            }
            if (cell.getYtemsCount() == 1 && stateMode == Mode.YTEM) {
                stateMode = Mode.CELL;
            }

            switch (stateMode) {
                case YTEM:

                    OJ.getDataProcessor().removeYtemByIndex(cell, ytem_index);
                    break;
                case POINT:
                    OJ.getDataProcessor().removeLocation(ytm, location_index);
                    break;
                default:
                    OJ.getDataProcessor().removeCellByIndex(cell_index);
            }
        }
        setCanvasCursor();
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        setCanvasCursor();
    }

    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
        //ij.IJ.log("entering: keycode ="+keyCode + "  flags="+flags);
        if (((flags & Event.SHIFT_MASK) == 0) && ((flags & Event.ALT_MASK) != 0)) {
            stateMode = Mode.POINT;
        } else if (((flags & Event.SHIFT_MASK) != 0) && ((flags & Event.ALT_MASK) == 0)) {
            stateMode = Mode.YTEM;
        } else {
            stateMode = Mode.CELL;
        }
        if (keyCode == KeyEvent.VK_D) {
            exDeleteCellMode = true;
        }
        setCanvasCursor();

    }

    public void keyReleased(String imageName, int stackIndex, int keyCode, int flags) {
        //ij.IJ.log("exiting: keycode ="+keyCode + "  flags="+flags);
        stateMode = Mode.CELL;
        if (keyCode == KeyEvent.VK_D) {
            exDeleteCellMode = false;
        }
        setCanvasCursor();
    }

    public Cursor getDefaultCursor() {
        return deleteCellCursor;
    }

    public boolean isExDeleteCellMode() {
        return exDeleteCellMode;
    }

    private void updateState(int flags) {
//        boolean isShift = ShortcutManagerOJ.getInstance().isShiftPressed();//didn't work on Windows 21.6.2009
//        boolean isAlt = ShortcutManagerOJ.getInstance().isAltPressed();
        boolean isShift = (flags & KeyEvent.SHIFT_MASK) != 0;
        boolean isAlt = (flags & KeyEvent.ALT_MASK) != 0;

        //       ij.IJ.log("updateState: shift = " + isShift);
        //       ij.IJ.log("updateState: alt = " + isAlt);
        if (isShift && !isAlt) {
            stateMode = Mode.YTEM;
        } else if (!isShift && isAlt) {
            stateMode = Mode.POINT;
        } else {
            stateMode = Mode.CELL;
        }
        setCanvasCursor();
    }

    private void setCanvasCursor() {
        ImageCanvas ic = getCanvas();
        Cursor cursor = null;
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                if (ij.IJ.isMacOSX()) {//keypressed does not work yet on Windows
                    switch (stateMode) {
                        case YTEM:
                            cursor = deleteYtemCursor;
                            

                            break;
                        case POINT:
                            cursor = deletePointCursor;
                            
                            break;
                        default:
                            cursor = deleteCellCursor;
                           
                    }
                } else {
                    cursor = deleteCellCursor;
                    ic.setCursor(deleteCellCursor);
                   
                }
            } else {
                cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
        if (cursor != null) {
            ic.setCursor(cursor);
            if (GlassWindowOJ.exists()) {
                GlassWindowOJ.getInstance().setCursor(cursor);
            }
        }
    }
}
