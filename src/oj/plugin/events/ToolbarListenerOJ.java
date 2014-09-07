/*
 * ToolbarListenerOJ.java
 */
package oj.plugin.events;

import ij.gui.Toolbar;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import oj.gui.tools.ToolManagerOJ;


public class ToolbarListenerOJ implements MouseListener {

    /** Creates a new instance of ToolbarListenerOJ */
    public ToolbarListenerOJ() {
        Toolbar.getInstance().addMouseListener(this);
    }

    //@SuppressWarnings("static-access")
    public void mousePressed(MouseEvent e) {
        if ((Toolbar.getToolId() >= 0) && (Toolbar.getToolId() < 23)) {
            ToolManagerOJ.getInstance().selectTool("");
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public static boolean isObjectJTool() {
        int tool_id = Toolbar.getToolId();
        if ((tool_id == Toolbar.getInstance().getToolId("Delete Object Tool")) || (tool_id == Toolbar.getInstance().getToolId("Delete Item Tool")) || (tool_id == Toolbar.getInstance().getToolId("Move Point Tool")) || (tool_id == Toolbar.getInstance().getToolId("Select Object Tool")) || (tool_id == Toolbar.getInstance().getToolId("Object to ROI Tool")) || (tool_id == Toolbar.getInstance().getToolId("Create Object Tool"))) {
            return true;
        }
        return false;
    }
}
