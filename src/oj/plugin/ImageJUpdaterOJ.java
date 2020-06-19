/*
 * ImageJUpdaterOJ.java
 * -- documented
 *
 * disables "update menus" when ObjectJ is active  //11.6.2015
 */
package oj.plugin;

import ij.IJ;

import ij.plugin.ImageJ_Updater;


public class ImageJUpdaterOJ extends ImageJ_Updater {

    boolean saveJarAttempt;

    public void run(String arg) {
        if (arg.equals("menus")) {

            String msg = "With ObjectJ, you currently cannot Refresh Menus, you rather need to restart ImageJ";
            IJ.showMessage(msg);
            return;
        }

        if (IJ.getApplet() != null) {
            return;
        }
    }
}
 

