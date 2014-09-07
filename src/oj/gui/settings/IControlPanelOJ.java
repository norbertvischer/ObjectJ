package oj.gui.settings;
// fully documented
import java.awt.Dimension;

/**
 * Interface for the control panels so that they
 * are forced to include methods for close, getPanelSize and setPanelSize
 */
public interface IControlPanelOJ {
    
    public void close();
    public Dimension getPanelSize();
    public void setPanelSize(Dimension panelSize);
}
