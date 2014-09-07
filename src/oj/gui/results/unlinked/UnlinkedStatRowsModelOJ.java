/*
 * UnlinkedHeaderTableModelOJ.java
 * fully documented
 *
 * Handles the non-scrolling 0..7 rows of unlinked headertable that contains statistic values
 */
package oj.gui.results.unlinked;

import javax.swing.table.AbstractTableModel;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;

/**
 * The header table contains 0..7 rows, depending on visible statistics.
 */
public class UnlinkedStatRowsModelOJ extends AbstractTableModel {

    public static int EMPTY_COLUMNS_COUNT = 8;
    public static int EMPTY_ROWS_COUNT = 50;

    public UnlinkedStatRowsModelOJ() {
    }

    public int getRowCount() {
        int count = 0;
        for (int i = 0; i < OJ.getData().getResults().getStatistics().getStatisticsCount(); i++) {
            if (OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getVisible()) {
                count = count + 1;
            }
        }
        return count;
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
    @Override
    public String getColumnName(int visColumn) {
        if (visColumn == 0) {
            return "";
        } else {
            if ((visColumn - 1) < getVisibleSize()) {
                return getVisibleElementAt(visColumn - 1).getName();
            } else {
                return "   ";
            }
        }
    }

    /**
     * @return one of the statistic values as String object, or blanks if out of visible range
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            int row = 0;
            for (int i = 0; i < OJ.getData().getResults().getStatistics().getStatisticsCount(); i++) {
                if (OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getVisible()) {
                    if (rowIndex == row) {
                        return OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getName();
                    }
                    row += 1;
                }
            }
        } else {
            if (columnIndex <= getVisibleSize()) {
                int row = 0;
                ColumnOJ column = getVisibleElementAt(columnIndex - 1);
                for (int i = 0; i < OJ.getData().getResults().getStatistics().getStatisticsCount(); i++) {
                    if (OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getVisible()) {//loop through the visible of all 7 statistics are checked
                        if (rowIndex == row) {
                            String statOperation = OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getName();
                            int validResults = column.getValidResults();
                            int digits = column.getColumnDef().getColumnDigits();
                            double value = column.getStatistics().getStatisticsValueByName(statOperation);

                             if ("StDev".equals(statOperation) & (digits < 2)) {
                                return UtilsOJ.doubleToString(value, 2);//at least 2
                            }if ("Count".equals(statOperation)) {
                                return UtilsOJ.doubleToString(value, 0);//always integer
                            } else if (("Sum".equals(statOperation)) || ("SumSquare".equals(statOperation))) {
                                return UtilsOJ.doubleToString(value, digits);//paint "0" if there are no valid results
                            } else {
                                if ((validResults == 0) || (Double.compare(value, Double.NaN) == 0)) {
                                    return "    ";
                                } else {
                                    return UtilsOJ.doubleToString(value, digits);
                                }
                            }

                        }
                        row += 1;
                    }
                }
            } else {
                return "    ";
            }
        }
        return "    ";
    }

    /** returns the number of unlinked columns that are not hidden by the column list's user checkboxes
     */
    public int getVisibleSize() {
        int count = 0;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) columns.getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

   /**
     * Returns the n-th non-hidden unlinked column (0-based)
     *
     * @param index
     * @return n-th visible ColumnOJ (0-based)
     */
    public ColumnOJ getVisibleElementAt(int index) {
        int count = -1;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
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
}
