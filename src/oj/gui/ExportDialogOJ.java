/*
 * ExportFrameOJ.java
 */
package oj.gui;

import ij.Prefs;
import ij.text.TextPanel;
import java.awt.Panel;
import oj.io.InputOutputOJ;

/**
 * Converts ObjectJ results table into a text table that can be copied and saved.
 */
public class ExportDialogOJ extends javax.swing.JFrame {

    private static ExportDialogOJ instance;

    public ExportDialogOJ() {
        instance = this;
        initComponents();
        initExtraComponents();
    }

    public static ExportDialogOJ getInstance() {
        if (instance == null) {
            instance = new ExportDialogOJ();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        pnlColumns = new javax.swing.JPanel();
        radioAllColumns = new javax.swing.JRadioButton();
        radioSpecifyColumns = new javax.swing.JRadioButton();
        fieldSpecifyColumns = new javax.swing.JTextField();
        cbxIncludeIndex = new javax.swing.JCheckBox();
        cbxIncludeHeaders = new javax.swing.JCheckBox();
        cbxIncludeStatiscics = new javax.swing.JCheckBox();
        fieldStatistics = new javax.swing.JTextField();
        lblSelectedColumns = new javax.swing.JLabel();
        lblQualifiedObjects = new javax.swing.JLabel();
        btnDone = new javax.swing.JButton();
        btnCopy = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export ObjectJ Results");
        setBounds(new java.awt.Rectangle(0, 22, 300, 450));
        setMaximumSize(new java.awt.Dimension(300, 450));
        setMinimumSize(new java.awt.Dimension(300, 450));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        pnlColumns.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        radioAllColumns.setText("All Columns");
        radioAllColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioAllColumnsActionPerformed(evt);
            }
        });

        radioSpecifyColumns.setText("Specify Columns (accepts wildcard):");
        radioSpecifyColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioSpecifyColumnsActionPerformed(evt);
            }
        });

        fieldSpecifyColumns.setText("*");
        fieldSpecifyColumns.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                fieldSpecifyColumnsFocusLost(evt);
            }
        });
        fieldSpecifyColumns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fieldSpecifyColumnsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSpecifyColumnsKeyReleased(evt);
            }
        });

        cbxIncludeIndex.setText("Include Index Column");
        cbxIncludeIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxIncludeIndexActionPerformed(evt);
            }
        });

        cbxIncludeHeaders.setText("Include Headers");
        cbxIncludeHeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxIncludeHeadersActionPerformed(evt);
            }
        });

        cbxIncludeStatiscics.setText("Include Statistics:");
        cbxIncludeStatiscics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxIncludeStatiscicsActionPerformed(evt);
            }
        });

        fieldStatistics.setText("Count, Mean, StDev");
        fieldStatistics.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fieldStatisticsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldStatisticsKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlColumnsLayout = new javax.swing.GroupLayout(pnlColumns);
        pnlColumns.setLayout(pnlColumnsLayout);
        pnlColumnsLayout.setHorizontalGroup(
            pnlColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColumnsLayout.createSequentialGroup()
                .addGroup(pnlColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cbxIncludeIndex, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addComponent(cbxIncludeStatiscics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbxIncludeHeaders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(fieldStatistics)
                    .addComponent(radioAllColumns)
                    .addComponent(fieldSpecifyColumns, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(radioSpecifyColumns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlColumnsLayout.setVerticalGroup(
            pnlColumnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColumnsLayout.createSequentialGroup()
                .addComponent(radioAllColumns)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioSpecifyColumns)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldSpecifyColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxIncludeHeaders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxIncludeIndex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxIncludeStatiscics)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldStatistics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblSelectedColumns.setText("Selected Columns: ");

        lblQualifiedObjects.setText("Qualified Objects: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlColumns, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSelectedColumns, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQualifiedObjects, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(pnlColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(lblSelectedColumns)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQualifiedObjects)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        btnDone.setText("Done");
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });

        btnCopy.setText("Copy");
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });

        btnExport.setText("Export...");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport)
                        .addGap(13, 13, 13)
                        .addComponent(btnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCopy)
                    .addComponent(btnExport)
                    .addComponent(btnDone))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radioAllColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioAllColumnsActionPerformed
        allColumnsFlag = true;
        specifyColumnsFlag = false;
        radioSpecifyColumns.setSelected(specifyColumnsFlag);
        radioAllColumns.setSelected(allColumnsFlag);
        buildTextOutput();
        fieldSpecifyColumns.setEnabled(false);


        //oj.gui.menuactions.ResultsActionsOJ.changePanelContents();
    }//GEN-LAST:event_radioAllColumnsActionPerformed

   private void radioSpecifyColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioSpecifyColumnsActionPerformed
       allColumnsFlag = false;
       specifyColumnsFlag = true;
       radioSpecifyColumns.setSelected(specifyColumnsFlag);
       radioAllColumns.setSelected(allColumnsFlag);
       buildTextOutput();
       fieldSpecifyColumns.setEnabled(true);
    }//GEN-LAST:event_radioSpecifyColumnsActionPerformed

   private void fieldSpecifyColumnsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSpecifyColumnsKeyPressed
       char c = evt.getKeyChar();
       if ((c >= 32) && (c <= 127))//avoid macro invocation
       {
           evt.consume();
       }
    }//GEN-LAST:event_fieldSpecifyColumnsKeyPressed

    private void fieldSpecifyColumnsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fieldSpecifyColumnsFocusLost
        buildTextOutput();
    }//GEN-LAST:event_fieldSpecifyColumnsFocusLost

    private void fieldSpecifyColumnsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSpecifyColumnsKeyReleased
        buildTextOutput();
    }//GEN-LAST:event_fieldSpecifyColumnsKeyReleased

    private void cbxIncludeHeadersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxIncludeHeadersActionPerformed

        headersFlag = !headersFlag;
        cbxIncludeHeaders.setSelected(headersFlag);
        buildTextOutput();
    }//GEN-LAST:event_cbxIncludeHeadersActionPerformed

    private void cbxIncludeIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxIncludeIndexActionPerformed
        indexFlag = !indexFlag;
        cbxIncludeIndex.setSelected(indexFlag);
        if (!indexFlag) {
            specifyStatisticsFlag = false;
            cbxIncludeStatiscics.setSelected(specifyStatisticsFlag);
            fieldStatistics.setEnabled(specifyStatisticsFlag);
        }
        buildTextOutput();

    }//GEN-LAST:event_cbxIncludeIndexActionPerformed

    private void cbxIncludeStatiscicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxIncludeStatiscicsActionPerformed
        specifyStatisticsFlag = !specifyStatisticsFlag;
        cbxIncludeStatiscics.setSelected(specifyStatisticsFlag);
        fieldStatistics.setEnabled(specifyStatisticsFlag);
        if (specifyStatisticsFlag) {
            indexFlag = true;
            cbxIncludeIndex.setSelected(indexFlag);
        }
        buildTextOutput();
    }//GEN-LAST:event_cbxIncludeStatiscicsActionPerformed

    private void fieldStatisticsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldStatisticsKeyReleased
        buildTextOutput();
    }//GEN-LAST:event_fieldStatisticsKeyReleased

    private void fieldStatisticsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldStatisticsKeyPressed
        char c = evt.getKeyChar();
        if ((c >= 32) && (c <= 127))//avoid macro invocation
        {
            evt.consume();
        }
    }//GEN-LAST:event_fieldStatisticsKeyPressed

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        TextPanel myTextPanel = oj.gui.menuactions.ResultsActionsOJ.myTextPanel;
        myTextPanel.scrollToTop();
        myTextPanel.selectAll();
        boolean a = Prefs.copyColumnHeaders;//9.8.2011
        Prefs.copyColumnHeaders = false;//because they are empty here, anyway
        boolean b = Prefs.noRowNumbers;
        Prefs.noRowNumbers = false;
        myTextPanel.copySelection();
        myTextPanel.resetSelection();
        Prefs.copyColumnHeaders = a;
        Prefs.noRowNumbers = b;

    }//GEN-LAST:event_btnCopyActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        oj.gui.menuactions.ResultsActionsOJ.closeTextPreview();
    }//GEN-LAST:event_formWindowClosed

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
        oj.gui.menuactions.ResultsActionsOJ.closeTextPreview();
        super.dispose();
    }//GEN-LAST:event_btnDoneActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        TextPanel myTextPanel = oj.gui.menuactions.ResultsActionsOJ.myTextPanel;
        String txt = myTextPanel.getText();
        int start = 1 + txt.indexOf("\n");
        txt = txt.substring(start);//skip those empty header tabs
        myTextPanel.scrollToTop();
        myTextPanel.resetSelection();
        new InputOutputOJ().saveResultsAsText(txt, "Results.txt");
    }//GEN-LAST:event_btnExportActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnExport;
    private javax.swing.JCheckBox cbxIncludeHeaders;
    private javax.swing.JCheckBox cbxIncludeIndex;
    private javax.swing.JCheckBox cbxIncludeStatiscics;
    private javax.swing.JTextField fieldSpecifyColumns;
    private javax.swing.JTextField fieldStatistics;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblQualifiedObjects;
    private javax.swing.JLabel lblSelectedColumns;
    private javax.swing.JPanel pnlColumns;
    private javax.swing.JRadioButton radioAllColumns;
    private javax.swing.JRadioButton radioSpecifyColumns;
    // End of variables declaration//GEN-END:variables
    private static boolean allColumnsFlag = true;
    private static boolean specifyColumnsFlag = false;
    private static boolean headersFlag = true;
    private static boolean indexFlag = true;
    private static boolean specifyStatisticsFlag = false;

    public void initExtraComponents() {
        cbxIncludeHeaders.setSelected(headersFlag);
        cbxIncludeIndex.setSelected(indexFlag);
        cbxIncludeStatiscics.setSelected(specifyStatisticsFlag);
        radioAllColumns.setSelected(allColumnsFlag);
        // cbxQualifiedOnly.setSelected(qualifiedOnlyFlag);
        fieldStatistics.setEnabled(specifyStatisticsFlag);
        fieldSpecifyColumns.setEnabled(specifyColumnsFlag);
        buildTextOutput();
    }

    public Panel getPanel5() {
        return null;
    }

    public void buildTextOutput() {

        String abc = "";
        if (allColumnsFlag) {
            abc += "All, ";
        }
        if (specifyColumnsFlag) {
            String txt = fieldSpecifyColumns.getText();
            txt = txt.replaceAll(" ", "");
            if (txt.length() > 0) {
                txt = "title=" + txt;
                txt = txt.replaceAll(",", ", title=");
                txt += ", ";
            }
            abc += txt;
        }
        if (headersFlag) {
            abc += "headers, ";
        }

        if (indexFlag) {
            abc += "indices, ";
        }
        if (specifyStatisticsFlag) {
            String txt = fieldStatistics.getText();
            txt = txt.replaceAll(" ", "");
            if (txt.length() > 0) {
                txt = txt.replaceAll(",", ", ");
                txt = txt + ", ";
                abc += txt;
            }
        }

        radioAllColumns.setSelected(allColumnsFlag);
        radioSpecifyColumns.setSelected(specifyColumnsFlag);
        ij.IJ.showStatus(abc);
        String[] pair = oj.gui.menuactions.ResultsActionsOJ.extractResults(abc);
        lblSelectedColumns.setText(pair[0]);
        lblQualifiedObjects.setText(pair[1]);
    }
}
