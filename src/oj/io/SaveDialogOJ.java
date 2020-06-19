/*
 * SaveDialogOJ.java
 * -- documented
 *
 * Dialog constructors for saving a project file
 */

package oj.io;

import ij.IJ;
import ij.Prefs;
import ij.io.SaveDialog;
import ij.util.Java2;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class SaveDialogOJ {

    private boolean approved = false;
    private String directory;
    private String filename;

    public SaveDialogOJ(String title, String currentDirectory, String defaultName, FileFilter[] filters) {
        if (Prefs.useJFileChooser) {
            Java2.setSystemLookAndFeel();
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle(title);
            fc.setCurrentDirectory(new File(currentDirectory));
            for (int i = 0; i < filters.length; i++) {
                fc.addChoosableFileFilter(filters[i]);
            }
            int returnVal = fc.showSaveDialog(IJ.getInstance());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                if (f.exists()) {
                    int ret = JOptionPane.showConfirmDialog(fc, "The file " + f.getName() + " already exists, \nwould you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ret != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                approved = true;
                filename = fc.getSelectedFile().getName();
                directory = fc.getCurrentDirectory().getAbsolutePath();
                if (fc.getFileFilter().getClass().isAssignableFrom(FileFilterOJ.class)) {
                    int index = filename.lastIndexOf(".");
                    if (index <= 0) {
                        filename = filename + ((FileFilterOJ) fc.getFileFilter()).getExtension();
                    } else {
                        String extension = filename.substring(index + 1);
                        if (!extension.equals(((FileFilterOJ) fc.getFileFilter()).getExtension())) {
                            filename = filename + ((FileFilterOJ) fc.getFileFilter()).getExtension();
                        }
                    }
                }
                InputOutputOJ.setCurrentDirectory(directory);
            }
        } else {
            SaveDialog sd = new SaveDialog(title, currentDirectory, "");
            approved = ((sd.getFileName() != null) && (!sd.getFileName().equals("")) && (sd.getDirectory() != null));
            if (approved) {
                directory = sd.getDirectory();
                filename = sd.getFileName();
                InputOutputOJ.setCurrentDirectory(directory);
            }
        }
    }

    public SaveDialogOJ(String title, String defaultName, FileFilter[] filters) {
        this(title, InputOutputOJ.getCurrentDirectory(), defaultName, filters);
    }

    public SaveDialogOJ(String title, String currentDirectory, String defaultName, FileFilter filter) {
        if (Prefs.useJFileChooser) {
            Java2.setSystemLookAndFeel();
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle(title);
            fc.setCurrentDirectory(new File(currentDirectory));
            fc.addChoosableFileFilter(filter);
            int returnVal = fc.showSaveDialog(IJ.getInstance());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                if (f.exists()) {
                    int ret = JOptionPane.showConfirmDialog(fc, "The file " + f.getName() + " already exists, \nwould you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ret != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                approved = true;
                directory = fc.getCurrentDirectory().getAbsolutePath();
                filename = fc.getSelectedFile().getName();
                filename = addExtension(filename, ((FileFilterOJ) filter).getExtension());
                InputOutputOJ.setCurrentDirectory(directory);
            }
        } else {
            SaveDialog sd = new SaveDialog(title, currentDirectory, defaultName, ((FileFilterOJ) filter).getExtension());
            approved = (sd.getFileName() != null) && (!sd.getFileName().equals("")) && (sd.getDirectory() != null);
            if (approved) {
                directory = sd.getDirectory();
                filename = sd.getFileName();
                filename = addExtension(filename, ((FileFilterOJ) filter).getExtension());
                InputOutputOJ.setCurrentDirectory(directory);
            }
        }
    }

    public SaveDialogOJ(String title, String defaultName, FileFilter filter) {
        this(title, InputOutputOJ.getCurrentDirectory(), defaultName, filter);
    }

    public SaveDialogOJ(String title, String currentDirectory) {
        this(title, oj.OJ.getData().getFilename(), new FileFilter[0]);//29.4.2010
    }

    public SaveDialogOJ(String title) {
        this(title, InputOutputOJ.getCurrentDirectory());
    }

    public boolean isApproved() {
        return approved;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFilename() {
        return filename;
    }

    private String addExtension(String filename, String ext) {
        int index = filename.lastIndexOf(".");
        if (index < 0) {
            return filename + ext;
        } else {
            String fext = filename.substring(index);
            if (!fext.equals(ext)) {
                return filename + ext;
            } else {
                return filename;
            }
        }
    }
}