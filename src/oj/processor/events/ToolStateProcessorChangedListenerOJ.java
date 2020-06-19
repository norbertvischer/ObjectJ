/*
 * ToolStateProcessorChangedListenerOJ.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a toolStateChanged method
 */

package oj.processor.events;

public interface ToolStateProcessorChangedListenerOJ {
    
    public void toolStateChanged(ToolStateProcessorChangedEventOJ evt);
    
}
