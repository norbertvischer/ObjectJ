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
}
