package oj.gui.tools;

import ij.IJ;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import oj.OJ;
import oj.macros.MacroItemOJ;
import oj.macros.MacroSetOJ;
import oj.processor.state.ToolStateAdaptorOJ;
import oj.gui.tools.events.ToolListChangedEventOJ;
import oj.gui.tools.events.ToolListChangedListenerOJ;
import oj.gui.tools.events.ToolSelectionChangedEventOJ;
import oj.gui.tools.events.ToolSelectionChangedListenerOJ;

/**
 *
 * Manages creation and actions of tools in ObjectJ Tools window
 */
public class ToolManagerOJ {

    private static ToolManagerOJ instance;
    private Hashtable objectTools = new Hashtable();//e.g:    "pistol"->pistolTool
    private Hashtable objectToolsIds = new Hashtable();//e.g.: 2->"pistol"
    private Hashtable macroImageTools = new Hashtable();
    private Hashtable macroToolsIds = new Hashtable();
    private ToolOJ selectedTool = null;
    //private int macroIndex = 0;
    //private int objectIndex = 0;

    private ToolManagerOJ() {
        addPredefinedTools();
    }

    /**
     * Makes sure that only one ToolManagerOJ exists
     */
    public static ToolManagerOJ getInstance() {
        if (instance == null) {
            instance = new ToolManagerOJ();
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            instance.clear();
            instance = null;
        }
    }

    /**
     * Clears the  hash tables for tool macros
     */
    public void clear() {
        macroImageTools.clear();
        macroToolsIds.clear();
        //macroActionTools.clear();
        clearListeners();
    }

    /**
     * Checks all macros, and sets additional buttons below the five 
     * ObjectJ standard tools for those macros that are of type macro tool.
     * An Action tool  performs it's action when clicking into an image.
     */
//    private void addMacroActionTools() {
//        MacroSetOJ macroSet = OJ.getData().getMacroSet();
//       // for (int i = 0; i < macroSets.length; i++)
//        {
//            MacroItemOJ[] items = macroSet.macroItemsToArray();
//            for (int j = 0; j < items.length; j++) {
//                if (items[j] instanceof MacroToolItemOJ) {
//                    if (((MacroToolItemOJ) items[j]).getToolType() == MacroToolItemOJ.ACTION_TOOL) {
//                        macroIndex += 1;
//                        MacroActionToolOJ actionTool = new MacroActionToolOJ(items[j].getName(), macroSet);
//                        actionTool.setId(macroIndex + 100);
//                        macroActionTools.put(items[j].getName(), actionTool);
//                        macroToolsIds.put(macroIndex + 100, items[j].getName());
//                    }
//                }
//            }
//        }
//    }
    /**
     * Enters the 5 standard ObjectJ tools (such as pistol) into two hash tables:
     * objectTools  and objectToolsIds
     */
    private void addPredefinedTools() {
        SetMarkerToolOJ createCellTool = new SetMarkerToolOJ();
        createCellTool.setId(1);
        objectTools.put(createCellTool.getName(), createCellTool);
        objectToolsIds.put(1, createCellTool.getName());

        PistolToolOJ deleteCellTool = new PistolToolOJ();
        deleteCellTool.setId(2);
        objectTools.put(deleteCellTool.getName(), deleteCellTool);
        objectToolsIds.put(2, deleteCellTool.getName());

        MoveCellToolOJ moveCellTool = new MoveCellToolOJ();
        moveCellTool.setId(3);
        objectTools.put(moveCellTool.getName(), moveCellTool);
        objectToolsIds.put(3, moveCellTool.getName());

        SelectCellToolOJ selectCellTool = new SelectCellToolOJ();
        selectCellTool.setId(4);
        objectTools.put(selectCellTool.getName(), selectCellTool);
        objectToolsIds.put(4, selectCellTool.getName());

        ObjectToRoiToolOJ objectToRoiTool = new ObjectToRoiToolOJ();
        objectToRoiTool.setId(5);
        objectTools.put(objectToRoiTool.getName(), objectToRoiTool);
        objectToolsIds.put(5, objectToRoiTool.getName());

        //objectIndex = 5;
    }

    /**
     * Checks all macros, and puts a tool for those macros that are of type TOGGLE_TOOL.
     * A toggle tool performs its action when clicking on the button.
     */
    private void addMacroTools() {
        MacroSetOJ macroSet = OJ.getData().getMacroSet();
        macroImageTools.clear();
        macroToolsIds.clear();
        if (macroSet == null) return;//20.10.2010
        int toolIndex = 0;
        for (MacroItemOJ item : macroSet.macroItemsToArray()) {
            if (item.getToolType() == MacroItemOJ.IMAGE_TOOL) {
                toolIndex++;//21.9.2010
                MacroToolOJ tool = new MacroToolOJ(item.getName(), macroSet);
                tool.setId(toolIndex + 100);
                macroImageTools.put(item.getName(), tool);
                macroToolsIds.put(toolIndex + 100, item.getName());
            }
        }

    }

    public Enumeration getMacroTools() {
        Vector tools = new Vector(macroImageTools.values());
        Collections.sort(tools, new ToolsComparer());
        return tools.elements();
    }

//    public Enumeration getMacroActionTools() {
//        Vector actionTools = new Vector(macroActionTools.values());
//        Collections.sort(actionTools, new ActionToolsComparer());
//        return actionTools.elements();
//    }

    public Enumeration getObjectTools() {
        Vector tools = new Vector(objectTools.values());
        Collections.sort(tools, new ToolsComparer());
        return tools.elements();
    }

//    public Enumeration getObjectActionTools() {
//        Vector actionTools = new Vector(objectActionTools.values());
//        Collections.sort(actionTools, new ActionToolsComparer());
//        return actionTools.elements();
//    }

    public ToolOJ getSelectedTool() {
        return selectedTool;
    }

    private void selectObjectTool(String toolName) {
        Object tool = objectTools.get(toolName);
        OJ.debugLog("tool=" + toolName);
        if (tool != null) {
            clearIJToolbarSelection();
            selectedTool = (ToolOJ) tool;
            OJ.getToolStateProcessor().setToolState(selectedTool.getState());
            fireToolSelectionChangedEvent(toolName);
        }
    }

//    private void selectObjectActionTool(String toolName) {
//        ((ActionToolOJ) objectActionTools.get(toolName)).actionPerformed();
//    }

    private void selectMacroTool(String toolName) {
        Object tool = macroImageTools.get(toolName);
        if (tool != null) {
            clearIJToolbarSelection();
            selectedTool = (ToolOJ) tool;
            OJ.getToolStateProcessor().setToolState(selectedTool.getState());
            fireToolSelectionChangedEvent(toolName);
        }
    }

//    private void selectMacroActionTool(String toolName) {
//        Object tool = macroActionTools.get(toolName);
//        if (tool != null) {
//            ((MacroActionToolOJ) tool).actionPerformed();
//        }
//    }

    public void selectTool(int toolId) {
        if (objectToolsIds.containsKey(toolId)) {
            selectTool((String) objectToolsIds.get(toolId));
        } else if (macroToolsIds.containsKey(toolId)) {
            selectTool((String) macroToolsIds.get(toolId));
        }
    }

    public void selectTool(String toolName) {
        if (!OJ.isProjectOpen)//29.10.2008
        {
            return;
        }
        if ((selectedTool == null) || (!selectedTool.getName().equals(toolName))) {
            if (objectTools.containsKey(toolName)) {
                selectObjectTool(toolName);
                ImageWindow imgw = WindowManager.getCurrentWindow();
                if (imgw != null && !IJ.macroRunning()) {//3.4.2009
                    imgw.toFront();
                }
            } else if (macroImageTools.containsKey(toolName)) {
                selectMacroTool(toolName);
            } else {
                selectedTool = null;
                OJ.getToolStateProcessor().setToolState(new ToolStateAdaptorOJ());
                fireToolSelectionChangedEvent("");
            }
        }

        if (objectTools.containsKey(toolName)) {
            ImageWindow imgw = WindowManager.getCurrentWindow();
            if (imgw != null && !IJ.macroRunning()) {
                imgw.toFront();//3.4.2009
            }

        }


    }

    public void reload() {
        //macroIndex = 0;
        macroImageTools.clear();
        macroToolsIds.clear();

 //       macroActionTools.clear();

        if (OJ.isValidData()) {
            addMacroTools();
            //addMacroActionTools();

            if ((selectedTool != null) && (selectedTool instanceof MacroToolOJ) && (macroImageTools.get(selectedTool) == null)) {
                selectedTool = null;
                fireToolSelectionChangedEvent("");
            }
        } else {
            selectedTool = null;
            fireToolSelectionChangedEvent("");
        }
        fireToolListChangedEvent("macro");
    }

    public void clearIJToolbarSelection() {
        final Field[] fields = Toolbar.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("down".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    boolean[] down = new boolean[23];
                    fields[i].set(Toolbar.getInstance(), down);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
            if ("current".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set(Toolbar.getInstance(), new Integer(23));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
            if ("previous".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    fields[i].set(Toolbar.getInstance(), new Integer(22));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Toolbar.getInstance().repaint();
    }
    /**
     * This is the event mechanism
     **/
    protected ArrayList toolListChangedListeners = new ArrayList();
    protected ArrayList toolSelectionChangedListeners = new ArrayList();

    public void addToolListChangedListener(ToolListChangedListenerOJ listener) {
        if (toolListChangedListeners.indexOf(listener) < 0) {
            toolListChangedListeners.add(listener);
        }
    }

    public void addToolSelectionChangedListener(ToolSelectionChangedListenerOJ listener) {
        if (toolSelectionChangedListeners.indexOf(listener) < 0) {
            toolSelectionChangedListeners.add(listener);
        }
    }

    public void removeToolListChangedListener(ToolListChangedListenerOJ listener) {
        int index = toolListChangedListeners.indexOf(listener);
        if (index > 0) {
            toolListChangedListeners.remove(index);
        }
    }

    public void removeToolSelectionChangedListener(ToolSelectionChangedListenerOJ listener) {
        int index = toolSelectionChangedListeners.indexOf(listener);
        if (index > 0) {
            toolSelectionChangedListeners.remove(index);
        }
    }

    private void clearListeners() {
        toolListChangedListeners.clear();
        toolSelectionChangedListeners.clear();
    }

    public void fireToolSelectionChangedEvent(String name) {
        if (OJ.isValidData()) {
            ToolSelectionChangedEventOJ evt = new ToolSelectionChangedEventOJ(name);
            for (int i = 0; i < toolSelectionChangedListeners.size(); i++) {
                ((ToolSelectionChangedListenerOJ) toolSelectionChangedListeners.get(i)).toolSelectionChanged(evt);
            }
        }
    }

    public void fireToolListChangedEvent(String group) {
        if (OJ.isValidData()) {
            ToolListChangedEventOJ evt = new ToolListChangedEventOJ(group);
            for (int i = 0; i < toolListChangedListeners.size(); i++) {
                ((ToolListChangedListenerOJ) toolListChangedListeners.get(i)).toolListChanged(evt);
            }
        }
    }

//    classes used to sort tools
    class ToolsComparer implements Comparator {

        public int compare(Object obj1, Object obj2) {
            int i1 = ((ToolOJ) obj1).getId();
            int i2 = ((ToolOJ) obj2).getId();

            return Math.abs(i1) - Math.abs(i2);
        }
    }

    class ActionToolsComparer implements Comparator {

        public int compare(Object obj1, Object obj2) {
            int i1 = ((ActionToolOJ) obj1).getId();
            int i2 = ((ActionToolOJ) obj2).getId();

            return Math.abs(i1) - Math.abs(i2);
        }
    }
}
