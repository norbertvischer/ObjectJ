/*
 * YtemDefChangedEventOJ.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 */
package oj.processor.events;

public class YtemDefChangedEventOJ {

    public static final int YTEMDEF_ADDED = 1;
    public static final int YTEMDEF_EDITED = 2;
    public static final int YTEMDEF_DELETED = 3;
    public static final int YTEMDEF_MOVED = 4;
    public static final int COLLECT_MODE_CHANGED = 5;
    public static final int LABEL_VISIBILITY_CHANGED = 6;
    public static final int YTEMDEF_VISIBILITY_CHANGED = 7;
    public static final int YTEM_LAYER_VISIBILITY_CHANGED = 8;
    public static final int THREE_D_MODE_CHANGED = 9;
    private int operation;
    private String oldName;
    private String newName;

    public YtemDefChangedEventOJ(String name, int operation) {
        this.newName = name;
        this.oldName = name;
        this.operation = operation;
    }

    public YtemDefChangedEventOJ(String oldName, String newName, int operation) {
        this.oldName = oldName;
        this.newName = newName;
        this.operation = operation;
    }

    public YtemDefChangedEventOJ(int operation) {
        this.operation = operation;
    }

    public String getName() {
        return newName;
    }

    public String getNewName() {
        return newName;
    }

    public String getOldName() {
        return oldName;
    }

    public int getOperation() {
        return operation;
    }
}
