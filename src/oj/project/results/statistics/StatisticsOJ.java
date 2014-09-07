/*
 * StatisticsOJ.java
 */
package oj.project.results.statistics;

import java.util.ArrayList;
import oj.OJ;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;
import oj.processor.events.StatisticsChangedEventOJ;

public class StatisticsOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -6525967648332622500L;
    private ArrayList statistics = new ArrayList();

    public StatisticsOJ() {
        addStatistics();
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (statistics == null) {
            statistics = new ArrayList();
        }
        addStatistics();
        for (int i = 0; i < statistics.size(); i++) {
            IStatisticsOJ statistic = (IStatisticsOJ) statistics.get(i);
            statistic.initAfterUnmarshalling(this);
        }
    }

    public boolean getChanged() {
        if (changed) {
            return true;
        } else {
            for (int i = 0; i < statistics.size(); i++) {
                if (((IStatisticsOJ) statistics.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < statistics.size(); i++) {
            ((IStatisticsOJ) statistics.get(i)).setChanged(changed);
        }
    }

    public boolean addStatistics(IStatisticsOJ statistic) {
        if (getStatisticsByName(statistic.getName()) == null) {
            statistics.add(statistic);
            statistic.setParent(this);
            changed = true;
            OJ.getEventProcessor().fireStatisticsChangedEvent(statistic.getName(), StatisticsChangedEventOJ.STATISTICS_ADDED);
            return true;
        }
        return false;
    }

    public int indexOfStatistics(String name) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equals(((IStatisticsOJ) statistics.get(i)).getName())) {
                return i;
            }
        }
        return -1;
    }

    public IStatisticsOJ getStatisticsByName(String name) {
        for (int i = 0; i < statistics.size(); i++) {
            if (name.equals(((IStatisticsOJ) statistics.get(i)).getName())) {
                return (IStatisticsOJ) statistics.get(i);
            }
        }
        return null;
    }

    public void removeStatisticsByName(String name) {
        int index = indexOfStatistics(name);
        if (index >= 0) {
            statistics.remove(index);
            changed = true;
            OJ.getEventProcessor().fireStatisticsChangedEvent(name, StatisticsChangedEventOJ.STATISTICS_DELETED);
        }
    }

    public void setStatistics(String name, StatisticsMacroOJ statistic) {
        for (int i = 0; i < statistics.size(); i++) {
            if (((StatisticsMacroOJ) statistics.get(i)).getName().equals(statistic.getName())) {
                statistics.set(i, statistic);
                statistic.setParent(this);
                changed = true;
                break;
            }
        }
    }

    public IStatisticsOJ getStatisticsByIndex(int index) {
        return (IStatisticsOJ) statistics.get(index);
    }

    public void removeStatisticsByIndex(int index) {
        String name = getStatisticsByIndex(index).getName();
        statistics.remove(index);
        changed = true;
        OJ.getEventProcessor().fireStatisticsChangedEvent(name, StatisticsChangedEventOJ.STATISTICS_DELETED);
    }

    public int getStatisticsCount() {
        return statistics.size();
    }

    private void addStatistics() {
        addStatistics(new StatisticsCountOJ());
        addStatistics(new StatisticsMeanOJ());
        addStatistics(new StatisticsMinimumOJ());
        addStatistics(new StatisticsMaximumOJ());
        addStatistics(new StatisticsSumOJ());
        addStatistics(new StatisticsStDevOJ());
        addStatistics(new StatisticsCvOJ());
    }
}
