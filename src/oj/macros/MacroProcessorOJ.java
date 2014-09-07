/**
 * Further processes oj functions, but without limitation to string parameters
 */
package oj.macros;

import ij.WindowManager;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import oj.util.UtilsOJ;
import oj.project.results.ColumnsOJ;
import oj.project.results.OperandOJ;
import oj.geometry.VertexCalculatorOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.StatisticsChangedEventOJ;
import oj.util.ImageJAccessOJ;
import oj.util.WildcardMatchOJ;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Line;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Toolbar;
import ij.process.FloatPolygon;
import ij.util.Tools;
import java.awt.*;
import oj.OJ;
import oj.project.*;
import oj.project.results.ColumnDefOJ;
import oj.graphics.CustomCanvasOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.gui.tools.ToolManagerOJ;
import oj.plugin.GlassWindowOJ;

import oj.gui.results.ProjectResultsOJ;
import oj.io.InputOutputOJ;

import oj.project.results.ColumnOJ;
import oj.project.results.QualifiersOJ;
import oj.project.shapes.AngleOJ;
import oj.project.shapes.LineOJ;
import oj.project.shapes.PointOJ;
import oj.project.shapes.PolygonOJ;
import oj.project.shapes.RoiOJ;
import oj.project.shapes.SeglineOJ;

public class MacroProcessorOJ {

    //private int imageWidth;
    //private int imageHeight;
    //========== cell and ytem have shortest possible life ============
    private ColumnOJ newestColumn;//used for adding algorithms
    final int one = 1;
    ImageOJ targetImageOJ;
    int[] targetDimensions;
    int[] targetPos = new int[]{1, 1, 1};
    //int targetImage;
    int stackIndexT = 1;
    CellOJ cellT;
    String ytemNameT;//eg. "axis"
    int ytemIndexT;//eg. YTEM_TYPE_POINT = 1;
    YtemOJ ytemT;

    public ImageOJ getTargetImage() {
        return targetImageOJ;
    }

    public MacroProcessorOJ() {
        //this.imageHeight = 0;
        //this.imageWidth = 0;
    }
    static Robot robot2;

    /**
     * For setting markers into current other than current window
     */
    public void setTarget(String arg) {
        boolean found = false;
        arg = arg.toLowerCase();
        String[] parts = arg.split("=");
        if (parts[0].equals("frontimage")) {
            int linkedIndex = getImageLink();

            targetImageOJ = OJ.getData().getImages().getImageByIndex(linkedIndex - one);
            if (targetImageOJ == null) {
                ImageJAccessOJ.InterpreterAccess.interpError("Current image is not linked");
                return;
            }
            targetDimensions = targetImageOJ.getDimensions();
            stackIndexT = IJ.getImage().getCurrentSlice();
            targetPos = UtilsOJ.convertIndexToPosition(targetDimensions, stackIndexT);

            ytemNameT = OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName();

            ytemIndexT = OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemType();

        } else if (parts[0].equals("exit")) {
            closeObjectT();

            ytemT = null;
            cellT = null;
            targetImageOJ = null;
            OJ.getImageProcessor().updateImagesProperties();
            OJ.getDataProcessor().recalculateResults();
            found = true;
        } else if (parts.length == 2) {

            if (parts[0].equals("image")) {
                int linkedIndex = Integer.parseInt(parts[1]);

                targetImageOJ = OJ.getData().getImages().getImageByIndex(linkedIndex - one);
                if (targetImageOJ == null) {
                    ImageJAccessOJ.InterpreterAccess.interpError("Selected image is not linked");
                    stackIndexT = stackIndexT + 0;
                    stackIndexT = stackIndexT + 0;
                    stackIndexT = stackIndexT + 0;
                    stackIndexT = stackIndexT + 0;
                    return;
                }
                targetDimensions = targetImageOJ.getDimensions();

                stackIndexT = 1;
                targetPos = UtilsOJ.convertIndexToPosition(targetDimensions, stackIndexT);
                ytemNameT = OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName();
                ytemIndexT = OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemType();
                return;
            }

            if (parts[0].equals("channel")) {
                targetPos[0] = Integer.parseInt(parts[1]);
            }
            if (parts[0].equals("slice")) {
                targetPos[1] = Integer.parseInt(parts[1]);
            }
            if (parts[0].equals("frame")) {
                targetPos[2] = Integer.parseInt(parts[1]);
            }

            if ("channel-slice-frame".indexOf(parts[0]) >= 0) {
                int index = UtilsOJ.convertPositionToIndex(targetDimensions, targetPos);
                if (index != stackIndexT) {
                    ytemT = null;
                }
                stackIndexT = index;
                found = true;
            }

            if (parts[0].equals("stackindex")) {
                int index = Integer.parseInt(parts[1]);
                targetPos = UtilsOJ.convertIndexToPosition(targetDimensions, index);
                if (index != stackIndexT) {
                    ytemT = null;
                }
                stackIndexT = index;
                return;
            }

            if (!found) {
                ImageJAccessOJ.InterpreterAccess.interpError("Invalid argument: " + arg);
            }
        }
    }

    String getTargetAsString() {
        if (targetImageOJ == null) {
            return "";
        }
        int index = OJ.getData().getImages().getIndex(targetImageOJ) + one;
        return "image=" + index + " stackindex=" + stackIndexT;
    }

    void switchToItemT(String name) {
        if (ytemT != null && ytemT.getLocationsCount() > 0) {
            if (cellT == null) {
                cellT = new CellOJ(targetImageOJ.getName(), stackIndexT);
            }

            cellT.add(ytemT);//add old ytem
            ytemT = null;//create new ytem
        }
        ytemIndexT = OJ.getData().getYtemDefs().getYtemDefByName(name).getYtemType();
        ytemNameT = name;

    }

    /**
     * Sets a marker into target window which can be different from current
     * window
     */
    public void setMarkerT(double x, double y) {
        if (ytemT == null) {
            switch (ytemIndexT) {
                case YtemDefOJ.YTEM_TYPE_POINT:
                    ytemT = new PointOJ();
                    break;
                case YtemDefOJ.YTEM_TYPE_LINE:
                    ytemT = new LineOJ();
                    break;
                case YtemDefOJ.YTEM_TYPE_SEGLINE:
                    ytemT = new SeglineOJ();
                    break;

                case YtemDefOJ.YTEM_TYPE_POLYGON:
                    ytemT = new PolygonOJ();
                    break;
                case YtemDefOJ.YTEM_TYPE_ROI:
                    ytemT = new RoiOJ();
                    break;
                case YtemDefOJ.YTEM_TYPE_ANGLE:
                    ytemT = new AngleOJ();
                    break;
            }
            if (ytemT != null) {
                ytemT.setObjectDef(ytemNameT);
                ytemT.setStackIndex(stackIndexT);
            }
        }
        if (ytemT != null) {
            ytemT.add(new LocationOJ(x, y, (double) stackIndexT));
        }
    }

    public void closeYtemT() {
        if (ytemT != null && ytemT.getLocationsCount() > 0) {
            if (cellT == null) {
                cellT = new CellOJ(targetImageOJ.getName(), stackIndexT);
            }
            cellT.add(ytemT);//add old ytem
        }
        ytemT = null;
    }

    public void closeObjectT() {
        closeYtemT();
        {
            if (ytemT != null && ytemT.getLocationsCount() > 0) {
                if (cellT == null) {
                    cellT = new CellOJ(targetImageOJ.getName(), stackIndexT);
                }
                cellT.add(ytemT);
            }
            ytemT = null;
            if (cellT != null && cellT.getYtemsCount() > 0) {

                CellsOJ cells = OJ.getData().getCells();
                if (!cellT.isOpen()) {
                    cells.addCell(cellT);
                }
                cellT.setOpen(false);
                int newestCell = cells.getCellIndex(cellT);
                cells.setNewestCellIndex(newestCell);//0-based
            }
        }
        cellT = null;
    }

    /**
     * if item was open, close item if maxClones is now reached, switch to next
     * item if cell is full, close cell if cell was closed and CompositeObjects
     * = true, switch to first item
     */
    public void advance() {
        if (targetImageOJ != null) {
            ImageJAccessOJ.InterpreterAccess.interpError("Not allowed in target mode");
            return;
        }
        if (!(OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ)) {
            ToolManagerOJ.getInstance().selectTool("Marker");
        }
        ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).advanceToNextYtemDef();
    }

    /**
     * return the name of the active ytem definition
     *
     * @return name of the active ytem
     */
    public String activeYtemName() {
        return OJ.getData().getYtemDefs().getSelectedYtemDefName();
    }

    /**
     * closes the current editing ytem
     */
    public void closeYtem() {
        if (targetImageOJ != null) {
            closeYtemT();
            return;
        }
        if (OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ) {
            ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).closeYtem();
        }
    }

    /**
     * closes the current editing cell
     */
    public void closeObject() {
        if (targetImageOJ != null) {
            closeObjectT();
            return;
        }
        if (OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ) {
            ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).closeCell();
        }
    }

    /**
     * delete all cells
     */
    public void deleteAllObjects() {
        OJ.getDataProcessor().removeAllCells();
    }

    /**
     * delete image witch match the name
     *
     * @param imageName the name of the image which must be deleted
     */
    public void disposeImage(String imageName) {
        for (int i = OJ.getData().getImages().getImagesCount(); i > 0; i--) {
            String image_name = OJ.getData().getImages().getImageByIndex(i - 1).getName();
            if (image_name.matches(imageName)) {
                OJ.getData().getImages().removeImageByName(image_name);
            }
        }
    }

    public void disposeAllImages() {
        OJ.getData().getImages().removeAllImages();
    }

    public int newestCell() {
        return OJ.getData().getCells().getNewestCellIndex() + one;
    }

    public void setLocation(int xpos, int ypos, int width, int height) {
        ImageWindow imgw = WindowManager.getCurrentWindow();
        if (imgw != null) {
            imgw.setBounds(xpos, ypos, width, height);
        }
    }

    public void setYtemDefVisible(String ytemtype, boolean visible) {//3.6.2009
        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);
        OJ.getData().getYtemDefs().setYtemVisibilitySwitchEnabled(true);//6.12.2011
        for (int i = 0; i < OJ.getData().getYtemDefs().getYtemDefsCount(); i++) {
            YtemDefOJ ytmDef = OJ.getData().getYtemDefs().getYtemDefByIndex(i);
            String name = ytmDef.getYtemDefName();
            if (wm.match(name, ytemtype)) {
                ytmDef.setVisible(visible);
            }
        }
    }

    public void zoom(double factor, int xhook, int yhook) {
        ImageWindow win = ij.WindowManager.getCurrentWindow();
        if (win == null) {
            return;
        }
        Rectangle rr = new Rectangle();
        ImagePlus imp = ij.WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        ImageCanvas ic = imp.getCanvas();
        if (ic == null) {
            return;
        }
        if (ic instanceof CustomCanvasOJ) {
            CustomCanvasOJ icoj = (CustomCanvasOJ) ic;
            icoj.ojZoom(factor, xhook, yhook);

        }
    }

    boolean selectWindowManagerImage(String imageName) {
        //why don't we call WindowManager.selectWindow(imageName);

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 400) { // 0.4 sec timeout

            int[] wList = WindowManager.getIDList();
            int len = wList != null ? wList.length : 0;
            for (int i = 0; i
                    < len; i++) {
                ImagePlus imp = WindowManager.getImage(wList[i]);
                if (imp != null) {
                    if (imp.getTitle().equals(imageName)) {
                        IJ.selectWindow(imp.getID());
                        return true;
                    }

                }
            }
            IJ.wait(10);
        }

        return (false);
    }

    public void showImage(int imageIndex) {

        ImagesOJ images = OJ.getData().getImages();
        if ((imageIndex <= 0) || (imageIndex > images.getImagesCount())) {
            ImageJAccessOJ.InterpreterAccess.interpError("Image number (" + imageIndex + ") out of range (1.." + images.getImagesCount() + ")");
            return;
        }

        String image_name = images.getImageByIndex(imageIndex - one).getName();
        ImagePlus imp = OJ.getImageProcessor().getOpenLinkedImage(image_name);
        if (imp != null) {//7.9.2010
            int ID = imp.getID();
            IJ.selectWindow(ID);
        } else {
            OJ.getImageProcessor().openImage(image_name);//19.10.2010

        }

    }

    public void flatten() {

        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        CustomCanvasOJ myCanvas = new CustomCanvasOJ(imp, OJ.getData(), imp.getTitle());
        myCanvas.makeFlattenedImage();
    }

    public void setTool(String tool) {
        //Marker Pistol MoveObject SelectObject ObjectToRoi
        //Rectangle Elliptical Brush Polygon FreehandRoi StraightLine SegmentedLine FreehandLine Angle Point Wand Text Zoom Scroll ColorPicker
        boolean isNumber = true;

        String imageJTools[] = {
            "00=rectangle", "01=oval", "02=polygon", "03=freehand", "04=straightline",
            "05=polyline", "06=freeline", "07=point", "08=wand", "09=text",
            "10=spare", "11=zoom", "12=hand", "13=dropper", "14=angle",
            "-1=Marker", "-2=Pistol", "-3=MoveObject",
            "-4=SelectObject", "-5=ObjectToRoi"
        };
        int tool_index = -999;
        try {
            tool_index = Integer.parseInt(tool);
        } catch (NumberFormatException e) {
            isNumber = false;
        }

        if (!isNumber) {
            for (int jj = 0; jj < imageJTools.length; jj++) {
                String fragment = imageJTools[jj];
                String name = fragment.substring(3);
                int index = Integer.parseInt(fragment.substring(0, 2));
                if (tool.equalsIgnoreCase(name)) {
                    tool_index = index;
                    break;
                }
            }
        }

        if (tool_index >= 0) {//it's an ImageJ tool

            ToolManagerOJ.getInstance().selectTool("");
            Toolbar.getInstance().setTool(tool_index);//is range-protected

        } else {//it's an ObjectJ tool

            ToolManagerOJ.getInstance().selectTool(Math.abs(tool_index));
        }
    }

    public void openCell(int index) {
        if (targetImageOJ != null) {
            openCellT(index);
            return;
        }
        if (UtilsOJ.inRange(1, OJ.getData().getCells().getCellsCount(), index)) {
            if (!(OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ)) {
                ToolManagerOJ.getInstance().selectTool("Marker");
            }

            ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).openCell(index - one);//++369
//+++Caused by: java.lang.ClassCastException: oj.processor.state.ToolStateAdaptorOJ cannot be cast to oj.processor.state.CreateCellStateOJ
//at oj.macros.MacroProcessorOJ.openCell(MacroProcessorOJ.java:369)
        }

    }

    /**
     * opens cell in target mode
     *
     * @param index
     */
    public void openCellT(int index) {
        cellT = OJ.getData().getCells().getCellByIndex(index - one);
        ytemT = null;
        cellT.setOpen(true);

    }

    public String ownerName(int cellIndex) {
        CellsOJ cells = OJ.getData().getCells();
        if (UtilsOJ.inRange(1, cells.getCellsCount(), cellIndex)) {
            return cells.getCellByIndex(cellIndex - one).getImageName();
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("object number out of range");
        }
        return "";
    }

    public int ownerIndex(int cellIndex) {
        CellsOJ cells = OJ.getData().getCells();
        if (UtilsOJ.inRange(1, cells.getCellsCount(), cellIndex)) {
            String imageName = cells.getCellByIndex(cellIndex - one).getImageName();
            return one + OJ.getData().getImages().getIndexOfImage(imageName);
        }
        return 0;
    }

    public void putOperand(String itemType, int clone, int point) {
        if (newestColumn != null) {
            OperandOJ op = new OperandOJ();
            op.setYtemName(itemType);
            op.setYtemClone(clone - one);
            op.setRelPosition(point - one);
            newestColumn.getColumnDef().addOperand(op);
        }

    }

    public void putAlgorithm(String algString) {
        if (newestColumn != null) {
            int algNum = ColumnDefOJ.getAlgorithm(newestColumn.getName(), algString);
            newestColumn.getColumnDef().setAlgorithm(algNum);
        }

    }

    public void showObject(int index) {
        if (!cellInRange(index)) {
            return;
        }
        //ImageJAccessOJ.FunctionsAccess.resetImage();//+++++1.7.2013
        OJ.getDataProcessor().showCell(index - one);
    }

//    public void setNewSize(int width, int height) {
//        this.imageWidth = width;
//        this.imageHeight = height;
//    }
    public void setMarker(double xpos, double ypos) {
        if (targetImageOJ != null) {
            setMarkerT(xpos, ypos);
            return;
        }
        String image_name = WindowManager.getCurrentImage().getTitle();

        if (OJ.getData().getImages().getImageByName(image_name) == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("A marker can only be set into a linked image. Select the linked image before calling the ojSetMarker function.");
            return;
        }

        int slice_index = WindowManager.getCurrentImage().getCurrentSlice();

        if (!(OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ)) {
            ToolManagerOJ.getInstance().selectTool("Marker");
        }

        OJ.getToolStateProcessor().mousePressed(image_name, slice_index, xpos, ypos, 0);
    }

    /**
     * Sorts objects so they have ascending stack indexes
     *
     * @param whichImage index of image (1-based); use 0 for all images
     */
    public void orderObjectsInZ(int whichImage) {
        ImagesOJ images = OJ.getData().getImages();
        CellsOJ cells = OJ.getData().getCells();
        int nImages = images.getImagesCount();
        for (int img = 0; img < nImages; img++) {
            if (whichImage == 0 || whichImage == img + 1) {
                int first = images.getImageByIndex(img).getFirstCell();//0-based
                int last = images.getImageByIndex(img).getLastCell();//0-based
                int nCells = last - first + 1;
                int kk = 0;
                double prevZ = 0;
                boolean flag = false;
                double[] stackIndices = new double[nCells];
                CellOJ[] tmpCells = new CellOJ[nCells];
                if (last > 0) {
                    for (int cellNo = first; cellNo <= last; cellNo++) {
                        CellOJ cell = cells.getCellByIndex(cellNo);
                        if (cell == null) {
                            cell = null;
                        }
                        int stackIndex = cell.getStackIndex();
                        flag = flag || stackIndex < prevZ;
                        prevZ = stackIndex;
                        stackIndices[kk] = stackIndex;
                        tmpCells[kk] = cell;
                        kk++;
                    }
                }
                if (flag) {
                    kk = 0;
                    int[] ranks = Tools.rank(stackIndices);
                    for (int cellNo = first; cellNo <= last; cellNo++) {
                        int rank = ranks[kk];
                        cells.setCell(cellNo, tmpCells[rank]);
                        kk++;
                    }
                }
            }
        }
    }

    /**
     * activates item type in ObjectJ Tools window
     */
    public void switchToItem(String name) {
        if (null == OJ.getData().getYtemDefs().getYtemDefByName(name)) {
            ImageJAccessOJ.InterpreterAccess.interpError("'" + name + "' is not a defined item");
            return;
        }
        if (targetImageOJ != null) {
            switchToItemT(name);
            return;
        }
//
        if (!(OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ)) {
            ToolManagerOJ.getInstance().selectTool("Marker");
        }

        OJ.getData().getYtemDefs().setSelectedYtemDef(name);

    }

    /**
     * activates n-th item type in ObjectJ Tools window (1-based) use negative
     * index to count from the back.
     */
    public void switchToItem(int itemTypeNumber) {

        String name = itemTypeNumberToName(itemTypeNumber);
        if (name != null) {
            switchToItem(name);
        }
    }

    public String itemTypeNumberToName(int itemTypeNumber) {
        int maxTypes = OJ.getData().getYtemDefs().getYtemDefsCount();
        int jj = itemTypeNumber;
        if (jj < 0) {
            jj = maxTypes + jj + 1;
        }
        jj -= one;
        if (jj < 0 || jj >= maxTypes) {
            ImageJAccessOJ.InterpreterAccess.interpError("itemType number '" + itemTypeNumber + "' is out of range");
            return null;
        }
        return OJ.getData().getYtemDefs().getYtemDefByIndex(jj).getYtemDefName();

    }

    /**
     * it swaps the backgrounds of two slices
     *
     * @param sliceA first slice
     * @param sliceB second slice
     */
    public void swapSlices(int sliceA, int sliceB) {
        IJ.runMacro("Stack.swap(" + sliceA + ", " + sliceB + ");");
        return;//30.6.2013
//        if (sliceA == sliceB || sliceA < 1 || sliceB < 1
//                || sliceA > IJ.getImage().getStackSize() || sliceB > IJ.getImage().getStackSize()) {
//            return;
//        }
//
//        ImagePlus imp = IJ.getImage();
//        ImageStack stack = imp.getStack();
//
//        String[] labels = ImageJAccessOJ.ImageStackAccess.getStackLabels(stack);
//
//        String labelA = labels[sliceA - 1];
//        String labelB = labels[sliceB - 1];
//        labels[sliceA - 1] = labelB;
//        labels[sliceB - 1] = labelA;
//
//        ImageJAccessOJ.ImageStackAccess.setStackLabels(stack, labels);
//
//        Object[] objects = ImageJAccessOJ.ImageStackAccess.getStackObjects(stack);
//        Object ipA = objects[sliceA - 1];
//        Object ipB = objects[sliceB - 1];
//        objects[sliceA - 1] = ipB;
//        objects[sliceB - 1] = ipA;
//
//        ImageJAccessOJ.ImageStackAccess.setStackObjects(stack, objects);
//
//
//        if (imp.getCurrentSlice() == sliceA) {
//            imp.setProcessor(imp.getTitle(), stack.getProcessor(sliceA));
//        } else if (imp.getCurrentSlice() == sliceB) {
//            imp.setProcessor(imp.getTitle(), stack.getProcessor(sliceB));
//        }
//
//        imp.repaintWindow();
    }

    public int getImageCount() {
        return OJ.getData().getImages().getImagesCount();
    }

    public int getNCells() {
        return OJ.getData().getCells().getCellsCount();
    }

    public void recalculate() {
        ProjectResultsOJ.close();//23.2.2009 completely close results
        OJ.getImageProcessor().updateImagesProperties();
        OJ.getDataProcessor().recalculateResults();
        new ProjectResultsOJ();
        ProjectResultsOJ.getInstance().setVisible(true);//23.2.2009 re-open results
        ProjectResultsOJ.getInstance().setState(Frame.NORMAL);//23.2.2009
    }

    public void closeResults() {
        ProjectResultsOJ.close();//15.3.2009

    }

    public void close(String pattern) {//17.3.2012
        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);

        ImagePlus currentImp = WindowManager.getCurrentImage();
        for (int img = WindowManager.getWindowCount(); img > 0; img--) {
            int id = WindowManager.getNthImageID(img);
            ImagePlus imp = WindowManager.getImage(id);
            if (imp != null) {
                String title = imp.getTitle();
                boolean flagOthers = (pattern.equalsIgnoreCase("\\others") && currentImp != imp);
                if (wm.match(title, pattern) || flagOthers) {
                    imp.changes = false;
                    imp.close();
                }
            }
        }
        if (currentImp != null) {
            WindowManager.setCurrentWindow(currentImp.getWindow());
        }
    }

    public String getResult(String columnName, int index) {
        ColumnOJ theColumn = OJ.getData().getResults().getColumns().getColumnByName(columnName);//n_ 11.9.2007

        if (theColumn == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("Column '" + columnName + "' does not exist");
            return "";
        }

        if ((index - one) < 0 || (index - one) >= theColumn.getResultCount()) {
            ImageJAccessOJ.InterpreterAccess.interpError("Row number " + index + "out of range");
            if (theColumn.getColumnDef().isTextMode()) {
                return "";
            } else {
                return Double.toString(Double.NaN);
            }

        }
        if (theColumn.getColumnDef().isTextMode()) {
            return theColumn.getStringResult(index - 1);
        } else {
            return Double.toString(theColumn.getDoubleResult(index - one));
        }

    }

    public int getFirstCell(int index) {
        ImageOJ img = OJ.getData().getImages().getImageByIndex(index - one);
        return img.getFirstCell() + one;
    }

    public int getLastCell(int index) {
        ImageOJ img = OJ.getData().getImages().getImageByIndex(index - one);
        int last = img.getLastCell() + one;
        if (last == 0) {
            last = -1;
        } //return -1 if image is unmarked: convenient for-loop

        return last;
    }

    public int getOpenCell() {
        if (targetImageOJ != null) {
            ImageJAccessOJ.InterpreterAccess.interpError("getOpenObject cannot be called in target mode");
            return 0;
        }

        int index = OJ.getData().getCells().getOpenCellIndex();
        return index + one;
    }

    public int getImageLink() {
        int index = OJ.getImageProcessor().getCurrentImageIndex();

        return index + one;//14.8.2011

    }

    public void setResult(String columnName, int index, String value) {
        ColumnOJ theColumn = OJ.getData().getResults().getColumns().getColumnByName(columnName);
        if (theColumn == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("Column '" + columnName + "' does not exist");
            return;

        }

        if ((index - one) >= theColumn.getResultCount()) {
            if (theColumn.isUnlinkedColumn()) {
                for (int i = (theColumn.getResultCount() - 1); i < (index - 2); i++) {
                    if (theColumn.getColumnDef().isTextMode()) {
                        OJ.getDataProcessor().addStringResult(columnName, "");
                    } else {
                        OJ.getDataProcessor().addDoubleResult(columnName, Double.NaN);
                    }

                }
                if (theColumn.getColumnDef().isTextMode()) {
                    OJ.getDataProcessor().addStringResult(columnName, value);
                } else {
                    OJ.getDataProcessor().addDoubleResult(columnName, MacroProcessorOJ.parseDouble(value));
                }

            }
        } else {
            if (theColumn.isUnlinkedColumn()) {
                if (UtilsOJ.inRange(1, theColumn.getResultCount(), index)) {
                    if (theColumn.getColumnDef().isTextMode()) {
                        OJ.getDataProcessor().setStringResult(columnName, index - one, value);
                    } else {
                        OJ.getDataProcessor().setDoubleResult(columnName, index - one, MacroProcessorOJ.parseDouble(value));
                    }

                }
            } else {
                if (UtilsOJ.inRange(1, OJ.getData().getCells().getCellsCount(), index)) {
                    if (theColumn.getColumnDef().isTextMode()) {
                        OJ.getDataProcessor().setStringResult(columnName, index - one, value);
                    } else {
                        OJ.getDataProcessor().setDoubleResult(columnName, index - one, MacroProcessorOJ.parseDouble(value));
                    }

                }
            }
        }
    }

    public void setImagePlusUnchanged() {
        ImagePlus imp = IJ.getImage();
        if (imp != null) {
            imp.changes = false;
        }

    }

    public void deleteCell(int index) {
        if ((index > 0) && (index <= OJ.getData().getCells().getCellsCount())) {
            OJ.getDataProcessor().removeCellByIndex(index - one);
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("ojDeleteObject - index out of range: " + index + "[0," + (OJ.getData().getCells().getCellsCount() - 1) + "]");
        }

    }

    public void deleteYtem(int index) {//1-based

        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            OJ.getDataProcessor().removeYtemByIndex(cell, index - 1);
            if (cell.getYtemsCount() == 0) {
                OJ.getDataProcessor().removeCell(cell);
            }

        }
    }

    public void deleteYtem(String ytemType, int index) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            WildcardMatchOJ wm = new WildcardMatchOJ();
            wm.setCaseSensitive(false);
            int hit = 0;
            for (int jj = 0; jj
                    < cell.getYtemsCount(); jj++) {
                YtemOJ ytem = cell.getYtemByIndex(jj);
                if (wm.match(ytem.getYtemDef(), ytemType)) {
                    hit++;
                }

                if (hit == index) {
                    deleteYtem(jj + 1);//1-based

                    return;

                }
            }
        }
    }

    public void qualifyCell(int index, int qq) {
        if ((index) > 0) {
            if (qq != 0) {
                OJ.getDataProcessor().qualifyCell(index - 1, true);
            } else {
                OJ.getDataProcessor().qualifyCell(index - 1, false);
            }
            OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, false);//21.8.2011

        }
    }

    public boolean qualified(int index) {
        int count = OJ.getData().getCells().getCellsCount();
        if (index > 0 && index <= count) {
            return OJ.getData().getCells().getCellByIndex(index - one).isQualified();
        }

        ImageJAccessOJ.InterpreterAccess.interpError("Object index " + index + " is out of range 1.." + count);
        return false;
    }

    /**
     * checks if cellindex is between 1 and nCells (i.e. 1-based)
     *
     * @return true if ok
     */
    public boolean cellInRange(int index) {//1-based

        int count = OJ.getData().getCells().getCellsCount();
        boolean good = UtilsOJ.inRange(0, count, index);//0 is valid for unselecting a cell

        if (!good) {
            ImageJAccessOJ.InterpreterAccess.interpError("Object index " + index + " is out of range 1.." + count);
        }

        return good;
    }

    /**
     * Rearranges an ytem as if ytems were clicked in a different order, but
     * does not change any coordinates.
     *
     * @param fromIndex position of ytem to be moved (1-based)
     * @param toIndex new position after movement
     */
    public void repositionYtem(int fromIndex, int toIndex) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            cell.repositionYtem(fromIndex - one, toIndex - one);

        }
    }

    public int selectedCell() {
        return one + OJ.getData().getCells().getSelectedCellIndex();
    }

    public String selectedYtemName() {
        return OJ.getData().getCells().getSelectedCell().getSelectedYtem().getYtemDef();
    }

    public void selectCell(int index) {
        if (!cellInRange(index)) {
            return;
        }

        OJ.getDataProcessor().selectCell(index - one);
    }

    /**
     * From a subset of those items in current cell whose names match
     * <itemType>, the nn-th one is selected. nn can be negative to count from
     * the back. item type can contain wildcards.
     *
     * @param itemType name of items that form the subset.
     * @param index index of subset (1-based; use negative values to count from
     * back)
     *
     */
    public void selectYtem(String ytemType, int index) {
        //we must check what is happenning in the CellProcessor
        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("No object is selected");
            return;

        }

        int ix, count = 0;
        int nYtems = cell.getYtemsCount();
        boolean backwards = (index < 0);
        if (backwards) {
            index = -index;
        }

        boolean titleFound = false;

        for (int jj = 0; jj
                < nYtems; jj++) {
            if (backwards) {
                ix = nYtems - jj - 1;
            } else {
                ix = jj;
            }

            if (wm.match(cell.getYtemByIndex(ix).getYtemDef(), ytemType)) {
                titleFound = true;
                count
                        += 1;
                if (count == index) {
                    cell.selectYtem(ix);
                    return;
                }
            }
        }
        if (!titleFound) {
            ImageJAccessOJ.InterpreterAccess.interpError("Item type '" + ytemType + "' does not exist");
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("Item index " + index + " is out of range");
        }

    }

    public void selectYtem(int itemNumber, int index) {
        String name = itemTypeNumberToName(itemNumber);
        if (name == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("Item type not found");
            return;

        }
    }

    public double getYtemLength() {
        double len = 0;
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ ytem = cell.getSelectedYtem();
            int nPoints = ytem.getLocationsCount();
            int divisor = nPoints;
            if (ytem.getType() == YtemDefOJ.YTEM_TYPE_POLYGON || ytem.getType() == YtemDefOJ.YTEM_TYPE_ROI) {
                nPoints++;
            }
            for (int pt = 0; pt < nPoints - 1; pt++) {
                double dx = ytem.getLocation(pt).x - ytem.getLocation((pt + 1) % divisor).x;
                double dy = ytem.getLocation(pt).y - ytem.getLocation((pt + 1) % divisor).y;
                len += Math.sqrt(dx * dx + dy * dy);
            }
        }
        return len;
    }

    public double getImageValue(int n, String key) {
        String theKey = key.toLowerCase();
        double value = 0.0;
        ImageOJ img = OJ.getData().getImages().getImageByIndex(n - one);
        if (theKey.equals("channels")) {
            value = img.getNumberOfChannels();
        } else if (theKey.equals("slices")) {
            value = img.getNumberOfSlices();
        } else if (theKey.equals("frames")) {
            value = img.getNumberOfFrames();
        } else if (theKey.equals("width")) {
            value = img.getWidth();
        } else if (theKey.equals("height")) {
            value = img.getHeight();
        } else if (theKey.equals("stacksize")) {
            value = img.getNumberOfChannels() * img.getNumberOfSlices() * img.getNumberOfFrames();
        } else if (theKey.equals("frameinterval")) {//29.7.2013
            value = img.getFrameInterval();
        } else if (theKey.equals("id")) {
            value = img.getID();
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("No valid key: " + key);
        }

        return value;
    }

    /**
     * returns the number of items in current cell whose name matches
     *
     * @param itemName can contain wildcards; all items whose name matches
     * itemname are counted
     * @return the number of the subset
     */
    public int getNItems(String itemName) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            int count = 0;
            WildcardMatchOJ wm = new WildcardMatchOJ();
            wm.setCaseSensitive(false);//n_27.3.2007

            for (int i = 0; i
                    < cell.getYtemsCount(); i++) {
                if (wm.match(cell.getYtemByIndex(i).getYtemDef(), itemName)) {
                    count = count + 1;
                }

            }
            return count;
        }

        return 0;
    }

    public String getYtemName() {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ item = cell.getSelectedYtem();
            if (item != null) {
                return item.getYtemDef();
            }
        }
        return "";
    }

    public String getYtemNames() {
        YtemDefsOJ defs = OJ.getData().getYtemDefs();
        if (defs == null) {
            return "";
        }
        String ytemNames = "";
        for (int jj = 0; jj < defs.getYtemDefsCount(); jj++) {
            if (jj > 0) {
                ytemNames = ytemNames + " ";
            }

            ytemNames = ytemNames + defs.getYtemDefByIndex(jj).getYtemDefName();
        }
        return ytemNames;
    }

    public String getYtemTypes() {
        YtemDefsOJ defs = OJ.getData().getYtemDefs();
        if (defs == null) {
            return "";
        }
        String ytemTypes = "";
        for (int jj = 0; jj < defs.getYtemDefsCount(); jj++) {
            if (jj > 0) {
                ytemTypes = ytemTypes + " ";
            }
            int n = defs.getYtemDefByIndex(jj).getYtemType();
            ytemTypes = ytemTypes + YtemDefOJ.getTypeName(n);
        }
        return ytemTypes;
    }

    public int getNPoints() {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ ytem = cell.getSelectedYtem();
            if (ytem != null) {
                return ytem.getLocationsCount();
            }

        }
        return 0;
    }

    public int getNColumns() {
        return OJ.getData().getResults().getColumns().getAllColumnsCount();
    }

    public String getColumnTitle(int columnIndex) {
        int count = OJ.getData().getResults().getColumns().getAllColumnsCount();
        if (columnIndex < 1 || columnIndex > count) {
            ImageJAccessOJ.InterpreterAccess.interpError("Column number " + columnIndex + " out of range");
            return null;
        }
        String title = OJ.getData().getResults().getColumns().getColumnByIndex(columnIndex - one).getName();
        return title;
    }

    public double getXPos(int index) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ ytem = cell.getSelectedYtem();
            if (ytem != null) {
                return ytem.getLocation(index - 1).getX();
            }

        }
        return -1;
    }

    public double getYPos(int index) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ item = cell.getSelectedYtem();
            if (item != null) {
                return item.getLocation(index - 1).getY();
            }

        }
        return -1;
    }

    public double getZPos(int index) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            YtemOJ ytem = cell.getSelectedYtem();
            if (ytem != null) {
                return ytem.getLocation(index - 1).getZ();
            }

        }
        return -1;
    }

    public void initColumn(String columnName, boolean isUnlinked, boolean isTextMode) {//10.5.2010synchronized
        newestColumn = OJ.getData().getResults().getColumns().getColumnByName(columnName);
        if (newestColumn != null) {
            newestColumn.getColumnDef().clearOperands();
            if (isUnlinked) {
                if (isTextMode) {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT);
                } else {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER);
                }
                newestColumn.rows.clear();

            }
            if (!isUnlinked) {
                if (isTextMode) {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT);
                } else {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER);
                }

                int cnt = newestColumn.getResultCount();
                for (int jj = 0; jj < cnt; jj++) {
                    if (newestColumn.getColumnDef().isTextMode()) {
                        OJ.getDataProcessor().setStringResult(columnName, jj, "");
                    } else {
                        OJ.getDataProcessor().setDoubleResult(columnName, jj, Double.NaN);
                    }
                }

            }
        }

        if (newestColumn == null) {
            newestColumn = new ColumnOJ();
            newestColumn.getColumnDef().setName(columnName);

            if (isUnlinked) {
                if (isTextMode) {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT);
                } else {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER);
                }

            } else {
                if (isTextMode) {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT);
                } else {
                    newestColumn.getColumnDef().setAlgorithm(ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER);
                }

            }
            OJ.getData().getResults().getColumns().addColumn(newestColumn, true);//15.3.2009
        }
    }

    public void ytemToRoi() {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("No cell is selected");
            return;
        }
        YtemOJ ytm = cell.getSelectedYtem();//13.5.2014
        ytm.ytemToRoi();
    }

    /**
     * deletes one or several columns whose names matches the pattern
     *
     * @param columnNamePattern
     */
    public void deleteColumn(String columnNamePattern) {
        WildcardMatchOJ wm = new WildcardMatchOJ();
        wm.setCaseSensitive(false);
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        String[] columnNames = columns.columnNamesToArray();
        for (int colIndex = columnNames.length - 1; colIndex >= 0; colIndex--) {
            String thisName = columnNames[colIndex];
            if (wm.match(thisName, columnNamePattern)) {
                OJ.getData().getResults().getColumns().removeColumnByName(thisName);
            }
        }
        newestColumn = null;
    }

    public double getStatistics(String columnName, String operation) {
        String op = "";
        if (operation.equalsIgnoreCase("mean")) {
            op = "Mean";
        } else if (operation.equalsIgnoreCase("min")) {
            op = "Minimum";
        } else if (operation.equalsIgnoreCase("max")) {
            op = "Maximum";
        } else if (operation.equalsIgnoreCase("count")) {
            op = "Count";
        } else if (operation.equalsIgnoreCase("stdev")) {
            op = "StDev";
        } else if (operation.equalsIgnoreCase("cv%")) {
            op = "Cv";
        } else if (operation.equalsIgnoreCase("sum")) {
            op = "Sum";
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("expect mean, min, max, count, stdev, cv% or sum");
            return Double.NaN;
        }

        ColumnOJ col = OJ.getData().getResults().getColumns().getColumnByName(columnName);
        if (col == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("Column not found");
            return Double.NaN;
        }
        return col.getStatistics().getStatisticsValueByName(op);
    }

    public String getVoxelSize(int imageIndex, String xyz) {//17.9.2009
        imageIndex -= one;
        ImagesOJ images = OJ.getData().getImages();
        if (imageIndex >= 0 && imageIndex < images.getImagesCount()) {
            ImageOJ theImage = images.getImageByIndex(imageIndex);
            if (xyz.equalsIgnoreCase("x")) {
                return Double.toString(theImage.getVoxelSizeX());
            }
            if (xyz.equalsIgnoreCase("y")) {
                return Double.toString(theImage.getVoxelSizeY());
            }
            if (xyz.equalsIgnoreCase("z")) {
                return Double.toString(theImage.getVoxelSizeZ());
            }
            if (xyz.equalsIgnoreCase("unit")) {
                return theImage.getVoxelUnitX();
            }
        }
        return "0";
    }

    public void movePoint(int index, double xPos, double yPos, double zPos) {
        index = index - one;
        LocationOJ loc = OJ.getData().getCells().getSelectedCell().getSelectedYtem().getLocation(index);
        if (xPos >= 0) {
            loc.setX(xPos);
        }

        if (yPos >= 0) {
            loc.setY(yPos);
        }

        if (zPos >= 0) {
            loc.setZ(zPos);
        }

        OJ.getDataProcessor().movePoint(index, loc);
    }

    //not used
    public void setStatisticValue(String columnName, String statisticName, double value) {
        OJ.getData().getResults().getColumns().getColumnByName(columnName).getStatistics().setStatisticsValueByName(statisticName, value);
        OJ.getEventProcessor().fireStatisticsChangedEvent(statisticName, StatisticsChangedEventOJ.STATISTICS_VALUE_CHANGED);
    }

    public void saveProject() {
        new InputOutputOJ().saveProject(OJ.getData(), true);
    }

    public void roiToYtem() {//19.6.2009
        ImagePlus imp = ij.WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        Roi roi = imp.getRoi();
        if (roi == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("No roi exists");
            return;
        }
        Polygon p = roi.getPolygon();
        FloatPolygon fp = roi.getFloatPolygon();
        double[] xpoints = new double[p.npoints];
        double[] ypoints = new double[p.npoints];

        if (fp != null) { //spline fit polygon
            for (int i = 0; i < p.npoints; i++) {
                xpoints[i] = fp.xpoints[i];
                ypoints[i] = fp.ypoints[i];
            }

        } else {
            for (int i = 0; i < p.npoints; i++) {
                xpoints[i] = p.xpoints[i];
                ypoints[i] = p.ypoints[i];
            }
        }

        for (int jj = 0; jj < xpoints.length; jj++) {
            setMarker(xpoints[jj], ypoints[jj]);
        }
        int rt = roi.getType();
        if (rt == roi.RECTANGLE || rt == roi.OVAL || rt == roi.POLYGON || rt == roi.FREEROI || rt == roi.TRACED_ROI) { //21.8.2009
            if (xpoints[p.npoints - 1] != xpoints[0] || ypoints[p.npoints - 1] != ypoints[0]) {
                setMarker(xpoints[0], ypoints[0]);
            }
        }
        imp.getCanvas().setCursor(-1,-1,-1, -1);//15.7.2014
    }

    public void selectClosestItem(double x, double y, double tolerance) {
        ImagesOJ images = OJ.getData().getImages();
        int linkNo = images.getIndexOfImage(IJ.getImage().getTitle());

        for (int img = 0; img < images.getImagesCount(); img++) {
            ImageOJ image = images.getImageByIndex(img);
        }

    }

    public int getColumnNumber(String columnName) {
        return OJ.getData().getResults().getColumns().getColumnIndexByName(columnName) + 1;
    }

    public void set3D(boolean d3) {
        OJ.getData().getYtemDefs().set3DYtems(d3);
        OJ.getEventProcessor().fireYtemDefChangedEvent(null, YtemDefChangedEventOJ.THREE_D_MODE_CHANGED);
    }

    public void setPlotProperties(String columnName, String properties) {//17.9.2011
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        ArrayList matchingColumns = columns.getColumnsByWildcard(columnName);
        for (int jj = 0; jj < matchingColumns.size(); jj++) {
            ColumnOJ col = (ColumnOJ) matchingColumns.get(jj);
            ColumnDefOJ colDef = col.getColumnDef();
            colDef.setPlotProperties(properties);
        }
    }

    public void setColumnProperty(String columnName, String property, String valueS) {
        valueS = valueS.toLowerCase();//24.1.2010
        String leftPart = "";
        String rightPart = "";
        if (valueS.contains("=")) {
            int eqPos = valueS.indexOf("=");
            leftPart = valueS.substring(0, eqPos);
            rightPart = valueS.substring(eqPos + 1);
        }
        int value = Integer.parseInt(valueS);
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        ArrayList matchingColumns = columns.getColumnsByWildcard(columnName);
        for (int jj = 0; jj
                < matchingColumns.size(); jj++) {
            ColumnOJ col = (ColumnOJ) matchingColumns.get(jj);
            ColumnDefOJ colDef = col.getColumnDef();
            if (property.equalsIgnoreCase("visible")) {
                colDef.setHidden(value == 0);
            } else if (property.equalsIgnoreCase("color")) {
                colDef.setColumnColor(new Color(value));
            } else if (property.equalsIgnoreCase("digits")) {
                colDef.setColumnDigits(value);
            } else if (property.equalsIgnoreCase("sort")) {
                if (value >= 0 && value <= 2) {
                    columns.setColumnLinkedSortFlag(value);
                    columns.setColumnLinkedSortName(colDef.getName());
                }

            } else if (property.equalsIgnoreCase("width")) {
                if (value >= 40 && value <= 200) {
                    colDef.setColumnWidth(value);
                }

            } else if (property.equalsIgnoreCase("histo")) {
                double rightVal = Double.parseDouble(rightPart);
                if (leftPart.equals("binwidth")) {
                    colDef.setHistoBinWidth(rightVal);
                }
                if (leftPart.equals("xmin")) {
                    colDef.setHistoXMin(rightVal);
                }
                if (leftPart.equals("xmax")) {
                    colDef.setHistoXMax(rightVal);
                }

            } else {
                ImageJAccessOJ.InterpreterAccess.interpError("Allowed terms: visible, color, digits, sort, width");
                return;

            }

            OJ.getEventProcessor().fireColumnChangedEvent(col.getName(), col.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
        }

    }

    public int cellID(int index) {
        return OJ.getData().getCells().getCellByIndex(index - one).getID();
    }

    public int idToIndex(int id) {
        CellsOJ cells = OJ.getData().getCells();
        for (int jj = 0; jj < cells.getCellsCount(); jj++) {
            if (cells.getCellByIndex(jj).getID() == id) {
                return jj + 1;
            }
        }
        return 0;
    }

    public void setComposite(boolean compositeFlag) {
        OJ.getData().getYtemDefs().setComposite(compositeFlag);
        OJ.getEventProcessor().fireYtemDefChangedEvent(null, YtemDefChangedEventOJ.COLLECT_MODE_CHANGED);
    }

    public void lineToPolygon(double lw, boolean roundFlag) {
        ImagePlus imp = IJ.getImage();
        if (imp == null) {
            return;
        }
        Roi roi = imp.getRoi();
        int typ = roi.getType();
        if (roi == null || !(typ == Roi.POLYLINE || typ == Roi.FREELINE)) {//8.5.2014
            ImageJAccessOJ.InterpreterAccess.interpError("Roi of type PolyLine or FeeLine expected");
            return;
        }
        if (lw == 0) {
            lw = roi.getStrokeWidth();//8.5.2014
        }
        FloatPolygon fp = roi.getFloatPolygon();
        //IJ.log("Converting a line into a polygon");

        int len = fp.npoints;
        double[] x = new double[len];
        double[] y = new double[len];
        for (int jj = 0; jj < len; jj++) {
            x[jj] = fp.xpoints[jj];
            y[jj] = fp.ypoints[jj];
        }
        double[] leftX = new double[len];

        double[] rightX = new double[len];
        double[] leftY = new double[len];
        double[] rightY = new double[len];
        double PI = Math.PI;
        double halfdev = 0, firstPhi = 0, lastPhi = 0, phi3;

        for (int i = 1; i < len - 1; i++) {
            double dxLeft = x[i - 1] - x[i];
            double dxRight = x[i + 1] - x[i];
            double dyLeft = y[i - 1] - y[i];
            double dyRight = y[i + 1] - y[i];

            double dotprod = dxRight * dxLeft + dyRight * dyLeft;
            double crossprod = dxRight * dyLeft - dyRight * dxLeft;

            double phiV = Math.atan2(crossprod, dotprod);//phi of this vertex
            if (phiV >= 0) {
                halfdev = (PI - phiV) / 2; //Half deviation
            }
            if (phiV < 0) {
                halfdev = (-PI - phiV) / 2;
            }
            //IJ.log("phiV = " + (phiV * 180 / PI) + " halfdev = " + (halfdev * 180 / PI));
            double rad = Math.abs(lw / 2 / Math.cos((PI - phiV) / 2));
            double phiLeft = Math.atan2(-dyLeft, -dxLeft);
            double phiMean = phiLeft + halfdev;
            if (i == 1) {
                firstPhi = phiLeft;
            }
            double phiRight = Math.atan2(dyRight, dxRight);
            lastPhi = phiRight;
            phi3 = phiMean + PI / 2;
            double dx = Math.cos(phi3) * rad;
            double dy = Math.sin(phi3) * rad;
            leftX[i] = x[i] + dx;
            leftY[i] = y[i] + dy;
            rightX[i] = x[i] - dx;
            rightY[i] = y[i] - dy;
        }
        double dx = lw / 2 * Math.cos(firstPhi + PI / 2);
        double dy = lw / 2 * Math.sin(firstPhi + PI / 2);
        leftX[0] = x[0] + dx;
        leftY[0] = y[0] + dy;
        rightX[0] = x[0] - dx;
        rightY[0] = y[0] - dy;
        dx = lw / 2 * Math.cos(lastPhi + PI / 2);
        dy = lw / 2 * Math.sin(lastPhi + PI / 2);
        leftX[len - 1] = x[len - 1] + dx;
        leftY[len - 1] = y[len - 1] + dy;
        rightX[len - 1] = x[len - 1] - dx;
        rightY[len - 1] = y[len - 1] - dy;
        float[] polygonX = new float[len * 2];
        float[] polygonY = new float[len * 2];
        for (int jj = 0; jj < len; jj++) {
            polygonX[jj] = (float) leftX[len - jj - 1];
            polygonY[jj] = (float) leftY[len - jj - 1];
            polygonX[jj + len] = (float) rightX[jj];
            polygonY[jj + len] = (float) rightY[jj];
        }

        roi = null;

        roi = new PolygonRoi(polygonX, polygonY, Roi.POLYGON);
        roi.setStrokeWidth(1);
        imp.setRoi(roi, true);
    }

    public void linkImage() {
        ImagePlus imp = IJ.getImage();
        OJ.getImageProcessor().addImage(imp);
    }

    public int lastRow(String columnName) {
        int last = 0;
        ColumnsOJ columns = OJ.getData().getResults().getColumns();
        ArrayList matchingColumns = columns.getColumnsByWildcard(columnName);
        for (int jj = 0; jj
                < matchingColumns.size(); jj++) {
            ColumnOJ col = (ColumnOJ) matchingColumns.get(jj);
            if (col.isUnlinkedColumn()) {
                int value = col.getResultCount();
                last
                        = value > last ? value : last;
            }

        }
        return last;
    }

    public void extendVisibilityDepth(int lowValue, int highValue) {
        OJ.getData().getYtemDefs().setVisRange(lowValue, highValue);
        OJ.getImageProcessor().updateOpenImages(); //31.5.2012
    }

    public void showProject() {
        ij.IJ.showStatus("ojShowProject is not implemented");
    }

    public void showResults() {
        oj.gui.menuactions.ViewActionsOJ.ResultsViewAction.actionPerformed(null);
    }

//    public void hideResults() {
//        oj.gui.menuactions.ViewActionsOJ.ResultsViewAction.actionPerformed(null);
//    }
    public void showTools() {
        oj.gui.menuactions.ViewActionsOJ.YtemListAction.actionPerformed(null);
    }

    public void showMap(String pattern) {
        ij.IJ.showStatus("showMap " + pattern);
    }

    public void deleteMap(String pattern) {
        ij.IJ.showStatus("deleteMap " + pattern);
    }

    public int ojMapCount(String pattern) {
        ij.IJ.showStatus("ojMapCount " + pattern);
        return -999;
    }

    public void storeMap(boolean keepFlag) {
        ij.IJ.showStatus("storeMap " + keepFlag);
    }

    public void initVertexStack(String dim) {
        VertexCalculatorOJ vc = OJ.getVertexCalculator();
        vc.init(dim);
    }

    public int ojvGetStackSize() {
        VertexCalculatorOJ vc = OJ.getVertexCalculator();
        return vc.size();
    }

    public double ojvGetResult(String str) {
        str = str.toLowerCase();
        VertexCalculatorOJ vc = OJ.getVertexCalculator();

        if (str.indexOf("innercircle") >= 0) {
            vc.calc("innerCircle");
            if (str.indexOf("radius") >= 0) {
                return vc.innerCircleRadius;
            }

            if (str.indexOf("centerx") >= 0) {
                return vc.innerCircleCenterX;
            }

            if (str.indexOf("centery") >= 0) {
                return vc.innerCircleCenterY;
            }

        } else if (str.indexOf("outercircle") >= 0) {
            vc.calc("outerCircle");
            if (str.indexOf("radius") >= 0) {
                return vc.outerCircleRadius;
            }

            if (str.indexOf("centerx") >= 0) {
                return vc.outerCircleCenterX;
            }

            if (str.indexOf("centery") >= 0) {
                return vc.outerCircleCenterY;
            }

        } else if (str.indexOf("partialpath") >= 0) {
            vc.calc("partialpath");
            if (str.indexOf("impactx") >= 0) {
                return vc.impactX;
            }

            if (str.indexOf("path", 10) >= 0) {
                return vc.partialPath;
            }

            if (str.indexOf("impacty") >= 0) {
                return vc.impactY;
            }

            if (str.indexOf("offset") >= 0) {
                if (str.indexOf("signedoffset") >= 0) {
                    return vc.signedMinSpark;
                }
                return vc.minSpark;
            }

            if (str.indexOf("leftedge") >= 0) {
                return vc.leftEdge;
            }

            if (str.indexOf("rightedge") >= 0) {
                return vc.rightEdge;
            }

            if (str.equalsIgnoreCase("partialpath")) {
                return vc.partialPath;
            }

        } else if (str.equalsIgnoreCase("totalpath")) {
            vc.calc("totalpath");
            return vc.totalLength;
        } else if (str.equalsIgnoreCase("perimeter")) {
            vc.calc("perimeter");
            return vc.perimeter;
        } else if (str.equalsIgnoreCase("crosspointx")) {
            vc.calc("crosspoint");
            return vc.crossPoint.x;
        } else if (str.equalsIgnoreCase("crosspointy")) {
            vc.calc("crosspoint");
            return vc.crossPoint.y;
        } else if (str.equalsIgnoreCase("height")) {
            vc.calc("height");
            return vc.height;
        } else if (str.equalsIgnoreCase("deviation")) {
            vc.calc("deviation");
            return vc.deviation;
        } else if (str.equalsIgnoreCase("angle")) {
            vc.calc("angle");
            return vc.angle;
        } else if (str.equalsIgnoreCase("area")) {
            vc.calc("area");
            return vc.area;
        } else if (str.equalsIgnoreCase("orientation")) {
            vc.calc("orientation");
            return vc.orientation;
        } else if (str.startsWith("partialpositionx")) {
            vc.calc(str);
            return vc.partialPositionX;
        } else if (str.startsWith("partialpositiony")) {
            vc.calc(str);
            return vc.partialPositionY;
        } else {
            ImageJAccessOJ.InterpreterAccess.interpError("Algorithm '" + str + "' not found");
        }

        return 0;
    }

    public void ojvPushRoi() {
        VertexCalculatorOJ vc = OJ.getVertexCalculator();
        ImagePlus imp = ij.WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }

        Roi roi = imp.getRoi();
        if (roi == null) {
            ImageJAccessOJ.InterpreterAccess.interpError("No roi exists");
            return;

        }

        Polygon p = roi.getPolygon();
        FloatPolygon fp = roi.getFloatPolygon();
        double[] xpoints = new double[p.npoints];
        double[] ypoints = new double[p.npoints];

        if (fp != null) { //spline fit polygon
            for (int i = 0; i
                    < p.npoints; i++) {
                xpoints[i] = fp.xpoints[i];
                ypoints[i] = fp.ypoints[i];
            }

        } else {
            for (int i = 0; i
                    < p.npoints; i++) {
                xpoints[i] = p.xpoints[i];
                ypoints[i] = p.ypoints[i];
            }

        }

        for (int jj = 0; jj < xpoints.length; jj++) {
            LocationOJ loc = new LocationOJ(xpoints[jj], ypoints[jj], 0);
            vc.push((Object) loc);
        }

    }

    public void pushYtem() {
        VertexCalculatorOJ vc = OJ.getVertexCalculator();
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell == null) {
            return;
        }

        YtemOJ ytem = cell.getSelectedYtem();
        if (ytem == null) {
            return;
        }

        for (int jj = 0; jj < ytem.getLocationsCount(); jj++) {
            LocationOJ loc = ytem.getLocation(jj);
            LocationOJ loc2 = new LocationOJ(loc.x, loc.y, loc.z);
            if (!vc.threeD) {
                loc2.z = 0;
            }
            vc.push((Object) loc2);
        }

    }

    public void pushPoint(int index) {
        VertexCalculatorOJ vc = OJ.getVertexCalculator();
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell == null) {
            return;
        }

        YtemOJ ytem = cell.getSelectedYtem();
        if (ytem == null) {
            return;
        }

        LocationOJ loc = ytem.getLocation(index - one);
        LocationOJ loc2 = new LocationOJ(loc.x, loc.y, loc.z);
        if (!vc.threeD) {
            loc2.z = 0;
        }
        vc.push((Object) loc2);

    }

    public double[] xYZPos(int index) {
        index = index - one;
        double[] ar = new double[3];
        ar[0] = ar[1] = ar[2] = Double.NaN;
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell == null) {
            return ar;
        }

        YtemOJ ytem = cell.getSelectedYtem();
        if (ytem == null) {
            return ar;
        }

        if (index < 0 || index >= ytem.getLocationsCount()) {
            return ar;
        }

        LocationOJ thisLoc = ytem.getLocation(index);
        ar[0] = thisLoc.getX();
        ar[1] = thisLoc.getY();
        ar[2] = thisLoc.getZ();
        return ar;
    }

    public double getVertexX(int index) {
        int size = OJ.getVertexCalculator().size();
        if (index >= 0 && index < size) {
            LocationOJ loc = (LocationOJ) OJ.getVertexCalculator().get(index);
            double x = loc.getX();
            x
                    += 0;
            return x;
        }

        return Double.NaN;
    }

    public double getVertexY(int index) {
        int size = OJ.getVertexCalculator().size();
        if (index >= 0 && index < size) {
            LocationOJ loc = (LocationOJ) OJ.getVertexCalculator().get(index);
            double y = loc.getY();
            return y;
        }

        return Double.NaN;
    }

    public double getVertexZ(int index) {
        int size = OJ.getVertexCalculator().size();
        if (index >= 0 && index < size) {
            LocationOJ loc = (LocationOJ) OJ.getVertexCalculator().get(index);
            double z = loc.getZ();
            return z;
        }

        return Double.NaN;
    }

    public void pushVertex(double[] fa) {
        double zz = 0.0;
        if (fa.length == 3) {
            zz = fa[2];
        } else if (fa.length != 2) {
            ImageJAccessOJ.InterpreterAccess.interpError("vertex must be array with 2 (x,y) or 3 (x, y, z) elements");
            return;

        }

        LocationOJ loc = new LocationOJ(fa[0], fa[1], zz);
        OJ.getVertexCalculator().push((Object) loc);
    }

    public double[] invertFloatArray(double[] fa) {
        for (int jj = 0; jj
                < fa.length; jj++) {
            fa[jj] = -fa[jj];
        }

        return (fa);
    }

// converts eg "1.0" correctly to int
    public static int parseInt(String value) {
        int returnVal = 0;
        try {
            returnVal = (int) Math.round(Double.parseDouble(value));
        } catch (IllegalArgumentException ex) {
            ImageJAccessOJ.InterpreterAccess.interpError("\"" + value + "\" is not an integer");
        }

        return returnVal;
    }

    public static double parseDouble2(String value) {
        double returnVal = Double.NaN;
        try {
            returnVal = Double.parseDouble(value);
        } catch (IllegalArgumentException ex) {
        }//without exception; n_ 29.5.2008

        return returnVal;
    }

    public static double parseDouble(String value) {
        double returnVal = Double.NaN;
        try {
            returnVal = Double.parseDouble(value);
        } catch (IllegalArgumentException ex) {
            ImageJAccessOJ.InterpreterAccess.interpError("\"" + value + "\" is not a float");
        }

        return returnVal;
    }

    public static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        int intValue = MacroProcessorOJ.parseInt(value);
        return intValue > 0 ? true : false;
    }

    public static void makeBoundingRoi(String value) {
        value = value.toLowerCase();
        boolean isCircle = value.indexOf("circle") >= 0;
        boolean isObj = value.indexOf("object") >= 0;
        boolean isRect = value.indexOf("rectangle") >= 0;
        boolean isSquare = value.indexOf("square") >= 0;
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        if (cell != null) {
            Rectangle rr = cell.getRectangle();
            int dia = Math.max(rr.width, rr.height);
            //to be continued, 10.7.2008, n_
        }
    }

    public double[] getGlass() {
        double[] ar = new double[4];

        GlassWindowOJ glassWindow = GlassWindowOJ.getInstance();
        ar[0] = GlassWindowOJ.getInstance().getComponent(0).getLocationOnScreen().x;
        ar[1] = GlassWindowOJ.getInstance().getComponent(0).getLocationOnScreen().y;
        ar[2] = GlassWindowOJ.getInstance().getComponent(0).getWidth();
        ar[3] = GlassWindowOJ.getInstance().getComponent(0).getHeight();
        return ar;
    }

    public void setGlass(String properties) {
        if (properties.equals("")) {
            GlassWindowOJ glassWindow = GlassWindowOJ.getInstance();
            return;
        }
        properties = properties.toLowerCase();

        if (properties.equals("connect")) {
            GlassWindowOJ glassWindow = GlassWindowOJ.getInstance();
            glassWindow.setImagePlus(IJ.getImage());
            glassWindow.update();
        }
        if (properties.equals("show")) {
            //if (GlassWindowOJ.exists()) {
            GlassWindowOJ glassWindow = GlassWindowOJ.getInstance();
            glassWindow.setVisible(true);
            //}
        }

        if (properties.startsWith("color=0x")) {
            String s = properties.substring(8);

            int backgroundColor = Integer.parseInt(s, 16);
            if (backgroundColor == 0) {
                IJ.error("GlassWindow color must be > 0");
            } else {
                GlassWindowOJ.backgroundColor = backgroundColor;
            }
        }
        if (properties.equals("hide")) {
            if (GlassWindowOJ.exists()) {
                GlassWindowOJ glassWindow = GlassWindowOJ.getInstance();
                glassWindow.setVisible(false);
                GlassWindowOJ.glassWin = null;
            }
        }
        if (properties.equals("refresh")) {
            GlassWindowOJ.robotCommand = GlassWindowOJ.GRAB_TO_CURRENT;
        }

        if (properties.equals("addslice")) {
            GlassWindowOJ.robotCommand = GlassWindowOJ.GRAB_TO_CURRENT;
        }

    }

    public int rankToIndex(int rank) {
        int[] indexes = OJ.getData().getResults().getSortedIndexes(false);
        rank -= one;
        if (rank >= 0 && rank < indexes.length) {
            return indexes[rank] + one;
        } else {
            return 0;
        }
    }

    public int indexToRank(int index) {
        index -= one;
        int[] indexes = OJ.getData().getResults().getSortedIndexes(false);
        for (int rank = 0; rank < indexes.length; rank++) {
            if (indexes[rank] == index) {
                return rank + one;
            }
        }
        return 0;
    }
}
