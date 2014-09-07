/*
 * QuitOJ.java
 * -- documented
 *
 * Asks to save ojj file before qietting
 */

package oj.plugin;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;
import oj.gui.menuactions.ProjectActionsOJ;

public class QuitOJ implements PlugIn {

    public void run(String arg) {
        ProjectActionsOJ.ForceCloseProjectAction.actionPerformed(null);
        ImageJ ij = IJ.getInstance();
        if (ij != null) {
            ij.quit();
        }
    }
}