/*
 * ColumnChangedListenerOJ.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a columnChanged method
 *
 */

package oj.processor.events;

public interface ColumnChangedListenerOJ {
    
    public void columnChanged(ColumnChangedEventOJ evt);
    
}
