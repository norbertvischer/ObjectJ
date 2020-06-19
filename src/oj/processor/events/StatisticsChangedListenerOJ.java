/*
 * StatisticsChangedListenerOJ.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a statisticaChanged method
 *
 */
package oj.processor.events;

public interface StatisticsChangedListenerOJ {
    
    public void statisticsChanged(StatisticsChangedEventOJ evt);
    
}
