/*
 * ImageJAccessOJ.java
 * fully documented on 4.4.2010
 * 
 * These 9 classes contain all the static functions for
 * accessing the fields and methods which are not public
 * inside the ImageJ application. Hopefully, Wayne will not change private method names
 */
package oj.util;

import ij.ImagePlus;
import ij.ImageStack;
import ij.Menus;
import ij.gui.Roi;
import ij.gui.Toolbar;
import ij.macro.Functions;
import ij.macro.Interpreter;
import ij.macro.Program;
import ij.plugin.Converter;
import java.awt.Graphics;
import java.awt.MenuBar;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class ImageJAccessOJ {

    /**
     * class to handle private method of roi access - not used yet
     */
//    public static class RoiAccess {
//
//        public static void handleMouseDrag(Roi roi, int sx, int sy, int flags) {
//            final Method methods[] = ImagePlus.class.getDeclaredMethods();
//            final String mName = "handleMouseDrag";
//            for (int i = 0; i < methods.length; ++i) {
//                if (mName.equals(methods[i].getName())) {
//                    methods[i].setAccessible(true);
//                    try {
//                        Object[] args = new Object[]{sx, sy, flags};
//                        methods[i].invoke(roi, args);
//                    } catch (Exception ex) {
//                        UtilsOJ.showException(ex, mName);//22.6.2009
//                    }
//                    return;
//                }
//            }
//            UtilsOJ.showException(null, mName);//22.6.2009
//        }
//
//        public static void handleMouseUp(Roi roi, int sx, int sy) {
//            final Method methods[] = ImagePlus.class.getDeclaredMethods();
//            final String mName = "handleMouseUp";
//            for (int i = 0; i < methods.length; ++i) {
//                if (mName.equals(methods[i].getName())) {
//                    methods[i].setAccessible(true);
//                    try {
//                        Object[] args = new Object[]{sx, sy};
//                        methods[i].invoke(roi, args);
//                    } catch (Exception ex) {
//                        UtilsOJ.showException(ex, mName);//22.6.2009
//                    }
//                    return;
//                }
//            }
//            UtilsOJ.showException(null, mName);//22.6.2009
//        }
//    }

    /**
     * used to send the "opened" message to all listeners when e.g. an image
     * linked by drag+drop into the Images panel
     */
//    public static class ImagePlusAccess {
//
//        public static void notifyImagePlusOpened(ImagePlus imp) {
//
//
//            //if (Interpreter.isBatchMode())return;//----- 30.6.2013
//
//
//            if (imp == null) {
//                return;
//            }
//            int[] dim = imp.getDimensions();//----- 29.6.2013
//            if (dim[2] == 1) {
//                int a = 3;
//                int b = 3;
//
//            }
//
//
//            final Method methods[] = ImagePlus.class.getDeclaredMethods();
//            final String mName = "notifyListeners";
//            for (int i = 0; i < methods.length; ++i) {
//                if (mName.equals(methods[i].getName())) {
//                    methods[i].setAccessible(true);
//                    try {
//                        Object[] args = new Object[]{0};
//                        methods[i].invoke(imp, args);
//                    } catch (Exception ex) {
//                        UtilsOJ.showException(ex, mName);//22.6.2009
//                    }
//                    return;
//                }
//            }
//            UtilsOJ.showException(null, mName);//22.6.2009
//        }
//    }

    // not used
//    public static class MenusAccess {
//
//        public static void setMennuBar(MenuBar mnuBar) {
//            final Field fields[] = Menus.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("mBar".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        fields[i].set(Menus.class, mnuBar);
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//    }

    //not used
//    public static class ConverterAccess {
//
//        public static void setImagePlus(Converter converter, ImagePlus imp) {
//            final Field fields[] = Converter.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("imp".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        fields[i].set(converter, imp);
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//    }

    //only used for obsolete macro functions ojSwapSlices
  //  public static class ImageStackAccess {

//        public static Object[] getStackObjects(ImageStack stack) {
//            final Field fields[] = ImageStack.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("stack".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        return ((Object[]) fields[i].get(stack));
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//            return null;
//        }
//
//        public static void setStackObjects(ImageStack stack, Object[] objects) {
//            final Field fields[] = ImageStack.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("stack".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        fields[i].set(stack, objects);
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//
////        public static String[] getStackLabels(ImageStack stack) {
////            final Field fields[] = ImageStack.class.getDeclaredFields();
////            for (int i = 0; i < fields.length; ++i) {
////                if ("label".equals(fields[i].getName())) {
////                    fields[i].setAccessible(true);
////                    try {
////                        return ((String[]) fields[i].get(stack));
////                    } catch (IllegalArgumentException ex) {
////                        ex.printStackTrace();
////                    } catch (IllegalAccessException ex) {
////                        ex.printStackTrace();
////                    }
////                    break;
////                }
////            }
////            return null;
////        }
//
//        public static void setStackLabels(ImageStack stack, String[] labels) {
//            final Field fields[] = ImageStack.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("label".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        fields[i].set(stack, labels);
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//    }

    /**
     * Used to handle macro errors, access functions in Functions (like
     * resetImage), and access
     */
    public static class InterpreterAccess {

        // gives access to the macro error message pipe
        public static void interpError(String message) {//n_9.9.2007
            final Method methods[] = Interpreter.class.getDeclaredMethods();
            final String mName = "error";
            for (int i = 0; i < methods.length; ++i) {
                if (mName.equals(methods[i].getName())) {
                    methods[i].setAccessible(true);
                    try {
                        Object[] args = new String[]{message};
                        methods[i].invoke(Interpreter.getInstance(), args);
                    } catch (Exception ex) {
                        //UtilsOJ.showException(ex, mName);//24.6.2009
                    }
                    Interpreter.abort();
                    return;
                }
            }
            UtilsOJ.showException(null, mName);
        }

        // this returns the Functions class; used by resetImage
        public static Functions getFunctions() {
            final Field fields[] = Interpreter.class.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                if ("func".equals(fields[i].getName())) {
                    Field thisField = fields[i];
                    Object intp = Interpreter.getInstance();
                    if (intp == null) {
                        return null;//4.4.2010
                    }
                    thisField.setAccessible(true);
                    try {
                        return ((Functions) thisField.get(intp));
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
        // Returns Program- not used

        public static Program getProgram() {
            final Field fields[] = Interpreter.class.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                if ("pgm".equals(fields[i].getName())) {
                    fields[i].setAccessible(true);
                    Object intp = Interpreter.getInstance();
                    if (intp == null) {
                        return null;//4.4.2010
                    }
                    try {
                        return ((Program) fields[i].get(Interpreter.getInstance()));
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
    }

//    public static class ProgramAccess {
//        // this returns the Extension registry class
//
//        public static Hashtable getExtensionRegistry(Program pgm) {
//            final Field fields[] = Program.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; ++i) {
//                if ("extensionRegistry".equals(fields[i].getName())) {
//                    fields[i].setAccessible(true);
//                    try {
//                        return ((Hashtable) fields[i].get(pgm));
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                    } catch (IllegalAccessException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//            return null;
//        }
//    }

    public static class FunctionsAccess {

        /**
         * used by ojShowObject and ojShowImage, e.g. sets defaultImp = null;
         */
        public static void resetImage() {
            final Method methods[] = Functions.class.getDeclaredMethods();
            final String mName = "resetImage";
            for (int i = 0; i < methods.length; ++i) {
                if (mName.equals(methods[i].getName())) {
                    methods[i].setAccessible(true);
                    Object theFunctions = ImageJAccessOJ.InterpreterAccess.getFunctions();
                    if (theFunctions == null) {//4.4.2010
                        return;
                    }
                    try {
                        Object[] args = new String[]{};

                        methods[i].invoke(theFunctions, args);
                    } catch (Exception ex) {
                        UtilsOJ.showException(ex, mName);//22.6.2009 again problems, 3.4.2010
                    }
                    return;
                }
            }
            UtilsOJ.showException(null, mName);//22.6.2009
        }
    }

    //not used, because we have our own toolbar
//    public static class ToolbarAccess {
//
//        public static void drawIcon(Graphics g, int tool, int x, int y) {
//            final Method methods[] = Toolbar.class.getDeclaredMethods();
//            final String mName = "drawIcon";
//            for (int i = 0; i < methods.length; ++i) {
//                if (mName.equals(methods[i].getName())) {
//                    methods[i].setAccessible(true);
//                    try {
//                        Object[] args = new Object[]{g, tool, x, y};
//                        methods[i].invoke(Toolbar.getInstance(), args);
//                    } catch (Exception ex) {
//                        UtilsOJ.showException(ex, mName);//22.6.2009
//                    }
//                    return;
//                }
//            }
//            UtilsOJ.showException(null, mName);//22.6.2009
//        }
//    }
}
