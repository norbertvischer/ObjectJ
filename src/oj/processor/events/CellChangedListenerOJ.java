/*
 * CellChangedListener.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a cellChanged method
 *
 */

package oj.processor.events;

public interface CellChangedListenerOJ {
    
    public void cellChanged(CellChangedEventOJ evt);
    
}
