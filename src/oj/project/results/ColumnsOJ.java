/*
 * ColumnsOJ.java
 */
package oj.project.results;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import oj.OJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.StatisticsChangedEventOJ;
import oj.processor.events.StatisticsChangedListenerOJ;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;
import oj.project.results.statistics.IStatisticsOJ;
import oj.project.results.statistics.StatisticsProxyOJ;
import oj.util.WildcardMatchOJ;

public class ColumnsOJ extends BaseAdapterOJ implements StatisticsChangedListenerOJ {

    private static final long serialVersionUID = 3318467840607037788L;
    public static final int COLUM_SORT_FLAG_NONE = 0;
    public static final int COLUM_SORT_FLAG_ASCENDING = 1;
    public static final int COLUM_SORT_FLAG_DESCENDING = 2;
    private int columnLinkedSortFlag = COLUM_SORT_FLAG_NONE;
    private String columnLinkedSortName = "";
    private ArrayList columns = new ArrayList();
    private transient int columnUnlinkedSortFlag = COLUM_SORT_FLAG_NONE;
    private transient String columnUnlinkedSortName;

    /**
     * Creates a new instance of StatisticsOJ
     */
    public ColumnsOJ() {
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeInt(columnLinkedSortFlag);
        stream.writeUTF(columnLinkedSortName);
        stream.writeObject(columns);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        columnLinkedSortFlag = stream.readInt();
        columnLinkedSortName = stream.readUTF();
        columns = (ArrayList) stream.readObject();
    }

    /**
     * return the name of the sorting flag possible values [none, ascending,
     * descending]
     *
     * @return sort flag name
     */
    public static String getSortFlagName(int flag) {
        switch (flag) {
            case ColumnsOJ.COLUM_SORT_FLAG_NONE:
                return "none";
            case ColumnsOJ.COLUM_SORT_FLAG_ASCENDING:
                return "ascending";
            case ColumnsOJ.COLUM_SORT_FLAG_DESCENDING:
                return "descending";
            default:
                return "none";
        }
    }

    /**
     * return the sorting flag type possible values [COLUM_SORT_FLAG_NONE,
     * COLUM_SORT_FLAG_DESCENDING, COLUM_SORT_FLAG_DESCENDING]
     *
     * @return sort flag name
     */
    public static int getSortFlag(String flag) {
        if ("ascending".equals(flag)) {
            return ColumnsOJ.COLUM_SORT_FLAG_ASCENDING;
        } else if ("descending".equals(flag)) {
            return ColumnsOJ.COLUM_SORT_FLAG_DESCENDING;
        } else {
            return ColumnsOJ.COLUM_SORT_FLAG_NONE;
        }
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        columnLinkedSortFlag = ColumnsOJ.COLUM_SORT_FLAG_NONE;
        if (columns == null) {
            columns = new ArrayList();
        }
        for (int i = 0; i < columns.size(); i++) {
            ColumnOJ column = (ColumnOJ) columns.get(i);
            column.initAfterUnmarshalling(this);
        }
    }

    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < columns.size(); i++) {
                if (((ColumnOJ) columns.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < columns.size(); i++) {
            ((ColumnOJ) columns.get(i)).setChanged(changed);
        }
    }

    public void fixColumnsOrder() {//20.7.2009
        //inserts linked columns
        int firstUnlinked = -1;//unlinked columns must be in second part
        int max = columns.size();
        for (int jj = 0; jj < max; jj++) {
            if (columns.get(jj) != null) {
                String name = ((ColumnOJ) columns.get(jj)).getName();
                boolean isUnlinked = name.startsWith("_");
                if (isUnlinked) {
                    if (firstUnlinked == -1) {
                        firstUnlinked = jj;
                    }

                } else {
                    if (firstUnlinked > -1) {
                        Object thisCol = columns.get(jj);
                        columns.set(jj, null);
                        columns.add(firstUnlinked, thisCol);
                        firstUnlinked++;
                        max++;
                    }
                }
            }
        }
        max = columns.size();
        for (int jj = max - 1; jj >= 0; jj--) {
            if (columns.get(jj) == null) {
                columns.remove(jj);
            }
        }
    }

    public int indexOfColumn(ColumnOJ column) {
        return columns.indexOf(column);
    }

    public int indexOfColumn(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (getColumnByIndex(i).getName().equalsIgnoreCase(name)) {
                //n_27.3.2007)
                return i;
            }
        }
        return -1;
    }

    public void removeColumnByIndex(int index) {//here is the shit
        ColumnOJ column = (ColumnOJ) columns.get(index);
        if (column != null) {
            //column.clear(); not needed 30.5.2010
            columns.remove(index);
            if (columnLinkedSortName.equals(column.getName())) {
                columnLinkedSortName = "";
            }
            changed = true;
            OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), null, ColumnChangedEventOJ.COLUMN_DELETED);
        }
    }

    public void removeColumnByName(String name) {
        columns.remove(indexOfColumn(name));
        if (columnLinkedSortName.equals(name)) {
            columnLinkedSortName = "";
        }
        changed = true;
        OJ.getEventProcessor().fireColumnChangedEvent(name, null, ColumnChangedEventOJ.COLUMN_DELETED);
    }

    public void removeColumn(ColumnOJ column) {
        columns.remove(column);
        if (columnLinkedSortName.equals(column.getName())) {
            columnLinkedSortName = "";
        }
        changed = true;
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), null, ColumnChangedEventOJ.COLUMN_DELETED);
    }

    public ColumnOJ setColumn(int index, ColumnOJ column) {
        ColumnOJ old_column = (ColumnOJ) columns.get(index);
        columns.set(index, column);
        changed = true;
        return old_column;
    }

    public ColumnOJ[] columnsToArray() {
        ColumnOJ[] result = new ColumnOJ[columns.size()];
        System.arraycopy(columns, 0, result, 0, columns.size());
        return result;
    }

    public String[] columnNamesToArray() {
        String[] result = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            result[i] = ((ColumnOJ) columns.get(i)).getName();
        }
        return result;
    }

    public String[] columnLinkedNamesToArray() {
        ArrayList names = new ArrayList();
        for (int i = 0; i < getAllColumnsCount(); i++) {
            if (!getColumnByIndex(i).isUnlinkedColumn()) {
                names.add(getColumnByIndex(i).getName());
            }
        }
        String[] result = (String[]) names.toArray(new String[names.size()]);
        return result;
    }

    public String[] columnUnlinkedNamesToArray() {
        ArrayList names = new ArrayList();
        for (int i = 0; i < getAllColumnsCount(); i++) {
            if (getColumnByIndex(i).isUnlinkedColumn()) {
                names.add(getColumnByIndex(i).getName());
            }
        }
        String[] result = (String[]) names.toArray(new String[names.size()]);
        return result;
    }

    public int getUnlinkedColumnsCount() {
        int jj = 0;
        for (int i = 0; i < getAllColumnsCount(); i++) {
            if (getColumnByIndex(i).isUnlinkedColumn()) {
                jj++;
            }
        }
        return jj;
    }

    public int getLinkedColumnsCount() {
        int jj = 0;
        for (int i = 0; i < getAllColumnsCount(); i++) {
            if (!getColumnByIndex(i).isUnlinkedColumn()) {
                jj++;
            }
        }
        return jj;
    }

    public int getColumnLinkedSortFlag() {
        return columnLinkedSortFlag;
    }

    public String getColumnLinkedSortName() {
        return columnLinkedSortName;
    }

    public int getColumnUnlinkedSortFlag() {
        return columnUnlinkedSortFlag;
    }

    public String getColumnUnlinkedSortName() {
        return columnUnlinkedSortName;
    }

    public void setColumnLinkedSortFlag(int columnSortFlag) {
        this.columnLinkedSortFlag = columnSortFlag;
    }

    public void setColumnLinkedSortName(String columnSortName) {
        this.columnLinkedSortName = columnSortName;
    }

    public void setColumnUnlinkedSortFlag(int columnSortFlag) {
        this.columnUnlinkedSortFlag = columnSortFlag;
    }

    public void setColumnUnlinkedSortName(String columnSortName) {
        this.columnUnlinkedSortName = columnSortName;
    }

    public int addColumn(ColumnOJ column, boolean initialize) {
        column.setParent(this);
        if (initialize) {
            column.init();
        }
        int atPosition = getLinkedColumnsCount();
        columns.add(atPosition, column);//1.2.2014
        changed = true;
        OJ.getEventProcessor().fireColumnChangedEvent(null, column.getName(), ColumnChangedEventOJ.COLUMN_ADDED);
        return atPosition;

    }

    /**
     *
     * @return total nmber of linked and unlinked columns
     */
    public int getAllColumnsCount() {
        return columns.size();
    }

    public ColumnOJ getColumnByIndex(int index) {
        if (index < 0 || index >= columns.size()) {
            ij.IJ.showMessage("index =" + index + "  size = " + columns.size());//15.3.2009
            return null;
        }
        return (ColumnOJ) columns.get(index);
    }

    public ColumnOJ getColumnByName(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (((ColumnOJ) columns.get(i)).getName().equalsIgnoreCase(name)) {
                //n_27.3.2007
                return (ColumnOJ) columns.get(i);
            }
        }
        return null;
    }

    /**
     * @return index (0-based) of column with that title, or -1 if not found
     */
    public int getColumnIndexByName(String name) {
        for (int i = 0; i < columns.size(); i++) {
            if (((ColumnOJ) columns.get(i)).getName().equalsIgnoreCase(name)) {
                //n_27.3.2007
                return i;
            }
        }
        return -1;
    }

    public ArrayList getColumnsByWildcard(String pattern) {

        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);

        ArrayList matchingColumns = new ArrayList();
        for (int jj = 0; jj < getAllColumnsCount(); jj++) {
            ColumnOJ thisCol = getColumnByIndex(jj);
            if (wm.match(thisCol.getName(), pattern)) {
                matchingColumns.add(thisCol);
            }
        }
        return matchingColumns;
    }

    public void statisticsChanged(StatisticsChangedEventOJ evt) {
        if (evt.getOperation() == StatisticsChangedEventOJ.STATISTICS_ADDED) {
            for (int i = 0; i < getAllColumnsCount(); i++) {
                IStatisticsOJ statistic = OJ.getData().getResults().getStatistics().getStatisticsByName(evt.getName());
                StatisticsProxyOJ statistic_proxy = new StatisticsProxyOJ(statistic.getName(), getColumnByIndex(i).getName(), statistic);
                getColumnByIndex(i).getStatistics().addStatistic(statistic_proxy);
            }
        } else if (evt.getOperation() == StatisticsChangedEventOJ.STATISTICS_DELETED) {
            for (int i = 0; i < getAllColumnsCount(); i++) {
                getColumnByIndex(i).removeStatistic(evt.getName());
            }
        }

    }
}
