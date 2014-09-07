/**
 * ResultChangedEvent.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */
package oj.processor.events;

public class ResultChangedEventOJ {
    
    public static int RESULTS_ADDED = 1;
    public static int RESULTS_EDITED = 2;
    public static int RESULTS_DELETED = 3;
    
    
    private String columnName;
    private int operation;
    private int row;
    
    public ResultChangedEventOJ(String columnName, int row, int operation) {
        this.columnName = columnName;
        this.operation = operation;
        this.row = row;
    }
    
    public String getColumnName(){
        return columnName;
    }
    
    public int getOperation(){
        return operation;
    }
    
    public int getRow(){
        return row;
    }

}
