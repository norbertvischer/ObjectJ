/*
 * MacroChangedListener.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a macroChanged method
 */

package oj.processor.events;

public interface MacroChangedListenerOJ {
    
    public void macroChanged(MacroChangedEventOJ evt);
    
}
