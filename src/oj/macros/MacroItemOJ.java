/*
 * MacroItemOJ.java
 */
package oj.macros;

public class MacroItemOJ {

    private String name;
    private String description;
    private boolean autorun;
    private int shortcut;
    private boolean shiftEnabled;
    private int theToolType;
    public static final int ACTION_TOOL = 0;
    public static final int IMAGE_TOOL = 1;
    public static final int MENU_TOOL = 2;
    private String icon;

    public MacroItemOJ(String theName) {
        name = theName;
        autorun = false;
        description = "";
        shortcut = 0;
        shiftEnabled = false;
    }

    public int getToolType() {
        return theToolType;
    }

    public void setToolType(int toolType) {
        this.theToolType = toolType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getShortcut() {
        return shortcut;
    }

    public void setShortcut(int shortcut) {
        this.shortcut = shortcut;
    }

    public boolean isShiftEnabled() {
        return shiftEnabled;
    }

    public void setShiftEnabled(boolean shiftEnabled) {
        this.shiftEnabled = shiftEnabled;
    }

    public boolean isAutorun() {
        return autorun;
    }

    public void setAutorun(boolean autorun) {
        this.autorun = autorun;
    }
}
