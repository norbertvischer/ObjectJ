package oj.gui.tools;

import javax.swing.Icon;
import oj.OJ;
import oj.processor.state.MoveCellStateOJ;
import oj.processor.state.ToolStateOJ;


public class MoveCellToolOJ extends ToolAdapterOJ {

    public ToolStateOJ getState() {
        return new MoveCellStateOJ();
    }

    public String getTooltip() {
        return "'Move Tool' for dragging an entire object |Keep Shift key down to drag item only|Keep Alt key down to drag point only. ";
    }

    public String getName() {
        return "MoveObject";
    }

    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"MoveCell16.gif"));
    }

    public Icon getSelectedIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"MoveCell16-Selected.png"));
    }
}
