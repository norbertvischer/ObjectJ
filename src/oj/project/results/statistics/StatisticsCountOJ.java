/*
 * StatisticsCountOJ.java
 */
package oj.project.results.statistics;

import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnOJ;

public class StatisticsCountOJ extends StatisticsAdapterOJ {

    private static final long serialVersionUID = -5694212139054710958L;

    public StatisticsCountOJ() {
        this.name = "Count";
    }

    public double recalculate(String columnName) {
        ColumnOJ column = ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName);
        if (column == null) {
            return Double.NaN;
        }
        double value = 0;
        if (column.getColumnDef().isTextMode()) {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (column.getColumnDef().isUnlinked()) {
                    value = value + 1;
                } else {
                    CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                    if (cell.isQualified() && (column.getStringResult(i) != null)) {
                        value = value + 1;
                    }
                }
            }
        } else {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (column.getColumnDef().isUnlinked()) {

                    if (!Double.isNaN(column.getDoubleResult(i)))
                    {
                        value = value + 1;
                    }
                } else {
                    CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                    if ((cell.isQualified()) && (!Double.isNaN(column.getDoubleResult(i)))) {
                        value = value + 1;
                    }
                }
            }
        }
        return value;
    }
}
