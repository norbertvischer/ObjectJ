/**
 * Project window with the four icons fully documented 12.2.2010
 */
package oj.gui.settings;

import ij.IJ;
import ij.Menus;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.border.Border;
import oj.OJ;
import oj.gui.KeyEventManagerOJ;

/**
 * Project window with the five icons
 */
public class ProjectSettingsOJ extends javax.swing.JFrame {

    private Dimension panelSize = new Dimension(600, 298);
    public final static String COLUMNS_PANEL = "Columns panel";//31.10.2008
    public final static String QUALIFIERS_PANEL = "Qualifiers panel";
    public final static String YTEM_DEFS_PANEL = "Items definitions panel";
    public final static String IMAGE_DEFS_PANEL = "Image definitions panel";
    public final static String PLOTS_PANEL = "Plots panel";
    private static ProjectSettingsOJ instance;
    private static Point prefferedLocation = new Point(300, 300);
    private Color selectedColor = Color.ORANGE;
    private Border unselectedBorder = javax.swing.BorderFactory.createEmptyBorder(11, 11, 11, 11);
    private Border selectedBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    public static String selectedPanelKey;
    private Hashtable settingsPanels = new Hashtable();

    /**
     * Creates new form SettingsFormOJ
     */
    public ProjectSettingsOJ() {
        ProjectSettingsOJ.instance = this;
        initComponents();
        initComponentsExt();
        lblImageDefsMousePressed(null);
        File file2 = new File(OJ.getData().getDirectory(), OJ.getData().getFilename());
        getRootPane().putClientProperty("Window.documentFile", file2);//setTitleBarProxy
        getRootPane().putClientProperty("Window.documentModified", false);
    }

    /**
     * returns corresponding panel, e.g. Images Panel when supplying "images"
     */
    public IControlPanelOJ getSettings() {
        return (IControlPanelOJ) settingsPanels.get(selectedPanelKey);
    }

    public IControlPanelOJ getSettings(String key) {
        return (IControlPanelOJ) settingsPanels.get(key);
    }

    public void selectQualifiersPanel() {
        lblQualifiersMousePressed(null);
    }

    public void selectColumnsPanel() {
        lblResultsMousePressed(null);
    }

    public void selectYtemDefsPanel() {
        lblYtemDefsMousePressed(null);
    }

    public void selectImageDefsPanel() {
        lblImageDefsMousePressed(null);
    }

    /**
     * first create the four panels, then add them to the gui
     */
    private void initComponentsExt() {
        addKeyListener(KeyEventManagerOJ.getInstance());

        settingsPanels.put(ProjectSettingsOJ.COLUMNS_PANEL, new ColumnSettingsOJ());
        settingsPanels.put(ProjectSettingsOJ.QUALIFIERS_PANEL, new QualifiersSettingsOJ());
        settingsPanels.put(ProjectSettingsOJ.YTEM_DEFS_PANEL, new YtemDefsSettingsOJ());
        settingsPanels.put(ProjectSettingsOJ.IMAGE_DEFS_PANEL, new ImageDefsSettingsOJ());
        if (PlotSettingsOJ.withPlots) {
            settingsPanels.put(ProjectSettingsOJ.PLOTS_PANEL, new PlotSettingsOJ());
        }
        pnlSettings.add((javax.swing.JPanel) settingsPanels.get(ProjectSettingsOJ.COLUMNS_PANEL), ProjectSettingsOJ.COLUMNS_PANEL);
        pnlSettings.add((javax.swing.JPanel) settingsPanels.get(ProjectSettingsOJ.QUALIFIERS_PANEL), ProjectSettingsOJ.QUALIFIERS_PANEL);
        pnlSettings.add((javax.swing.JPanel) settingsPanels.get(ProjectSettingsOJ.YTEM_DEFS_PANEL), ProjectSettingsOJ.YTEM_DEFS_PANEL);
        pnlSettings.add((javax.swing.JPanel) settingsPanels.get(ProjectSettingsOJ.IMAGE_DEFS_PANEL), ProjectSettingsOJ.IMAGE_DEFS_PANEL);

        if (PlotSettingsOJ.withPlots) {
            pnlSettings.add((javax.swing.JPanel) settingsPanels.get(ProjectSettingsOJ.PLOTS_PANEL), ProjectSettingsOJ.PLOTS_PANEL);
        }
        if (IJ.isWindows()) {
            btnShowInFinder.setText("Show in Explorer");
        }
    }

    /**
     * Hilight one of label "Images", "Objects", "Columns" and "Qualifiers"
     * background Defs",
     */
    private void updateLabels(JLabel selectedLabel) {
        selectedLabel.setBorder(selectedBorder);
        selectedLabel.setBackground(selectedColor);

        if (selectedLabel != lblQualifiers) {
            lblQualifiers.setBorder(unselectedBorder);
            lblQualifiers.setBackground(Color.WHITE);
        }

        if (selectedLabel != lblYtemDefs) {
            lblYtemDefs.setBorder(unselectedBorder);
            lblYtemDefs.setBackground(Color.WHITE);
        }
        if (selectedLabel != lblImageDefs) {
            lblImageDefs.setBorder(unselectedBorder);
            lblImageDefs.setBackground(Color.WHITE);
        }
        if (selectedLabel != lblResults) {
            lblResults.setBorder(unselectedBorder);
            lblResults.setBackground(Color.WHITE);
        }
        if (selectedLabel != lblPlots) {
            lblPlots.setBorder(unselectedBorder);
            lblPlots.setBackground(Color.WHITE);
        }
        if (!PlotSettingsOJ.withPlots && lblPlots != null) {
            lblPlots.hide();
        }
    }

    /**
     * so there is only one instance of ProjectSettingsOJ
     */
    public static ProjectSettingsOJ getInstance() {
        if (instance == null) {
            instance = new ProjectSettingsOJ();
        }
        return instance;
    }

    public static void rebuildSettings() {
        instance = new ProjectSettingsOJ();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblImageDefs = new javax.swing.JLabel();
        lblYtemDefs = new javax.swing.JLabel();
        lblResults = new javax.swing.JLabel();
        lblQualifiers = new javax.swing.JLabel();
        lblPlots = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 88), new java.awt.Dimension(20, 88), new java.awt.Dimension(20, 88));
        jPanel2 = new javax.swing.JPanel();
        btnObjectJResults = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 6), new java.awt.Dimension(0, 10));
        btnShowInFinder = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 12), new java.awt.Dimension(0, 32767));
        pnlMain = new javax.swing.JPanel();
        pnlSettings = new javax.swing.JPanel();

        setTitle("ObjectJ Settings");
        setBounds(new java.awt.Rectangle(0, 22, 580, 416));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(580, 416));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlHeader.setMaximumSize(new java.awt.Dimension(2147483647, 74));
        pnlHeader.setMinimumSize(new java.awt.Dimension(412, 74));
        pnlHeader.setPreferredSize(new java.awt.Dimension(412, 74));
        pnlHeader.setLayout(new javax.swing.BoxLayout(pnlHeader, javax.swing.BoxLayout.LINE_AXIS));

        lblImageDefs.setBackground(new java.awt.Color(255, 255, 255));
        lblImageDefs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/images-48x48-b.gif"))); // NOI18N
        lblImageDefs.setText("Images");
        lblImageDefs.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblImageDefs.setDoubleBuffered(true);
        lblImageDefs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblImageDefs.setMaximumSize(new java.awt.Dimension(84, 88));
        lblImageDefs.setMinimumSize(new java.awt.Dimension(72, 88));
        lblImageDefs.setOpaque(true);
        lblImageDefs.setPreferredSize(new java.awt.Dimension(76, 88));
        lblImageDefs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lblImageDefs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblImageDefsMousePressed(evt);
            }
        });
        pnlHeader.add(lblImageDefs);

        lblYtemDefs.setBackground(new java.awt.Color(255, 255, 255));
        lblYtemDefs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/objects-48x48-b.gif"))); // NOI18N
        lblYtemDefs.setText("Objects");
        lblYtemDefs.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblYtemDefs.setDoubleBuffered(true);
        lblYtemDefs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblYtemDefs.setMaximumSize(new java.awt.Dimension(84, 88));
        lblYtemDefs.setMinimumSize(new java.awt.Dimension(72, 88));
        lblYtemDefs.setOpaque(true);
        lblYtemDefs.setPreferredSize(new java.awt.Dimension(76, 88));
        lblYtemDefs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lblYtemDefs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblYtemDefsMousePressed(evt);
            }
        });
        pnlHeader.add(lblYtemDefs);

        lblResults.setBackground(new java.awt.Color(255, 255, 255));
        lblResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/columns-48x48-b.gif"))); // NOI18N
        lblResults.setText("Columns");
        lblResults.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblResults.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblResults.setMaximumSize(new java.awt.Dimension(84, 88));
        lblResults.setMinimumSize(new java.awt.Dimension(72, 88));
        lblResults.setOpaque(true);
        lblResults.setPreferredSize(new java.awt.Dimension(80, 88));
        lblResults.setRequestFocusEnabled(false);
        lblResults.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lblResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblResultsMousePressed(evt);
            }
        });
        pnlHeader.add(lblResults);

        lblQualifiers.setBackground(new java.awt.Color(255, 255, 255));
        lblQualifiers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/qualifiers-48x48-c.gif"))); // NOI18N
        lblQualifiers.setText("Qualifiers");
        lblQualifiers.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblQualifiers.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblQualifiers.setMaximumSize(new java.awt.Dimension(84, 88));
        lblQualifiers.setMinimumSize(new java.awt.Dimension(72, 88));
        lblQualifiers.setOpaque(true);
        lblQualifiers.setPreferredSize(new java.awt.Dimension(84, 88));
        lblQualifiers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lblQualifiers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblQualifiersMousePressed(evt);
            }
        });
        pnlHeader.add(lblQualifiers);

        lblPlots.setBackground(new java.awt.Color(255, 255, 255));
        lblPlots.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/plots-48x48.png"))); // NOI18N
        lblPlots.setText("Plots");
        lblPlots.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblPlots.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPlots.setMaximumSize(new java.awt.Dimension(84, 88));
        lblPlots.setMinimumSize(new java.awt.Dimension(72, 88));
        lblPlots.setOpaque(true);
        lblPlots.setPreferredSize(new java.awt.Dimension(76, 88));
        lblPlots.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lblPlots.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblPlotsMousePressed(evt);
            }
        });
        pnlHeader.add(lblPlots);
        pnlHeader.add(filler2);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(400, 88));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 88));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 88));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        btnObjectJResults.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        btnObjectJResults.setText("ObjectJ Results");
        btnObjectJResults.setMaximumSize(new java.awt.Dimension(118, 24));
        btnObjectJResults.setMinimumSize(new java.awt.Dimension(118, 24));
        btnObjectJResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObjectJResultsActionPerformed(evt);
            }
        });
        jPanel2.add(btnObjectJResults);
        jPanel2.add(filler1);

        btnShowInFinder.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        btnShowInFinder.setText("Show in Finder");
        btnShowInFinder.setMaximumSize(new java.awt.Dimension(118, 24));
        btnShowInFinder.setMinimumSize(new java.awt.Dimension(118, 24));
        btnShowInFinder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowInFinderActionPerformed(evt);
            }
        });
        jPanel2.add(btnShowInFinder);
        jPanel2.add(filler3);

        pnlHeader.add(jPanel2);

        getContentPane().add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlMain.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlSettings.setLayout(new java.awt.CardLayout());
        pnlMain.add(pnlSettings, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(857, 381));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        ProjectSettingsOJ.instance = null;
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        prefferedLocation = ProjectSettingsOJ.instance.getLocation();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        ProjectSettingsOJ.instance.setLocation(prefferedLocation);
    }//GEN-LAST:event_formWindowOpened

    private void lblQualifiersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQualifiersMousePressed
        updateLabels(lblQualifiers);
        selectedPanelKey = ProjectSettingsOJ.QUALIFIERS_PANEL;
        resizeControlPanel();
    }//GEN-LAST:event_lblQualifiersMousePressed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if (IJ.isMacintosh() && IJ.getInstance() != null) {
            IJ.wait(1); // needed for 1.4.1 on OS X
            if (this.getMenuBar() != Menus.getMenuBar()) {
                this.setMenuBar(Menus.getMenuBar());
            }
        }
    }//GEN-LAST:event_formWindowActivated

private void lblYtemDefsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblYtemDefsMousePressed
    updateLabels(lblYtemDefs);
    selectedPanelKey = ProjectSettingsOJ.YTEM_DEFS_PANEL;
    resizeControlPanel();
}//GEN-LAST:event_lblYtemDefsMousePressed

private void lblImageDefsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageDefsMousePressed
    updateLabels(lblImageDefs);
    selectedPanelKey = ProjectSettingsOJ.IMAGE_DEFS_PANEL;
    resizeControlPanel();
}//GEN-LAST:event_lblImageDefsMousePressed

private void lblResultsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblResultsMousePressed
    updateLabels(lblResults);
    selectedPanelKey = ProjectSettingsOJ.COLUMNS_PANEL;
    resizeControlPanel();
}//GEN-LAST:event_lblResultsMousePressed

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    if (selectedPanelKey != null) {
        IControlPanelOJ cPanel = ((IControlPanelOJ) settingsPanels.get(selectedPanelKey));
        if (cPanel != null) {
            cPanel.setPanelSize(pnlSettings.getSize());
        }
    }
}//GEN-LAST:event_formComponentResized

  private void lblPlotsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPlotsMousePressed

      if (PlotSettingsOJ.withPlots) {
          updateLabels(lblPlots);
          selectedPanelKey = ProjectSettingsOJ.PLOTS_PANEL;
          resizeControlPanel();
      }
  }//GEN-LAST:event_lblPlotsMousePressed

    private void btnShowInFinderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowInFinderActionPerformed
        oj.gui.menuactions.ViewActionsOJ.ShowProjectFolderAction.actionPerformed(null);
    }//GEN-LAST:event_btnShowInFinderActionPerformed

    private void btnObjectJResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObjectJResultsActionPerformed
        oj.gui.menuactions.ViewActionsOJ.ResultsViewAction.actionPerformed(null);
    }//GEN-LAST:event_btnObjectJResultsActionPerformed

    /**
     * each of the four panels uses different size
     */
    private void resizeControlPanel() {
        ProjectSettingsOJ.getInstance().setTitle(OJ.getData().getName() + ".ojj");
        ((CardLayout) pnlSettings.getLayout()).show(pnlSettings, selectedPanelKey);
        Dimension dim = ((IControlPanelOJ) settingsPanels.get(selectedPanelKey)).getPanelSize();
        pnlSettings.setPreferredSize(dim);
        pnlSettings.setSize(dim);
        ProjectSettingsOJ.instance.pack();
    }

    public static void close() {
        if (instance != null) {
            instance.setVisible(false);
            instance = null;
        }
    }

    /**
     * used for histograms
     */
    public ColumnSettingsOJ getColumnsPanel() {//9.2.2010
        return (ColumnSettingsOJ) settingsPanels.get(ProjectSettingsOJ.COLUMNS_PANEL);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnObjectJResults;
    private javax.swing.JButton btnShowInFinder;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblImageDefs;
    private javax.swing.JLabel lblPlots;
    private javax.swing.JLabel lblQualifiers;
    private javax.swing.JLabel lblResults;
    private javax.swing.JLabel lblYtemDefs;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSettings;
    // End of variables declaration//GEN-END:variables
}
