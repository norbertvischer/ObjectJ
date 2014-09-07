
package oj.gui.tools;

import javax.swing.Icon;
import oj.OJ;
import oj.processor.state.YtemToRoiStateOJ;
import oj.processor.state.ToolStateOJ;


public class ObjectToRoiToolOJ extends ToolAdapterOJ {

    public ToolStateOJ getState() {
        return new YtemToRoiStateOJ();
    }

    public String getTooltip() {
        return "Object To ROI tool";
    }

    public String getName() {
        return "ObjectToRoi";
    }

    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"ObjectToRoi16.gif"));
    }

    public Icon getSelectedIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"ObjectToRoi16-Selected.png"));
    }
}
