package oj.gui.settings;

import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.PlotWindow;
import ij.plugin.Colors;
import ij.plugin.ScreenGrabber;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import oj.OJ;
import oj.project.results.ColumnDefOJ;

/**
 * PlotManagerOJ is the settings panel with table of existing plot macros and
 * buttons to show them or crate new ones.
 */
public class PlotDialogOJ {//implements DialogListener {

    final int MAXCURVES = 9;
    final int XCOL = 0, YCOL = 1, COLOR = 2, TYP = 3, QUALIFIED = 4, BINWIDTH = 5;
    final int LIMITS = 5;
    int currentCurve = 0;
    int nCurves = 1;
    Panel radioPanel;
    Button plusButton, minusButton;
    Checkbox previewBox, qualifiedBox;
    Label msg_NameLabel;
    private Vector choices, stringFields;
    String template;
    CheckboxGroup cg = new CheckboxGroup();
    ArrayList<String[]> multiPlot = new ArrayList<String[]>();//
    String multiPlotTitle = "";
    Checkbox[] multiPlotBoxes;
    boolean previewFlag = false;
    String plotLimit = "";
    String[] limitStrings = "Automatic,Left = zero,Bottom = zero,BottomLeft = zero".split(",");
    final String[] plotColors = "red blue green orange magenta cyan black gray other".split(" ");

    /**
     * Creates the New Plot dialog and then composes the macro text. uses either
     * default values or fromExistinMacro if != null
     */
    /* 
	- builds the Dialog 
	- while Dialog is open, events will populate the multiplot 
	- Plus button adds a singleCurve: multiplot array will expand
	- when Dialog is closed: -
	 * makeMultiplotText() creates multiplot macro plotTitle is made unique -
	 * returns multiPlotText via makeMultiplotText(), or null if cancelled
     */
    String composePlotMacro(String plotType, String fromExistinMacro) {
	multiPlot.clear();

	PlotDialog2 plotDialog = new PlotDialog2("New " + plotType);
	for (int trial = 0; trial < 2; trial++) {//23.8.2017 invoke twice to avoid missing dialog items
	    plotDialog = new PlotDialog2("New " + plotType);

	    //Top panel with buttons
	    Panel buttonPanel = new Panel(new GridLayout(1, 3, 0, 0));
	    buttonPanel.add(new Label("Curves:"));

	    minusButton = new Button("-");
	    minusButton.addActionListener(plotDialog);
	    buttonPanel.add(minusButton);

	    plusButton = new Button("+");
	    plusButton.addActionListener(plotDialog);
	    buttonPanel.add(plusButton);
	    buttonPanel.add(new Label(""));//create distance

	    //IJ.wait(1000);
	    previewBox = new Checkbox("Preview");
	    previewBox.addItemListener(plotDialog);
	    buttonPanel.add(previewBox);
	    plotDialog.addPanel(buttonPanel, GridBagConstraints.WEST, new Insets(5, 0, 0, 0));

	    //Radio panel
	    radioPanel = new Panel(new GridLayout(1, 3, 0, 0));
	    multiPlotBoxes = new Checkbox[MAXCURVES];
	    for (int i = 1; i <= MAXCURVES; i++) {
		Checkbox cb = new Checkbox("" + i, cg, i == 1);
		cb.addItemListener(plotDialog);
		radioPanel.add(cb);
		cb.setVisible(i == 1);
		multiPlotBoxes[i - 1] = cb;
	    }
	    plotDialog.addPanel(radioPanel);

	    //further, use Wayne's generic methods
	    plotDialog.addMessage("---");
	    msg_NameLabel = (Label) plotDialog.getMessage();

	    String[] columnTitles = OJ.getData().getResults().getColumns().columnLinkedNamesToArray();
	    columnTitles = modifyTitles(columnTitles);
	    if (plotType.equalsIgnoreCase("Histogram")) {
	    }//not implemented

	    plotDialog.addChoice("x-Axis:", columnTitles, columnTitles[0]);
	    plotDialog.addChoice("y-Axis:", columnTitles, columnTitles[0]);
	    plotDialog.addChoice("Color:", plotColors, "red");
	    plotDialog.addChoice("Marker type:", "circles,line,boxes,triangles,crosses,dots,x,connected,error_bars".split(","), "circles");
	    plotDialog.addChoice("Qualification:", "all,qualified,unqualified".split(","), "all");
	    plotDialog.addStringField("Bin Width:", "0", 8);
	    plotDialog.addChoice("Ranges:", limitStrings, limitStrings[0]);
	    plotDialog.addStringField("Title *:", "", 32);
	    String note = "* Leave empty for automatic title";
	    plotDialog.addMessage(note, Font.decode("Arial-12"));
	    //plotDialog.addCheckbox("Redraw this Dialog", false);

	    choices = plotDialog.getChoices();
	    stringFields = plotDialog.getStringFields();
if(trial==1)
	    if (fromExistinMacro != null && fromExistinMacro.length() > 0) {
		previewFlag = true;
		macroToGui(fromExistinMacro);
		createPreview();
	    }

	    plotDialog.setCancelLabel("Cancel");
	}

	plotDialog.showDialog();//---- show Dialog -----

	IJ.runMacro("close('Preview*');");
	if (plotDialog.wasCanceled()) {
	    return null;
	}

	//All settings are already recorded via events, except plotTitle?
	plotDialog.getNextString();//advance counter; bins are cought by event
	String plotTitle = plotDialog.getNextString();
	if (!plotDialog.wasOKed()) {
	    plotTitle = IJ.getString("Duplicate As:", plotTitle);
	}
	String macroText = makeMultiplotText(plotTitle);

	return macroText;
    }

    /**
     * Extracts key parameters from macro text to set the multiPlot variables,
     * then calls dataToGui()
     */
    void macroToGui(String macro) {
//final int XCOL = 0, YCOL = 1, COLOR = 2, TYP = 3, QUALIFIED = 4, BINWIDTH = 5;
	String[] xColumnsAr = PlotManagerOJ.extractQuote(macro, "xColumns").split(" ");
	String[] yColumnsAr = PlotManagerOJ.extractQuote(macro, "yColumns").split(" ");
	String[] colorsAr = PlotManagerOJ.extractQuote(macro, "colors").split(" ");
	String[] typesAr = PlotManagerOJ.extractQuote(macro, "types").split(" ");
	String[] qualifyFlagsAr = PlotManagerOJ.extractQuote(macro, "qualifyFlags").split(" ");
	String[] binWidthsAr = PlotManagerOJ.extractQuote(macro, "binWidths").split(" ");
	String[] xRange = PlotManagerOJ.extractQuote(macro, "xRange").split(" ");
	String[] yRange = PlotManagerOJ.extractQuote(macro, "yRange").split(" ");
	multiPlotTitle = PlotManagerOJ.extractQuote(macro, "plotTitle");
	plotLimit = limitStrings[0];//auto
	if (xRange.length == 2 && yRange.length == 2) {
	    boolean left0 = xRange[0].equals("0");
	    boolean bottom0 = yRange[0].equals("0");
	    if (left0 && bottom0) {
		plotLimit = limitStrings[3];
	    } else if (left0) {
		plotLimit = limitStrings[1];
	    } else if (bottom0) {
		plotLimit = limitStrings[2];
	    }
	}

	int nPlots = yColumnsAr.length;
	nCurves = nPlots;
	multiPlot.clear();
	for (int jj = 0; jj < nPlots; jj++) {
	    String[] singleCurve = new String[22];
	    singleCurve[XCOL] = xColumnsAr[jj];
	    singleCurve[YCOL] = yColumnsAr[jj];
	    singleCurve[COLOR] = colorsAr[jj];
	    singleCurve[TYP] = typesAr[jj];
	    singleCurve[QUALIFIED] = qualifyFlagsAr[jj];
	    singleCurve[BINWIDTH] = binWidthsAr[jj];
	    multiPlot.add(singleCurve);
	}
	for (int jj = 0; jj < MAXCURVES; jj++) {
	    multiPlotBoxes[jj].setVisible(nPlots > jj);
	}
	dataToGui();
    }

    String makeMultiplotText(String plotTitle) {

	InputStream fis = this.getClass().getResourceAsStream("/oj/macros/plotMacros/plotTemplate.txt");
	try {
	    StringBuilder inputStringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
	    String line = bufferedReader.readLine();
	    while (line != null) {
		inputStringBuilder.append(line);
		inputStringBuilder.append('\n');
		line = bufferedReader.readLine();
	    }
	    template = inputStringBuilder.toString();
	} catch (Exception e) {
	    return null;
	}
	if (plotTitle.length() == 0) {
	    plotTitle = multiPlot.get(0)[YCOL] + " vs. " + multiPlot.get(0)[XCOL];
	}
	String xColumns = "";
	String yColumns = "";
	String colors = "";
	String types = "";
	String qualifyChars = "";
	String binWidths = "";
	String legend = "";
	for (int plot = 0; plot < nCurves; plot++) {
	    xColumns += multiPlot.get(plot)[XCOL] + " ";
	    yColumns += multiPlot.get(plot)[YCOL] + " ";
	    colors += multiPlot.get(plot)[COLOR] + " ";
	    types += multiPlot.get(plot)[TYP] + " ";
	    binWidths += multiPlot.get(plot)[BINWIDTH] + " ";
	    String qualifyString = multiPlot.get(plot)[QUALIFIED];
	    String qualifyChar = qualifyString.substring(0, 1);
	    qualifyChars += qualifyChar + " ";
	    String thisLegend = multiPlot.get(plot)[YCOL];
	    if ("qu".contains(qualifyChar)) {
		thisLegend += " (" + qualifyChar + ")";
	    }
	    legend += thisLegend + "\\n";
	}
	String legendPosition = "auto";
	//String limits = PlotManagerOJ.makeLimits(plotLimit);
	String text = template;
	text = PlotManagerOJ.replaceQuote(text, "macro ", plotTitle);
	text = PlotManagerOJ.replaceQuote(text, "plotTitle =", plotTitle);
	text = PlotManagerOJ.replaceQuote(text, "xColumns =", xColumns);
	text = PlotManagerOJ.replaceQuote(text, "yColumns =", yColumns);
	text = PlotManagerOJ.replaceQuote(text, "types =", types);
	text = PlotManagerOJ.replaceQuote(text, "colors =", colors);
	text = PlotManagerOJ.replaceQuote(text, "binWidths =", binWidths);
	text = PlotManagerOJ.replaceQuote(text, "qualifyFlags =", qualifyChars);
	text = PlotManagerOJ.replaceQuote(text, "legend =", legend);
	text = PlotManagerOJ.replaceQuote(text, "legendPosition =", legendPosition);
	text = PlotManagerOJ.replaceQuote(text, "limits =", plotLimit);

	if (xColumns.contains("- ") || yColumns.contains("- ")) {
	    return null;
	}

	return text;
    }

    /**
     * Reads all buttons and choices and updates MultiPlot.
     */
    void guiToData() {
	String[] singleCurve = new String[33];
	singleCurve[YCOL] = ((Choice) choices.get(YCOL)).getSelectedItem();
	singleCurve[XCOL] = ((Choice) choices.get(XCOL)).getSelectedItem();
	String color = ((Choice) choices.get(COLOR)).getSelectedItem();
	if (!(color.equals("other"))) {
	    singleCurve[COLOR] = color;
	} else {
	    singleCurve[COLOR] = "#bbbbbb";
	}
	singleCurve[TYP] = ((Choice) choices.get(TYP)).getSelectedItem();
	singleCurve[QUALIFIED] = ((Choice) choices.get(QUALIFIED)).getSelectedItem().substring(0, 1);
	singleCurve[BINWIDTH] = ((TextField) (stringFields.get(0))).getText();
	String limit = ((Choice) choices.get(LIMITS)).getSelectedItem();
	String left = "NaN";
	if (limit.contains("Left")) {
	    left = "0";
	}
	String bottom = "NaN";
	if (limit.contains("Bottom")) {
	    bottom = "0";
	}
	plotLimit = left + " NaN " + bottom + " NaN";
	if (multiPlot.size() <= currentCurve) {
	    multiPlot.add(currentCurve, singleCurve);
	} else {
	    multiPlot.set(currentCurve, singleCurve);
	}
    }

    /**
     * Uses data from the active SingleCurve and adjusts dialog components.
     */
    void dataToGui() {
	String[] singleCurve = multiPlot.get(currentCurve);
	((Choice) choices.get(YCOL)).select(singleCurve[YCOL]);
	((Choice) choices.get(XCOL)).select(singleCurve[XCOL]);
	handleOther(((Choice) choices.get(COLOR)), singleCurve[COLOR]);

	((Choice) choices.get(TYP)).select(singleCurve[TYP]);
	//((Choice) choices.get(COLOR)).select(singleCurve[COLOR]);
	//expand e.g. "q" to "qualified" before addressing choice
	String qChar = singleCurve[QUALIFIED];
	String qString = "all";
	if (qChar.equals("q")) {
	    qString = "qualified";
	}
	if (qChar.equals("u")) {
	    qString = "unqualified";
	}
	((Choice) choices.get(QUALIFIED)).select(qString);
	((TextField) (stringFields.get(0))).setText(singleCurve[BINWIDTH]);
	for (int plot = 0; plot < multiPlot.size(); plot++) {
	    multiPlot.get(plot)[XCOL] = singleCurve[XCOL];//xAxis is same for all plots
	}
	String color = multiPlot.get(currentCurve)[COLOR];
	Color thisColor = Colors.getColor(color, Color.black);
	if (thisColor == Color.green) {
	    thisColor = new Color(0, 192, 0);
	}
	String xColumn = multiPlot.get(currentCurve)[XCOL];
	String yColumn = multiPlot.get(currentCurve)[YCOL];
	msg_NameLabel.setForeground(thisColor);
	msg_NameLabel.setText(yColumn + "  vs. " + xColumn);

	for (int curve = 0; curve < nCurves; curve++) {
	    color = multiPlot.get(curve)[COLOR];
	    thisColor = Colors.getColor(color, Color.BLACK);

	    if (thisColor == Color.green) {
		thisColor = new Color(0, 192, 0);
	    }
	    multiPlotBoxes[curve].setForeground(thisColor);
	}

	((Choice) choices.get(LIMITS)).select(plotLimit);
	((TextField) (stringFields.get(1))).setText(multiPlotTitle);
	//IJ.log("Putting plot title back:  [" +multiPlotTitle + "]");
	previewBox.setState(previewFlag);
	//IJ.beep();

    }

    /*If source contains keywords other than standard choice items, 
	(only color so far, needs to be improved)*/
    void handleOther(Choice choice, String s) {
	for (int jj = 0; jj < choice.getItemCount(); jj++) {
	    String item = choice.getItem(jj);
	    if (item.equals(s)) {
		choice.select(jj);
		return;
	    }
	}
	choice.select("other");
    }

    /**
     * Returns false if any of the column titles does not exist.
     */
    boolean checkValidColumns() {
	if (multiPlot != null) {
	    for (int mm = 0; mm < multiPlot.size(); mm++) {
		String[] singleCurve = multiPlot.get(mm);

		int a = OJ.getData().getResults().getColumns().getColumnIndexByName(singleCurve[XCOL]);
		int b = OJ.getData().getResults().getColumns().getColumnIndexByName(singleCurve[YCOL]);
		if (a < 0 || b < 0) {
		    return false;
		}
	    }
	}
	return true;
    }

    /**
     * The preview is a plot that is altered when changing the plot dialog.
     */
    void createPreview() {
	Macro.abort();
	if (!checkValidColumns()) {
	    msg_NameLabel.setForeground(Color.red);
	    msg_NameLabel.setText("No value pairs are available");
	    return;
	}

	Rectangle frame = null;
	if (WindowManager.getImageCount() > 0) {
	    ImagePlus imp = IJ.getImage();
	    ImageWindow win = imp.getWindow();
	    if (win instanceof PlotWindow && imp.getTitle().equals("Preview")) {
		frame = ((PlotWindow) win).getPlot().getDrawingFrame(); //imp.close();
	    }
	}

	if (multiPlot != null && multiPlot.size() > 0) {
	    String text = makeMultiplotText("Preview");
	    IJ.wait(10);
	    String notUsed = IJ.runMacro(text);// ---- run the plot macro -----

	    IJ.wait(100);
	    if (frame != null) {
		IJ.runMacro("Plot.setFrameSize(" + frame.width + "," + frame.height + ");");
	    }
	    IJ.runMacro("changeValues(0xffffff,0xffffff,0xeeeeee);");

	    ImagePlus imp = WindowManager.getCurrentImage();
	    ImagePlus imp2 = WindowManager.getImage("Preview2");
	    imp2.setProcessor(imp.getProcessor());
	    imp2.show();
	    imp.close();
	}
    }

    /**
     * Linked Column titles: exclude Text columns and precede with a dummy '-'.
     */
    String[] modifyTitles(String[] colTitles
    ) {
	String validColumns = "- ";
	//String validColumns = "Frequency - ";
	for (String tilte : colTitles) {
	    ColumnDefOJ columnDef = OJ.getData().getResults().getColumns().getColumnByName(tilte).getColumnDef();
	    if (!columnDef.isUnlinked() && !columnDef.isTextMode()) {
		validColumns += tilte + " ";
	    }
	}
	colTitles = validColumns.split(" ");
	return colTitles;
    }

    /**
     * This class extends GenericDialog in order to catch the events.
     */
    class PlotDialog2 extends GenericDialog {

	public PlotDialog2(String title) {
	    super(title);
	    IJ.wait(100);
	    //super.revalidate();// will it help?
	    //IJ.beep();
	}

	/**
	 * Handles changes in Choices and Checkboxes. Updates the preview window
	 * if it exists.
	 */
	public void itemStateChanged(ItemEvent e) {

	    if (e.getSource() == previewBox) {
		previewFlag = previewBox.getState();
		if (previewFlag) {
		    guiToData();
		    createPreview();
		} else {
		    IJ.runMacro("close(\"Preview2\");");
		}
		return;
	    }

	    //adjust number of plots
	    String label = cg.getSelectedCheckbox().getLabel();
	    int n = Integer.parseInt(label);
	    int tmp = n - 1;
	    if (tmp != currentCurve) {
		currentCurve = tmp;
		dataToGui();
		return;
	    }

	    guiToData();
	    dataToGui();

	    if (multiPlot.size() > 0) {
		boolean isErrorBars = multiPlot.get(currentCurve)[TYP].startsWith("error");
		TextField tf = ((TextField) (stringFields.get(0)));
		tf.setVisible(isErrorBars);
	    }

	    if (previewFlag) {
		createPreview();
	    }
	}

	/**
	 * Handles Plus and Minus buttons for adding/deleting a SingleCurve.
	 */
	public void actionPerformed(ActionEvent e) {
	    guiToData();

	    super.actionPerformed(e);
	    if (e.getSource() == plusButton) {
		String[] ss = (String[]) multiPlot.get(currentCurve);
		if (ss[XCOL].equals("-") || ss[YCOL].equals("-")) {
		    IJ.showMessage("", "Invalid x- or y-Axis");
		    return;
		}
		if (nCurves < MAXCURVES) {
		    nCurves++;
		    currentCurve = nCurves - 1;
		    for (int jj = 0; jj < MAXCURVES; jj++) {
			Checkbox box = (Checkbox) radioPanel.getComponent(jj);
			box.setVisible(jj <= currentCurve);
			box.setState(jj == currentCurve);
		    }
		    String[] singleCurve = multiPlot.get(currentCurve - 1).clone();

		    singleCurve[YCOL] = "-";
		    if (nCurves < plotColors.length) {
			singleCurve[COLOR] = plotColors[nCurves - 1];
			//((Choice) choices.get(2)).select(singleCurve[COLOR]);
		    }

		    multiPlot.add(singleCurve.clone());
		    ss = (String[]) multiPlot.get(currentCurve);

		    //we need a None column! It could be the first of the choices. However, we may have too many columns?
		}
		dataToGui();
	    }
	    if (e.getSource() == minusButton) {
		if (nCurves > 1) {
		    if (currentCurve < nCurves - 1) {
			if (!IJ.showMessageWithCancel("", "Delete Curve #" + (currentCurve + 1))) {
			    return;
			}
		    }
		    multiPlot.remove(currentCurve);
		    nCurves--;
		    currentCurve = nCurves - 1;
		    for (int jj = 0; jj < MAXCURVES; jj++) {
			Checkbox box = (Checkbox) radioPanel.getComponent(jj);
			box.setVisible(jj < nCurves);
			box.setState(jj == currentCurve);
		    }
		}
		dataToGui();

		if (previewFlag) {
		    createPreview();
		}
	    }

	}

	public void keyReleased(KeyEvent e) {
	    Component c = e.getComponent();
	    if (c == stringField.get(0)) {

		guiToData();
		dataToGui();

		if (previewFlag) {
		    createPreview();
		}
	    }
	    super.keyReleased(e);
	}

	public void focusLost(FocusEvent e) {
	    Component c = e.getComponent();
	    if (c == stringField.get(0)) {
		//IJ.beep();
		if (previewFlag) {
		    createPreview();
		}
	    }
	    if (c instanceof TextField) {
		((TextField) c).select(0, 0);
		multiPlotTitle = ((TextField) c).getText();
	    }
	    super.focusLost(e);
	}

    }
}
