/*
 * MacroSetOJ.java
 */
package oj.macros;

import ij.Menus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import oj.util.UtilsOJ;
import ij.macro.MacroRunner;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class MacroSetOJ //extends MacroBaseOJ
        implements ActionListener {

    private static final long serialVersionUID = 3980538672435890313L;
    private transient ArrayList macroItems = new ArrayList();
    private transient MacroProgramOJ macroProgram = new MacroProgramOJ();
    private String name = "";
    private String content = "";
    private String description = "";
    private boolean changed = false;

    public MacroSetOJ() {
        name = "";
        content = "";
        description = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMacrosCount() {
        return macroItems.size();
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int indexOfMacroItem(String macroName) {
        for (int i = 0; i < macroItems.size(); i++) {
            if (((MacroItemOJ) macroItems.get(i)).getName().equals(macroName)) {
                return i;
            }
        }
        return -1;
    }

    public void installText(String content) {
        if (macroItems == null) {
            macroItems = new ArrayList();
        } else {
            macroItems.clear();
        }
        parseContent(content);
        if (macroProgram == null) {
            macroProgram = new MacroProgramOJ();
        }
        macroProgram.install(content + MacroExtStrOJ.macroExtensions(), macroItems.size());
    }

    public MacroItemOJ[] macroItemsToArray() {
        if (macroItems.size() > 0) {
            MacroItemOJ[] result = new MacroItemOJ[macroItems.size()];
            macroItems.toArray(result);
            return result;
        } else {
            MacroItemOJ[] result = new MacroItemOJ[0];
            return result;
        }
    }

    private static String extractMacroName(String macroExtName) {
        if (macroExtName.contains("Tool")) {
            return macroExtName.substring(1, macroExtName.indexOf("Tool") + 4);
        }
        StringTokenizer st = new StringTokenizer(macroExtName, "\"'[", true);
        if (st.hasMoreTokens()) {
            st.nextToken();
            return st.nextToken().trim();
        }
        return null;
    }

    private static String extractStringShortcut(String macroExtName) {
        if (macroExtName.indexOf("[") > 0) {
            StringTokenizer st = new StringTokenizer(macroExtName, "[]", true);
            if (st.hasMoreTokens()) {
                st.nextToken();
                st.nextToken();
                return st.nextToken().trim();
            }
        }
        return null;
    }

    private static String extractMacroIcon(String macroExtName) {
        if (macroExtName.indexOf("-") > 0) {
            StringTokenizer st = new StringTokenizer(macroExtName, "-", true);
            if (st.hasMoreTokens()) {
                st.nextToken();
                st.nextToken();
                return st.nextToken().trim();
            }
        }
        return "";
    }

    private static int extractMacroShortcut(String macroExtName) {
        String shortcut = extractStringShortcut(macroExtName);
        if (shortcut != null) {
            int len = shortcut.length();
            if (len > 1) {
                shortcut = shortcut.toUpperCase(Locale.US);
                if (len > 3 || (len > 1 && shortcut.charAt(0) != 'F' && shortcut.charAt(0) != 'N')) {
                    return 0;
                }
            }
            return UtilsOJ.convertShortcutToCode(shortcut);
        }
        return 0;
    }

    private static int extractMacroToolType(String macroExtName) {
        if (macroExtName.contains("Action")) {
            return MacroItemOJ.ACTION_TOOL;
        } else if (macroExtName.contains("Menu")) {
            return MacroItemOJ.MENU_TOOL;
        } else {
            return MacroItemOJ.IMAGE_TOOL;
        }
    }

    private void parseContent(String text) {
        String text2 = UtilsOJ.maskComments(text);//19.10.2010
        text2 = text2.replaceAll("macro\"", "macro \"");
        String[] macros = text2.split("macro ");
        if (macros.length > 0) {
            for (int i = 1; i < macros.length; i++) {
                String macro_ext_name = macros[i].substring(0, macros[i].indexOf("{")).trim();
                String macro_name = MacroSetOJ.extractMacroName(macro_ext_name);
                if ((macro_name != null) && (!macro_name.startsWith("Unused"))) {
                    String ss = macro_ext_name.toLowerCase();
                    ss = ss.replaceAll(" ", "");
                    if (ss.contains("tool-")) {//25.9.2013
                        MacroItemOJ item = new MacroItemOJ(macro_name);
                        item.setIcon(MacroSetOJ.extractMacroIcon(macro_ext_name));
                        item.setToolType(MacroSetOJ.extractMacroToolType(macro_ext_name));
                        item.setShortcut(MacroSetOJ.extractMacroShortcut(macro_ext_name));
                        macroItems.add(item);
                    } else {
                        MacroItemOJ item = new MacroItemOJ(macro_name);
                        if (macro_name.equalsIgnoreCase("AutoRun")) {
                            item.setAutorun(true);
                        }
                        int shortcutIndex = MacroSetOJ.extractMacroShortcut(macro_ext_name);
                        item.setShortcut(shortcutIndex);

                        Hashtable macroShortcuts = Menus.getMacroShortcuts();
                        macroShortcuts.put(new Integer(shortcutIndex), "^" + macro_ext_name); //user shortcut overrides ImageJ shortcut 13.7.2012

                        macroItems.add(item);
                    }
                }
            }
        }
    }

    public void runMacro(String macroName) {
        int index = indexOfMacroItem(macroName);
        if (index >= 0) {
            new MacroRunner(macroProgram.getProgram(), macroProgram.getMacroStarts(macroName), macroName, (String) null);//9.1.2009
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String cmd = actionEvent.getActionCommand();
        if (cmd != null) {
            runMacro(cmd);
        }
    }

    public MacroItemOJ getMacroItemByIndex(int index) {
        if (index < macroItems.size()) {
            return (MacroItemOJ) macroItems.get(index);
        } else {
            return null;
        }
    }

    public static String removeMacro(String text, String name) {
        String macroText = "";
        String[] macros = UtilsOJ.maskComments(text).split("macro ");
        if (macros.length > 0) {
            for (int i = 1; i < macros.length; i++) {
                String macro_ext_name = macros[i].substring(0, macros[i].indexOf("{")).trim();
                String macro_name = MacroSetOJ.extractMacroName(macro_ext_name);
                if ((macro_name != null) && (macro_name.equals(name))) {
                    int pos = macros[i].indexOf("function");
                    if (pos <= 0) {
                        pos = macros[i].lastIndexOf("}");
                    }
                    macroText = macros[0];
                    for (int j = 1; j < i; j++) {
                        macroText = macroText + "\n macro " + macros[j];
                    }
                    macroText = macroText + "\n" + macros[i].substring(pos + 1);
                    for (int j = i + 1; j < macros.length; j++) {
                        macroText = macroText + "\n macro " + macros[j];
                    }
                }
            }
        }
        return macroText;
    }
}
