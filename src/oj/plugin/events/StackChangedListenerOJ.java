/*
 * StackListenerOJ.java
 * -- documented
 *
 * This class registers for stack changing events
 * like sliceDeleted, sliceAdded on ImageJ.
 * Thus it can receive such events that are broadcasted by
 * ImageJ.
 */

package oj.plugin.events;

import ij.ImagePlus;
import oj.OJ;
import oj.project.ImageOJ;
import oj.plugin.StackEditorOJ;


public class StackChangedListenerOJ {

    public StackChangedListenerOJ() {
        StackEditorOJ.addStackChangedListener(this);
    }

    public void sliceDeleted(ImagePlus imp, int slice) {
        ImageOJ imj = OJ.getData().getImages().getImageByName(imp.getTitle());

        if (imj != null) {
            String imageName=imp.getTitle();//11.4.2009
            OJ.getDataProcessor().deleteCellsAfterDeleteSlice( imageName,  slice) ;
            imj.setNumberOfSlices(imj.getNumberOfSlices() - 1);
        }
    }

    public void sliceAdded(ImagePlus imp, int slice) {//11.4.2009
        ImageOJ imj = OJ.getData().getImages().getImageByName(imp.getTitle());
        if (imj != null) {
            OJ.getDataProcessor().updateCellsAfterAddSlice(imp.getTitle(), slice);
            imj.setNumberOfSlices(imj.getNumberOfSlices() + 1);
        }
    }

    public void imagesToStack(ImagePlus imp, int slices) {
        System.out.println("Images to stack: " + Integer.toString(slices));
    }

    public void stackToImages(ImagePlus imp, int images) {
        System.out.println("Stack to images: " + Integer.toString(images));
    }
}