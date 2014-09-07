/**
 * Qualifiers Panel within Project window fully documented 11.2.2010
 */
package oj.gui.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import oj.util.UtilsOJ;
import oj.graphics.RoundPanelOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.QualifierChangedEventOJ;
import oj.processor.events.QualifierChangedListenerOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.QualifierOJ;
import oj.project.results.QualifiersOJ;

/**
 * Handles the Qualifier dialog, and contains additional classes: -
 * ColumnActionListener, - OperationActionListener - QualifiersHeaderRenderer -
 * QualifiersTableModel - QualifiersTableRenderer
 */
public class QualifiersSettingsOJ extends javax.swing.JPanel implements TableColumnModelListener, IControlPanelOJ, CellChangedListenerOJ, ColumnChangedListenerOJ, QualifierChangedListenerOJ {

    private QualifiersHeaderRenderer qualifiersHeaderRenderer = new QualifiersHeaderRenderer();
    private QualifiersTableRenderer qualifiersTableRenderer = new QualifiersTableRenderer();
    private ConditionActionListener operationActionListener = new ConditionActionListener();
    private ColumnActionListener columnActionListener = new ColumnActionListener();
    private JTextField valueEditor = new JTextField();
    private JComboBox columnSelector = new JComboBox();
    private JComboBox conditionSelector = new JComboBox();
    private Dimension panelSize = new Dimension(585, 385);

    /**
     * Creates new form QualifiersSettings2OJ
     */
    public QualifiersSettingsOJ() {
        initComponents();
        initExtComponents();

        OJ.getEventProcessor().addCellChangedListener(this);
        OJ.getEventProcessor().addColumnChangedListener(this);
        OJ.getEventProcessor().addQualifierChangedListener(this);
    }

    /**
     * puts all available column names into a combo box
     */
    private void fillColumnSelector() {
        tblQualifiers.clearSelection();//3.2.2014
        columnSelector.setMaximumRowCount(100);//5.7.2009
        columnSelector.removeAllItems();
        if (OJ.isValidData()) {
            for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
                ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByIndex(i);
                if (!column.isUnlinkedColumn()) {//1.2.2014
                    columnSelector.addItem(column.getName());
                }
            }
        }
        if (OJ.getData().getResults().getQualifiers().getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {
            btnNew.setEnabled(columnSelector.getItemCount() > 0);
        }
    }

    /**
     * puts all available conditions (such as >, ==, etc) into names into a
     * combo box
     */
    private void fillConditionSelector() {
        conditionSelector.setMaximumRowCount(100);//5.7.2009
        conditionSelector.removeAllItems();
        conditionSelector.addItem("<");
        conditionSelector.addItem("==");
        conditionSelector.addItem(">");
        conditionSelector.addItem("<=");
        conditionSelector.addItem("!=");
        conditionSelector.addItem(">=");
        conditionSelector.addItem("within");
        conditionSelector.addItem("notwithin");
        conditionSelector.addItem("empty");
        conditionSelector.addItem("exists");
    }

    /**
     * Listener called when one condition is changed
     */
    private class ConditionActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            int index = tblQualifiers.getSelectedRow();
            QualifierOJ qualifier = OJ.getData().getResults().getQualifiers().getQualifierByIndex(index);
            if (qualifier != null) {
                ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByName(qualifier.getColumnName());
                qualifier.setOperation(conditionSelector.getSelectedIndex() + 1);
                switch (qualifier.getOperation()) {
                    case QualifierOJ.OPERATION_EMPTY:
                        if (column.getColumnDef().isTextMode()) {
                            qualifier.setFirstStringValue("");
                            qualifier.setSecondStringValue("");

                        } else {
                            qualifier.setFirstDoubleValue(0.0);
                            qualifier.setSecondDoubleValue(1.0);
                        }
                        break;
                    case QualifierOJ.OPERATION_WITHIN:
                    case QualifierOJ.OPERATION_NOT_WITHIN:
                        break;
                    default:
                        if (column.getColumnDef().isTextMode()) {
                            qualifier.setSecondStringValue("");

                        } else {
                            qualifier.setSecondDoubleValue(1.0);
                        }
                }
                ((QualifiersTableModel) tblQualifiers.getModel()).fireTableRowUpdated(index);
            }

            tblQualifiers.getColumnModel().getColumn(2).getCellEditor().stopCellEditing();
            OJ.getDataProcessor().qualifyCells();

            OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011

            updateView();
        }
    }

    /**
     * Listener called when column is changed, or when a new conditions is added
     * to the bottom
     */
    private class ColumnActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            int index = tblQualifiers.getSelectedRow();
            QualifierOJ qualifier = OJ.getData().getResults().getQualifiers().getQualifierByIndex(index);
            ColumnOJ newColumn = OJ.getData().getResults().getColumns().getColumnByName((String) columnSelector.getSelectedItem());
            if ((qualifier != null) && (newColumn != null)) {
                ColumnOJ oldColumn = OJ.getData().getResults().getColumns().getColumnByName(qualifier.getColumnName());
                qualifier.setColumnName(newColumn.getName());
                if ((oldColumn.getColumnDef().isTextMode()) && (!newColumn.getColumnDef().isTextMode())) {
                    try {
                        Double.parseDouble(qualifier.getFirstStringValue());
                    } catch (Exception e) {
                        qualifier.setFirstDoubleValue(0.0);
                    }
                    try {
                        Double.parseDouble(qualifier.getSecondStringValue());
                    } catch (Exception e) {
                        qualifier.setSecondDoubleValue(1.0);
                    }
                }
            } else if (index == OJ.getData().getResults().getQualifiers().getQualifiersCount()) {
                qualifier = new QualifierOJ(newColumn.getName());
                qualifier.setOperation(QualifierOJ.OPERATION_EQUAL);
                OJ.getData().getResults().getQualifiers().addQualifier(qualifier);
                if (!newColumn.getColumnDef().isTextMode()) {
                    qualifier.setFirstDoubleValue(0.0);
                    qualifier.setSecondDoubleValue(1.0);
                }
            }
            tblQualifiers.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
            OJ.getDataProcessor().qualifyCells();
            OJ.getEventProcessor().fireCellChangedEvent();

            updateView();
        }
    }

    private void initExtComponents() {
        updateIfSelectionStatus();

        tblQualifiers.getTableHeader().setOpaque(false);
        tblQualifiers.getTableHeader().setDefaultRenderer(qualifiersHeaderRenderer);

        jScrollPane1.setBorder(new EmptyBorder(0, 0, 0, 0));
        jScrollPane1.getViewport().setBorder(null);
        jScrollPane1.getViewport().setOpaque(false);

        fillColumnSelector();
        columnSelector.addActionListener(columnActionListener);

        fillConditionSelector();
        conditionSelector.addActionListener(operationActionListener);

        valueEditor.addFocusListener(new FocusListener() {//qualify objects when focus is lost

            int index;
            QualifierOJ qualifier;

            public void focusGained(FocusEvent event) {
                index = tblQualifiers.getSelectedRow();
                qualifier = OJ.getData().getResults().getQualifiers().getQualifierByIndex(index);
            }

            public void focusLost(FocusEvent event) {//leaving a value field after editing
                if (qualifier != null) {
                    ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByName(qualifier.getColumnName());
                    if (column != null) {
                        switch (qualifier.getOperation()) {
                            case QualifierOJ.OPERATION_EMPTY:
                                break;
                            case QualifierOJ.OPERATION_WITHIN:
                            case QualifierOJ.OPERATION_NOT_WITHIN:
                                String[] values = {"", ""}; //valueEditor.getText().split("\0x2E{1}");

                                int pos = valueEditor.getText().indexOf("..");
                                if (pos > 0) {
                                    values[0] = valueEditor.getText().substring(0, pos);
                                    if (pos < valueEditor.getText().length()) {
                                        values[1] = valueEditor.getText().substring(pos + 2);
                                    }
                                }
                                if (values.length > 0) {
                                    if (column.getColumnDef().isTextMode()) {
                                        qualifier.setFirstStringValue(values[0]);
                                    } else {
                                        try {
                                            double dvalue = Double.parseDouble(values[0].trim());
                                            qualifier.setFirstDoubleValue(dvalue);
                                        } catch (Exception e) {
                                        }
                                    }
                                    if (values.length > 1) {
                                        if (column.getColumnDef().isTextMode()) {
                                            qualifier.setSecondStringValue(values[1]);
                                        } else {
                                            try {
                                                double dvalue = Double.parseDouble(values[1].trim());
                                                qualifier.setSecondDoubleValue(dvalue);
                                            } catch (Exception e) {
                                            }
                                        }
                                    } else {
                                        if (column.getColumnDef().isTextMode()) {
                                            qualifier.setSecondStringValue("");
                                        } else {
                                            qualifier.setSecondDoubleValue(0.0);
                                        }
                                    }
                                } else {
                                    if (column.getColumnDef().isTextMode()) {
                                        qualifier.setFirstStringValue("");
                                        qualifier.setSecondStringValue("");
                                    } else {
                                        qualifier.setFirstDoubleValue(0.0);
                                        qualifier.setSecondDoubleValue(1.0);
                                    }
                                }
                                break;
                            default:
                                String value = valueEditor.getText();
                                if (column.getColumnDef().isTextMode()) {
                                    qualifier.setFirstStringValue(value);
                                } else {
                                    try {
                                        double dvalue = Double.parseDouble(value.trim());
                                        qualifier.setFirstDoubleValue(dvalue);
                                    } catch (Exception e) {
                                    }
                                }
                        }
                        ((QualifiersTableModel) tblQualifiers.getModel()).fireTableRowUpdated(index);
                    }
                }
                OJ.getDataProcessor().qualifyCells();//18.8.2011
                OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011
                updateView();
            }
        });
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        qalGroup = new javax.swing.ButtonGroup();
        pnlNorth = new javax.swing.JPanel();
        pnlAllNoneArb = new javax.swing.JPanel();
        radAll = new javax.swing.JRadioButton();
        radNone = new javax.swing.JRadioButton();
        radArbitrary = new javax.swing.JRadioButton();
        pnlHits = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblQualified = new javax.swing.JLabel();
        pnlInvert = new javax.swing.JPanel();
        btnInvert = new javax.swing.JButton();
        pnlSouthA = new javax.swing.JPanel();
        pnlSouthB = new RoundPanelOJ();
        pnlIf = new javax.swing.JPanel();
        radIf = new javax.swing.JRadioButton();
        lblError = new javax.swing.JLabel();
        pnlTwoButtons = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        pnlConditions = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblQualifiers = new javax.swing.JTable();
        tblQualifiers.getColumnModel().addColumnModelListener(this);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        pnlNorth.setMaximumSize(new java.awt.Dimension(32767, 100));
        pnlNorth.setMinimumSize(new java.awt.Dimension(252, 100));
        pnlNorth.setPreferredSize(new java.awt.Dimension(100, 90));
        pnlNorth.setLayout(new java.awt.GridLayout(1, 2));

        pnlAllNoneArb.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 38, 10, 10));
        pnlAllNoneArb.setLayout(new java.awt.GridLayout(3, 1, 0, 3));

        qalGroup.add(radAll);
        radAll.setText("All");
        radAll.setContentAreaFilled(false);
        radAll.setFocusPainted(false);
        radAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radAllActionPerformed(evt);
            }
        });
        pnlAllNoneArb.add(radAll);

        qalGroup.add(radNone);
        radNone.setText("None");
        radNone.setFocusPainted(false);
        radNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNoneActionPerformed(evt);
            }
        });
        pnlAllNoneArb.add(radNone);

        qalGroup.add(radArbitrary);
        radArbitrary.setText("Arbitrary");
        radArbitrary.setFocusPainted(false);
        radArbitrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radArbitraryActionPerformed(evt);
            }
        });
        pnlAllNoneArb.add(radArbitrary);

        pnlNorth.add(pnlAllNoneArb);

        pnlHits.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlHits.setLayout(new java.awt.GridLayout(3, 0));

        lblTotal.setText("Total:         1607");
        lblTotal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnlHits.add(lblTotal);

        lblQualified.setText("Qualified:   206 = 12,7%");
        lblQualified.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnlHits.add(lblQualified);

        pnlInvert.setLayout(new javax.swing.BoxLayout(pnlInvert, javax.swing.BoxLayout.LINE_AXIS));

        btnInvert.setText("Invert");
        btnInvert.setFocusPainted(false);
        btnInvert.setMaximumSize(new java.awt.Dimension(106, 29));
        btnInvert.setMinimumSize(new java.awt.Dimension(106, 29));
        btnInvert.setPreferredSize(new java.awt.Dimension(106, 29));
        btnInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvertActionPerformed(evt);
            }
        });
        pnlInvert.add(btnInvert);

        pnlHits.add(pnlInvert);

        pnlNorth.add(pnlHits);

        add(pnlNorth, java.awt.BorderLayout.NORTH);

        pnlSouthA.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 16, 20, 16));
        pnlSouthA.setLayout(new java.awt.BorderLayout());

        pnlSouthB.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        pnlSouthB.setLayout(new java.awt.BorderLayout());

        pnlIf.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 18, 4, 1));
        pnlIf.setLayout(new java.awt.GridLayout(1, 0));

        qalGroup.add(radIf);
        radIf.setText("If:");
        radIf.setFocusPainted(false);
        radIf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radIfActionPerformed(evt);
            }
        });
        pnlIf.add(radIf);

        lblError.setForeground(java.awt.Color.gray);
        lblError.setText("error field");
        pnlIf.add(lblError);

        pnlSouthB.add(pnlIf, java.awt.BorderLayout.NORTH);

        pnlTwoButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 10, 1));
        pnlTwoButtons.setLayout(new javax.swing.BoxLayout(pnlTwoButtons, javax.swing.BoxLayout.LINE_AXIS));

        btnNew.setText("New");
        btnNew.setEnabled(false);
        btnNew.setFocusPainted(false);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        pnlTwoButtons.add(btnNew);

        btnRemove.setText("Remove");
        btnRemove.setEnabled(false);
        btnRemove.setFocusPainted(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        pnlTwoButtons.add(btnRemove);

        pnlSouthB.add(pnlTwoButtons, java.awt.BorderLayout.PAGE_END);

        pnlConditions.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 10));
        pnlConditions.setLayout(new java.awt.BorderLayout());

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

        tblQualifiers.setModel(new QualifiersTableModel());
        tblQualifiers.setFocusable(false);
        tblQualifiers.setGridColor(new java.awt.Color(204, 204, 204));
        tblQualifiers.setIntercellSpacing(new java.awt.Dimension(2, 2));
        tblQualifiers.setOpaque(false);
        tblQualifiers.setRequestFocusEnabled(false);
        tblQualifiers.setRowHeight(26);
        tblQualifiers.setShowHorizontalLines(false);
        tblQualifiers.setShowVerticalLines(false);
        tblQualifiers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblQualifiersMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblQualifiers);

        pnlConditions.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlSouthB.add(pnlConditions, java.awt.BorderLayout.CENTER);

        pnlSouthA.add(pnlSouthB, java.awt.BorderLayout.CENTER);

        add(pnlSouthA, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        switch (OJ.getData().getResults().getQualifiers().getQualifyMethod()) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                radAll.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                radNone.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_IF:
                radIf.setSelected(true);
                break;
            default:
                radArbitrary.setSelected(true);
        }
        updateView();
        tblQualifiers.getSelectionModel().clearSelection();
        if (OJ.getData().getResults().getQualifiers().getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {
            tblQualifiers.setForeground(Color.BLACK);
            tblQualifiers.setEnabled(true);
        } else {
            tblQualifiers.setForeground(Color.GRAY);
            tblQualifiers.setEnabled(false);
        }
    }//GEN-LAST:event_formComponentShown

    /**
     * called when radio button All is clicked
     */
    private void radAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radAllActionPerformed
        OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ALL, true);
        OJ.getData().getCells().qualifyAllCells();
        disableQualifiersTable();
        OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011
        updateView();
    }//GEN-LAST:event_radAllActionPerformed

    /**
     * called when radio button "None" is clicked
     */
    private void radNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNoneActionPerformed
        OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_NONE, true);
        OJ.getData().getCells().disqualifyAllCells();
        disableQualifiersTable();
        OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011
        updateView();
    }//GEN-LAST:event_radNoneActionPerformed

    /**
     * called when radio button "Arbitrary" is clicked
     */
    private void radArbitraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radArbitraryActionPerformed
        OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
        disableQualifiersTable();
        OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011
        updateView();
    }//GEN-LAST:event_radArbitraryActionPerformed
    /**
     * called when radio button "If" is clicked
     */
    private void radIfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radIfActionPerformed
        OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_IF, true);
        boolean[] qualified_cells = OJ.getData().getResults().qualifyCells();
        for (int i = 0; i < qualified_cells.length; i++) {
            OJ.getData().getCells().getCellByIndex(i).setQualified(qualified_cells[i]);
        }
        btnNew.setEnabled(columnSelector.getItemCount() > 0);
        tblQualifiers.setForeground(Color.BLACK);
        tblQualifiers.setEnabled(true);
        OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011
        updateView();
    }//GEN-LAST:event_radIfActionPerformed

    /**
     * called when radio button "Invert" is clicked
     */
    private void btnInvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvertActionPerformed
        switch (OJ.getData().getResults().getQualifiers().getQualifyMethod()) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                radNoneActionPerformed(null);
                radNone.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                radAllActionPerformed(null);
                radAll.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_IF:
                OJ.getData().getCells().invertCellsQualification();//17.8.2011
                radArbitraryActionPerformed(null);
                radArbitrary.setSelected(true);
                break;
            default:
                OJ.getData().getCells().invertCellsQualification();//17.8.2011
                radArbitraryActionPerformed(null);
                radArbitrary.setSelected(true);
        }

        tblQualifiers.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
        OJ.getDataProcessor().qualifyCells();//18.8.2011
        OJ.getEventProcessor().fireCellChangedEvent();//18.8.2011

        updateView();


    }//GEN-LAST:event_btnInvertActionPerformed

    /**
     * for updating "Remove" button after clicking any condition row
     */
    private void tblQualifiersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblQualifiersMousePressed
        int index = tblQualifiers.getSelectedRow();
        if (index >= OJ.getData().getResults().getQualifiers().getQualifiersCount()) {
            btnRemove.setEnabled(false);
        } else {
            btnRemove.setEnabled(index >= 0);
        }
    }//GEN-LAST:event_tblQualifiersMousePressed

    /**
     * called when the "Remove" button is clicked
     */
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int index = tblQualifiers.getSelectedRow();
        if (index >= 0) {
            OJ.getData().getResults().getQualifiers().removeQualifierByIndex(index);
            tblQualifiers.clearSelection();
            ((QualifiersTableModel) tblQualifiers.getModel()).fireTableStructureChanged();
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    /**
     * called when the "New" button is clicked
     */
    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByIndex(0);
        QualifierOJ qualifier = new QualifierOJ(column.getName());
        qualifier.setOperation(QualifierOJ.OPERATION_EQUAL);
        if (!column.getColumnDef().isTextMode()) {
            qualifier.setFirstDoubleValue(0.0);
            qualifier.setSecondDoubleValue(1.0);
        }
        OJ.getData().getResults().getQualifiers().addQualifier(qualifier);
    }//GEN-LAST:event_btnNewActionPerformed

    private void jScrollPane1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jScrollPane1AncestorAdded
        jScrollPane1.getColumnHeader().setOpaque(false);
}//GEN-LAST:event_jScrollPane1AncestorAdded

    /**
     * For leaving the editing mode after pressing one of the radiobuttons
     * "All", "If" etc
     */
    private void disableQualifiersTable() {
        ((DefaultCellEditor) tblQualifiers.getColumnModel().getColumn(1).getCellEditor()).stopCellEditing();
        ((DefaultCellEditor) tblQualifiers.getColumnModel().getColumn(2).getCellEditor()).stopCellEditing();
        ((DefaultCellEditor) tblQualifiers.getColumnModel().getColumn(3).getCellEditor()).stopCellEditing();
        tblQualifiers.getSelectionModel().clearSelection();
        tblQualifiers.setForeground(Color.GRAY);
        tblQualifiers.setEnabled(false);
        btnRemove.setEnabled(false);
        btnNew.setEnabled(false);
    }

    /**
     * updates radio buttons, counts qualified flags of all objects and updates
     * the hit results
     */
    private void updateView() {
        switch (OJ.getData().getResults().getQualifiers().getQualifyMethod()) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                radAll.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                radNone.setSelected(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_IF:
                radIf.setSelected(true);
                break;
            default:
                radArbitrary.setSelected(true);
        }
        int total = OJ.getData().getCells().getCellsCount();
        int qualified = OJ.getData().getCells().getQualifiedCellsCount();
        lblTotal.setText("Total:        " + total);
        if (total > 0) {
            lblQualified.setText("Qualified : " + qualified + " = " + UtilsOJ.doubleToString(100.0 * qualified / total, 1) + "%");//13.1.2010
        } else {
            lblQualified.setText("Qualified : " + qualified + " = " + UtilsOJ.doubleToString(0.0, 1) + "%");
        }
        ((QualifiersTableModel) tblQualifiers.getModel()).fireTableUpdated();//3.12.2009

    }

    /**
     * removes listeners
     */
    public void close() {
        OJ.getEventProcessor().removeCellChangedListener(this);
        OJ.getEventProcessor().removeColumnChangedListener(this);
        OJ.getEventProcessor().removeQualifierChangedListener(this);
    }

    /**
     * updates hits after cell change
     */
    public synchronized void cellChanged(CellChangedEventOJ evt) {//9.9.2009
        updateView();
    }

    /**
     * Set If button if possible, or set error text if there is no condition
     */
    private void updateIfSelectionStatus() {
        if (OJ.getData().getResults().getColumns().getAllColumnsCount() > 0) {
            radIf.setEnabled(true);
            lblError.setText("");
        } else {
            radIf.setEnabled(false);
            lblError.setText("There are no columns defined !!!");
            if (OJ.getData().getResults().getQualifiers().getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {
                OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
                disableQualifiersTable();
                updateView();
            }
        }
    }

    /**
     * if a result column has changed, this methods listens to it
     */
    public synchronized void columnChanged(ColumnChangedEventOJ evt) {//9.9.2009
        fillColumnSelector();
        updateIfSelectionStatus();
    }

    /**
     * called by QualifiersOJ
     */
    public synchronized void qualifierChanged(QualifierChangedEventOJ evt) {//9.9.2009
        if (evt.getOperation() == QualifierChangedEventOJ.QUALIFIER_ITEM_ADDED) {
            ((QualifiersTableModel) tblQualifiers.getModel()).fireTableStructureChanged();
        } else {
            ((QualifiersTableModel) tblQualifiers.getModel()).fireTableUpdated();
        }
        btnRemove.setEnabled(false);
        tblQualifiers.clearSelection();
        OJ.getDataProcessor().qualifyCells();
        updateView();
    }

    //Three classes follow to manage the table:
    //Header is "Column", "Condition", "Values" and "Hits";
    //table body consists of editable fields, partly with popup menus
    public class QualifiersHeaderRenderer extends JLabel implements TableCellRenderer {

        private Font fontArial = Font.decode("Arial-11");

        /**
         * return table header cell in form of correctly formated label (always
         * black)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // if (OJ.getData().getResults().getQualifiers().getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {removed 11.2.2010
            setForeground(Color.BLACK);
            //} else {
            //    setForeground(Color.GRAY);
            //}
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(5, 6, 7, 3));
            setText((String) value);
            setFont(fontArial);
            setOpaque(false);
            return this;
        }
    }

    public class QualifiersTableRenderer extends javax.swing.JLabel/*RoundLabelOJ*/ implements TableCellRenderer {

        private Font fontArial = Font.decode("Arial-11");
        private Font fontArialBold = Font.decode("Arial-BOLD-14");
        private Border emptyBorder2 = new EmptyBorder(2, 2, 2, 2);
        private Border emptyBorder4 = new EmptyBorder(2, 6, 2, 4);
        boolean backgroundEnabled = true;
        boolean borderEnabled = true;
        Color borderColor = Color.BLACK;

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

        /**
         * return table cell in form of correctly formated label (gray if
         * conditions overruled by "All" etc)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            backgroundEnabled = (true);
            borderEnabled = (true);
            switch (column) {
                case 0://for the "*" character to show what is selected
                    setHorizontalAlignment(SwingConstants.CENTER);
                    backgroundEnabled = (false);
                    borderEnabled = (false);
                    setBorder(emptyBorder2);
                    break;
                case 1://name of results column
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 2://Condition
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 3://values
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
                case 4://Hits
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBackground(Color.WHITE);
                    setBorder(emptyBorder4);
                    break;
            }
            if (OJ.getData().getResults().getQualifiers().getQualifyMethod() == QualifiersOJ.QUALIFY_METHOD_IF) {
                setForeground(Color.BLACK);
                borderColor = (Color.LIGHT_GRAY);
            } else {
                setForeground(Color.GRAY);
                borderColor = (Color.LIGHT_GRAY);
            }

            if (column == 0) {
                setFont(fontArialBold);
                if (tblQualifiers.getSelectedRow() == row) {
                    setText("*");
                } else {
                    setText("");
                }
            } else {
                setFont(fontArial);
                if (value != null) {
                    setText(value.toString());
                } else {
                    setText("");
                }
            }
            setFocusable(false);
            return this;
        }
    }

    /**
     * Management of the qualifiers table data
     */
    private class QualifiersTableModel extends AbstractTableModel {

        DecimalFormat dfcount = new DecimalFormat("####");
        DecimalFormat dfpercent = new DecimalFormat("##.#");

        public String getColumnName(int col) {

            switch (col) {
                case 0:
                    return "";
                case 1:
                    return "Column";
                case 2:
                    return "Condition";
                case 3:
                    return "Values";
                case 4:
                    return "Hits";
            }
            return "";
        }

        public int getRowCount() {
            return Math.max(4, OJ.getData().getResults().getQualifiers().getQualifiersCount());
        }

        public int getColumnCount() {
            return 5;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= OJ.getData().getResults().getQualifiers().getQualifiersCount()) {
                return "";
            }
            QualifierOJ qualifier = OJ.getData().getResults().getQualifiers().getQualifierByIndex(rowIndex);
            switch (columnIndex) {
                case 0:
                    return "";
                case 1:
                    return qualifier.getColumnName();
                case 2:
                    return QualifierOJ.getOperationText(qualifier.getOperation());
//                    return QualifierOJ.getOperationText(qualifier.getOperation());
                case 3:
                    ColumnOJ col = OJ.getData().getResults().getColumns().getColumnByName(qualifier.getColumnName());
                    if (col.getColumnDef().isTextMode()) {
                        switch (qualifier.getOperation()) {
                            case QualifierOJ.OPERATION_WITHIN:
                            case QualifierOJ.OPERATION_NOT_WITHIN:
                                return qualifier.getFirstStringValue() + " .. " + qualifier.getSecondStringValue();
                            case QualifierOJ.OPERATION_EMPTY:
                            case QualifierOJ.OPERATION_EXISTS:
                                return "";
                            default:
                                return qualifier.getFirstStringValue();
                        }
                    } else {
                        switch (qualifier.getOperation()) {
                            case QualifierOJ.OPERATION_WITHIN:
                            case QualifierOJ.OPERATION_NOT_WITHIN:
                                return qualifier.getFirstDoubleValue() + " .. " + qualifier.getSecondDoubleValue();
                            case QualifierOJ.OPERATION_EMPTY:
                            case QualifierOJ.OPERATION_EXISTS:
                                return "";
                            default:
                                return qualifier.getFirstDoubleValue();
                        }
                    }
                case 4:
                    return getQualifierHits(qualifier);
            }
            return null;
        }

        /**
         * e.g. a cell is not editable if it is below the last row of
         * qualifiers, or if condition does not require a numeric value
         */
        public boolean isCellEditable(int row, int col) {

            switch (col) {
                case 1:
                    return (row <= OJ.getData().getResults().getQualifiers().getQualifiersCount());
                case 2:
                    return (row < OJ.getData().getResults().getQualifiers().getQualifiersCount());
                case 3:
                    QualifierOJ qualifier = OJ.getData().getResults().getQualifiers().getQualifierByIndex(row);
                    return ((row < OJ.getData().getResults().getQualifiers().getQualifiersCount())
                            && ((qualifier.getOperation() != QualifierOJ.OPERATION_EMPTY) && (qualifier.getOperation() != QualifierOJ.OPERATION_EXISTS)));
                default:
                    return false;
            }
        }

        /**
         * called e.g. by updateView
         */
        public void fireTableUpdated() {
            fireTableRowsUpdated(0, getRowCount());
        }

        public void fireTableRowUpdated(int index) {
            fireTableRowsUpdated(index, index);
        }

        /**
         * accepts a qualifier object, that corresponds to one row in the
         * Qualifier settings
         *
         * @param qualifier
         * @return
         */
        private String getQualifierHits(QualifierOJ qualifier) {
            ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByName(qualifier.getColumnName());
            if (column != null) {
                int count = 0;
                if ((qualifier.getOperation() == QualifierOJ.OPERATION_NOT_WITHIN) || (qualifier.getOperation() == QualifierOJ.OPERATION_WITHIN)) {
                    if (column.getColumnDef().isTextMode()) {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getStringResult(i), qualifier.getFirstStringValue(), qualifier.getSecondStringValue(), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    } else {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getDoubleResult(i), qualifier.getFirstDoubleValue(), qualifier.getSecondDoubleValue(), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    }
                    if (qualifier.getOperation() == QualifierOJ.OPERATION_NOT_WITHIN) {
                        count += OJ.getData().getCells().getCellsCount() - column.getResultCount();
                    }
                } else if ((qualifier.getOperation() == QualifierOJ.OPERATION_EXISTS) || (qualifier.getOperation() == QualifierOJ.OPERATION_EMPTY)) {
                    if (column.getColumnDef().isTextMode()) {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getStringResult(i), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    } else {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getDoubleResult(i), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    }
                    count += OJ.getData().getCells().getCellsCount() - column.getResultCount();
                } else if (qualifier.getOperation() == QualifierOJ.OPERATION_NONE) {
                    count = 0;
                } else {
                    if (column.getColumnDef().isTextMode()) {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getStringResult(i), qualifier.getFirstStringValue(), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    } else {
                        for (int i = 0; i < column.getResultCount(); i++) {
                            if (QualifierOJ.qualify(column.getDoubleResult(i), qualifier.getFirstDoubleValue(), qualifier.getOperation())) {
                                count += 1;
                            }
                        }
                    }
                }
                if (OJ.getData().getCells().getCellsCount() > 0) {
                    return dfcount.format(count) + " [" + dfpercent.format((double) count / OJ.getData().getCells().getCellsCount() * 100) + " %]";
                } else {
                    return dfcount.format(0) + " [" + dfpercent.format(0.0) + "%]";
                }
            } else {
                return dfcount.format(0) + " [" + dfpercent.format(0.0) + "%]";
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInvert;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblQualified;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlAllNoneArb;
    private javax.swing.JPanel pnlConditions;
    private javax.swing.JPanel pnlHits;
    private javax.swing.JPanel pnlIf;
    private javax.swing.JPanel pnlInvert;
    private javax.swing.JPanel pnlNorth;
    private javax.swing.JPanel pnlSouthA;
    private javax.swing.JPanel pnlSouthB;
    private javax.swing.JPanel pnlTwoButtons;
    private javax.swing.ButtonGroup qalGroup;
    private javax.swing.JRadioButton radAll;
    private javax.swing.JRadioButton radArbitrary;
    private javax.swing.JRadioButton radIf;
    private javax.swing.JRadioButton radNone;
    private javax.swing.JTable tblQualifiers;
    // End of variables declaration//GEN-END:variables

    public void columnAdded(TableColumnModelEvent event) {
        int toIndex = event.getToIndex();
        if (event.getSource() == tblQualifiers.getColumnModel()) {
            TableColumn tableCol = (TableColumn) tblQualifiers.getColumnModel().getColumn(toIndex);
            tableCol.setCellRenderer(qualifiersTableRenderer);
            switch (toIndex) {
                case 0:
                    tableCol.setWidth(22);
                    tableCol.setMaxWidth(22);
                    tableCol.setPreferredWidth(22);
                    break;
                case 1:
                    tableCol.setWidth(100);
                    tableCol.setPreferredWidth(100);
                    tableCol.setCellEditor(new DefaultCellEditor(columnSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 2:
                    tableCol.setWidth(60);
                    tableCol.setPreferredWidth(60);
                    tableCol.setCellEditor(new DefaultCellEditor(conditionSelector));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
                case 3:
                    tableCol.setWidth(80);
                    tableCol.setPreferredWidth(80);
                    tableCol.setCellEditor(new DefaultCellEditor(valueEditor));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
            }
        }
    }

    public void columnRemoved(TableColumnModelEvent event) {
    }

    public void columnMoved(TableColumnModelEvent event) {
    }

    public void columnMarginChanged(ChangeEvent event) {
    }

    public void columnSelectionChanged(ListSelectionEvent event) {
    }

    public Dimension getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(Dimension panelSize) {
        this.panelSize = panelSize;
    }
}
