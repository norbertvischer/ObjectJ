package oj.gui.tools;

import javax.swing.Icon;
import oj.OJ;
import oj.processor.state.SelectCellStateOJ;
import oj.processor.state.ToolStateOJ;

public class SelectCellToolOJ extends ToolAdapterOJ {

    public ToolStateOJ getState() {
        return new SelectCellStateOJ();
    }

    public String getTooltip() {
        return "Finger tool for selecting an object |Double-click to open an object";
    }

    public String getName() {
        return "SelectObject";
    }

    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"SelectCell16.gif"));
    }

    public Icon getSelectedIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"SelectCell16-Selected.png"));
    }
}
