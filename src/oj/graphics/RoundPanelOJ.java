/*
 * RoundPanelOJ.java
 *
 * fully documented
 */

package oj.graphics;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * Used to draw roundrectangles in OualifiersSettingsOJ, ImageDefSettingsOJ, YtemDefSettingsOJ,
 * ColumnSettingsOJ
 */
public class RoundPanelOJ extends javax.swing.JPanel {
    
    /** Creates new form RoundPanelOJ */
    public RoundPanelOJ() {
        initComponents();
        setOpaque(false);
    }
    
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public void paint(Graphics g){
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        try{
        super.paint(g);}
        catch(Exception e){
        ij.IJ.showMessage("Paint Error");
        }
    }
    
}
