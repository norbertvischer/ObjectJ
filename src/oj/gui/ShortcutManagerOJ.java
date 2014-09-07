package oj.gui;

import ij.ImageJ;
import ij.plugin.frame.PlugInFrame;
import ij.text.TextWindow;
import java.awt.Dialog;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.MenuShortcut;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JDialog;
import oj.OJ;
import oj.gui.menuactions.ViewActionsOJ;
import oj.gui.settings.ProjectSettingsOJ;

public class ShortcutManagerOJ {

    int flags = 0;
    private static ShortcutManagerOJ instance;
    private ArrayList errors = new ArrayList();
    private Hashtable macroListeners = new Hashtable();
    private Hashtable macroShortcuts = new Hashtable();
    private PostProcessor postProcessor = new PostProcessor();

    public ShortcutManagerOJ() {
        instance = this;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(postProcessor);
    }

    public static ShortcutManagerOJ getInstance() {
        return instance;
    }

    public ArrayList getErrors() {
        return errors;
    }

    public void cleanMacroShortcuts() {
        macroShortcuts.clear();
        macroListeners.clear();
        errors.clear();
    }

    public boolean isShortcutDefined(int keyCode, boolean shiftEnabled) {
        MenuShortcut shortcut = new MenuShortcut(keyCode, shiftEnabled);
        return macroShortcuts.containsKey(shortcut);
    }

    public void runMacro(int keyCode, boolean shiftEnabled) {

        MenuShortcut shortcut = new MenuShortcut(keyCode, shiftEnabled);
        String macroName = (String) macroShortcuts.get(shortcut);
        if (macroName != null) {
            ImageJ.setCommandName("^" + macroName);//9.11.2012
            Object obj = macroListeners.get(macroName);
            if (obj != null) {
                ActionEvent action = new ActionEvent(this, 0, macroName);
                ((ActionListener) obj).actionPerformed(action);
            }
        }
    }

    void addShortcut(String name, MenuShortcut shortcut, ActionListener listener) {
        macroShortcuts.put(shortcut, name);
        macroListeners.put(name, listener);
    }

    private void addDroppedCommandShortcut(int keyCode, boolean shiftEnabled, String name, String command) {
        String shortcut = KeyEvent.getKeyText(keyCode);
        if (shiftEnabled) {
            shortcut = KeyEvent.getKeyText(KeyEvent.SHIFT_MASK) + shortcut;
        }
        String error = String.format("Shortcut %s for '%s' dropped. Is used by menu command '%s'", shortcut, name, command);
        errors.add(error);
    }

    private void addDroppedMacroShortcut(int keyCode, boolean shiftEnabled, String name, String command) {
        String shortcut = KeyEvent.getKeyText(keyCode);
        if (shiftEnabled) {
            shortcut = KeyEvent.getKeyText(KeyEvent.SHIFT_MASK) + shortcut;
        }
        String error = String.format("Shortcut %s for '%s' dropped. Is used by macro command '%s'", shortcut, name, command);
        errors.add(error);
    }

    public boolean isAvailableShortcut(int keyCode, boolean shiftEnabled, String name) {
        MenuShortcut shortcut = getShortcut(keyCode, shiftEnabled);
        if (shortcut != null) {
            addDroppedMacroShortcut(keyCode, shiftEnabled, name, (String) macroShortcuts.get(shortcut));
            return false;
        }
        return true;
    }

    private MenuShortcut getShortcut(int keyCode, boolean shiftEnabled) {
        for (Enumeration keys = macroShortcuts.keys(); keys.hasMoreElements();) {
            MenuShortcut shortcut = (MenuShortcut) keys.nextElement();
            if ((shortcut.getKey() == keyCode) && (shortcut.usesShiftModifier() == shiftEnabled)) {
                return shortcut;
            }
        }
        return null;
    }

    public boolean isShiftPressed() {
        return (flags & KeyEvent.SHIFT_MASK) != 0;
    }

    public boolean isControlPressed() {
        return (flags & KeyEvent.CTRL_MASK) != 0;
    }

    public boolean isAltPressed() {
        return (flags & KeyEvent.ALT_MASK) != 0;
    }

    private class PostProcessor implements KeyEventPostProcessor {//don't evaluate if you are in the command finder 24.2.2010

        public boolean postProcessKeyEvent(KeyEvent evt) {
            flags = evt.getModifiers();
            if (OJ.isValidData()) {// could be omitted21.9.2010
                int id = evt.getID();
                if ((id == KeyEvent.KEY_PRESSED) && !evt.isConsumed()) {
                    int keyCode = evt.getKeyCode();
                    boolean shift = (flags & KeyEvent.SHIFT_MASK) != 0;
                    boolean meta = (flags & KeyEvent.META_MASK) != 0;  //16.11.2008
                    boolean control = (flags & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;

                    if (!evt.isConsumed()) {
                        if (control && shift) {
                            switch (keyCode) {
                                case KeyEvent.VK_F1: {
                                    ViewActionsOJ.SettingsAction.actionPerformed(null);
                                    evt.consume();
                                }
                                break;
                                case KeyEvent.VK_F2: {
                                    ViewActionsOJ.YtemListAction.actionPerformed(null);
                                    evt.consume();
                                }
                                break;
                                case KeyEvent.VK_F3: {
                                    ViewActionsOJ.ResultsViewAction.actionPerformed(null);
                                    evt.consume();
                                }
                                break;
//                                case KeyEvent.VK_F4: {
//                                    ViewActionsOJ.ShowEmbeddedMacroAction.actionPerformed(null);
//                                    evt.consume();
//                                }
//                                break;
                                case KeyEvent.VK_F5: {
                                    ViewActionsOJ.ShowProjectFolderAction.actionPerformed(null);
                                    evt.consume();
                                }
                                break;
//                                case KeyEvent.VK_C:
//                                    YtemDefActionsOJ.SwitchCompositeModeAction.actionPerformed(null);
//                                    evt.consume();
//                                    break;
                            }
                        } else {
                            if (ShortcutManagerOJ.isFocusedWindowSupportingShortcuts() && !meta) {  //16.11.2008
                                if ((!evt.isConsumed()) && (isShortcutDefined(evt.getKeyCode(), shift))) {
                                    runMacro(evt.getKeyCode(), shift);
                                    evt.consume();
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

    public static boolean isFocusedWindowSupportingShortcuts() {
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();   
        if (win == null) {
            return false;
        }
        boolean aa = (win instanceof PlugInFrame);//25.10.2008
        boolean bb = (win instanceof Dialog);
        boolean cc = (win instanceof JDialog);
        boolean dd = (win instanceof ProjectSettingsOJ);
        String name = win.toString();
        boolean ee = (name.startsWith("ij.plugin.CommandFinder"));//24.2.2010
        boolean ff = (name.startsWith("fiji.scripting.TextEditor"));//7.3.2014
        boolean supported = !(aa || bb || cc || dd || ee || ff);
        return supported;

    }
}
