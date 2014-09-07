/*
 * WriterOJ.java
 * -- documented
 *
 * - Remembers current file name, 
 * - does the SaveAs by calling FileSaver
 * - looks up old name in ObjectJ data
 * - updates old to new name in ObjectJ data
 * 
 * - if image is saved in other format than TIFF, objectj data are
 *   not updated and window is closed.
 * 
 * - This class partly copies Writer.java and needs to be synchronized manually
 *   in case Wayne adds new formats.
 * 
 */

package oj.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.Writer;
import ij.process.ImageProcessor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.project.ImageOJ;


public class WriterOJ extends Writer {

    public int setup(String arg, ImagePlus imp) {
        return super.setup(arg, imp);
    }

    public void run(ImageProcessor ip) {
        if (OJ.isValidData() && (OJ.getData().getImages().getImageByName(getImagePlus().getTitle()) != null)) {
            String path;
            String oldName = getImagePlus().getTitle();
            FileSaver fileSaver = new FileSaver(getImagePlus());

            if (getArgument().equals("tiff")) {
                path = getPath(fileSaver, "TIFF", ".tiff");
                if (path == null) {
                    return;//3.12.2009
                }
                if (getImagePlus().getStackSize() == 1) {
                    if (fileSaver.saveAsTiff(path)) {
                        updateOjImageName(path, oldName);
                    }
                } else {
                    if (fileSaver.saveAsTiffStack(path)) {
                        updateOjImageName(path, oldName);
                    }
                }
            } else if (getArgument().equals("gif")) {
                path = getPath(fileSaver, "GIF", ".gif");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsGif(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("jpeg")) {
                String type = "JPEG (" + FileSaver.getJpegQuality() + ")";
                path = getPath(fileSaver, type, ".jpg");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsJpeg(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("text")) {
                path = getPath(fileSaver, "Text", ".txt");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsText(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("lut")) {
                if (getImagePlus().getType() == ImagePlus.COLOR_RGB) {
                    IJ.error("RGB Images do not have a LUT.");
                    return;
                }
                path = getPath(fileSaver, "LUT", ".lut");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsLut(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("raw")) {
                path = getPath(fileSaver, "Raw", ".raw");
                if (path == null) {
                    return;//3.12.2009
                }
                if (getImagePlus().getStackSize() == 1) {
                    if (fileSaver.saveAsRaw(path)) {
                        OJ.getImageProcessor().closeImage(oldName);
                    }
                } else {
                    if (fileSaver.saveAsRawStack(path)) {
                        OJ.getImageProcessor().closeImage(oldName);
                    }
                }
            } else if (getArgument().equals("zip")) {
                path = getPath(fileSaver, "TIFF/ZIP", ".zip");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsZip(path)) {
                }
            } else if (getArgument().equals("bmp")) {
                path = getPath(fileSaver, "BMP", ".bmp");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsBmp(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("png")) {
                path = getPath(fileSaver, "PNG", ".png");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsPng(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("pgm")) {
                String extension = getImagePlus().getBitDepth() == 24 ? ".pnm" : ".pgm";
                path = getPath(fileSaver, "PGM", extension);
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsPgm(path)) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else if (getArgument().equals("fits")) {
                path = getPath(fileSaver, "FITS", ".fits");
                if (path == null) {
                    return;//3.12.2009
                }
                if (fileSaver.saveAsFits()) {
                    OJ.getImageProcessor().closeImage(oldName);
                }
            } else {
                super.run(ip);
            }
        } else {
            super.run(ip);
        }
    }

    private void updateOjImageName(String path, String imageName) {
        String filename = UtilsOJ.getFullFilename(path);
        ImageOJ image = OJ.getData().getImages().getImageByName(imageName);
        image.setName(filename);
        image.setFilename(filename);
        OJ.getData().getImages().updateImageName(imageName, image.getName());
    }

    private String getPath(FileSaver fileSaver, String type, String extension) {
        final Method[] methods = FileSaver.class.getDeclaredMethods();
        final String mName = "getPath";

        for (int i = 0; i < methods.length; ++i) {
            if (mName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    Object[] args = new String[]{type, extension};
                    return (String) methods[i].invoke(fileSaver, args);
                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }
            }
        }
        UtilsOJ.showException(null, mName);//22.6.2009
        return null;
    }

    private String getArgument() {
        final Field fields[] = Writer.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("arg".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return (String) fields[i].get((Writer) this);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

    private ImagePlus getImagePlus() {
        final Field fields[] = Writer.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("imp".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return (ImagePlus) fields[i].get((Writer) this);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
        return null;
    }
    InvocationHandler handler = new InvocationHandler() {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(proxy, args);
        }
    };
}
