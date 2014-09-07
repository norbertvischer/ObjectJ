/*
 * StatisticsCvOJ.java
 */
package oj.project.results.statistics;

import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnOJ;

public class StatisticsCvOJ extends StatisticsAdapterOJ {

    private static final long serialVersionUID = 417974041873846540L;

    public StatisticsCvOJ() {
        this.name = "Cv";
    }

    public double recalculate(String columnName) {
        ColumnOJ column = ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName);
        if ((column == null) || column.getColumnDef().isTextMode()) {
            return Double.NaN;
        }
        int validResults = 0;
        double stDev = Double.NaN;
        double cv = Double.NaN;
        double sumOfDeltaSquares = 0.0;
        double sum = 0.0, min = 1e99, max = -1e99;
        double delta, val, mean = 0.0;
        for (int loop = 1; loop <= 2; loop++) {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (column.getColumnDef().isUnlinked()) {
                    val = column.getDoubleResult(i);
                    if (!Double.isNaN(val)) {
                        if (loop == 1) {
                            validResults = validResults + 1;
                            sum = sum + val;
                            if (min > val) {
                                min = val;
                            }
                            if (max < val) {
                                max = val;
                            }
                        }
                        if (loop == 2) {
                            delta = mean - val;
                            sumOfDeltaSquares = sumOfDeltaSquares + delta * delta;
                        }
                    }
                } else {
                    CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
                    val = column.getDoubleResult(i);
                    if ((cell.isQualified()) && (!Double.isNaN(val))) {
                        if (loop == 1) {
                            validResults = validResults + 1;
                            sum = sum + val;
                            if (min > val) {
                                min = val;
                            }
                            if (max < val) {
                                max = val;
                            }
                        }
                        if (loop == 2) {
                            delta = mean - val;
                            sumOfDeltaSquares = sumOfDeltaSquares + delta * delta;
                        }
                    }
                }
            }
            if (loop == 1) {
                if (validResults < 2) {
                    return Double.NaN;
                }

                mean = sum / validResults;
            }
        }

        if (validResults > 1) {

            stDev = Math.sqrt(sumOfDeltaSquares / (validResults - 1));//n_9.6.2008

            if (!(min * max > 0)) {
                cv = Double.NaN;
            } else {
                cv = stDev / mean * 100;
            }
        }
        return cv;
    }
}
