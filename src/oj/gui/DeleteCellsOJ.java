/**
 * DeleteCellsDialogOJ.java
 * fully documented 22.5.2010
 *
 * dialog for conditionally deleting cells
 */
package oj.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.UIManager;
import oj.OJ;

public class DeleteCellsOJ extends javax.swing.JDialog {

    public static int CLOSE_DELETE = 1;
    public static int CLOSE_CANCEL = 2;
    public static final int DELETE_RANGE = 1;
    public static final int KEEP_RANGE = 2;
    public static final int DELETE_IMAGE = 3;
    public static final int DELETE_SLICE = 4;
    public static final int DELETE_ROI = 5;
    public static final int DELETE_QUALIFIED = 6;
    public static final int DELETE_UNQUALIFIED = 7;
    private boolean error;
    private int closeStatus;
    private int deleteStatus;
    private int maxCount;
    private int minValue;
    private int maxValue;
    private int roiCount;
    private int imageCount;
    private int sliceCount;
    private int qualifiedCount;
    private int unqualifiedCount;

    /** Creates new form DeleteCellsDialogOJ */
    public DeleteCellsOJ(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initExtCmponents();
        closeStatus = DeleteCellsOJ.CLOSE_CANCEL;
        error = false;
    }

    public int getMinValue() {
        return minValue - 1;
    }

    public int getMaxValue() {
        return maxValue - 1;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        minValue = maxCount;
        maxValue = maxCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public void setSliceCount(int sliceCount) {
        this.sliceCount = sliceCount;
    }

    public void setROICount(int roiCount) {
        this.roiCount = roiCount;
    }

    public void setQualifiedCount(int qualifiedCount) {
        this.qualifiedCount = qualifiedCount;
    }

    public void setUnqualifiedCount(int unqualifiedCount) {
        this.unqualifiedCount = unqualifiedCount;
    }

    public int getCloseStatus() {
        return closeStatus;
    }

    public int getDeleteStatus() {
        return deleteStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpDeleteCells = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        radDeleteRange = new javax.swing.JRadioButton();
        radKeepRange = new javax.swing.JRadioButton();
        radDeleteUnqualified = new javax.swing.JRadioButton();
        radDeleteQualified = new javax.swing.JRadioButton();
        radDeleteInImage = new javax.swing.JRadioButton();
        radDeleteInSlice = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblMin = new javax.swing.JLabel();
        lblMax = new javax.swing.JLabel();
        numMin = new javax.swing.JTextField();
        numMax = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();

        UIManager.put("TextField.inactiveBackground", jPanel1.getBackground());
        descText = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();

        setTitle("Delete Objects");
        setModal(true);
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 10));
        jPanel2.setLayout(new java.awt.GridLayout(6, 1));

        grpDeleteCells.add(radDeleteRange);
        radDeleteRange.setSelected(true);
        radDeleteRange.setText("Delete Object Range:");
        radDeleteRange.setContentAreaFilled(false);
        radDeleteRange.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        radDeleteRange.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radDeleteRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radDeleteRangeActionPerformed(evt);
            }
        });
        jPanel2.add(radDeleteRange);

        grpDeleteCells.add(radKeepRange);
        radKeepRange.setText("Keep Range, delete others");
        radKeepRange.setMargin(new java.awt.Insets(0, 0, 10, 0));
        radKeepRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radKeepRangeActionPerformed(evt);
            }
        });
        jPanel2.add(radKeepRange);

        grpDeleteCells.add(radDeleteUnqualified);
        radDeleteUnqualified.setText("Delete Unqualified Objects");
        radDeleteUnqualified.setMargin(new java.awt.Insets(10, 0, 0, 0));
        radDeleteUnqualified.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radDeleteUnqualifiedActionPerformed(evt);
            }
        });
        jPanel2.add(radDeleteUnqualified);

        grpDeleteCells.add(radDeleteQualified);
        radDeleteQualified.setText("Delete Qualified Objects");
        radDeleteQualified.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radDeleteQualified.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radDeleteQualifiedActionPerformed(evt);
            }
        });
        jPanel2.add(radDeleteQualified);

        grpDeleteCells.add(radDeleteInImage);
        radDeleteInImage.setText("Delete Objects in current image");
        radDeleteInImage.setMargin(new java.awt.Insets(10, 0, 0, 0));
        radDeleteInImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radDeleteInImageActionPerformed(evt);
            }
        });
        jPanel2.add(radDeleteInImage);

        grpDeleteCells.add(radDeleteInSlice);
        radDeleteInSlice.setText("Delete Objects in current slice");
        radDeleteInSlice.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radDeleteInSlice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radDeleteInSliceActionPerformed(evt);
            }
        });
        jPanel2.add(radDeleteInSlice);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 1, 1, 1));
        jPanel4.setLayout(new java.awt.GridLayout(2, 2));

        lblMin.setText("From:");
        jPanel4.add(lblMin);

        lblMax.setText("To:");
        jPanel4.add(lblMax);

        numMin.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        numMin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                numMinKeyReleased(evt);
            }
        });
        jPanel4.add(numMin);

        numMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        numMax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                numMaxKeyReleased(evt);
            }
        });
        jPanel4.add(numMax);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBackground(new java.awt.Color(238, 238, 238));
        jScrollPane1.setEnabled(false);
        jScrollPane1.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 78));

        descText.setBackground(new java.awt.Color(238, 238, 238));
        descText.setColumns(20);
        descText.setEditable(false);
        descText.setFont(new java.awt.Font("Lucida Grande", 0, 11));
        descText.setLineWrap(true);
        descText.setRows(3);
        descText.setWrapStyleWord(true);
        descText.setFocusable(false);
        descText.setMargin(new java.awt.Insets(5, 5, 5, 5));
        descText.setMaximumSize(new java.awt.Dimension(250, 78));
        descText.setMinimumSize(new java.awt.Dimension(250, 78));
        descText.setOpaque(false);
        jScrollPane1.setViewportView(descText);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel3, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel6.add(jButtonCancel);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jPanel6.add(jButtonDelete);

        jPanel5.add(jPanel6, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel5, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-469)/2, (screenSize.height-280)/2, 469, 280);
    }// </editor-fold>//GEN-END:initComponents

    private void initExtCmponents() {
        if (OJ.getImageProcessor().getCurrentImageIndex() >= 0) {
            setImageCount(OJ.getDataProcessor().getImageCellsCount());
            setSliceCount(OJ.getDataProcessor().getSliceCellsCount());
            radDeleteInImage.setEnabled(true);
            radDeleteInSlice.setEnabled(true);
        } else {
            radDeleteInImage.setEnabled(false);
            radDeleteInSlice.setEnabled(false);
        }
        setMaxCount(OJ.getData().getCells().getCellsCount());
        setROICount(OJ.getDataProcessor().getROICellsCount());
        setQualifiedCount(OJ.getData().getCells().getQualifiedCellsCount());
        setUnqualifiedCount(OJ.getData().getCells().getUnqualifiedCellsCount());
    }

    private void numMaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numMaxKeyReleased
        if ((evt.getSource() == numMin) && (!((evt.getKeyChar() == KeyEvent.VK_0) || (evt.getKeyChar() == KeyEvent.VK_1) || (evt.getKeyChar() == KeyEvent.VK_2) || (evt.getKeyChar() == KeyEvent.VK_3) || (evt.getKeyChar() == KeyEvent.VK_4) || (evt.getKeyChar() == KeyEvent.VK_5) || (evt.getKeyChar() == KeyEvent.VK_6) || (evt.getKeyChar() == KeyEvent.VK_7) || (evt.getKeyChar() == KeyEvent.VK_8) || (evt.getKeyChar() == KeyEvent.VK_9) || (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)))) {
            evt.consume();
        }
        if (numMax.getText().equals("")) {
            numMax.setText("1");
        }
        maxValue = Integer.parseInt(numMax.getText());
        error = (minValue > maxValue) || (maxValue > maxCount) || (maxValue == 0);
        descText.setText(deleteMessage(minValue, maxValue, maxCount));
    }//GEN-LAST:event_numMaxKeyReleased

    private void numMinKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numMinKeyReleased
        if ((evt.getSource() == numMin) && (!((evt.getKeyChar() == KeyEvent.VK_0) || (evt.getKeyChar() == KeyEvent.VK_1) || (evt.getKeyChar() == KeyEvent.VK_2) || (evt.getKeyChar() == KeyEvent.VK_3) || (evt.getKeyChar() == KeyEvent.VK_4) || (evt.getKeyChar() == KeyEvent.VK_5) || (evt.getKeyChar() == KeyEvent.VK_6) || (evt.getKeyChar() == KeyEvent.VK_7) || (evt.getKeyChar() == KeyEvent.VK_8) || (evt.getKeyChar() == KeyEvent.VK_9) || (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)))) {
            evt.consume();
        }
        if (numMin.getText().equals("")) {
            numMin.setText("1");
        }
        minValue = Integer.parseInt(numMin.getText());
        error = (minValue > maxValue) || (maxValue > maxCount) || (minValue <= 0);
        descText.setText(deleteMessage(minValue, maxValue, maxCount));
    }//GEN-LAST:event_numMinKeyReleased

    private void radDeleteUnqualifiedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radDeleteUnqualifiedActionPerformed
        setMinMaxVisible(false);
        deleteStatus = DELETE_UNQUALIFIED;
        error = false;
        descText.setText(deleteMessage(unqualifiedCount, maxCount));
    }//GEN-LAST:event_radDeleteUnqualifiedActionPerformed

    private void radDeleteQualifiedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radDeleteQualifiedActionPerformed
        setMinMaxVisible(false);
        deleteStatus = DELETE_QUALIFIED;
        error = false;
        descText.setText(deleteMessage(qualifiedCount, maxCount));
    }//GEN-LAST:event_radDeleteQualifiedActionPerformed

    private void radDeleteInSliceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radDeleteInSliceActionPerformed
        setMinMaxVisible(false);
        deleteStatus = DELETE_SLICE;
        error = false;
        descText.setText(deleteMessage(sliceCount, maxCount));
    }//GEN-LAST:event_radDeleteInSliceActionPerformed

    private void radKeepRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radKeepRangeActionPerformed
        deleteStatus = KEEP_RANGE;
        setMinMaxVisible(true);
        numMin.setText(Integer.toString(minValue));
        numMax.setText(Integer.toString(maxValue));
        error = (minValue > maxValue) || (maxValue > maxCount);
        descText.setText(deleteMessage(minValue, maxValue, maxCount));
    }//GEN-LAST:event_radKeepRangeActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        deleteStatus = DELETE_RANGE;
        radDeleteRange.setSelected(true);
        setMinMaxVisible(true);
        numMin.setText(Integer.toString(minValue));
        numMax.setText(Integer.toString(maxValue));
        descText.setText(deleteMessage(1, maxCount));
    }//GEN-LAST:event_formComponentShown

    private void radDeleteRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radDeleteRangeActionPerformed
        deleteStatus = DELETE_RANGE;
        setMinMaxVisible(true);
        numMin.setText(Integer.toString(minValue));
        numMax.setText(Integer.toString(maxValue));
        error = (minValue > maxValue) || (maxValue > maxCount);
        descText.setText(deleteMessage(minValue, maxValue, maxCount));
    }//GEN-LAST:event_radDeleteRangeActionPerformed

    private void radDeleteInImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radDeleteInImageActionPerformed
        setMinMaxVisible(false);
        deleteStatus = DELETE_IMAGE;
        error = false;
        descText.setText(deleteMessage(imageCount, maxCount));
    }//GEN-LAST:event_radDeleteInImageActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        this.setVisible(false);
        if (error) {
            closeStatus = DeleteCellsOJ.CLOSE_CANCEL;
        } else {
            closeStatus = DeleteCellsOJ.CLOSE_DELETE;
        }//GEN-LAST:event_jButtonDeleteActionPerformed
    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        closeStatus = DeleteCellsOJ.CLOSE_CANCEL;
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void setMinMaxVisible(boolean visible) {
        numMin.setText("");
        numMax.setText("");
        numMin.setVisible(visible);
        numMax.setVisible(visible);
        lblMin.setVisible(visible);
        lblMax.setVisible(visible);
    }

    private String deleteMessage(int nrCells, int maxCells) {
        if (nrCells != 1) {
            return Integer.toString(nrCells) + " objects (of " + Integer.toString(maxCells) + ") will be deleted";
        } else {
            return "1 object (of " + Integer.toString(maxCells) + ") will be deleted";
        }
    }

    private String deleteMessage(int minValue, int maxValue, int maxCount) {
        if (error) {
            descText.setForeground(Color.RED);
            if (maxValue < minValue) {
                return "ERROR: Max value is lower than min value";
            } else if (maxValue > maxCount) {
                return "ERROR: Max value is bigger than max count (" + Integer.toString(maxCount) + ")";
            } else if (minValue <= 0) {
                return "ERROR: Min value should be higher than 0";
            } else if (maxValue <= 0) {
                return "ERROR: Max value should be higher than 0";
            } else {
                return "ERROR:";
            }
        } else {
            descText.setForeground(Color.BLACK);
            if (radDeleteRange.isSelected()) {
                return deleteMessage(maxValue - minValue + 1, maxCount);
            } else {
                return deleteMessage(maxCount - maxValue + minValue - 1, maxCount);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descText;
    private javax.swing.ButtonGroup grpDeleteCells;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMax;
    private javax.swing.JLabel lblMin;
    private javax.swing.JTextField numMax;
    private javax.swing.JTextField numMin;
    private javax.swing.JRadioButton radDeleteInImage;
    private javax.swing.JRadioButton radDeleteInSlice;
    private javax.swing.JRadioButton radDeleteQualified;
    private javax.swing.JRadioButton radDeleteRange;
    private javax.swing.JRadioButton radDeleteUnqualified;
    private javax.swing.JRadioButton radKeepRange;
    // End of variables declaration//GEN-END:variables
}
