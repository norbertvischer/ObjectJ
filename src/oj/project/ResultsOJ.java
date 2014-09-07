/*
 * ResultsOJ.java
 *
 * Contains methods to calculate and qualify results. Display of results is not handled here (see gui)
 *
 */
package oj.project;

import java.util.ArrayList;
import java.util.Comparator;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;
import oj.project.results.PlotsOJ;
import oj.project.results.QualifierOJ;
import oj.project.results.QualifiersOJ;
import oj.project.results.statistics.StatisticsOJ;

public class ResultsOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -7558098201187378826L;
    public StatisticsOJ statistics = new StatisticsOJ();
    public QualifiersOJ qualifiers = new QualifiersOJ();
    public ColumnsOJ columns = new ColumnsOJ();
    public PlotsOJ plots = new PlotsOJ();

    /**
     * Creates a new instance of ResultsOJ
     */
    public ResultsOJ() {
        init();
    }

    public void init() {
        statistics.setParent(this);
        qualifiers.setParent(this);
        columns.setParent(this);
        plots.setParent(this);
    }

    public PlotsOJ getPlots() {
        return plots;
    }

    public ColumnsOJ getColumns() {
        return columns;
    }

    public QualifiersOJ getQualifiers() {
        return qualifiers;
    }

    public StatisticsOJ getStatistics() {
        return statistics;
    }

    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            return columns.getChanged() || qualifiers.getChanged() || statistics.getChanged();
        }
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        columns.setChanged(changed);
        qualifiers.setChanged(changed);
        statistics.setChanged(changed);
        plots.setChanged(changed);
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (qualifiers == null) {
            qualifiers = new QualifiersOJ();
        }
        qualifiers.initAfterUnmarshalling(this);

        if (statistics == null) {
            statistics = new StatisticsOJ();
        }
        statistics.initAfterUnmarshalling(this);

        if (columns == null) {
            columns = new ColumnsOJ();
        }
        columns.initAfterUnmarshalling(this);

        if (plots == null) {
            plots = new PlotsOJ();
        }
        plots.initAfterUnmarshalling(this);
    }

    public int[] getSortedIndexes(boolean unlinkedResults) {
        int[] result = new int[0];
        String sort_name = null;
        int sort_flag = ColumnsOJ.COLUM_SORT_FLAG_NONE;
        if (unlinkedResults) {
            sort_name = columns.getColumnUnlinkedSortName();
            sort_flag = columns.getColumnUnlinkedSortFlag();
            result = new int[getUnlinkedResultsCount()];
        } else {
            sort_name = columns.getColumnLinkedSortName();
            sort_flag = columns.getColumnLinkedSortFlag();
            result = new int[((DataOJ) parent).getCells().getCellsCount()];
        }
        if (columns.getColumnByName(sort_name) == null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = i;
            }
        } else {
            switch (sort_flag) {
                case ColumnsOJ.COLUM_SORT_FLAG_ASCENDING:
                case ColumnsOJ.COLUM_SORT_FLAG_DESCENDING:
                    ColumnOJ column = columns.getColumnByName(sort_name);
                    ArrayList rind = new ArrayList();
                    if (column.getColumnDef().isTextMode()) {//Text column
                        ArrayList sind = new ArrayList();//arrange strings so that valid ones are at the top
                        for (int i = 0; i < result.length; i++) {
                            if (column.isValidResult(i)) {
                                sind.add(0, column.getStringResult(i));
                                rind.add(0, new Integer(i));
                            } else {
                                sind.add(new String());
                                rind.add(new Integer(i));
                            }
                        }
                        Object[] ind = rind.toArray(new Object[result.length]);
                        Object[] snd = sind.toArray(new Object[result.length]);
                        //now only sort valid strings:
                        sort(snd, ind, 0, column.getValidResults() - 1, sort_flag == ColumnsOJ.COLUM_SORT_FLAG_ASCENDING, new StringComparator());
                        for (int i = 0; i < result.length; i++) {
                            result[i] = ((Integer) ind[i]).intValue();
                        }
                    } else {//Numbers column
                        ArrayList sind = new ArrayList();//arrange numbers so that valid ones are at the top
                        for (int i = 0; i < result.length; i++) {
                            if (column.isValidResult(i)) {
                                sind.add(0, new Double(column.getDoubleResult(i)));
                                rind.add(0, new Integer(i));
                            } else {
                                sind.add(new Double(Double.NaN));
                                rind.add(new Integer(i));
                            }
                        }
                        Object[] ind = rind.toArray(new Object[result.length]);
                        Object[] snd = sind.toArray(new Object[result.length]);
                        //now only sort valid numbers:
                        sort(snd, ind, 0, column.getValidResults() - 1, sort_flag == ColumnsOJ.COLUM_SORT_FLAG_ASCENDING, new DoubleComparator());
                        for (int i = 0; i < result.length; i++) {
                            result[i] = ((Integer) ind[i]).intValue();
                        }
                    }
                    break;
                default:
                    for (int i = 0; i < result.length; i++) {
                        result[i] = i;
                    }
            }
        }
        return result;
    }

    /**
     * Get a cell's qualified flag from all conditions
     */
    public boolean getQualified(int index) {
        boolean qualified_cell = false;
        if (qualifiers.getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_ALL) {
            qualified_cell = true;
        } else if (qualifiers.getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_NONE) {
            qualified_cell = false;
        } else {
            qualified_cell = true;
            for (int i = 0; i < qualifiers.getQualifiersCount(); i++) {
                QualifierOJ qualifier = qualifiers.getQualifierByIndex(i);
                if (!columns.getColumnByName(qualifiers.getQualifierByIndex(i).getColumnName()).getColumnDef().isTextMode()) {
                    qualified_cell = getQualified(qualifier.getColumnName(), qualifier.getOperation(), qualifier.getFirstDoubleValue(), qualifier.getSecondDoubleValue(), index);
                } else {
                    qualified_cell = getQualified(qualifier.getColumnName(), qualifier.getOperation(), qualifier.getFirstStringValue(), qualifier.getSecondStringValue(), index);
                }
            }
        }
        return qualified_cell;

    }

    /**
     * Get a cell's qualified flag from one numerical condition
     */
    private boolean getQualified(String columnName, int operation, double minValue, double maxValue, int index) {
        boolean qualified = true;
        ColumnOJ coj = columns.getColumnByName(columnName);
        if ((operation == QualifierOJ.OPERATION_NOT_WITHIN) || (operation == QualifierOJ.OPERATION_WITHIN)) {
            if (!QualifierOJ.qualify(coj.getDoubleResult(index), minValue, maxValue, operation)) {
                qualified = false;
            }
            if (operation == QualifierOJ.OPERATION_NOT_WITHIN) {
                qualified = false;
            }
        } else if ((operation == QualifierOJ.OPERATION_EXISTS) || (operation == QualifierOJ.OPERATION_EMPTY)) {
            if (!QualifierOJ.qualify(coj.getDoubleResult(index), operation)) {
                qualified = false;
            }
        } else {
            if (!QualifierOJ.qualify(coj.getDoubleResult(index), minValue, operation)) {
                qualified = false;
            }
        }
        return qualified;
    }

    /**
     * Get a cell's qualified flag from one string condition
     */
    private boolean getQualified(String columnName, int operation, String minValue, String maxValue, int index) {
        boolean qualified = true;
        ColumnOJ coj = columns.getColumnByName(columnName);
        if ((operation == QualifierOJ.OPERATION_NOT_WITHIN) || (operation == QualifierOJ.OPERATION_WITHIN)) {
            if (!QualifierOJ.qualify(coj.getStringResult(index), minValue, maxValue, operation)) {
                qualified = false;
            }
            if (operation == QualifierOJ.OPERATION_NOT_WITHIN) {
                qualified = !qualified;//3.12.2009
            }
        } else if ((operation == QualifierOJ.OPERATION_EXISTS) || (operation == QualifierOJ.OPERATION_EMPTY)) {
            if (!QualifierOJ.qualify(coj.getStringResult(index), operation)) {
                qualified = false;
            }
        } else {
            if (!QualifierOJ.qualify(coj.getStringResult(index), minValue, operation)) {
                qualified = false;
            }
        }
        return qualified;
    }

    public boolean[] qualifyCells() {
        boolean[] qualifying_flags = new boolean[((DataOJ) parent).getCells().getCellsCount()];//set all to false
        if (qualifiers.getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {
            for (int i = 0; i < qualifying_flags.length; i++) {
                qualifying_flags[i] = true;
            }
            for (int i = 0; i < qualifiers.getQualifiersCount(); i++) {
                QualifierOJ qq = qualifiers.getQualifierByIndex(i);//3.12.2009
                String colName = qq.getColumnName();
                if (!columns.getColumnByName(colName).getColumnDef().isTextMode()) {
                    applyOneCondition(colName, qq.getOperation(), qq.getFirstDoubleValue(), qq.getSecondDoubleValue(), qualifying_flags);
                } else {
                    qualifyCells(colName, qq.getOperation(), qq.getFirstStringValue(), qq.getSecondStringValue(), qualifying_flags);
                }
            }
        }
        return qualifying_flags;
    }

    public boolean[] qualifyCells(QualifierOJ[] qualifiers) {
        boolean[] qualified_cells = new boolean[((DataOJ) parent).getCells().getCellsCount()];
        for (int i = 0; i < qualified_cells.length; i++) {
            qualified_cells[i] = true;
        }
        for (int i = 0; i < qualifiers.length; i++) {
            if (columns.getColumnByName(qualifiers[i].getColumnName()).getColumnDef().isTextMode()) {
                qualifyCells(qualifiers[i].getColumnName(), qualifiers[i].getOperation(), qualifiers[i].getFirstStringValue(), qualifiers[i].getSecondStringValue(), qualified_cells);
            } else {
                double first_double;
                double second_double;
                try {
                    first_double = Double.parseDouble(qualifiers[i].getFirstStringValue());
                } catch (NumberFormatException e) {
                    first_double = Double.NaN;
                }
                try {
                    second_double = Double.parseDouble(qualifiers[i].getSecondStringValue());
                } catch (NumberFormatException e) {
                    second_double = Double.NaN;
                }
                applyOneCondition(qualifiers[i].getColumnName(), qualifiers[i].getOperation(), first_double, second_double, qualified_cells);
            }
        }
        return qualified_cells;
    }

    private void qualifyCells(String columnName, int operation, String minValue, String maxValue, boolean[] qualified_cells) {
        ColumnOJ coj = columns.getColumnByName(columnName);
        if ((operation == QualifierOJ.OPERATION_NOT_WITHIN) || (operation == QualifierOJ.OPERATION_WITHIN)) {
            for (int i = 0; i < coj.getResultCount(); i++) {
                if (!QualifierOJ.qualify(coj.getStringResult(i), minValue, maxValue, operation)) {
                    qualified_cells[i] = false;
                }
            }
            if (operation == QualifierOJ.OPERATION_NOT_WITHIN) {
                for (int i = 0; i < coj.getResultCount(); i++) {
                    qualified_cells[i] = !qualified_cells[i];
                }
            }
        } else if ((operation == QualifierOJ.OPERATION_EXISTS) || (operation == QualifierOJ.OPERATION_EMPTY)) {
            for (int i = 0; i < coj.getResultCount(); i++) {
                if (!QualifierOJ.qualify(coj.getStringResult(i), operation)) {
                    qualified_cells[i] = false;
                }
            }
        } else {
            for (int i = 0; i < coj.getResultCount(); i++) {
                if (!QualifierOJ.qualify(coj.getStringResult(i), minValue, operation)) {
                    qualified_cells[i] = false;
                }
            }
        }
    }

    //narrows selection by disqualifying more cells in qualified_cells array
    private void applyOneCondition(String columnName, int operation, double minValue, double maxValue, boolean[] qualified_cells) {
        ColumnOJ column = columns.getColumnByName(columnName);
        if (column.isUnlinkedColumn()) {//1.2.2014
            return;
        }
        if ((operation == QualifierOJ.OPERATION_NOT_WITHIN) || (operation == QualifierOJ.OPERATION_WITHIN)) {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (!QualifierOJ.qualify(column.getDoubleResult(i), minValue, maxValue, operation)) {
                    qualified_cells[i] = false;
                }
            }
            if (operation == QualifierOJ.OPERATION_NOT_WITHIN) {
                for (int k = column.getResultCount(); k < ((DataOJ) parent).getCells().getCellsCount(); k++) {
                    qualified_cells[k] = false;
                }
            }
        } else if ((operation == QualifierOJ.OPERATION_EXISTS) || (operation == QualifierOJ.OPERATION_EMPTY)) {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (!QualifierOJ.qualify(column.getDoubleResult(i), operation)) {
                    qualified_cells[i] = false;
                }
            }
            for (int k = column.getResultCount(); k < ((DataOJ) parent).getCells().getCellsCount(); k++) {
                qualified_cells[k] = false;
            }
        } else {
            for (int i = 0; i < column.getResultCount(); i++) {
                if (!QualifierOJ.qualify(column.getDoubleResult(i), minValue, operation)) {
                    qualified_cells[i] = false;
                }
            }
            for (int k = column.getResultCount(); k < ((DataOJ) parent).getCells().getCellsCount(); k++) {
                qualified_cells[k] = false;
            }
        }
    }

    public void recalculate() {
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            columns.getColumnByIndex(i).recalculate();
        }
    }

    private void sort(Object[] a, Object[] b, int from, int to, boolean ascending, Comparator comparator) {
        // No sort
        if (a == null || a.length < 2) {
            return;
        }
        // sort using Quicksort
        int i = from;
        int j = to;
        Object center = a[(from + to) / 2];
        do {
            if (ascending) {
                while ((i < to) && (comparator.compare(center, a[i]) > 0)) {
                    i++;
                }
                while ((j > from) && (comparator.compare(center, a[j]) < 0)) {
                    j--;
                }
            } else {
                // Decending sort
                while ((i < to) && (comparator.compare(center, a[i]) < 0)) {
                    i++;
                }
                while ((j > from) && (comparator.compare(center, a[j]) > 0)) {
                    j--;
                }
            }
            if (i < j) {
                // Swap elements
                Object temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                // Swap in b array if needed
                if (b != null) {
                    temp = b[i];
                    b[i] = b[j];
                    b[j] = temp;
                }
            }
            if (i <= j) {
                i++;
                j--;
            }
        } while (i <= j);
        // Sort the rest
        if (from < j) {
            sort(a, b, from, j, ascending, comparator);
        }
        if (i < to) {
            sort(a, b, i, to, ascending, comparator);
        }
    }

    public int getCellIndexByMinValue(double value) {
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            ColumnOJ column = columns.getColumnByIndex(i);
            if (column.getStatistics().getStatisticsValueByName("Minimum") == value) {
                return i;
            }
        }
        return -1;
    }

    public int getCellIndexByMaxValue(double value) {
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            ColumnOJ column = columns.getColumnByIndex(i);
            if (column.getStatistics().getStatisticsValueByName("Maximum") == value) {
                return i;
            }
        }
        return -1;
    }

    public int getResultsCount() {
        return ((DataOJ) parent).getCells().getCellsCount();
    }

    public int getUnlinkedResultsCount() {
        int count = 0;
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (columns.getColumnByIndex(i).isUnlinkedColumn()) {
                count = count > columns.getColumnByIndex(i).getResultCount() ? count : columns.getColumnByIndex(i).getResultCount();
            }
        }
        return count;
    }

    class StringComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            return ((String) arg0).compareTo((String) arg1);
        }
    }

    class DoubleComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            return ((Double) arg0).compareTo((Double) arg1);
        }
    }
}
