/*
 * AboutOJ.java
 * -- documented
 */
package oj.gui;

import ij.plugin.BrowserLauncher;
import java.awt.Color;
import java.io.IOException;
import oj.OJ;
import oj.plugin.ImageJUpdaterOJ;
import oj.project.CellsOJ;
import oj.project.DataOJ;

/**
 *
 * For displaying the ObjectJ Aboutbox
 */
public class AboutOJ extends javax.swing.JDialog {

    public AboutOJ(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        DataOJ data = OJ.getData();
        textAreaAbout.setTabSize(12);

        String thisVersion = OJ.releaseVersion;
        String aboutText = "";
        aboutText = aboutText + "ObjectJ plugin:";
        aboutText = aboutText + "\n   Version:\t" + thisVersion;
        aboutText = aboutText + "\n   Build:\t" + OJ.build;
        aboutText = aboutText + "\n   Date:\t" + OJ.buildDate;
        if (data == null) {
            aboutText = aboutText + "\n\nFile:\tNo project is loaded";

        } else {
            aboutText = aboutText + "\n\nProject file:";
            aboutText = aboutText + "\n   File:\t" + data.getFilename();

            aboutText = aboutText + "\n   Linked Images:\t" + data.getImages().getImagesCount();
            CellsOJ cells = data.getCells();
            int ptCount = 0;
            for (int cell = 0; cell < cells.getCellsCount(); cell++) {
                ptCount += cells.getCellByIndex(cell).getTotalPointsCount();
            }
            int mCount = 0;
            if (data.macroSet != null) {
                mCount = data.macroSet.getMacrosCount();
            }
            aboutText = aboutText + "\n   Objects:\t" + data.getCells().getCellsCount();
            aboutText = aboutText + "\n   Points:\t" + ptCount;
            aboutText = aboutText + "\n   Macros:\t" + mCount;

        }
        textAreaAbout.setText(aboutText);
        textAreaAbout.setEditable(false);
        textAreaAbout.revalidate();
    }

  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textAreaAbout = new javax.swing.JTextArea();
    jScrollPane2 = new javax.swing.JScrollPane();
    textAreaUpdateAnswer = new javax.swing.JTextArea();
    jPanel2 = new javax.swing.JPanel();
    buttOk = new javax.swing.JButton();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setModal(true);
    setResizable(false);

    jPanel1.setBackground(new java.awt.Color(255, 255, 255));
    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/About-ObjectJ.png")));

    textAreaAbout.setColumns(20);
    textAreaAbout.setRows(5);
    textAreaAbout.setText("Version:\t\t0.98i6\nDate:\t\t15-sep-2010   15:36\n\nproject file:\t\tFilaments-98.ojj\n#linked images:\t3\n#objects\t\t22\n#points\t\t8765\n#macros\t\t12");
    jScrollPane1.setViewportView(textAreaAbout);

    jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

    textAreaUpdateAnswer.setColumns(20);
    textAreaUpdateAnswer.setRows(5);
    textAreaUpdateAnswer.setAutoscrolls(false);
    textAreaUpdateAnswer.setRequestFocusEnabled(false);
    jScrollPane2.setViewportView(textAreaUpdateAnswer);

    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jPanel1Layout.createSequentialGroup()
        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 558, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jPanel1Layout.createSequentialGroup()
        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        .add(18, 18, 18)
        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 20, 4, 20));
    jPanel2.setMinimumSize(new java.awt.Dimension(75, 40));
    jPanel2.setPreferredSize(new java.awt.Dimension(100, 40));

    buttOk.setText("OK");
    buttOk.setFocusPainted(false);
    buttOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttOkActionPerformed(evt);
      }
    });

    jButton1.setText("Check for Update");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    jButton2.setText("ObjectJ Website");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jPanel2Layout.createSequentialGroup()
        .add(jButton2)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jButton1)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
        .add(buttOk, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jPanel2Layout.createSequentialGroup()
        .add(1, 1, 1)
        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jButton1)
          .add(buttOk)
          .add(jButton2)))
    );

    getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width-474)/2, (screenSize.height-571)/2, 474, 571);
  }// </editor-fold>//GEN-END:initComponents

    private void buttOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttOkActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttOkActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String msg;

        String thisVersion = OJ.releaseVersion;
        String[] notes = new ImageJUpdaterOJ().openUrlAsList(OJ.URL + "/notes.txt");
        if (notes.length < 2) {
            msg = "Could not connect to:   " + OJ.URL;
        } else {
            String newestVersion = notes[notes.length - 2].substring(8);
            msg = "Newest ObjectJ version is:  " + newestVersion;
            String msg2 = "\nYou can upgrade both ObjectJ and ImageJ \nvia menu Help>Update ImageJ.";
            String msg3 = "\nYour ObjectJ is up-to-date.";
            if (newestVersion.compareToIgnoreCase(thisVersion) > 0) {
                textAreaUpdateAnswer.setForeground(Color.red);
                msg = msg + msg2;
            } else {
                textAreaUpdateAnswer.setForeground(new Color(0, 122, 0));
                msg = msg + msg3;
            }
            //ij.IJ.showMessage(msg);
        }
        textAreaUpdateAnswer.setText(msg);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            BrowserLauncher.openURL(OJ.URL);
        } catch (IOException e) {
        }
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttOk;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTextArea textAreaAbout;
  private javax.swing.JTextArea textAreaUpdateAnswer;
  // End of variables declaration//GEN-END:variables
}
