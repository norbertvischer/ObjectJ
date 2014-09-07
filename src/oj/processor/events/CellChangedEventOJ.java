/*
 * CellChangedEvent.java
 * -- documented
 * After a change, such an object is created and passed to the listener
 *
 */
package oj.processor.events;


public class CellChangedEventOJ {

    public final static int CELL_OPEN_EVENT = 1;
    public final static int CELL_CLOSE_EVENT = 2;
    public final static int CELL_SELECT_EVENT = 3;
    public final static int CELL_UNSELECT_EVENT = 4;
    private int operation;
    private int cellIndex;

    public CellChangedEventOJ() {
    }

    public CellChangedEventOJ(int cellIndex, int operation) {
        this.cellIndex = cellIndex;
        this.operation = operation;
    }

    public int getOperation() {
        return operation;
    }

    public int getCellIndex() {
        return cellIndex;
    }
}
