package oj;

import ij.IJ;
import ij.ImagePlus;
import ij.Menus;
import ij.plugin.frame.Editor;
import java.awt.Color;
import java.awt.Window;
import javax.swing.ToolTipManager;
import oj.project.DataOJ;
import oj.geometry.VertexCalculatorOJ;
import oj.gui.MenuManagerOJ;
import oj.plugin.events.IjImageChangedListenerOJ;
import oj.plugin.events.StackChangedListenerOJ;
import oj.plugin.events.ToolbarListenerOJ;
import oj.processor.ToolStateProcessorOJ;
import oj.processor.DataProcessorOJ;
import oj.processor.EventProcessorOJ;
import oj.processor.ImageProcessorOJ;
//import oj.processor.GeometryProcessorOJ;
import oj.macros.MacroProcessorOJ;
import oj.macros.MacroExtStrOJ;
import oj.gui.tools.ToolManagerOJ;

/**
 * OJ is a set of static methods for handling general operations on the ObjectJ
 * plugin. Most of them are accessed from the menu. This class contains the
 * singletons doj, moj, poj, ccj. (Later we want to get rid of static methods)
 *
 * @author stelian
 */
public class OJ {

    //public static boolean ojIsInstalled = false;//14.9.2010
    private static DataOJ doj;
    private static ToolStateProcessorOJ ccj;
    private static DataProcessorOJ poj;
    private static EventProcessorOJ evp;
    private static ImageProcessorOJ fpj;
    private static MacroProcessorOJ mpj;
    private static ToolbarListenerOJ tlj;
    private static VertexCalculatorOJ vcj;
    private static IjImageChangedListenerOJ ilj;
    private static StackChangedListenerOJ slj;
    public final static int build = 485;
    public final static String releaseVersion = "1.03q";//do not follow Wayne
    public final static String buildDate = "02-sep-2014 16:52";
    public static final String URL = "http://simon.bio.uva.nl/objectj";
    public static final String ICONS = "/oj/gui/icons/";
    public static int bufferStrategy = 2;//10.7.2009
    public static boolean loadedAsBinary = true;
    public static boolean saveAsBinary = true;
    public static boolean useProjectMenu = true;//13.4.2010
    public static boolean addMagicBytes = true;//23.4.2010
    public static Editor editor = null;//
    public static Window editorWindow = null;//23.4.2010
    public static Color headerBackground = new Color(80, 80, 80);//17.5.2010
    public static boolean isProjectOpen = false;//17.5.2010
    public static boolean doubleBuffered = true;// for Windows only 10.2.2011

    /**
     * creates the singletons (processors for event, data, image, macro,
     * geometry ) increases ImageJ Window width on PC,
     *
     */
    public static void init() {
        if (!IJ.isMacintosh()) {
            IJ.getInstance().setSize(IJ.getInstance().getWidth() + 48, IJ.getInstance().getHeight());//1.12.2008
        }
        //ojIsInstalled = true;
        evp = new EventProcessorOJ();
        poj = new DataProcessorOJ();
        fpj = new ImageProcessorOJ();
        mpj = new MacroProcessorOJ();


        tlj = new ToolbarListenerOJ();
        vcj = new VertexCalculatorOJ();

        ilj = new IjImageChangedListenerOJ();
        slj = new StackChangedListenerOJ();
        //imageManagerOJ = new ImageManagerOJ();
        new MenuManagerOJ(Menus.getMenuBar());

        ImagePlus.addImageListener(ilj);
        IJ.getInstance().setDropTarget(null);

        installPlugins();

        String abc = MacroExtStrOJ.macroExtensions();
        ij.macro.Interpreter.setAdditionalFunctions(abc);

        //ojIsInstalled = true;

        ToolTipManager.sharedInstance().setDismissDelay(10000);
    }

    /**
     * Replaces Wayne's plugins by our one ones in the Hash table so we can
     * intercept. For example, if the user scales an image, ObjectJ is informed
     * so it can update the scale information in the project.
     */
    public static void installPlugins() {

        //Menus.getCommands().put("Scale...", "oj.plugin.ScalerOJ");--21.8.2009
        Menus.getCommands().put("Set Scale...", "oj.plugin.ScaleDialogOJ");
        Menus.getCommands().put("Quit", "oj.plugin.QuitOJ");
        //Menus.getCommands().put("Open...", "oj.plugin.OpenFileOJ"); removed 24.5.2010
        Menus.getCommands().put("Properties...", "oj.plugin.ImagePropertiesOJ");
        Menus.getCommands().put("Add Slice", "oj.plugin.StackEditorOJ(\"add\")");
        Menus.getCommands().put("Delete Slice", "oj.plugin.StackEditorOJ(\"delete\")");
        Menus.getCommands().put("Convert Images to Stack", "oj.plugin.StackEditorOJ(\"tostack\")");
        Menus.getCommands().put("Convert Stack to Images", "oj.plugin.StackEditorOJ(\"toimages\")");
        Menus.getCommands().put("Rename...", "oj.plugin.SimpleCommandsOJ(\"rename\")");

        Menus.getCommands().put("Update ImageJ...", "oj.plugin.ImageJUpdaterOJ");
        Menus.getCommands().put("Update Menus", "oj.plugin.ImageJUpdaterOJ(\"menus\")");
        Menus.getCommands().put("Refresh Menus", "oj.plugin.ImageJUpdaterOJ(\"menus\")");

        Menus.getCommands().put("Tiff...", "oj.plugin.WriterOJ(\"tiff\")");
        Menus.getCommands().put("Gif...", "oj.plugin.WriterOJ(\"gif\")");
        Menus.getCommands().put("Jpeg...", "oj.plugin.WriterOJ(\"jpeg\")");
        Menus.getCommands().put("Text Image...", "oj.plugin.WriterOJ(\"text\")");
        Menus.getCommands().put("ZIP...", "oj.plugin.WriterOJ(\"zip\")");
        Menus.getCommands().put("Raw Data...", "oj.plugin.WriterOJ(\"raw\")");
        Menus.getCommands().put("BMP...", "oj.plugin.WriterOJ(\"bmp\")");
        Menus.getCommands().put("PNG...", "oj.plugin.WriterOJ(\"png\")");
        Menus.getCommands().put("PGM...", "oj.plugin.WriterOJ(\"pgm\")");
        Menus.getCommands().put("FITS...", "oj.plugin.WriterOJ(\"fits\")");
        Menus.getCommands().put("LUT...", "oj.plugin.WriterOJ(\"lut\")");
    }

    /**
     * @return pointer to the entire project
     */
    public static DataProcessorOJ getDataProcessor() {
        return poj;
    }

    /**
     * @return pointer to singleton EventProcessor, which provides all the
     * listener methods etc
     */
    public static EventProcessorOJ getEventProcessor() {
        return evp;
    }

    /**
     * @return pointer to singleton MacroProcessorOJ, that contains a collection
     * of all ObjectJ macro functions
     */
    public static MacroProcessorOJ getMacroProcessor() {
        return mpj;
    }

    public static MacroProcessorOJ initMacroProcessor() {
        mpj = new MacroProcessorOJ();
        return mpj;
    }

    /**
     * @return pointer to singleton ToolStateProcessorOJ, that contains a
     * methods to detect mouse actions which change the status of point
     * collection
     */
    public static ToolStateProcessorOJ getToolStateProcessor() {
        return ccj;
    }

    /**
     *
     * @return pointer to singleton ImageProcessorOJ, that provides methods for
     * managing the linked images
     */
    public static ImageProcessorOJ getImageProcessor() {
        return fpj;
    }

    /**
     * @return pointer to DataOJ, i.e. the part of data that is written to the
     * ojj file
     */
    public static DataOJ getData() {
        return doj;
    }

    /**
     * @return pointer to VertexCalculatorOJ vcj, that provides methods to
     * calculate triangular results. A second instance of VertexCalculatorOJ is
     * used during macro execution in MacroProcessorOJ
     */
    public static VertexCalculatorOJ getVertexCalculator() {
        return vcj;
    }

    /**
     * Checks whether DataOJ doj exists
     */
    public static boolean isValidData() {
        return doj != null;
    }

    /**
     * Sets DataOJ data, e.g. after being read from the ojj file
     *
     * @param data
     */
    public static void setData(DataOJ data) {
        doj = data;
        if (data == null) {
            ccj = null;
            if (evp != null) {
                evp.cleanListeners();
                ToolManagerOJ.getInstance().clear();
            }
        } else {
            ccj = new ToolStateProcessorOJ();
        }
    }

    public static void setBufferStrategy(int n) {
        if (n >= 1 && n <= 3) {
            bufferStrategy = n;
        } else {
            IJ.showMessage("buffer strategy must be 1 .. 3");

        }
    }

    public static void debugLog(String msg) {
        if (IJ.debugMode) {
            IJ.log(msg);
        }
    }
}
