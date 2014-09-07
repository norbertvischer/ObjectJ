package oj.gui.tools;

import javax.swing.Icon;
import oj.OJ;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.ToolStateOJ;

public class SetMarkerToolOJ extends ToolAdapterOJ {

    public ToolStateOJ getState() {
        return new CreateCellStateOJ();
    }

    public String getTooltip() {
        return "Set Marker Tool for placing non-destructive|markers onto a linked image.";
    }

    public String getName() {
        return "Marker";
    }

    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"CreateCell16.gif"));
    }

    public Icon getSelectedIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"CreateCell16-Selected.png"));
    }
}
