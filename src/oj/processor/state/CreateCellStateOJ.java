/*
 * CreateObjectStateOJ.java
 */
package oj.processor.state;

import ij.IJ;
import ij.gui.ImageCanvas;
import ij.plugin.frame.Recorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import oj.OJ;
import oj.project.CellOJ;
import oj.project.YtemDefOJ;
import oj.graphics.CustomCanvasOJ;
import oj.plugin.GlassWindowOJ;
import oj.processor.DataProcessorOJ;
import oj.processor.ToolStateProcessorOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.processor.events.YtemDefSelectionChangedEventOJ;
import oj.processor.events.YtemDefSelectionChangedListenerOJ;
import oj.processor.state.proxy.ImageProxyOJ;
import oj.project.CellsOJ;

public class CreateCellStateOJ extends ToolStateAdaptorOJ implements YtemDefSelectionChangedListenerOJ, YtemDefChangedListenerOJ {

    private double xPos = 0;
    private double yPos = 0;
    private String ytemDefName;
    private ImageProxyOJ imageProxy;
    private Cursor defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/oj/processor/state/resources/CreateCellCursor32.png")), new Point(15, 15), "Create Object");

    public CreateCellStateOJ() {
        setCanvasCursor();
        OJ.getEventProcessor().addYtemDefSelectionChangedListener(this);
    }

    public int getToolState() {
        return ToolStateProcessorOJ.STATE_OBJECT_TOOL;
    }

    public void mousePressed(String imageName, int stackIndex, double x, double y, int flags) {
        super.mousePressed(imageName, stackIndex, x, y, flags);
        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
            resetYtemDef();
            if (OJ.getData().getYtemDefs().getYtemDefsCount() == 0) {
                IJ.showMessage("No items are defined in:\nObjectJ> Show Project Window> Objects ");
            }
            if (ytemDefName == null) {
                return;
            }
        }
        if ((imageProxy == null) || (!imageProxy.getImageName().equals(imageName))) {
            if ((imageProxy != null) && (imageProxy.getCellProxy() != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {
                imageProxy.getCellProxy().getYtemProxy().close();
            }
            imageProxy = new ImageProxyOJ(imageName);
            imageProxy.setYtemDefName(ytemDefName);
        }
        int ytemDefType = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType();
        if (ytemDefType == YtemDefOJ.YTEM_TYPE_ROI) {
            imageProxy.addLocation(stackIndex, x, y, (double) stackIndex);
        } else {
            if ((ytemDefType == YtemDefOJ.YTEM_TYPE_POLYGON) || (ytemDefType == YtemDefOJ.YTEM_TYPE_SEGLINE)) {
                imageProxy.addLocation(stackIndex, x, y, (double) stackIndex);
                if ((flags & Event.CTRL_MASK) != 0) {
                    closeYtem();
                }
            } else {

                imageProxy.addLocation(stackIndex, x, y, (double) stackIndex);
            }
            if (Recorder.record && !IJ.isMacro()) {//second recorded macro command
                Recorder.record("ojSetMarker", x, y);
            }
        }
        xPos = x;
        yPos = y;
        setCanvasCursor();
    }

    public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseDragged(imageName, stackIndex, x, y, flags);
        if (OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType() == YtemDefOJ.YTEM_TYPE_ROI) {
            imageProxy.addLocation(stackIndex, x, y, (double) stackIndex);
        }
    }

    public void mouseMoved(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseMoved(imageName, stackIndex, x, y, flags);
    }

    public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseReleased(imageName, stackIndex, x, y, flags);
        if (ytemDefName == null) {
            return;
        }
        if (OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType() == YtemDefOJ.YTEM_TYPE_ROI) {
            closeYtem();
        }
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        setCanvasCursor();
    }

    private void setYtemDefName(String ytemDefName) {
        if ((ytemDefName != null) && (!ytemDefName.equals(this.ytemDefName))) {
            this.ytemDefName = ytemDefName;
            if (imageProxy != null) {
                imageProxy.setYtemDefName(ytemDefName);
            } else {
                OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
            }
        }
    }

    public void openCell(int cellIndex) {
        if (cellIndex >= 0) {
            CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
            int objectIndex = cell.getYtemsCount() - 1;
            openCell(cellIndex, objectIndex);
        }
    }

    public void openCell(int cellIndex, int objectIndex) {
        if (cellIndex >= 0) {
            CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
            if ((imageProxy == null) || (!imageProxy.getImageName().equals(cell.getImageName()))) {
                if ((imageProxy != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {
                    imageProxy.getCellProxy().getYtemProxy().close();
                }
                imageProxy = new ImageProxyOJ(cell.getImageName());
            }
            imageProxy.openCell(cell, objectIndex);
            ytemDefName = imageProxy.getYtemDefName();
            OJ.getData().getYtemDefs().setSelectedYtemDef(imageProxy.getYtemDefName());

            defaultCursor = ytemDefCursor(OJ.getData().getYtemDefs().getSelectedObjectDef().getLineColor());
            setCanvasCursor();
        }
    }

    public void cleanup() {
        xPos = 0;
        yPos = 0;
        closeCell();
        OJ.getEventProcessor().removeYtemDefSelectionChangedListener(this);
    }

    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
        if (imageName != null) {
            switch (keyCode) {
                case KeyEvent.VK_TAB:
                    if (OJ.getData().getYtemDefs().isComposite()) {
                        if ((imageProxy == null) || (!imageProxy.getImageName().equals(imageName))) {
                            if ((imageProxy != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {
                                imageProxy.getCellProxy().getYtemProxy().close();
                            }
                            imageProxy = new ImageProxyOJ(imageName);
                            imageProxy.setYtemDefName(ytemDefName);
                        }

                        advanceToNextYtemDef();
                        int openCell = OJ.getData().getCells().getOpenCellIndex();//29.6.2011
                        OJ.getEventProcessor().fireCellChangedEvent(openCell, CellChangedEventOJ.CELL_OPEN_EVENT);//29.6.2011
                    } else {
                        xPos = 0;
                        yPos = 0;
                        if ((imageProxy != null) && (imageProxy.getCellProxy() != null)) {
                            imageProxy.getCellProxy().close();
                        } else {
                            if (OJ.getData().getCells().getOpenCellIndex() >= 0) {
                                OJ.getData().getCells().getCellByIndex(OJ.getData().getCells().getOpenCellIndex()).setOpen(false);
                                int openCell = OJ.getData().getCells().getOpenCellIndex();//29.6.2011
                                OJ.getEventProcessor().fireCellChangedEvent(openCell, CellChangedEventOJ.CELL_CLOSE_EVENT);
                            }
                        }
                    }
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if ((imageProxy != null) && (imageProxy.getCellProxy() != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {
                        imageProxy.getCellProxy().deleteLastLocation();
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    closeCell();
                    break;
            }
        }
    }

    public void closeCell() {
        xPos = 0;
        yPos = 0;
        CellsOJ cells = OJ.getData().getCells();
        cells.setNewestCellIndex(cells.getOpenCellIndex());//16.8.2009
        if ((imageProxy != null) && (imageProxy.getCellProxy() != null)) {
            imageProxy.getCellProxy().close();
        } else {
            if (OJ.getData().getCells().getOpenCellIndex() >= 0) {
                OJ.getData().getCells().getCellByIndex(OJ.getData().getCells().getOpenCellIndex()).setOpen(false);
                OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().getOpenCellIndex(), CellChangedEventOJ.CELL_CLOSE_EVENT);
            }
        }
        if (OJ.getData().getYtemDefs().isComposite()) {
            resetYtemDef();
        }
    }

    public void closeYtem() {//14.8.2011
        if ((imageProxy != null) && (imageProxy.getCellProxy() != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {//25.8.2011
            if (imageProxy.getCellProxy().getYtemProxy().isClosed()) {
                imageProxy.getCellProxy().updateYtemDef();
            } else {
                imageProxy.getCellProxy().closeYtemProxy();
                OJ.getEventProcessor().fireYtemChangedEvent();
            }
            if (!OJ.getData().getYtemDefs().isComposite()) {
                imageProxy.closeCellProxy();
            }
        }
    }

    /**
     * moves to the next object definition if the object definition is of type
     * polygon or polyline and there is still posibility to define clones of
     * these types the call only close the current open object
     */
    public void advanceToNextYtemDef() {
        if (OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName) != null) {
            int ytemDefType = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType();
            if (OJ.getData().getYtemDefs().isComposite() && ((ytemDefType == YtemDefOJ.YTEM_TYPE_POLYGON) || (ytemDefType == YtemDefOJ.YTEM_TYPE_SEGLINE))
                    && (imageProxy != null) && (imageProxy.getCellProxy().getYtemProxy() != null)) {
                if (imageProxy.getCellProxy().getYtemProxy().getLocationsCount() == 0) {
                    imageProxy.getCellProxy().getYtemProxy().close();
                    String object_def_name = DataProcessorOJ.getNextYtemDef(ytemDefName);
                    if ((ytemDefName != null) && (ytemDefName.equals(object_def_name))) {
                        closeCell();
                    } else {
                        setYtemDefName(object_def_name);
                    }
                    return;
                } else {
                    imageProxy.getCellProxy().getYtemProxy().close();
                    if (imageProxy.getCellProxy().getCloneCount() < OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getCloneMax()) {
                        return;
                    }
                }
            }
        }
        String object_def_name = DataProcessorOJ.getNextYtemDef(ytemDefName);
        if ((ytemDefName != null) && (ytemDefName.equals(object_def_name))) {
            closeCell();
        } else {
            setYtemDefName(object_def_name);
        }
    }

    public void ytemDefSelectionChanged(YtemDefSelectionChangedEventOJ evt) {
        setYtemDefName(evt.getName());
        defaultCursor = ytemDefCursor(OJ.getData().getYtemDefs().getSelectedObjectDef().getLineColor());
        setCanvasCursor();
    }

    private void resetYtemDef() {
        if (OJ.getData().getYtemDefs().getYtemDefsCount() > 0) {
            setYtemDefName(OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName());
        } else {
            ytemDefName = null;
        }
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    private Cursor ytemDefCursor(Color color) {
        Dimension d = Toolkit.getDefaultToolkit().getBestCursorSize(32, 32);
        if ((d.height == 0) && (d.width == 0)) {
            BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) bi.getGraphics();
            g.setColor(color);
            g.drawLine(7, 1, 7, 3);
            g.drawLine(7, 11, 7, 13);
            g.drawLine(1, 7, 3, 7);
            g.drawLine(11, 7, 13, 7);
            g.drawLine(5, 5, 9, 5);
            g.drawLine(5, 9, 9, 9);
            g.drawLine(5, 5, 5, 9);
            g.drawLine(9, 5, 9, 9);
            return Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(7, 7), "Create Object");
        } else {
            BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) bi.getGraphics();
            g.setColor(color);
            g.drawLine(15, 9, 15, 11);
            g.drawLine(15, 19, 15, 21);
            g.drawLine(9, 15, 11, 15);
            g.drawLine(19, 15, 21, 15);
            g.drawLine(13, 13, 17, 13);
            g.drawLine(13, 17, 17, 17);
            g.drawLine(13, 13, 13, 17);
            g.drawLine(17, 13, 17, 17);
            return Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(15, 15), "Create Object");
        }
    }

    public void ytemDefChanged(YtemDefChangedEventOJ evt) {
        if (OJ.getToolStateProcessor().getToolStateObject() == this) {
            defaultCursor = ytemDefCursor(OJ.getData().getYtemDefs().getSelectedObjectDef().getLineColor());
            setCanvasCursor();
        }
    }

    private void setCanvasCursor() {

        ImageCanvas ic = getCanvas();
        Cursor cursor = null;
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                cursor = defaultCursor;
            } else {
                cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            }
        }
        if (ic != null) {
            ic.setCursor(cursor);
            if (GlassWindowOJ.showing()) {
                GlassWindowOJ.getInstance().setCursor(cursor);
            }
        }
    }
}
