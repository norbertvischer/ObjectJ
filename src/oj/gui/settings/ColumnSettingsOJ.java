/**
 * Column definition Panel within Project window Completely documented 10.2.2010
 */
package oj.gui.settings;

import ij.IJ;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import oj.OJ;
import oj.graphics.PlotOJ;
import oj.gui.results.ProjectResultsOJ;
import oj.project.results.ColumnDefOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.OperandOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.project.results.ColumnsOJ;

/**
 * ColumnsSettingsOJ.java
 *
 * Panel inside Project Settings For creating ObjectJ result columns, connecting
 * algorithms, and set histogram parameters
 */
public class ColumnSettingsOJ extends javax.swing.JPanel implements TableColumnModelListener, IControlPanelOJ, ColumnChangedListenerOJ, YtemDefChangedListenerOJ {

    private static int suffix = 0;
    private JTextField nameEditor = new JTextField();
    private Dimension panelSize = new Dimension(585, 600);
    private ColumnsTableRenderer columnsTableRenderer = new ColumnsTableRenderer();
    private ColumnsHeaderTableRendererOJ columnsHeaderTableRenderer = new ColumnsHeaderTableRendererOJ();

    public ColumnSettingsOJ() {
        initComponents();
        initComponentsExt();

        updateView();
        OJ.getEventProcessor().addColumnChangedListener(this);
        OJ.getEventProcessor().addYtemDefChangedListener(this);
    }

    public void newColumn() {
        btnNewActionPerformed(null);
    }

    /**
     * Selects a column for editing
     */
    public void editColumn(String columnName) {
        for (int i = 0; i < tblColumns.getRowCount(); i++) {
            if (OJ.getData().getResults().getColumns().getColumnByIndex(i).getColumnDef().getName().equals(columnName)) {
                tblColumns.getSelectionModel().setSelectionInterval(i, i);
                updateView();
                break;
            }
        }
    }

    /**
     * only one column can be selected
     */
    private ColumnOJ getSelectedColumn() {
        if (tblColumns.getSelectedRowCount() != 1) {
            return null;
        }
        int index = tblColumns.getSelectedRow();
        if ((index >= 0) && (index < OJ.getData().getResults().getColumns().getAllColumnsCount())) {
            return OJ.getData().getResults().getColumns().getColumnByIndex(index);
        }
        return null;
    }

    /**
     * Fills the combo boxes with the available ytem names
     */
    private void fillObjectDefs() {
        Object firstItem = cbxFirstObjectType.getSelectedItem();
        Object secondItem = cbxSecondObjectType.getSelectedItem();
        Object thirdItem = cbxThirdObjectType.getSelectedItem();
        cbxFirstObjectType.removeAllItems();
        cbxSecondObjectType.removeAllItems();
        cbxThirdObjectType.removeAllItems();
        for (int i = 0; i < OJ.getData().getYtemDefs().getYtemDefsCount(); i++) {
            cbxFirstObjectType.addItem(OJ.getData().getYtemDefs().getYtemDefByIndex(i).getYtemDefName());
            cbxSecondObjectType.addItem(OJ.getData().getYtemDefs().getYtemDefByIndex(i).getYtemDefName());
            cbxThirdObjectType.addItem(OJ.getData().getYtemDefs().getYtemDefByIndex(i).getYtemDefName());
        }
        if (firstItem != null) {
            cbxFirstObjectType.setSelectedItem(firstItem);
        }
        if (secondItem != null) {
            cbxSecondObjectType.setSelectedItem(secondItem);
        }
        if (thirdItem != null) {
            cbxThirdObjectType.setSelectedItem(thirdItem);
        }
    }

    /**
     * Fills the combo boxes with the available algorithms
     */
    private void fillOperations() {
        cbxOperation.setMaximumRowCount(100);
        cbxOperation.addItem(".None");
        //cbxOperation.addItem("..Unlinked User Column");
        cbxOperation.addItem("-");
        cbxOperation.addItem("Distance");
        cbxOperation.addItem("Path");
        cbxOperation.addItem("Partial Path");
        cbxOperation.addItem("Relative Partial Path");
        //cbxOperation.addItem("Offroad");//
        cbxOperation.addItem("XPos");
        cbxOperation.addItem("YPos");
        cbxOperation.addItem("ZPos");
        cbxOperation.addItem("Orientation");
        cbxOperation.addItem("Angle");
        cbxOperation.addItem("Count");
        cbxOperation.addItem("Area");
        cbxOperation.addItem("Slice");
        cbxOperation.addItem("Image");
        cbxOperation.addItem("Index");
        cbxOperation.addItem("ID");
        cbxOperation.addItem("Exists");
        cbxOperation.addItem("Length");
        cbxOperation.addItem("File Name");

    }

    /**
     * For the color of the column, combo box is filled with available colors
     */
    private void fillColors() {
        cbxColor.setMaximumRowCount(100);//5.7.2009
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbxColor.getModel();
        model.removeAllElements();
        model.addElement(Color.BLACK);
        model.addElement(Color.BLUE);
        model.addElement(Color.CYAN);
        model.addElement(Color.DARK_GRAY);
        model.addElement(Color.GRAY);
        model.addElement(Color.GREEN);
        model.addElement(new Color(0, 153, 0));
        model.addElement(Color.MAGENTA);
        model.addElement(Color.ORANGE);
        model.addElement(Color.PINK);
        model.addElement(Color.RED);
        model.addElement(Color.WHITE);
        model.addElement(Color.YELLOW);
        model.addElement(new String("Custom..."));

    }

    /**
     * Depending on the algorithm, a certain amount of parameters can be defined
     * by the user, while the rest remains invisible
     */
    private void updateOperandsView(int operandsCount, boolean cloneEnabled, boolean pointEnabled) {
        //first operand
        boolean firstEnabled = operandsCount > 0;
        pnlFirstOperand.setVisible(firstEnabled);
        if (cloneEnabled) {
            lblFirstClone.setText("Clone#");
        } else {
            lblFirstClone.setText("");
        }
        txtFirstClone.setVisible(firstEnabled && cloneEnabled);
        if (pointEnabled) {
            lblFirstPoint.setText("Point#");
        } else {
            lblFirstPoint.setText("");
        }
        txtFirstPoint.setVisible(firstEnabled && pointEnabled);

        //second operand
        boolean secondEnabled = operandsCount > 1;
        pnlSecondOperand.setVisible(secondEnabled);
        if (cloneEnabled) {
            lblSecondClone.setText("Clone#");
        } else {
            lblSecondClone.setText("");
        }
        txtSecondClone.setVisible(secondEnabled && cloneEnabled);
        if (pointEnabled) {
            lblSecondPoint.setText("Point#");
        } else {
            lblSecondPoint.setText("");
        }
        txtSecondPoint.setVisible(secondEnabled && pointEnabled);

        //third operand
        boolean thirdEnabled = operandsCount > 2;
        pnlThirdOperand.setVisible(thirdEnabled);
        if (cloneEnabled) {
            lblThirdClone.setText("Clone#");
        } else {
            lblThirdClone.setText("");
        }
        txtThirdClone.setVisible(thirdEnabled && cloneEnabled);
        if (pointEnabled) {
            lblThirdPoint.setText("Point#");
        } else {
            lblThirdPoint.setText("");
        }
        txtThirdPoint.setVisible(thirdEnabled && pointEnabled);

        if (operandsCount <= 1) {
            ((TitledBorder) ((CompoundBorder) pnlFirstOperand.getBorder()).getInsideBorder()).setTitle("Operand");
        } else {
            ((TitledBorder) ((CompoundBorder) pnlFirstOperand.getBorder()).getInsideBorder()).setTitle("1st Operand");
        }
    }

    /**
     * updates Ytem names in comboboxes
     */
    private void updateYtemTypeText(ColumnOJ column, int index) {
        JComboBox cbxField = null;

        switch (index) {
            case 0:
                cbxField = cbxFirstObjectType;
                break;
            case 1:
                cbxField = cbxSecondObjectType;
                break;
            case 2:
                cbxField = cbxThirdObjectType;
                break;
        }

        OperandOJ operand = column.getColumnDef().getOperand(index);
        if ((cbxField != null) && ((cbxField.getSelectedItem() == null) || ((operand != null) && (!((String) cbxField.getSelectedItem()).equals(operand.getObjectName()))))) {
            cbxField.getModel().setSelectedItem(operand.getObjectName());
        }
    }

    /**
     * updates clone numbers
     */
    private void updateCloneText(ColumnOJ column, int index) {
        JTextField txtField = null;
        switch (index) {
            case 0:
                txtField = txtFirstClone;
                break;
            case 1:
                txtField = txtSecondClone;
                break;
            case 2:
                txtField = txtThirdClone;
                break;
        }

        OperandOJ operand = column.getColumnDef().getOperand(index);
        if ((txtField != null) && ((txtField.getText() == null) || ((operand != null) && (!txtField.getText().equals(Integer.toString(operand.getYtemClone() + 1)))))) {
            txtField.setText(Integer.toString(operand.getYtemClone() + 1));
        }
    }

    /**
     * updates point numbers
     */
    private void updatePointText(ColumnOJ column, int index) {
        JTextField txtField = null;
        switch (index) {
            case 0:
                txtField = txtFirstPoint;
                break;
            case 1:
                txtField = txtSecondPoint;
                break;
            case 2:
                txtField = txtThirdPoint;
                break;
        }

        OperandOJ operand = column.getColumnDef().getOperand(index);
        if ((txtField != null) && ((txtField.getText() == null) || ((operand != null) && (!txtField.getText().equals(Integer.toString(operand.getRelPosition() + 1)))))) {
            txtField.setText(Integer.toString(operand.getRelPosition() + 1));
        }
    }

    /**
     * uses column definition to update panel showing histogram parameters
     * (xmin, xmax, binwidth etc)
     */
    private void updateHistoSettings(ColumnDefOJ colDef) {
        double val = colDef.getHistoXMin();
        String txt = Double.isNaN(val) ? "auto" : "" + colDef.getHistoXMin();
        tfXMin.setText(txt);

        val = colDef.getHistoXMax();
        txt = Double.isNaN(val) ? "auto" : "" + colDef.getHistoXMax();
        tfXMax.setText(txt);

        val = colDef.getHistoBinWidth();
        txt = (val <= 0) || Double.isNaN(val) ? "auto" : "" + colDef.getHistoBinWidth();
        tfBinWidth.setText(txt);

        val = colDef.getHistoYMax();
        txt = val < 1 ? "auto" : "" + colDef.getHistoYMax();
        tfYMax.setText(txt);

        tfXMin.setVisible(true);
        tfXMax.setVisible(true);
        tfBinWidth.setVisible(true);
        tfYMax.setVisible(true);
    }

    /**
     * updates the two tabbed panels for operands and presentation
     */
    private void updateView() {
        ColumnOJ column = getSelectedColumn();
        if (column != null) {
            String name = column.getName();
            if (name.startsWith("Column")) {
                name = name + "";
            }
            ColumnDefOJ colDef = column.getColumnDef();
            updateHistoSettings(colDef);
            int alg = column.getColumnDef().getAlgorithm();
            boolean nonAutomatic = alg > ColumnDefOJ.ALGORITHM_LAST_AUTOMATIC && alg != ColumnDefOJ.ALGORITHM_CALC_OFFROAD;//10.2.2010
            if (nonAutomatic) {//27.9.2009
                if ((column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER)
                        || (column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT)) {
                    if (cbxOperation.getSelectedIndex() != 0) {
                        cbxOperation.setSelectedIndex(0);
                    }
                }
                if ((column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER)
                        || (column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT)) {
                    if (cbxOperation.getSelectedIndex() != 1) {
                        cbxOperation.setSelectedIndex(1);
                    }
                }
            } else {
                if (cbxOperation.getSelectedIndex() != (column.getColumnDef().getAlgorithm() + 1)) {
                    cbxOperation.setSelectedIndex(column.getColumnDef().getAlgorithm() + 1);
                }
            }
            pnlTextMode.setVisible(false);

            switch (column.getColumnDef().getAlgorithm()) {
                case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
                case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:
                case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:
                case ColumnDefOJ.ALGORITHM_CALC_ORIENTATION:
                case ColumnDefOJ.ALGORITHM_CALC_OFFROAD:
                    updateOperandsView(2, true, true);
                    updateYtemTypeText(column, 0);
                    updateYtemTypeText(column, 1);
                    updateCloneText(column, 0);
                    updateCloneText(column, 1);
                    updatePointText(column, 0);
                    updatePointText(column, 1);
                    break;

                case ColumnDefOJ.ALGORITHM_CALC_PATH:

                case ColumnDefOJ.ALGORITHM_CALC_XPOS:
                case ColumnDefOJ.ALGORITHM_CALC_YPOS:
                case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
                    updateOperandsView(1, true, true);
                    updateYtemTypeText(column, 0);
                    updateCloneText(column, 0);
                    updatePointText(column, 0);
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
                    updateOperandsView(3, true, true);
                    updateYtemTypeText(column, 0);
                    updateYtemTypeText(column, 1);
                    updateYtemTypeText(column, 2);
                    updateCloneText(column, 0);
                    updateCloneText(column, 1);
                    updateCloneText(column, 2);
                    updatePointText(column, 0);
                    updatePointText(column, 1);
                    updatePointText(column, 2);
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_COUNT:
                    updateOperandsView(1, false, false);
                    updateYtemTypeText(column, 0);//29.9.2009
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_AREA:
                case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
                case ColumnDefOJ.ALGORITHM_CALC_SLICE:
                case ColumnDefOJ.ALGORITHM_CALC_EXISTS:
                    updateOperandsView(1, true, false);
                    updateYtemTypeText(column, 0);
                    updateCloneText(column, 0);
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_IMAGE:
                case ColumnDefOJ.ALGORITHM_CALC_ID:
                case ColumnDefOJ.ALGORITHM_CALC_INDEX://21.9.2009
                case ColumnDefOJ.ALGORITHM_CALC_FILE_NAME:
                    updateOperandsView(0, false, false);
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER:
                case ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT:
                case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER:
                case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT:
                    pnlTextMode.setVisible(true);
                    updateOperandsView(0, false, false);
                    break;
                default:
                    updateOperandsView(0, false, false);
            }

            spinDigits.setValue(new Integer(column.getColumnDef().getColumnDigits()));

            int colorIndex = getColorIndex(column.getColumnDef().getColumnColor());
            if (colorIndex < 13) {
                if (cbxColor.getSelectedIndex() != colorIndex) {
                    cbxColor.setSelectedIndex(colorIndex);
                }
            } else {
                ((DefaultComboBoxModel) cbxColor.getModel()).setSelectedItem(column.getColumnDef().getColumnColor());
            }
            tabbedColumnDetail.setVisible(true);
        } else {
            tabbedColumnDetail.setVisible(false);
        }

        //btnRemove.setEnabled(column != null);
        btnRemove.setEnabled(tblColumns.getSelectedRowCount() > 0);
    }

    /**
     * get index from predefined colors, such as red = 10
     */
    private static int getColorIndex(Color color) {
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
        if (color.getRGB() == new Color(0, 153, 0).getRGB()) {
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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rightPanelA = new javax.swing.JPanel();
        rightPanelB = new javax.swing.JPanel();
        tabbedColumnDetail = new javax.swing.JTabbedPane();
        pnlOperands = new javax.swing.JPanel();
        pnlOperation = new javax.swing.JPanel();
        lbOperation = new javax.swing.JLabel();
        cbxOperation = new javax.swing.JComboBox();
        pnlFirstOperand = new javax.swing.JPanel();
        lblFirstObjectType = new javax.swing.JLabel();
        lblFirstClone = new javax.swing.JLabel();
        lblFirstPoint = new javax.swing.JLabel();
        cbxFirstObjectType = new javax.swing.JComboBox();
        txtFirstClone = new javax.swing.JTextField();
        txtFirstPoint = new javax.swing.JTextField();
        pnlSecondOperand = new javax.swing.JPanel();
        lblSecondObjectType = new javax.swing.JLabel();
        lblSecondClone = new javax.swing.JLabel();
        lblSecondPoint = new javax.swing.JLabel();
        cbxSecondObjectType = new javax.swing.JComboBox();
        txtSecondClone = new javax.swing.JTextField();
        txtSecondPoint = new javax.swing.JTextField();
        pnlThirdOperand = new javax.swing.JPanel();
        lblThirdObjectType = new javax.swing.JLabel();
        lblThirdClone = new javax.swing.JLabel();
        lblThirdPoint = new javax.swing.JLabel();
        cbxThirdObjectType = new javax.swing.JComboBox();
        txtThirdClone = new javax.swing.JTextField();
        txtThirdPoint = new javax.swing.JTextField();
        pnlTextMode = new javax.swing.JPanel();
        pnlPresentation = new javax.swing.JPanel();
        spinDigits = new javax.swing.JSpinner();
        lbPrecision = new javax.swing.JLabel();
        lbColor = new javax.swing.JLabel();
        cbxColor = new javax.swing.JComboBox(new DefaultComboBoxModel());
        cbxColor.setRenderer(new ColorRendererOJ());
        lbColumnAppearance = new javax.swing.JLabel();
        tfXMin = new javax.swing.JTextField();
        lbXMin = new javax.swing.JLabel();
        tfXMax = new javax.swing.JTextField();
        lbXMax = new javax.swing.JLabel();
        tfBinWidth = new javax.swing.JTextField();
        lbBinWidth = new javax.swing.JLabel();
        tfYMax = new javax.swing.JTextField();
        lbYMax = new javax.swing.JLabel();
        lbHistogram = new javax.swing.JLabel();
        btnShowHisto = new javax.swing.JButton();
        leftPanel = new oj.graphics.RoundPanelOJ();
        pnlListOfColumns = new javax.swing.JPanel();
        scrlpaneListOfColumns = new javax.swing.JScrollPane();
        tblColumns = new javax.swing.JTable();
        tblColumns.getColumnModel().addColumnModelListener(this);
        pnlTwoButtons = new javax.swing.JPanel();
        btnNew = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 20, 20));
        setMaximumSize(new java.awt.Dimension(561, 477));
        setMinimumSize(new java.awt.Dimension(561, 477));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(561, 550));
        setLayout(new java.awt.BorderLayout());

        rightPanelA.setMaximumSize(new java.awt.Dimension(300, 2147483647));
        rightPanelA.setMinimumSize(new java.awt.Dimension(300, 139));
        rightPanelA.setPreferredSize(new java.awt.Dimension(300, 100));
        rightPanelA.setLayout(new java.awt.BorderLayout(0, 10));

        rightPanelB.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanelB.setLayout(new java.awt.BorderLayout());

        tabbedColumnDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tabbedColumnDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabbedColumnDetailMouseClicked(evt);
            }
        });

        pnlOperands.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 2));

        pnlOperation.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1), javax.swing.BorderFactory.createTitledBorder("")));
        pnlOperation.setMaximumSize(new java.awt.Dimension(270, 45));
        pnlOperation.setMinimumSize(new java.awt.Dimension(270, 45));
        pnlOperation.setPreferredSize(new java.awt.Dimension(270, 45));
        pnlOperation.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 5));

        lbOperation.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lbOperation.setText("Operation");
        lbOperation.setMaximumSize(new java.awt.Dimension(65, 14));
        lbOperation.setMinimumSize(new java.awt.Dimension(65, 14));
        lbOperation.setPreferredSize(new java.awt.Dimension(65, 14));
        pnlOperation.add(lbOperation);

        cbxOperation.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        cbxOperation.setMaximumSize(new java.awt.Dimension(150, 20));
        cbxOperation.setMinimumSize(new java.awt.Dimension(150, 20));
        cbxOperation.setPreferredSize(new java.awt.Dimension(150, 20));
        cbxOperation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxOperationItemStateChanged(evt);
            }
        });
        cbxOperation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxOperationActionPerformed(evt);
            }
        });
        pnlOperation.add(cbxOperation);

        pnlOperands.add(pnlOperation);

        pnlFirstOperand.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1), javax.swing.BorderFactory.createTitledBorder("1st Operand")));
        pnlFirstOperand.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        pnlFirstOperand.setMaximumSize(new java.awt.Dimension(270, 80));
        pnlFirstOperand.setMinimumSize(new java.awt.Dimension(270, 80));
        pnlFirstOperand.setPreferredSize(new java.awt.Dimension(270, 80));
        pnlFirstOperand.setLayout(new java.awt.GridBagLayout());

        lblFirstObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblFirstObjectType.setText("Item type");
        lblFirstObjectType.setMaximumSize(new java.awt.Dimension(120, 14));
        lblFirstObjectType.setMinimumSize(new java.awt.Dimension(120, 14));
        lblFirstObjectType.setPreferredSize(new java.awt.Dimension(120, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlFirstOperand.add(lblFirstObjectType, gridBagConstraints);

        lblFirstClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblFirstClone.setText("Clone");
        lblFirstClone.setMaximumSize(new java.awt.Dimension(40, 14));
        lblFirstClone.setMinimumSize(new java.awt.Dimension(40, 14));
        lblFirstClone.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 1);
        pnlFirstOperand.add(lblFirstClone, gridBagConstraints);

        lblFirstPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblFirstPoint.setText("Point");
        lblFirstPoint.setMaximumSize(new java.awt.Dimension(40, 14));
        lblFirstPoint.setMinimumSize(new java.awt.Dimension(40, 14));
        lblFirstPoint.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlFirstOperand.add(lblFirstPoint, gridBagConstraints);

        cbxFirstObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        cbxFirstObjectType.setMaximumSize(new java.awt.Dimension(120, 18));
        cbxFirstObjectType.setMinimumSize(new java.awt.Dimension(120, 18));
        cbxFirstObjectType.setPreferredSize(new java.awt.Dimension(120, 20));
        cbxFirstObjectType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFirstObjectTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlFirstOperand.add(cbxFirstObjectType, gridBagConstraints);

        txtFirstClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtFirstClone.setMaximumSize(new java.awt.Dimension(40, 20));
        txtFirstClone.setMinimumSize(new java.awt.Dimension(40, 20));
        txtFirstClone.setPreferredSize(new java.awt.Dimension(40, 20));
        txtFirstClone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFirstCloneKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlFirstOperand.add(txtFirstClone, gridBagConstraints);

        txtFirstPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtFirstPoint.setMaximumSize(new java.awt.Dimension(40, 20));
        txtFirstPoint.setMinimumSize(new java.awt.Dimension(40, 20));
        txtFirstPoint.setPreferredSize(new java.awt.Dimension(40, 20));
        txtFirstPoint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFirstPointKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlFirstOperand.add(txtFirstPoint, gridBagConstraints);

        pnlOperands.add(pnlFirstOperand);

        pnlSecondOperand.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1), javax.swing.BorderFactory.createTitledBorder("2nd Operand")));
        pnlSecondOperand.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        pnlSecondOperand.setMaximumSize(new java.awt.Dimension(270, 80));
        pnlSecondOperand.setMinimumSize(new java.awt.Dimension(270, 80));
        pnlSecondOperand.setPreferredSize(new java.awt.Dimension(270, 80));
        pnlSecondOperand.setLayout(new java.awt.GridBagLayout());

        lblSecondObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblSecondObjectType.setText("Item type");
        lblSecondObjectType.setMaximumSize(new java.awt.Dimension(120, 14));
        lblSecondObjectType.setMinimumSize(new java.awt.Dimension(120, 14));
        lblSecondObjectType.setPreferredSize(new java.awt.Dimension(120, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlSecondOperand.add(lblSecondObjectType, gridBagConstraints);

        lblSecondClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblSecondClone.setText("Clone");
        lblSecondClone.setMaximumSize(new java.awt.Dimension(40, 14));
        lblSecondClone.setMinimumSize(new java.awt.Dimension(40, 14));
        lblSecondClone.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 0);
        pnlSecondOperand.add(lblSecondClone, gridBagConstraints);

        lblSecondPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblSecondPoint.setText("Point");
        lblSecondPoint.setMaximumSize(new java.awt.Dimension(40, 14));
        lblSecondPoint.setMinimumSize(new java.awt.Dimension(40, 14));
        lblSecondPoint.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlSecondOperand.add(lblSecondPoint, gridBagConstraints);

        cbxSecondObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        cbxSecondObjectType.setMaximumSize(new java.awt.Dimension(120, 32767));
        cbxSecondObjectType.setMinimumSize(new java.awt.Dimension(120, 18));
        cbxSecondObjectType.setPreferredSize(new java.awt.Dimension(120, 20));
        cbxSecondObjectType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxSecondObjectTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlSecondOperand.add(cbxSecondObjectType, gridBagConstraints);

        txtSecondClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtSecondClone.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSecondClone.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSecondClone.setPreferredSize(new java.awt.Dimension(40, 20));
        txtSecondClone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSecondCloneKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlSecondOperand.add(txtSecondClone, gridBagConstraints);

        txtSecondPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtSecondPoint.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSecondPoint.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSecondPoint.setPreferredSize(new java.awt.Dimension(40, 20));
        txtSecondPoint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSecondPointKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlSecondOperand.add(txtSecondPoint, gridBagConstraints);

        pnlOperands.add(pnlSecondOperand);

        pnlThirdOperand.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1), javax.swing.BorderFactory.createTitledBorder("3rd Operand")));
        pnlThirdOperand.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        pnlThirdOperand.setMaximumSize(new java.awt.Dimension(270, 80));
        pnlThirdOperand.setMinimumSize(new java.awt.Dimension(270, 80));
        pnlThirdOperand.setPreferredSize(new java.awt.Dimension(270, 80));
        pnlThirdOperand.setLayout(new java.awt.GridBagLayout());

        lblThirdObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblThirdObjectType.setText("Item type");
        lblThirdObjectType.setMaximumSize(new java.awt.Dimension(120, 14));
        lblThirdObjectType.setMinimumSize(new java.awt.Dimension(120, 14));
        lblThirdObjectType.setPreferredSize(new java.awt.Dimension(120, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlThirdOperand.add(lblThirdObjectType, gridBagConstraints);

        lblThirdClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblThirdClone.setText("Clone");
        lblThirdClone.setMaximumSize(new java.awt.Dimension(40, 14));
        lblThirdClone.setMinimumSize(new java.awt.Dimension(40, 14));
        lblThirdClone.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 0);
        pnlThirdOperand.add(lblThirdClone, gridBagConstraints);

        lblThirdPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblThirdPoint.setText("Point");
        lblThirdPoint.setMaximumSize(new java.awt.Dimension(40, 14));
        lblThirdPoint.setMinimumSize(new java.awt.Dimension(40, 14));
        lblThirdPoint.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlThirdOperand.add(lblThirdPoint, gridBagConstraints);

        cbxThirdObjectType.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        cbxThirdObjectType.setMaximumSize(new java.awt.Dimension(120, 32767));
        cbxThirdObjectType.setMinimumSize(new java.awt.Dimension(120, 18));
        cbxThirdObjectType.setPreferredSize(new java.awt.Dimension(120, 20));
        cbxThirdObjectType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxThirdObjectTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlThirdOperand.add(cbxThirdObjectType, gridBagConstraints);

        txtThirdClone.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtThirdClone.setMaximumSize(new java.awt.Dimension(40, 20));
        txtThirdClone.setMinimumSize(new java.awt.Dimension(40, 20));
        txtThirdClone.setPreferredSize(new java.awt.Dimension(40, 20));
        txtThirdClone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtThirdCloneKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlThirdOperand.add(txtThirdClone, gridBagConstraints);

        txtThirdPoint.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        txtThirdPoint.setMaximumSize(new java.awt.Dimension(40, 20));
        txtThirdPoint.setMinimumSize(new java.awt.Dimension(40, 20));
        txtThirdPoint.setPreferredSize(new java.awt.Dimension(40, 20));
        txtThirdPoint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtThirdPointKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 3, 8);
        pnlThirdOperand.add(txtThirdPoint, gridBagConstraints);

        pnlOperands.add(pnlThirdOperand);

        pnlTextMode.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 4, 1), javax.swing.BorderFactory.createTitledBorder("")));
        pnlTextMode.setMaximumSize(new java.awt.Dimension(270, 35));
        pnlTextMode.setMinimumSize(new java.awt.Dimension(270, 35));
        pnlTextMode.setPreferredSize(new java.awt.Dimension(270, 35));
        pnlTextMode.setLayout(new java.awt.BorderLayout());
        pnlOperands.add(pnlTextMode);

        tabbedColumnDetail.addTab("Operands", pnlOperands);

        spinDigits.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        spinDigits.setMaximumSize(new java.awt.Dimension(152, 20));
        spinDigits.setMinimumSize(new java.awt.Dimension(152, 20));
        spinDigits.setPreferredSize(new java.awt.Dimension(152, 20));
        spinDigits.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinDigitsStateChanged(evt);
            }
        });

        lbPrecision.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lbPrecision.setText("Precision");
        lbPrecision.setMaximumSize(new java.awt.Dimension(80, 14));
        lbPrecision.setMinimumSize(new java.awt.Dimension(80, 14));
        lbPrecision.setPreferredSize(new java.awt.Dimension(80, 14));

        lbColor.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lbColor.setText("Color");
        lbColor.setMaximumSize(new java.awt.Dimension(80, 14));
        lbColor.setMinimumSize(new java.awt.Dimension(80, 14));
        lbColor.setPreferredSize(new java.awt.Dimension(80, 14));

        cbxColor.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        cbxColor.setDoubleBuffered(true);
        cbxColor.setMaximumSize(new java.awt.Dimension(152, 20));
        cbxColor.setMinimumSize(new java.awt.Dimension(152, 20));
        cbxColor.setPreferredSize(new java.awt.Dimension(152, 20));
        cbxColor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxColorItemStateChanged(evt);
            }
        });
        cbxColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxColorActionPerformed(evt);
            }
        });

        lbColumnAppearance.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lbColumnAppearance.setForeground(new java.awt.Color(100, 100, 100));
        lbColumnAppearance.setText("Column Appearance:");

        tfXMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfXMinActionPerformed(evt);
            }
        });
        tfXMin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfXMinKeyReleased(evt);
            }
        });

        lbXMin.setText("xMin");

        tfXMax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfXMaxKeyReleased(evt);
            }
        });

        lbXMax.setText("xMax");

        tfBinWidth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfBinWidthKeyReleased(evt);
            }
        });

        lbBinWidth.setText("binWidth");

        tfYMax.setText("    ");
        tfYMax.setMaximumSize(new java.awt.Dimension(66, 2147483647));
        tfYMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfYMaxActionPerformed(evt);
            }
        });
        tfYMax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfYMaxKeyReleased(evt);
            }
        });

        lbYMax.setText("yMax");

        lbHistogram.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        lbHistogram.setForeground(new java.awt.Color(100, 100, 100));
        lbHistogram.setText("Histogram:");

        btnShowHisto.setText("Udate Histogram");
        btnShowHisto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHistoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlPresentationLayout = new org.jdesktop.layout.GroupLayout(pnlPresentation);
        pnlPresentation.setLayout(pnlPresentationLayout);
        pnlPresentationLayout.setHorizontalGroup(
            pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPresentationLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPresentationLayout.createSequentialGroup()
                            .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(lbColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(lbPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(spinDigits, 0, 0, Short.MAX_VALUE)
                                .add(cbxColor, 0, 135, Short.MAX_VALUE)))
                        .add(btnShowHisto))
                    .add(pnlPresentationLayout.createSequentialGroup()
                        .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lbXMin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lbXMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lbBinWidth, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lbYMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(tfBinWidth, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                            .add(tfXMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                            .add(tfXMin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                            .add(tfYMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .add(lbColumnAppearance)
                    .add(lbHistogram))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        pnlPresentationLayout.setVerticalGroup(
            pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPresentationLayout.createSequentialGroup()
                .addContainerGap()
                .add(lbColumnAppearance)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbxColor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .add(lbColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lbPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(spinDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(lbHistogram)
                .add(14, 14, 14)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbXMin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tfXMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbXMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tfXMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbBinWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tfBinWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPresentationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbYMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .add(tfYMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnShowHisto)
                .add(134, 134, 134))
        );

        tabbedColumnDetail.addTab("Presentation", pnlPresentation);

        rightPanelB.add(tabbedColumnDetail, java.awt.BorderLayout.CENTER);
        tabbedColumnDetail.getAccessibleContext().setAccessibleName("");

        rightPanelA.add(rightPanelB, java.awt.BorderLayout.CENTER);

        add(rightPanelA, java.awt.BorderLayout.CENTER);

        leftPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setLayout(new java.awt.BorderLayout());

        pnlListOfColumns.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        pnlListOfColumns.setMaximumSize(new java.awt.Dimension(220, 2147483647));
        pnlListOfColumns.setMinimumSize(new java.awt.Dimension(220, 54));
        pnlListOfColumns.setPreferredSize(new java.awt.Dimension(220, 100));
        pnlListOfColumns.setLayout(new java.awt.BorderLayout());

        tblColumns.setModel(new ColumnsTableModel());
        tblColumns.setRowHeight(22);
        tblColumns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblColumns.setShowHorizontalLines(false);
        tblColumns.setShowVerticalLines(false);
        tblColumns.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblColumnsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblColumnsMouseReleased(evt);
            }
        });
        tblColumns.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblColumnsFocusGained(evt);
            }
        });
        tblColumns.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                tblColumnsCaretPositionChanged(evt);
            }
        });
        tblColumns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblColumnsKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblColumnsKeyPressed(evt);
            }
        });
        scrlpaneListOfColumns.setViewportView(tblColumns);

        pnlListOfColumns.add(scrlpaneListOfColumns, java.awt.BorderLayout.CENTER);

        leftPanel.add(pnlListOfColumns, java.awt.BorderLayout.WEST);

        pnlTwoButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTwoButtons.setLayout(new java.awt.GridLayout(1, 0));

        btnNew.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusPainted(false);
        btnNew.setMaximumSize(new java.awt.Dimension(81, 29));
        btnNew.setMinimumSize(new java.awt.Dimension(81, 29));
        btnNew.setPreferredSize(new java.awt.Dimension(81, 29));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        pnlTwoButtons.add(btnNew);

        btnRemove.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        btnRemove.setText("Remove");
        btnRemove.setFocusable(false);
        btnRemove.setMaximumSize(new java.awt.Dimension(85, 29));
        btnRemove.setMinimumSize(new java.awt.Dimension(85, 29));
        btnRemove.setPreferredSize(new java.awt.Dimension(85, 29));
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        pnlTwoButtons.add(btnRemove);

        leftPanel.add(pnlTwoButtons, java.awt.BorderLayout.SOUTH);

        add(leftPanel, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Removes a column and it's entries when Remove button is clicked
     */
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        //nameEditor.getFocusListeners()[0].focusLost(null);

        if (ProjectResultsOJ.getInstance() != null) {
            ij.IJ.showStatus("Project results window was automatically closed");
        }
        ProjectResultsOJ.close();//15.1.2012

        int[] indexes = tblColumns.getSelectedRows();
        for (int jj = indexes.length - 1; jj >= 0; jj--) {
            ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByIndex(indexes[jj]);
            String column_name = column.getName();
            OJ.getData().getResults().getColumns().removeColumnByName(column_name);
        }
    }//GEN-LAST:event_btnRemoveActionPerformed
    /**
     * creates a new column with default name when New Column button is pressed
     *
     * @param evt
     */
    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        if (ProjectResultsOJ.getInstance() != null) {
            ij.IJ.showStatus("Project results window was automatically closed");
        }
        ProjectResultsOJ.close();//15.1.2012
        suffix += 1;
        String name = "Column" + Integer.toString(suffix);
        while (OJ.getData().getResults().getColumns().getColumnByName(name) != null) {
            suffix += 1;
            name = "Column" + Integer.toString(suffix);
        }
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        int atPosition = columns.addColumn(new ColumnOJ(name), true);//1.2.2014
        tblColumns.getSelectionModel().setSelectionInterval(atPosition, atPosition);
        // tblColumns.scrollRectToVisible(new Rectangle(0,0,1,1));
        tblColumns.scrollRectToVisible(tblColumns.getCellRect(atPosition, 0, true));
        updateView();//2.2.2014
    }//GEN-LAST:event_btnNewActionPerformed

    /**
     * Extra initialisations
     */
    private void initComponentsExt() {
        fillOperations();
        fillObjectDefs();
        fillColors();

        //if (IJ.isMacintosh()) {
        ((TitledBorder) ((CompoundBorder) pnlOperation.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        ((TitledBorder) ((CompoundBorder) pnlFirstOperand.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        ((TitledBorder) ((CompoundBorder) pnlSecondOperand.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        ((TitledBorder) ((CompoundBorder) pnlThirdOperand.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        ((TitledBorder) ((CompoundBorder) pnlTextMode.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        // ((TitledBorder) ((CompoundBorder) pnlColor.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        // ((TitledBorder) ((CompoundBorder) pnlPrecision.getBorder()).getInsideBorder()).setBorder(new EmptyBorder(1, 1, 1, 1));
        //}

        tblColumns.setEnabled(true);
        tblColumns.getTableHeader().setOpaque(true);
        tblColumns.getTableHeader().setDefaultRenderer(columnsHeaderTableRenderer);

        //tblColumns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblColumns.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//4.1.2013

        /**
         * nameEditor is used when double-clicking on a column name in left
         * table, or creating a new column
         */
        nameEditor.addFocusListener(new FocusListener() {
            int index;
            ColumnOJ column;

            public void focusGained(FocusEvent event) {
                index = tblColumns.getSelectedRow();
                if ((index >= 0) && (index < OJ.getData().getResults().getColumns().getAllColumnsCount())) {//9.9.2009
                    column = OJ.getData().getResults().getColumns().getColumnByIndex(index);
                    nameEditor.setText(column.getName());
                } else {
                    nameEditor.setText("");
                    column = null;
                }
            }

            public void focusLost(FocusEvent event) {
                if ((column != null) && (nameEditor.getText().trim().length() > 0)) {

                    String oldName = column.getName();
                    boolean oldIsUnlinked = oldName.startsWith("_");
                    String newName = nameEditor.getText().trim();//5.7.2009
                    boolean newIsUnlinked = newName.startsWith("_");
                    ColumnsOJ columns = oj.OJ.getData().getResults().getColumns();

                    ColumnOJ col = columns.getColumnByName(newName);
                    if (col != null && col != column) {
                        ij.IJ.showMessage(newName + ": column already exists");
                        return;
                    }
                    String notAllowed = ("* \t,\n");//20.7.2009
                    for (int jj = 0; jj < notAllowed.length(); jj++) {
                        if (newName.indexOf(notAllowed.charAt(jj)) >= 0) {
                            ij.IJ.showMessage(newName + ": illegal character");
                            return;
                        }
                    }

                    if (oldIsUnlinked != newIsUnlinked) {
                        if (IJ.showMessageWithCancel("Column Settings", "Changing from 'unlinked' to 'linked' or vice versa will clear contents of this column")) {
                            column.clear();
                            column.setName(newName);

                            column.getColumnDef().clearOperands();
                            if (newIsUnlinked) {

                                column.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER);
                            }

                            if (!newIsUnlinked) {

                                column.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER);
                            }

                            columns.fixColumnsOrder();//20.7.2009
                            ((ColumnsTableModel) tblColumns.getModel()).fireTableRowUpdated(index);
                            ((ColumnsTableModel) tblColumns.getModel()).fireTableDataChanged();//my guess
                            return;
                        } else {
                            return;
                        }
                    }

                    column.setName(nameEditor.getText().trim());
                    ((ColumnsTableModel) tblColumns.getModel()).fireTableRowUpdated(index);
                }
            }
        });
    }

    /**
     * first removes all operands from columnDef's operand array list, then
     * creates new ones and gives tham by default the name of the first ytemdefs
     * such as "axis"
     */
    private void updateOperands(ColumnDefOJ columnDef, int count) {
        switch (count) {
            case 0:
                for (int i = columnDef.getOperandCount() - 1; i == 0; i--) {
                    columnDef.removeOperand(i);
                }
                break;
            case 1:
                for (int i = columnDef.getOperandCount() - 1; i == 1; i--) {
                    columnDef.removeOperand(i);
                }
                for (int i = columnDef.getOperandCount(); i < 1; i++) {
                    columnDef.addOperand(new OperandOJ());
                    columnDef.getOperand(i).setYtemName(OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName());
                }
                break;
            case 2:
                for (int i = columnDef.getOperandCount() - 1; i == 2; i--) {
                    columnDef.removeOperand(i);
                }
                for (int i = columnDef.getOperandCount(); i < 2; i++) {
                    columnDef.addOperand(new OperandOJ());
                    columnDef.getOperand(i).setYtemName(OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName());
                }
                break;
            case 3:
                for (int i = columnDef.getOperandCount() - 1; i == 3; i--) {
                    columnDef.removeOperand(i);
                }
                for (int i = columnDef.getOperandCount(); i < 3; i++) {
                    columnDef.addOperand(new OperandOJ());
                    columnDef.getOperand(i).setYtemName(OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName());
                }
                break;
        }
    }

private void tblColumnsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblColumnsMousePressed
    updateView();
}//GEN-LAST:event_tblColumnsMousePressed

    /**
     * not used
     */
private void tblColumnsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblColumnsKeyTyped

    IJ.showStatus("Key typed " + evt.getKeyCode());
    if (true) {
        return;
    }
    IJ.wait(500);
    int jj = tblColumns.getSelectedRow();
    if (evt.getKeyCode() == evt.VK_UP && jj > 0) {
        tblColumns.editCellAt(0, jj - 1, null);

    }
    tblColumns.editingCanceled(null);

    evt.consume();
    tblColumns.repaint();

    updateView();
}//GEN-LAST:event_tblColumnsKeyTyped

    /**
     * not used
     */
private void tblColumnsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblColumnsFocusGained
}//GEN-LAST:event_tblColumnsFocusGained

private void tblColumnsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblColumnsKeyPressed
    int flags = evt.getModifiers();
    boolean up = evt.getKeyCode() == KeyEvent.VK_UP;
    boolean down = evt.getKeyCode() == KeyEvent.VK_DOWN;
    boolean control = (flags & KeyEvent.CTRL_MASK) != 0;
    if (control && (up || down)) {
        shiftColumns(up, down);
    } else {
        int jj = tblColumns.getSelectedRow();
        if (evt.getKeyCode() == KeyEvent.VK_UP && jj > 0) {
            tblColumns.setRowSelectionInterval(jj - 1, jj - 1);
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN && jj < tblColumns.getRowCount() - 1) {
            tblColumns.setRowSelectionInterval(jj + 1, jj + 1);
        }
    }
    evt.consume();
    updateView();
}//GEN-LAST:event_tblColumnsKeyPressed

    private void shiftColumns(boolean left, boolean right) {

        ProjectResultsOJ.close();
        int[] selColumns = tblColumns.getSelectedRows();
        int selCount = selColumns.length;
        if (selCount > 0) {

            ColumnsOJ columns = OJ.getData().getResults().getColumns();
            if (left && selColumns[0] > 0 && selColumns[selCount - 1] < columns.getLinkedColumnsCount()) {
                for (int jj = 0; jj < selCount; jj++) {
                    int oldIndex = selColumns[jj];
                    int newIndex = oldIndex - 1;
                    ColumnOJ column1 = columns.getColumnByIndex(oldIndex);
                    ColumnOJ column2 = columns.getColumnByIndex(newIndex);
                    columns.setColumn(newIndex, column1);
                    columns.setColumn(oldIndex, column2);
                }
                for (int jj = 0; jj < selCount; jj++) {
                    selColumns[jj]--;
                }

            }
            if (right && selColumns[selCount - 1] < columns.getLinkedColumnsCount() - 1) {
                for (int jj = selCount - 1; jj >= 0; jj--) {
                    int oldIndex = selColumns[jj];
                    int newIndex = oldIndex + 1;
                    ColumnOJ column1 = columns.getColumnByIndex(oldIndex);
                    ColumnOJ column2 = columns.getColumnByIndex(newIndex);
                    columns.setColumn(newIndex, column1);
                    columns.setColumn(oldIndex, column2);

                }
                for (int jj = 0; jj < selCount; jj++) {
                    selColumns[jj]++;
                }

            }
            if (right || left) {
                ((ColumnsTableModel) tblColumns.getModel()).fireTableDataChanged();
                for (int jj = 0; jj < selCount; jj++) {
                    tblColumns.getSelectionModel().addSelectionInterval(selColumns[jj], selColumns[jj]);
                }
            }
            updateView();
        }
    }

private void tblColumnsCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tblColumnsCaretPositionChanged
}//GEN-LAST:event_tblColumnsCaretPositionChanged

private void btnShowHistoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowHistoActionPerformed
    ColumnOJ column = getSelectedColumn();//26.1.2010
    if (column != null) {
        new PlotOJ().makeHistoFromColumn(column);
    }
}//GEN-LAST:event_btnShowHistoActionPerformed

private void cbxColorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxColorItemStateChanged
    ColumnOJ column = getSelectedColumn();
    int index = cbxColor.getSelectedIndex();
    if (index == 13) {
        Color initColor = Color.BLACK;
        if (column != null) {
            initColor = column.getColumnDef().getColumnColor();
        }
        Color color = JColorChooser.showDialog(this, "Choose Column Color", initColor);
        ((DefaultComboBoxModel) cbxColor.getModel()).setSelectedItem(color);
        if (column != null) {
            if (color.getRGB() != column.getColumnDef().getColumnColor().getRGB()) {
                column.getColumnDef().setColumnColor(color);
            }
        }
    } else {
        if (column != null) {
            if (((Color) cbxColor.getSelectedItem()).getRGB() != column.getColumnDef().getColumnColor().getRGB()) {
                column.getColumnDef().setColumnColor((Color) cbxColor.getSelectedItem());
            }
        }
    }
    if (column != null) {
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
        updateView();
    }
}//GEN-LAST:event_cbxColorItemStateChanged

private void txtThirdPointKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtThirdPointKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(2);
        try {
            int value = Integer.parseInt(txtThirdPoint.getText()) - 1;
            if (value != operand.getRelPosition()) {
                operand.setRelPosition(value);
            }
        } catch (NumberFormatException e) {
            if (!txtThirdPoint.getText().equals("")) {
                txtThirdPoint.setText(Integer.toString(operand.getRelPosition() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtThirdPoint.setText("");
    }
}//GEN-LAST:event_txtThirdPointKeyReleased

private void txtThirdCloneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtThirdCloneKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(2);
        try {
            int value = Integer.parseInt(txtThirdClone.getText()) - 1;
            if (value != operand.getYtemClone()) {
                operand.setYtemClone(value);
            }
        } catch (NumberFormatException e) {
            if (!txtThirdClone.getText().equals("")) {
                txtThirdClone.setText(Integer.toString(operand.getYtemClone() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtThirdClone.setText("");
    }
}//GEN-LAST:event_txtThirdCloneKeyReleased

private void cbxThirdObjectTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxThirdObjectTypeItemStateChanged
    ColumnOJ column = getSelectedColumn();
    if ((column != null) && (column.getColumnDef().getOperand(2) != null) && (cbxThirdObjectType.getSelectedIndex() >= 0)) {
        if (!column.getColumnDef().getOperand(2/*3*/).getObjectName().equals((String) cbxThirdObjectType.getSelectedItem())) {//7.4.2009
            column.getColumnDef().getOperand(2).setYtemName((String) cbxThirdObjectType.getSelectedItem());
            column.reset();
            OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
            updateView();
        }
    }
}//GEN-LAST:event_cbxThirdObjectTypeItemStateChanged

private void txtSecondPointKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSecondPointKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(1);
        try {
            int value = Integer.parseInt(txtSecondPoint.getText()) - 1;
            if (value != operand.getRelPosition()) {
                operand.setRelPosition(value);
            }
        } catch (NumberFormatException e) {
            if (!txtSecondPoint.getText().equals("")) {
                txtSecondPoint.setText(Integer.toString(operand.getRelPosition() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtSecondPoint.setText("");
    }
}//GEN-LAST:event_txtSecondPointKeyReleased

private void txtSecondCloneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSecondCloneKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(1);
        try {
            int value = Integer.parseInt(txtSecondClone.getText()) - 1;
            if (value != operand.getYtemClone()) {
                operand.setYtemClone(value);
            }
        } catch (NumberFormatException e) {
            if (!txtSecondClone.getText().equals("")) {
                txtSecondClone.setText(Integer.toString(operand.getYtemClone() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtSecondClone.setText("");
    }
}//GEN-LAST:event_txtSecondCloneKeyReleased

private void cbxSecondObjectTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxSecondObjectTypeItemStateChanged
    ColumnOJ column = getSelectedColumn();
    if ((column != null) && (column.getColumnDef().getOperand(1) != null) && (cbxSecondObjectType.getSelectedIndex() >= 0)) {
        if (!column.getColumnDef().getOperand(1).getObjectName().equals((String) cbxSecondObjectType.getSelectedItem())) {
            column.getColumnDef().getOperand(1).setYtemName((String) cbxSecondObjectType.getSelectedItem());
            column.reset();
            OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
            updateView();
        }
    }
}//GEN-LAST:event_cbxSecondObjectTypeItemStateChanged

private void txtFirstPointKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFirstPointKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(0);
        try {
            int value = Integer.parseInt(txtFirstPoint.getText()) - 1;
            if (value != operand.getRelPosition()) {
                operand.setRelPosition(value);
            }
        } catch (NumberFormatException e) {
            if (!txtFirstPoint.getText().equals("")) {
                txtFirstPoint.setText(Integer.toString(operand.getRelPosition() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtFirstPoint.setText("");
    }
}//GEN-LAST:event_txtFirstPointKeyReleased

private void txtFirstCloneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFirstCloneKeyReleased
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        OperandOJ operand = column.getColumnDef().getOperand(0);
        try {
            int value = Integer.parseInt(txtFirstClone.getText()) - 1;
            if (value != operand.getYtemClone()) {
                operand.setYtemClone(value);
            }
        } catch (NumberFormatException e) {
            if (!txtFirstClone.getText().equals("")) {
                txtFirstClone.setText(Integer.toString(operand.getYtemClone() + 1));
            }
        }
        column.reset();
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    } else {
        txtFirstClone.setText("");
    }
}//GEN-LAST:event_txtFirstCloneKeyReleased

private void cbxFirstObjectTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFirstObjectTypeItemStateChanged
    ColumnOJ column = getSelectedColumn();
    if ((column != null) && (column.getColumnDef().getOperand(0) != null) && (cbxFirstObjectType.getSelectedIndex() >= 0)) {
        if (!column.getColumnDef().getOperand(0).getObjectName().equals((String) cbxFirstObjectType.getSelectedItem())) {
            column.getColumnDef().getOperand(0).setYtemName((String) cbxFirstObjectType.getSelectedItem());
            column.reset();
            OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
            updateView();
        }
    }
}//GEN-LAST:event_cbxFirstObjectTypeItemStateChanged

private void cbxOperationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxOperationActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cbxOperationActionPerformed

    /**
     * Fill operations panel with default operands when changing the algorithm
     */
private void cbxOperationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxOperationItemStateChanged
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        int old_algorithm = column.getColumnDef().getAlgorithm();
        if (cbxOperation.getSelectedIndex() > 1) {//0 and 1 are "no algorithm"
            column.setAlgorithm(cbxOperation.getSelectedIndex() - 1);
        } else {
            if (cbxOperation.getSelectedIndex() == 0) {
                column.setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER);
            }
            if (cbxOperation.getSelectedIndex() == 1) {
                column.setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER);//8.5.2014
            }

        }
        switch (column.getColumnDef().getAlgorithm()) {
            case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:

            case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
            case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:
            case ColumnDefOJ.ALGORITHM_CALC_ORIENTATION:
            case ColumnDefOJ.ALGORITHM_CALC_OFFROAD:
                column.getColumnDef().setColumnDigits(2);
                updateOperands(column.getColumnDef(), 2);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
                column.getColumnDef().setColumnDigits(2);
                updateOperands(column.getColumnDef(), 3);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_PATH:
            case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
            case ColumnDefOJ.ALGORITHM_CALC_XPOS:
            case ColumnDefOJ.ALGORITHM_CALC_YPOS:
            case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
            case ColumnDefOJ.ALGORITHM_CALC_AREA:
                column.getColumnDef().setColumnDigits(2);
                updateOperands(column.getColumnDef(), 1);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_COUNT:
            case ColumnDefOJ.ALGORITHM_CALC_SLICE:
            case ColumnDefOJ.ALGORITHM_CALC_EXISTS:
                column.getColumnDef().setColumnDigits(0);
                updateOperands(column.getColumnDef(), 1);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_IMAGE:
            case ColumnDefOJ.ALGORITHM_CALC_INDEX:
            case ColumnDefOJ.ALGORITHM_CALC_ID:
                column.getColumnDef().setColumnDigits(0);
                updateOperands(column.getColumnDef(), 0);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_FILE_NAME:
                updateOperands(column.getColumnDef(), 0);
                break;
            case ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER:
                column.getColumnDef().setColumnDigits(2);
                updateOperands(column.getColumnDef(), 0);
            case ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT:
                column.getColumnDef().setColumnDigits(0);
                updateOperands(column.getColumnDef(), 0);
            case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER:
                column.getColumnDef().setColumnDigits(2);
                updateOperands(column.getColumnDef(), 0);
            case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT:
                column.getColumnDef().setColumnDigits(0);
                updateOperands(column.getColumnDef(), 0);
                break;
            default:
                updateOperandsView(0, false, false);
        }

        if ((column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER)
                || (column.getColumnDef().getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT)) {
            if (!column.getName().startsWith("_")) {
                column.setName("_" + column.getName());
            }
        } else {
            if (column.getName().startsWith("_")) {
                column.setName(column.getName().substring(1));
            }
        }

        ColumnsOJ columns = OJ.getData().getResults().getColumns();  //2.2.2014    
        columns.fixColumnsOrder();
        int index = columns.indexOfColumn(column);
        ((ColumnsTableModel) tblColumns.getModel()).fireTableDataChanged();//my guess//2.2.2014    

        if (column.getColumnDef().getAlgorithm() != old_algorithm) {
            column.init();
        }
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
        tblColumns.getSelectionModel().setSelectionInterval(index, index);
        updateView();
    }
}//GEN-LAST:event_cbxOperationItemStateChanged

private void tfYMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfYMaxActionPerformed
}//GEN-LAST:event_tfYMaxActionPerformed

private void tfXMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfXMinActionPerformed
}//GEN-LAST:event_tfXMinActionPerformed

private void tfXMinKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfXMinKeyReleased
    String txt = tfXMin.getText();
    double xMin;
    try {
        xMin = Double.parseDouble(txt);

    } catch (NumberFormatException e) {
        xMin = Double.NaN;
    }
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        ColumnDefOJ colDef = column.getColumnDef();
        colDef.setHistoXMin(xMin);
    }
}//GEN-LAST:event_tfXMinKeyReleased

private void tfXMaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfXMaxKeyReleased
    String txt = tfXMax.getText();
    double xMax;
    try {
        xMax = Double.parseDouble(txt);

    } catch (NumberFormatException e) {
        xMax = Double.NaN;
    }
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        ColumnDefOJ colDef = column.getColumnDef();
        colDef.setHistoXMax(xMax);
    }
}//GEN-LAST:event_tfXMaxKeyReleased

private void tfBinWidthKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfBinWidthKeyReleased
    String txt = tfBinWidth.getText();
    double binWidth;
    try {
        binWidth = Double.parseDouble(txt);

    } catch (NumberFormatException e) {
        binWidth = Double.NaN;
    }
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        ColumnDefOJ colDef = column.getColumnDef();
        colDef.setHistoBinWidth(binWidth);
    }
}//GEN-LAST:event_tfBinWidthKeyReleased

private void tfYMaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfYMaxKeyReleased
    String txt = tfYMax.getText();
    int yMax = -1;
    try {
        yMax = Integer.parseInt(txt);

    } catch (NumberFormatException e) {
        yMax = -1;
    }
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        ColumnDefOJ colDef = column.getColumnDef();
        colDef.setHistoYMax(yMax);
    }
}//GEN-LAST:event_tfYMaxKeyReleased

private void cbxColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxColorActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cbxColorActionPerformed

private void spinDigitsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinDigitsStateChanged
    ColumnOJ column = getSelectedColumn();
    if (column != null) {
        column.getColumnDef().setColumnDigits(((Integer) spinDigits.getValue()).intValue());
        OJ.getEventProcessor().fireColumnChangedEvent(column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
        updateView();
    }
}//GEN-LAST:event_spinDigitsStateChanged

    private void tblColumnsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblColumnsMouseReleased
        updateView();
    }//GEN-LAST:event_tblColumnsMouseReleased

    private void tabbedColumnDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedColumnDetailMouseClicked
        ProjectSettingsOJ prSettings = ProjectSettingsOJ.getInstance();
        if (prSettings == null) {
            return;
        }
        if (tabbedColumnDetail.getSelectedIndex() == 1) {
            Rectangle rect = prSettings.getBounds();//19.6.2014
            if (rect.height < 500) {
                rect.height = 500;
                prSettings.setBounds(rect);
            }
        }
    }//GEN-LAST:event_tabbedColumnDetailMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnShowHisto;
    private javax.swing.JComboBox cbxColor;
    private javax.swing.JComboBox cbxFirstObjectType;
    private javax.swing.JComboBox cbxOperation;
    private javax.swing.JComboBox cbxSecondObjectType;
    private javax.swing.JComboBox cbxThirdObjectType;
    private javax.swing.JLabel lbBinWidth;
    private javax.swing.JLabel lbColor;
    private javax.swing.JLabel lbColumnAppearance;
    private javax.swing.JLabel lbHistogram;
    private javax.swing.JLabel lbOperation;
    private javax.swing.JLabel lbPrecision;
    private javax.swing.JLabel lbXMax;
    private javax.swing.JLabel lbXMin;
    private javax.swing.JLabel lbYMax;
    private javax.swing.JLabel lblFirstClone;
    private javax.swing.JLabel lblFirstObjectType;
    private javax.swing.JLabel lblFirstPoint;
    private javax.swing.JLabel lblSecondClone;
    private javax.swing.JLabel lblSecondObjectType;
    private javax.swing.JLabel lblSecondPoint;
    private javax.swing.JLabel lblThirdClone;
    private javax.swing.JLabel lblThirdObjectType;
    private javax.swing.JLabel lblThirdPoint;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel pnlFirstOperand;
    private javax.swing.JPanel pnlListOfColumns;
    private javax.swing.JPanel pnlOperands;
    private javax.swing.JPanel pnlOperation;
    private javax.swing.JPanel pnlPresentation;
    private javax.swing.JPanel pnlSecondOperand;
    private javax.swing.JPanel pnlTextMode;
    private javax.swing.JPanel pnlThirdOperand;
    private javax.swing.JPanel pnlTwoButtons;
    private javax.swing.JPanel rightPanelA;
    private javax.swing.JPanel rightPanelB;
    private javax.swing.JScrollPane scrlpaneListOfColumns;
    private javax.swing.JSpinner spinDigits;
    private javax.swing.JTabbedPane tabbedColumnDetail;
    private javax.swing.JTable tblColumns;
    private javax.swing.JTextField tfBinWidth;
    private javax.swing.JTextField tfXMax;
    private javax.swing.JTextField tfXMin;
    private javax.swing.JTextField tfYMax;
    private javax.swing.JTextField txtFirstClone;
    private javax.swing.JTextField txtFirstPoint;
    private javax.swing.JTextField txtSecondClone;
    private javax.swing.JTextField txtSecondPoint;
    private javax.swing.JTextField txtThirdClone;
    private javax.swing.JTextField txtThirdPoint;
    // End of variables declaration//GEN-END:variables

    /**
     * Two classes, ColorModelOJ and ColorRendererOJ to get fancy color icons
     * while selecting column/histogram color
     */
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
                        return new Color(0, 153, 0);
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
    }

    class ColorRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(3, 8, 3, 3);
        private final Icon blackIcon = getColorIcon(Color.BLACK);
        private final Icon blueIcon = getColorIcon(Color.BLUE);
        private final Icon cyanIcon = getColorIcon(Color.CYAN);
        private final Icon darkGrayIcon = getColorIcon(Color.DARK_GRAY);
        private final Icon grayIcon = getColorIcon(Color.GRAY);
        private final Icon greenIcon = getColorIcon(Color.GREEN);
        private final Icon darkGreenIcon = getColorIcon(new Color(0, 153, 0));
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
                        setIcon(darkGreenIcon);
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
            if (color.getRGB() == new Color(0, 153, 0).getRGB()) {
                return "Dark Green";
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

        private Icon getColorIcon(Color color) {
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

    public void close() {
        OJ.getEventProcessor().removeColumnChangedListener(this);
        OJ.getEventProcessor().removeYtemDefChangedListener(this);
    }

    /**
     *
     * called when adding, deleting or changing a column
     */
    public synchronized void columnChanged(ColumnChangedEventOJ evt) {//9.9.2009
        int colCount = OJ.getData().getResults().getColumns().getAllColumnsCount();//10.2.2010
        int selRow = tblColumns.getSelectedRow();
        if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_ADDED) {
            ((ColumnsTableModel) tblColumns.getModel()).fireTableRowsInserted(colCount - 1, colCount - 1);
            tblColumns.getSelectionModel().setSelectionInterval(colCount - 1, colCount - 1);
        } else if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_DELETED) {
            ((ColumnsTableModel) tblColumns.getModel()).fireTableRowsDeleted(selRow, selRow);
        } else {
            ((ColumnsTableModel) tblColumns.getModel()).fireTableRowUpdated(selRow);
        }

        updateView();
    }

    public synchronized void ytemDefChanged(YtemDefChangedEventOJ evt) {//9.9.2009
        fillObjectDefs();
        btnNew.setEnabled(OJ.getData().getYtemDefs().getYtemDefsCount() > 0);
    }

    /**
     * three classes to handle the table of columns in the left panel:
     * ColumnsTableModel, ColumnsHeaderTableRendererOJ and ColumnsTableRenderer
     */
    private class ColumnsTableModel extends AbstractTableModel {

        public String getColumnName(int col) {

            switch (col) {
                case 0:
                    return "Column Title";
            }
            return "";
        }

        public int getRowCount() {
            return OJ.getData().getResults().getColumns().getAllColumnsCount();
        }

        public int getColumnCount() {
            return 1;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            JLabel label = new JLabel();
            if (rowIndex >= OJ.getData().getResults().getColumns().getAllColumnsCount()) {
                label.setText("");
                label.setIcon(null);
                return label;
            }
            ColumnDefOJ columnDef = OJ.getData().getResults().getColumns().getColumnByIndex(rowIndex).getColumnDef();
            switch (columnIndex) {
                case 0:
                    label.setText(columnDef.getName());
                    label.setIcon(null);
                    break;
            }
            return label;
        }

        public boolean isCellEditable(int row, int col) {
            return (row <= OJ.getData().getResults().getColumns().getAllColumnsCount());
        }

        public void fireTableUpdated() {
            fireTableRowsUpdated(0, getRowCount());
        }

        public void fireTableRowUpdated(int index) {
            fireTableRowsUpdated(index, index);
        }
    }

    public void columnAdded(TableColumnModelEvent event) {
        int toIndex = event.getToIndex();
        if (event.getSource() == tblColumns.getColumnModel()) {
            TableColumn tableCol = (TableColumn) tblColumns.getColumnModel().getColumn(toIndex);
            tableCol.setCellRenderer(columnsTableRenderer);
            switch (toIndex) {
                case 0:
                    tableCol.setWidth(100);
                    tableCol.setPreferredWidth(100);
                    tableCol.setCellEditor(new DefaultCellEditor(nameEditor));
                    ((DefaultCellEditor) tableCol.getCellEditor()).setClickCountToStart(2);
                    break;
            }
        }
    }

    class ColumnsHeaderTableRendererOJ extends JLabel implements TableCellRenderer {

        private Font fontArialBold = Font.decode("Arial-BOLD-12");
        private Border headerBorder = new CompoundBorder(new LineBorder(Color.GRAY, 1, false), new EmptyBorder(2, 8, 2, 8));

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setBackground(oj.OJ.headerBackground);//.darker().darker());setBackground(headerBackground.darker().darker());//Linux exception
            setForeground(Color.WHITE);
            setBorder(headerBorder);
            setOpaque(true);
            if (value != null) {
                setText(value.toString());
            } else {
                setText("");
            }
            if (column == 0) {
                setHorizontalAlignment(SwingConstants.LEFT);
            } else {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            setFont(fontArialBold);
            return this;
        }
    }

    public class ColumnsTableRenderer extends JLabel implements TableCellRenderer {

        private boolean isSelected;
        private boolean hasFocus;
        private Font fontArial = Font.decode("Arial-12");
        private Color rowBackground = new Color(236, 241, 244);

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setOpaque(true);
            this.setBorder(new EmptyBorder(1, 8, 1, 8));

            if (isSelected) {
                setForeground(Color.WHITE);
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.BLACK);
                //if (row % 2 == 0) {
                setBackground(Color.WHITE);
            }

            this.isSelected = isSelected;
            this.hasFocus = hasFocus;
            if (value != null) {
                this.setText(((JLabel) value).getText());
            } else if (IJ.debugMode) {
                ij.IJ.showMessage("was Null 86654");
            }
//            Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
//        at oj.settings.ColumnSettingsOJ$ColumnsTableRenderer.getTableCellRendererComponent(ColumnSettingsOJ.java:2179)
            this.setIcon(((JLabel) value).getIcon());
            setHorizontalAlignment(SwingConstants.LEFT);
            switch (column) {
                case 0:
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
            }
            setFont(fontArial);
            return this;
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

    public Dimension getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(Dimension panelSize) {
        this.panelSize = panelSize;
    }

    public void selectColumn(String title) {//9.2.2010

        for (int i = 0; i < tblColumns.getRowCount(); i++) {
            if (OJ.getData().getResults().getColumns().getColumnByIndex(i).getColumnDef().getName().equalsIgnoreCase(title)) {
                tblColumns.getSelectionModel().setSelectionInterval(i, i);
                updateView();
                break;
            }
        }
    }

    public void selectPresentationTab() {
        tabbedColumnDetail.setSelectedIndex(1);
        tabbedColumnDetail.setVisible(true);
    }
}
