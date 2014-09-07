/*
 * MenusOJ.java
 *
 * Installs menu items under the ObjectJ menu, plus project macros
 */
package oj.gui;

import ij.IJ;
import ij.ImageJ;
import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import oj.OJ;
import oj.macros.MacroItemOJ;
import oj.macros.MacroSetOJ;
import oj.gui.menuactions.ImageActionsOJ;
import oj.gui.menuactions.CellActionsOJ;
import oj.gui.menuactions.ProjectActionsOJ;
import oj.gui.menuactions.ResultsActionsOJ;
import oj.gui.menuactions.ViewActionsOJ;
import oj.processor.events.MacroChangedEventOJ;
import oj.processor.events.MacroChangedListenerOJ;

public class MenuManagerOJ implements ItemListener, MacroChangedListenerOJ {

    private boolean menuInstalled = false;
    private MenuBar menuBar;
    private Menu mnuObjectj;
    private Menu menuProjectFile;
    /**/ private MenuItem itemAboutObjectJ;
    /**/ private MenuItem itemNewProject;
    /**/ private MenuItem itemOpenProject;
    /**/ private MenuItem itemExportLinkedResults;
    /**/ private MenuItem itemSaveACopy;
    /**/ private MenuItem itemSaveEmptyCopy;
    /**/ private MenuItem itemExportXML;
    /**/ private MenuItem itemCloseProject;
    private MenuItem itemSaveProject;
    //---
    private Menu menuLinkedImages;
    /**/ private MenuItem itemLinkAnImage;
    /**/ private MenuItem itemLinkAllImages;
    /**/ private MenuItem itemLinkCurrentImage;
    /**/ private MenuItem itemForgetUnmarkedImages;
    /**/ private MenuItem itemPropagateScale;
    /**/ private MenuItem itemFlatten;
    private Menu menuObjects;
    /**/ private MenuItem itemShowObject;
    /**/ private MenuItem itemDeleteObjects;
    /**/ private MenuItem itemDeleteAllObjects;
    /**/ private MenuItem itemRoiToObject;
    private Menu menuResults;
    /**/ private MenuItem itemRecalculate;
     //private MenuItem itemReloadMacros;
    //---
    private MenuItem itemShowProjectWindow;
    private MenuItem itemShowObjectjTools;
    private MenuItem itemShowObjectjResult;
    private MenuItem itemShowMacroText;
    private MenuItem itemShowProjectFolder;
    private static MenuManagerOJ instance;
    private int mnuItemsCount = 11;//25.8.2010
    private Hashtable macroComands = new Hashtable();
    private Hashtable macroListeners = new Hashtable();
    private MacroActionListener macroActionListener = new MacroActionListener();
    private ShortcutManagerOJ shortcutManager = new ShortcutManagerOJ();
    private ArrayList<Menu> subMenus = new ArrayList();//29.7.2013

    public MenuManagerOJ(MenuBar menuBar) {
        this.menuBar = menuBar;
        instance = this;

        installMenu();
        setEnabled(false);
        OJ.getEventProcessor().addMacroChangedListener(this);//reactivated 3.4.2010
    }

    public static MenuManagerOJ getInstance() {
        return instance;
    }

    public void clearSubMenus() {
        subMenus.clear();
    }
    /* Now linearly start to add all menu items and submenus*/
    private void installMenu() {
        subMenus.clear();

        if (!menuInstalled) {
            mnuObjectj = new Menu("ObjectJ");
            if (IJ.isWindows()) {
                Menu help = menuBar.getMenu(menuBar.getMenuCount() - 1);
                Menu window = menuBar.getMenu(menuBar.getMenuCount() - 2);
                Menu plugins = menuBar.getMenu(menuBar.getMenuCount() - 3);
                menuBar.add(mnuObjectj);
                menuBar.add(plugins);
                menuBar.add(window);
                menuBar.add(help);
            } else {
                Menu window = menuBar.getMenu(menuBar.getMenuCount() - 2);
                Menu plugins = menuBar.getMenu(menuBar.getMenuCount() - 3);
                menuBar.add(mnuObjectj);
                menuBar.add(plugins);
                menuBar.add(window);
            }

            menuProjectFile = new Menu("Project");
            mnuObjectj.add(menuProjectFile);

            /**/ itemAboutObjectJ = new MenuItem("About ObjectJ...");
            /**/ itemAboutObjectJ.addActionListener(ProjectActionsOJ.AboutObjectJAction);
            /**/ menuProjectFile.add(itemAboutObjectJ);

            /**/ itemNewProject = new MenuItem("New Project...");
            /**/ itemNewProject.addActionListener(ProjectActionsOJ.NewProjectAction);
            /**/ menuProjectFile.add(itemNewProject);

            /**/ itemOpenProject = new MenuItem("Open Project...");
            /**/ itemOpenProject.addActionListener(ProjectActionsOJ.OpenProjectAction);
            /**/ menuProjectFile.add(itemOpenProject);

            /**/ itemExportLinkedResults = new MenuItem("Export Linked Results...");
            /**/ itemExportLinkedResults.addActionListener(ProjectActionsOJ.ExportLinkedResultsAction);
            /**/ menuProjectFile.add(itemExportLinkedResults);

            /**/ itemSaveACopy = new MenuItem("Save A Copy Of Project...");
            /**/ itemSaveACopy.addActionListener(ProjectActionsOJ.SaveCopyAction);
            /**/ menuProjectFile.add(itemSaveACopy);

            /**/ itemSaveEmptyCopy = new MenuItem("Save An Empty Copy...");
            /**/ itemSaveEmptyCopy.addActionListener(ProjectActionsOJ.SaveEmptyCopyAction);
            /**/ menuProjectFile.add(itemSaveEmptyCopy);

            /**/ itemExportXML = new MenuItem("Export Project As XML...");
            /**/ itemExportXML.addActionListener(ProjectActionsOJ.ExportAsXMLAction);
            /**/ menuProjectFile.add(itemExportXML);

            /**/ itemCloseProject = new MenuItem("Close Project");
            /**/ itemCloseProject.addActionListener(ProjectActionsOJ.CloseProjectAction);
            /**/ menuProjectFile.add(itemCloseProject);





            itemSaveProject = new MenuItem("Save Project");
            itemSaveProject.addActionListener(ProjectActionsOJ.SaveProjectAction);
            mnuObjectj.add(itemSaveProject);
            mnuObjectj.addSeparator();
//---
            menuLinkedImages = new Menu("Linked Images");
            mnuObjectj.add(menuLinkedImages);

            /**/ itemLinkAnImage = new MenuItem("Link Image from Project Folder...");
            /**/ itemLinkAnImage.addActionListener(ImageActionsOJ.LinkImageFileAction);
            /**/ menuLinkedImages.add(itemLinkAnImage);

            /**/ itemLinkAllImages = new MenuItem("Link All Images from Project Folder");
            /**/ itemLinkAllImages.addActionListener(ImageActionsOJ.LinkAllImagesAction);
            /**/ menuLinkedImages.add(itemLinkAllImages);

            /**/ itemLinkCurrentImage = new MenuItem("Link Front Image");
            /**/ itemLinkCurrentImage.addActionListener(ImageActionsOJ.LinkImagePlusAction);
            /**/ menuLinkedImages.add(itemLinkCurrentImage);


            /**/ itemForgetUnmarkedImages = new MenuItem("Forget Unmarked Images");
            /**/ itemForgetUnmarkedImages.addActionListener(ImageActionsOJ.RemoveAllImagesAction);
            /**/ menuLinkedImages.add(itemForgetUnmarkedImages);

            /**/ itemPropagateScale = new MenuItem("Propagate Scale to Linked Images...");
            /**/ itemPropagateScale.addActionListener(ImageActionsOJ.PropagateScaleAction);
            /**/ menuLinkedImages.add(itemPropagateScale);

            /**/ itemFlatten = new MenuItem("Flattened Duplicate");
            /**/ itemFlatten.addActionListener(ImageActionsOJ.FlattenAction);
            /**/ menuLinkedImages.add(itemFlatten);

            menuObjects = new Menu("Objects");
            mnuObjectj.add(menuObjects);

            /**/ itemShowObject = new MenuItem("Show Object #...");
            /**/ itemShowObject.addActionListener(CellActionsOJ.ShowCellAction);
            /**/ menuObjects.add(itemShowObject);

            /**/ itemDeleteObjects = new MenuItem("Delete Objects...");
            /**/ itemDeleteObjects.addActionListener(CellActionsOJ.DeleteCellsAction);
            /**/ menuObjects.add(itemDeleteObjects);

            /**/ itemDeleteAllObjects = new MenuItem("Delete All Objects");
            /**/ itemDeleteAllObjects.addActionListener(CellActionsOJ.DeleteAllCellsAction);
            /**/ menuObjects.add(itemDeleteAllObjects);

            /**/ itemRoiToObject = new MenuItem("Create Item from ROI");
            /**/ itemRoiToObject.addActionListener(CellActionsOJ.ROIToYtemAction);
            /**/ menuObjects.add(itemRoiToObject);

            menuResults = new Menu("Results");
            mnuObjectj.add(menuResults);

            /**/ itemRecalculate = new MenuItem("Recalculate");
            /**/ itemRecalculate.addActionListener(ResultsActionsOJ.RecalculateAction);
            /**/ menuResults.add(itemRecalculate);

//---
            itemShowProjectWindow = new MenuItem("Show Project Window", new MenuShortcut(KeyEvent.VK_F1, true));
            itemShowProjectWindow.addActionListener(ViewActionsOJ.SettingsAction);
            mnuObjectj.add(itemShowProjectWindow);

            itemShowObjectjTools = new MenuItem("Show ObjectJ Tools", new MenuShortcut(KeyEvent.VK_F2, true));
            itemShowObjectjTools.addActionListener(ViewActionsOJ.YtemListAction);
            mnuObjectj.add(itemShowObjectjTools);

            itemShowObjectjResult = new MenuItem("Show ObjectJ Results", new MenuShortcut(KeyEvent.VK_F3, true));
            itemShowObjectjResult.addActionListener(ViewActionsOJ.ResultsViewAction);
            mnuObjectj.add(itemShowObjectjResult);

            itemShowMacroText = new MenuItem("Show Embedded Macros", new MenuShortcut(KeyEvent.VK_F4, true));
            itemShowMacroText.addActionListener(ViewActionsOJ.ShowEmbeddedMacroAction);
            mnuObjectj.add(itemShowMacroText);

            itemShowProjectFolder = new MenuItem("Show in Finder/Explorer", new MenuShortcut(KeyEvent.VK_F5, true));
            itemShowProjectFolder.addActionListener(ViewActionsOJ.ShowProjectFolderAction);
            mnuObjectj.add(itemShowProjectFolder);
        }
    }

    private CheckboxMenuItem addCheckboxItem(Menu menu, String label, boolean state) {
        if (menu == null) {
            return null;
        }
        CheckboxMenuItem item;
        item = new CheckboxMenuItem(label);
        item.setState(state);
        item.addItemListener(this);
        menu.add(item);
        return item;
    }

    /**
     * Handles CheckboxMenuItem state changes.
     */
    public void itemStateChanged(ItemEvent e) {
    }

    void cleanMacroItems() {
        if (mnuObjectj != null) {
            for (int i = mnuObjectj.getItemCount() - 1; i > mnuItemsCount - 1; i--) {
                mnuObjectj.remove(i);
            }
        }

        shortcutManager.cleanMacroShortcuts();
    }

    public void reloadMacroItems() {
        cleanMacroItems();
        if (OJ.isValidData()) {
            MacroSetOJ macroSet = OJ.getData().getMacroSet();
            if (macroSet != null) {
                MacroItemOJ[] items = macroSet.macroItemsToArray();
                if (items.length > 0) {

                    MenuItem mnuItem = new MenuItem("-");//first separator
                    mnuObjectj.add(mnuItem);
                    for (int j = 0; j < items.length; j++) {
                        if (items[j].getToolType() != MacroItemOJ.IMAGE_TOOL && !items[j].isAutorun()) {
                            addMacroItem(mnuObjectj, items[j], macroSet);//29.7.2013
                        }
                    }

                    for (int j = 0; j < items.length; j++) {
                        if (items[j].isAutorun()) {
                            macroSet.runMacro(items[j].getName());
                        }
                    }
                }
            }


        }
        ArrayList errors = shortcutManager.getErrors();
        if (errors.size() > 0) {
            IJ.log("");
            IJ.log("Macro shortcuts dropped during loading of the project");
            IJ.log("-----------------------------------------------------");
            for (int i = 0; i < errors.size(); i++) {
                IJ.log((String) errors.get(i));
            }
        }
    }

    private void addMacroItem(Menu parent, MacroItemOJ macroItem, ActionListener listener) {
        if (mnuObjectj != null) {
            String theName = macroItem.getName();
            String actionName = theName;
            //macroitem has no indent, menu item has indent
            String indent = "   ";
            MenuItem mnuItem;
            if ((macroItem.getShortcut() == 0) || !shortcutManager.isAvailableShortcut(macroItem.getShortcut(), macroItem.isShiftEnabled(), theName)) {
                mnuItem = new MenuItem(indent + theName);
                macroListeners.put(theName, listener);
                macroComands.put(theName, theName);
            } else {
                if (macroItem.isShiftEnabled()) {
                    MenuShortcut shortcut = new MenuShortcut(macroItem.getShortcut(), true);
                    String extName = String.format("%s [%s]", theName, KeyEvent.getKeyText(KeyEvent.SHIFT_MASK) + KeyEvent.getKeyText(macroItem.getShortcut()));
                    actionName = extName;
                    mnuItem = new MenuItem(indent + extName);
                    macroListeners.put(extName, listener);
                    macroComands.put(extName, theName);
                    shortcutManager.addShortcut(theName, shortcut, listener);
                } else {
                    MenuShortcut shortcut = new MenuShortcut(macroItem.getShortcut());
                    String extName = String.format("%s [%s]", theName, KeyEvent.getKeyText(macroItem.getShortcut()));
                     actionName = extName;
                    mnuItem = new MenuItem(indent + extName);
                    macroListeners.put(extName, listener);
                    macroComands.put(extName, theName);
                    shortcutManager.addShortcut(theName, shortcut, listener);
                }
                
            }
            Menu subMenu = null;
            // is it a submenu?
            String parentStr = null;
            String labelStr = null;
                            

            int pos = theName.indexOf(">");
            if (pos > 1 && theName.startsWith("<")) {
                parentStr = theName.substring(1, pos);
                labelStr = actionName.substring(pos +1);
            }

            if (parentStr != null) {
                String indentedParent = indent + parentStr;
                mnuItem.setActionCommand( actionName);
                mnuItem.setLabel(labelStr);
                for (int jj = 0; jj < subMenus.size(); jj++) {

                    String aName = subMenus.get(jj).getName();
                    if (aName.equals(indentedParent)) {
                        subMenu = subMenus.get(jj);
                    }
                }
                if (subMenu == null) {
                    subMenu = new Menu(indentedParent);
                    subMenu.setName(indentedParent);
                    subMenus.add(subMenu);
                    parent.add(subMenu);

                }
                subMenu.add(mnuItem);
            } else {
                parent.add(mnuItem);//normal menu item
            }

            mnuItem.addActionListener(macroActionListener);
        }
    }

    public void macroChanged(MacroChangedEventOJ evt) {
        reloadMacroItems();
    }
    
    public class MacroActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            String name = evt.getActionCommand();//19.9.2010
            name = name.trim();
            ImageJ.setCommandName(name);
            Object obj = macroListeners.get(name);
            if (obj != null) {
                ActionEvent action = new ActionEvent(evt.getSource(), 0, (String) macroComands.get(name));
                ((ActionListener) obj).actionPerformed(action);
            }
        }
    }

    public static void close() {
        if (instance != null) {
            instance.cleanMacroItems();
            instance.setEnabled(false);
        }
    }

    public void setEnabled(boolean enabled) {
        itemCloseProject.setEnabled(enabled);
        menuLinkedImages.setEnabled(enabled);
        menuObjects.setEnabled(enabled);
        itemSaveProject.setEnabled(enabled);
        itemExportLinkedResults.setEnabled(enabled);
        itemSaveACopy.setEnabled(enabled);
        itemSaveEmptyCopy.setEnabled(enabled);
        itemExportXML.setEnabled(enabled);
        menuResults.setEnabled(enabled);
        itemRecalculate.setEnabled(enabled);

        itemShowMacroText.setEnabled(enabled);

        itemShowProjectWindow.setEnabled(enabled);
        itemShowObjectjResult.setEnabled(enabled);
        itemShowObjectjTools.setEnabled(enabled);
        itemShowProjectFolder.setEnabled(enabled);
        itemAboutObjectJ.setEnabled(true);
    }
}
