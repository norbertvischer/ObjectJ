
import ij.*;
import static ij.ImagePlus.COLOR_RGB;
import ij.process.*;
import ij.gui.*;
import ij.plugin.PlugIn;
import ij.text.TextWindow;

/*This plugin correlates roi contents of different stack slices.
 If no roi exists, entire image is used.
 For example, if user sets parameters chnX =2 and chnY = 3, then:

 if image is a hyperstack, chn 2 and chn 3 of current slice and frame are correlated
 if image is a simple stack, slice 2 and slice 3 are correlated
 if image is RGB image: not allowed

 If "Plot" is checked, a plot will be created using these parameters:

 If "ID" indicates an open window with correct size and type, 
 the new plot will be added to its  contents.

 Otherwise, such an image will be created.
 PlotImage must be a square 32-bit image or stack.

 Many correlation plots need to be recontrasted. Pressing Apple/Shift/C will
 bring up the Brightness/Contrast menu.
 */
public class Roi_Correlator implements PlugIn {

    private static int chanX;
    private static int chanY;
    private static int maxX;
    private static int maxY;
    private static int nBins;
    private static boolean withPlot;
    private int plotID;
    private int z1, z2;
    private Roi roi;
    ImagePlus thisImp;
    static double pearson;

    public void run(String arg) {
        String msg = "";
        thisImp = WindowManager.getCurrentImage();
        if (thisImp == null) {
            IJ.noImage();
            return;
        }
        if (thisImp.getType() == ImagePlus.COLOR_RGB) {
            msg += "Does not work with RGB images\n (perform Image>ColorMake Composite\n";
        }

        if (thisImp.getImageStackSize() < 2) {
            msg += "Stack with >= 2 images expected\n";
        }

        if (!msg.equals("")) {
            ij.Macro.abort();
            IJ.error(msg);
        }

        if (showDialog()) {

            int maxChn;
            if (thisImp.isHyperStack()) {
                maxChn = thisImp.getNChannels();
            } else {
                maxChn = thisImp.getStackSize();
            }
            if (chanX > maxChn || chanY > maxChn) {

                IJ.error("Channel " + chanX + " or " + chanY + " out of range");
                return;
            }

            ImageProcessor plot = null;
            ImagePlus plotImp = null;
            if (withPlot) {
                plotImp = getPlotImage(plotID);
                plot = plotImp.getProcessor();
            }
            pearson = correlate(chanX, maxX, chanY, maxY, nBins, plot);
            if (Double.isNaN(pearson)) {
                return;
            }
            if (plot != null) {
                String label = "pc=" + IJ.d2s(pearson, 3);
                if (plotImp.getStackSize() == 1) {
                    plotImp.setProperty("Label", label);
                } else {
                    plotImp.getStack().setSliceLabel(label, plotImp.getCurrentSlice());
                }
                plotImp.show();
            }
        }
    }

    public boolean showDialog() {

        GenericDialog gd = new GenericDialog("Image Correlator");

        gd.addNumericField("ChannelX: ", 2, 0);
        gd.addNumericField("ChannelY: ", 3, 0);

        gd.addMessage("Optional Plot Parameters:");
        gd.addCheckbox("Create Plot", true);
        gd.addNumericField("Use existing Plot with ID:", 0, 0);
        gd.addNumericField("Bins: ", 100, 0);
        gd.addMessage("Use Max=0 for automatic scaling");
        gd.addNumericField("MaxX: ", 0, 0);
        gd.addNumericField("MaxY: ", 0, 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }
        chanX = (int) gd.getNextNumber();
        chanY = (int) gd.getNextNumber();
        withPlot = gd.getNextBoolean();
        plotID = (int) gd.getNextNumber();
        nBins = (int) gd.getNextNumber();
        maxX = (int) gd.getNextNumber();
        maxY = (int) gd.getNextNumber();
        return true;
    }

    public double correlate(int chnA, int maxA, int chnB, int maxB, int nBins, ImageProcessor plot) {
        int[] dimensions = thisImp.getDimensions();
        int slA = 0;
        int slB = 0;
        if (thisImp.isHyperStack()) {
            int channels = dimensions[2];
            int slices = dimensions[3];
            int frames = dimensions[4];
            int c = thisImp.getChannel();
            int z = thisImp.getSlice();
            int t = thisImp.getFrame();

            slA = (t - 1) * slices * channels + (z - 1) * channels + chnA;
            slB = (t - 1) * slices * channels + (z - 1) * channels + chnB;

            if (slA > thisImp.getImageStackSize() || slB > thisImp.getImageStackSize()) {
                IJ.error("out of range: " + slA + " " + slB);
            }
        } else {
            slA = chnA;
            slB = chnB;
        }

        float[] v1, v2;
        int width = thisImp.getWidth();
        int height = thisImp.getHeight();
        int left = 0;
        int top = 0;
        roi = thisImp.getRoi();
        if (roi != null) {
            left = roi.getBounds().x;
            top = roi.getBounds().y;
            width = roi.getBounds().width;
            height = roi.getBounds().height;
        }

        ImageProcessor ipA = null, ipB = null;
        int index = 0;
        v1 = new float[width * height];
        v2 = new float[width * height];

        ipA = thisImp.getImageStack().getProcessor(slA);
        ipB = thisImp.getImageStack().getProcessor(slB);
        int max1 = 0;
        int max2 = 0;
        for (int y = top; y < top + height; y++) {//Loop for Y-Values
            for (int x = left; x < left + width; x++) {//Loop for X-Values
                boolean inside = (roi == null || roi.contains(x, y));
                if (inside) {
                    z1 = (int) ipA.getPixelValue(x, y); // z-value of pixel (x,y)in stack #1 on slice s
                    z2 = (int) ipB.getPixelValue(x, y); // z-value of pixel (x,y)in stack #2 on slice s
                    if (z1 > max1) {
                        max1 = z1;
                    }
                    if (z2 > max2) {
                        max2 = z2;
                    }
                    v1[index] = z1;
                    v2[index] = z2;

                    index++;
                }
            }
        }
        if (withPlot) {
            if (maxA == 0) {
                maxA = max1;
            }
            if (maxB == 0) {
                maxB = max2;
            }
            for (int jj = 0; jj < index; jj++) {
                int px = (int) (v1[jj] * nBins / maxA);
                int py = nBins - (int) (v2[jj] * nBins / maxB);
                int count = (int) plot.getPixelValue(px, py);
                plot.putPixelValue(px, py, ++count);
            }
        }
        pearson = calculateCorrelation(v1, v2, index);
        IJ.showStatus("PearsonC = " + (float) pearson);
        return pearson;
    }

    // http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
    double calculateCorrelation(float[] x, float[] y, int len
    ) {
        double sumx = 0;
        double sumy = 0;

        for (int i = 0; i < len; i++) {
            sumx += x[i];
            sumy += y[i];
        }
        double xmean = sumx / len;
        double ymean = sumy / len;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += (x[i] - xmean) * (y[i] - ymean);
        }
        double sumx2 = 0;
        for (int i = 0; i < len; i++) {
            sumx2 += sqr(x[i] - xmean);
        }
        double sumy2 = 0;
        for (int i = 0; i < len; i++) {
            sumy2 += sqr(y[i] - ymean);
        }
        return sum / (Math.sqrt(sumx2) * Math.sqrt(sumy2));
    }

    double sqr(double x
    ) {
        return x * x;
    }

    public static String getPearson() {
        return "" + pearson;
    }

    ImagePlus getPlotImage(int id) {
        ImagePlus imp;

        imp = WindowManager.getImage(id);
        if (imp != null && imp.getType() == ImagePlus.GRAY32 && imp.getWidth() == nBins && imp.getHeight() == nBins) {
            return imp;
        }

        FloatProcessor plot = new FloatProcessor(nBins, nBins);
        imp = new ImagePlus("Correlation Plot", plot);
        return imp;
    }
}
