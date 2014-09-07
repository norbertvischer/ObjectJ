/*
 * InputOutputOJ.java
 * -- documented
 *
 * methods to load and save project and results
 */
package oj.io;

import java.util.logging.Level;
import java.util.logging.Logger;
import ij.IJ;
import ij.Macro;
import ij.io.OpenDialog;
import ij.util.Java2;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.project.*;
import oj.io.spi.IIOProviderOJ;
import oj.io.spi.IOFactoryOJ;
import oj.project.results.ColumnsOJ;

public class InputOutputOJ {

    private static String currentDirectory = "";

    /**
     * @return directory containing the ojj file
     */
    public static String getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * directory containing the ojj file
     */
    public static void setCurrentDirectory(String directory) {
        currentDirectory = directory;
    }

    /**
     * currently not used- later we will save the macro only as zip entry
     */
    public void saveMacro(String title, String defaultDir, String defaultName, String content, double unusedSpoiler) {
        Java2.setSystemLookAndFeel();
        JFileChooser fc = new JFileChooser();
        if (defaultDir != null) {
            File f = new File(defaultDir);
            if (f != null) {
                fc.setCurrentDirectory(f);
            }
        }
        if (defaultName != null) {
            fc.setSelectedFile(new File(defaultName));
        }
        int returnVal = fc.showSaveDialog(IJ.getInstance());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        InputOutputOJ.setCurrentDirectory(fc.getCurrentDirectory().getAbsolutePath());
        File f = fc.getSelectedFile();
        if (f.exists()) {
            int ret = JOptionPane.showConfirmDialog(fc, "The file " + f.getName() + " already exists, \nwould you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ret != JOptionPane.OK_OPTION) {
                f = null;
            }
        }
        if (f == null) {
            Macro.abort();
        } else {
            String dir = fc.getCurrentDirectory().getPath() + File.separator;
            String name = fc.getName(f);
            try {
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dir + name)));
                out.writeBytes(content);
            } catch (Exception e) {
                IJ.error("Error 8563: " + e.getMessage());
                return;
            }
        }
    }
//usually there is already a project file that now needs to be overwritten.
//If old project file does not exist, then a warning is issued:
//e.g. the old project file may have been moved or trashed in the Finder.
//now the user can save it under this or a different name.

    /**
     * saves the project in an .ojj file. old version: xml text is saved
     * uncompressed new version: binary and macro are put in a compressed zip
     * archive
     *
     * @return true if successful
     */
    public boolean saveProject(DataOJ data, boolean itsBinary) {
        ij.IJ.showStatus("Saving ObjectJ project...");
        if ((data.getDirectory() != null) && (data.getFilename() != null) && (new File(data.getDirectory(), data.getFilename()).exists())) {
            File f = new File(data.getDirectory(), data.getName() + FileFilterOJ.objectJFileFilter().getExtension());

            if (!oj.OJ.loadedAsBinary) {
                boolean flag = ij.IJ.showMessageWithCancel("Saving Project", "Project file was loaded in XML format, and will now be saved in the newer Binary format.");
                if (!flag) {
                    return false;
                }
                oj.OJ.loadedAsBinary = true;
            }
            if (f.exists()) {
                boolean useBinary = OJ.saveAsBinary;//10.4.2010
                return saveProject(data, data.getDirectory(), data.getFilename(), useBinary);
            } else {
                boolean pathMayChange = false;
                return saveProjectAs(data, itsBinary, pathMayChange);//, true
            }
        } else {
            boolean pathMayChange = true;
            IJ.showMessage("Couldn't relocate project file on disk");
            return saveProjectAs(data, itsBinary, pathMayChange);//, true
        }
    }

    /**
     * Saves a copy of current project for backup
     */
    public boolean saveACopy(DataOJ data) {
        boolean ok = true;
        String tmpName = data.getName();
        String tmpDir = data.getDirectory();
        String tmpFileName = data.getFilename();
        String fName = data.getName();
        if (fName.length() < 20) {
            fName += "-Copy";
        }
        fName += ".ojj";
        String fNameNoExtension = "";
        String dir = "";
        SaveDialogOJ sd = new SaveDialogOJ("Save a copy of project ...", fName, FileFilterOJ.objectJFileFilter());
        if (sd.isApproved()) {
            try {
                fName = sd.getFilename();
                dir = sd.getDirectory();
                int index = fName.lastIndexOf(".");
                fNameNoExtension = fName;
                if (index > 0) {
                    fNameNoExtension = fName.substring(0, index);
                }

            } catch (Exception e) {
                IJ.error("error 5456");
                ok = false;
            }
            if (ok) {
                try {
                    data.setName(fNameNoExtension);
                    IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("javaobject");
                    ioProvider.saveProject(data, dir, fName);

                } catch (Exception e) {
                    IJ.error("error 9843");
                    ok = false;
                }
            }
        }
        //data.setImages(tmpImages);
        //data.setCells(tmpCells);
        data.setName(tmpName);
        data.setDirectory(tmpDir);
        data.setFilename(tmpFileName);
        return ok;
    }

    /**
     * Sets Images and Cells temporarily to zero and saves an empty copy, so it
     * can be used for a similar experiment. All other information is retained:
     * macros, object defs, column defs. Caution: unlinked columns are not
     * emptied.
     */
    public boolean saveEmptyProject(DataOJ data) {
        boolean ok = true;
        CellsOJ tmpCells = data.getCells();
        ImagesOJ tmpImages = data.getImages();
        String tmpName = data.getName();
        String tmpDir = data.getDirectory();
        String tmpFileName = data.getFilename();
        data.setCells(new CellsOJ());
        data.setImages(new ImagesOJ());
        String fName = "Untitled.ojj";
        String fNameNoExtension = "";
        String dir = "";
        SaveDialogOJ sd = new SaveDialogOJ("Save empty copy ...", fName, FileFilterOJ.objectJFileFilter());
        if (sd.isApproved()) {
            try {
                fName = sd.getFilename();
                dir = sd.getDirectory();
                int index = fName.lastIndexOf(".");
                fNameNoExtension = fName;
                if (index > 0) {
                    fNameNoExtension = fName.substring(0, index);
                }

            } catch (Exception e) {
                IJ.error("error 9895");
                ok = false;
            }
            if (ok) {
                try {
                    data.setName(fNameNoExtension);
                    IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("javaobject");
                    ioProvider.saveProject(data, dir, fName);

                } catch (Exception e) {
                    IJ.error("error 8871");
                    ok = false;
                }
            }
        }
        data.setImages(tmpImages);
        data.setCells(tmpCells);
        data.setName(tmpName);
        data.setDirectory(tmpDir);
        data.setFilename(tmpFileName);
        return ok;
    }

    /**
     * will only be used in the new zipped version which contains the macro
     */
    //public boolean saveProjectAs(DataOJ data, boolean itsBinary, boolean saveACopy/*, boolean withContent*/) {
    public boolean saveProjectAs(DataOJ data, boolean itsBinary, boolean pathMayChange) {

        boolean ok = false;

        if (data != null) {
            String oldDir = data.getDirectory();
            String oldFileName = data.getFilename();
            String oldProjectName = data.getName();

            String newDir = "";
            String newFileName = "";
            String newProjectName = "";

            String defaultName = data.getName();
            if ((data.getFilename() != null) && (!data.getFilename().equals(""))) {
                int index = data.getFilename().lastIndexOf(".");
                if (index > 0) {
                    defaultName = data.getFilename().substring(0, index);
                }
            } else {
                defaultName = "Untitled";
            }
            SaveDialogOJ sd;
            if (itsBinary) {
                sd = new SaveDialogOJ("Save project ...", defaultName, FileFilterOJ.objectJFileFilter());
            } else {
                sd = new SaveDialogOJ("Save project ...", defaultName, FileFilterOJ.xmlFileFilter());
            }
            if (sd.isApproved()) {
                try {
                    newFileName = sd.getFilename();
                    newDir = sd.getDirectory();
                    int index = sd.getFilename().lastIndexOf(".");
                    if (index > 0) {
                        newProjectName = sd.getFilename().substring(0, index);
                    }

                    data.setName(newProjectName);
                    ok = saveProject(data, sd.getDirectory(), sd.getFilename(), itsBinary);//30.9.2010

                } catch (Exception e) {
                    IJ.error("OJ_Prefs.txt contains binary=true?");
                    ok = false;
                }
            } else {
                ok = false;
            }

            if (!pathMayChange) {
                data.setDirectory(oldDir);
                data.setFilename(oldFileName);
                data.setName(oldProjectName);
            } else {
                data.setDirectory(newDir);
                data.setFilename(newFileName);
                data.setName(newProjectName);
            }
        }
        OJ.isProjectOpen = true;//4.10.2010
        return ok;

    }

    /**
     * saves current project under same name. depending on flag "asBinary",
     * selects either "xmlstream" or "javaobject" provider, then calls that
     * provider's "saveProject()" method. Before saving, killBadCells() removes
     * cells with zero ytems, and ytems with zero points
     *
     * @return true if successful
     */
    private boolean saveProject(DataOJ data, String directory, String filename, boolean asBinary) {
        //asBinary = false;
        int error = 6004;
        if (data != null) {
            data.getCells().killBadCells();

            data.setDescription(data.xmlComment);

        }
        try {
            data.setFilename(filename);
            data.setDirectory(directory);
            error = 6005;
            data.getResults().recalculate();
            error = 6006;
            data.updateChangeDate();
            if (asBinary) {
                IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("javaobject");
                ioProvider.saveProject(data, directory, filename);
                return true;
            } else {
                IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("xmlstream");
                ioProvider.saveProject(data, directory, filename);
                return true;
            }
        } catch (Exception e) {
            IJ.error("error = " + error + ":  " + e.getMessage());
            return false;
        }
    }

    /**
     * Called by ResultActionsOJ to save results as text
     */
    public void saveResultsAsText(String txt, String defaultName) {
        DataOJ data = oj.OJ.getData();
        if (data != null) {
            OpenDialog.setDefaultDirectory(data.getDirectory());//26.8.2010
        }
        SaveDialogOJ sd = new SaveDialogOJ("Save results ...", defaultName, FileFilterOJ.objectResultTextFileFilter());
        if (sd.isApproved()) {
            try {
                FileWriter fos = new FileWriter(new File(sd.getDirectory(), sd.getFilename()));
                PrintWriter out = new PrintWriter(fos);
                out.println(txt);
                out.close();
            } catch (Exception e) {
                IJ.error("error 3365: " + e.getMessage());
            }

        }
    }

    public void saveIgorAsText(String txt, String dir, String name) {

        try {
            FileWriter fos = new FileWriter(new File(dir, name));
            PrintWriter out = new PrintWriter(fos);
            out.println(txt);
            out.close();
        } catch (Exception e) {
            IJ.error("error 3715: " + e.getMessage());
        }
    }

    /**
     * Asks to load a project
     */
    public DataOJ loadProjectWithDialog() {
        OpenDialogOJ od = new OpenDialogOJ("Open ObjectJ project ...", FileFilterOJ.objectJFileFilter());
        if (od.isApproved()) {
            return loadAProject(od.getDirectory(), od.getFilename());
        } else {
            return null;
        }

    }

    /**
     * Depending on file type, selects either the "xmlstream" or the "javaobject
     * provider, then calls that provider's "loadProject" method.
     *
     * @return dataOJ or null
     */
    public DataOJ loadAProject(String directory, String filename) {
        DataOJ dataOj = null;
        //OJ.doubleBuffered = false; //10.2.2011
        String theType = UtilsOJ.getFileType(directory, filename);
        try {
            if (theType.startsWith("isZipped")) {
                IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("javaobject");
                dataOj = ioProvider.loadProject(directory, filename);
                oj.OJ.loadedAsBinary = true;
                OJ.isProjectOpen = true;
            } else {
                IIOProviderOJ ioProvider = IOFactoryOJ.getFactory().getProvider("xmlstream");
                if (ioProvider.isValidData(directory, filename)) {
                    IJ.showStatus("Please wait while loading project file ...");
                    dataOj = ioProvider.loadProject(directory, filename);
                    oj.OJ.loadedAsBinary = false;
                    OJ.isProjectOpen = true;
                }
            }
        } catch (ProjectIOExceptionOJ ex) {
            Logger.getLogger(InputOutputOJ.class.getName()).log(Level.SEVERE, null, ex);
            IJ.showMessage(ex.getMessage());
            return null;
        }

        if (dataOj != null) {//repair if z=0
            int nCells = dataOj.getCells().getCellsCount();
            int hits = 0;
            boolean repairFlag = false;
            for (int pass = 1; pass <= 2; pass++) {
                for (int jj = 0; jj < nCells; jj++) {
                    CellOJ cell = dataOj.getCells().getCellByIndex(jj);
                    int nYtems = cell.getYtemsCount();
                    for (int ytm = 0; ytm < nYtems; ytm++) {
                        YtemOJ ytem = cell.getYtemByIndex(ytm);
                        int slice = ytem.getStackIndex();
                        for (int ll = 0; ll < ytem.getLocationsCount(); ll++) {
                            double z = ytem.getLocation(ll).z;
                            if (z == 0.0) {
                                hits++;
                                if (repairFlag) {
                                    ytem.getLocation(ll).z = slice;
                                }
                            }
                        }
                    }
                }
                if (pass == 1 && hits > 0) {
                    repairFlag = ij.IJ.showMessageWithCancel("", "Found coordinates z==0; Repair?\n (you should click \"OK\")");
                }
            }
        }

        try {
          dataOj.getResults().getColumns().fixColumnsOrder();//2.2.2014
                    
        } catch (Exception e) {
        }
        return dataOj;
    }

    /**
     *
     * Special exception object for project IO doesn't do anything special
     */
    public static class ProjectIOExceptionOJ extends Exception {

        public ProjectIOExceptionOJ(String message) {
            super(message);
        }
    }
}
