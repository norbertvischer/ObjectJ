/**
 * ColumnChangedEvent.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 *
 */

package oj.processor.events;

public class ColumnChangedEventOJ {
    public final static int COLUMN_ADDED = 1;
    public final static int COLUMN_EDITED = 2;
    public final static int COLUMN_DELETED = 3;    
    
    private String newName;
    private String oldName;
    private int operation;
    
    public ColumnChangedEventOJ(String oldName, String newName, int operation) {
        this.oldName = oldName;
        this.newName = newName;
        this.operation = operation;
    }
    
    public String getNewName(){
        return newName;
    }
    
    public String getOldName(){
        return oldName;
    }
    
    public int getOperation(){
        return operation;
    }
    
}
