
package oj.gui.tools;

import javax.swing.Icon;
import oj.OJ;
import oj.processor.state.DeleteCellStateOJ;
import oj.processor.state.ToolStateOJ;

public class PistolToolOJ extends ToolAdapterOJ{

    public ToolStateOJ getState() {
        return new DeleteCellStateOJ();
    }

    public String getTooltip() {
        return "Pistol tool for removing an object.|Keep Shift key down to remove an item only;|Keep Alt key down to remove a point only";
    }

    public String getName() {
        return "Pistol";
    }

    public Icon getIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"Pistol16.gif"));
    }

    public Icon getSelectedIcon() {
        return new javax.swing.ImageIcon(getClass().getResource(OJ.ICONS+"Pistol16-Selected.png"));
    }
}
