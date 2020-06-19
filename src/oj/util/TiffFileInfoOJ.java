package oj.util;

import ij.io.FileInfo;
import ij.io.RandomAccessStream;
import ij.io.TiffDecoder;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Needed to get Tiff properties such as Voxelsize used by ImageProcessorOJ
 */
public class TiffFileInfoOJ extends TiffDecoder {

    private String dirName;
    private String fileName;

    public TiffFileInfoOJ(String directory, String name) {
        super(directory, name);
        this.dirName = directory;
        this.fileName = name;
    }

    /**
     * Used by ImageProcessorOJ
     */
    public FileInfo getTiffFileInfo() {
        try {
            in = new RandomAccessStream(new RandomAccessFile(new File(dirName, fileName), "r"));

            String ver = ij.IJ.getVersion();
            long ifdOffset = 0;

            ifdOffset = openImageFileHeader();//20.6.2009

            if (ifdOffset < 0) {
                in.close();
                return null;
            }

            if (ifdOffset > 0) {
                in.seek(ifdOffset);
                FileInfo finfo = openIFD();
                in.close();//9.4.2009 -otheriwse file is locked
                return finfo;
            }

            return null;
        } catch (IOException ex) {
            Logger.getLogger(TiffFileInfoOJ.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Trick to make OpenImageFileHeader in Wayne's TiffDecoder accessible though it is protected.
     * @return idf offset(byte 4, 5, 6, 7) to first tag in TIFF
     */
    public long openImageFileHeader() {
        final Method methods[] = TiffDecoder.class.getDeclaredMethods();
        final String mName = "OpenImageFileHeader";
        for (int i = 0; i < methods.length; ++i) {
            if (mName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    String ver = ij.IJ.getVersion();
                    if (ver.compareToIgnoreCase("1.43a") < 0) {
                        return ((Integer) methods[i].invoke(this, (Object[]) null)).intValue();//14.1.2009
                    } else {
                        return ((Long) methods[i].invoke(this, (Object[]) null)).longValue();//20.6.2009
                    }

                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }
                break;
            }
        }
        UtilsOJ.showException(null, mName);//22.6.2009
        return -1;
    }

    /**
     * Trick to make OpenIFD in Wayne's TiffDecoder.java accessible though it is protected.
     * @return FileInfo containing all the image properties such as compression, type, voxel size etc.
     */
    public FileInfo openIFD() {
        final Method methods[] = TiffDecoder.class.getDeclaredMethods();
        final String mName = "OpenIFD";
        for (int i = 0; i < methods.length; ++i) {
            if (mName.equals(methods[i].getName())) {
                methods[i].setAccessible(true);
                try {
                    return (FileInfo) methods[i].invoke(this, (Object[]) null);//14.1.2009
                } catch (Exception ex) {
                    UtilsOJ.showException(ex, mName);//22.6.2009
                }
            }
        }
        UtilsOJ.showException(null, mName);//22.6.2009
        return null;
    }
}
