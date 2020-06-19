/*
 * OpenDialogOJ.java
 * -- documented
 *
 * (too) many constuctors that call FileDialog for opening a file
 */

package oj.io;

import ij.IJ;
import ij.Prefs;
import ij.util.Java2;
import java.awt.FileDialog;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class OpenDialogOJ {
    
    private boolean approved;
    private String directory;
    private String filename;
    
    public OpenDialogOJ(String title, String currentDirectory, FileFilter[] filters) {
        if (Prefs.useJFileChooser) {
            Java2.setSystemLookAndFeel();
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle(title);
            fc.setCurrentDirectory(new File(currentDirectory));
            for (int i = 0; i < filters.length; i++) {
                fc.addChoosableFileFilter(filters[i]);
            }
            approved = (fc.showOpenDialog(IJ.getInstance()) == JFileChooser.APPROVE_OPTION);
            if (approved) {
                directory = fc.getCurrentDirectory().getAbsolutePath();
                filename = fc.getSelectedFile().getName();
                InputOutputOJ.setCurrentDirectory(directory);
            }
        } else {
            FileDialog fd = new FileDialog(IJ.getInstance(), title);
            fd.setDirectory(currentDirectory);
            fd.setVisible(true);
            
            approved = ((fd.getFile() != null) && (!fd.getFile().equals("")) && (fd.getDirectory() != null));
            if (approved) {
                directory = fd.getDirectory();
                filename = fd.getFile();
                InputOutputOJ.setCurrentDirectory(directory);
            }
        }
    }
    
    public OpenDialogOJ(String title, FileFilter[] filters) {
        this(title, InputOutputOJ.getCurrentDirectory(), filters);
    }
    
    public OpenDialogOJ(String title, String currentDirectory, FileFilter filter) {
        if (Prefs.useJFileChooser) {
            Java2.setSystemLookAndFeel();
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle(title);
            fc.setCurrentDirectory(new File(currentDirectory));
            fc.addChoosableFileFilter(filter);
            approved = (fc.showOpenDialog(IJ.getInstance()) == JFileChooser.APPROVE_OPTION);
            if (approved) {
                directory = fc.getCurrentDirectory().getAbsolutePath();
                filename = fc.getSelectedFile().getName();
                InputOutputOJ.setCurrentDirectory(directory);
            }
        } else {
            FileDialog fd = new FileDialog(IJ.getInstance(), title);
            fd.setDirectory(currentDirectory);
            fd.setVisible(true);
            
            approved = ((fd.getFile() != null) && (!fd.getFile().equals("")) && (fd.getDirectory() != null));
            if (approved) {
                directory = fd.getDirectory();
                filename = fd.getFile();
                InputOutputOJ.setCurrentDirectory(directory);
            }
        }
    }
    
    public OpenDialogOJ(String title, FileFilter filter) {
        this(title, InputOutputOJ.getCurrentDirectory(), filter);
    }
    
    public OpenDialogOJ(String title, String currentDirectory) {
        this(title, currentDirectory, new FileFilter[0]);
    }
    
    public OpenDialogOJ(String title) {
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
}