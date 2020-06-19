/*
 * LinkedHeaderTableRendererOJ.java
 * fully documented
 *
 * holds instructions how a statistic table cell in the linked table needs to be rendered
 */
package oj.gui.results.linked;

import ij.IJ;
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
 *
 * For displaying the results headers
 */
public class LinkedStatRowsRendererOJ extends JLabel implements TableCellRenderer {

    private static Font fontArial = Font.decode("Arial-12");
    private static Border headerBorder = new EmptyBorder(2, 8, 2, 8);
    private static Color statisticBackground = new Color(133, 133, 133);

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        setBorder(headerBorder);
        setBackground(statisticBackground);
        setForeground(Color.WHITE);
        setFont(fontArial);

        setIcon(null);
        if (value != null) {
            setText(value.toString());
        }
        setOpaque(true);
        return this;
    }
}
