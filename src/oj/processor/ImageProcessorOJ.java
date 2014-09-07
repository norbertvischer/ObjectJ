/*
 * ImageProcessorOJ.java
 *
 * manages linked images in the project window, such as adding, deleting, and updating properties of linked images
 * 
 */
package oj.processor;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.*;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.util.Tools;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.graphics.CustomCanvasOJ;
import oj.gui.MouseEventManagerOJ;
import oj.plugin.GlassWindowOJ;
import oj.plugin.SimpleCommandsOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.ImageChangedListener2OJ;
import oj.processor.events.YtemChangedListenerOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.project.ImageOJ;
import oj.project.ImagesOJ;
import oj.util.ImageJAccessOJ;
import oj.util.ImageWindowUtilsOJ;
import oj.util.TiffFileInfoOJ;

public class ImageProcessorOJ implements ImageChangedListener2OJ, DropTargetListener {

    private Hashtable openedImages = new Hashtable();//ImageOJ <-> Name

    public ImageProcessorOJ() {
        OJ.getEventProcessor().addImageChangedListener(this);
    }

    /**
     * If image with this name is open and linked, it is closed.
     *
     */
    public void closeImage(String name) {//9.7.2013
        ImagePlus imp = getOpenLinkedImage(name);
        if (imp != null) {
            imp.close();
            removeFromOpenedImages(name);
        }
    }

//   public void closeImageOld(String name) {
//        ImageWindow imgw = getOpenLinkedImageWindow(name);
//        if (imgw != null) {
//            imgw.close();
//            removeFromOpenedImages(name);
//        }
//    }

    /*
     * not used, does not work
     */
    private String resolveAlias(String dir, String fName) {
        File f = new File(dir, fName);
        String fullpath = f.getPath();
        File f2 = new File(fullpath);
        try {
            String path = f2.getCanonicalPath();

            return path;

        } catch (IOException e) {
            return "An error occured while resolving alias";
        }
    }

    /**
     * Opens linked image if it is not open yet. Says "Bring Project Folder to
     * Front" if file is not found
     *
     * @param imageName
     */
    public void openImage(String imageName) {
        //check for the file in the project directory
        ImageOJ imageOJ = OJ.getData().getImages().getImageByName(imageName);
        if (imageOJ != null) {
            if (!isInProjectDir(imageOJ.getFilename())) {

                String msg = "The selected image \n\"" + imageName + "\" \nwas not found in the project directory.";//8.7.2009
                if (ij.IJ.isMacro()) {
                    ImageJAccessOJ.InterpreterAccess.interpError(msg);
                    return;

                } else {
                    GenericDialog gd = new GenericDialog("Linked Images");
                    gd.addMessage(msg);
                    if (ij.IJ.isMacintosh()) {
                        gd.setOKLabel("Bring Project Folder to Front");
                    } else if (ij.IJ.isWindows()) {
                        gd.setOKLabel("Bring Project Folder to Front");
                    } else {
                        ij.IJ.error(msg);
                    }
                    gd.showDialog();
                    if (!gd.wasCanceled()) {
                        UtilsOJ.showInFinderOrExplorer();
                    }
                }
                return;

            }
            if (!isLinkedImageOpen(imageName)) {
                ImagePlus imp = checkForOpenIJ(imageName);

                if (imp == null) {
                    String dir = OJ.getData().getDirectory();
                    String fName = imageOJ.getFilename();
                    imp = new Opener().openImage(dir, fName);//30.6.2013
                    imp.show();//30.6.2013                  
                    IJ.selectWindow(imp.getID());//7.12.2013
                }
                Calibration cal = imp.getCalibration();//10.7.2009
                if (cal.pixelHeight == 1 && cal.pixelWidth == 1) {
                    if ((imageOJ.getVoxelSizeX() != 1 || imageOJ.getVoxelSizeY() != 1)) {
                        cal.pixelWidth = imageOJ.getVoxelSizeX();
                        cal.pixelHeight = imageOJ.getVoxelSizeY();
                        cal.setUnit(imageOJ.getVoxelUnitX());
                        imp.setCalibration(cal);
                    } else {
                        imageOJ.setVoxelSizeX(cal.pixelWidth);
                        imageOJ.setVoxelSizeY(cal.pixelHeight);
                    }
                }

            } else /*already open*/ {
                ImageWindow imgw = getOpenLinkedImageWindow(imageName);
                if (imgw != null) {//i.e. not opened in Batch
                    imgw.setExtendedState(Frame.NORMAL);
                    imgw.toFront();
                }
            }
        } else {
            IJ.showMessage("The selected image \"" + imageName + "\" does not exist.");
        }
    }

    /**
     * Links an image already opened in the ImageJ
     *
     * @param imp the image opened by the ImageJ
     */
    public void addImage(ImagePlus imp) {
        if (imp == null) {
            return;
        }
        //if (imp != null) {
        //check for the file in the project directory
        String fileName = imp.getTitle();
        String imageName = fileName;
        if (!isInProjectDir(fileName)) {
            IJ.showMessage("Error: Image \"" + imageName + "\" is not saved in the project directory.");
            return;

        }
        //check if the image is already linked
        boolean showChangeMessage = true;
        if (isLinked(imageName)) {
            IJ.showMessage("Image \"" + imageName + "\" is already linked.");

            if (!isLinkedImageOpen(imageName)) {
                applyImageGraphics(imp, OJ.getData().getImages().getImageByName(imageName), showChangeMessage);
            } else {
                ImageWindow imgw = getOpenLinkedImageWindow(imageName);
                if (imgw != null) {//i.e. not opened in Batch
                    imgw.setExtendedState(Frame.NORMAL);
                    imgw.toFront();
                }
            }

            return;
        } else {
            ImageOJ imj = OJ.getData().getImages().getImageByName(imageName);
            if (imj == null) {
                //imp.setTitle(fileName);+++ 9.7.2013
                imj = new ImageOJ(imageName, imp);//27.8.2010
                updateImageProperties(OJ.getData().getDirectory(), imj);
                OJ.getData().getImages().addImage(imj);
            }

            applyImageGraphics(imp, imj, showChangeMessage);
            //  }

        }
    }

    /**
     * the function links an image after a drag and drop action
     *
     * @param imageName the name of the image dropped on the application
     */
    public void addImage(String fileName, boolean shouldOpen, boolean showAlreadyLinkedMessage) {
        String imageName = fileName;
        //check for the file in the project directory
        boolean valid = isInProjectDir(fileName);//23.2.2010 wrong message when it is a directory
        if (!valid) {
            String msg = "Before linking image \n \"" + imageName + "\"\n to the project, you need to move it to the project folder.";

            GenericDialog gd = new GenericDialog("Linked Images");
            gd.addMessage(msg);
            if (ij.IJ.isMacintosh()) {
                gd.setOKLabel("Bring Project Folder to Front");
            } else if (ij.IJ.isWindows()) {
                gd.setOKLabel("Bring Project Folder to Front");
            } else {
                ij.IJ.error(msg);
            }
            gd.showDialog();
            if (!gd.wasCanceled()) {
                UtilsOJ.showInFinderOrExplorer();
            }
            return;

        }
        //check if the image is already linked

        if (isLinked(imageName)) {
            if (showAlreadyLinkedMessage) {
                IJ.showMessage("This is already linked to the project.");
            }

            if (!isLinkedImageOpen(imageName)) {
            } else {
                ImageWindow imgw = getOpenLinkedImageWindow(imageName);
                if (imgw != null) {//i.e. not opened in Batch
                    imgw.setExtendedState(Frame.NORMAL);
                    imgw.toFront();
                    ImagePlus imp = imgw.getImagePlus();
                    ImageOJ imOj = OJ.getData().getImages().getImageByName(imageName);
                    if (imOj != null && imp != null) {
                        OJ.getImageProcessor().applyImageGraphics(imp, imOj, true);//3.11.2013
                    }

                }
            }

            return;
        } else {
            File file = new File(OJ.getData().getDirectory(), fileName);
            String path = file.getAbsolutePath();//16.3.2013

            if (!shouldOpen) {//5.4.2013

                ImageOJ imj = new ImageOJ(imageName, null);
                OJ.getData().getImages().addImage(imj);
                updateImageProperties(OJ.getData().getDirectory(), imj);
            } else {
                ImagePlus imp = null;
                imp = ImageWindowUtilsOJ.isOpen(path);
                if (imp == null) {
                    imp = new Opener().openImage(path);
                }
                if (imp != null) {
                    imp.show();//30.12.2013
                    ImageOJ imj = OJ.getData().getImages().getImageByName(imageName);
                    if (imj == null) {
                        imp.setTitle(fileName);
                        imj = new ImageOJ(imageName, imp);//27.8.2010
                        updateImageProperties(OJ.getData().getDirectory(), imj);
                        OJ.getData().getImages().addImage(imj);

                        OJ.getImageProcessor().applyImageGraphics(imp, imj, false);//16.3.2013
                        imj.setID(imp.getID());//16.3.2013
                        imj.setImagePlus(imp);//16.3.2013

                    }
                    imp.show();//30.12.2013

                }
            }
        }
    }

//    private void updateOjImageName(int index, String imageName) {
//        ImageOJ image = OJ.getData().getImages().getImageByName(imageName);
//        image.setName(filename);
//        image.setFilename(filename);
//        OJ.getData().getImages().updateImageName(imageName, image.getName());
//    }
    
//    public void renameImage(int index) {
//
//        ImagesOJ images = OJ.getData().getImages();
//        // ImagePlus impl = WindowManager.getCurrentImage();
//        ImageOJ img = images.getImageByIndex(index);
//        String oldName = img.getFilename();
//        String error = SimpleCommandsOJ.renameImageAndFile(oldName, "");
//        if (!error.equals("")) {
//            IJ.showMessage(error);
//        }
//
//    }

    public void propagateScale(int index) {

        ImagesOJ images = OJ.getData().getImages();

        double proposed = 1.0;
        String unit = "um";
        String title = "";
        int nImages = images.getImagesCount();
        ImagePlus impl = WindowManager.getCurrentImage();
        ImageOJ thisImg;
        if (index >= nImages || index < 0) {
            if (impl != null && ImageProcessorOJ.isFrontImageLinked()) {
                title = impl.getTitle();
                thisImg = images.getImageByName(title);
                proposed = thisImg.getVoxelSizeX();
                unit = thisImg.getVoxelUnitX();
            } else {
                for (int im = 0; im < nImages; im++) {
                    ImageOJ imj = images.getImageByIndex(im);
                    if (imj.getVoxelSizeX() != 1 && proposed == 1.0) {
                        proposed = imj.getVoxelSizeX();
                        unit = imj.getVoxelUnitX();
                    }
                }
            }
        } else {
            ImageOJ imj = images.getImageByIndex(index);
            title = imj.getName();
            proposed = imj.getVoxelSizeX();
            unit = imj.getVoxelUnitX();
        }
        double scale = 1 / proposed;
        GenericDialog gd = new GenericDialog("Scale All Linked Images");
        gd.addMessage("Image = " + title);
        gd.addNumericField("New Scale", scale, 3, 9, "px/unit");
        gd.addStringField("Unit:", unit);
        //gd.addCheckbox("Apply to all linked images", false);
        gd.setOKLabel("Propagate Scale");
        gd.showDialog();
        if (gd.wasOKed()) {
            //boolean scaleAll = gd.getNextBoolean();
            double newPxSize = 1 / gd.getNextNumber();
            if (newPxSize <= 0) {
                newPxSize = 1;
            }
            unit = gd.getNextString();
            for (int im = 0; im < nImages; im++) {
                ij.IJ.showProgress(im, nImages);

                ImageOJ imj = images.getImageByIndex(im);
                //if (scaleAll || im == index) 
                {
                    String name = imj.getFilename().toLowerCase();

                    boolean isTiff = name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".tiff");
                    if (isTiff) {
                        if ((Math.abs(1 - imj.getVoxelSizeX() / newPxSize) + Math.abs(1 - imj.getVoxelSizeY() / newPxSize)) > 1e-4) {//allow 0.1% difference
                            if (imj.isFileExists()) {
                                int expectedSlices = imj.getStackSize();
                                //long bytesNeeded = imj.bytesNeeded();
                                //long freeMem = Runtime.getRuntime().freeMemory();

                                String dir = OJ.getData().getDirectory();
                                String path = dir + name;
                                boolean isOpen = imj.getImagePlus() != null;
                                boolean isVirtual = false;
                                if (isOpen) {
                                    ImagePlus linkedImp = imj.getImagePlus();
                                    isVirtual = (linkedImp.getStackSize() > 1 && linkedImp.getStack().isVirtual());
                                    if (isVirtual) {
                                        linkedImp.close();
                                    }

                                }
                                OJ.getImageProcessor().openImage(imj.getName());
                                ImagePlus imp = imj.getImagePlus();
                                if (expectedSlices != imp.getStackSize()) {
                                }
                                Calibration cal = imp.getCalibration();
                                cal.pixelHeight = newPxSize;
                                cal.pixelWidth = newPxSize;
                                cal.setXUnit(unit);
                                imp.setCalibration(cal);
                                imp.repaintWindow();
                                boolean outofMem = IJ.maxMemory() < (IJ.currentMemory() + 2e7);//20MB
                                if (!outofMem) {
                                    FileSaver fs = new FileSaver(imp);
                                    if (imp.getStackSize() > 1) {
                                        fs.saveAsTiffStack(path);
                                    } else {
                                        fs.saveAsTiff(path);
                                    }
                                }
                                if (!isOpen) {
                                    imp.close();//9.2.2014
                                    //IJ.doCommand("Close");
                                }
                                if (isOpen && isVirtual) {
                                    imp.close();//9.2.2014
                                    //IJ.doCommand("Close");
                                    //OJ.getImageProcessor().openImageVirtually(imj.getName());
                                }
                            }
                        }
                    }
                    imj.setVoxelSizeX(newPxSize);
                    imj.setVoxelSizeY(newPxSize);
                    imj.setVoxelUnitX(unit);
                }
            }
            OJ.getDataProcessor().recalculateResults();
        }
    }

    /**
     * Links all images in the projct folder. Does not go into subfoldeers, and
     * does not link .zip, .mov or .avi
     */
    public void linkAllImages() {
        File projectDirectory = new File(OJ.getData().getDirectory());

        File[] files = projectDirectory.listFiles();
        int nFiles = files.length;
        String[] fileNames = new String[nFiles];//7.2.2014
        for (int jj = 0; jj < nFiles; jj++) {
            fileNames[jj] = files[jj].getName().toLowerCase();
        }
        int[] indexes = Tools.rank(fileNames);

        for (int jj = 0; jj < nFiles; jj++) {
            int index = indexes[jj];
            ij.IJ.showProgress(jj, nFiles);
            ij.IJ.showStatus("" + jj + "/" + nFiles);
            File file = files[index];
            String name = file.getName().toLowerCase();
            if (!file.isDirectory() && !file.isHidden()) {
                String[] allowedExtensions = ".tif .tiff .jpg .jpeg .gif .png .bmp".split(" ");
                for (int kk = 0; kk < allowedExtensions.length; kk++) {
                    if (name.endsWith(allowedExtensions[kk])) {
                        addImage(files[index].getName(), false, false);
                    }
                }
            }

        }
        int count = OJ.getData().getImages().getImagesCount();
        ij.IJ.showStatus("" + count + " images are linked");
    }

    /**
     * Returns true if file with this name is in project directory and visible
     *
     * @param imageName
     * @return
     */
    private boolean isInProjectDir(String imageName) {
        String dir = OJ.getData().getDirectory();
        File file = new File(dir, imageName);

        return (file.exists() && file.isFile() && !file.isHidden());
    }

    /**
     * @param imageName
     * @return true if name is among linked images
     */
    private boolean isLinked(String imageName) {
        return OJ.getData().getImages().getImageByName(imageName) != null;
    }

    public boolean isLinked(ImagePlus imp) {
        return (isLinked(imp.getTitle()) && isInProjectDir(imp.getTitle()));
    }

    /**
     * @param imageName
     * @return true if linked image is open
     */
    private boolean isLinkedImageOpen(String imageName) {//true if
        return getOpenLinkedImage(imageName) != null;//9.7.2013

        //return getOpenLinkedImageWindow(imageName) != null;
    }

    private ImagePlus checkForOpenIJ(String imageName) {
        ImagePlus imp = WindowManager.getImage(imageName);//only picks the first one!
        if (imp != null) {
            if (imp.getOriginalFileInfo() == null) {
                return null;
            }
            String dir = imp.getOriginalFileInfo().directory;
            if (dir == null) {
                return null;
            }
            File imageDirectory = new File(imp.getOriginalFileInfo().directory);
            File projectDirectory = new File(OJ.getData().getDirectory());
            if ((imp != null) && (projectDirectory.equals(imageDirectory))) {
                return imp;
            }
        }
        return null;
    }

    /**
     * Replaces canvas by extended CustomCanvas if imp has a window
     */
    /* - compares stack dimensions (ask user if not matching)
     * - create new CustomCanvasOJ
     * - adds more listeners (such as YtemDefChangesListener) 
     * - adjust CanvasOJ's srcrect from ImageCanvas' srcrect 
     * - if Imp does not have a window, create a new one 
     * - remove old canvas add myCanvas' Layout add my
     * canvas
     */
    public void applyImageGraphics(ImagePlus imp, ImageOJ imOj, boolean showChangeMessage) {
        if (imp == null) {
            IJ.showMessage("Error", "ImageProcessorOJ.applyImageGraphics failed. The ImagePlus is null.");
            return;
        }
        if (imOj == null) {
            IJ.showMessage("Error", "ImageProcessorOJ.applyImageGraphics failed. The ImageOJ is null.");
            return;
        }

        boolean outofMem = false;
        int slicesA = imp.getNSlices();
        int framesA = imp.getNFrames();
        int channelsA = imp.getNChannels();
        int slicesB = imOj.getNumberOfSlices();
        int framesB = imOj.getNumberOfFrames();
        int channelsB = imOj.getNumberOfChannels();
        if (slicesA != slicesB || framesA != framesB || channelsA != channelsB) {
            // if ((imp.getNSlices() != imOj.getNumberOfSlices()) || (imp.getNFrames() != imOj.getNumberOfFrames()) || (imp.getNChannels() != imOj.getNumberOfChannels())) {
            boolean doChange = true;

            outofMem = IJ.maxMemory() < (IJ.currentMemory() + 2e7);//20MB
            if (outofMem) {
                IJ.error("out of memory");

            }

            if (showChangeMessage && !outofMem && slicesB > -1) {//14.8.2011
                String s2 = "Image: " + imp.getTitle();
                s2 += "\nDifferent dimensions between image file and project file:";
                s2 += "\nImage file (channels-slices-frames:   " + channelsA + "-" + slicesA + "-" + framesA;
                s2 += "\nProject file (channels-slices-frames:   " + channelsB + "-" + slicesB + "-" + framesB;
                s2 += "\n \nAdjust project data?";

                GenericDialog gd = new GenericDialog("Synchronize Project File");
                gd.addMessage(s2);
                gd.showDialog();
                doChange = !gd.wasCanceled();
                // IJ.showMessage("There are differences in image properties (slices, frames and channels count) and project data. Project data are now adjusted");
            }
            if (doChange && !outofMem) {
                int slices = imp.getNSlices();
                imOj.setNumberOfSlices(slices);
                int frames = imp.getNFrames();
                imOj.setNumberOfFrames(frames);
                int channels = imp.getNChannels();
                imOj.setNumberOfChannels(channels);
            }
        }
        ImageCanvas ic = imp.getCanvas();
        if (imp.getWindow() == null) {
            return;//9.7.2013
        }
        CustomCanvasOJ myCanvas = new CustomCanvasOJ(imp, OJ.getData(), imOj.getName());

        myCanvas.updateCanvas(ic, imp);
        ImageWindow imgw = imp.getWindow();
//        if (imgw == null) {
//            if (imp.getStackSize() > 1) {
//                imgw = new StackWindow(imp, myCanvas);
//            } else {
//                imgw = new ImageWindow(imp, myCanvas);
//            }
//
//        } else {
        ImageWindowUtilsOJ.setImageCanvas(imgw, myCanvas);
        imgw.remove(ic);
        imgw.setLayout(new ImageLayout(myCanvas));
        if (imgw instanceof StackWindow) {
            imgw.add(myCanvas, 0);
        } else {
            imgw.add(myCanvas);
        }

        imgw.validate();
        //}

        MouseEventManagerOJ.getInstance().replaceMouseListener(imp);

        imp.getWindow().getComponent(0).requestFocus();
        imp.setTitle(imOj.getName());
        addToOpenedImages(imp);
    }

    public static String extractImageName(
            String filename) {
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index > 0) {
                return filename.substring(0, index);
            } else {
                return filename;
            }

        } else {
            return null;
        }

    }

    public static ImagePlus getCurrentImage() {
        return IJ.getImage();
    }

    public static ImageWindow getCurrentImageOld() {
        ImagePlus imp = IJ.getImage();
        if (imp != null) {
            return imp.getWindow();
        } else {
            return null;
        }
    }

    public static boolean isFrontImageLinked() {
        //ImagePlus imp = IJ.getImage();

        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return false;
        }
        String title = imp.getTitle();
        FileInfo fileInfo = imp.getOriginalFileInfo();
        if (fileInfo == null) {
            return false;
        }
        String dir = fileInfo.directory;
        if (!oj.OJ.getData().getDirectory().equals(dir)) {
            return false;
        }
        ImagesOJ images = OJ.getData().getImages();
        int index = images.getIndexOfImage(title);
        if (index == -1) {
            return false;
        }
        return true;
    }

    static String getCurrentImageName() {
        //ImagePlus imp = IJ.getImage();//+++++1.7.2013
        ImagePlus imp = WindowManager.getCurrentImage();

        if (imp != null) {
//            return ImageProcessorOJ.extractImageName(imp.getTitle());
            return imp.getTitle();
        } else {
            return null;
        }

    }

    public void imageChanged(ImageChangedEventOJ evt) {
        if (evt.getOperation() == ImageChangedEventOJ.IMAGE_DELETED) {
            closeImage(evt.getName());
        }

    }

    public void applyImageMarkers() {
        for (int i = 0; i
                < OJ.getData().getImages().getImagesCount(); i++) {
            ImageOJ imj = OJ.getData().getImages().getImageByIndex(i);
            ImagePlus imp = checkForOpenIJ(imj.getName());
            boolean showChangeMessage = true;
            if (imp != null) {
                applyImageGraphics(imp, imj, showChangeMessage);
            }

        }
    }

    public void updateImagesProperties() {
        String dirname = OJ.getData().getDirectory();
        for (int i = 0; i
                < OJ.getData().getImages().getImagesCount(); i++) {
            ImageOJ image = OJ.getData().getImages().getImageByIndex(i);
            updateImageProperties(dirname, image);
        }

    }

    /**
     * if image is open, image properties are copied to ImageOJ. if image is not
     * open and it is TIF, tiff properties are read from disk and copied to
     * ImageOJ. otherwise nothing happens. Why do we need all this?
     */
    public void updateImageProperties(String directory, ImageOJ image) {
        ImagePlus imp = WindowManager.getImage(image.getName());
        if (imp != null) {
            applyImageProperties(imp, image);
            ///30.6.2012 OJ.getEventProcessor().fireImageChangedEvent(image.getName(), ImageChangedEventOJ.IMAGE_EDITED);
        } else {//disabled 4.9.2010
            String filename = image.getFilename();
            File file = new File(directory, filename);
            if (file.exists() && true) {//9.4.2004
                TiffFileInfoOJ tiffInfoDecoder = new TiffFileInfoOJ(directory, filename);
                FileInfo fileInfo = tiffInfoDecoder.getTiffFileInfo();
                if (fileInfo != null) {
                    applyImageProperties(fileInfo, image);
                    ///30.6.2012 OJ.getEventProcessor().fireImageChangedEvent(image.getName(), ImageChangedEventOJ.IMAGE_EDITED);
                }

            }
        }
    }

    /**
     * used for synchronization: copy image properties into the ojj project move
     * information from open imp to ImageOj image
     *
     * @param imp
     * @param image
     */
    public void applyImageProperties(ImagePlus imp, ImageOJ image) {
        //set image size

        image.setWidth(imp.getWidth());
        image.setHeight(imp.getHeight());
        image.setNumberOfSlices(imp.getNSlices());
        image.setNumberOfFrames(imp.getNFrames());
        image.setNumberOfChannels(imp.getNChannels());
        //imoj.setBitDepth(imp.getBitDepth());

        //set image calibration
        String unit = imp.getCalibration().getUnit();
        if (unit.equals(IJ.micronSymbol + "m")) {
            unit = "um";
        } else if (unit.equals("" + IJ.angstromSymbol)) {
            unit = "A";
        }

        image.setVoxelUnitX(unit);
        image.setVoxelUnitY(unit);
        image.setVoxelUnitZ(unit);

        double newX = ((double) ((int) (imp.getCalibration().pixelWidth * 1E6))) / 1E6;
        double newY = ((double) ((int) (imp.getCalibration().pixelHeight * 1E6))) / 1E6;
        double newZ = ((double) ((int) (imp.getCalibration().pixelDepth * 1E6))) / 1E6;
        boolean recalcFlag = newX != image.getVoxelSizeX() || newY != image.getVoxelSizeY() || newZ != image.getVoxelSizeZ();

        image.setVoxelSizeX(newX);
        image.setVoxelSizeY(newY);
        image.setVoxelSizeZ(newZ);
        //set frame settings
        image.setFrameInterval((int) imp.getCalibration().frameInterval);
        image.setFrameRateUnit(imp.getCalibration().getTimeUnit());
        if (recalcFlag) {//26.11.2013
            OJ.getData().getResults().recalculate();
        }
        image.setChanged(true);
        //imp.changes = true; removed: 4.11.2013
    }

    //move information from saved tiff to ImageOj image
    private void applyImageProperties(FileInfo fileInfo, ImageOJ image) {
        //should be a static method
        Properties props = new FileOpener(fileInfo).decodeDescriptionString(fileInfo);
        int newWidth = fileInfo.width;
        int newHeight = fileInfo.height;
        image.setWidth(newHeight);
        image.setWidth(newWidth);

        double newX = ((double) ((int) (fileInfo.pixelWidth * 1E6))) / 1E6;
        double newY = ((double) ((int) (fileInfo.pixelHeight * 1E6))) / 1E6;
        double newZ = ((double) ((int) (fileInfo.pixelDepth * 1E6))) / 1E6;
        boolean recalcFlag = newX != image.getVoxelSizeX() || newY != image.getVoxelSizeY() || newZ != image.getVoxelSizeZ();
        image.setVoxelSizeX(newX);
        image.setVoxelSizeY(newY);
        image.setVoxelSizeZ(newZ);

        image.setVoxelUnitX((fileInfo.unit != null) ? fileInfo.unit : "");
        image.setVoxelUnitY((fileInfo.unit != null) ? fileInfo.unit : "");
        image.setVoxelUnitZ((fileInfo.unit != null) ? fileInfo.unit : "");
        //n_3.3.2009
        image.setNumberOfSlices(1);
        image.setNumberOfFrames(1);
        image.setNumberOfChannels(1);

        if (props != null) {//n_3.3.2009
            image.setNumberOfSlices(getInteger(props, "slices") == 0 ? 1 : getInteger(props, "slices"));
            image.setNumberOfFrames(getInteger(props, "frames") == 0 ? 1 : getInteger(props, "frames"));
            image.setNumberOfChannels(getInteger(props, "channels") == 0 ? 1 : getInteger(props, "channels"));

            //set frame settings
            image.setFrameInterval(getDouble(props, "finterval"));
            image.setFrameRateUnit(props.getProperty("tunit", "sec"));
        } else {//14.8.2011
            String title = image.getName().toLowerCase();
            if (title.endsWith(".tif") || title.endsWith(".tiff")) {
                image.setNumberOfSlices(-1);
            }

        }

        image.setChanged(true);
        if (recalcFlag) {
            OJ.getData().getResults().recalculate();
        }
    }

    private Double getNumber(Properties props, String key) {
        String s = props.getProperty(key);
        if (s != null) {
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    private int getInteger(Properties props, String key) {
        Double n = getNumber(props, key);
        return n != null ? n.intValue() : 0;
    }

    private double getDouble(Properties props, String key) {
        Double n = getNumber(props, key);
        return n != null ? n.doubleValue() : 0.0;
    }

    private boolean getBoolean(Properties props, String key) {
        String s = props.getProperty(key);
        return s != null && s.equals("true") ? true : false;
    }

    //private static ImageManagerOJ instance;
    /**
     * add an image name to the list of linked images
     */
    public void addToOpenedImages(ImagePlus imp) {
        if (imp == null) {
            return;//6.7.2013
        }            //ij.IJ.showMessage("add to opened image Nullpointer");
        //FileInfo fInfo = imgw.getImagePlus().getFileInfo();//13.10.2010
        String fName = imp.getTitle();
        //fInfo.fileName;
        openedImages.put(fName, imp);
    }

    /**
     * @return link index of current window, or -1 if it is not linked
     */
    public int getCurrentImageIndex() {

        ImagePlus imp = WindowManager.getCurrentImage();
        //-----1.7.2013
        if (imp == null) {
            return -1;
        }
        String name = imp.getTitle();
        FileInfo fInfo = imp.getOriginalFileInfo();//24.10.2010

        String projectDir = OJ.getData().getDirectory();
        if (fInfo != null && fInfo.directory != null && fInfo.directory.equals(projectDir)) {//25.5.2013

            ImageOJ img = OJ.getData().getImages().getImageByName(name);
            if (img != null) {
                for (int i = 0; i < OJ.getData().getImages().getImagesCount(); i++) {
                    if (OJ.getData().getImages().getImageByIndex(i).getName().equals(name)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * @param name
     * @return linked ImageWindow with this name, or null if not found
     */
    public ImageWindow getOpenLinkedImageWindow(String name) {
        ImagePlus imp = getOpenLinkedImage(name);
        if (imp != null) {
            return imp.getWindow();
        }
        return null;

    }

    /**
     * returns imp if it is open and linked, even if no window is attached
     */
    public ImagePlus getOpenLinkedImage(String name) {//test 19.10.2010
        ImageOJ img = OJ.getData().getImages().getImageByName(name);//9.7.2013
        if (img == null) {
            return null;
        }
        return img.getImagePlus();//null if not open

//        int[] openImgs = WindowManager.getIDList();
//        if (openImgs != null) {
//            for (int jj = 0; jj < openImgs.length; jj++) {
//                int id = openImgs[jj];
//                ImagePlus imp = WindowManager.getImage(id);
//                FileInfo fInfo = imp.getOriginalFileInfo();
//                String fileName = null;
//                if (fInfo != null) {
//                    fileName = fInfo.fileName;
//                    String dirName = fInfo.directory;
//                    if (fileName == null || dirName == null) {
//                        return null;
//                    }
//                    if (dirName.equals(OJ.getData().getDirectory()) && fileName.equals(name)) {
//                        return imp;
//                    }
//                }
//            }
//        }
//        return null;
    }

    //if linked image is open several times, close all except the first one
    public boolean closeDuplicates(String name) {
        int found = 0;
        boolean wasClosed = false;
        ImagePlus firstImp = null;
        int[] openImgs = WindowManager.getIDList();
        if (openImgs != null) {
            for (int jj = 0; jj < openImgs.length; jj++) {
                int id = openImgs[jj];
                ImagePlus imp = WindowManager.getImage(id);
                FileInfo fInfo = imp.getOriginalFileInfo();
                String fileName = null;
                if (fInfo != null) {
                    fileName = fInfo.fileName;
                    String dirName = fInfo.directory;
                    if (fileName != null && dirName != null) {

                        if (dirName.equals(OJ.getData().getDirectory()) && fileName.equals(name)) {
                            found++;
                            if (found == 1) {
                                firstImp = imp;
                            }
                            if (found > 1 && imp != firstImp) {//here we are ............
                                imp.close();
                                wasClosed = true;//7.12.2013
//                                if (firstImp.getWindow() != null) {
//                                    firstImp.getWindow().setState(Frame.NORMAL);
//                                }
                            }
                        }
                    }
                }
            }
            //           if (found > 1) {
            if (wasClosed) {//7.12.2013
                if (firstImp.getWindow() != null) {
                    firstImp.getWindow().setState(Frame.NORMAL);
                }
            }
        }
        //return found > 1;
        return wasClosed;//7.12.2013
    }

    /**
     * If
     */
//    public ImageWindow getImageWindow(int id) {
//        for (Enumeration e = openedImages.keys(); e.hasMoreElements();) {
//            ImageWindow imgw = (ImageWindow) openedImages.get(e.nextElement());
//            if (imgw.getImagePlus().getID() == id) {
//                return imgw;
//            }
//        }
//        return null;
//    }
    /**
     * removes linked image and its listeners
     *
     * @param name
     */
    public void removeFromOpenedImages(String name) {
        if (openedImages.containsKey(name)) {
            ImagePlus imp = (ImagePlus) openedImages.get(name);
            ImageWindow imgw = imp.getWindow();
            if (imgw != null) {
                ImageCanvas cnv = imgw.getCanvas();
                if (cnv instanceof CustomCanvasOJ) {
                    OJ.getEventProcessor().removeYtemDefChangedListener((YtemDefChangedListenerOJ) cnv);
                    OJ.getEventProcessor().removeYtemChangedListener((YtemChangedListenerOJ) cnv);
                    OJ.getEventProcessor().removeCellChangedListener((CellChangedListenerOJ) cnv);
                }
            }

            openedImages.remove(name);
        }
    }

    /**
     * repaints all open linked images
     */
    public void updateOpenImages() {
        for (Enumeration e = openedImages.keys(); e.hasMoreElements();) {
            ImagePlus imp = (ImagePlus) openedImages.get(e.nextElement());
            if (imp.getWindow() != null) {
                imp.getWindow().getCanvas().repaint();

            }
        }
        if (GlassWindowOJ.showing()) {
            GlassWindowOJ.getInstance().repaint();
        }

    }
    /**
     * ImageDnDOJ.java -- documented
     *
     * for handling drag+drop of images into the Images panel
     */
    //private static ImageDnDOJ instance = null;
    public static int dropOperations = DnDConstants.ACTION_COPY;

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dtde) {
    }

    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY | DnDConstants.ACTION_LINK);

        try {
            Transferable t = dtde.getTransferable();
            DataFlavor[] flavors = t.getTransferDataFlavors();
            if (flavors.length == 0) {
                ij.IJ.showMessage("Drag&Drop ignored, -please try again");//30.9.2009
            }
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                Collections.sort((List) data);
                Iterator iterator = ((List) data).iterator();
                while (iterator.hasNext()) {
                    File file = (File) iterator.next();
                    String fName = file.getName();
                    boolean good = false;
                    String[] goodExt = ".tiff .tif .jpg .png .gif".split(" ");
                    for (int jj = 0; jj < goodExt.length; jj++) {
                        if (fName.toLowerCase().endsWith(goodExt[jj])) {
                            good = true;
                        }
                    }
                    if (good && !file.isDirectory()) {//16.4.2013
                        OJ.getImageProcessor().addImage(fName, false, false);//problem as the file name is changed 17.5.2009
                        ImagePlus imp = isAlreadyOpen(fName);//3.11.2013
                        if (imp != null) {
                            ImageOJ imgOj = OJ.getData().getImages().getImageByName(fName);
                            if (imgOj != null) {
                                imgOj.setImagePlus(imp);
                                imgOj.setID(imp.getID());
                                applyImageGraphics(imp, imgOj, true);//3.11.2013
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            dtde.dropComplete(false);
            return;
        }
        dtde.dropComplete(true);
    }

    ImagePlus isAlreadyOpen(String fName) {
        int[] openImgs = WindowManager.getIDList();
        if (openImgs != null) {
            for (int jj = 0; jj < openImgs.length; jj++) {
                int id = openImgs[jj];
                ImagePlus imp = WindowManager.getImage(id);
                FileInfo fInfo = imp.getOriginalFileInfo();
                String fileName = null;
                if (fInfo != null) {
                    fileName = fInfo.fileName;
                    String dirName = fInfo.directory;
                    if (fileName == null || dirName == null) {
                        return null;
                    }
                    if (dirName.equals(OJ.getData().getDirectory()) && fileName.equals(fName)) {
                        return imp;
                    }
                }
            }
        }
        return null;
    }
}
