/*
 * StatisticsOJ.java
 */
package oj.project.results.statistics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import oj.OJ;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;
import oj.project.ResultsOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.ImageChangedListener2OJ;

public class StatisticsProxysOJ extends BaseAdapterOJ implements ImageChangedListener2OJ {

    private static final long serialVersionUID = 7138288919785402141L;
    private ArrayList statistics = new ArrayList();

    public StatisticsProxysOJ() {
        init();
    }

    public StatisticsProxysOJ(String columnName) {
        addStatistics(columnName);
        setStatisticsDirty();

        for (int i = 0; i < statistics.size(); i++) {
            ((StatisticsProxyOJ) statistics.get(i)).setParent(this);
        }
        for (int i = 0; i < statistics.size(); i++) {
            ((StatisticsProxyOJ) statistics.get(i)).setColumnName(columnName);
        }
    }

    public void init(String columnName) {
        addStatistics(columnName);
        setStatisticsDirty();
    }

    public void initAfterUnmarshalling(IBaseOJ parent, String columnName) {
        super.initAfterUnmarshalling(parent);
        if (statistics == null) {
            statistics = new ArrayList();
        }

        addStatistics(columnName);
        setStatisticsDirty();

        for (int i = 0; i < statistics.size(); i++) {
            StatisticsProxyOJ statistic = (StatisticsProxyOJ) statistics.get(i);
            statistic.initAfterUnmarshalling(this, columnName);
        }
        OJ.getEventProcessor().addImageChangedListener(this);
    }

    public void setColumnName(String columnName) {
        for (int i = 0; i < statistics.size(); i++) {
            StatisticsProxyOJ statistic = (StatisticsProxyOJ) statistics.get(i);
            statistic.setColumnName(columnName);
        }
    }

    private void addStatistics(String columnName) {
        if ((parent != null) && (parent.getParent() != null) && (parent.getParent().getParent() != null)) {
            for (int i = 0; i < ((ResultsOJ) parent.getParent().getParent()).getStatistics().getStatisticsCount(); i++) {
                IStatisticsOJ statistic = ((ResultsOJ) parent.getParent().getParent()).getStatistics().getStatisticsByIndex(i);
                addStatistic(new StatisticsProxyOJ(statistic.getName(), columnName, statistic));
            }
        }
    }

    public boolean getChanged() {
        if (changed) {
            return true;
        } else {
            for (int i = 0; i < statistics.size(); i++) {
                if (((StatisticsProxyOJ) statistics.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < statistics.size(); i++) {
            ((StatisticsProxyOJ) statistics.get(i)).setChanged(changed);
        }
    }

    public void addStatistic(StatisticsProxyOJ statistic) {
        if (getStatisticsByName(statistic.getName()) == null) {
            statistics.add(statistic);
            statistic.setParent(this);
            changed = true;
        }
    }

    public int indexOfStatistics(String name) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equalsIgnoreCase(((StatisticsProxyOJ) statistics.get(i)).getName())) {
                return i;
            }
        }
        return -1;
    }

    public StatisticsProxyOJ getStatisticsByName(String name) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equalsIgnoreCase(((StatisticsProxyOJ) statistics.get(i)).getName())) {
                return (StatisticsProxyOJ) statistics.get(i);
            }
        }
        return null;
    }

    public void removeStatisticsByName(String name) {
        int index = indexOfStatistics(name);
        if (index >= 0) {
            statistics.remove(index);
            changed = true;
        }
    }

    public void setStatistics(String name, StatisticsProxyOJ statistic) {
        for (int i = 0; i < statistics.size(); i++) {
            if (((StatisticsProxyOJ) statistics.get(i)).getName().equalsIgnoreCase(statistic.getName())) {
                statistics.set(i, statistic);
                changed = true;
                break;
            }
        }
    }

    public double getStatisticsValueByName(String name) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equalsIgnoreCase(((StatisticsProxyOJ) statistics.get(i)).getName())) {
                return ((StatisticsProxyOJ) statistics.get(i)).getValue();
            }
        }
        return Double.NaN;
    }

    public double getStatisticsValueByIndex(int index) {
        return ((StatisticsProxyOJ) statistics.get(index)).getValue();
    }

    public void setStatisticsValueByName(String name, double value) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equalsIgnoreCase(((StatisticsProxyOJ) statistics.get(i)).getName())) {
                ((StatisticsProxyOJ) statistics.get(i)).setValue(value);
                changed = true;
                break;
            }
        }
    }

    public void setStatisticsValueByIndex(int index, double value) {
        ((StatisticsProxyOJ) statistics.get(index)).setValue(value);
        changed = true;
    }

    public StatisticsProxyOJ getStatisticsByIndex(int index) {
        return (StatisticsProxyOJ) statistics.get(index);
    }

    public void removeStatisticsByIndex(int index) {
        statistics.remove(index);
        changed = true;
    }

    public int getStatisticsCount() {
        return statistics.size();
    }

    public void setStatisticsDirty() {
        for (int i = 0; i < statistics.size(); i++) {
            ((StatisticsProxyOJ) statistics.get(i)).setDirty(true);
        }
    }

    public void imageChanged(ImageChangedEventOJ evt) {
        if (evt.getOperation() == ImageChangedEventOJ.IMAGE_DELETED) {
            for (int i = 0; i < getStatisticsCount(); i++) {
                getStatisticsByIndex(i).setDirty(true);
            }
        }
    }
}
