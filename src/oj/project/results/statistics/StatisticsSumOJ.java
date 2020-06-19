/*
 * StatisticsSumOJ.java
 */
package oj.project.results.statistics;

import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnOJ;

public class StatisticsSumOJ extends StatisticsAdapterOJ {

    private static final long serialVersionUID = -4828855466333050978L;

    public StatisticsSumOJ() {
        this.name = "Sum";
    }

    public double recalculate(String columnName) {
        ColumnOJ column = ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName);
        if (column == null) {
            return Double.NaN;
        }
        if (column.getColumnDef().isTextMode()) {
            return Double.NaN;
        }
        double value = 0.0;
        for (int i = 0; i < column.getResultCount(); i++) {
            if ((column.getColumnDef().isUnlinked())) {
                if (!Double.isNaN(column.getDoubleResult(i))) {
                    value = value + column.getDoubleResult(i);
                }
            } else {
                CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                if ((cell.isQualified()) && (!Double.isNaN(column.getDoubleResult(i)))) {
                    value = value + column.getDoubleResult(i);
                }
            }
        }

        return value;
    }
}
