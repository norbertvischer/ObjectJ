/* LinkedHeaderRendererOJ.java
 * fully documented
 */
package oj.gui.results.linked;

import ij.util.FontUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import oj.OJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;

/**
 * For displaying one linked column header using the JLabel class.
 * In the JLabel we accommodate several lines to show statistic values - these stay in place when scrolling up or down
 */
public class LinkedHeaderRendererOJ extends JLabel implements TableCellRenderer {

    private static Font fontArialBold = Font.decode("Arial-BOLD-12");
	private static Font	 fontArialBoldItalic = FontUtil.getFont("Arial", Font.BOLD + Font.ITALIC, 12);
    private static Border headerBorder = new EmptyBorder(2, 8, 2, 8);
//    private static Icon triangleIcon = new ImageIcon(LinkedHeaderRendererOJ.class.getResource(OJ.ICONS+"TriangleWhite.gif"));
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBorder(headerBorder);
        setHorizontalAlignment(SwingConstants.RIGHT);//13.5.2019
        setBackground(oj.OJ.headerBackground);
		
		setFont(fontArialBold);
        if (column > 0 && column <= getVisibleSize()) {
            ColumnOJ col = getVisibleElementAt(column - 1);
			if(col.getColumnDef().isTextMode())
				setFont(fontArialBoldItalic);
            if ((col.getName() != null) && (col.getName().equals(OJ.getData().getResults().getColumns().getColumnLinkedSortName())) && (OJ.getData().getResults().getColumns().getColumnLinkedSortFlag() != ColumnsOJ.COLUM_SORT_FLAG_NONE)) {
                setForeground(Color.YELLOW);
            } else {
                setForeground(Color.WHITE);
            }
            //setIcon(triangleIcon);
        } else {
            setForeground(Color.WHITE);
            //setIcon(null);
        }
        //setFont(fontArialBold);
        if (value != null) {
            setText(value.toString());
        }

        if (column == 0) {
			table.getColumnModel().getColumn(0).setResizable(false);//13.5.2019
            setText("[Stat]");
        }
        setOpaque(true);
        return this;
    }

    /** returns the number of linked columns that are not hidden by user checkbox
     */
    private int getVisibleSize() {
        int count = 0;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (!((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) columns.getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * Returns the n-th non-hidden linked column (0-based)
     */
    public ColumnOJ getVisibleElementAt(int index) {
        int count = -1;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            if (!((ColumnOJ) columns.getColumnByIndex(i)).isUnlinkedColumn()) {
                if (!((ColumnOJ) columns.getColumnByIndex(i)).getColumnDef().isHidden()) {
                    count = count + 1;
                    if (count == index) {
                        return columns.getColumnByIndex(i);
                    }
                }
            }
        }
        return null;
    }
}
