
import ij.measure.*;
import ij.*;
import ij.plugin.MacroInstaller;
import ij.plugin.PlugIn;
import ij.process.*;
import java.awt.Polygon;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author N.Vischer
 * 15.4.2013
 */
public class SIPchart_ implements PlugIn, Measurements {

  ImagePlus imp;
  byte[] reds = new byte[256];
  byte[] greens = new byte[256];
  byte[] blues = new byte[256];
  double pxDepth = 0;
  double pxWidth = 0;
  double backGround = 0;

  
  public void run(String arg) {



    int x = -1;
    int y = -1;
    String options = Macro.getOptions();
    if (options == null) {
      IJ.showMessage("SIPchart macros are now installed as tool menu  \"Sip\" and tool \"PL\"\n \nv1.18 \n(15-apr-2013)\n ");
      installSIPMacro();
    } else {
      imp = IJ.getImage();
      makeSipChart(imp);
    }
  }

  public void makeSipChart(ImagePlus impSrc) {

//First create the results stack
    String makeResultsStack = "run(\"Hyperstack...\", \"title=HyperStack type=32-bit display=Color width=64 height=64 channels=5 slices=1 frames=1\");";
    String s = IJ.runMacro(makeResultsStack);//don't check return value
    ImagePlus resultsImp = WindowManager.getCurrentImage();
    ImageStack resultsStack = resultsImp.getImageStack();
    String[] labels = "ISum IMax ZMax FWHM Skew".split(" ");
    int nPanels5 = labels.length;
    for (int jj = 0; jj < nPanels5; jj++) {
      resultsStack.setSliceLabel(labels[jj], jj + 1);
      resultsImp.setPosition(jj + 1);
      if (jj < 4) {
        makeRainbow();
      }
      if (jj == 4) {
        makeRedBlue();
      }

      LUT lut = new LUT(8, 256, reds, greens, blues);
      ((CompositeImage) resultsImp).setChannelLut(lut);


    }
    ImageStack srcStack = impSrc.getStack();
    int zSize = srcStack.getSize();
    ImageProcessor ip = srcStack.getProcessor(1);
    int backGround1 = (int) ip.getStatistics().min;
    ip = srcStack.getProcessor(zSize);
    backGround = (int) ip.getStatistics().min;
    if (backGround > backGround1) {
      backGround = backGround1;
    }
//    int width = ip.getWidth();
//    int height = ip.getHeight();
    pxDepth = impSrc.getCalibration().pixelDepth;
    pxWidth = impSrc.getCalibration().pixelWidth;


    double[] zProfileD = new double[zSize];
    float zProfileF[] = new float[zSize];
    int[] indexes = new int[zSize];
    for (int yy = 0; yy < 64; yy++) {
      IJ.showProgress(yy, 63);
      for (int xx = 0; xx < 64; xx++) {
        for (int slice = 1; slice <= zSize; slice++) {
          ip = srcStack.getProcessor(slice);
          zProfileD[slice - 1] = ip.getPixel(xx, yy);//create the profile
          indexes[slice - 1] = slice - 1;//0..99
          zProfileF[slice - 1] = (float) zProfileD[slice - 1];
        }
        double[] fitResults = simpleFit(zProfileD, backGround);
        for (int panel = 0; panel < nPanels5; panel++) {
          ImageProcessor resultIP = resultsStack.getProcessor(panel + 1);
          resultIP.putPixelValue(xx, yy, fitResults[panel]);

        }
      }
    }
  }

  void makeRainbow() {
    for (int jj = 0; jj < 85; jj++) {
      byte val = (byte) Math.round((255.0 - 64) * jj / 85);

      reds[jj] = 64;
      greens[jj] = (byte) (64 + val);
      blues[jj] = (byte) (255 - val);

      reds[jj + 85] = (byte) (64 + val);
      greens[jj + 85] = (byte) 255;
      blues[jj + 85] = 64;

      reds[jj + 170] = (byte) 255;
      greens[jj + 170] = (byte) (255 - val);
      blues[jj + 170] = 64;
    }
    reds[255] = (byte) 255;
    greens[255] = 64;
    blues[255] = 64;
  }

  void makeRedBlue() {
    for (int jj = 0; jj < 128; jj++) {
      reds[jj] = (byte) Math.round(64.0 + jj / 128.0 * (255 - 64));
      greens[jj] = (byte) (reds[jj]);
      blues[jj] = (byte) 255;

      reds[255 - jj] = blues[jj];
      blues[255 - jj] = reds[jj];
      greens[255 - jj] = greens[jj];
    }
  }
  /*
   * processes a single z-profile;
   * subtract background, calculate center of gravity of upper 25% height:
   * centerx becomes peak position, and centerheight/0.85 becomes peak height.
   * points where the profile passes the 50% of the peak is used for FWHM.
   */

  public double[] simpleFit(double[] photons, double backgnd) {

    double sum = 0;
    int nSlices = photons.length;

    double valGold = 0;//largest value
    int indexGold = 0;

    for (int z = 0; z < nSlices; z++) {
      double val = photons[z] - backgnd;
      photons[z] = val;
      if (val >= valGold) {
        valGold = val;
        indexGold = z;
      }
      sum += val;
    }

    double thr50 = valGold * 0.5;
    double thr75 = valGold * 0.75;


    //Approach peak from left, then from right
    int leftLowX = 0;
    int leftHiX = 0;
    int rightLowX = 0;
    int rightHiX = 0;

    double leftLowY = 0;
    double leftHiY = 0;
    double rightLowY = 0;
    double rightHiY = 0;
    double left75X = 0;
    double right75X = 0;
    int firstAbove75 = 0;
    int lastAbove75 = 0;

    for (int x = 0; x <= indexGold; x++) {
      double val = photons[x];
      if (val < thr50) {
        leftLowX = x;
        leftLowY = val;
      }
      if (val > thr50 && leftHiY == 0) {
        leftHiX = x;
        leftHiY = val;
      }
      if (val > thr75 && left75X == 0) {
        double y0 = photons[x - 1];
        double y1 = photons[x];
        double x0 = x - 1;
        double x1 = x;
        double fraction = (thr75 - y0) / (y1 - y0);
        left75X = x0 + fraction * (x1 - x0);
        firstAbove75 = x;
      }
    }
    for (int x = nSlices - 1; x >= indexGold; x--) {
      double val = photons[x];
      if (val < thr50) {
        rightLowX = x;
        rightLowY = val;
      }
      if (val > thr50 && rightHiY == 0) {
        rightHiX = x;
        rightHiY = val;
      }
      if (val > thr75 && right75X == 0) {
        double y0 = photons[x + 1];
        double y1 = photons[x];
        double x0 = x + 1;
        double x1 = x;
        double fraction = (thr75 - y0) / (y1 - y0);
        right75X = x0 + fraction * (x1 - x0);
        lastAbove75 = x;
      }


    }

    //create polygon above 75%
    int size = lastAbove75 - firstAbove75 + 1 + 2;

    double[] polygon75x = new double[size];
    double[] polygon75y = new double[size];
    for (int jj = 0; jj < size; jj++) {
      int index = firstAbove75 + jj;
      polygon75x[jj] = index;
      polygon75y[jj] = photons[index];

    }
    polygon75x[size - 2] = right75X;
    polygon75y[size - 2] = thr75;
    polygon75x[size - 1] = left75X;
    polygon75y[size - 1] = thr75;
    float[] centers = PolygonCenterOfMass(polygon75x, polygon75y);
    double peakPosX = centers[0];
    valGold = centers[1] / 0.85;//center of gravity of 75%..100%% of a circle

    int dx = leftHiX - leftLowX;
    double dy = leftHiY - leftLowY;
    double xRising50 = leftLowX + (thr50 - leftLowY) / dy * dx;// left 50% transition

    dx = rightHiX - rightLowX;
    dy = -rightHiY + rightLowY;
    double xFalling50 = rightLowX - (thr50 - rightLowY) / dy * dx;// right 50% transition

    double fwhm = xFalling50 - xRising50;
    double lwhm = peakPosX - xRising50;
    double rwhm = xFalling50 - peakPosX;
    double skew = (rwhm - lwhm) / fwhm;

    double[] results = new double[5];
    results[0] = sum;
    results[1] = valGold;
    results[2] = peakPosX * pxDepth;
    results[3] = fwhm * pxDepth;
    results[4] = skew;
    return results;

  }

  /** METHODS TO CALCULATE THE AREA AND CENTROID OF A POLYGON
  INSERT THEM INTO THE CORRESPONDING CLASS 
  http://paulbourke.net/geometry/polyarea/**/
  public double SignedPolygonArea(double[] xx, double[] yy) {
    int N = xx.length;
    Polygon P;
    int i, j;
    double area = 0;

    for (i = 0; i < N; i++) {
      j = (i + 1) % N;
      area += xx[i] * yy[j];
      area -= yy[i] * xx[j];
    }
    area /= 2.0;
    return area;
  }

  /* CENTROID */
  public float[] PolygonCenterOfMass(double[] xx, double[] yy) {
    int N = xx.length;
    float cx = 0, cy = 0;
    float area = (float) SignedPolygonArea(xx, yy);
    float[] res = new float[2];
    int i, j;

    double factor = 0;
    for (i = 0; i < N; i++) {
      j = (i + 1) % N;
      factor = (xx[i] * yy[j] - xx[j] * yy[i]);
      cx += (xx[i] + xx[j]) * factor;
      cy += (yy[i] + yy[j]) * factor;
    }
    area *= 6.0f;
    factor = 1 / area;
    cx *= factor;
    cy *= factor;
    res[0] = cx;
    res[1] = cy;
    return res;
  }


  /** Opens the .txt from a JAR file and returns it as an InputStream. */
  void installSIPMacro() {
    String pluginsDir = Menus.getPlugInsPath();
    String jarPath = pluginsDir + "SIPchart_.jar";
    String text;
    try {
      ZipFile jarFile = new ZipFile(jarPath);
      Enumeration entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        if (entry.getName().endsWith(".txt")) {
          InputStream is = jarFile.getInputStream(entry);

          if (is == null) {
            return;
          }
          InputStreamReader isr = new InputStreamReader(is);
          StringBuilder sb = new StringBuilder();
          char[] b = new char[8192];//read in 8k chunks
          int n;
          while ((n = isr.read(b)) > 0) {
            sb.append(b, 0, n);
          }
          text = sb.toString();

          new MacroInstaller().install(text);


        }
      }
    } catch (Throwable e) {
    }

  }
}
