/* ImagePropertiesOJ.java
 * -- documented
 *
 * Replaces ImageJ's ImageProperties.
 *
 * First, ImageJProperties is called, where the user can change scaling, origin, stack organisation, fps etc.
 * Then, ObjectJ's ImageProcessor is called to forward these changes to the ojj file.
 */
package oj.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.FileInfo;
import ij.plugin.SimpleCommands;
import java.io.File;
import oj.OJ;
import oj.io.InputOutputOJ;
import oj.processor.ImageProcessorOJ;
import oj.project.ImageOJ;
import oj.project.ImagesOJ;
import oj.util.UtilsOJ;

public class SimpleCommandsOJ extends SimpleCommands {

    public void run(String arg) {
        if (arg.equals("rename")) {
            rename();
        } else {
            super.run(arg);
        }

    }

    private void rename() {
        if (OJ.getData() == null) {
            super.run("rename");
            return;
        }
        ImagesOJ imgsOJ = OJ.getData().getImages();
        ImagePlus imp = IJ.getImage();
        if (!imgsOJ.isLinked(imp)) {
            super.run("rename");
            return;
        }
        String errorString = renameImageAndFile(imp.getTitle(), "");
        if (!errorString.equals("")) {
            IJ.showMessage(errorString);
        }
    }

    /**
     * Renames window title and file name of a linked image if oldName == "",
     * frontImage is used if newName == "", dialog is triggered
     */
    public static String renameImageAndFile(String oldName, String newName) {
        if (oldName.equals(newName)) {
            return "";
        }
        ImagesOJ imgs = OJ.getData().getImages();

        ImageOJ img = imgs.getImageByName(oldName);
        if (img == null) {
            return "Old name not found";
        }

        ImagePlus imp = img.getImagePlus();
        String projDir = OJ.getData().getDirectory();

        boolean doSave = false;

        if (newName.equals("")) {
            GenericDialog gd = new GenericDialog("Rename");
            gd.addMessage("Rename Image window and file on disk:");
            gd.addStringField("Title:", oldName, 30);
            gd.addCheckbox("Save project file after renaming", true);
            gd.showDialog();
            if (gd.wasCanceled()) {
                return "Canceled";
            }
            newName = gd.getNextString();
            doSave = gd.getNextBoolean();
        }
        if (oldName.equals(newName)) {
            return "";
        }

        String oldExt = UtilsOJ.getFileExtension(oldName);
        String newExt = UtilsOJ.getFileExtension(newName);
        if (!oldExt.equalsIgnoreCase(newExt)) {
            return "Cannot change file extension ";
        }

        boolean alreadyLinked = imgs.getImageByName(newName) != null;
        File f1 = new File(projDir, oldName);
        File f2 = new File(projDir, newName);
        boolean alreadyOnDisk = f2.exists();

        if (alreadyLinked || alreadyOnDisk) {
            return "\"" + newName + "\" is already used";
        }

        boolean possible = f1.renameTo(f2);//first try
        if (possible) {
            IJ.wait(2);
            f2.renameTo(f1);//undo
        } else {
            //IJ.showMessage("File could not be renamed on disk");
            return "File could not be renamed on disk";
        }
        ImageOJ image = OJ.getData().getImages().getImageByName(oldName);
        image.setName(newName);
        image.setFilename(newName);
        OJ.getData().getImages().updateImageName(oldName, image.getName());
        if (imp != null) {
            FileInfo fi = imp.getOriginalFileInfo();
            if (fi != null) {
                fi.directory = projDir;
                fi.fileName = newName;
                imp.setFileInfo(fi);
            }
            imp.setTitle(newName);
        }
        if (doSave) {
            new InputOutputOJ().saveProject(OJ.getData(), true);
        }
        OJ.getImageProcessor().updateImagesProperties();
        boolean ok = f1.renameTo(f2);
        return "";
    }
}
