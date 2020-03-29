/*
 * ImageListenerOJ.java
 * -- documented
 */
package oj.plugin.events;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PlotWindow;
import java.io.File;
import oj.OJ;
import oj.project.ImageOJ;
import oj.gui.KeyEventManagerOJ;
import oj.gui.MouseEventManagerOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.ToolStateOJ;

/**
 * This class registers for image changing events like ImageOpened, ImageClosed
 * on ImageJ. Thus it can receive such events that are broadcasted by ImageJ.
 */
public class IjImageChangedListenerOJ implements ImageListener {

	 int previousID;
	/**
	 * Checks if path is equal to a linked path and applies graphics. However, if
	 * this is the second instance of a linked image, it is closed again
	 */
	public void imageOpened(ImagePlus imp) {

		if (OJ.isValidData() && (imp.getOriginalFileInfo() != null) && (imp.getOriginalFileInfo().directory != null)) {
			String file_name = imp.getTitle();//file called zip, image called tif
			ImageOJ imageOJ = OJ.getData().getImages().getImageByName(file_name);
			if (imageOJ == null) {
				KeyEventManagerOJ.getInstance().replaceKeyListener(imp);//also listen to non-linked images because of the tool macro
				MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
				return;
			}
			boolean isPlot = imp.getWindow() instanceof PlotWindow;
			if(isPlot ){
				OJ.getData().getImages().removeImage(imageOJ);
				IJ.showMessage(file_name + " is a Plot and cannot be linked");
				return;
			}
			File imageDirectory = new File(imp.getOriginalFileInfo().directory);
			File projectDirectory = new File(OJ.getData().getDirectory());

			if (!projectDirectory.equals(imageDirectory)) {
				KeyEventManagerOJ.getInstance().replaceKeyListener(imp);//also listen to non-linked images because of the tool macro, 24.10.2010
				MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
				return;
			}
			if (OJ.getImageProcessor().closeDuplicates(file_name)) {
				return;
			}
			boolean showChangedMessage = true;
			OJ.getImageProcessor().applyImageGraphics(imp, imageOJ, showChangedMessage);
			imageOJ.setImagePlus(imp);
			KeyEventManagerOJ.getInstance().replaceKeyListener(imp);
			MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
		}
		KeyEventManagerOJ.getInstance().replaceKeyListener(imp);//also listen to non-linked images because of the tool macro, 24.10.2010
		MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
	}

	public void imageClosed(ImagePlus imp) {
	}

	//Plot does not send "Open" event, so we use a one-time Update event. 
	//Important to block ObjectJ shortcut keys when creating text rois.
	public void imageUpdated(ImagePlus imp) {
		
		boolean isPlot = imp.getWindow() instanceof PlotWindow;
		if (isPlot && imp.getID() != previousID) {
			previousID = imp.getID();
			imageOpened(imp);
		}
	}
}
