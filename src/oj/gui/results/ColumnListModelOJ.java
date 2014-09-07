/* ColumnListModelOJ.java
 * fully documented
 */
package oj.gui.results;

import javax.swing.AbstractListModel;
import oj.OJ;
import oj.project.DataOJ;
import oj.project.results.ColumnOJ;

/**
 *
 * Handles checkboxes for column visibility on the left-hand side of the ObjectJ results window
 */
public class ColumnListModelOJ extends AbstractListModel {

    public ColumnListModelOJ(){
    }

    /**
     * @return number of all defined columns
     */
    public int getSize() {
        return OJ.getData().getResults().getColumns().getAllColumnsCount();
    }

    /**
     * returns n-th ColumnOJ
     */
    public Object getElementAt(int index) {
        if (index >= 0 && index < OJ.getData().getResults().getColumns().getAllColumnsCount())
            return OJ.getData().getResults().getColumns().getColumnByIndex(index);
        return null;
    }


    /**
     * Updates the list of checkboxes
     */
    public void fireColumChanged() {
        fireContentsChanged(this, 0, getSize() - 1);
    }
}