package oj.gui.tools;

import javax.swing.Icon;
import oj.processor.state.ToolStateOJ;

/**
 *
 * provides necessary methods to characterize a tool
 */
public interface ToolOJ {

    public ToolStateOJ getState();
    public String getTooltip();
    public String getName();
    public Icon getIcon();
    public Icon getSelectedIcon();

    
    public int getId();
    public void setId(int id);
    
}
