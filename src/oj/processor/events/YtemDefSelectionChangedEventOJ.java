/*
 * YtemDefSelectionChangedEventOJ.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */
package oj.processor.events;

public class YtemDefSelectionChangedEventOJ {
    
    private String name;
    
    public YtemDefSelectionChangedEventOJ(String name) {
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
}
