
import ij.ImagePlus;
import ij.VirtualStack;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.RoiEncoder;
import ij.io.TiffEncoder;
import ij.plugin.*;
import ij.IJ;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip_Test implements PlugIn {

    public void run(String arg) {
        saveHelloWorldToZip();
        loadHelloWorldFromZip();
    }

    void saveHelloWorldToZip() {
        ImagePlus imp = ij.IJ.getImage();
        FileSaver fs = new FileSaver(imp);

        FileInfo fi = new FileInfo();
        fi.description = fs.getDescriptionString();
        Object info = imp.getProperty("Info");
        if (info != null && (info instanceof String)) {
            fi.info = (String) info;
        }
        fi.roi = RoiEncoder.saveAsByteArray(imp.getRoi());
        //fi.overlay = fs.getOverlay(imp);
        //fi.sliceLabels = imp.getStack().getSliceLabels();
        if (imp.isComposite()) {
            fs.saveDisplayRangesAndLuts(imp, fi);
        }
        if (fi.nImages > 1 && imp.getStack().isVirtual()) {
            fi.virtualStack = (VirtualStack) imp.getStack();
        }
        String path = "/Users/norbert/Desktop/";
        String name = "abc_.zip";
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(zos));
            zos.putNextEntry(new ZipEntry(name));
            TiffEncoder te = new TiffEncoder(fi);
            te.write(out);
            out.close();
        } catch (IOException e) {
            IJ.showMessage("hello");
            //return false;
        }
        //updateImp(fi, FileInfo.TIFF);
        //return true;

    }

    void loadHelloWorldFromZip() {
        //still empty
    }
}
//
//
//
//
//        FileInfo myDescription = fs.getDescriptionString();
//        Object info = imp.getProperty("Info");
//        if (info != null && (info instanceof String)) {
//            fi.info = (String) info;
//        }
//        fi.roi = RoiEncoder.saveAsByteArray(imp.getRoi());
//        fi.overlay = getOverlay(imp);
//        fi.sliceLabels = imp.getStack().getSliceLabels();
//        if (imp.isComposite()) {
//            saveDisplayRangesAndLuts(imp, fi);
//        }
//        if (fi.nImages > 1 && imp.getStack().isVirtual()) {
//            fi.virtualStack = (VirtualStack) imp.getStack();
//        }
//        try {
//            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path));
//            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(zos));
//            zos.putNextEntry(new ZipEntry(name));
//            TiffEncoder te = new TiffEncoder(fi);
//            te.write(out);
//            out.close();
//        } catch (IOException e) {
//            showErrorMessage(e);
//            return false;
//        }
//        ZipOutputStream zipOut = null;
//        File newOjjFile = new File("/Users/norbert/Desktop/", "MyZipTest.zip");
//        ByteArrayOutputStream myStream = new ByteArrayOutputStream();
//        try {
//            zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(newOjjFile)));
//            zipOut.putNextEntry(new ZipEntry("Hello World.txt"));
//            byte[] textBytes = "Hello World".getBytes();
//            myStream.write(textBytes);
//            myStream.writeTo(zipOut);
//        } catch (IOException ex) {
//        }
//        try {
//            zipOut.close();
//        } catch (IOException ex) {
//        }

