/**
 * ProjectActionsOJ.java fully documented 18.5.2010
 *
 * ProjectActionsOJ supplies listener methods that are connected to creating,
 * opening and saving a project file
 */
package oj.gui.menuactions;

import ij.IJ;
import ij.WindowManager;
import ij.gui.YesNoCancelDialog;
import ij.plugin.PluginInstaller;
import ij.plugin.frame.Editor;
import ij.util.Tools;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import oj.OJ;
import oj.project.DataOJ;
import oj.gui.AboutOJ;
import oj.macros.EmbeddedMacrosOJ;
import oj.gui.results.ProjectResultsOJ;
import oj.gui.MenuManagerOJ;
import oj.gui.ToolsWindowOJ;
//import oj.gui.settings.PlotManagerOJ;
import oj.io.InputOutputOJ;
import oj.gui.settings.ProjectSettingsOJ;
import oj.gui.tools.ToolManagerOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.ToolStateOJ;
import oj.project.ImagesOJ;

public class ProjectActionsOJ {

    public static ActionListener NewProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    if (resetProjectData()) {
		OJ.setData(new DataOJ("Untitled"));
		boolean pathMayChange = true;
		if (new InputOutputOJ().saveProjectAs(OJ.getData(), true, pathMayChange)) {
		    MenuManagerOJ.getInstance().setEnabled(true);
		    ViewActionsOJ.SettingsAction.actionPerformed(e);
		    ViewActionsOJ.YtemListAction.actionPerformed(e);
		} else {
		    OJ.setData(null);
		}
	    }
	}
    };
    public static ActionListener OpenProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    if (resetProjectData()) {
		OJ.setData(new InputOutputOJ().loadProjectWithDialog());
		if (OJ.getData() != null) {
		    OJ.getData().getCells().killBadCells();
		    OJ.getData().getCells().sortCells();
		    OJ.getData().setChanged(false);
		    ProjectActionsOJ.initProject();
		}
	    }
	}
    };
    public static ActionListener CloseProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    resetProjectData();
	    Runtime rt = Runtime.getRuntime();
	    rt.gc();
	}
    };
    public static ActionListener AboutObjectJAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    new AboutOJ(IJ.getInstance(), true).setVisible(true);
	}
    };

    public static ActionListener UpdateObjectJAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    try {
		boolean doDailyBuild = IJ.altKeyDown();
		if (doDailyBuild) {
		    IJ.showMessage("alt was down - updating to Daily build");
		}
		String url = OJ.URLcurrent + "objectj_info.txt";

		IJ.runMacro("open('" + url + "');");
		Window win = WindowManager.getActiveWindow();
		if (win instanceof Editor) {
		    String text = ((Editor) win).getText();
		    String[] parts = Tools.split(text, ":\n");
		    String webVersion = parts[1].replace(" ", "");
		    String installedVersion = OJ.releaseVersion;
		    ((Editor) win).close();
		    int i = installedVersion.compareToIgnoreCase(webVersion);
		    char lastChar = installedVersion.charAt(installedVersion.length() - 1);
		    String comment = "";
		    if (lastChar >= '0' && lastChar <= '9') {
			comment = " (beta version)";
		    }
		    if (i == 0) {
			comment = " (up-to-date)";
		    }
		    String msg = "";
		    msg = "You are running   ObjectJ  " + installedVersion + comment;
		    if (doDailyBuild) {
			msg += "\n \nClicking 'OK' will install daily build ";
		    } else {
			msg += "\n \nClicking 'OK' will install version " + webVersion;
		    }
		    boolean doIt = IJ.showMessageWithCancel("Update ObjectJ", msg);
		    if (doIt) {
			updateObjectJ(doDailyBuild);
		    }
		}
	    } catch (Exception ex) {
		IJ.showMessage("No success");
		IJ.log(ex.toString());
	    }
	}
    };

    private static void updateObjectJ(boolean doDailyBuild ) {
	
	try {
	    if (OJ.isProjectOpen && ProjectResultsOJ.getInstance() != null) {
		ProjectResultsOJ.close();
	    }
	    ProjectActionsOJ.resetProjectData();
	    String msg = "Note: you will be asked \nto replace objectj_.jar in Plugins folder.";
	    msg += "\n \nClick OK to continue";
	    if(!IJ.showMessageWithCancel("Updating ObjectJ", msg))
		return;
	    PluginInstaller pi = new PluginInstaller();
	    String url = OJ.URLcurrent + "objectj_.jar";
	    if (doDailyBuild) {
		url = OJ.URL + "/download/all_versions/daily-build/" + "objectj_.jar";
	    }
	    boolean success = pi.install(url);
	    if (success) {
		boolean ok = IJ.showMessageWithCancel("Updating ObjectJ", "You now must quit ImageJ by clicking 'OK'");
		if (ok) {
		    IJ.getInstance().quit();//
		}
	    }
	} catch (Exception ex) {
	    IJ.showMessage("Error 4543: ");
	    IJ.log(ex.toString());
	}
    }

    public static ActionListener ExportEmbeddedMacrosAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    InputOutputOJ.exportEmbeddedMacros();
	}
    };
    public static ActionListener ReplaceEmbeddedMacrosAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    boolean altKey = (e.getModifiers() & 8) > 0;
	    InputOutputOJ.replaceEmbeddedMacros(altKey);
	}
    };
    public static ActionListener SaveProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    saveProjectData();
	}
    };
    public static ActionListener SaveEmptyCopyAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    saveEmptyCopy();
	}
    };
    public static ActionListener SaveCopyAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    saveACopy();
	}
    };
    public static ActionListener SaveProjectAsAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    saveProjectDataAs();
	    //OJ.getEventProcessor().fireImageChangedEvent(ImageChangedEventOJ.IMAGES_SORT);//20.3.2010

	}
    };
    public static ActionListener ForceCloseProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    closeProjectData();
	}
    };
    public static ActionListener DummyProjectAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    IJ.showStatus("dummy");

	}
    };

    public static ActionListener ExportLinkedResultsAction = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    ResultsActionsOJ.exportResultsToText();
	}
    };

    public static boolean openProjectData(String directory, String filename) {
	if (!resetProjectData()) {
	    return false;
	}
	DataOJ data = new InputOutputOJ().loadAProject(directory, filename);
	ij.IJ.showStatus("Loading ObjectJ project " + filename);//4.9.2010

	OJ.setData(data);
	if (OJ.getData() != null) {
	    OJ.getData().getCells().killBadCells();//19.4.2009
	    OJ.getData().getCells().sortCells();
	    OJ.getData().setChanged(false);
	    ProjectActionsOJ.initProject();
	    OJ.getData().setChanged(false);//3.1.2020
	    ij.IJ.showStatus("Loaded project: " + data.getCells().getCellsCount() + " objects in " + data.getImages().getImagesCount() + " image(s)");//24.2.2010
	    return true;
	}
	return false;
    }

    private static void initProject() {
	OJ.getImageProcessor().updateImagesProperties();

	ViewActionsOJ.SettingsAction.actionPerformed(null);
	ViewActionsOJ.YtemListAction.actionPerformed(null);
	try {
	    EmbeddedMacrosOJ emb = new EmbeddedMacrosOJ();
	    emb.loadEmbeddedMacros();
	} catch (Exception ex) {
	    IJ.error("Load macros failed. " + ex.getMessage());
	}

	MenuManagerOJ.getInstance().setEnabled(true);
	OJ.getDataProcessor().qualifyCells();
	OJ.getImageProcessor().applyImageMarkers();
    }

    private static void saveProjectData() {
	new InputOutputOJ().saveProject(OJ.getData(), true);//20.8.2010
	ij.IJ.showStatus("Done...");

	OJ.getData().setChanged(false);
    }

    private static void saveEmptyCopy() {
	new InputOutputOJ().saveEmptyProject(OJ.getData());
	//OJ.getData().setChanged(false);
    }

    private static void saveACopy() {
	new InputOutputOJ().saveACopy(OJ.getData());
	//OJ.getData().setChanged(false);
    }

    private static void saveProjectDataAs() {
	boolean pathMayChange = true;
	if (new InputOutputOJ().saveProjectAs(OJ.getData(), true, pathMayChange)) {//, true
	    if (ProjectSettingsOJ.getInstance() != null) {
		ProjectSettingsOJ.getInstance().setTitle(OJ.getData().getName());
	    }
	    OJ.getData().setChanged(false);
	}
    }

//	private static void saveProjectDataAsXML() {
//		DataOJ data = OJ.getData();
//
//		if (data != null) {
//			String tmpDir = data.getDirectory();
//			String tmpFileName = data.getFilename();
//			String tmpProjectName = data.getName();
//			boolean pathMayChange = true;
//			if (new InputOutputOJ().saveProjectAs(OJ.getData(), false, pathMayChange)) {//20.8.2010
//				if (ProjectSettingsOJ.getInstance() != null) {
//					ProjectSettingsOJ.getInstance().setTitle(OJ.getData().getName());
//				}
//			}
//
//			data.setDirectory(tmpDir);
//			data.setFilename(tmpFileName);
//			data.setName(tmpProjectName);
//		}
//	}
    public static boolean closeProjectData() {
	if (OJ.getData() != null) {
	    ToolStateOJ state = OJ.getToolStateProcessor().getToolStateObject();
	    if (state != null && (state instanceof CreateCellStateOJ)) {
		((CreateCellStateOJ) state).closeCell();//2.11.2013        
	    }
	    /*    
            //6.6.2015
        if (ToolsWindowOJ.getInstance() != null) {
            int toolsX = ToolsWindowOJ.getInstance().getLocationOnScreen().x;
            int toolsY = ToolsWindowOJ.getInstance().getLocationOnScreen().y;
            Prefs.set("objectj.toolsx", "" + toolsX);
            Prefs.set("objectj.toolsy", "" + toolsY);
        }
       if (ProjectSettingsOJ.getInstance() != null) {
            int projectX = ProjectSettingsOJ.getInstance().getLocationOnScreen().x;
            int projectY = ProjectSettingsOJ.getInstance().getLocationOnScreen().y;
            Prefs.set("objectj.projectx", "" + projectX);
            Prefs.set("objectj.projecty", "" + projectY);
        }
	     */

	}

	if ((OJ.getData() != null) && (OJ.getData().getChanged() == true)) {
	    if (ProjectSettingsOJ.getInstance() != null) {
		ProjectSettingsOJ.getInstance().setVisible(true);
	    }
	    String filename = OJ.getData().getFilename();
	    YesNoCancelDialog d = new YesNoCancelDialog(IJ.getInstance(), "Closing Project ", "Save Changes to Project \n\"" + filename + "\" ?");

	    if (d.cancelPressed()) {
		return false;
	    }

	    if (d.yesPressed()) {
		saveProjectData();
	    }
	    OJ.isProjectOpen = false;
	}

	DataOJ data = OJ.getData();
	if (data != null) {
	    ImagesOJ imgs = data.getImages();
	    if (imgs != null) {
		imgs.removeAllImages();
	    }
	}
	return true;
    }

    public static boolean resetProjectData() {
	if (closeProjectData()) {
	    if (OJ.editorWindow != null) {
		OJ.editorWindow.dispose();//24.6.2010
	    }
	    OJ.editorWindow = null;//26.1.2012
	    MenuManagerOJ.close();
	    ToolsWindowOJ.close();
	    ToolManagerOJ.close();

	    ProjectResultsOJ.close();
	    ProjectSettingsOJ.close();

	    OJ.setData(null);
	    OJ.isProjectOpen = false;
	    return true;
	}
	return false;
    }
}
