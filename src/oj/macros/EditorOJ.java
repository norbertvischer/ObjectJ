package oj.macros;

import ij.IJ;
import ij.plugin.frame.Editor;
import ij.util.Tools;
import java.awt.TextArea;
import oj.OJ;

public class EditorOJ extends Editor {

    public EditorOJ() {
        this(16, 60, 0, MENU_BAR);
    }

    public EditorOJ(int rows, int columns, int fontSize, int options) {
        super(rows, columns, fontSize, options);
    }

    public void showMacroFunctions() {
        TextArea thisTA = getTextArea();
        String selText = thisTA.getSelectedText().replace("\n", " ");
        String[] selectedWords = Tools.split(selText, "/,(,[\"\'&+");
        if (selectedWords.length == 1 && selectedWords[0].length() > 0) {
            if (selectedWords[0].startsWith("oj")) {
                String url = OJ.URL+"/4b-ObjectJMacroFunctions.html";
                IJ.runPlugIn("ij.plugin.BrowserLauncher", url += "#" + selectedWords[0]);

            } else {
                super.showMacroFunctions();
            }
        }
    }
    
}
