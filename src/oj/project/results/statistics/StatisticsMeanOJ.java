/*
 * StatisticsMeanOJ.java
 */
package oj.project.results.statistics;

import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnOJ;

public class StatisticsMeanOJ extends StatisticsAdapterOJ {

    private static final long serialVersionUID = -7851530017039697003L;

    public StatisticsMeanOJ() {
        this.name = "Mean";
    }

    public double recalculate(String columnName) {
        ColumnOJ column = ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName);
        if ((column == null) || column.getColumnDef().isTextMode()) {
            return Double.NaN;
        }
        int validResults = 0;
        double value = Double.NaN;
        double sumOfResult = 0.0;
        for (int i = 0; i < column.getResultCount(); i++) {
            if (column.getColumnDef().isUnlinked()) {
                if (!Double.isNaN(column.getDoubleResult(i))) {
                    validResults = validResults + 1;
                    sumOfResult = sumOfResult + column.getDoubleResult(i);
                }
            } else {
                CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                if ((cell.isQualified()) && (!Double.isNaN(column.getDoubleResult(i)))) {
                    validResults = validResults + 1;
                    sumOfResult = sumOfResult + column.getDoubleResult(i);
                }
            }
        }

        if (validResults > 0) {
            value = sumOfResult / validResults;
        }
        return value;
    }
}
