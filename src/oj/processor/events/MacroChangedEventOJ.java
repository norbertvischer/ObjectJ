/*
 * MacroChangedEvent.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */

package oj.processor.events;

public class MacroChangedEventOJ {
    
    public static final int MACRO_ADDED = 1;
    public static final int MACRO_EDITED = 2;
    public static final int MACRO_DELETED = 3;
    
    public static final int MACROSET_ADDED = 4;
    public static final int MACROSET_EDITED = 5;
    public static final int MACROSET_DELETED = 6;
    
    private String name;
    private int operation;
        
    public MacroChangedEventOJ(String name, int operation) {
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
