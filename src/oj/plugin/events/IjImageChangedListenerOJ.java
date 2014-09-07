/*
 * ImageListenerOJ.java
 * -- documented
 */
package oj.plugin.events;

import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import java.awt.Frame;
import java.io.File;
import oj.OJ;
import oj.project.ImageOJ;
import oj.gui.KeyEventManagerOJ;
import oj.gui.MenuManagerOJ;
import oj.gui.MouseEventManagerOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.ToolStateOJ;

/**
 * This class registers for image changing events like ImageOpened, ImageClosed
 * on ImageJ. Thus it can receive such events that are broadcasted by ImageJ.
 */
public class IjImageChangedListenerOJ implements ImageListener {

    /**
     * Checks if path is equal to a linked path and applie graphics. However, if
     * this is the second instance of a linked image, it is closed again
     */
    public void imageOpened(ImagePlus imp) {



        if (OJ.isValidData() && (imp.getOriginalFileInfo() != null) && (imp.getOriginalFileInfo().directory != null)) {
            String file_name = imp.getOriginalFileInfo().fileName;
            ImageOJ imageOJ = OJ.getData().getImages().getImageByName(file_name);
            if (imageOJ == null) {
                KeyEventManagerOJ.getInstance().replaceKeyListener(imp);//also listen to non-linked images because of the tool macro, 24.10.2010
                MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
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

            imageOJ.setID(imp.getID());
            imageOJ.setImagePlus(imp);
            KeyEventManagerOJ.getInstance().replaceKeyListener(imp);
            MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
        }
        KeyEventManagerOJ.getInstance().replaceKeyListener(imp);//also listen to non-linked images because of the tool macro, 24.10.2010
        MouseEventManagerOJ.getInstance().replaceMouseListener(imp);
    }

    public void imageClosed(ImagePlus imp) {
        String image_name = imp.getTitle();
        if (OJ.isProjectOpen) {
            ImageOJ imageOJ = OJ.getData().getImages().getImageByName(image_name);
            if (imageOJ != null) {
                //3.11.2013
                ToolStateOJ state = OJ.getToolStateProcessor().getToolStateObject();
                if (state != null && (state instanceof CreateCellStateOJ)) {
                    ((CreateCellStateOJ) state).closeCell();//3.11.2013        
                }

                //MenuManagerOJ.getInstance().imageClosed(image_name);
                OJ.getImageProcessor().removeFromOpenedImages(image_name);
                imageOJ.setID(0);
                imageOJ.setImagePlus(null);

            }
        }
    }

    public void imageUpdated(ImagePlus imp) {
    }
}
