/*
 * StatisticProxyOJ.java
 */
package oj.project.results.statistics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;
import oj.project.ResultsOJ;

public class StatisticsProxyOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -861678127821753788L;
    private transient IStatisticsOJ statistic;
    private transient String columnName;
    protected transient boolean dirty = true;
    private String name;
    private double value = Double.NaN;

    public StatisticsProxyOJ(String name, String columnName) {
        this.columnName = columnName;
        this.name = name;
    }

    public StatisticsProxyOJ(String name, String columnName, IStatisticsOJ statistic) {
        this.name = name;
        this.statistic = statistic;
        this.columnName = columnName;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(name);
        stream.writeDouble(value);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        name = stream.readUTF();
        value = stream.readDouble();
    }

    public void setStatistic(IStatisticsOJ statistic) {
        this.statistic = statistic;
        changed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        if (isDirty()) {
            recalculate();
        }
        return value;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        changed = dirty;
    }

    public void setValue(double value) {
        this.value = value;
        changed = false;
        dirty = false;
    }

    public void recalculate() {
        if (statistic != null) {
            value = statistic.recalculate(columnName);
            changed = true;
            dirty = false;
        }
    }

    public void initAfterUnmarshalling(IBaseOJ parent, String columnName) {
        super.initAfterUnmarshalling(parent);
        if (statistic == null) {
            statistic = ((ResultsOJ) parent.getParent().getParent().getParent()).getStatistics().getStatisticsByName(getName());
        }
        this.columnName = columnName;
        this.dirty = true;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.dirty = true;
    }
}
