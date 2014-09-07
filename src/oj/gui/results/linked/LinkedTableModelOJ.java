/*
 * LinkedTableModelOJ.java
 * fully documented
 */

package oj.gui.results.linked;

import java.awt.Color;
import javax.swing.table.AbstractTableModel;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;

/**
 * For getting, setting and sorting linked ObjectJ result values.
 * At any time, the table width corresponds to visible columns only.
 * Column 0 contains the cell indexes and is always visible
 */
public class LinkedTableModelOJ extends AbstractTableModel {

    public static int EMPTY_COLUMNS_COUNT =8;
    public static int EMPTY_ROWS_COUNT = 50;

    private int[] resultsIndexes;

    public LinkedTableModelOJ() {
        super();
        resultsIndexes = OJ.getData().getResults().getSortedIndexes(false);
    }

    public void setSortedIndexes(int[] resultsIndexes) {
        this.resultsIndexes = new int[resultsIndexes.length];
        System.arraycopy(resultsIndexes, 0, this.resultsIndexes, 0, resultsIndexes.length);
    }

    /**
     * Returns a ResultsTableValueOJ object containing value (as string), color, qualifiedflag
     * i.e. display information of that table cell. However, only  the value-string is used.
     */
    public Object getValueAt(int iRowIndex, int iColumnIndex) {
        if (iColumnIndex == 0) {// the index column
            if (iRowIndex < OJ.getData().getResults().getResultsCount()) {
                LinkedTableValueOJ value = new LinkedTableValueOJ(iRowIndex);
                if ((iRowIndex >= resultsIndexes.length) || (resultsIndexes[iRowIndex] >= OJ.getData().getCells().getCellsCount())) {
                    return value;
                }
                value.qualified = OJ.getData().getCells().getCellByIndex(resultsIndexes[iRowIndex]).isQualified();
                value.content = Integer.toString(resultsIndexes[iRowIndex] + 1);
                return value;
            } else {
                return new LinkedTableValueOJ(iRowIndex);
            }
        } else {
            if ((iColumnIndex - 1) < getVisibleSize()) {
                ColumnOJ column = getVisibleElementAt(iColumnIndex - 1);
                if (column.getColumnDef().isTextMode()) {
                    if (iRowIndex < OJ.getData().getResults().getResultsCount()) {
                        LinkedTableValueOJ value = new LinkedTableValueOJ(iRowIndex);
                        if ((iRowIndex >= resultsIndexes.length) || (resultsIndexes[iRowIndex] >= OJ.getData().getCells().getCellsCount())) {
                            return value;
                        }
                        value.qualified = OJ.getData().getCells().getCellByIndex(resultsIndexes[iRowIndex]).isQualified();
                        value.content = column.getStringResult(resultsIndexes[iRowIndex]);
                        value.color = column.getColumnDef().getColumnColor();
                        return value;
                    } else {
                        return new LinkedTableValueOJ(iRowIndex);
                    }
                } else {
                    if (iRowIndex < OJ.getData().getResults().getResultsCount()) {
                        LinkedTableValueOJ value = new LinkedTableValueOJ(iRowIndex);
                        if ((iRowIndex >= resultsIndexes.length) || (resultsIndexes[iRowIndex] >= OJ.getData().getCells().getCellsCount())) {
                            return value;
                        }
                        value.qualified = OJ.getData().getCells().getCellByIndex(resultsIndexes[iRowIndex]).isQualified();
                        if (Double.compare(column.getDoubleResult(resultsIndexes[iRowIndex]), Double.NaN) != 0) {
                            value.content = UtilsOJ.doubleToString(column.getDoubleResult(resultsIndexes[iRowIndex]), column.getColumnDef().getColumnDigits());
                        } else {
                            value.content = null;
                        }
                        if ((value.content != null) && value.content.equals("NaN")) {
                            value.content = null;
                        }
                        value.color = column.getColumnDef().getColumnColor();
                        return value;
                    } else {
                        return new LinkedTableValueOJ(iRowIndex);
                    }
                }
            } else {
                return new LinkedTableValueOJ(iRowIndex);
            }
        }
    }

    /** All data is manufactured - nothing to do here
     */
    public void setValueAt(Object aValue, int iRowIndex, int iColumnIndex) {
    }

    /**
     * At any time, 8 empty columns are appended to the visible ones
     */
    public int getColumnCount() {
        return getVisibleSize() + EMPTY_COLUMNS_COUNT;
    }

    /**
     * Returns table's column name (or blanks if out of range)
     */
    public String getColumnName(int visColumn) {
        if (visColumn == 0) {
            return "n";
        } else {
            if ((visColumn - 1) < getVisibleSize()) {
                return getVisibleElementAt(visColumn - 1).getName();
            } else {
                return "   ";
            }
        }
    }

    /**
     * At any time, 50 empty rows are appended to the visible ones
     */
    public int getRowCount() {
        return OJ.getData().getCells().getCellsCount() + EMPTY_ROWS_COUNT;
    }

    /** returns the number of linked columns that are not hidden by user checkbox
     */
    public int getVisibleSize() {
        int count = 0;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (!((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) columns.getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * Returns the n-th non-hidden linked column (0-based)
     *
     * @param index
     * @return
     */
    public ColumnOJ getVisibleElementAt(int index) {
        int count = -1;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (!((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) columns.getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                    if (count == index) {
                        return columns.getColumnByIndex(i);
                    }
                }
            }
        }
        return null;
    }

    /** Returns row index of a cell, (same number if not sorted, 0-based)
     */
    public int getCellRowIndex(int cellIndex) {
        for (int i = 0; i < resultsIndexes.length; i++) {
            if (resultsIndexes[i] == cellIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * class for defining a single linked result table entry
     */
    public static class LinkedTableValueOJ {

        public int index;
        public String content = "";
        public boolean qualified = true;
        public Color color = Color.BLACK;

        public LinkedTableValueOJ(int index) {
            this(index, true, "");
        }

        public LinkedTableValueOJ(int index, boolean qualified) {
            this(index, qualified, "");
        }

        public LinkedTableValueOJ(int index, boolean qualified, String value) {
            this.index = index;
            this.content = value;
            this.qualified = qualified;
        }
    }
}