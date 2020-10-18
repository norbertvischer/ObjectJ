
import ij.*;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.Color;

/* This plugin, in combination with macro  CorrelateWithMask-6,  allows to correlate 2 images,
 * and to make selections in the resulting 2D histogram to highlight the corresponding pixels
 * in the source image pair (two different colors can be used).
 *
 */
public class Image_Correlator6 implements PlugInFilter {

    //private int z1, z2, count;
    private ImageProcessor ip1, ip2, /*redMaskIp,*/ maskIp, plotIP;
    ImagePlus imp, srcImp, plotImp;
    Roi[] roiArr;
    int id = 0, plotID = 0, srcID = 0;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_8G + DOES_8C + DOES_16 + DOES_32;
    }

    void showAbout() {
        IJ.showMessage("Image_Correlator6 \n-\n09-jan-2012 \nNorbert Vischer\nUniversity of Amsterdam");
    }

    public void run(ImageProcessor ip) {
        String arg = Macro.getOptions();
        if (arg == null) {
            showAbout();
        }
        String[] options = null;
        if (arg != null) {
            if (arg.startsWith("about")) {
                showAbout();
                return;
            }
            options = arg.split(" ");
        }
        int nBins = 256;
        double xMin = 0, xMax = 0, yMin = 0, yMax = 0;


        int k60 = 60;
        int k15 = 15;
        if (options != null) {
            for (int jj = 0; jj < options.length; jj++) {
                if (options[jj].startsWith("srcID=")) {
                    srcID = Integer.parseInt(options[jj].substring("srcID=".length()));
                }
                if (options[jj].startsWith("plotID=")) {
                    plotID = Integer.parseInt(options[jj].substring("plotID=".length()));
                }
                if (options[jj].startsWith("xmin=")) {
                    xMin = Double.parseDouble(options[jj].substring("xmin=".length()));
                }
                if (options[jj].startsWith("xmax=")) {
                    xMax = Double.parseDouble(options[jj].substring("xmax=".length()));
                }
                if (options[jj].startsWith("ymin=")) {
                    yMin = Double.parseDouble(options[jj].substring("ymin=".length()));
                }
                if (options[jj].startsWith("ymax=")) {
                    yMax = Double.parseDouble(options[jj].substring("ymax=".length()));
                }
                if (options[jj].startsWith("nbins=")) {
                    nBins = Integer.parseInt(options[jj].substring("nbins=".length()));
                }

            }
        }


//        String fromTxt = "ID SRC   =";//use substring(10 ..
//        String toTxt = "ID PLOT  =";
        //String title = imp.getTitle();
        //title = title + "";

        srcImp = WindowManager.getImage(srcID);
        if (srcImp == null) {
            srcImp = imp;
        }
        //int scrDepth = srcImp.getBitDepth();



        plotImp = WindowManager.getImage(plotID);
        if (plotImp == null) {
            return;
        }
        plotIP = plotImp.getProcessor();

        if (imp.getOverlay() != null) {
            roiArr = imp.getOverlay().toArray();
        }



        int nChannels = srcImp.getNChannels();


        int width = srcImp.getWidth();
        int height = srcImp.getHeight();

        nChannels = srcImp.getNChannels();
        maskIp = srcImp.getImageStack().getProcessor(nChannels);

        double ch1Val, ch2Val;
        int count;



        ip1 = srcImp.getImageStack().getProcessor(1);
        ip2 = srcImp.getImageStack().getProcessor(2);

//        double xMin = ip1.getStatistics().min;
//        double xMax = ip1.getStatistics().max;
//        double yMin = ip2.getStatistics().min;
//        double yMax = ip2.getStatistics().max;


        for (int y = 0; y < height; y++) {//Loop for Y-Values
            for (int x = 0; x < width; x++) {//Loop for X-Values
                ch1Val = ip1.getPixelValue(x, y);
                ch2Val = ip2.getPixelValue(x, y);
                if (ch1Val >= xMin && ch1Val <= xMax && ch2Val >= yMin && ch2Val <= yMax) {

                    // now convert value to bin#:

                    int binX = (int) (nBins * (ch1Val - xMin) / (xMax - xMin));


                    int binY = (int) (nBins * (ch2Val - yMin) / (yMax - yMin));
                    int binYfromTop = nBins - binY - 1;

                    if (binX > 0 && binY > 0) {
                        count = (int) plotIP.getPixelValue(k60 + binX, k15 + binYfromTop);
                        count++;
                        plotIP.putPixelValue(k60 + binX, k15 + binYfromTop, count);
                        if (roiArr != null) {
                            int red = 0, green = 0, blue = 0;
                            for (int rr = 0; rr < roiArr.length; rr++) {
                                if (roiArr[rr].contains(k60 + binX, k15 + binYfromTop)) {
                                    Color color = roiArr[rr].getFillColor();
                                    if (color != null) {
                                        int rot = color.getRed();
                                        int gruen = color.getGreen();
                                        int blau = color.getBlue();
                                        if (rot != 0) {
                                            red = 255;
                                        }
                                        if (gruen != 0) {
                                            green = 255;
                                        }
                                        if (blau != 0) {
                                            blue = 255;
                                        }
                                    }
                                }
                            }
                            //redMaskIp.putPixel(x, y, red);
                            maskIp.putPixel(x, y, green);
                        }
                    }
                }

            }
        }

        srcImp.show();
        plotImp.show();
        IJ.selectWindow(plotImp.getID());
    }
}
