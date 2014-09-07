/*
 * UnlinkedContentTableRendererOJ
 * fully documented
 *
 * holds instructions how a unlinked result value needs to be rendered
 */
package oj.gui.results.unlinked;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * For displaying a single result value, using the JLabel class
 */
public class UnlinkedContentTableRendererOJ extends JLabel implements TableCellRenderer {

    private boolean isSelected;
    private boolean hasFocus;
    private Font fontArial = Font.decode("Arial-12");
    public static Border emptyBorder = new EmptyBorder(2, 8, 2, 8);
    public static Color rowBackground = new Color(236, 236, 222);
    public static Color rowBackground2 = new Color(236, 236, 188);

    public UnlinkedContentTableRendererOJ() {
    }

    /**
     * Contains instructions to paint one table cell, depending
     * such as column color, odd/even background color, and of course the value.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setOpaque(true);
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            if (row % 2 == 0) {
                setBackground(rowBackground2);
            } else {
                setBackground(rowBackground);
            }
        }

        this.isSelected = isSelected;
        this.hasFocus = hasFocus;
        if (column == 0) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setText(((UnlinkedTableModelOJ.UnlinkedTableValueOJ) value).content);
        } else {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setText(((UnlinkedTableModelOJ.UnlinkedTableValueOJ) value).content);
            setForeground(((UnlinkedTableModelOJ.UnlinkedTableValueOJ) value).color);
        }

        setBorder(emptyBorder);
        setFont(fontArial);
        return this;
    }
}
