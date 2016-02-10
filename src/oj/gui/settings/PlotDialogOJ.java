package oj.gui.settings;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.PlotWindow;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import oj.OJ;

public class PlotDialogOJ {

    int currentPlot = 0;
    int maxPlots = 9;
    int nPlots = 1;
    Panel radioPanel;
    Button plusButton, minusButton;
    Checkbox previewBox, qualifiedBox;
    private Vector choices, stringFields;
    String template;
    CheckboxGroup cg = new CheckboxGroup();
    ArrayList<String[]> multiPlot = new ArrayList<String[]>();//
    //String[] singlePlot;
    boolean previewFlag = false;
    String plotLimits = "";

    final int XCOL = 0, YCOL = 1, COLOR = 2, TYP = 3, QUALIFIED = 4, BINWIDTH = 5;
    final String[] plotColors = "red blue green orange magenta cyan black gray".split(" ");


    /*
     - creates an empty multiPlot
     - builds the Dialog
     - while Dialog is open, events will fill the multiplot
     - Plus button adds a singlePlot: elements of multiplot will become longer arrays of strings
     - when Dialog is closed:  
     -       makeMultiplotText() creates multiplot macro
     plotTtle is made unique
     - multiplot macro text is returned.
     */
    String composeNewPlotMacro(String plotType) {
        multiPlot.clear();
        PlotDialog plotDialog = new PlotDialog("New " + plotType);

        //Top panel with buttons
        Panel buttonPanel = new Panel(new GridLayout(1, 3, 0, 0));
        buttonPanel.add(new Label("MultiPlots:"));
        minusButton = new Button("-");
        minusButton.addActionListener(plotDialog);
        buttonPanel.add(minusButton);
        plusButton = new Button("+");
        plusButton.addActionListener(plotDialog);
        buttonPanel.add(plusButton);
        previewBox = new Checkbox("Preview");
        previewBox.addItemListener(plotDialog);
        buttonPanel.add(previewBox);
        plotDialog.addPanel(buttonPanel, GridBagConstraints.WEST, new Insets(5, 0, 0, 0));
        //Radio panel
        radioPanel = new Panel(new GridLayout(1, 3, 0, 0));
        for (int i = 1; i <= maxPlots; i++) {
            Checkbox cb = new Checkbox("" + i, cg, i == 1);
            cb.addItemListener(plotDialog);
            radioPanel.add(cb);
            cb.setVisible(i == 1);
        }
        plotDialog.addPanel(radioPanel, GridBagConstraints.WEST, new Insets(5, 0, 0, 0));

        //furthor, use Wayne's generic methods
        plotDialog.addMessage("");
        String[] columnTitles = OJ.getData().getResults().getColumns().columnLinkedNamesToArray();
        plotDialog.addChoice("x-Axis:", columnTitles, columnTitles[0]);
        plotDialog.addChoice("y-Axis:", columnTitles, columnTitles[1]);
        plotDialog.addChoice("Color:", plotColors, "red");

        plotDialog.addChoice("Marker type:", "line,circles,boxes,triangles,crosses,dots,x,connected,Error Bars".split(","), "circles");
        plotDialog.addChoice("Objects:", "all,qualified,unqualified".split(","), "all");
        plotDialog.addStringField("Bin Width:", "0", 8);
        plotDialog.addStringField("Limits*:", "", 32);
        plotDialog.addStringField("Title **:", "", 32);
        String note = "* Leave empty for automatic range\n- or enter fixed min/max limits, e.g. 'xMin=0, yMin=0'";
        note += "\n \n** Leave empty for automatic title";

        plotDialog.addMessage(note, Font.decode("Arial-12"));

        //plotDialog.addCheckbox("Qualified Objects Only", false);
        choices = plotDialog.getChoices();
        stringFields = plotDialog.getStringFields();

        //radio_Groups = plotDialog.getRadioButtonGroups();;//Generic Dialog is not aware of  previeous radio
        plotDialog.showDialog();
        //All settings are already recorded via events, except plotTitle?
        IJ.runMacro("close('Preview');");
        String dummy = plotDialog.getNextString();//bins are cought by event
        String range = plotDialog.getNextString();
        String plotTitle = plotDialog.getNextString();
        String macroText = makeMultiplotText(plotTitle);
        return macroText;
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
        //String qualifyStrings = "";

        String qualifyChars = "";
        String binWidths = "";
        String legend = "";
        for (int plot = 0; plot < nPlots; plot++) {
            xColumns += multiPlot.get(plot)[XCOL] + " ";
            yColumns += multiPlot.get(plot)[YCOL] + " ";
            colors += multiPlot.get(plot)[COLOR] + " ";
            types += multiPlot.get(plot)[TYP] + " ";
            binWidths += multiPlot.get(plot)[BINWIDTH] + " ";
            String qualifyString = multiPlot.get(plot)[QUALIFIED];
            String qualifyChar = qualifyString.substring(0, 1);
            //qualifyStrings += qualifyString + " ";
            qualifyChars += qualifyChar + " ";
            String thisLegend = multiPlot.get(plot)[YCOL];
            if ("qu".contains(qualifyChar)) {
                thisLegend += " " + qualifyChar;
            }
            legend += thisLegend + "\\n";
        }
        String legendPosition = "auto";
        String limits = PlotSettingsOJ.makeLimits(plotLimits);
        String text = template;
        text = PlotSettingsOJ.replaceQuote(text, "macro ", plotTitle);
        text = PlotSettingsOJ.replaceQuote(text, "plotTitle =", plotTitle);
        text = PlotSettingsOJ.replaceQuote(text, "xColumns =", xColumns);
        text = PlotSettingsOJ.replaceQuote(text, "yColumns =", yColumns);
        text = PlotSettingsOJ.replaceQuote(text, "types =", types);
        text = PlotSettingsOJ.replaceQuote(text, "colors =", colors);
        text = PlotSettingsOJ.replaceQuote(text, "binWidths =", binWidths);
        text = PlotSettingsOJ.replaceQuote(text, "qualifyFlags =", qualifyChars);
        text = PlotSettingsOJ.replaceQuote(text, "legend =", legend);
        text = PlotSettingsOJ.replaceQuote(text, "legendPosition =", legendPosition);
        text = PlotSettingsOJ.replaceQuote(text, "limits =", limits);

        return text;

    }

    //Reads all buttons and choices and updates MultiPlot 
    void guiToData() {
        String[] singlePlot = new String[33];
        singlePlot[XCOL] = ((Choice) choices.get(0)).getSelectedItem();
        singlePlot[YCOL] = ((Choice) choices.get(1)).getSelectedItem();
        singlePlot[COLOR] = ((Choice) choices.get(2)).getSelectedItem();
        singlePlot[TYP] = ((Choice) choices.get(3)).getSelectedItem();
        singlePlot[QUALIFIED] = ((Choice) choices.get(4)).getSelectedItem();
        singlePlot[BINWIDTH] = ((TextField) (stringFields.get(0))).getText();
        plotLimits = ((TextField) (stringFields.get(1))).getText();
        if (multiPlot.size() <= currentPlot) {
            multiPlot.add(currentPlot, singlePlot);
        } else {
            multiPlot.set(currentPlot, singlePlot);
        }
    }

    void dataToGui() {

        String[] singlePlot = multiPlot.get(currentPlot);

        ((Choice) choices.get(XCOL)).select(singlePlot[XCOL]);
        ((Choice) choices.get(YCOL)).select(singlePlot[YCOL]);
        ((Choice) choices.get(TYP)).select(singlePlot[TYP]);
        ((Choice) choices.get(COLOR)).select(singlePlot[COLOR]);
        //expand e.g. "q" to "qualified" before addressing choice
        String qChar = singlePlot[QUALIFIED];
        String qString = "all";
        if (qChar.equals("q")) {
            qString = "qualified";
        }
        if (qChar.equals("u")) {
            qString = "unqualified";
        }
        ((Choice) choices.get(QUALIFIED)).select(qString);
        ((TextField) (stringFields.get(0))).setText(singlePlot[BINWIDTH]);
        for (int plot = 0; plot < multiPlot.size(); plot++) {
            multiPlot.get(plot)[XCOL] = singlePlot[XCOL];//xAxis is same for all plots
        }
    }

    /*
     - Is preview open: get drawing frame
     - close preview
     - create MultiPlot text with title preview
     - execute MultiPlot text 
    
    
     */
    void createPreview() {
        Rectangle frame = null;
        if (WindowManager.getImageCount() > 0) {
            ImagePlus imp = IJ.getImage();
            ImageWindow win = imp.getWindow();
            if (win instanceof PlotWindow && imp.getTitle().equals("Preview")) {
                frame = ((PlotWindow) win).getPlot().getDrawingFrame();
            }
        }

        if (multiPlot != null && multiPlot.size() > 0) {
            String text = makeMultiplotText("Preview");
            IJ.runMacro("close(\"Preview\");");
            IJ.runMacro(text);
            if (frame != null) {
                IJ.runMacro("Plot.setFrameSize(" + frame.width + "," + frame.height + ");");
            }
        }
    }

    //Extends GenericDialog to catch the events
    class PlotDialog extends GenericDialog {

        public PlotDialog(String title) {
            super(title);
        }

        //radio buttons, checkboxes and choices
        public void itemStateChanged(ItemEvent e) {

            if (e.getSource() == previewBox) {
                previewFlag = previewBox.getState();
                if (previewFlag) {
                    createPreview();
                } else {
                    IJ.runMacro("close(\"Preview\");");
                }
                return;
            }

            //adjust number of plots
            String label = cg.getSelectedCheckbox().getLabel();
            int n = Integer.parseInt(label);
            int tmp = n - 1;
            if (tmp != currentPlot) {
                currentPlot = tmp;
                dataToGui();
                return;
            }
            guiToData();
            if (previewFlag) {
                createPreview();
            }
        }

        //plus or minus button
        public void actionPerformed(ActionEvent e) {
            guiToData();
            super.actionPerformed(e);
            if (e.getSource() == plusButton) {
                if (nPlots < maxPlots) {
                    nPlots++;
                    currentPlot = nPlots - 1;
                    for (int jj = 0; jj < maxPlots; jj++) {
                        Checkbox box = (Checkbox) radioPanel.getComponent(jj);
                        box.setVisible(jj <= currentPlot);
                        box.setState(jj == currentPlot);
                    }
                    String[] singlePlot = multiPlot.get(currentPlot - 1);

                    multiPlot.add(singlePlot.clone());
                    String[] ss = (String[]) multiPlot.get(currentPlot);
                    if (nPlots < plotColors.length) {
                        ss[COLOR] = plotColors[nPlots - 1];
                        ((Choice) choices.get(2)).select(ss[COLOR]);
                    }

                    //we need a None column! It could be the first of the choices. However, we may have too many columns?
                }
            }
            if (e.getSource() == minusButton) {
                if (nPlots > 1) {
                    multiPlot.remove(currentPlot);
                    nPlots--;
                    currentPlot = 0;
                    for (int jj = 0; jj < maxPlots; jj++) {
                        Checkbox box = (Checkbox) radioPanel.getComponent(jj);
                        box.setVisible(jj < nPlots);
                        box.setState(jj == currentPlot);
                    }
                }
            }

        }
    }
}
