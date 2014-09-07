/*
 * ImageWindowUtilsOJ.java
 */
package oj.util;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import java.awt.Container;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ImageWindowUtilsOJ {

    public static void setImageCanvas(ImageWindow imgw, ImageCanvas ic) {
        final Field[] fields = ImageWindow.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("ic".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set(imgw, ic);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    /* 
     * draws the info on top of a image
     * @param imgv the image window for which the info is printed
     */
    //temporirily enabled  drawInfo, getTextGap, getSubtitle probably problems with hyperstacks //17.4.2010
    public static void drawInfo(ImageWindow imgw) {
        int text_gap = ImageWindowUtilsOJ.getTextGap(imgw);
        if (text_gap != 0) {
            Insets insets = ((Container) imgw).getInsets();
            if (imgw.getImagePlus() instanceof CompositeImage) {
                imgw.getGraphics().setColor(((CompositeImage) imgw.getImagePlus()).getChannelColor());
            }
        }
    }

    public static int getTextGap(ImageWindow imgw) {
        final Field[] fields = ImageWindow.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("textGap".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return ((Integer) fields[i].get(imgw)).intValue();
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

    public static String getSubtitle(ImageWindow imgw) {
        final Method[] methods = ImageWindow.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if ("createSubtitle".equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    return (String) methods[i].invoke(imgw, (Object) null);//9.1.2009
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
        return "";
    }

    /**
     * Returns ImagePlus that is connected to path1, or null if not found
     *
     * @param path1
     * @return imp, or null if not found
     */
    public static ImagePlus isOpen(String path1) {
        int[] ids = WindowManager.getIDList();
        if (ids == null) {
            return null;
        }
        for (int jj = 0; jj < ids.length; jj++) {
            ImagePlus imp = WindowManager.getImage(ids[jj]);
            if (imp != null) {
                String name = imp.getOriginalFileInfo().fileName;
                String path2 = imp.getOriginalFileInfo().directory + name;
                if (path2.equals(path1)) {
                    return imp;
                }
            }
        }
        return null;
    }
}
