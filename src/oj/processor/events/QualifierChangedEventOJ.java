/*
 * QualifierChangedEventOJ.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */
package oj.processor.events;

public class QualifierChangedEventOJ {       
    
    public static int QUALIFIER_ITEM_ADDED = 1;
    public static int QUALIFIER_ITEM_EDITED = 2;
    public static int QUALIFIER_ITEM_DELETED = 3;
    public static int QUALIFIER_METHOD_CHANGED = 4;
    
    private String name;
    private int operation;
        
    public QualifierChangedEventOJ(String name, int operation) {
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
