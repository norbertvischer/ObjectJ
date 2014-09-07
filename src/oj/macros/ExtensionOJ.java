/**
 * ExtensionOJ.java -- documented
 *
 * general mechanism to handle oj macor functions with different number of
 * arguments
 */
package oj.macros;

import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import oj.OJ;
import oj.util.ImageJAccessOJ;

/**
 * We only have 5 static functions called handleMacroExtensions, with 1, 2, 3,
 * 4, 5 string parameters. They are fanned out to >80 calls.
 *
 */
public class ExtensionOJ implements MacroExtension {

    static ExtensionOJ instance = null;
    String[] noProjectNeeded = {"ojRequires", "ojvInitStack", "ojvCalculate", "ojvGetVertexX",
        "ojvGetVertexY", "ojvGetVertexZ", "ojvPushVertex", "ojvPushRoi",
        "ojMatches", "ojvPushPoint", "ojvGetStackSize", "ojPluginTest", "ojCopyFromScreen", "ojTest", "ojLineToPolygon"};
    FunctionsOJ functions = new FunctionsOJ();

    /**
     *
     * @return
     */
    public static ExtensionOJ getInstance() {
        if (instance == null) {
            instance = new ExtensionOJ();
        }
        return instance;
    }

    public static boolean validateProjectOpen() {
        if (!OJ.isProjectOpen) {
            ImageJAccessOJ.InterpreterAccess.interpError("No project is open");
            return false;
        }
        return true;
    }

    /**
     * returns pointer to one of the >60 methods by supplying the name
     */
    private Method validateMethod(String funcName) {
        int pos = 2;
        if (funcName.startsWith("ojv")) {
            pos = 3;
        }
        String methodName = "validate" + funcName.substring(pos);
        final Method methods[] = FunctionsOJ.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methodName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                return methods[i];
            }
        }
        return null;
    }

    private Method macroMethod(String funcName) {
        final Method methods[] = FunctionsOJ.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (funcName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                return methods[i];
            }
        }
        return null;
    }

    /**
     * first gets the correct method, then calls it with the correct signature
     * (0..5 arguments) via method.invoke.
     */
    private boolean validate(String name, Object[] args) {
        Method method = validateMethod(name);
        if (method != null) {
            try {
                method.invoke(functions, args);
                return true;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static String handleMacroExtension(String name) {
        return ExtensionOJ.getInstance().handleExtension(name, new Object[0]);
    }

    public static String handleMacroExtension(String name, String arg) {
        return ExtensionOJ.getInstance().handleExtension(name, new Object[]{arg});
    }

    public static String handleMacroExtension(String name, String arg1, String arg2) {
        return ExtensionOJ.getInstance().handleExtension(name, new Object[]{arg1, arg2});
    }

    public static String handleMacroExtension(String name, String arg1, String arg2, String arg3) {
        return ExtensionOJ.getInstance().handleExtension(name, new Object[]{arg1, arg2, arg3});
    }

    public static String handleMacroExtension(String name, String arg1, String arg2, String arg3, String arg4) {
        return ExtensionOJ.getInstance().handleExtension(name, new Object[]{arg1, arg2, arg3, arg4});
    }

    boolean needsProject(String name) {
        for (int jj = 0; jj < noProjectNeeded.length; jj++) {
            if (noProjectNeeded[jj].equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;

    }

    public String handleExtension(String name, Object[] args) {
        if (!needsProject(name) || validateProjectOpen()) {
            if (validate(name, args)) {
                Method method = macroMethod(name);
                if (method != null) {
                    try {
                        return (String) method.invoke(functions, args);
                    } catch (InvocationTargetException ex) {
                        System.err.print(name);
                        ex.printStackTrace();
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    return "---";
                }
                ImageJAccessOJ.InterpreterAccess.interpError("method invocation failed");
                return "- -";
            }
            ImageJAccessOJ.InterpreterAccess.interpError("arguments are not valid");
        }
        return "-- -";
    }

    public ExtensionDescriptor[] getExtensionFunctions() {

        ArrayList<ExtensionDescriptor> extensions = new ArrayList<ExtensionDescriptor>();

        final Method methods[] = FunctionsOJ.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            String name = methods[i].getName();
            if (name.startsWith("oj")) {
                int argsCount = methods[i].getParameterTypes().length;
                int[] args = new int[argsCount];
                for (int j = 0; j < argsCount; j++) {
                    args[j] = MacroExtension.ARG_STRING;
                    if (name.equals("ojGetPositions")) {
                        args[0] = MacroExtension.ARG_ARRAY;
                        args[1] = MacroExtension.ARG_ARRAY;
                    }
                }
                extensions.add(new ExtensionDescriptor(methods[i].getName(), args, this));
            }
        }
        ExtensionDescriptor[] extensionsA = new ExtensionDescriptor[extensions.size()];
        extensions.toArray(extensionsA);
        return extensionsA;
    }
}
