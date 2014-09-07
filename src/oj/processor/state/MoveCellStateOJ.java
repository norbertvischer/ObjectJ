/*
 * MoveObjectStateOJ.java
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
import oj.processor.ToolStateProcessorOJ;

public class MoveCellStateOJ extends ToolStateAdaptorOJ {

    private enum Mode {

        CELL, YTEM, POINT
    }
    private Mode stateMode = Mode.CELL;
    private LocationOJ location;
    private YtemOJ ytem;
    private String imageName;
    private int locationIndex;
    private int stackIndex;
    private int cellIndex;
    private Cursor moveCellCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/MoveCellCursor32.png")), new Point(10, 8), "Move Object");
    private Cursor movePointCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/MoveCellCursor32.png")), new Point(10, 8), "Move Object");
    private Cursor moveObjectCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/MoveCellCursor32.png")), new Point(10, 8), "Move Object");

    public MoveCellStateOJ() {
        setCanvasCursor();
    }

    public int getToolState() {
        return ToolStateProcessorOJ.STATE_OBJECT_TOOL;
    }

    public void mousePressed(String imageName, int sliceNo, double x, double y, int flags) {//sliceNo is 1-based
        super.mousePressed(imageName, sliceNo, x, y, flags);
        int imgIndex = OJ.getData().getImages().getIndexOfImage(imageName);

        Object[] closestPt = OJ.getData().getCells().closestPoint(imgIndex, x, y, sliceNo);
        CellOJ cell = (CellOJ) closestPt[0];
        YtemOJ ytm = (YtemOJ) closestPt[1];
        LocationOJ loc = (LocationOJ) closestPt[2];
        if (loc != null) {
            cellIndex = OJ.getData().getCells().indexOfCell(cell);
            locationIndex = ytm.indexOf(loc);
            location = loc;// null if not found
            ytem = ytm;
        }
        this.stackIndex = sliceNo;
        this.imageName = imageName;
        setCanvasCursor();
    }

    public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseDragged(imageName, stackIndex, x, y, flags);
        if ((this.imageName != null) && (this.imageName.equals(imageName)) && (this.stackIndex == stackIndex)) {
            if (location != null) {
                double dx = x - location.getX();
                double dy = y - location.getY();
                updateState(flags);
                switch (stateMode) {
                    case YTEM:
                        for (int j = 0; j
                                < ytem.getLocationsCount(); j++) {
                            LocationOJ loc = ytem.getLocation(j);
                            loc.setX(loc.getX() + dx);
                            loc.setY(loc.getY() + dy);
                            OJ.getDataProcessor().setLocation(ytem, j, loc);
                        }

                        break;
                    case POINT:
                        location.setX(x);
                        location.setY(y);
                        OJ.getDataProcessor().setLocation(ytem, locationIndex, location);
                        break;
                    default:
                        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
                        for (int i = 0; i
                                < cell.getYtemsCount(); i++) {
                            YtemOJ ytm = cell.getYtemByIndex(i);
                            for (int j = 0; j
                                    < ytm.getLocationsCount(); j++) {
                                LocationOJ loc = ytm.getLocation(j);
                                loc.setX(loc.getX() + dx);
                                loc.setY(loc.getY() + dy);
                                OJ.getDataProcessor().setLocation(ytm, j, loc);
                            }

                        }
                }
            }
            getCanvas().repaint();
        }
    }

    public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseReleased(imageName, stackIndex, x, y, flags);
        if ((this.imageName != null) && (this.imageName.equals(imageName)) && (this.stackIndex == stackIndex)) {
            if (location != null) {
                double dx = x - location.getX();
                double dy = y - location.getY();
                updateState(flags);//30.9.2009

                switch (stateMode) {
                    case YTEM:
                        for (int j = 0; j
                                < ytem.getLocationsCount(); j++) {
                            LocationOJ loc = ytem.getLocation(j);
                            loc.setX(loc.getX() + dx);
                            loc.setY(loc.getY() + dy);
                            OJ.getDataProcessor().setLocation(ytem, j, loc);
                        }

                        break;
                    case POINT:
                        location.setX(x);
                        location.setY(y);
                        OJ.getDataProcessor().setLocation(ytem, locationIndex, location);
                        break;
                    default:
                        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
                        for (int i = 0; i
                                < cell.getYtemsCount(); i++) {
                            YtemOJ ytm = cell.getYtemByIndex(i);
                            for (int j = 0; j
                                    < ytm.getLocationsCount(); j++) {
                                LocationOJ loc = ytm.getLocation(j);
                                loc.setX(loc.getX() + dx);
                                loc.setY(loc.getY() + dy);
                                OJ.getDataProcessor().setLocation(ytm, j, loc);
                            }

                        }
                }
                locationIndex = -1;
                location =
                        null;
                ytem =
                        null;
            }

        }
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        setCanvasCursor();
    }

    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
        if (((flags & Event.SHIFT_MASK) == 0) && ((flags & Event.ALT_MASK) != 0)) {
            stateMode = Mode.POINT;
        } else if (((flags & Event.SHIFT_MASK) != 0) && ((flags & Event.ALT_MASK) == 0)) {
            stateMode = Mode.YTEM;
        } else {
            stateMode = Mode.CELL;
        }

        setCanvasCursor();
    }

    public void keyReleased(String imageName, int stackIndex, int keyCode, int flags) {
        stateMode = Mode.CELL;
        setCanvasCursor();
    }

    public Cursor getDefaultCursor() {
        return moveCellCursor;
    }

    private void updateState(int flags) {//30.9.2009

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
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                switch (stateMode) {
                    case YTEM:
                        ic.setCursor(moveObjectCursor);
                        break;
                    case POINT:
                        ic.setCursor(movePointCursor);
                        break;
                    default:
                        ic.setCursor(moveCellCursor);
                }

            } else {
                ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }
}
