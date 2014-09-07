package oj.gui.tools;

import javax.swing.Icon;
import oj.processor.state.ToolStateOJ;

/**
 * contains all (empty) TooloJ methods so we only can override those needed.
 */
public class ToolAdapterOJ implements ToolOJ {

    private int id;

    public ToolStateOJ getState() {
        return null;
    }

    public String getTooltip() {
        return "";
    }

    public String getName() {
        return "";
    }

    public Icon getIcon() {
        return null;
    }

    public Icon getSelectedIcon() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
