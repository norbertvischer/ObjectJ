/* MacroActionToolOJ.java
 *
 */
package oj.gui.tools;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import oj.macros.MacroItemOJ;
import oj.util.UtilsOJ;
import oj.macros.MacroSetOJ;

/** Tool that appears as additional "Button" in the ObjectJ Tools window. When clicking with this tool in
 * an image, the corresponding macro is executed
 */
public class MacroActionToolOJ extends ActionToolAdapterOJ {

    private MacroSetOJ macroSet;
    private String macroName;

    public MacroActionToolOJ() {

    }

    public MacroActionToolOJ(String macroName, MacroSetOJ macroSet) {
        super();
        setMacroSet(macroName, macroSet);
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

    public void setMacroSet(String macroName, MacroSetOJ macroSet) {
        this.macroName = macroName;
        this.macroSet = macroSet;
    }

    public void actionPerformed() {
        ActionEvent action = new ActionEvent(this, 0, macroName);
        ((ActionListener) macroSet).actionPerformed(action);
    }
}
