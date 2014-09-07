/**
 * ResultsActionsOJ.java
 * fully documented 18.5.2010
 *
 * supplies listener methods that are
 * connected to menu items in the ObjectJ Results sub-menu
 */
package oj.gui.menuactions;

import ij.text.TextPanel;
import ij.text.TextWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.gui.results.ProjectResultsOJ;
import java.awt.Frame;
import oj.gui.ExportDialogOJ;
import oj.project.CellsOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;
import oj.util.WildcardMatchOJ;

public class ResultsActionsOJ {

    public static TextPanel myTextPanel = null;
    public static TextWindow myTextWindow = null;
    public static ActionListener RecalculateAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {


            ProjectResultsOJ.close();//23.2.2009 completely close results
            OJ.getImageProcessor().updateImagesProperties();
            OJ.getDataProcessor().recalculateResults();
            new ProjectResultsOJ();
            ProjectResultsOJ.getInstance().setVisible(true);//23.2.2009 re-open results
            ProjectResultsOJ.getInstance().setState(Frame.NORMAL);//23.2.2009
        }
    };

    public static void exportResultsToText() {
        closeTextPreview();
        int ww = 500;
        int hh = 700;
        myTextWindow = new TextWindow("Preview Text Output", "\t \t ", "  ", ww, hh);
        myTextWindow.setBounds(330, 0, ww, hh);
        if (myTextPanel == null) {
            myTextPanel = myTextWindow.getTextPanel();
        }

        ExportDialogOJ exportDlg = ExportDialogOJ.getInstance();
        exportDlg.initExtraComponents();
        exportDlg.setVisible(true);
    }

    public static void closeTextPreview() {
        if (myTextWindow != null) {
            myTextWindow.close();
        }
        myTextPanel = null;
        myTextWindow = null;


    }

    public static String[] extractResults(String s) {

        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        CellsOJ cells = OJ.getData().getCells();
        int nCells = cells.getCellsCount();

        String[] linkedNames = columns.columnLinkedNamesToArray();
        boolean qualifiedOnly = true;
        boolean indices = false;
        boolean headers = false;

        int nLinked = linkedNames.length;
        boolean[] colFlags = new boolean[nLinked];
        boolean[] statFlags = new boolean[nLinked];
        s = s.replaceAll(" ", "");
        String[] parts = s.split(",");

        //set invalid parts to ""
        for (int jj = 0; jj < parts.length; jj++) {
            String part = parts[jj].toLowerCase();
            boolean ok = false;
            if (part.startsWith("title=")) {
                ok = true;
            }
            String words[] = "count-mean-stdev-min-max-sum-cv-all-indices-headers".split("-");
            for (int kk = 0; kk < words.length; kk++) {
                if (words[kk].equals(part)) {
                    ok = true;
                }
            }
            if (!ok) {
                parts[jj] = "";
            }
        }

        for (String sub : parts) {
            sub = sub.toLowerCase();
            if (sub.equals("all")) {
                for (int jj = 0; jj < nLinked; jj++) {
                    colFlags[jj] = true;
                }

            }
            if (sub.startsWith("title=")) {

                String pattern = sub.substring("title=".length());
                for (int jj = 0; jj < nLinked; jj++) {
                    if (wm.match(linkedNames[jj], pattern)) {
                        colFlags[jj] = true;
                    }
                }
            }

            if (sub.equals("qualifiedonly")) {
                qualifiedOnly = true;
            }
            if (sub.equals("indices")) {
                indices = true;
            }
            if (sub.equals("headers")) {
                headers = true;
            }
        }
        int nExportedColumns = 0;
        for (int jj = 0; jj < nLinked; jj++) {
            if (colFlags[jj]) {
                nExportedColumns++;
            }
        }
        int nTableColumns = nExportedColumns;
        if (indices) {
            nTableColumns++;
        }
        //now let's start to create the output
        //if statistics are included, also indexes are included.
        //three loops through the columns:
        //  a) titles
        //  b) statistics
        //  c) values

        StringBuilder totalText = new StringBuilder();
        StringBuilder thisLine = new StringBuilder();
        int sCount = OJ.getData().getResults().getStatistics().getStatisticsCount();


        //a) column titles
        if (headers) {
            for (int col = -1; col < linkedNames.length; col++) {
                if (col == -1) {
                    if (indices) {
                        thisLine.append("n");
                    }

                } else {
                    if (colFlags[col]) {
                        if (thisLine.length() > 0) {
                            thisLine.append("\t");
                        }
                        thisLine.append(linkedNames[col]);
                    }
                }
            }
            thisLine.append("\n");
            totalText.append(thisLine.toString());
            thisLine.setLength(0);

        }//titles are done

        //b) statistics
        boolean needSeparator = false;
        for (int partNo = 0; partNo < parts.length; partNo++) {
            String thisPart = parts[partNo].toLowerCase();
            if ("count-stdev-mean-min-max-cv-sum".contains(thisPart)) {//25.11.2010
                needSeparator = true;
                thisLine.append(thisPart);
                for (int col = 0; col < linkedNames.length; col++) {
                    if (colFlags[col]) {
                        String title = linkedNames[col];
                        if (thisPart.equals("min")) {
                            thisPart = "minimum";
                        }
                        if (thisPart.equals("max")) {
                            thisPart = "maximum";
                        }
                        double val = columns.getColumnByName(title).getStatistics().getStatisticsValueByName(thisPart);
                        int digits = columns.getColumnByName(title).getColumnDef().getColumnDigits() + 2;
                        if (thisPart.equalsIgnoreCase("count")) {
                            digits = 0;
                        }

                        if (thisLine.length() > 0) {
                            thisLine.append("\t");

                        }
                        thisLine.append(UtilsOJ.doubleToString(val, digits));
                    }
                }
                totalText.append(thisLine.toString());
                totalText.append("\n");
                thisLine.setLength(0);
            }
        }
        if (needSeparator) {
            for (int jj = 0; jj < nTableColumns; jj++) {
                if (jj > 0) {
                    totalText.append("\t");
                }
                totalText.append("---");
            }
            totalText.append("\n");
        }

        // statistics are done

        for (int obj = 0; obj < nCells; obj++) {
            String tab = "";
            if (!qualifiedOnly || cells.getCellByIndex(obj).isQualified()) {
                thisLine.setLength(0);
                if (indices) {
                    String appendix = "" + (obj + 1);
                    thisLine.append(appendix);
                    tab = "\t";
                }
                for (int col = 0; col < linkedNames.length; col++) {
                    if (colFlags[col]) {
                        String title = linkedNames[col];
                        ColumnOJ column = columns.getColumnByName(title);
                        String result;
                        if (column.getColumnDef().isTextMode()) {
                            result = column.getStringResult(obj);
                        } else {
                            double val = column.getDoubleResult(obj);
                            int digits = column.getColumnDef().getColumnDigits();
                            if (digits != 0) {
                                digits += 2;
                            }
                            result = UtilsOJ.doubleToString(val, digits);
                        }
                        thisLine.append(tab);
                        thisLine.append(result);
                        tab = "\t";
                    }
                }
                thisLine.append("\n");
                totalText.append(thisLine);
                thisLine.setLength(0);
            }
        }

        String neutralHeader = " ";
        for (int jj = 1; jj < nTableColumns; jj++) {
            neutralHeader += "\t ";
        }

        String[] labelPair = new String[2];
        labelPair[0] = "Selected Columns: " + nExportedColumns + " of " + columns.getLinkedColumnsCount();
        labelPair[1] = "Qualified Objects: " + cells.getQualifiedCellsCount() + " of " + cells.getCellsCount();
        myTextPanel.clear();
        myTextPanel.setColumnHeadings(neutralHeader);
        myTextPanel.append(totalText.toString());
        myTextPanel.scrollToTop();
        //myTextPanel.setSelection(5, Integer.MAX_VALUE);
        myTextPanel.setVisible(true);
        return labelPair;
    }
}
