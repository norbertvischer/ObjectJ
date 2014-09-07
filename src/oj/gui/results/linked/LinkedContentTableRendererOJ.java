/*
 * LinkedContentTableRendererOJ
 * fully documented
 * 
 * holds instructions how a linked result value needs to be rendered
 */
package oj.gui.results.linked;

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
public class LinkedContentTableRendererOJ extends JLabel implements TableCellRenderer {

    private boolean isSelected;
    private boolean hasFocus;
    private Font fontArial = Font.decode("Arial-12");
    public static Border emptyBorder = new EmptyBorder(2, 8, 2, 8);
    public static Color rowBackground = new Color(236, 241, 244);

    /**
     * Contains instructions to paint one table cell, depending
     * on "selected", "qualified", column color, and of course the value.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setOpaque(true);
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            if (row % 2 == 0) {
                setBackground(Color.WHITE);
            } else {
                setBackground(rowBackground);
            }
        }

        this.isSelected = isSelected;
        this.hasFocus = hasFocus;

        LinkedTableModelOJ.LinkedTableValueOJ val2 = (LinkedTableModelOJ.LinkedTableValueOJ) value;

        if (column == 0) {
            setHorizontalAlignment(SwingConstants.LEFT);
            if (val2 != null) {
                if (((LinkedTableModelOJ.LinkedTableValueOJ) value).qualified) {
                    setText(((LinkedTableModelOJ.LinkedTableValueOJ) value).content);
                    setForeground(((LinkedTableModelOJ.LinkedTableValueOJ) value).color);
                } else {
                    setText("{" + ((LinkedTableModelOJ.LinkedTableValueOJ) value).content + "}");
                    setForeground(Color.GRAY);
                }
            }
        } else {

            setHorizontalAlignment(SwingConstants.RIGHT);
            if (val2 != null) {
                setText(val2.content);
                if (val2.qualified) {
                    setForeground(((LinkedTableModelOJ.LinkedTableValueOJ) value).color);
                } else {
                    setForeground(Color.GRAY);
                }
            }
        }

        setFont(fontArial);
        setBorder(emptyBorder);

        return this;
    }
}
