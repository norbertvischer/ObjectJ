/**
 * ViewActionsOJ.java
 * fully documented 18.5.2010
 *
 * ViewActionsOJ supplies listener methods that are
 * connected to menu items that show one of the 5 ObjectJ windows (project, tools, results, Finder, show_embedded_macros)
 * plus reload macros plus clicks on the items in tool bar
 */
package oj.gui.menuactions;

import ij.IJ;
import ij.Menus;
import ij.plugin.frame.Recorder;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import oj.macros.EmbeddedMacrosOJ;
import oj.util.UtilsOJ;
import oj.gui.results.ProjectResultsOJ;
import oj.gui.KeyEventManagerOJ;
import oj.gui.ToolsWindowOJ;
import oj.gui.settings.ProjectSettingsOJ;

public class ViewActionsOJ {

    /** Used to make project window visible*/
    public static ActionListener SettingsAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {


            if (Recorder.record && !IJ.isMacro()) {//my first recorded macro command
                Recorder.record("ojShowProject");
            }
            if (ProjectSettingsOJ.getInstance() != null) {
                ProjectSettingsOJ.getInstance().validate();
                ProjectSettingsOJ.getInstance().setVisible(true);

            } else {
                new ProjectSettingsOJ();
                ProjectSettingsOJ.getInstance().validate();
                ProjectSettingsOJ.getInstance().setVisible(true);
                // ProjectSettingsOJ.getInstance().setState(Frame.NORMAL);
            }
            if (ProjectSettingsOJ.getInstance().getState() == Frame.ICONIFIED) {//30.10.2011
                ProjectSettingsOJ.getInstance().setState(Frame.NORMAL);
            }

            ProjectSettingsOJ.getInstance().toFront();
        }
    };
    /**
     * Used to add listeners to the item list at the bottom of ObjectJ tools
     */
    public static ActionListener YtemListAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (ToolsWindowOJ.getInstance() == null) {
                if (IJ.isMacintosh()) {
                    ToolsWindowOJ.setInstance(new JFrame());
                    ToolsWindowOJ.getInstance().addKeyListener(KeyEventManagerOJ.getInstance());
                    ToolsWindowOJ.getInstance().addWindowListener(new java.awt.event.WindowAdapter() {

                        public void windowActivated(java.awt.event.WindowEvent evt) {
                            if (IJ.isMacintosh() && IJ.getInstance() != null) {
                                IJ.wait(1); // needed for 1.4.1 on OS X
                                if (((Frame) ToolsWindowOJ.getInstance()).getMenuBar() != Menus.getMenuBar()) {
                                    ((Frame) ToolsWindowOJ.getInstance()).setMenuBar(Menus.getMenuBar());
                                }
                            }
                        }
                    });
                } else {
                    ToolsWindowOJ.setInstance(new JDialog());
                }

            }
            ToolsWindowOJ.getInstance().setVisible(true);
        }
    };
    
    
    /** called to make the ObjectJ results window visible
     */
    public static ActionListener ResultsViewAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (ProjectResultsOJ.getInstance() == null) {//30.10.2011
                new ProjectResultsOJ();
            }
            ProjectResultsOJ.getInstance().setVisible(true);
            ProjectResultsOJ.getInstance().setState(Frame.NORMAL);
            ProjectResultsOJ.getInstance().toFront();
        }
    };
    /** calls a macro to tell Finder/Explorer to show the project folder
     */
    public static ActionListener ShowProjectFolderAction = new ActionListener() {//7.6.2009

        public void actionPerformed(ActionEvent e) {
            UtilsOJ.showInFinderOrExplorer();
        }
    };
    public static ActionListener ShowEmbeddedMacroAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            EmbeddedMacrosOJ emb = EmbeddedMacrosOJ.getInstance();
            emb.showEmbeddedMacros(e.getModifiers());
            
            oj.OJ.editor.setState(Frame.NORMAL);
            oj.OJ.editor.toFront();
        }
    };
}
