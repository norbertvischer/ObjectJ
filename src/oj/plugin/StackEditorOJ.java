/*
 * StackEditorOJ.java
 * -- documented
 *
 * Extends ImageJ's Stack Editor: deleting or adding a slice,  dropping owning markers,
 * reprositioning z of markers in higher slices.
 * Not completely ok for hyperstacks
 */
package oj.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.StackEditor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.plugin.events.StackChangedListenerOJ;
import oj.processor.ImageProcessorOJ;
import oj.project.ImageOJ;

public class StackEditorOJ extends StackEditor {
    /* Intercepts Add and Delete slice if image is linked.
     * Currently, only Delete Slice or Delete Frame is supported,
     * (not delete Channel), and no 5d images are supported.
     * Also, no virtual stack is supported (because it cannot be re-saved).
     * After the slice is actually deleted, all z positions are 
     * adjusted and the user is asked to resave and overwrite 
     * the old image (what he should do).
     */

    public void run(String arg) {

        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            return;
        }

        if ((!OJ.isProjectOpen) || (!ImageProcessorOJ.isFrontImageLinked())) {
            super.run(arg);
            return;
        }

        if (imp.getStack().isVirtual()) {
            IJ.error("Cannot delete slice from linked virtual stack");//because it does not work yet
            return;
        }

        int imgID1 = imp.getID();
        int thisSlice = imp.getCurrentSlice();

        int nChannels1 = imp.getDimensions()[2];//13.8.2013
        int nSlices1 = imp.getDimensions()[3];
        int nFrames1 = imp.getDimensions()[4];
        int stackSize1 = imp.getStackSize();
        if (nSlices1 > 1 && nFrames1 > 1) {
            IJ.error("Not possible on 5D- stack");//because it does not work yet
            return;
        }

        if (arg.equals("add")) {//2.9.2014
            if (stackSize1 == 1) {
                IJ.error("In a linked image, cannot add a slice to non-stack");
                return;
            }
            if (nChannels1 > 1 || (nFrames1 > 1 && nSlices1 > 1) || stackSize1 != thisSlice || stackSize1 == 1) {
                IJ.error("To a linked image, only can add a slice to the end of a non-hyperstack");
                return;
            }
        }
        super.run(arg);//DELETING SLICES
        if (arg.equals("add") && stackSize1 != imp.getStackSize()) {//2.9.2014
            ImageOJ imj = OJ.getData().getImages().getImageByName(imp.getTitle());
            if (imj != null) {
                OJ.getDataProcessor().updateCellsAfterAddSlice(imp.getTitle(), imp.getStackSize());
                imj.setNumberOfFrames(1);
                imj.setNumberOfSlices(imp.getStackSize());
                imj.setNumberOfChannels(1);
            }
        }
        if (arg.equals("delete")) {
            int imgID2 = imp.getID();
            int[] deletedImages;
            int nChannels2 = imp.getDimensions()[2];//13.8.2013
            int nSlices2 = imp.getDimensions()[3];
            int nFrames2 = imp.getDimensions()[4];
            if (nChannels1 != nChannels2) {
                IJ.error("Cannot delete a channel of linked image- please undo via File>Revert");//because it does not work yet
                return;
            }

            if (nSlices2 < nSlices1 || nFrames2 < nFrames1) {
                deletedImages = new int[nChannels2];
                int firstDeleted = ((thisSlice - 1) / nChannels2) * nChannels2 + 1;

                for (int jj = 0; jj < nChannels2; jj++) {
                    deletedImages[jj] = firstDeleted + jj;

                }

                OJ.getDataProcessor().adjustZPositions(imp.getTitle(), deletedImages);

                OJ.getData().getCells().killMarkedCells();
                OJ.getEventProcessor().fireCellChangedEvent();
                boolean showChangeMessage = false;
                ImageOJ imageOj = OJ.getData().getImages().getImageByName(imp.getTitle());
                OJ.getImageProcessor().applyImageGraphics(imp, imageOj, showChangeMessage);
                if (imgID1 == imgID2 && !IJ.isMacro()) {
                    boolean doSave = IJ.showMessageWithCancel("Save", "Decreased stack needs to be re-saved. \n(You should click OK to save now)");
                    if (doSave) {
                        FileSaver fileSaver = new FileSaver(imp);
                        fileSaver.save();

                        //String dir = imp.getOriginalFileInfo().directory;
                        //fileSaver.saveAsTiff(dir + imp.getTitle());
                    }
                }

            }
        }
    }
    static ArrayList stackChangedListeners = new ArrayList();

    public static void addStackChangedListener(StackChangedListenerOJ listener) {
        if (stackChangedListeners.indexOf(listener) < 0) {
            stackChangedListeners.add(listener);

        }
    }

    public static void removeStackChangedListener(StackChangedListenerOJ listener) {
        int index = stackChangedListeners.indexOf(listener);

        if (index > 0) {
            stackChangedListeners.remove(index);

        }
    }

    public static void fireSliceDeleted(ImagePlus imp, int slice) {
        for (int i = 0; i
                < stackChangedListeners.size(); i++) {
            ((StackChangedListenerOJ) stackChangedListeners.get(i)).sliceDeleted(imp, slice);

        }
    }

    public static void fireSliceAdded(ImagePlus imp, int slice) {
        for (int i = 0; i
                < stackChangedListeners.size(); i++) {
            ((StackChangedListenerOJ) stackChangedListeners.get(i)).sliceAdded(imp, slice);

        }
    }

    public static void fireImagesToStack(ImagePlus imp, int slices) {
        for (int i = 0; i
                < stackChangedListeners.size(); i++) {
            ((StackChangedListenerOJ) stackChangedListeners.get(i)).imagesToStack(imp, slices);

        }
    }

    public static void fireStackToImages(ImagePlus imp, int images) {
        for (int i = 0; i
                < stackChangedListeners.size(); i++) {
            ((StackChangedListenerOJ) stackChangedListeners.get(i)).stackToImages(imp, images);

        }
    }

    private void setHeight(int height) {
        final Field[] fields = StackEditor.class.getDeclaredFields();

        for (int i = 0;
                i < fields.length;
                ++i) {
            if ("height".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set((StackEditor) this, Integer.valueOf(height));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    private void setWidth(int width) {
        final Field[] fields = StackEditor.class.getDeclaredFields();

        for (int i = 0;
                i < fields.length;
                ++i) {
            if ("width".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set((StackEditor) this, Integer.valueOf(width));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    private void setNSlices(int nSlices) {
        final Field[] fields = StackEditor.class.getDeclaredFields();

        for (int i = 0;
                i < fields.length;
                ++i) {
            if ("nSlices".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set((StackEditor) this, Integer.valueOf(nSlices));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    private int getNSlices() {
        final Field[] fields = StackEditor.class.getDeclaredFields();

        for (int i = 0;
                i < fields.length;
                ++i) {
            if ("nSlices".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return ((Integer) fields[i].get((StackEditor) this)).intValue();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
        return 0;
    }

    private void setImagePlus(ImagePlus imp) {
        final Field[] fields = StackEditor.class.getDeclaredFields();

        for (int i = 0;
                i < fields.length;
                ++i) {
            if ("imp".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set((StackEditor) this, imp);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    private ImagePlus getImagePlus() {
        final Field[] fields = StackEditor.class.getDeclaredFields();
        final String mName = "imp";
        for (int i = 0;
                i < fields.length;
                ++i) {
            if (mName.equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return (ImagePlus) fields[i].get((StackEditor) this);
                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }

                break;
            }
        }

        UtilsOJ.showException(null, mName);//22.6.2009

        return null;
    }

    private void addImagePlusSlice() {
        final Method[] methods = StackEditor.class.getDeclaredMethods();
        final String mName = "addSlice";
        for (int i = 0;
                i < methods.length;
                ++i) {
            if (mName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    methods[i].invoke((StackEditor) this);//20.1.2009, /*(Object)*/null);//9.1.2009

                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }
                return;
            }
        }

        UtilsOJ.showException(null, mName);//22.6.2009
    }

    private void deleteImagePlusSlice() {
        final Method[] methods = StackEditor.class.getDeclaredMethods();
        final String mName = "deleteSlice";
        for (int i = 0;
                i < methods.length;
                ++i) {
            if (mName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    methods[i].invoke((StackEditor) this);//20.1.2009, (Object)null);//9.1.2009
                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }
                return;
            }
        }

        UtilsOJ.showException(null, mName);//22.6.2009
    }
}
