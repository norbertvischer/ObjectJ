/**
 * StatisticsChangedEventOJ.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */

package oj.processor.events;

public class StatisticsChangedEventOJ {
    
    public static int STATISTICS_ADDED = 1;
    public static int STATISTICS_EDITED = 2;
    public static int STATISTICS_DELETED = 3;
    public static int STATISTICS_VALUE_CHANGED = 4;
    
    private String name;
    private int operation;
    
    public StatisticsChangedEventOJ(String name, int operation) {
        this.name = name;
        this.operation = operation;
    }
    
    public String getName(){
        return name;
    }
    
    public int getOperation(){
        return operation;
    }
}
