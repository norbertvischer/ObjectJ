/*
 * StatisticsCountOJ.java
 */
package oj.project.results.statistics;

import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;


public abstract class StatisticsAdapterOJ extends BaseAdapterOJ implements IStatisticsOJ {

    private static final long serialVersionUID = 1963207179051045421L;
    protected /*transient*/ boolean enabled = true;//13.4.2010
    protected /*transient*/ boolean visible = false;//13.4.2010
    protected String name;

    public StatisticsAdapterOJ() {
    }

    public StatisticsAdapterOJ(IBaseOJ parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double recalculate(String columnName) {
        return Double.NaN;
    }
}
