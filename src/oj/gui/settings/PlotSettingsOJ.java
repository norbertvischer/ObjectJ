package oj.gui.settings;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Plot;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.plugin.Colors;
import ij.util.Tools;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import oj.OJ;
import oj.io.InputOutputOJ;
import oj.processor.events.QualifierChangedEventOJ;
import oj.processor.events.QualifierChangedListenerOJ;
import oj.project.CellOJ;
import oj.project.CellsOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;
import oj.project.results.QualifiersOJ;
import oj.util.UtilsOJ;

public class PlotSettingsOJ extends javax.swing.JPanel implements IControlPanelOJ, TextListener, ItemListener, QualifierChangedListenerOJ {

    public static boolean withPlots = false;
    private TextField xMinField, xMaxField, binField, yMinField, yMaxField, titleField;
    private Object fieldWithFocus;
    private Vector fields;
    private Vector choices;
    private Choice xAxisChoice, yAxisChoice;
    private Dimension panelSize = new Dimension(585, 585);
    private ArrayList<String> plotDefs;
    private String plotTitle;//current plot
    private String colTitleX, colTitleY;//current plot
    private int plotID;//
    private String[] colTitlesY;
    private double[] dotsX, dotsY;//dot positions in plots, and the owning object
    private int[] dotsObjIndexes0;//object index (owner of dotX, dotY) zero based 
    private double xMin, xMax, yMin, yMax, binWidth = 0;
    private String[] plotMarkers = " ,  , , , , , , , , ".split(",");
    private String[] plotColors = " ,  , , , , , , , , ".split(",");
    private double[] extremes = new double[4];
    private boolean[] autoFlags = new boolean[4];
    private boolean autoRefresh = true;
    private CheckFrontWindowThread abc;
    boolean running = true;
    private String oldTitle = "";
    double[] binnedXvalues, binnedYvalues, binnedErrorBars;

    public PlotSettingsOJ() {
        plotDefs = OJ.getData().getResults().getPlots().getPlotDefs();
        initComponents();
        titlesToTable();
        startThread();

    }

    void startThread() {
        abc = new CheckFrontWindowThread("CheckFrontWindowThread");
        abc.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jPopupMenu4 = new javax.swing.JPopupMenu();
        titlesPane = new javax.swing.JScrollPane();
        tblPlotTitles = new javax.swing.JTable();
        scrollCurrentPlotDef = new javax.swing.JScrollPane();
        pnlCurrentPlotDef = new javax.swing.JTextPane();
        btnPlot = new javax.swing.JButton();
        choiceNewPlot = new javax.swing.JComboBox();
        choiceQualify = new javax.swing.JComboBox();
        cbxAutoRefresh = new javax.swing.JCheckBox();
        btnHelp = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        lblPercent1 = new java.awt.Label();

        jMenu1.setText("jMenu1");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        tblPlotTitles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Plot Ttitles"
            }
        ));
        tblPlotTitles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPlotTitlesMouseClicked(evt);
            }
        });
        titlesPane.setViewportView(tblPlotTitles);

        scrollCurrentPlotDef.setViewportView(pnlCurrentPlotDef);

        btnPlot.setText("Plot");
        btnPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlotActionPerformed(evt);
            }
        });

        choiceNewPlot.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "New Plot...", "-", "Histogram", "Line Plot", "Scatter Plot", "Scatter with Error Bars", "-", "Duplicate Selection" }));
        choiceNewPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceNewPlotActionPerformed(evt);
            }
        });

        choiceQualify.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Qualify...", "-", "Qualify All", "Qualify Points in ROI", "Disqualify Points in ROI" }));
        choiceQualify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceQualifyActionPerformed(evt);
            }
        });

        cbxAutoRefresh.setSelected(true);
        cbxAutoRefresh.setText("Auto-refresh");
        cbxAutoRefresh.setToolTipText("If checked, clicking a title in left panel will perform the plot");
        cbxAutoRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAutoRefreshActionPerformed(evt);
            }
        });

        btnHelp.setText("Help");

        jButton1.setText("Show in Igor");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblPercent1.setText("All");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbxAutoRefresh)
                            .add(choiceNewPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(16, 16, 16)
                .add(scrollCurrentPlotDef, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(260, 260, 260)
                .add(btnPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(30, 30, 30)
                .add(btnHelp)
                .add(155, 155, 155)
                .add(choiceQualify, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20)
                .add(lblPercent1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(cbxAutoRefresh)
                        .add(7, 7, 7)
                        .add(choiceNewPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(jButton1))
                    .add(scrollCurrentPlotDef, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 350, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(btnPlot)
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnHelp)
                    .add(choiceQualify, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPercent1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents
//Click once in column 0 to plot; click once in column 1 to edit
    private void tblPlotTitlesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlotTitlesMouseClicked
        boolean ctrl = evt.isControlDown();
        boolean right = SwingUtilities.isRightMouseButton(evt) || IJ.isMacOSX() && ctrl;
        if (right) {
            IJ.showMessage("rightButton");
        } else {
            int y = evt.getY();
            //int row = tblPlotTitles.rowAtPoint(evt.getPoint());
            //int col = tblPlotTitles.columnAtPoint(evt.getPoint());
            int selected[] = tblPlotTitles.getSelectedRows();

            for (int nPlot = 0; nPlot < selected.length; nPlot++) {
                String thisDef = plotDefs.get(selected[nPlot]);
                pnlCurrentPlotDef.setText(thisDef);
                defToVars(thisDef);
                if (autoRefresh) {
                    makePlot();
                }
            }
        }
    }//GEN-LAST:event_tblPlotTitlesMouseClicked

    private void btnPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlotActionPerformed
        String txt = pnlCurrentPlotDef.getText();
        defToVars(txt);
        addDef(txt);
        makePlot();
    }//GEN-LAST:event_btnPlotActionPerformed

    private void choiceNewPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceNewPlotActionPerformed
        int index = choiceNewPlot.getSelectedIndex();
        String choice = (String) choiceNewPlot.getSelectedItem();
        choiceNewPlot.setSelectedIndex(0);
        if (index >= 2) {
            String plotDef = newPlotDialog(choice);
            if (plotDef == null) {
                return;
            }
            defToVars(plotDef);
            pnlCurrentPlotDef.setText(plotDef);


            plotDefs.add(plotDef);
            updatePlotDefTitles();
            makePlot();
        }
    }//GEN-LAST:event_choiceNewPlotActionPerformed

    private void cbxAutoRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAutoRefreshActionPerformed
        autoRefresh = !autoRefresh;
    }//GEN-LAST:event_cbxAutoRefreshActionPerformed

    private void choiceQualifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceQualifyActionPerformed
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            return;
        }
        Roi roi, roi2 = null;
        roi = imp.getRoi();
        if (roi != null) {
            roi2 = (Roi) roi.clone();
            roi2.setImage(null);
        }

        int index = choiceQualify.getSelectedIndex();
        //String choice = (String) choiceQualify.getSelectedItem();
        choiceQualify.setSelectedIndex(0);
        boolean inside = (index == 3);
        if (index == 2) {
            OJ.getData().getCells().qualifyAllCells();
            OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ALL, true);
        }
        if (index == 3 || index == 4) {
            if (roi == null) {
                IJ.showStatus("No Roi found");
                IJ.beep();
                return;
            }
            String currentTitle = IJ.getImage().getTitle();
            if (!currentTitle.equalsIgnoreCase(plotTitle)) {
                IJ.showStatus("Plot '" + plotTitle + "' is not  front");
                IJ.beep();
                return;
            }
            getColumnPair(colTitleX, colTitleY, true);//includeUnqualified=true, includeNaN=true
            qualifyRoi(imp, dotsX, dotsY, dotsObjIndexes0, inside);

            OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
        }
        getColumnPair(colTitleX, colTitleY, false);//includeUnqualified=false, includeNaN=true
        adjustExtremes();
        makePlot();
        imp = IJ.getImage();

        if (imp != null && roi != null) {
            roi2.setImage(imp);
            imp.setRoi(roi2, true);

        }
        OJ.getImageProcessor().updateOpenImages();
    }//GEN-LAST:event_choiceQualifyActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
        String txt= "beep;abc;wait(1000);beep";
        IJ.runMacro(txt , "");
        
        //createIgorIpf();
    }//GEN-LAST:event_jButton1ActionPerformed

    void qualifyRoi(ImagePlus imp, double[] xArr, double[] yArr, int[] indexes0, boolean chooseInner) {
        CellsOJ cells = OJ.getData().getCells();
        int nObjects = cells.getCellsCount();
        Roi roi = imp.getRoi();
        if (roi != null) {
            for (int jj = 0; jj < xArr.length; jj++) {
                int height = imp.getHeight();
                Calibration cal = imp.getCalibration();

                int x = (int) Math.round(cal.getRawX(xArr[jj]));
                int y = (int) Math.round(cal.getRawY(yArr[jj], height));
                int index0 = indexes0[jj];
                boolean inRoi = roi.contains(x, y);
                boolean flag = (inRoi && chooseInner) || (!inRoi && !chooseInner);//XOR

                CellOJ cell = cells.getCellByIndex(index0);
                if (cell != null) {
                    cell.setQualified(flag);
                }
            }


        }
    }

    //clears old title and adds
    void applyCurrentPlotDef() {
        String[] strs = new String[2];
        int current = -1;
        for (int jj = 0; jj < plotDefs.size(); jj++) {
            String thisDef = (String) plotDefs.get(jj);
            if (thisDef != null) {
                int titleLength = thisDef.indexOf("\n");
                String thisTitle = thisDef;
                if (titleLength > 1) {
                    thisTitle = thisTitle.substring(0, titleLength);
                } else {
                    thisTitle = thisDef;
                }
                thisTitle = thisTitle.replaceFirst("title=", "");
                if (thisTitle.equals(plotTitle)) {
                    current = jj;
                }
            }
        }
        if (current >= 0) {
            plotDefs.set(current, pnlCurrentPlotDef.getText());
        } else {
            plotDefs.add(pnlCurrentPlotDef.getText());
        }
    }
    //extracts titles from plotDefs and puts them into the table column 0

    void updatePlotDefTitles() {
        DefaultTableModel model = (DefaultTableModel) tblPlotTitles.getModel();
        model.setRowCount(0);
        ArrayList plotDefs = OJ.getData().getResults().getPlots().getPlotDefs();
        //plotDefs.add(plotDefs);
        String[] strs = new String[1];

        for (int jj = 0; jj < plotDefs.size(); jj++) {
            String thisDef = (String) plotDefs.get(jj);
            if (thisDef != null) {
                int titleLength = thisDef.indexOf("\n");
                String thisTitle = thisDef;
                if (titleLength > 1) {
                    thisTitle = thisTitle.substring(0, titleLength);
                } else {
                    thisTitle = thisDef;
                }
                thisTitle = thisTitle.replaceFirst("title=", "");
                strs[0] = thisTitle;
                model.addRow(strs);
            }
        }
    }

    String newPlotDialog(String plotType) {
        String[] columnTitles = OJ.getData().getResults().getColumns().columnLinkedNamesToArray();

        GenericDialog gd = new GenericDialog("New " + plotType, IJ.getInstance());

        gd.addChoice("x-Axis:", columnTitles, columnTitles[0]);
        gd.addStringField("X-Min:", "auto");
        gd.addStringField("X-Max:", "auto (8.97)");
        gd.addStringField("BinWidth:", "");
        gd.addMessage("\t");
        gd.addChoice("y-Axis:", columnTitles, columnTitles[1]);
        gd.addStringField("Y-Min:", "auto (0.46)");
        gd.addStringField("Y-Min:", "auto (1.53)");
        gd.addChoice("Marker type:", "Circles,Dots,x,Error Bars".split(","), "Circles");
        gd.addChoice("Color:", "red green blue orange magenta cyan black gray".split(" "), "red");
        gd.addStringField("Name:", columnTitles[1] + " vs. " + columnTitles[0], 32);

        fields = gd.getStringFields();//fields and choices may change while user handles dialog
        choices = gd.getChoices();
        xAxisChoice = (Choice) choices.elementAt(0);
        xAxisChoice.addItemListener(this);
        yAxisChoice = (Choice) choices.elementAt(1);
        yAxisChoice.addItemListener(this);

        xMinField = (TextField) fields.elementAt(0);
        xMaxField = (TextField) fields.elementAt(1);
        binField = (TextField) fields.elementAt(2);
        yMinField = (TextField) fields.elementAt(3);
        yMaxField = (TextField) fields.elementAt(4);
        titleField = (TextField) fields.elementAt(5);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return null;
        }


        String xColumn = gd.getNextChoice();
        String xmin = gd.getNextString();
        String xmax = gd.getNextString();
        String xBin = gd.getNextString();

        String yColumn = gd.getNextChoice();
        String ymin = gd.getNextString();
        String ymax = gd.getNextString();
        String theTitle = gd.getNextString();
        plotMarkers[0] = gd.getNextChoice();
        plotColors[0] = gd.getNextChoice();

        if (xmin.contains("auto")) {
            xmin = "";
        }
        if (xmax.contains("auto")) {
            xmax = "";
        }
        if (ymin.contains("auto")) {
            ymin = "";
        }
        if (ymax.contains("auto")) {
            ymax = "";
        }

        theTitle = uniqueTitle(theTitle);
        String txt = "title=" + theTitle + "\n\n";

        txt += "xAxis=" + xColumn + "\n";
        txt += "xMin=" + xmin + "\n";
        txt += "xMax=" + xmax + "\n";
        txt += "binWidth=" + xBin + "\n\n";

        txt += "yAxis=" + yColumn + "\n";
        txt += "yMin=" + ymin + "\n";
        txt += "yMax=" + ymax + "\n\n";

        txt += "marker=" + plotMarkers[0] + "\n";
        txt += "color=" + plotColors[0] + "\n";
        return txt;

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHelp;
    private javax.swing.JButton btnPlot;
    private javax.swing.JCheckBox cbxAutoRefresh;
    private javax.swing.JComboBox choiceNewPlot;
    private javax.swing.JComboBox choiceQualify;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JPopupMenu jPopupMenu4;
    private java.awt.Label lblPercent1;
    private javax.swing.JTextPane pnlCurrentPlotDef;
    private javax.swing.JScrollPane scrollCurrentPlotDef;
    private javax.swing.JTable tblPlotTitles;
    private javax.swing.JScrollPane titlesPane;
    // End of variables declaration//GEN-END:variables

    String uniqueTitle(String title) {
        String newTitle = title;
        int trials = 0;
        for (int jj = 0; jj < plotDefs.size() && trials < 26; jj++) {
            String[] lines = plotDefs.get(jj).split("\n");
            String[] words = lines[0].split("=");
            String tit = words[1];
            int len = tit.length();
            if (tit.equalsIgnoreCase(newTitle)) {
                if (tit.charAt(len - 2) == '-') {
                    newTitle = tit.substring(0, len - 1) + ((char) (tit.charAt(len - 1) + 1));
                    jj = 0;
                    trials++;
                } else {
                    newTitle = tit + "-a";
                }
            }
        }
        return newTitle;
    }

    public void qualifierChanged(QualifierChangedEventOJ e) {

//        int qual = OJ.getData().getCells().getQualifiedCellsCount();
//        int all = OJ.getData().getCells().getCellsCount();
//        lblPercent.setText(IJ.d2s(100.0*qual/all)+"%");
//        


        int qMethod = OJ.getData().getResults().getQualifiers().getQualifyMethod();
        int q1 = OJ.getData().getCells().getQualifiedCellsCount();
        int n1 = OJ.getData().getCells().getCellsCount();
        String txt;
        if (qMethod == QualifiersOJ.QUALIFY_METHOD_ALL) {
            txt = "All";
            lblPercent1.setForeground(Color.black);

        } else {
            txt = "" + IJ.d2s(100.0 * q1 / n1, 1) + "%";
            lblPercent1.setForeground(Color.red);
        }
        lblPercent1.setText(txt);


    }

    public void textValueChanged(TextEvent e) {
        Object source = e.getSource();
        //double newXScale = xscale;

        if (source == xMinField && fieldWithFocus == xMinField) {
            String newXText = xMinField.getText();
        }
    }

    //show min and max in auto-fields
    public void itemStateChanged(ItemEvent e) {
        Choice choice = (Choice) e.getSource();
        double min = 0, max = 0;

        if (choice == xAxisChoice || choice == yAxisChoice) {
            String colTitle = choice.getSelectedItem();
            ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByName(colTitle);
            min = column.getStatistics().getStatisticsValueByName("Minimum");
            max = column.getStatistics().getStatisticsValueByName("Maximum");

        }
        if (choice == xAxisChoice) {
            if (xMinField.getText().startsWith("auto")) {
                xMinField.setText("auto   (" + IJ.d2s(min, 2) + ")");
            }
            if (xMaxField.getText().startsWith("auto")) {
                xMaxField.setText("auto   (" + IJ.d2s(max, 2) + ")");
            }
        }
        if (choice == yAxisChoice) {
            if (yMinField.getText().startsWith("auto")) {
                yMinField.setText("auto   (" + IJ.d2s(min, 2) + ")");
            }
            if (yMaxField.getText().startsWith("auto")) {
                yMaxField.setText("auto   (" + IJ.d2s(max, 2) + ")");
            }
        }
        if (choice == xAxisChoice || choice == yAxisChoice) {
            String title = yAxisChoice.getSelectedItem() + " vs. " + xAxisChoice.getSelectedItem();
            String tmpTitle = title;
            int suffix = 1;
            while (titlesPos(tmpTitle) >= 0) {
                tmpTitle = title + "-" + suffix++;
            }
            titleField.setText(tmpTitle);
        }
    }

    public void focusGained(FocusEvent e) {
        fieldWithFocus = e.getSource();
    }

    public void focusLost(FocusEvent e) {
    }

    public Dimension getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(Dimension panelSize) {
        this.panelSize = panelSize;
    }

    public void close() {
        running = false;
    }

    //From both titles, create two arrays that do not contain NaNs, and put min/max to extremes[] in case neded for auto-scale
    //result is stored in class variables dotsX and dotsY, none of which will contain NaNs
    void getColumnPair(String xTitle, String yTitle, boolean includeUnqualified) {
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        ColumnOJ columnX = columns.getColumnByName(xTitle);
        ColumnOJ columnY = columns.getColumnByName(yTitle);
        if (columnX == null || columnY == null) {
            return;
        }
        double[] fullArrayX = columnX.getDoubleArray(includeUnqualified, true);//we have to include NaNs to keep both arrays synchronized
        double[] fullArrayY = columnY.getDoubleArray(includeUnqualified, true);//
        int[] fullObjIndexes = OJ.getData().getCells().getIndexes0(includeUnqualified);//



        int len = fullArrayX.length;
        double[] dotsTmpX = new double[len];//will partially be filled
        double[] dotsTmpY = new double[len];
        int[] dotsTmpIndexes = new int[len];//will partially be filled
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = xmin;
        double xmax = -xmin;
        double ymax = -xmin;
        int count = 0;
        for (int jj = 0; jj < len; jj++) {
            if (jj < len) {
                double x = fullArrayX[jj];
                double y = fullArrayY[jj];
                int index0 = fullObjIndexes[jj];
                if (!Double.isNaN(x + y)) {//only append if both values are not NaN
                    dotsTmpX[count] = x;
                    dotsTmpY[count] = y;
                    dotsTmpIndexes[count] = index0;
                    count++;
                    if (x < xmin) {
                        xmin = x;
                    }
                    if (y < ymin) {
                        ymin = y;
                    }
                    if (x > xmax) {
                        xmax = x;
                    }
                    if (y > ymax) {
                        ymax = y;
                    }
                }
            }
        }
        dotsX = new double[count];
        dotsY = new double[count];
        dotsObjIndexes0 = new int[count];
        for (int jj = 0; jj < count; jj++) {
            dotsX[jj] = dotsTmpX[jj];
            dotsY[jj] = dotsTmpY[jj];
            dotsObjIndexes0[jj] = dotsTmpIndexes[jj];
        }
        extremes[0] = xmin;
        extremes[1] = xmax;
        extremes[2] = ymin;
        extremes[3] = ymax;
        if (binWidth > 0) {
            getConfidences(dotsX, dotsY, binWidth, xmin);
        }
    }

    //parses a definition txt and sets class vars xmin, xmax, xaxis etc
    void defToVars(String txt) {
        String[] lines = txt.split("\n");
        double nan = Double.NaN;
        xMin = xMax = yMin = yMax = binWidth = nan;
        plotColors[0] = "red";
        plotMarkers[0] = "circles";
        //String title = "";
        for (String line : lines) {
            String[] tokens = line.split("=");
            String key = tokens[0].toLowerCase();
            if (tokens.length >= 2) {
                if (key.equalsIgnoreCase("title")) {
                    plotTitle = tokens[1];
                }
                if (key.equalsIgnoreCase("xvalues")) {
                    colTitleX = tokens[1];
                }
                if (key.equalsIgnoreCase("yvalues")) {
                    colTitleY = tokens[1];
                }
                if (key.equalsIgnoreCase("xrange")) {
                    String[] range = tokens[1].split(",");
                    xMin = Tools.parseDouble(range[0], nan);
                    xMax = Tools.parseDouble(range[1], nan);
                }
               if (key.equalsIgnoreCase("yrange")) {
                    String[] range = tokens[1].split(",");
                    yMin = Tools.parseDouble(range[0], nan);
                    yMax = Tools.parseDouble(range[1], nan);
                }

//                if (key.equalsIgnoreCase("xmax")) {
//                    xMax = Tools.parseDouble(tokens[1], nan);
//                }
//                if (key.equalsIgnoreCase("ymin")) {
//                    yMin = Tools.parseDouble(tokens[1], nan);
//                }
//                if (key.equalsIgnoreCase("ymax")) {
//                    yMax = Tools.parseDouble(tokens[1], nan);
//                }
                if (key.equalsIgnoreCase("color")) {
                    plotColors[0] = tokens[1];
                }
                if (key.equalsIgnoreCase("marker")) {
                    plotMarkers[0] = tokens[1];
                }
                if (key.equalsIgnoreCase("binwidth")) {
                    binWidth = Tools.parseDouble(tokens[1], nan);
                }
            }

        }

        getColumnPair(colTitleX, colTitleY, false);//qualifiedOnly
        adjustExtremes();

    }

    void varsToDef() {

        String txt = "";
        txt += "title=" + plotTitle + "\n\n";
        txt += "xAxis=" + colTitleX + "\n";
        txt += "xMin=";
        if (!autoFlags[0]) {
            txt += extremes[0];
        }
        txt += "\nxMax=";

        if (!autoFlags[1]) {
            txt += extremes[1];
        }

        txt += "\n\nyAxis=" + colTitleY + "\n";



        txt += "yMin=";
        if (!autoFlags[2]) {
            txt += extremes[2];
        }
        txt += "\nyMax=";

        if (!autoFlags[3]) {
            txt += extremes[3];
        }

        txt += "\ncolor1=" + colTitleX + "\n";
        txt += "marker1=" + colTitleX + "\n";
    }

    //converts definitions to array of titles,  e.g. for the table of titles
    private String[] defsToTitles() {
        int len = plotDefs.size();
        String[] titles = new String[len];

        for (int jj = 0; jj < len; jj++) {
            String def = plotDefs.get(jj);
            titles[jj] = defToTitle(def);
        }
        return titles;
    }

    //converts a definition to its title
    String defToTitle(String def) {
        if (!def.startsWith("title=")) {
            return null;
        }
        int pos1 = "title=".length();
        int pos2 = def.indexOf("\n");
        return def.substring(pos1, pos2);

    }

    //converts a title to entire definition text
    String titleToDef(String title, boolean selectIt) {
        int len = plotDefs.size();
        for (int jj = 0; jj < len; jj++) {
            String def = plotDefs.get(jj);
            if (def.startsWith("title=" + title)) {
                if (selectIt) {
                    tblPlotTitles.getSelectionModel().setSelectionInterval(jj, jj);
                }
                return def;
            }
        }
        return "";
    }

    void selectPlotTitle(String title) {
        String def = titleToDef(title, true);
        if (!def.equals("")) {
            pnlCurrentPlotDef.setText(def);
            defToVars(def);
        }
    }

    //adds or replaces a definition text
    void addDef(String def) {
        String title = defToTitle(def);
        String[] titles = defsToTitles();
        for (int jj = 0; jj < titles.length; jj++) {
            if (title.equalsIgnoreCase(titles[jj])) {
                plotDefs.set(jj, def);
                return;
            }
        }
        plotDefs.add(def);
        titlesToTable();

    }

    private void titlesToTable() {

        DefaultTableModel model = (DefaultTableModel) tblPlotTitles.getModel();
        model.setRowCount(0);
        String[] strs = defsToTitles();
        for (int jj = 0; jj < strs.length; jj++) {
            String[] pair = new String[]{strs[jj], ">>"};
            model.addRow(pair);
        }
    }

    int titlesPos(String title) {
        String[] strs = defsToTitles();
        for (int jj = 0; jj < strs.length; jj++) {
            if (title.equalsIgnoreCase(strs[jj])) {
                return jj;
            }
        }
        return -1;
    }

    void makePlot() {

//        int qMethod = OJ.getData().getResults().getQualifiers().getQualifyMethod();
//        int q1 = OJ.getData().getCells().getQualifiedCellsCount();
//        int n1 = OJ.getData().getCells().getCellsCount();
//        String txt;
//        if (qMethod == QualifiersOJ.QUALIFY_METHOD_ALL) {
//            txt = "All";
//            lblPercent.setForeground(Color.black);
//
//        } else {
//            txt = "" + IJ.d2s(100.0 * q1 / n1, 1) + "%";
//            lblPercent.setForeground(Color.red);
//        }
//        lblPercent.setText(txt);
//
        ImagePlus imp = WindowManager.getImage(plotTitle);
        int locx = 0, locy = 0;// ww = 0, hh = 0;
        if (imp != null) {
            locx = imp.getWindow().getLocation().x;
            locy = imp.getWindow().getLocation().y;
            //ww = imp.getWindow().getSize().width;
            //hh = imp.getWindow().getSize().height;
            imp.close();
        }

        if ((dotsX == null) || (dotsY == null) || dotsX.length != dotsY.length || dotsX.length < 1) {
            IJ.beep();
            IJ.showStatus("Plot data available");
            return;
        }
        int mType = 0;
        String[] types = "circles x line boxes triangles crosses dots".split(" ");
        for (int jj = 0; jj < types.length; jj++) {
            if (types[jj].equalsIgnoreCase(plotMarkers[0])) {
                mType = jj;
            }
        }

        Plot plot;
        boolean withErrorBars = binWidth > 0;
        Color color = Colors.getColor(plotColors[0], Color.orange);


        if (withErrorBars) {
            plot = new Plot(plotTitle, colTitleX, colTitleY, binnedXvalues, binnedYvalues);
            plot.setLimits(xMin, xMax, yMin, yMax);
            plot.addErrorBars(binnedErrorBars);
            plot.setColor(color);
            plot.addPoints(dotsX, dotsY, Plot.DOT);
            plot.setColor(Color.blue);


        } else {
            plot = new Plot(plotTitle, colTitleX, colTitleY);
            plot.setLimits(xMin, xMax, yMin, yMax);
            plot.setColor(color);
            plot.addPoints(dotsX, dotsY, mType);

        }
        plot.draw();
        plot.show();
        if (locx > 0) {
            IJ.runMacro("setLocation(" + locx + "," + locy + ");");
        }
    }

    void adjustExtremes() {
        if (Double.isNaN(xMin)) {
            xMin = extremes[0];
            autoFlags[0] = true;
        }
        if (Double.isNaN(xMax)) {
            xMax = extremes[1];
            autoFlags[1] = true;
        }
        if (Double.isNaN(yMin)) {
            yMin = extremes[2];
            autoFlags[2] = true;
        }
        if (Double.isNaN(yMax)) {
            yMax = extremes[3];
            autoFlags[3] = true;
        }
    }

//calculates globals binnedXvalues, binnedYvalues, binnedErrorBars;
    void getConfidences(double[] xValues, double[] yValues, double binWidth, double xStart) {//eg binWidth = 0.5

        double k90 = 1.645, k95 = 1.9600, k98 = 2.3264, k99 = 2.5758;
        double confid = k95;

        int[] rankPArr = Tools.rank(xValues);
        int count = 0;
        double[] group = new double[9000];
        double[] means = new double[500];
        double[] e95 = new double[500];
        int[] counts = new int[500];
        double[] midBins = new double[500];

        int nBins = 0;//need not be contiguous	
        int nQualified = xValues.length;
        for (int jj = 0; jj < nQualified; jj++) {
            int index = rankPArr[jj];//start with smallest value
            double xVal = xValues[index];
            double yVal = yValues[index];
            int bin = (int) Math.floor((xVal - xStart) / binWidth);//0, 1, 2...
            boolean full = (jj == (nQualified - 1));
            if (!full) {
                int nextIndex = rankPArr[jj + 1];
                int nextBin = (int) Math.floor((xValues[nextIndex] - xStart) / binWidth);
                full = bin != nextBin;
            }
            group[count++] = yVal;
            if (full && count > 1) {
                //IJ.showProgress(jj / nQualified);
                group = trim(group, count);
                double mean = UtilsOJ.getStatistics(group, "mean");
                double stdDev = UtilsOJ.getStatistics(group, "std");
                double midBin = xStart + binWidth * bin + binWidth / 2;
                midBins[nBins] = midBin;//0, 0.5, 1
                means[nBins] = mean;
                //stDevs[nBins] = stdDev;
                e95[nBins] = confid * stdDev / Math.sqrt(count);
                counts[nBins] = count;
                group = new double[9000];
                count = 0;
                nBins++;
            }
            if (full) {
                count = 0;
            }

        }
        midBins = trim(midBins, nBins);
        means = trim(means, nBins);
        //stDevs = trim(stDevs, nBins);
        e95 = trim(e95, nBins);
        //counts = trim(counts, nBins);

        binnedXvalues = midBins;
        binnedYvalues = means;
        binnedErrorBars = e95;


    }

    double[] trim(double[] arr, int newSize) {
        double[] result = new double[newSize];
        for (int jj = 0; jj < newSize && jj < arr.length; jj++) {
            result[jj] = arr[jj];
        }
        return result;
    }

    int[] trim(int[] arr, int newSize) {
        int[] result = new int[newSize];
        for (int jj = 0; jj < newSize && jj < arr.length; jj++) {
            result[jj] = arr[jj];
        }
        return result;
    }

    private void createIgorIpf() {
        String txt = "IGOR\n";
        txt += "WAVES/O Axis,Dia" + "\n";
        txt += "BEGIN" + "\n";
        for (int jj = 0; jj < dotsX.length; jj++) {
            txt += IJ.d2s(dotsX[jj], 4) + "\t" + IJ.d2s(dotsY[jj], 4) + "\n";
        }
        txt += "END" + "\n";
        txt += "X Display Dia vs Axis" + "\n";
        txt += "X Modify grid=1" + "\n";
        txt += "X ModifyGraph mode=2" + "\n";
        txt += "X SetAxis left 0,1.5" + "\n";
        txt += "X SetAxis bottom 0,5" + "\n";
        txt += "X ModifyGraph lsize=1.5" + "\n";
        txt += "X Open/P=home K0 as \"Igor Batch Results\"; Close K0" + "\n";
        String dir = OJ.getData().getDirectory();
        new InputOutputOJ().saveIgorAsText(txt, dir, "tmp.ipf");
    }

    class CheckFrontWindowThread extends Thread {

        public CheckFrontWindowThread(String str) {
            super(str);
        }

        public void run() {
            while (true) {

                String a = ProjectSettingsOJ.selectedPanelKey;
                ImagePlus imp = WindowManager.getCurrentImage();
                if (imp != null) {
                    if (a.equals(ProjectSettingsOJ.PLOTS_PANEL)) {
                        String title = imp.getTitle();
                        boolean same = title.equals(oldTitle);
                        if (!same) {
                            IJ.showStatus("Front Image has changed");

                            selectPlotTitle(title);
                        }
                        oldTitle = title;
                    }
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    IJ.showMessage("Thread interrupted");
                }
            }
        }
    }
}
