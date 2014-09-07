package oj.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.TextRoi;
import ij.gui.Toolbar;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import oj.OJ;
import oj.graphics.CustomCanvasOJ;
import oj.gui.menuactions.ViewActionsOJ;
import oj.plugin.GlassWindowOJ;
import oj.processor.state.CreateCellStateOJ;

/**
 *
 * @author norbert
 */
public class KeyEventManagerOJ implements KeyListener {

    private static KeyEventManagerOJ instance;

    private KeyEventManagerOJ() {
    }

    public static KeyEventManagerOJ getInstance() {
        if (instance == null) {
            instance = new KeyEventManagerOJ();
        }
        return instance;
    }

    public static void release() {
        if (instance != null) {
            IJ.getInstance().addKeyListener(IJ.getInstance());
            IJ.getInstance().removeKeyListener(instance);

            //remove the listener from any open ImageWindow
            for (int i = 0; i < WindowManager.getImageCount(); i++) {
                WindowManager.getImage(i).getCanvas().addKeyListener(IJ.getInstance());
                WindowManager.getImage(i).getWindow().addKeyListener(IJ.getInstance());
                WindowManager.getImage(i).getCanvas().removeKeyListener(KeyEventManagerOJ.getInstance());
                WindowManager.getImage(i).getWindow().removeKeyListener(KeyEventManagerOJ.getInstance());
            }

            instance = null;
        }
    }

    public void replaceKeyListener(ImagePlus imp) {
        if (imp != null) {
            imp.getCanvas().removeKeyListener(KeyEventManagerOJ.getInstance());
            imp.getWindow().removeKeyListener(KeyEventManagerOJ.getInstance());
            imp.getCanvas().removeKeyListener(IJ.getInstance());
            imp.getWindow().removeKeyListener(IJ.getInstance());

            imp.getCanvas().addKeyListener(KeyEventManagerOJ.getInstance());
            imp.getWindow().addKeyListener(KeyEventManagerOJ.getInstance());
        }
    }

    public void keyTyped(KeyEvent evt) {
        IJ.getInstance().keyTyped(evt);
    }

    public void keyPressed(KeyEvent evt) {
        
        
        
        if ((KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) 
                && (Toolbar.getToolId() == Toolbar.TEXT) 
                && (IJ.getImage().getRoi() != null) 
                && (IJ.getImage().getRoi() instanceof TextRoi)) {
            IJ.getInstance().keyPressed(evt);
            evt.consume();
            return;
        }
        if (OJ.isValidData()) {
            // 1. check the canvas keys
            // 2. check the macro shortcuts keys
            // 3. check the menu shortcuts keys
            // 4. if not consumed then passed to ImageJ
            boolean shift = (evt.getModifiers() & KeyEvent.SHIFT_MASK) != 0;
            boolean control = (evt.getModifiers() & KeyEvent.CTRL_MASK) != 0;
            boolean alt = (evt.getModifiers() & KeyEvent.ALT_MASK) != 0;
            boolean meta = (evt.getModifiers() & KeyEvent.META_MASK) != 0;
            int keyCode = evt.getKeyCode();
            if (keyCode == '<' || keyCode == '>') {
                evt.consume();
                return;
            }
            ImagePlus imp = null;

            boolean isGlassWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof GlassWindowOJ;
            if (isGlassWindow) {
                imp = GlassWindowOJ.getInstance().getImagePlus();
            }
            else {
                if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
                    if (((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getCanvas() instanceof CustomCanvasOJ) {
                        imp = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
                    }
                }
            }
            if (imp != null) {
                OJ.getToolStateProcessor().keyPressed(imp.getTitle(), imp.getCurrentSlice(), evt.getKeyCode(), evt.getModifiers());
                if (OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ) {
                    switch (evt.getKeyCode()) {
                        case KeyEvent.VK_TAB:
                        //case KeyEvent.VK_ENTER:DISABL´D 21.5.2010
                        case KeyEvent.VK_BACK_SPACE:
                            evt.consume();
                            break;
                        default:
                            break;
                    }
                }

            } else {
                if (ShortcutManagerOJ.isFocusedWindowSupportingShortcuts()) {
                    OJ.getToolStateProcessor().keyPressed(null, 0, evt.getKeyCode(), evt.getModifiers());
                }
            }
            if (ShortcutManagerOJ.isFocusedWindowSupportingShortcuts()) {
                if (!evt.isConsumed()) {
                    if (!control && !meta && ShortcutManagerOJ.getInstance().isShortcutDefined(evt.getKeyCode(), shift)) {
                        ShortcutManagerOJ.getInstance().runMacro(evt.getKeyCode(), shift);
                        evt.consume();
                    } else {

                    }
                }
            }
            if (!evt.isConsumed()) {
                IJ.getInstance().keyPressed(evt);
            }
        } else {
            IJ.getInstance().keyPressed(evt);
        }
    }

    public void keyReleased(KeyEvent evt) {
        if (OJ.isValidData()) {
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof ImageWindow) {
                if (((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getCanvas() instanceof CustomCanvasOJ) {
                    ImagePlus imp = ((ImageWindow) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()).getImagePlus();
                    OJ.getToolStateProcessor().keyReleased(imp.getTitle(), imp.getCurrentSlice(), evt.getKeyCode(), evt.getModifiers());
                }
            } else {
                OJ.getToolStateProcessor().keyReleased(null, 0, evt.getKeyCode(), evt.getModifiers());
            }
        }
        IJ.getInstance().keyReleased(evt);

    }
}
