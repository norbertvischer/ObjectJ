package ij.plugin.filter;

import ij.*;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.*;
import ij.process.*;
import java.awt.*;
import ij.gui.*;
import ij.measure.*;

public class Palm_5 implements PlugInFilter {

    int tolerance = 400;
    boolean opAdd = true;
    String rangeStr = "All Slices";
    int sliceRange = 100;
    ImagePlus imp;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_8G + DOES_16 + SUPPORTS_MASKING;
    }

    public void run(ImageProcessor ip) {
        findTheMaxima(ip);
    }

    void showAbout() {
        IJ.showMessage("About Palm_...", "Palm\n");
    }

    boolean readParameters() {
        GenericDialog gd = new GenericDialog("Maximum Finder Options");
        gd.addNumericField("Tolerance", tolerance, 0);
        gd.addChoice("Summing operation", new String[]{"Add", "Binary OR"}, "Add");
        gd.addChoice("Slice Range", new String[]{"All Slices", "1 Slice", "2 Slices", "3 Slices", "5 Slices", "10 Slices", "30 Slices", "100 Slices", "300 Slices", "1000 Slices"}, "All Slices");
        //gd.addNumericField("Neighbor slices +/-:", neighborRange, 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }
        tolerance = (int) gd.getNextNumber();
        opAdd = (gd.getNextChoice().equals("Add"));
        rangeStr = gd.getNextChoice();
        String[] options = rangeStr.split(" ");
        if (options[0].equals("All")) {
            sliceRange = -1;
        } else {
            sliceRange = Integer.parseInt(options[0]);
        }
        return true;


    }

    void findTheMaxima(ImageProcessor ip) {
        ImagePlus imp = WindowManager.getCurrentImage();
        Roi roi = imp.getRoi();

        if (!readParameters()) {
            return;
        }


        int nSlices = imp.getNSlices();
        int thisSlc = imp.getSlice();
        int slcStart = thisSlc;
        int slcStop = thisSlc;
        if (sliceRange == -1) {
            slcStart = 1;
            slcStop = nSlices;
        } else {
            slcStop = thisSlc + sliceRange - 1;
        }

        if (slcStop > nSlices) {
            slcStop = nSlices;
        }

        double threshold = ImageProcessor.NO_THRESHOLD;
        int outputType = MaximumFinder.SINGLE_POINTS;
        boolean excludeOnEdges = false;
        MaximumFinder maxF = new MaximumFinder();
        ImageProcessor singleIP = maxF.findMaxima(ip, tolerance, threshold, outputType, excludeOnEdges, false); //just to get empty 8-bit image
        singleIP.and(0);//just to get empty image

        ImageProcessor accuIP = maxF.findMaxima(ip, tolerance, threshold, outputType, excludeOnEdges, false); //just to get empty 8-bit image
        accuIP.and(0);//just to get empty image

        ImagePlus singleImp = new ImagePlus("Single-slice Maxima", singleIP);
        ImagePlus accuImp = new ImagePlus("Accu Maxima", accuIP);
        
        ImageCalculator ical = new ImageCalculator();
        for (int slc = slcStart; slc <= slcStop; slc++) {
            ImageProcessor srcIp = imp.getImageStack().getProcessor(slc);
            singleIP = maxF.findMaxima(srcIp, tolerance, threshold, outputType, excludeOnEdges, false); //process the image
            singleIP.and(1);
            singleImp.setProcessor(null, singleIP);
            singleImp.show();
            //IJ.wait(2000);
            ical.run("add", accuImp, singleImp);

        }
        new ContrastEnhancer().stretchHistogram(accuIP, 0.5);
        accuImp.show();
        imp.updateAndDraw();
        imp.unlock();
        IJ.showStatus("");
    }
}








