/* PlotOJ.java
 Fully documented
 *
 */
package oj.graphics;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Plot;
import oj.project.results.ColumnOJ;

/**
 *
 * Plot functions from ObjectJ columns
 */
public class PlotOJ {

    public void makeHistoFromColumn2(ColumnOJ column) {
	String plotName = "Histogram of " + column.getName();
	while (WindowManager.getImage(plotName) != null){
	    WindowManager.getImage(plotName).close();
	}

	Plot plot = new Plot(plotName, column.getName(), "Freq.");
	String color = "blue";
	GenericDialog gd = new GenericDialog(plotName, IJ.getInstance());
	gd.addCheckbox("All objects (include unqualified)", true);
	gd.addStringField("Color", color);
	gd.addNumericField("Bin width", Double.NaN, 2, 6, "(0 = automatic)");
	gd.showDialog();
	boolean allObjects = gd.getNextBoolean();
	color = gd.getNextString();
	double binWidth = gd.getNextNumber();
	if (!(binWidth >= 0)) {
	    binWidth = 0;
	}
	plot.setColor(color);

	double[] values = column.getDoubleArray(allObjects, false);
	plot.addHistogram(values, binWidth, 0);
	plot.show();
    }
    /**
     * Get string representation of statistics with friendly precision
     *
     */
    private String statAsString(ColumnOJ col, String statName) {
	double value = col.getStatistics().getStatisticsValueByName(statName);
	int digits = 2;
	if (statName.equals("Count")) {
	    digits = 0;
	} else if (Math.abs(value) < 3.0) {
	    digits = 3;
	    if (Math.abs(value) < 0.3) {
		digits = 4;
	    }
	}
	String st = ij.IJ.d2s(value, digits);
	return st;
    }


}
