/**
 * ProjectActionsOJ.java fully documented 18.5.2010
 *
 * ProjectActionsOJ supplies listener methods that are connected to creating,
 * opening and saving a project file
 */
package oj.gui.menuactions;

import ij.IJ;
import ij.gui.YesNoCancelDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import oj.OJ;
import oj.project.DataOJ;
import oj.gui.AboutOJ;
import oj.macros.EmbeddedMacrosOJ;
import oj.gui.results.ProjectResultsOJ;
import oj.gui.MenuManagerOJ;
import oj.gui.ToolsWindowOJ;
import oj.io.InputOutputOJ;
import oj.gui.settings.ProjectSettingsOJ;
import oj.gui.tools.ToolManagerOJ;
import oj.plugin.GlassWindowOJ;
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
    /**
     * saves data (without macros) as old XML file
     */
    public static ActionListener ExportAsXMLAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            oj.OJ.saveAsBinary = false;//29.4.2010
            saveProjectDataAsXML();
            oj.OJ.saveAsBinary = true;
            IJ.showStatus("Exporting as XML");
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

    private static void saveProjectDataAsXML() {
        DataOJ data = OJ.getData();

        if (data != null) {
            String tmpDir = data.getDirectory();
            String tmpFileName = data.getFilename();
            String tmpProjectName = data.getName();
            boolean pathMayChange = true;
            if (new InputOutputOJ().saveProjectAs(OJ.getData(), false, pathMayChange)) {//20.8.2010
                if (ProjectSettingsOJ.getInstance() != null) {
                    ProjectSettingsOJ.getInstance().setTitle(OJ.getData().getName());
                }
            }

            data.setDirectory(tmpDir);
            data.setFilename(tmpFileName);
            data.setName(tmpProjectName);
        }
    }

    private static boolean closeProjectData() {
        if (OJ.getData() != null) {
            ToolStateOJ state = OJ.getToolStateProcessor().getToolStateObject();
            if (state != null && (state instanceof CreateCellStateOJ)) {
                ((CreateCellStateOJ) state).closeCell();//2.11.2013        
            }
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

            DataOJ data = OJ.getData();
            if (data != null) {
                ImagesOJ imgs = data.getImages();
                if (imgs != null) {
                    imgs.removeAllImages();
                }
            }
            OJ.isProjectOpen = false;
        }

        return true;


    }

    static boolean resetProjectData() {
        if (GlassWindowOJ.exists()) {
            IJ.runMacro("ojGlassWindow(\"hide\");");//15.10.2012
        }
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
