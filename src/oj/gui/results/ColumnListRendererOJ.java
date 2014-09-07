/* ColumnListRendererOJ.java
 * fully documented
 */
package oj.gui.results;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import oj.gui.results.ProjectResultsOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;

/**
 *  How to paint a single entry of the left-hand column list as checkbox
 */
public class ColumnListRendererOJ extends JCheckBox implements ListCellRenderer {

    private Font fontArial = Font.decode("Arial-12");

    public ColumnListRendererOJ() {
    }

    public Component getListCellRendererComponent(JList list, Object value, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) {

        int tab =ProjectResultsOJ.getInstance().getTab();
        if (value == null) {
            setSelected(false);
            setEnabled(false);
            setText("");
        } else {
            setSelected(!((ColumnOJ) value).getColumnDef().isHidden());
            setText(((ColumnOJ) value).getName());
            setEnabled(list.isEnabled());
            ColumnsOJ columns = oj.OJ.getData().getResults().getColumns();
            int lastLinked = columns.getAllColumnsCount() - columns.getUnlinkedColumnsCount();
            int jj = oj.OJ.getData().getResults().getColumns().getUnlinkedColumnsCount();
            if (index >= lastLinked) {
                setBackground(new Color(236, 236, 188));
            } else {
                setBackground(new Color(255, 255, 255));
            }

        }
        setFont(fontArial);
        setOpaque(false);
        return this;
    }
}
