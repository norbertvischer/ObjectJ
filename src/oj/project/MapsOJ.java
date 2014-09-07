package oj.project;

import ij.ImagePlus;
import ij.VirtualStack;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.RoiEncoder;
import ij.io.TiffEncoder;
import ij.plugin.*;
import ij.IJ;
import ij.io.Opener;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MapsOJ implements PlugIn {

    public void run(String arg) {
        saveAsZip();
    }

    public void saveAsZip() {
        ImagePlus imp = ij.IJ.getImage();
        FileSaver fs = new FileSaver(imp);

	FileInfo fi = imp.getFileInfo();
        fi.description = fs.getDescriptionString();
        Object info = imp.getProperty("Info");
        if (info != null && (info instanceof String)) {
            fi.info = (String) info;
        }
        fi.roi = RoiEncoder.saveAsByteArray(imp.getRoi());
        //fi.overlay = fs.getOverlay(imp);
        fi.sliceLabels = imp.getStack().getSliceLabels();
        if (imp.isComposite()) {
            fs.saveDisplayRangesAndLuts(imp, fi);
        }
        if (fi.nImages > 1 && imp.getStack().isVirtual()) {
            fi.virtualStack = (VirtualStack) imp.getStack();
        }
        String path = "/Users/norbert/Desktop/abc/abc_.zip";
        String name = "MyImage.tif";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(fos);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(zos));
            zos.putNextEntry(new ZipEntry(name));
            TiffEncoder te = new TiffEncoder(fi);
            ij.IJ.log("a");
            te.write(out);
             ij.IJ.log("b");
           out.close();
        } catch (IOException e) {
            IJ.showMessage("error.....");

        }

    }

    public void openZip(){
    String path = "/Users/norbert/Desktop/abc/abc_.zip";
    
    	//public ImagePlus openZip(String path) {
		ImagePlus imp = null;
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
			if (zis==null) return ;
			ZipEntry entry = zis.getNextEntry();
			if (entry==null) return ;
			String name = entry.getName();
			
			if (name.endsWith(".tif")) {
				imp = new Opener().openTiff(zis, name);
			}  else {
				zis.close();
				IJ.error("This ZIP archive does not appear to contain a \nTIFF (\".tif\") or DICOM (\".dcm\") file, or ROIs (\".roi\").");
				return ;
			}
		} catch (Exception e) {
			IJ.error("ZipDecoder", ""+e);
			return ;
		}
		File f = new File(path);
		FileInfo fi = imp.getOriginalFileInfo();
		fi.fileFormat = FileInfo.ZIP_ARCHIVE;
		fi.fileName = f.getName();
		fi.directory = f.getParent()+File.separator;
		imp.show();
	}
}
