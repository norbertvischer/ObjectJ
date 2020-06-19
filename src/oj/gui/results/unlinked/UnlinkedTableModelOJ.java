/*
 * UnlinkedTableModelOJ.java
 * fully documented
 */

package oj.gui.results.unlinked;

import java.awt.Color;
import javax.swing.table.AbstractTableModel;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.project.results.ColumnOJ;


/**
 * For getting, setting and sorting ObjectJ unlinked result values.
 * At any time, the table width corresponds to visible columns only.
 * Column 0 contains the cell indexes and is always visible
 */
public class UnlinkedTableModelOJ extends AbstractTableModel {

    private int[] resultsIndexes;

    public UnlinkedTableModelOJ() {
        resultsIndexes = OJ.getData().getResults().getSortedIndexes(true);
    }

    public void setSortedIndexes(int[] resultsIndexes) {
        this.resultsIndexes = new int[resultsIndexes.length];
        System.arraycopy(resultsIndexes, 0, this.resultsIndexes, 0, resultsIndexes.length);
    }

    /**
     * Returns a ResultsTableValueOJ object containing value (as string), color
     * i.e. display information of that table cell. However, only  the value-string is used.
     */
    public Object getValueAt(int iRowIndex, int iColumnIndex) {
        if (iColumnIndex == 0) {
            if (iRowIndex < OJ.getData().getResults().getUnlinkedResultsCount()) {
                UnlinkedTableValueOJ value = new UnlinkedTableValueOJ(iRowIndex);
                value.content = Integer.toString(resultsIndexes[iRowIndex] + 1);
                return value;
            } else {
                return new UnlinkedTableValueOJ(iRowIndex);
            }
        } else {
            if ((iColumnIndex - 1) < getVisibleSize()) {
                ColumnOJ column = getVisibleElementAt(iColumnIndex - 1);
                if (column.getColumnDef().isTextMode()) {
                    if (iRowIndex < OJ.getData().getResults().getUnlinkedResultsCount()) {
                        UnlinkedTableValueOJ value = new UnlinkedTableValueOJ(iRowIndex);
                        value.content = column.getStringResult(resultsIndexes[iRowIndex]);
                        value.color = column.getColumnDef().getColumnColor();
                        return value;
                    } else {
                        return new UnlinkedTableValueOJ(iRowIndex);
                    }
                } else {
                    if (iRowIndex < OJ.getData().getResults().getUnlinkedResultsCount()) {
                        UnlinkedTableValueOJ value = new UnlinkedTableValueOJ(iRowIndex);
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
                        return new UnlinkedTableValueOJ(iRowIndex);
                    }
                }
            } else {
                return new UnlinkedTableValueOJ(iRowIndex);
            }
        }
    }

    public void setValueAt(Object aValue, int iRowIndex, int iColumnIndex) {
        // All data is manufactured - nothing to do here
    }

    public int getColumnCount() {
        return getVisibleSize() + 8;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "n";
        } else {
            if ((column - 1) < getVisibleSize()) {
                return getVisibleElementAt(column - 1).getName();
            } else {
                return "   ";
            }
        }
    }

    public int getRowCount() {
        return OJ.getData().getResults().getUnlinkedResultsCount() + 50;
    }

    public int getVisibleSize() {
        int count = 0;
        for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
            if (((ColumnOJ) OJ.getData().getResults().getColumns().getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) OJ.getData().getResults().getColumns().getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public ColumnOJ getVisibleElementAt(int index) {
        int count = -1;
        for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
            if (((ColumnOJ) OJ.getData().getResults().getColumns().getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) OJ.getData().getResults().getColumns().getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                    if (count == index) {
                        return OJ.getData().getResults().getColumns().getColumnByIndex(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * class for defining a single unlinked result table entry
     */
    public static class UnlinkedTableValueOJ {

        public int index;
        public String content = "";
        public Color color = Color.BLACK;

        public UnlinkedTableValueOJ(int index) {
            this(index, "");
        }

        public UnlinkedTableValueOJ(int index, String value) {
            this.index = index;
            this.content = value;
        }
    }
}