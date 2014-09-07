
package oj.processor.state;

import ij.gui.ImageCanvas;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import oj.macros.MacroSetOJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.ToolStateProcessorOJ;


public class MacroToolStateOJ extends ToolStateAdaptorOJ {

    private MacroSetOJ macroSet;
    private String macroName;
    
    public MacroToolStateOJ(){
        setCanvasCursor();
    }
    
    public int getToolState(){
        return ToolStateProcessorOJ.STATE_MACRO_TOOL;
    }

    public void mousePressed(String imageName, int stackIndex, double x, double y, int flags) {
        super.mousePressed(imageName, stackIndex, x, y, flags);
        ImageCanvas ic = getCanvas();
        if (ic != null) {
            if (macroSet != null) {
                ActionEvent action = new ActionEvent(this, 0, macroName);
                ((ActionListener) macroSet).actionPerformed(action);
            }
            ic.setCursor(getDefaultCursor());
        }
    }

    public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseDragged(imageName, stackIndex, x, y, flags);
    }

    public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseReleased(imageName, stackIndex, x, y, flags);
    }

    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseEntered(imageName, stackIndex, x, y, flags);
        ImageCanvas ic = getCanvas();
        if (ic != null) {
            ic.setCursor(getDefaultCursor());
        }
    }

    public void mouseMoved(String imageName, int stackIndex, double x, double y, int flags) {
        super.mouseMoved(imageName, stackIndex, x, y, flags);
    }

    public void setMacroSet(String macroName, MacroSetOJ macroSet) {
        this.macroName = macroName;
        this.macroSet = macroSet;
    }

    private void setCanvasCursor() {
        ImageCanvas ic = getCanvas();
        if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
            if (ic instanceof CustomCanvasOJ) {
                ic.setCursor(getDefaultCursor());
            } else {
                ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }
}
