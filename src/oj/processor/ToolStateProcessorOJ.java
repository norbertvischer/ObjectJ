/*
 * ToolStateProcessorOJ.java
 */
package oj.processor;

import oj.processor.state.ToolStateAdaptorOJ;
import oj.processor.state.ToolStateOJ;

public class ToolStateProcessorOJ {

    public final static int STATE_NONE = 0;
    public final static int STATE_MACRO_TOOL = 1;
    public final static int STATE_OBJECT_TOOL = 2;
    private ToolStateOJ toolStateObject = new ToolStateAdaptorOJ();
    private int toolState;

    public ToolStateProcessorOJ() {
        toolStateObject = null;
        toolState = ToolStateProcessorOJ.STATE_NONE;
        toolStateObject = new ToolStateAdaptorOJ();
    }

    public void setToolState(ToolStateOJ state) {
        if (toolStateObject != null) {
            toolStateObject.cleanup();
        }
        if (state != null) {
            toolStateObject = state;
            toolState = state.getToolState();
        } else {
            toolStateObject = null;
            toolState = ToolStateProcessorOJ.STATE_NONE;
        }
    }

    public int getToolState() {
        return toolState;
    }

    public ToolStateOJ getToolStateObject() {
        return toolStateObject;
    }

    public void mousePressed(String imageName, int stackIndex, double x, double y, int flags) {
        if (toolStateObject != null) {
            toolStateObject.mousePressed(imageName, stackIndex, x, y, flags);
        }
    }

    public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags) {
        if (toolStateObject != null) {
            toolStateObject.mouseDragged(imageName, stackIndex, x, y, flags);
        }
    }

    public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags) {
        if (toolStateObject != null) {
            toolStateObject.mouseReleased(imageName, stackIndex, x, y, flags);
        }
    }

    public void mouseMoved(String imageName, int stackIndex, double x, double y, int flags) {
        if (toolStateObject != null) {
            toolStateObject.mouseMoved(imageName, stackIndex, x, y, flags);
        }
    }

    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
        if (toolStateObject != null) {
            toolStateObject.keyPressed(imageName, stackIndex, keyCode, flags);
        }
    }

    public void keyReleased(String imageName, int stackIndex, int keyCode, int flags) {
        if (toolStateObject != null) {
            toolStateObject.keyReleased(imageName, stackIndex, keyCode, flags);
        }
    }


}
