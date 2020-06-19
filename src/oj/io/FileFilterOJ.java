/*
 * FileFilterOJ.java
 *
 * used to check for file extension - this could be simplified
 */
package oj.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileFilterOJ extends FileFilter {

    private String fileExtension;
    private String fileDescription;

    /** Creates a new instance of FileFilterOJ */
    public FileFilterOJ(String fileExtension, String fileDescription) {
        this.fileExtension = fileExtension;
        this.fileDescription = fileDescription;
    }

    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return false;
        }
        if (f.isHidden()) {
            return false;
        }
        int index = f.getName().lastIndexOf(".");
        if (index > 0) {
            String extension = f.getName().substring(index);
            if (extension.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(String f) {
        int index = f.lastIndexOf(".");
        if (index > 0) {
            String extension = f.substring(index);
            if (extension.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     * @see FileView#getName
     */
    public String getDescription() {
        return fileDescription;
    }

    public String getExtension() {
        return fileExtension;
    }

    public static FileFilterOJ objectJFileFilter() {
        return new FileFilterOJ(".ojj", "ObjectJ project files (*.ojj)");
    }

    public static FileFilterOJ xmlFileFilter() {
        return new FileFilterOJ(".xml", "ObjectJ xml files (*.xml)");//20.8.2010
    }

    public static FileFilterOJ objectResultTextFileFilter() {
        return new FileFilterOJ(".txt", "ObjectJ result files (*.txt)");
    }
}
