/**
 * Item definition Panel within Project window
 *
 */
package oj.gui.settings;

import ij.IJ;
import ij.gui.GenericDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import oj.OJ;
import oj.project.YtemDefOJ;
import oj.graphics.RoundPanelOJ;
import oj.gui.ToolsWindowOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.project.YtemDefsOJ;

public class YtemDefsSettingsOJ extends javax.swing.JPanel implements TableColumnModelListener, IControlPanelOJ, YtemDefChangedListenerOJ {

    private YtemDefsHeaderRenderer ytemDefsHeaderRenderer = new YtemDefsHeaderRenderer();
    private YtemDefsTableRenderer ytemDefsTableRenderer = new YtemDefsTableRenderer();
    private MarkerRendererOJ markerRenderer = new MarkerRendererOJ();
    private ColorRendererOJ colorRenderer = new ColorRendererOJ();
    private LineRendererOJ lineRenderer = new LineRendererOJ();
    private TypeRendererOJ typeRenderer = new TypeRendererOJ();
    private JTextField cloneEditor = new JTextField();
    private JTextField nameEditor = new JTextField();
    private JComboBox typeSelector = new JComboBox();
    private JComboBox lineSelector = new JComboBox();
    private JComboBox colorSelector = new JComboBox();
    private JComboBox markerSelector = new JComboBox();
    private Dimension panelSize = new Dimension(621, 325);

    /**
     * Creates new form ObjectDefSettingsOJ
     */
    public YtemDefsSettingsOJ() {
        initComponents();
        initExtComponents();

        OJ.getEventProcessor().addYtemDefChangedListener(this);
    }

    public void close() {
        OJ.getEventProcessor().removeYtemDefChangedListener(this);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        chkShowObjectLabel = new javax.swing.JCheckBox();
        chkCompositeObjects = new javax.swing.JCheckBox();
        chk3DObjects = new javax.swing.JCheckBox();
        chkVisibilitySwitch = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new RoundPanelOJ();
        jPanel8 = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnRebuild = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblYtemDefs = new javax.swing.JTable();
        tblYtemDefs.getColumnModel().addColumnModelListener(this);

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 22, 5, 5));
        jPanel1.setLayout(new java.awt.GridLayout(2, 2, 4, 4));

        chkShowObjectLabel.setText("Show Object Label");
        chkShowObjectLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowObjectLabelActionPerformed(evt);
            }
        });
        jPanel1.add(chkShowObjectLabel);

        chkCompositeObjects.setText("Composite Objects");
        chkCompositeObjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCompositeObjectsActionPerformed(evt);
            }
        });
        jPanel1.add(chkCompositeObjects);

        chk3DObjects.setText("3D Objects");
        chk3DObjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk3DObjectsActionPerformed(evt);
            }
        });
        jPanel1.add(chk3DObjects);

        chkVisibilitySwitch.setLabel("Selective Item Visibility");
        chkVisibilitySwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVisibilitySwitchActionPerformed(evt);
            }
        });
        jPanel1.add(chkVisibilitySwitch);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 16, 20, 16));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 10, 1));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        btnNew.setFocusPainted(false);
        btnNew.setLabel("New Item Type");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel8.add(btnNew);

        btnRemove.setText("Remove");
        btnRemove.setEnabled(false);
        btnRemove.setFocusPainted(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jPanel8.add(btnRemove);

        btnRebuild.setText("Rebuild");
        btnRebuild.setEnabled(false);
        btnRebuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRebuildActionPerformed(evt);
            }
        });
        jPanel8.add(btnRebuild);

        jPanel4.add(jPanel8, java.awt.BorderLayout.PAGE_END);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 10));
        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setOpaque(false);
        jScrollPane1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jScrollPane1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        tblYtemDefs.setModel(new YtemDefsTableModel());
        tblYtemDefs.setFocusable(false);
        tblYtemDefs.setGridColor(new java.awt.Color(204, 204, 204));
        tblYtemDefs.setIntercellSpacing(new java.awt.Dimension(2, 2));
        tblYtemDefs.setOpaque(false);
        tblYtemDefs.setRequestFocusEnabled(false);
        tblYtemDefs.setRowHeight(26);
        tblYtemDefs.setShowHorizontalLines(false);
        tblYtemDefs.setShowVerticalLines(false);
        tblYtemDefs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblYtemDefsMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblYtemDefs);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void chkShowObjectLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowObjectLabelActionPerformed
        OJ.getData().getYtemDefs().setShowCellNumber(chkShowObjectLabel.isSelected());
        OJ.getEventProcessor().fireYtemDefChangedEvent(YtemDefChangedEventOJ.LABEL_VISIBILITY_CHANGED);
    }//GEN-LAST:event_chkShowObjectLabelActionPerformed

    private void chkCompositeObjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCompositeObjectsActionPerformed
        int flags = evt.getModifiers();
        if ((flags & evt.ALT_MASK) > 0) {
            OJ.getData().getYtemDefs().setComposite(chkCompositeObjects.isSelected());
            OJ.getEventProcessor().fireYtemDefChangedEvent(null, YtemDefChangedEventOJ.COLLECT_MODE_CHANGED);
        } else {
            chkCompositeObjects.setSelected(!chkCompositeObjects.isSelected());//undo
            GenericDialog gd = new GenericDialog("Composite Objects", IJ.getInstance());
            gd.addMessage("Alt key must be down to change the 'Composite' checkbox");
            gd.addMessage("For more information, click 'Help'");
            gd.addHelp("http://simon.bio.uva.nl/objectj/3b-ManualTools.html");
            gd.showDialog();
        }

    }//GEN-LAST:event_chkCompositeObjectsActionPerformed

    private void chk3DObjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk3DObjectsActionPerformed
        OJ.getData().getYtemDefs().set3DYtems(chk3DObjects.isSelected());
        OJ.getEventProcessor().fireYtemDefChangedEvent(YtemDefChangedEventOJ.THREE_D_MODE_CHANGED);
    }//GEN-LAST:event_chk3DObjectsActionPerformed

    private void chkVisibilitySwitchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVisibilitySwitchActionPerformed
        OJ.getData().getYtemDefs().setYtemVisibilitySwitchEnabled(chkVisibilitySwitch.isSelected());
        // if (chkVisibilitySwitch.isSelected()) 15.7.2008 n_
        {
            for (int i = 0; i < OJ.getData().getYtemDefs().getYtemDefsCount(); i++) {
                OJ.getData().getYtemDefs().getYtemDefByIndex(i).setVisible(true);
            }
        }
        if (ToolsWindowOJ.getYtemDefListOJ() != null) {
            ToolsWindowOJ.getYtemDefListOJ().updateVisibilityView();
        }
    }//GEN-LAST:event_chkVisibilitySwitchActionPerformed

private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
    int index = OJ.getData().getYtemDefs().getYtemDefsCount() + 1;
    String name = "Item" + index;
    while (OJ.getData().getYtemDefs().getYtemDefByName(name) != null) {
        index += 1;
        name = "Item" + index;
    }
    YtemDefOJ ytemDef = new YtemDefOJ(name);
    OJ.getData().getYtemDefs().addYtemDef(ytemDef);
    ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableUpdated();
}//GEN-LAST:event_btnNewActionPerformed

private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
    int index = tblYtemDefs.getSelectedRow();
    if (index >= 0) {
        String objectDefName = OJ.getData().getYtemDefs().getYtemDefByIndex(index).getYtemDefName();
        if (OJ.getData().getCells().getYtemCount(objectDefName) > 0) {
            int ret = JOptionPane.showConfirmDialog(this, "There are items of this type '" + objectDefName + "'. \nIf you continue they will be deleted", "Continue?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ret != JOptionPane.OK_OPTION) {
                return;
            }
        }
        OJ.getData().getYtemDefs().removeYtemDefByIndex(index);
        tblYtemDefs.clearSelection();
        ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableStructureChanged();
    }
}//GEN-LAST:event_btnRemoveActionPerformed

private void tblYtemDefsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblYtemDefsMousePressed
    int index = tblYtemDefs.getSelectedRow();
    if (index >= OJ.getData().getYtemDefs().getYtemDefsCount()) {
        btnRemove.setEnabled(false);
    } else {
        btnRemove.setEnabled(index >= 0);
    }
}//GEN-LAST:event_tblYtemDefsMousePressed

private void jScrollPane1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jScrollPane1AncestorAdded
    jScrollPane1.getColumnHeader().setOpaque(false);
}//GEN-LAST:event_jScrollPane1AncestorAdded

private void btnRebuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRebuildActionPerformed
    ij.IJ.showMessage("Rebuilding");// 20.3.2009
    close();
}//GEN-LAST:event_btnRebuildActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRebuild;
    private javax.swing.JButton btnRemove;
    private javax.swing.JCheckBox chk3DObjects;
    private javax.swing.JCheckBox chkCompositeObjects;
    private javax.swing.JCheckBox chkShowObjectLabel;
    private javax.swing.JCheckBox chkVisibilitySwitch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblYtemDefs;
    // End of variables declaration//GEN-END:variables

    public synchronized void ytemDefChanged(YtemDefChangedEventOJ evt) {//9.9.2009
        switch (evt.getOperation()) {
            case YtemDefChangedEventOJ.LABEL_VISIBILITY_CHANGED:
                chkShowObjectLabel.setSelected(OJ.getData().getYtemDefs().getShowCellNumber());
                break;
            case YtemDefChangedEventOJ.COLLECT_MODE_CHANGED:
                chkCompositeObjects.setSelected(OJ.getData().getYtemDefs().isComposite());
                break;
            case YtemDefChangedEventOJ.THREE_D_MODE_CHANGED:
                chk3DObjects.setSelected(OJ.getData().getYtemDefs().is3DYtems());
                break;
            case YtemDefChangedEventOJ.YTEMDEF_VISIBILITY_CHANGED:
                chkVisibilitySwitch.setSelected(OJ.getData().getYtemDefs().isYtemVisibilitySwitchEnabled());
                break;
            case YtemDefChangedEventOJ.YTEMDEF_ADDED:
                btnRemove.setEnabled(false);
                ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableStructureChanged();
                break;
            default:
                btnRemove.setEnabled(false);
                ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableUpdated(); //15.9.2009: wasn't able to select a poygon for the butterfly!
        }
    }

    private void initExtComponents() {
        tblYtemDefs.setEnabled(true);
        tblYtemDefs.getTableHeader().setOpaque(false);
        tblYtemDefs.getTableHeader().setDefaultRenderer(ytemDefsHeaderRenderer);

        jScrollPane1.setBorder(new EmptyBorder(0, 0, 0, 0));
        jScrollPane1.getViewport().setBorder(null);
        jScrollPane1.getViewport().setOpaque(false);

        chk3DObjects.setSelected(OJ.getData().getYtemDefs().is3DYtems());
        chkCompositeObjects.setSelected(OJ.getData().getYtemDefs().isComposite());
        chkShowObjectLabel.setSelected(OJ.getData().getYtemDefs().getShowCellNumber());
        chkVisibilitySwitch.setSelected(OJ.getData().getYtemDefs().isYtemVisibilitySwitchEnabled());

        typeSelector.setModel(new TypeModelOJ());
        typeSelector.setRenderer(typeRenderer);
        typeSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkForOpenCell();
                int index = tblYtemDefs.getSelectedRow();
                int typeIndex = typeSelector.getSelectedIndex();
                YtemDefsOJ ytemDefs = OJ.getData().getYtemDefs();
                YtemDefOJ ytemDef = ytemDefs.getYtemDefByIndex(index);
                TypeModelOJ typeModel = (TypeModelOJ) typeSelector.getModel();

                Object theObject = typeModel.getElementAt(typeIndex);//16.9.2009
                if (theObject == null) {
                    ij.IJ.showMessage("Item shape input ignored - please try again");

                } else {
                    int type = ((Integer) theObject).intValue();// will it crash here?

                    if (ytemDef.getYtemType() != type) {
                        ytemDef.setYtemDefType(type);
                    }
                }
            }
        });

        lineSelector.setModel(new LineModelOJ());
        lineSelector.setRenderer(lineRenderer);
        lineSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkForOpenCell();
                int index = tblYtemDefs.getSelectedRow();
                int lineIndex = lineSelector.getSelectedIndex();
                YtemDefOJ objectDef = OJ.getData().getYtemDefs().getYtemDefByIndex(index);
                LineModelOJ lineModel = (LineModelOJ) lineSelector.getModel();//16.9.2009
                Object element = lineModel.getElementAt(lineIndex);
                if (element == null) {
                    ij.IJ.showMessage("Linetype input ignored - please try again");
                } else {
                    int lineType = ((Integer) element).intValue();

                    if (objectDef.getLineType() != lineType) {
                        objectDef.setLineType(lineType);
                    }
                }
            }
        });

        markerSelector.setModel(new MarkerModelOJ());
        markerSelector.setRenderer(markerRenderer);
        markerSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ij.IJ.wait(200);
                checkForOpenCell();
                int index = tblYtemDefs.getSelectedRow();
                int markerIndex = markerSelector.getSelectedIndex();
                YtemDefOJ objectDef = OJ.getData().getYtemDefs().getYtemDefByIndex(index);
                MarkerModelOJ markerModel = (MarkerModelOJ) markerSelector.getModel();//16.9.2009
                if (markerModel == null) {
                    OJ.debugLog("markerModel==null");
                }
                Object theElement = markerModel.getElementAt(markerIndex);
                if (theElement == null) {
                    ij.IJ.showMessage("Marker input ignored - please try again");
                } else {
                    int markerType = ((Integer) theElement).intValue();

                    if (objectDef.getMarkerType() != markerType) {
                        objectDef.setMarkerType(markerType);
                    }
                }
            }
        });

        colorSelector.setModel(new ColorModelOJ());
        colorSelector.setRenderer(colorRenderer);
        colorSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkForOpenCell();
                int index = tblYtemDefs.getSelectedRow();
                int colorIndex = colorSelector.getSelectedIndex();
                YtemDefOJ objectDef = OJ.getData().getYtemDefs().getYtemDefByIndex(index);
                Color lineColor = objectDef.getLineColor();//16.9.2009

                if (colorIndex == (((ColorModelOJ) colorSelector.getModel()).getSize() - 1)) {
                    Color fancyColor  = JColorChooser.showDialog(null, "Choose Object Color", lineColor);
                    if (fancyColor != null) {
                        objectDef.setLineColor(fancyColor);
                    }
                } else {


                    ColorModelOJ colorModel = (ColorModelOJ) colorSelector.getModel();
                    Object theElement = colorModel.getElementAt(colorIndex);
                    if (theElement == null) {
                        ij.IJ.showMessage("Color input ignored - please try again");
                    } else {
                        lineColor = (Color) theElement;
                    }
                    if (!objectDef.getLineColor().equals(lineColor) && lineColor != null) {
                        objectDef.setLineColor(lineColor);
                    }
                }
            }
        });

        nameEditor.addFocusListener(new FocusListener() {
            int index;
            YtemDefOJ ytemDef;

            public void focusGained(FocusEvent event) {
                checkForOpenCell();
                index = tblYtemDefs.getSelectedRow();
                if (index < OJ.getData().getYtemDefs().getYtemDefsCount()) {
                    ytemDef = OJ.getData().getYtemDefs().getYtemDefByIndex(index);
                    nameEditor.setText(ytemDef.getYtemDefName());
                } else {
                    nameEditor.setText("");
                    ytemDef = null;
                }
            }

            public void focusLost(FocusEvent event) {
                if (ytemDef != null) {
                    ytemDef.setYtemDefName(nameEditor.getText().trim());
                    ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableRowUpdated(index);
                } else {
                    String name = nameEditor.getText().trim();
                    if (!name.equals("")) {
                        ytemDef = new YtemDefOJ(name);
                        OJ.getData().getYtemDefs().addYtemDef(ytemDef);
                        btnRemove.setEnabled(true);
                    }
                }
                ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableRowUpdated(index);
            }
        });

        cloneEditor.addFocusListener(new FocusListener() {
            int index;
            YtemDefOJ ytemDef;

            public void focusGained(FocusEvent event) {
                checkForOpenCell();
                index = tblYtemDefs.getSelectedRow();
                ytemDef = OJ.getData().getYtemDefs().getYtemDefByIndex(index);
                cloneEditor.setText(Integer.toString(ytemDef.getCloneMax()));
            }

            public void focusLost(FocusEvent event) {
                if (ytemDef != null) {
                    try {
                        int value = Integer.parseInt(cloneEditor.getText().trim());
                        ytemDef.setCloneMax(value);
                    } catch (Exception e) {
                    }

                    ((YtemDefsTableModel) tblYtemDefs.getModel()).fireTableRowUpdated(index);
                }
            }
        });

    }

    private void checkForOpenCell() {
        if (OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ) {
            ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).closeCell();
        }
    }

    public void columnAdded(TableColumnModelEvent event) {
        int toIndex = event.getToIndex();
        if (event.getSource() == tblYtemDefs.getColumnModel()) {
            TableColumn tableCol = (TableColumn) tblYtemDefs.getColumnModel().getColumn(toIndex);
            tableCol.setCellRenderer(ytemDefsTableRenderer);
            switch (toIndex) {
                case 0:
                    tableCol.setWidth(22);
                    tableCol.setMaxWidth(22);
                    tableCol.setPreferredWidth(22);
                    break;
                case 1:
                    tableCol.setWidth(100);
                    tableCol.setPreferredWidth(100);
                    tableCol.setCellEditor(new DefaultCellEditor(nameEditor));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 2:
                    tableCol.setWidth(98);
                    tableCol.setPreferredWidth(98);
                    tableCol.setCellEditor(new DefaultCellEditor(typeSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 3:
                    tableCol.setWidth(24);
                    tableCol.setPreferredWidth(24);
                    tableCol.setCellEditor(new DefaultCellEditor(cloneEditor));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 4:
                    tableCol.setWidth(70);
                    tableCol.setPreferredWidth(70);
                    tableCol.setCellEditor(new DefaultCellEditor(markerSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 5:
                    tableCol.setWidth(90);
                    tableCol.setPreferredWidth(90);
                    tableCol.setCellEditor(new DefaultCellEditor(lineSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 6:
                    tableCol.setWidth(80);
                    tableCol.setPreferredWidth(80);
                    tableCol.setCellEditor(new DefaultCellEditor(colorSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
            }
        }
    }

    public void columnRemoved(TableColumnModelEvent e) {
    }

    public void columnMoved(TableColumnModelEvent e) {
    }

    public void columnMarginChanged(ChangeEvent e) {
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
    }

    public class YtemDefsHeaderRenderer extends JLabel implements TableCellRenderer {

        private Font fontArial = Font.decode("Arial-11");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(5, 6, 7, 3));
            setText((String) value);
            setFont(fontArial);
            setOpaque(false);
            return this;
        }
    }

    public class YtemDefsTableRenderer extends /*RoundLabelOJ*/ JLabel implements TableCellRenderer {

        private Font fontArial = Font.decode("Arial-11");
        private Font fontArialBold = Font.decode("Arial-BOLD-14");
        private Border emptyBorder2 = new EmptyBorder(2, 2, 2, 2);
        private Border emptyBorder4 = new EmptyBorder(2, 6, 2, 4);
        boolean borderEnabled = true;
        boolean backgroundEnabled = true;
        Color borderColor = Color.LIGHT_GRAY;

        public void paint(Graphics g) {
            if (backgroundEnabled) {
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            if (borderEnabled) {
                g.setColor(borderColor);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            super.paint(g);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            backgroundEnabled = true;
            borderEnabled = true;
            switch (column) {
                case 0:
                    setHorizontalAlignment(SwingConstants.CENTER);
                    backgroundEnabled = false;
                    setBorder(emptyBorder2);
                    borderEnabled = (false);
                    break;
                case 1:
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 2:
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 3:
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 4:
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 5:
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 6:
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
            }

            if (column == 0) {
                setFont(fontArialBold);
                if (tblYtemDefs.getSelectedRow() == row) {
                    setText("*");
                } else {
                    setText("");
                }
                setIcon(null);
            } else {
                setFont(fontArial);
                setText(((JLabel) value).getText());
                setIcon(((JLabel) value).getIcon());
            }
            //borderColor = Color.LIGHT_GRAY;
            setForeground(Color.BLACK);
            setFocusable(false);
            return this;
        }
    }

    private class YtemDefsTableModel extends AbstractTableModel {

        public String getColumnName(int col) {

            switch (col) {
                case 0:
                    return "";
                case 1:
                    return "Item Name";
                case 2:
                    return "Item Shape";
                case 3:
                    return "Clones";
                case 4:
                    return "Marker Type";
                case 5:
                    return "Line Type";
                case 6:
                    return "Item Color";
            }
            return "";
        }

        public int getRowCount() {
            return Math.max(4, OJ.getData().getYtemDefs().getYtemDefsCount());
        }

        public int getColumnCount() {
            return 7;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            JLabel label = new JLabel();
            if (rowIndex >= OJ.getData().getYtemDefs().getYtemDefsCount()) {
                label.setText("");
                label.setIcon(null);
                return label;
            }
            YtemDefOJ ytemDef = OJ.getData().getYtemDefs().getYtemDefByIndex(rowIndex);
            switch (columnIndex) {
                case 0:
                    label.setText("");
                    label.setIcon(null);
                    break;
                case 1:
                    label.setText(ytemDef.getYtemDefName());
                    label.setIcon(null);
                    break;
                case 2:
                    label.setText(YtemDefOJ.getTypeName(ytemDef.getYtemType()));
                    label.setIcon(typeRenderer.getTypeIcon(ytemDef.getYtemType()));
                    break;
                case 3:
                    label.setText(Integer.toString(ytemDef.getCloneMax()));
                    label.setIcon(null);
                    break;
                case 4:
                    label.setText(markerRenderer.getMarkerName(ytemDef.getMarkerType()));
                    label.setIcon(markerRenderer.getMarkerIcon(ytemDef.getMarkerType()));
                    break;
                case 5:
                    label.setText(lineRenderer.getLineName(ytemDef.getLineType()));
                    label.setIcon(lineRenderer.getLineIcon(ytemDef.getLineType()));
                    break;
                case 6:
                    label.setText(colorRenderer.getColorName(ytemDef.getLineColor()));
                    label.setIcon(colorRenderer.getColorIcon(ytemDef.getLineColor()));
                    break;
            }
            return label;
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 1) {
                return (row <= OJ.getData().getYtemDefs().getYtemDefsCount());
            } else {
                if (col > 1) {
                    return (row < OJ.getData().getYtemDefs().getYtemDefsCount());
                } else {
                    return false;
                }
            }
        }

        public void fireTableUpdated() {
            fireTableRowsUpdated(0, getRowCount());
        }

        public void fireTableRowUpdated(int index) {
            fireTableRowsUpdated(index, index);
        }
    }

    class TypeModelOJ extends DefaultComboBoxModel {

        public int getSize() {
            return 6;
        }

        public Object getElementAt(int index) {
            if (index < 6) {
                switch (index) {
                    case 0:
                        return new Integer(YtemDefOJ.YTEM_TYPE_ANGLE);
                    case 1:
                        return new Integer(YtemDefOJ.YTEM_TYPE_LINE);
                    case 2:
                        return new Integer(YtemDefOJ.YTEM_TYPE_POINT);
                    case 3:
                        return new Integer(YtemDefOJ.YTEM_TYPE_POLYGON);
                    case 4:
                        return new Integer(YtemDefOJ.YTEM_TYPE_ROI);
                    case 5:
                        return new Integer(YtemDefOJ.YTEM_TYPE_SEGLINE);
                }
                return null;
            } else {
                return null;
            }
        }
    }

    class TypeRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(0, 8, 3, 3);
        private final Icon angleIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Angle.gif"));
        private final Icon lineIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Line.gif"));
        private final Icon pointIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Point.gif"));
        private final Icon roiIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/ROI.gif"));
        private final Icon polyIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Poly.gif"));
        private final Icon seglineIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/SegLine.gif"));

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) {
            if (value == null) {
                return new JLabel();
            }
            setOpaque(true);
            setBorder(cellBorder);
            setBackground(Color.WHITE);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            if (value instanceof String) {
                setText((String) value);
                setIcon(null);
            } else {
                Integer type = (Integer) value;
                setText(YtemDefOJ.getTypeName(type.intValue()));
                setIcon(getTypeIcon(type.intValue()));
            }
            return this;
        }

        public Icon getTypeIcon(int objectType) {
            switch (objectType) {
                case YtemDefOJ.YTEM_TYPE_ANGLE:
                    return angleIcon;
                case YtemDefOJ.YTEM_TYPE_LINE:
                    return lineIcon;
                case YtemDefOJ.YTEM_TYPE_POINT:
                    return pointIcon;
                case YtemDefOJ.YTEM_TYPE_POLYGON:
                    return polyIcon;
                case YtemDefOJ.YTEM_TYPE_ROI:
                    return roiIcon;
                case YtemDefOJ.YTEM_TYPE_SEGLINE:
                    return seglineIcon;
            }
            return null;
        }
    }

    class MarkerModelOJ extends DefaultComboBoxModel {

        public int getSize() {
            return 6;
        }

        public Object getElementAt(int index) {
            if (index < 6) {
                switch (index) {
                    case 0:
                        return new Integer(YtemDefOJ.MARKER_TYPE_CROSS);
                    case 1:
                        return new Integer(YtemDefOJ.MARKER_TYPE_DIAMOND);
                    case 2:
                        return new Integer(YtemDefOJ.MARKER_TYPE_DOT);
                    case 3:
                        return new Integer(YtemDefOJ.MARKER_TYPE_PIXEL);
                    case 4:
                        return new Integer(YtemDefOJ.MARKER_TYPE_PLUS);
                    case 5:
                        return new Integer(YtemDefOJ.MARKER_TYPE_SQUARE);
                }
                return null;
            } else {
                return null;
            }
        }

        public int getMarkerIndex(int markerType) {
            switch (markerType) {
                case YtemDefOJ.MARKER_TYPE_CROSS:
                    return 0;
                case YtemDefOJ.MARKER_TYPE_DIAMOND:
                    return 1;
                case YtemDefOJ.MARKER_TYPE_DOT:
                    return 2;
                case YtemDefOJ.MARKER_TYPE_PIXEL:
                    return 3;
                case YtemDefOJ.MARKER_TYPE_PLUS:
                    return 4;
                case YtemDefOJ.MARKER_TYPE_SQUARE:
                    return 5;
            }
            return 0;
        }
    }

    class MarkerRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(0, 8, 3, 3);
        private final Icon crossIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Cross.gif"));
        private final Icon diamondIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Diamond.gif"));
        private final Icon dotIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Dot.gif"));
        private final Icon pixelIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Pixel.gif"));
        private final Icon plusIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Plus.gif"));
        private final Icon squareIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Square.gif"));

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) {
            if (value == null) {
                return new JLabel();
            }
            setOpaque(true);
            setBorder(cellBorder);
            setBackground(Color.WHITE);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            if (value instanceof String) {
                setText((String) value);
                setIcon(null);
            } else {
                Integer marker = (Integer) value;
                setText(getMarkerName(marker.intValue()));
                setIcon(getMarkerIcon(marker.intValue()));
            }
            return this;
        }

        public String getMarkerName(int markerType) {
            switch (markerType) {
                case YtemDefOJ.MARKER_TYPE_CROSS:
                    return "Cross";
                case YtemDefOJ.MARKER_TYPE_DIAMOND:
                    return "Diamond";
                case YtemDefOJ.MARKER_TYPE_DOT:
                    return "Dot";
                case YtemDefOJ.MARKER_TYPE_PIXEL:
                    return "Pixel";
                case YtemDefOJ.MARKER_TYPE_PLUS:
                    return "Plus";
                case YtemDefOJ.MARKER_TYPE_SQUARE:
                    return "Square";
            }
            return "";
        }

        public Icon getMarkerIcon(int markerType) {
            switch (markerType) {
                case YtemDefOJ.MARKER_TYPE_CROSS:
                    return crossIcon;
                case YtemDefOJ.MARKER_TYPE_DIAMOND:
                    return diamondIcon;
                case YtemDefOJ.MARKER_TYPE_DOT:
                    return dotIcon;
                case YtemDefOJ.MARKER_TYPE_PIXEL:
                    return pixelIcon;
                case YtemDefOJ.MARKER_TYPE_PLUS:
                    return plusIcon;
                case YtemDefOJ.MARKER_TYPE_SQUARE:
                    return squareIcon;
            }
            return null;
        }
    }

    class LineModelOJ extends DefaultComboBoxModel {

        public int getSize() {
            return 6;
        }

        public Object getElementAt(int index) {
            if (index < 6) {
                switch (index) {
                    case 0:
                        return new Integer(YtemDefOJ.LINE_TYPE_ONEPT);
                    case 1:
                        return new Integer(YtemDefOJ.LINE_TYPE_TWOPT);
                    case 2:
                        return new Integer(YtemDefOJ.LINE_TYPE_THREEPT);
                    case 3:
                        return new Integer(YtemDefOJ.LINE_TYPE_LIGHT_DOTTED);
                    case 4:
                        return new Integer(YtemDefOJ.LINE_TYPE_DOTTED);
                    case 5:
                        return new Integer(YtemDefOJ.LINE_TYPE_ZEROPT);
                }
                return null;
            } else {
                return null;
            }
        }

        public int getLineIndex(int lineType) {
            switch (lineType) {
                case YtemDefOJ.LINE_TYPE_ONEPT:
                    return 0;
                case YtemDefOJ.LINE_TYPE_TWOPT:
                    return 1;
                case YtemDefOJ.LINE_TYPE_THREEPT:
                    return 2;
                case YtemDefOJ.LINE_TYPE_LIGHT_DOTTED:
                    return 3;
                case YtemDefOJ.LINE_TYPE_DOTTED:
                    return 4;
                case YtemDefOJ.LINE_TYPE_ZEROPT:
                    return 5;
            }
            return 0;
        }
    }

    class LineRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(0, 8, 3, 3);
        private final Icon zeroptIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/0pt.gif"));
        private final Icon oneptIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/1pt.gif"));
        private final Icon twoptIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/2pt.gif"));
        private final Icon threeptIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/3pt.gif"));
        private final Icon lightDottedIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/LightDotted.gif"));
        private final Icon dottedIcon = new ImageIcon(getClass().getResource("/oj/gui/icons/Dotted.gif"));

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) {
            if (value == null) {
                return new JLabel();
            }
            setOpaque(true);
            setBorder(cellBorder);
            setBackground(Color.WHITE);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            if (value instanceof String) {
                setText((String) value);
                setIcon(null);
            } else {
                Integer marker = (Integer) value;
                setText(getLineName(marker.intValue()));
                setIcon(getLineIcon(marker.intValue()));
            }
            return this;
        }

        public String getLineName(int lineType) {
            switch (lineType) {
                case YtemDefOJ.LINE_TYPE_ONEPT:
                    return "1 pt";
                case YtemDefOJ.LINE_TYPE_TWOPT:
                    return "2 pt";
                case YtemDefOJ.LINE_TYPE_THREEPT:
                    return "3 pt";
                case YtemDefOJ.LINE_TYPE_LIGHT_DOTTED:
                    return "Light dotted";
                case YtemDefOJ.LINE_TYPE_DOTTED:
                    return "Dotted";
                case YtemDefOJ.LINE_TYPE_ZEROPT:
                    return "Invisible";
            }
            return "";
        }

        public Icon getLineIcon(int lineType) {
            switch (lineType) {
                case YtemDefOJ.LINE_TYPE_ONEPT:
                    return oneptIcon;
                case YtemDefOJ.LINE_TYPE_TWOPT:
                    return twoptIcon;
                case YtemDefOJ.LINE_TYPE_THREEPT:
                    return threeptIcon;
                case YtemDefOJ.LINE_TYPE_LIGHT_DOTTED:
                    return lightDottedIcon;
                case YtemDefOJ.LINE_TYPE_DOTTED:
                    return dottedIcon;
                case YtemDefOJ.LINE_TYPE_ZEROPT:
                    return zeroptIcon;
            }
            return null;
        }
    }

    class ColorModelOJ extends DefaultComboBoxModel {

        public int getSize() {
            return 14;
        }

        public Object getElementAt(int index) {
            if (index < 14) {
                switch (index) {
                    case 0:
                        return Color.BLACK;
                    case 1:
                        return Color.BLUE;
                    case 2:
                        return Color.CYAN;
                    case 3:
                        return Color.DARK_GRAY;
                    case 4:
                        return Color.GRAY;
                    case 5:
                        return Color.GREEN;
                    case 6:
                        return Color.LIGHT_GRAY;
                    case 7:
                        return Color.MAGENTA;
                    case 8:
                        return Color.ORANGE;
                    case 9:
                        return Color.PINK;
                    case 10:
                        return Color.RED;
                    case 11:
                        return Color.WHITE;
                    case 12:
                        return Color.YELLOW;
                    case 13:
                        return new String("Custom...");
                }
                return null;
            } else {
                return null;
            }
        }

        public int getColorIndex(Color color) {
            if (color.getRGB() == Color.BLACK.getRGB()) {
                return 0;
            }
            if (color.getRGB() == Color.BLUE.getRGB()) {
                return 1;
            }
            if (color.getRGB() == Color.CYAN.getRGB()) {
                return 2;
            }
            if (color.getRGB() == Color.DARK_GRAY.getRGB()) {
                return 3;
            }
            if (color.getRGB() == Color.GRAY.getRGB()) {
                return 4;
            }
            if (color.getRGB() == Color.GREEN.getRGB()) {
                return 5;
            }
            if (color.getRGB() == Color.LIGHT_GRAY.getRGB()) {
                return 6;
            }
            if (color.getRGB() == Color.MAGENTA.getRGB()) {
                return 7;
            }
            if (color.getRGB() == Color.ORANGE.getRGB()) {
                return 8;
            }
            if (color.getRGB() == Color.PINK.getRGB()) {
                return 9;
            }
            if (color.getRGB() == Color.RED.getRGB()) {
                return 10;
            }
            if (color.getRGB() == Color.WHITE.getRGB()) {
                return 11;
            }
            if (color.getRGB() == Color.YELLOW.getRGB()) {
                return 12;
            }
            return 13;
        }
    }

    class ColorRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(3, 8, 3, 3);
        private final Icon blackIcon = getColorIcon(Color.BLACK);
        private final Icon blueIcon = getColorIcon(Color.BLUE);
        private final Icon cyanIcon = getColorIcon(Color.CYAN);
        private final Icon darkGrayIcon = getColorIcon(Color.DARK_GRAY);
        private final Icon grayIcon = getColorIcon(Color.GRAY);
        private final Icon greenIcon = getColorIcon(Color.GREEN);
        private final Icon lightGrayIcon = getColorIcon(Color.LIGHT_GRAY);
        private final Icon magentaIcon = getColorIcon(Color.MAGENTA);
        private final Icon orangeIcon = getColorIcon(Color.ORANGE);
        private final Icon pinkIcon = getColorIcon(Color.PINK);
        private final Icon redIcon = getColorIcon(Color.RED);
        private final Icon whiteIcon = getColorIcon(Color.WHITE);
        private final Icon yellowIcon = getColorIcon(Color.YELLOW);

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) {
            if (value == null) {
                return new JLabel();
            }
            setOpaque(true);
            setBorder(cellBorder);
            setBackground(Color.WHITE);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            if (value instanceof String) {
                setText((String) value);
                setIcon(null);
            } else {
                Color color = (Color) value;
                setText(getColorName(color));
                switch (index) {
                    case 0:
                        setIcon(blackIcon);
                        break;
                    case 1:
                        setIcon(blueIcon);
                        break;
                    case 2:
                        setIcon(cyanIcon);
                        break;
                    case 3:
                        setIcon(darkGrayIcon);
                        break;
                    case 4:
                        setIcon(grayIcon);
                        break;
                    case 5:
                        setIcon(greenIcon);
                        break;
                    case 6:
                        setIcon(lightGrayIcon);
                        break;
                    case 7:
                        setIcon(magentaIcon);
                        break;
                    case 8:
                        setIcon(orangeIcon);
                        break;
                    case 9:
                        setIcon(pinkIcon);
                        break;
                    case 10:
                        setIcon(redIcon);
                        break;
                    case 11:
                        setIcon(whiteIcon);
                        break;
                    case 12:
                        setIcon(yellowIcon);
                        break;
                    default:
                        setIcon(getColorIcon(color));
                }
            }
            return this;
        }

        public String getColorName(Color color) {
            if (color.getRGB() == Color.BLACK.getRGB()) {
                return "Black";
            }
            if (color.getRGB() == Color.BLUE.getRGB()) {
                return "Blue";
            }
            if (color.getRGB() == Color.CYAN.getRGB()) {
                return "Cyan";
            }
            if (color.getRGB() == Color.DARK_GRAY.getRGB()) {
                return "Dark Gray";
            }
            if (color.getRGB() == Color.GRAY.getRGB()) {
                return "Gray";
            }
            if (color.getRGB() == Color.GREEN.getRGB()) {
                return "Green";
            }
            if (color.getRGB() == Color.LIGHT_GRAY.getRGB()) {
                return "Light Gray";
            }
            if (color.getRGB() == Color.MAGENTA.getRGB()) {
                return "Magenta";
            }
            if (color.getRGB() == Color.ORANGE.getRGB()) {
                return "Orange";
            }
            if (color.getRGB() == Color.PINK.getRGB()) {
                return "Pink";
            }
            if (color.getRGB() == Color.RED.getRGB()) {
                return "Red";
            }
            if (color.getRGB() == Color.WHITE.getRGB()) {
                return "White";
            }
            if (color.getRGB() == Color.YELLOW.getRGB()) {
                return "Yellow";
            }
            return "[" + Integer.toString(color.getRed()) + "," + Integer.toString(color.getGreen()) + "," + Integer.toString(color.getBlue()) + "]";
        }

        public Icon getColorIcon(Color color) {
            BufferedImage bimg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
            if (color != null) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        bimg.setRGB(i, j, color.getRGB());
                    }
                }
                for (int i = 0; i < 10; i++) {
                    bimg.setRGB(i, 0, Color.BLACK.getRGB());
                }
                for (int i = 0; i < 10; i++) {
                    bimg.setRGB(i, 9, Color.BLACK.getRGB());
                }
                for (int i = 0; i < 10; i++) {
                    bimg.setRGB(0, i, Color.BLACK.getRGB());
                }
                for (int i = 0; i < 10; i++) {
                    bimg.setRGB(9, i, Color.BLACK.getRGB());
                }
            }
            ImageIcon icon = new ImageIcon(bimg);
            return icon;
        }
    }

    public Dimension getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(Dimension panelSize) {
        this.panelSize = panelSize;
    }
}
