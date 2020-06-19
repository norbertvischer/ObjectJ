package oj.gui.tools;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import oj.util.UtilsOJ;
import oj.macros.MacroSetOJ;
import oj.processor.state.ToolStateOJ;
import oj.processor.state.MacroToolStateOJ;
import oj.macros.MacroItemOJ;

public class MacroToolOJ extends ToolAdapterOJ {

    private MacroSetOJ macroSet;
    private String macroName;

    public MacroToolOJ() {
    }

    public MacroToolOJ(String macroName, MacroSetOJ macroSet) {
        setMacroSet(macroName, macroSet);
    }

    public ToolStateOJ getState() {
        MacroToolStateOJ state = new MacroToolStateOJ();
        state.setMacroSet(macroName, macroSet);
        return state;
    }

    public String getTooltip() {
        return macroName;
    }

    public String getName() {
        return macroName;
    }

    public Icon getIcon() {
        int index = macroSet.indexOfMacroItem(macroName);
        MacroItemOJ tool =  macroSet.getMacroItemByIndex(index);
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        ((Graphics2D) bi.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        UtilsOJ.drawIcon(bi.getGraphics(), tool.getIcon(), 0, 0);

        return new ImageIcon(bi);
    }

    public Icon getSelectedIcon() {//9.9.2009  I have to change manually the icon to show that it is selected
        int index = macroSet.indexOfMacroItem(macroName);
        MacroItemOJ tool = macroSet.getMacroItemByIndex(index);
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        ((Graphics2D) bi.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        UtilsOJ.drawIcon(bi.getGraphics(), tool.getIcon(), 0, 0);

        return new ImageIcon(bi);
    }

    public void setMacroSet(String macroName, MacroSetOJ macroSet) {
        this.macroName = macroName;
        this.macroSet = macroSet;
    }
}
