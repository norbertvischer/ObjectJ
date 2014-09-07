/*
 * ToolStateProcessorChangedEventOJ.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */

package oj.processor.events;

public class ToolStateProcessorChangedEventOJ {
    
    public final static int NONE = 0;
    public final static int MACRO_TOOL_STATE = 1;
    public final static int OJECT_TOOL_STATE = 2;
    
    private int state;
    
    public ToolStateProcessorChangedEventOJ(int state) {
        this.state = state;
    }
    
    public int getState(){
        return state;
    }
    
}
