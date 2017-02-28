//Data explanation:
//plotMacroSet: saved textblock containing all macros with 'macro separators'
//plotMacrosList: an ArrayList containing individual multiPlot macros
//plotMacro: single macro (including macro title) for creating a plot
//multiPlot: Plot containig several singlePlots in different colors
//singlePlot: contains properties for one array in specific color
//plotTitle:
package oj.gui.settings;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PlotWindow;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import oj.OJ;
import oj.macros.EmbeddedMacrosOJ;

/**
 * The fifth ObjectJ panel including table with plot titles and some buttons
 */
public class PlotManagerOJ extends javax.swing.JPanel implements IControlPanelOJ {

    public static boolean withPlots = false;//enable this when it works
    private static PlotManagerOJ instance;

    Dimension panelSize = new Dimension(580, 580);
    public static String macroSeparator = "\n//<macro-separator>\n";
    public static String separatorRegex = "\n\\/\\/<macro-separator>\n";
    private ArrayList<String> plotMacrosList = new ArrayList<String>();
    boolean running = true;
    long mouseDownTime = 0;

    public PlotManagerOJ() {

	textToList();
	initComponents();
	updatePlotTable();
	instance = this;
    }

    public static PlotManagerOJ getInstance() {
	return instance;
    }

    /**
     * Returns plotMacro belonging to that title.
     */
    private String getPlotMacro(String plotTitle) {
	String[] titles = getPlotTitles();
	String plotMacro = "";
	for (int jj = 0; jj < titles.length; jj++) {
	    if (titles[jj].equalsIgnoreCase(plotTitle)) {
		plotMacro = plotMacrosList.get(jj);
	    }
	}
	return plotMacro;
    }

    /**
     * Converts plotMacroSet to an arrayList of plotMacros.
     */
    private void textToList() {
	plotMacrosList = new <String>ArrayList();
	String linkedPlotText = OJ.getData().getLinkedPlotText();
	if (linkedPlotText == null) {
	    return;
	}
	String[] macros = linkedPlotText.split(separatorRegex);
	for (String s : macros) {
	    plotMacrosList.add(s);
	}
    }

    /**
     * Composes all macros with macroseparators to single text.
     */
    void listToText() {
	String allMacros = "";
	if (plotMacrosList != null) {
	    for (int jj = 0; jj < plotMacrosList.size(); jj++) {
		if (jj > 0) {
		    allMacros += macroSeparator;
		}
		allMacros += plotMacrosList.get(jj);
	    }
	}
	OJ.getData().setLinkedPlotText(allMacros);
    }

    /**
     * Use macroList to extract titles and show these.
     */
    private void updatePlotTable() {
	DefaultTableModel model = (DefaultTableModel) tbl_PlotTitles.getModel();
	model.setColumnCount(0);
	String[] plotTitles = getPlotTitles();
	model.addColumn("--- Plot Titles ---", plotTitles);
    }

    /**
     * Extracts the first quote of the first occurrence of keyword in text.
     * Example: if text contains Colors = split("red green"), and varName =
     * "Colors" then "red green" is returned
     */
    public static String extractQuote(String text, String varName) {
	int firstKeyPos = text.indexOf(varName);
	int firstQuote = text.indexOf('"', firstKeyPos);
	int secondQuote = text.indexOf('"', firstQuote + 1);
	String quoted = text.substring(firstQuote + 1, secondQuote);
	return quoted;
    }

    /**
     * Replaces the first quote after the first occurrence of keyword in text.
     */
    public static String replaceQuote(String text, String varName, String replacement) {
	//int subBegin = text.indexOf("Substitute_begin");
	int subEnd = text.indexOf("dialogInterface");
	int firstKeyPos = text.indexOf(varName);
	int firstQuote = text.indexOf('"', firstKeyPos);
	int secondQuote = text.indexOf('"', firstQuote + 1);
	if (firstKeyPos == -1 || firstQuote == -1 || secondQuote == -1 || secondQuote > subEnd) {
	    return text;
	}
	String s1 = text.substring(0, firstQuote + 1);
	String s2 = replacement;
	String s3 = text.substring(secondQuote, text.length());
	String replaced = s1 + s2 + s3;
	return replaced;
    }

    /**
     * Extracts macro title of each macro and returns array of titles.
     */
    public String[] getPlotTitles() {
	int count = plotMacrosList.size();
	String macro;
	for (int jj = count - 1; jj >= 0; jj--) {//clean nulls
	    macro = plotMacrosList.get(jj);
	    if (macro == null) {
		plotMacrosList.remove(jj);
	    }
	}
	count = plotMacrosList.size();
	String[] titles = new String[count];
	for (int jj = 0; jj < count; jj++) {
	    macro = plotMacrosList.get(jj);
	    String title = extractQuote(macro, "macro");
	    titles[jj] = title;
	}
	return titles;
    }

    public Dimension getPanelSize() {
	return panelSize;
    }

    public void setPanelSize(Dimension panelSize) {
	this.panelSize = panelSize;
    }

    public void close() {
	running = false;
    }

    public boolean setCurrentPlotMacro(String txt) {
	String plotTitle = extractQuote(txt, "plotTitle");
	String newPlotTitle = makeUnique(plotTitle);
	String macroName = extractQuote(txt, "macro");
	if (!plotTitle.equals(macroName)) {
	    if (IJ.showMessageWithCancel("abc", "Changing Macro name to " + newPlotTitle)) {
		txt = replaceQuote(txt, "macro", newPlotTitle);
	    }
	}
	String[] plotTitles = getPlotTitles();
	for (int jj = 0; jj < plotTitles.length; jj++) {
	    if (plotTitles[jj].equalsIgnoreCase(plotTitle)) {
		plotMacrosList.set(jj, txt);
		listToText();
		return true;

	    }

	}
	return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlesPane = new javax.swing.JScrollPane();
        tbl_PlotTitles = new javax.swing.JTable();
        btn_EditCode = new javax.swing.JButton();
        btn_ShowPlot = new javax.swing.JButton();
        btn_NewPlot = new javax.swing.JButton();
        btn_CloseAllPlots = new javax.swing.JButton();
        btn_ModifyPlot = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        tbl_PlotTitles.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        tbl_PlotTitles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Plot Ttitles"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_PlotTitles.setDragEnabled(true);
        tbl_PlotTitles.setShowGrid(true);
        tbl_PlotTitles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_PlotTitlesMouseClicked(evt);
            }
        });
        tbl_PlotTitles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbl_PlotTitlesKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_PlotTitlesKeyPressed(evt);
            }
        });
        titlesPane.setViewportView(tbl_PlotTitles);

        btn_EditCode.setText("Modify Code");
        btn_EditCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EditCodeActionPerformed(evt);
            }
        });

        btn_ShowPlot.setText("Refresh");
        btn_ShowPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ShowPlotActionPerformed(evt);
            }
        });

        btn_NewPlot.setText("New Plot");
        btn_NewPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NewPlotActionPerformed(evt);
            }
        });

        btn_CloseAllPlots.setText("Close All Plots");
        btn_CloseAllPlots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CloseAllPlotsActionPerformed(evt);
            }
        });

        btn_ModifyPlot.setText("Modify via Dialog");
        btn_ModifyPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModifyPlotActionPerformed(evt);
            }
        });

        jLabel1.setText(" Save via menu \"ObjectJ>Save Project\" ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 408, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(13, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(btn_NewPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(9, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, btn_ShowPlot, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, btn_CloseAllPlots, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, btn_ModifyPlot, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, btn_EditCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(0, 0, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btn_NewPlot)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 77, Short.MAX_VALUE)
                        .add(btn_ModifyPlot)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btn_EditCode)
                        .add(57, 57, 57)
                        .add(btn_CloseAllPlots)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btn_ShowPlot))
                    .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .add(8, 8, 8))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void tbl_PlotTitlesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_PlotTitlesMouseClicked
	boolean ctrl = evt.isControlDown();
	//long 
	boolean right = SwingUtilities.isRightMouseButton(evt) || IJ.isMacOSX() && ctrl;
	if (right) {
	    IJ.showMessage("rightButton");
	} else {
	    int selected[] = tbl_PlotTitles.getSelectedRows();
	    long difference = System.currentTimeMillis() - mouseDownTime;
	    mouseDownTime = System.currentTimeMillis();
	    boolean doubleClick = (difference <= 250);
	    if (doubleClick) {

		int selectedRowIndex = tbl_PlotTitles.getSelectedRow();

		if (selectedRowIndex >= 0) {
		    String plotTitle = (String) tbl_PlotTitles.getModel().getValueAt(selectedRowIndex, 0);
		    String macro = getPlotMacro(plotTitle);
		    IJ.runMacro(macro);
		}
	    }
	}
    }//GEN-LAST:event_tbl_PlotTitlesMouseClicked

    /**
     * Shows macro for selected plot in editor window.
     */
    private void btn_EditCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EditCodeActionPerformed

	int selectedRowIndex = tbl_PlotTitles.getSelectedRow();
	if (tbl_PlotTitles.getSelectedRowCount() != 1) {
	    IJ.showMessage("Exactly one plot title must be selected.");
	    return;
	}
	int selectedColumnIndex = 0;
	if (selectedRowIndex >= 0) {
	    String plotTitle = (String) tbl_PlotTitles.getModel().getValueAt(selectedRowIndex, selectedColumnIndex);
	    String plotMacro = getPlotMacro(plotTitle);
	    EmbeddedMacrosOJ embedded = EmbeddedMacrosOJ.getInstance();
	    embedded.showEmbeddedPlotMacros(plotMacro);

	}
    }//GEN-LAST:event_btn_EditCodeActionPerformed

    /**
     * Executes the macros that are selected in the table of Plot Titles.
     */
    private void btn_ShowPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ShowPlotActionPerformed
	//int selectedRowIndex = tblPlotTitles.getSelectedRow();
	int[] selectedRows = tbl_PlotTitles.getSelectedRows();
	for (int jj = 0; jj < selectedRows.length; jj++) {
	    String plotTitle = (String) tbl_PlotTitles.getModel().getValueAt(selectedRows[jj], 0);
	    String plotMacro = getPlotMacro(plotTitle);
	    IJ.runMacro(plotMacro);
	}
    }//GEN-LAST:event_btn_ShowPlotActionPerformed

    private void tbl_PlotTitlesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_PlotTitlesKeyTyped

    }//GEN-LAST:event_tbl_PlotTitlesKeyTyped

    private void tbl_PlotTitlesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_PlotTitlesKeyPressed
	if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	    int[] selected = tbl_PlotTitles.getSelectedRows();
	    for (int jj = selected.length - 1; jj >= 0; jj--) {
		int row = selected[jj];
		if (plotMacrosList.size() > row) {
		    plotMacrosList.remove(row);
		}
	    }
	    updatePlotTable();
	    OJ.getData().setLinkedPlotText("");
	}
    }//GEN-LAST:event_tbl_PlotTitlesKeyPressed
    String makeUnique(String oldTitle) {
	String newTitle = oldTitle;
	String baseTitle = oldTitle;

	try {
	    int dashPos = oldTitle.lastIndexOf("-");
	    if (Integer.parseInt(oldTitle.substring(dashPos + 1)) > 0) {
		baseTitle = oldTitle.substring(0, dashPos);
	    }
	} catch (Exception e) {
	}
	String[] candidates = new String[100];
	for (int suffix = 2; suffix < 100; suffix++) {
	    candidates[suffix] = baseTitle + "-" + suffix;
	}
	int trial = 2;
	String[] titles = getPlotTitles();
	for (int suffix = 2; suffix < 100 && trial < 100; suffix++) {
	    for (int jj = 0; jj < titles.length; jj++) {//guarantee unique plotTitle
		if (newTitle.equalsIgnoreCase(titles[jj])) {
		    newTitle = candidates[trial];
		    suffix = 2;// start again
		    trial++;
		    break;
		}
	    }
	}
	return newTitle;
    }

    /**
     * Calls the plot dialog for creating a new multiplot-macro. The plotTitle
     * is made unique if necessary. If successful, a new multiplot-macro is
     * added to macroList and plot table is updated. Also the macroset (text
     * containing all plotmacros with separators) is updated
     */
    private void createNew(String what) {
	IJ.runMacro("close('Preview*');");
	ImagePlus imp = IJ.createImage("Preview2", 600, 400, 1, 24);
	imp.getProcessor().set(0xffffff);
	imp.show();
	PlotDialogOJ plotDlg = new PlotDialogOJ();
	String plotMacro = plotDlg.composePlotMacro(what, "");//***** C a l l   D i a l o g  ***** 
	if (plotMacro == null) {
	    return;
	}
	String thisTitle = extractQuote(plotMacro, "plotTitle");
	thisTitle = makeUnique(thisTitle);
	plotMacro = replaceQuote(plotMacro, "macro", thisTitle);
	plotMacro = replaceQuote(plotMacro, "plotTitle", thisTitle);
	plotMacrosList.add(plotMacro);
	updatePlotTable();
	listToText();

	int nRows = tbl_PlotTitles.getRowCount();
	if (nRows > 0) {
	    tbl_PlotTitles.setRowSelectionInterval(nRows - 1, nRows - 1);

	    String plotTitle = (String) tbl_PlotTitles.getModel().getValueAt(nRows - 1, 0);
	    plotMacro = getPlotMacro(plotTitle);
	    IJ.runMacro(plotMacro);

	}
    }

    private void btn_NewPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NewPlotActionPerformed
	createNew("scatter");
    }//GEN-LAST:event_btn_NewPlotActionPerformed
    /**
     * Closes all windows of type PlotWindow
     */
    private void btn_CloseAllPlotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CloseAllPlotsActionPerformed
	int nImages = WindowManager.getImageCount();
	for (int jj = nImages; jj > 0; jj--) {
	    ImagePlus imp = WindowManager.getImage(jj);
	    if (imp.getWindow() instanceof PlotWindow) {
		imp.changes = false;
		imp.close();
	    }
	}
    }//GEN-LAST:event_btn_CloseAllPlotsActionPerformed

    private void btn_ModifyPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModifyPlotActionPerformed
//		IJ.runMacro("close('Preview*');");
//		ImagePlus imp = IJ.createImage("Preview2", 600, 400, 1, 24);
//		imp.getProcessor().set(0xffffff);
//		imp.show();

	PlotDialogOJ plotDlg = new PlotDialogOJ();

	int selectedRowIndex = tbl_PlotTitles.getSelectedRow();
	if (tbl_PlotTitles.getSelectedRowCount() != 1) {
	    IJ.showMessage("Exactly one plot title must be selected.");
	    return;
	}
	int selectedColumnIndex = 0;
	if (selectedRowIndex >= 0) {
	    String plotTitle = (String) tbl_PlotTitles.getModel().getValueAt(selectedRowIndex, selectedColumnIndex);
	    IJ.runMacro("close('" + plotTitle + "');");
	    String plotMacro = getPlotMacro(plotTitle);
	    if (!extractQuote(plotMacro, "dialogInterface").equals("true")) {
		IJ.showMessage("Plot can only be modified via 'Edit Code'");
	    } else {

		IJ.runMacro("close('Preview*');");
		ImagePlus imp = IJ.createImage("Preview2", 600, 400, 1, 24);
		imp.getProcessor().set(0xffffff);
		imp.show();

		String changedPlotMacro = plotDlg.composePlotMacro("Plot", plotMacro);
		if (changedPlotMacro != null) {
		    String newTitle = extractQuote(changedPlotMacro, "plotTitle");
		    //replaceQuote(plotMacro, "macro", newTitle);
		    if (!newTitle.equalsIgnoreCase(plotTitle)) {

			newTitle = makeUnique(newTitle);
			changedPlotMacro = replaceQuote(changedPlotMacro, "macro", newTitle);
			changedPlotMacro = replaceQuote(changedPlotMacro, "plotTitle", newTitle);
			//plotMacrosList.add(changedPlotMacro);
		    }
		    plotMacrosList.set(selectedRowIndex, changedPlotMacro);
		    listToText();
		    tbl_PlotTitles.getModel().setValueAt(newTitle, selectedRowIndex, selectedColumnIndex);
		    IJ.runMacro(changedPlotMacro);
		}
	    }
	}
	IJ.runMacro("close('Preview*');");
    }//GEN-LAST:event_btn_ModifyPlotActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_CloseAllPlots;
    private javax.swing.JButton btn_EditCode;
    private javax.swing.JButton btn_ModifyPlot;
    private javax.swing.JButton btn_NewPlot;
    private javax.swing.JButton btn_ShowPlot;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTable tbl_PlotTitles;
    private javax.swing.JScrollPane titlesPane;
    // End of variables declaration//GEN-END:variables

}
