package oj.gui;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import oj.OJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.ToolStateProcessorOJ;
import oj.processor.EventProcessorOJ;
import oj.processor.state.MacroToolStateOJ;
import oj.processor.state.MoveCellStateOJ;
import oj.processor.state.ToolStateOJ;
import oj.project.DataOJ;
import oj.project.ImagesOJ;

public class MouseEventManagerOJ implements MouseListener, MouseMotionListener {

	private static MouseEventManagerOJ instance;
	private static int pressedX;
	private static int pressedY;

	private MouseEventManagerOJ() {
	}


	public static MouseEventManagerOJ getInstance() {
		if (instance == null) {
			instance = new MouseEventManagerOJ();
		}
		return instance;
	}

	public static void release() {
		if (instance != null) {

			//remove the listener from any open ImageWindow
			for (int i = 0; i < WindowManager.getImageCount(); i++) {
				WindowManager.getImage(i).getCanvas().addMouseListener(WindowManager.getImage(i).getCanvas());
				WindowManager.getImage(i).getCanvas().addMouseMotionListener(WindowManager.getImage(i).getCanvas());
				WindowManager.getImage(i).getCanvas().removeMouseListener(MouseEventManagerOJ.getInstance());
				WindowManager.getImage(i).getCanvas().removeMouseMotionListener(MouseEventManagerOJ.getInstance());
			}

			instance = null;
		}
	}

	private boolean impLinked(ImagePlus imp) {
		DataOJ data = OJ.getData();
		if (data == null || imp == null) {
			return false;
		}
		ImagesOJ images = OJ.getData().getImages();
		String title = imp.getTitle();
		boolean linked = (images.getImageByName(title) != null);//18.6.2009
		return linked;
	}

	public void replaceMouseListener(ImagePlus imp) {
		if (imp != null && imp.getCanvas() != null) {//13.5.2020			
			imp.getCanvas().removeMouseListener(MouseEventManagerOJ.getInstance());
			imp.getCanvas().removeMouseMotionListener(MouseEventManagerOJ.getInstance());

			imp.getCanvas().removeMouseListener(imp.getCanvas());
			imp.getCanvas().removeMouseMotionListener(imp.getCanvas());

			imp.getCanvas().addMouseListener(MouseEventManagerOJ.getInstance());
			imp.getCanvas().addMouseMotionListener(MouseEventManagerOJ.getInstance());
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		ImagePlus connectedImp = null;
		double x = 0.0, y = 0.0;
		ImagePlus imp2 = null;
		if (connectedImp != null) {
			imp2 = connectedImp;
			x = e.getX();
			y = e.getY();
		} else {
			Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
			if (win instanceof ImageWindow) {
				imp2 = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
				x = imp2.getCanvas().offScreenXD(e.getX());
				y = imp2.getCanvas().offScreenYD(e.getY());
			}
		}
		if (imp2 != null) {
			int stackIndex = imp2.getCurrentSlice();//24.6.2009
			if (OJ.isValidData() && impLinked(imp2) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {//4.7.2009
				if ((imp2 instanceof CompositeImage) && (((CompositeImage) imp2).getMode() == CompositeImage.COMPOSITE)) {
					int channel = CustomCanvasOJ.getChannel(imp2, stackIndex);
					if (!CustomCanvasOJ.isActiveChannel(imp2, channel - 1)) {
						JOptionPane.showMessageDialog(imp2.getWindow(), "You just tried to add a marker on an invisible stack slice.\nYou must make the channel visible before continuing.", "Marking error", JOptionPane.ERROR_MESSAGE);

						return;
					}
				}
				ToolStateOJ toolState = OJ.getToolStateProcessor().getToolStateObject();
				toolState.mousePressed(imp2.getTitle(), stackIndex, x, y, e.getModifiers());
				if (OJ.getToolStateProcessor().getToolStateObject() instanceof MoveCellStateOJ) {
					EventProcessorOJ.BlockEventsOnDrag = true;
				}
			} else if ((OJ.isValidData() && OJ.getToolStateProcessor().getToolStateObject() instanceof MacroToolStateOJ) && !IJ.spaceBarDown()) {//12.10.2014
				OJ.getToolStateProcessor().getToolStateObject().mousePressed(imp2.getTitle(), stackIndex, x, y, e.getModifiers());
			} else {
				imp2.getCanvas().mousePressed(e);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		pressedX = 0;
		pressedY = 0;
		ImagePlus imp2 = null;
		if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
			imp2 = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
		}
		if (imp2 == null) {
			return;
		}
		if (impLinked(imp2) && (OJ.isValidData()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {
			EventProcessorOJ.BlockEventsOnDrag = false;
			double x = imp2.getCanvas().offScreenXD(e.getX());
			double y = imp2.getCanvas().offScreenYD(e.getY());
			int stackIndex = imp2.getStackIndex(imp2.getChannel(), imp2.getSlice(), imp2.getFrame());
			OJ.getToolStateProcessor().getToolStateObject().mouseReleased(imp2.getTitle(), stackIndex, x, y, e.getModifiers());
		} else {
			imp2.getCanvas().mouseReleased(e);
		}
	}
//propagate mouseEntered to Toolstate.mouseEntered

	public void mouseEntered(MouseEvent e) {
		ImagePlus imp2 = null;
		if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
			imp2 = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
		}
		if (imp2 == null) {
			return;
		}
		if (impLinked(imp2) && (OJ.isValidData()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {
			double x = imp2.getCanvas().offScreenXD(e.getX());
			double y = imp2.getCanvas().offScreenYD(e.getY());
			int stackIndex = imp2.getStackIndex(imp2.getChannel(), imp2.getSlice(), imp2.getFrame());
			OJ.getToolStateProcessor().getToolStateObject().mouseEntered(imp2.getTitle(), stackIndex, x, y, e.getModifiers());
		}
		imp2.getCanvas().mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {

		if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
			ImagePlus imp = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
			if (impLinked(imp) && (OJ.isValidData()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {
				double x = imp.getCanvas().offScreenXD(e.getX());
				double y = imp.getCanvas().offScreenYD(e.getY());
				int stackIndex = imp.getStackIndex(imp.getChannel(), imp.getSlice(), imp.getFrame());
				OJ.getToolStateProcessor().getToolStateObject().mouseExited(imp.getTitle(), stackIndex, x, y, e.getModifiers());
			}
			//else {
			imp.getCanvas().mouseExited(e);
			//}
		}
	}

	public void mouseDragged(MouseEvent e) {
		ImagePlus imp = null;
		if (imp == null) {
			if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
				imp = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();

			}
		}
		if (imp == null) {
			return;
		}
		if (impLinked(imp) && (OJ.isValidData()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {
			double x = imp.getCanvas().offScreenXD(e.getX());
			double y = imp.getCanvas().offScreenYD(e.getY());
			IJ.setInputEvent(e);
			int stackIndex = imp.getStackIndex(imp.getChannel(), imp.getSlice(), imp.getFrame());
			OJ.getToolStateProcessor().getToolStateObject().mouseDragged(imp.getTitle(), stackIndex, x, y, e.getModifiers());
		} else {
			imp.getCanvas().mouseDragged(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
		ImagePlus imp2 = null;

		if (imp2 == null) {
			imp2 = WindowManager.getCurrentImage();
		}
		if (imp2 != null) {
			if (imp2.getCanvas() != null) {
				int sx = e.getX();
				int sy = e.getY();
				int ox = imp2.getCanvas().offScreenX(sx);
				int oy = imp2.getCanvas().offScreenY(sy);
				imp2.getCanvas().setCursor(sx, sy, ox, oy);
			}
		}

		if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
			ImagePlus imp = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
			if(imp == null)//29.9.2021
			    return;
			if (impLinked(imp) && (OJ.isValidData()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE) && (!IJ.spaceBarDown())) {
				double x = imp.getCanvas().offScreenXD(e.getX());
				double y = imp.getCanvas().offScreenYD(e.getY());
				IJ.setInputEvent(e);
				int stackIndex = imp.getStackIndex(imp.getChannel(), imp.getSlice(), imp.getFrame());
				OJ.getToolStateProcessor().getToolStateObject().mouseMoved(imp.getTitle(), stackIndex, x, y, e.getModifiers());
			} else {
				if (imp != null && imp.getCanvas() != null) {
					imp.getCanvas().mouseMoved(e);
				}
			}
		}
	}
}
