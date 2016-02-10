//plotMacroSet: saved textblock containing all macros with  separators
//plotMacrosList: an ArrayList containing individual multiPlot macros
//plotMacro: single macro (including macro title) for creating a plot
//singlePlot: a multiPlot can be composed of one or several singlePlots
//plotTitle:
package oj.gui.settings;

import ij.IJ;
import ij.WindowManager;
import ij.gui.PlotWindow;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import oj.OJ;
import oj.macros.EmbeddedMacrosOJ;

public class PlotSettingsOJ extends javax.swing.JPanel implements IControlPanelOJ {

	public static boolean withPlots = true;
	private static PlotSettingsOJ instance;

	Dimension panelSize = new Dimension(580, 580);
	public static String macroSeparator = "\n//<macro-separator>\n";
	public static String regex = "\n\\/\\/<macro-separator>\n";
	private ArrayList<String> plotMacrosList = new ArrayList<String>();
	boolean running = true;
	long mouseDownTime = 0;

	public PlotSettingsOJ() {

		textToList();
		initComponents();
		updatePlotTable();
		instance = this;
		//setInstance();
	}

	public static PlotSettingsOJ getInstance() {
		return instance;
	}

//    private void setInstance() {
//        instance = this;
//    }
	//Returns plotMacro belonging to that title
	private String getPlotMacro(String plotTitle) {
		String[] titles = getPlotTitles();
		String plotMacro = "";
		for (int jj = 0; jj < titles.length; jj++)
			if (titles[jj].equalsIgnoreCase(plotTitle))
				plotMacro = plotMacrosList.get(jj);
		return plotMacro;
	}

	//converts plotMacroSet to an arrayList of plotMacros
	private void textToList() {
		plotMacrosList = new <String>ArrayList();
		String linkedPlotText = OJ.getData().getLinkedPlotText();
		if (linkedPlotText == null)
			return;
		String[] macros = linkedPlotText.split(regex);
		for (String s : macros)
			plotMacrosList.add(s);
	}

	//composes all macros with macroseparators to single text
	void listToText() {
		String allMacros = "";
		if (plotMacrosList != null)
			for (int jj = 0; jj < plotMacrosList.size(); jj++) {
				if (jj > 0)
					allMacros += macroSeparator;
				allMacros += plotMacrosList.get(jj);
			}
		OJ.getData().setLinkedPlotText(allMacros);
	}

//use macroList to extract titles and show these
	private void updatePlotTable() {
		DefaultTableModel model = (DefaultTableModel) tblPlotTitles.getModel();
		model.setColumnCount(0);
		String[] plotTitles = getPlotTitles();
		model.addColumn("--- Plot Titles ---", plotTitles);
	}

	void makePlot(String txt) {
		IJ.runMacro(txt);
	}

	//extracts the first quote afer the first occurence of keyword in text.
	//Example: Colors = split("red, green") returns 'red, green'
	public static String extractQuote(String text, String varName) {
		int firstKeyPos = text.indexOf(varName);
		int firstQuote = text.indexOf('"', firstKeyPos);
		int secondQuote = text.indexOf('"', firstQuote + 1);
		String quoted = text.substring(firstQuote + 1, secondQuote);
		return quoted;
	}

	//replaces the first quote afer the first occurence of keyword in text.
	public static String replaceQuote(String text, String varName, String replacement) {
		int firstKeyPos = text.indexOf(varName);
		int firstQuote = text.indexOf('"', firstKeyPos);
		int secondQuote = text.indexOf('"', firstQuote + 1);
		if (firstKeyPos == -1 || firstQuote == -1 || secondQuote == -1)
			return text;
		String s1 = text.substring(0, firstQuote + 1);
		String s2 = replacement;
		String s3 = text.substring(secondQuote, text.length());
		String replaced = s1 + s2 + s3;
		return replaced;
	}

	//convert a string like "xMin=0 yMin=0" into "0 NaN 0 NaN"
	public static String makeLimits(String s) {
		String[] resultsArr = "NaN ,NaN ,NaN ,NaN ".split(",");
		s = s.toLowerCase();
		s = s.replaceAll(";", ",");

		String[] varNames = "xmin xmax ymin ymax".split(" ");
		String[] parts = s.split(",");
		for (int jj = 0; jj < parts.length; jj++)
			for (int kk = 0; kk < 4; kk++)
				if (parts[jj].contains(varNames[kk])) {
					String[] terms = parts[jj].split("=");
					if (terms.length == 2)
						resultsArr[kk] = terms[1] + " ";
				}
		String result = resultsArr[0] + resultsArr[1] + resultsArr[2] + resultsArr[3];
		return result;
	}

	//Extract macro title of each macro and return array of titles
	String[] getPlotTitles() {
		int count = plotMacrosList.size();
		String macro;
		for (int jj = count - 1; jj >= 0; jj--) {//clean nulls
			macro = plotMacrosList.get(jj);
			if (macro == null)
				plotMacrosList.remove(jj);
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

//composes all macros with macroseparators to single text
//    void updateMacroSet() {
//        String plotText = "";
//        if (macroList != null) {
//            String[] macros = (String[]) macroList.toArray();
//            for (int jj = 0; jj < macros.length; jj++) {
//                if (jj > 0) {
//                    plotText += macroSeparator;
//                }
//                plotText += macros[jj];
//            }
//        }
//        OJ.getData().setLinkedPlotText(plotText);
//    }
//show min and max in auto-fields
	public Dimension getPanelSize() {
		return panelSize;
	}

	public void setPanelSize(Dimension panelSize) {
		this.panelSize = panelSize;
	}

	public void close() {
		running = false;
	}

	public void setCurrentPlotMacro(String txt) {

	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlesPane = new javax.swing.JScrollPane();
        tblPlotTitles = new javax.swing.JTable();
        btnShowInEditor = new javax.swing.JButton();
        btnShowPlot = new javax.swing.JButton();
        btnAdoptRanges = new javax.swing.JButton();
        btnNew_Plot = new javax.swing.JButton();

        tblPlotTitles.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        tblPlotTitles.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPlotTitles.setDragEnabled(true);
        tblPlotTitles.setShowGrid(true);
        tblPlotTitles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPlotTitlesMouseClicked(evt);
            }
        });
        tblPlotTitles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblPlotTitlesKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblPlotTitlesKeyPressed(evt);
            }
        });
        titlesPane.setViewportView(tblPlotTitles);

        btnShowInEditor.setText("Show in Editor");
        btnShowInEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowInEditorActionPerformed(evt);
            }
        });

        btnShowPlot.setText("Show Plot");
        btnShowPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowPlotActionPerformed(evt);
            }
        });

        btnAdoptRanges.setText("Accept Range");
        btnAdoptRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdoptRangesActionPerformed(evt);
            }
        });

        btnNew_Plot.setText("New Plot");
        btnNew_Plot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNew_PlotActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(btnShowPlot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnShowInEditor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                    .add(btnAdoptRanges, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(btnNew_Plot, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(titlesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(46, 46, 46))
                    .add(layout.createSequentialGroup()
                        .add(btnNew_Plot)
                        .add(75, 75, 75)
                        .add(btnShowInEditor)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdoptRanges)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(btnShowPlot)
                        .add(18, 18, 18))))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void tblPlotTitlesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlotTitlesMouseClicked
		boolean ctrl = evt.isControlDown();
		//long 
		boolean right = SwingUtilities.isRightMouseButton(evt) || IJ.isMacOSX() && ctrl;
		if (right)
			IJ.showMessage("rightButton");
		else {
			int selected[] = tblPlotTitles.getSelectedRows();
			long difference = System.currentTimeMillis() - mouseDownTime;
			mouseDownTime = System.currentTimeMillis();
			boolean doubleClick = (difference <= 250);
			if (doubleClick) {

				int selectedRowIndex = tblPlotTitles.getSelectedRow();

				if (selectedRowIndex >= 0) {
					String plotTitle = (String) tblPlotTitles.getModel().getValueAt(selectedRowIndex, 0);
					String macro = getPlotMacro(plotTitle);
					IJ.runMacro(macro);
				}
			}
		}
    }//GEN-LAST:event_tblPlotTitlesMouseClicked

	// shows complete macro incl. macro statement
    private void btnShowInEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowInEditorActionPerformed

		int selectedRowIndex = tblPlotTitles.getSelectedRow();
		int selectedColumnIndex = 0;
		if (selectedRowIndex >= 0) {
			String plotTitle = (String) tblPlotTitles.getModel().getValueAt(selectedRowIndex, selectedColumnIndex);
			String plotMacro = getPlotMacro(plotTitle);
			EmbeddedMacrosOJ embo = EmbeddedMacrosOJ.getInstance();

			embo.showEmbeddedPlotMacros(plotMacro);
		}


    }//GEN-LAST:event_btnShowInEditorActionPerformed

    private void btnShowPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowPlotActionPerformed
		int selectedRowIndex = tblPlotTitles.getSelectedRow();

		if (selectedRowIndex >= 0) {
			String plotTitle = (String) tblPlotTitles.getModel().getValueAt(selectedRowIndex, 0);
			String plotMacro = getPlotMacro(plotTitle);
			IJ.runMacro(plotMacro);
		}
    }//GEN-LAST:event_btnShowPlotActionPerformed

    private void tblPlotTitlesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPlotTitlesKeyTyped

    }//GEN-LAST:event_tblPlotTitlesKeyTyped

    private void tblPlotTitlesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPlotTitlesKeyPressed
		if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			int[] selected = tblPlotTitles.getSelectedRows();
			for (int jj = selected.length - 1; jj >= 0; jj--)
				plotMacrosList.remove(selected[jj]);
			updatePlotTable();
			OJ.getData().setLinkedPlotText("");
		}
    }//GEN-LAST:event_tblPlotTitlesKeyPressed

	//Calls the plot dialog to obtain a new multiplot-macro
	//plotTitle (embedded in macro)  is made unique if necessary
	//multiplot-macro is added to  macroList
	//plot table is updated
	//macroset (text containing all plotmacros with separators) is updated

    private void btnNew_PlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNew_PlotActionPerformed
		PlotDialogOJ plotDlg = new PlotDialogOJ();
		String plotMacro = plotDlg.composeNewPlotMacro("Plot");//***** C a l l   D i a l o g  ***** 
		String thisTitle = extractQuote(plotMacro, "macro");
		String[] titles = getPlotTitles();
		boolean hit = true;
		for (int trial = 1; trial <= 99 && hit; trial++) {
			hit = false;

			for (int jj = 0; jj < titles.length; jj++)//guarantee unique plotTitle

				if (thisTitle.equalsIgnoreCase(titles[jj])) {
					hit = true;
					int dashPos = thisTitle.lastIndexOf("-");
					if (dashPos > 0)
						thisTitle = thisTitle.substring(dashPos + 1);
					thisTitle += "-" + trial;
				}
			plotMacro = replaceQuote(plotMacro, "macro", thisTitle);
			plotMacro = replaceQuote(plotMacro, "plotTitle", thisTitle);
			plotMacrosList.add(plotMacro);
			updatePlotTable();
			listToText();
		}
    }//GEN-LAST:event_btnNew_PlotActionPerformed

    private void btnAdoptRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdoptRangesActionPerformed
		String shownTitle = null, selectedTitle = null;
		PlotWindow pw = null;

		int[] selected = tblPlotTitles.getSelectedRows();
		if (selected.length == 1) {
			int index = selected[0];
			String[] availablePlots = getPlotTitles();
			selectedTitle = availablePlots[index];

			if (WindowManager.getCurrentWindow() instanceof PlotWindow) {
				pw = (PlotWindow) WindowManager.getCurrentWindow();
				shownTitle = pw.getImagePlus().getTitle();

			}

			if (selectedTitle == null || shownTitle == null) {
				if (selectedTitle != null)
					IJ.showMessage(selectedTitle + "Window '" + selectedTitle + "'  must be in front");
				else
					IJ.showMessage("Single plot title must be selected");
				return;
			}
			Rectangle frame = pw.getPlot().getDrawingFrame();
			String frameStr = "" + frame.width + " " + frame.height;

			double[] limits = pw.getPlot().getLimits();
			String limitsStr = "" + (float) limits[0];
			for (int jj = 1; jj < 4; jj++)
				limitsStr += " " + (float) limits[jj];
			String macro = getPlotMacro(selectedTitle);
			macro = replaceQuote(macro, "frameSize =", frameStr);
			macro = replaceQuote(macro, "limits =", limitsStr);
			plotMacrosList.set(index, macro);
			listToText();

		}

		//IJ.showMessage("Selected plot window must be in front");
    }//GEN-LAST:event_btnAdoptRangesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdoptRanges;
    private javax.swing.JButton btnNew_Plot;
    private javax.swing.JButton btnShowInEditor;
    private javax.swing.JButton btnShowPlot;
    private javax.swing.JTable tblPlotTitles;
    private javax.swing.JScrollPane titlesPane;
    // End of variables declaration//GEN-END:variables

}
