/*
 * IStatisticsOJ.java
 */

package oj.project.results.statistics;

import oj.project.IBaseOJ;


public interface IStatisticsOJ extends IBaseOJ {

    public String getName();

    public boolean getEnabled();

    public boolean getVisible();

    public double recalculate(String columnName);

    public void setName(String name);

    public void setEnabled(boolean enabled);

    public void setVisible(boolean visible);
}