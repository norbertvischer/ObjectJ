  //ToolStateOJ. Interface used by ToolStateAdaptor, which in turn is extended by CreateCellStateOj, DeleteCellStateOJ etc.
//Handles user events from mouse and controls the cursor appearance.

package oj.processor.state;

import java.awt.Cursor;

public interface ToolStateOJ {
    
    public void mousePressed(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseMoved(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseClicked(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags);
    public void mouseExited(String imageName, int stackIndex, double x, double y, int flags);
    
    public void keyPressed(String imageName, int stackIndex, int keyCode, int flags);
    public void keyReleased(String imageName, int stackIndex, int keyCode, int flags);
    
    public Cursor getDefaultCursor();
    public int getToolState();
    
    public void cleanup();
}
