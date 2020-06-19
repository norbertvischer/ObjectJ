/*
 * StatisticsMinimumOJ.java
 */
package oj.project.results.statistics;

import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnOJ;

public class StatisticsMinimumOJ extends StatisticsAdapterOJ {

    private static final long serialVersionUID = -4093526053167838018L;

    public StatisticsMinimumOJ() {
        this.name = "Minimum";
    }

    public double recalculate(String columnName) {
        ColumnOJ column = ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName);
        if ((column == null) || column.getColumnDef().isTextMode()) {
            return Double.NaN;
        }
        double value = Double.NaN;
        for (int i = 0; i < column.getResultCount(); i++) {
            if ((column.getColumnDef().isUnlinked())) {
                if (!Double.isNaN(column.getDoubleResult(i))) {
                    if (Double.isNaN(value)) {
                        value = column.getDoubleResult(i);
                    } else {
                        value = Math.min(value, column.getDoubleResult(i));
                    }
                }
            } else {
                CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                if ((cell.isQualified()) && (!Double.isNaN(column.getDoubleResult(i)))) {
                    if (Double.isNaN(value)) {
                        value = column.getDoubleResult(i);
                    } else {
                        value = Math.min(value, column.getDoubleResult(i));
                    }
                }
            }
        }

        return value;
    }
}
