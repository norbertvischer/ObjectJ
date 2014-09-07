/*
 * CellStateAdaptorOJ.java
 * is extended by CreateCellStateOj, DeleteCellStateOJ etc.
 * Handles user events from mouse and controls the cursor appearance.
 */
package oj.processor.state;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import oj.util.UtilsOJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.ToolStateProcessorOJ;

public class ToolStateAdaptorOJ implements ToolStateOJ {

  public ToolStateAdaptorOJ() {
    setCanvasCursor();
  }

  public int getToolState() {
    return ToolStateProcessorOJ.STATE_NONE;
  }

  public void mousePressed(String imageName, int stackIndex, double x, double y, int flags) {
    setMousePos(x, y, flags);
  }

  public void mouseDragged(String imageName, int stackIndex, double x, double y, int flags) {
    if (flags == 0) // workaround for Mac OS 9 bug
    {
      flags = InputEvent.BUTTON1_MASK;
    }
    setMousePos(x, y, flags);
  }

  public void mouseReleased(String imageName, int stackIndex, double x, double y, int flags) {
    flags &= ~InputEvent.BUTTON1_MASK; // make sure button 1 bit is not set
    flags &= ~InputEvent.BUTTON2_MASK; // make sure button 2 bit is not set
    flags &= ~InputEvent.BUTTON3_MASK; // make sure button 3 bit is not set
    ImageCanvas ic = getCanvas();
    if (ic != null) {
      setFlags(ic, flags);
    }
    showMouseStatus(x, y);
  }

  public void mouseMoved(String imageName, int stackIndex, double x, double y, int flags) {
    showMouseStatus(x, y);
    setMousePos(x, y, flags);
  }

  public void mouseClicked(String imageName, int stackIndex, double x, double y, int flags) {
  }

  public void mouseEntered(String imageName, int stackIndex, double x, double y, int flags) {
  }

  public void mouseExited(String imageName, int stackIndex, double x, double y, int flags) {
    IJ.showStatus("");
  }

  public void keyPressed(String imageName, int stackIndex, int keyCode, int flags) {
  }

  public void keyReleased(String imageName, int stackIndex, int keyCode, int flags) {

    setCanvasCursor();
  }

  public Cursor getDefaultCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  public void cleanup() {
  }

  protected void setMousePos(double x, double y, int flags) {
    ImageCanvas ic = getCanvas();
    if (ic != null) {
      setMousePos(ic, (int) x, (int) y);
      setFlags(ic, flags);
    }
  }

  protected ImageCanvas getCanvas() {
    ImageWindow win = WindowManager.getCurrentWindow();
    if (win != null) {
      return win.getCanvas();
    }
    return null;
  }

  private ImagePlus getImagePlus() {
    ImageWindow win = WindowManager.getCurrentWindow();
    if (win != null) {
      return win.getImagePlus();
    }
    return null;
  }

  private String getValueAsString(ImagePlus imp, int x, int y) {
    final Method methods[] = ImagePlus.class.getDeclaredMethods();
    final String mName = "getValueAsString";
    for (int i = 0; i < methods.length; ++i) {
      if (mName.equals(methods[i].getName())) {
        methods[i].setAccessible(true);
        try {
          Object[] args = new Object[]{x, y};
          return (String) methods[i].invoke(imp, args);
        } catch (Exception ex) {
          UtilsOJ.showException(ex, mName);//22.6.2009
        }

        break;
      }
    }
    UtilsOJ.showException(null, mName);//22.6.2009
    return "";
  }

  protected void showMouseStatus(double x, double y) {
    ImagePlus imp = getImagePlus();
    IJ.showStatus(imp.getLocationAsString((int) x, (int) y) + getValueAsString(imp, (int) x, (int) y));
  }

  protected void setMousePos(ImageCanvas canvas, int xMouse, int yMouse) {
    final Field fields[] = ImageCanvas.class.getDeclaredFields();
    for (int i = 0; i < fields.length; ++i) {
      if ("xMouse".equals(fields[i].getName())) {
        fields[i].setAccessible(true);
        try {
          fields[i].set(canvas, xMouse);
        } catch (IllegalArgumentException ex) {
          ex.printStackTrace();
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
        break;
      }
    }
    for (int i = 0; i < fields.length; ++i) {
      if ("yMouse".equals(fields[i].getName())) {
        fields[i].setAccessible(true);
        try {
          fields[i].set(canvas, yMouse);
        } catch (IllegalArgumentException ex) {
          ex.printStackTrace();
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
        break;
      }
    }
  }

  protected void setFlags(ImageCanvas canvas, int flags) {
    final Field fields[] = ImageCanvas.class.getDeclaredFields();
    for (int i = 0; i < fields.length; ++i) {
      if ("flags".equals(fields[i].getName())) {
        fields[i].setAccessible(true);
        try {
          fields[i].set(canvas, flags);
        } catch (IllegalArgumentException ex) {
          ex.printStackTrace();
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
        break;
      }
    }
  }

  private void setCanvasCursor() {
    ImageCanvas ic = getCanvas();
    if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
      if (ic instanceof CustomCanvasOJ) {
        ic.setCursor(getDefaultCursor());
      } else {
        ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
    }
  }

  protected void setCanvasCursor(Cursor cursor) {
    ImageCanvas ic = getCanvas();
    if ((ic != null) && (ic instanceof CustomCanvasOJ)) {
      if (ic instanceof CustomCanvasOJ) {
        ic.setCursor(cursor);
      } else {
        ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
    }
  }
}
