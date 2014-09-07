
import ij.CommandListener;
import ij.Executer;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.util.Java2;
import java.io.File;
import oj.gui.AboutOJ;
import oj.OJ;
import oj.util.UtilsOJ;
import oj.gui.menuactions.ProjectActionsOJ;
import oj.io.InputOutputOJ;

public class ObjectJ_ implements PlugIn, CommandListener {

    public static int isInstalled = 0;
    public static int busyOpeningProject = 0;

    public void run(String arg) {
        System.out.println("run ObjectJ, arg=" + arg);
        if (isInstalled == 0) {

            isInstalled++;
            if (IJ.getVersion().compareTo("1.44") < 0) {
                IJ.showMessage("Version Error", "For this version of ObjectJ you need to update to ImageJ 1.44 or later");
                return;
            }
            Java2.setSystemLookAndFeel();
            OJ.init();
            IJ.register(oj.OJ.class);
            IJ.showStatus("ImageJ " + IJ.getVersion() + ",  ObjectJ " + OJ.releaseVersion + " (" + OJ.build + "),  Java " + System.getProperty("java.version"));
            Executer.addCommandListener(this);//12.8.2009

        }

        String ojdir = (new File(arg)).getParent();
        String ojName = (new File(arg)).getName();
        String ojType = UtilsOJ.getFileType(ojdir, ojName);

        if (arg.endsWith(".ojj")) {
            if (ojType.contains("magic-ojj") || ojType.contains("isZipped")) {//23.12.2012
                if (busyOpeningProject == 0) {
                    busyOpeningProject = 1;
                    ProjectActionsOJ.openProjectData(ojdir, ojName);//18.8.2009
                    OJ.getData().setDirectory(ojdir + File.separator);
                    InputOutputOJ.setCurrentDirectory(ojdir + File.separator);//28.8.2010
                    OJ.getData().setFilename(ojName);
                    if (isInstalled > 1) {//this shouyldn't happen
                        IJ.showMessage("ObjectJ installed twice");
                    }
                    busyOpeningProject = 0;
                }
            } else {
                ij.IJ.showMessage("Could not open .ojj file (was it an alias?)");
            }
        }

        if (arg.equals("about")) {
            new AboutOJ(IJ.getInstance(), true).setVisible(true);
        }

        if (isInstalled > 1) {
            IJ.showMessage("Installed twice");
        }
    }

    public String commandExecuting(String command) {
        if (IJ.debugMode) {//12.8.2009
            IJ.log("commandExecuting: " + command);
        }

        return command;
    }
}
