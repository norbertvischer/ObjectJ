/*
 * QualifierChangedListenerOJ.java
 * -- documented
 *
 * Interface that tells other classes that they should contain a qualifierChanged method
 *
 */
package oj.processor.events;

public interface QualifierChangedListenerOJ {
    
    public void qualifierChanged(QualifierChangedEventOJ evt);
    
}
