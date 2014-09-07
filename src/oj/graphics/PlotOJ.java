/* PlotOJ.java
 Fully documented
 *
 */
package oj.graphics;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JTextArea;
import oj.OJ;
import oj.project.results.ColumnDefOJ;
import oj.project.results.ColumnOJ;
import oj.gui.settings.ColumnSettingsOJ;
import oj.gui.settings.ProjectSettingsOJ;
import oj.project.results.ColumnsOJ;

/**
 *
 * Plot functions from ObjectJ columns
 */
public class PlotOJ {

    //Properties props = null;
    /**
     * Plots a city-shaped histogram from a ObjectJ results column using
     * modified ImageJ plot window Only qualified entries are used.
     */
    public void makeHistoFromColumn(ColumnOJ column) {

        if (column == null) {
            return;
        }
        Plot plot = null;
        final String theName = column.getName();//for action
        ColumnDefOJ coldef = column.getColumnDef();
        if (coldef.isTextMode()) {
            ij.IJ.showMessage("Cannot create Histogram from text column");
            return;
        }

        double[] allValues = column.getDoubleArray(true, false);//includeUnqualified = true, includeNaNs = false
        double[] qualifiedValues = column.getDoubleArray(false, false);//includeUnqualified = false, includeNaNs = false
        double[] values = allValues;

        //get statistics of all
        int len = allValues.length;
        int cnt = 0;
        double sum = 0.0, sum2 = 0.0, value;
        double allXMin = Double.POSITIVE_INFINITY;
        double allXMax = Double.NEGATIVE_INFINITY;
        double allStdDev = Double.NaN;
        //double allMean = Double.NaN;
        for (int jj = 0; jj < len; jj++) {
            value = allValues[jj];
            if (!Double.isNaN(value)) {
                cnt++;
                sum += value;
                sum2 += value * value;
                if (value < allXMin) {
                    allXMin = value;
                }
                if (value > allXMax) {
                    allXMax = value;
                }
            }
        }
        if (cnt == 0) {
            ij.IJ.showMessage("No Histogram because count = 0");
            return;
        }
        //allMean = sum / cnt;
        if (cnt > 1) {
            allStdDev = (cnt * sum2 - sum * sum) / cnt;
            allStdDev = Math.sqrt(allStdDev / (cnt - 1.0));
        }

        double xLeft = 0, xRight = 0, binWidth = 0, range = 0;
        int yMax = 1, nBins = 0, arrSize = 0;
        //double qCnt = column.getStatistics().getStatisticsValueByName("Count");
        //double qStDev = column.getStatistics().getStatisticsValueByName("StDev");
        //double qMean = column.getStatistics().getStatisticsValueByName("Mean");

        //Now let us calculate
        //Text to appear below the plot
        //String[] namesArr = names.split(",");
        String[] statArr = new String[4];
        statArr[0] = "Name:\t";
        statArr[1] = "Count:\t";
        statArr[2] = "Mean:\t";
        statArr[3] = "StDev:\t";

//get adjustments from columns; if NaN use "automatic"
        xLeft = coldef.getHistoXMin();
        xRight = coldef.getHistoXMax();
        if (xRight == 0) {
            xRight = Double.NaN;
            coldef.setHistoXMax(xRight);
        }
        binWidth = coldef.getHistoBinWidth();
        yMax = coldef.getHistoYMax();

        if (Double.isNaN(xLeft)) {
            xLeft = allXMin;
        }
        if (Double.isNaN(xRight)) {
            xRight = allXMax;
        }
        range = xRight - xLeft;
        if (!(range > 0)) {
            ij.IJ.showMessage("No histogram is possible");
            return;
        }

        if (!(binWidth > 0.0)) {
            if (cnt > 1) {
                binWidth = (3.5 * allStdDev / Math.pow(cnt, 1.0 / 3));//Scott's choice
            }
            if (cnt == 1) {
                binWidth = 1;//9.2.2010 to be checked
            }
        }

        nBins = (int) (Math.floor((range) / binWidth)) + 1;
        arrSize = 2 * nBins + 2;
        if (nBins
                > 1000) {
            binWidth = Double.NaN;
            coldef.setHistoBinWidth(binWidth);
            ij.IJ.showMessage("Histo error:  nBins > 1000");
            changeHistoActionPerformed(theName);
            return;
        }
        if (!(xLeft < xRight && nBins > 0 && nBins <= 1000)) {
            ij.IJ.showMessage("Histo error: xMin=" + xLeft + " xMax=" + xRight + " nBins=" + nBins);
            return;
        }
        //{
        statArr[0] += theName + "\t";
        statArr[1] += statAsString(column, "Count") + "\t";
        statArr[2] += statAsString(column, "Mean") + "\t";
        statArr[3] += statAsString(column, "StDev") + "\t";

        Color color = coldef.getColumnColor();

        int flags = Plot.DEFAULT_FLAGS + Plot.X_FORCE2GRID;
        String plotName = "Histogram of " + column.getName();
        ImagePlus imp;
        while(WindowManager.getImage(plotName) != null){//2.2.2014
            WindowManager.getImage(plotName).close();
        }
        plot = new Plot(plotName, column.getName(), "count", ((double[]) null), ((double[]) null), flags);
        double[] xxAll = null;
        double[] yyAll = null;
        double[] xxQual = null;
        double[] yyQual = null;
        for (int pass = 1; pass <= 2; pass++) {//first pass is "all", which defines limits;
            if (pass == 2) {
                values = qualifiedValues;
            }
            len = values.length;

//Calculate  histogram of this column
            double[] xx = new double[arrSize];
            double[] yy = new double[arrSize];

            for (int jj = 0; jj < nBins; jj++) {
                xx[jj] = 0;
            }
            int bin;
            for (int jj = 0;
                    jj < len;
                    jj++) {
                if (values[jj] == xRight) {
                    bin = nBins - 1;
                } else {
                    double realBin = (values[jj] - xLeft) / range * nBins;
                    bin = (int) Math.round(Math.floor(realBin));
                }
                if (bin >= 0 && (2 * bin + 2) < arrSize) {
                    yy[2 * bin + 1]++;
                    yy[2 * bin + 2]++;
                }
            }
            for (bin = 0; bin <= nBins; bin++) {
                xx[2 * bin] = xLeft + binWidth * bin;
                xx[2 * bin + 1] = xLeft + binWidth * bin;
            }
            if (yMax <= 0) {
                yMax = 1;
                for (int nn = 0; nn < yy.length; nn++) {
                    if (yy[nn] > yMax) {
                        yMax = (int) yy[nn] + 1;
                    }
                }
                yMax *= 1.05;
            }
            plot.setLimits(xLeft, xRight, 0, yMax);

            if (pass == 1) {
                xxAll = xx.clone();
                yyAll = yy.clone();

            }
            if (pass == 2) {
                xxQual = xx.clone();
                yyQual = yy.clone();
            }
        }
        plot.setColor(Color.GRAY);
        plot.addPoints(xxAll, yyAll, Plot.LINE);
        plot.setColor(color);
        plot.addPoints(xxQual, yyQual, Plot.LINE);

        plot.draw();

        PlotWindow plotWindow = plot.show();
        Dimension dim = plotWindow.getSize();

        int id = plot.getImagePlus().getID();
        Point hook = null;
        ImagePlus oldImp = WindowManager.getImage(column.getHistoID());
        if (oldImp != null) {
            hook = oldImp.getWindow().getLocationOnScreen();
            plotWindow.setLocation(hook);
            oldImp.close();
        }

        plotWindow.setSize(dim.width, dim.height + 100);
        String statText = "";
        for (int jj = 0;
                jj < statArr.length;
                jj++) {
            statText += statArr[jj] + "\n";
        }
        JTextArea txtArea = new JTextArea(statText, 5, 40);

        txtArea.setSize(dim.width, 100);
        txtArea.setEditable(false);
        plotWindow.add(txtArea);

        plotWindow.setComponentZOrder(txtArea, 1);
        int count = plotWindow.getComponentCount();
        for (int jj = 0;
                jj < count;
                jj++) {
            Component comp = plotWindow.getComponent(jj);

            if (comp instanceof Panel) {
                Panel panel = (Panel) comp;
                int btnCount = panel.getComponentCount();
                Button saveButton = null;
                for (int kk = 0; kk < btnCount; kk++) {
                    Component cmp = panel.getComponent(kk);
                    if (cmp instanceof Button) {
                        Button btn = (Button) cmp;
                        if (btn.getLabel().startsWith("Save")) {
                            saveButton = btn;
                        }
                        if (kk == 2) {
                            btn.setLabel("Modify");
                            ActionListener[] al = btn.getActionListeners();
                            for (int ll = 0; ll < al.length; ll++) {
                                btn.removeActionListener(al[ll]);
                            }
                            btn.addActionListener(new java.awt.event.ActionListener() {

//                                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                                    changeHistoActionPerformed();
//                                }
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    changeHistoActionPerformed(theName);
                                }
                            });
                        }
                    }

                }
                if (saveButton != null) {
                    saveButton.setVisible(false);//20.2.2010
                    panel.remove(saveButton);//18.2.2010
                }
            }

            Component bb = comp;
            comp = bb;
            comp.repaint();
            txtArea.setOpaque(true);
            txtArea.repaint(100);
        }
    }

//    public void makePlotFromColumns(String[] colNames, int nPlots) {
//        ColumnsOJ columns = OJ.getData().getResults().getColumns();
//        props = new Properties();
//        Plot plot = null;
//        double[] xValues = null;
//        double[] yValues = null;
//        double minVal = 0, maxVal = 10, xMinVal = 0, xMaxVal = 10;
//
//        if (colNames == null) {
//            return;
//        }
//        String xName = "";
//        for (int colNum = 0; colNum <= nPlots; colNum++) {
//            
//            ColumnOJ column = columns.getColumnByName(colNames[colNum]);
//                    
//            ColumnDefOJ coldef = column.getColumnDef();
//            String colProperties = coldef.getPlotProperties();
//            setProperties(colProperties);
//            String color = props.getProperty("color", "red");
//            String sMinVal = props.getProperty("min", "0");
//            String sMaxVal = props.getProperty("max", "auto");
//
//
//            minVal = myParse(sMinVal, 0);
//            maxVal = myParse(sMaxVal, Double.NaN);
//
//            if (Double.isNaN(maxVal)) {
//                minVal = column.getStatistics().getStatisticsValueByName("Minimum");
//            }
//            if (Double.isNaN(maxVal)) {
//                maxVal = column.getStatistics().getStatisticsValueByName("Maximum");
//            }
//            if (colNum == 0) {
//                xValues = column.getDoubleResults(true);
//                xName = column.getName();
//
//                xMinVal = minVal;
//                xMaxVal = maxVal;
//            } else {
//                double yMinVal = minVal;
//                double yMaxVal = maxVal;
//                yValues = column.getDoubleResults(true);
//                String yName = column.getName();
//                if (colNum == 1) {
//                    plot = new Plot("cde", xName, yName, (double[]) null, (double[]) null);
//                    plot.setLimits(xMinVal, xMaxVal, yMinVal, yMaxVal);
//                }
//
//                if (color.equalsIgnoreCase("red")) {
//                    plot.setColor(Color.red);
//                }
//                if (color.equalsIgnoreCase("green")) {
//                    plot.setColor(Color.green);
//                }
//                if (color.equalsIgnoreCase("blue")) {
//                    plot.setColor(Color.blue);
//                }
//                if (color.equalsIgnoreCase("black")) {
//                    plot.setColor(Color.black);
//                }
//                if (color.equalsIgnoreCase("magenta")) {
//                    plot.setColor(Color.magenta);
//                }
//                plot.addPoints(xValues, yValues, Plot.CIRCLE);
//                //plot.addPoints(xValues, yValues, Plot.LINE);
//
//            }
//        }
//        plot.draw();
//
//
//        PlotWindow plotWindow = plot.show();
//        // Dimension dim = plotWindow.getSize();
//        //plotWindow.setSize(dim.width, dim.height + 100);
//
//
//    }
    /**
     * Get string representation of statistics with friendly precision
     *
     */
    private String statAsString(ColumnOJ col, String statName) {
        double value = col.getStatistics().getStatisticsValueByName(statName);
        int digits = 2;
        if (statName.equals("Count")) {
            digits = 0;
        } else {
            if (Math.abs(value) < 3.0) {
                digits = 3;
                if (Math.abs(value) < 0.3) {
                    digits = 4;
                }
            }
        }
        String st = ij.IJ.d2s(value, digits);
        return st;
    }

    /**
     * When clicking "Modify", column settings must expose correct column
     * settings
     */
    private void changeHistoActionPerformed(String theName) {
        if (!oj.OJ.isProjectOpen) {
            return;
        }
        ProjectSettingsOJ prSettings = ProjectSettingsOJ.getInstance();
        if (prSettings != null) {
            prSettings.setVisible(true);
            prSettings.selectColumnsPanel();
            ColumnSettingsOJ colSettings = prSettings.getColumnsPanel();
            colSettings.selectPresentationTab();
            if (null != oj.OJ.getData().getResults().getColumns().getColumnByName(theName)) {
                colSettings.selectColumn(theName);
            } else {
                ij.IJ.error("Lost connection to coumn");
            }
            prSettings.setVisible(true);
        }
    }

//    void setProperties(String list) {
//        props.clear();
//        try {
//            InputStream is = new ByteArrayInputStream(list.getBytes("utf-8"));
//            props.load(is);
//        } catch (Exception e) {
//        }
//    }
//
//    String getProperties() {
//        String list = props.toString();
//        list = list.substring(1, list.length() - 1);
//        list = list.replaceAll(", ", "\n");
//        return list;
//    }
//    double myParse(String s, double defaultVal) {
//        try {
//            return Double.parseDouble(s);
//        } catch (NumberFormatException e) {
//            return defaultVal;
//        }
//    }
}
