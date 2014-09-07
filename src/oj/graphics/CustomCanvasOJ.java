/*
 * CustomCanvasOJ.java
 * fully documented on 15.3.2010
 *
 * extends ImageCanvas to append routines for drawing markers after
 * ImageJ is finished with drawing the image
 */
package oj.graphics;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.gui.ImageCanvas;
import ij.gui.NewImage;
import ij.macro.Interpreter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import oj.OJ;
import oj.project.CellOJ;
import oj.project.DataOJ;
import oj.project.ImageOJ;
import oj.project.YtemOJ;
import oj.project.YtemDefOJ;
import oj.gui.KeyEventManagerOJ;
import oj.processor.ToolStateProcessorOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.YtemChangedEventOJ;
import oj.processor.events.YtemChangedListenerOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.EventProcessorOJ;
import oj.processor.ImageProcessorOJ;
import oj.processor.events.DrawCellEventOJ;
import oj.processor.events.DrawCellListenerOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.project.YtemDefsOJ;

public class CustomCanvasOJ extends ImageCanvas implements DrawCellListenerOJ, CellChangedListenerOJ, YtemChangedListenerOJ, YtemDefChangedListenerOJ {

    public static int hits = 0;
    public static int markerRad = 2;
    public static int markerSize = 4;
    private String imageName;
    private DataOJ dataOJ;
    private ImageOJ image;
    public BufferStrategy buforowanie;
    public static Font fontArial = Font.decode("Arial-11");
    public static Font fontArialItalic = Font.decode("Arial-ITALIC-11");
    private Image offScreenImage;
    private int offScreenWidth = 0;
    private int offScreenHeight = 0;

    //called by applyImageGraphics
    public CustomCanvasOJ(ImagePlus imp, DataOJ dataOJ, String imageName) {
        super(imp);
        this.dataOJ = dataOJ;
        this.imageName = imageName;

        addKeyListener(KeyEventManagerOJ.getInstance());

        image = dataOJ.getImages().getImageByName(imageName);

        OJ.getEventProcessor().addYtemDefChangedListener(this);
        OJ.getEventProcessor().addYtemChangedListener(this);
        OJ.getEventProcessor().addCellChangedListener(this);
        OJ.getEventProcessor().addDrawCellListener(this);
        OJ.getEventProcessor().updateMarkers = true;//10.2.2011
        requestFocus();
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    /**
     * repaints image if cell has changed
     */
    public void drawCell(DrawCellEventOJ evt) {
        if (imp == null) {
            return;
        } else if (imp.getWindow() == null) {
            return;
        } else if (imp.getWindow().isVisible()) {
            repaint();
        }
    }

    public void cellChanged(CellChangedEventOJ evt) {
        if (imp == null) {
            return;
        } else if (imp.getWindow() == null) {
            return;
        } else if (imp.getWindow().isVisible()) {
            repaint();
        }
    }

    boolean batchFilter() {

        if (Macro.getOptions() != null && Interpreter.isBatchMode()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * repaints image if ytem has changed
     */
    public void ytemChanged(YtemChangedEventOJ evt) {
        if (imp == null) {
            return;
        } else if (imp.getWindow() == null) {
            return;
        } else if (imp.getWindow().isVisible()/*
                 * && batchFilter()
                 */) {//6.2.2011
            repaint();
        }
    }

    public String getImageName() {
        return imageName;
    }

    /**
     * first ImageJ repaints image, then markers are superimposed
     */
//  public void paintOld(Graphics g) {
//    super.paint(g);
//    drawOverlayoj(g);
//  }
    @Override
    public void resetDoubleBuffer() {
        super.resetDoubleBuffer();
        offScreenImage = null;

    }

    @Override
    //first create an offscreen image, then pass it to ImageJ-Canvas
    //then add my own drawing,
    //then take the original graphport and draw the complete offscreen image onto it.
    // problem: the entire image is
    public void paint(Graphics g) {
        hits++;
        if (!IJ.isWindows() || !OJ.doubleBuffered) {
            super.paint(g);
            drawOverlayoj(g);

        } else {
            Graphics g2 = getOffscreenGraphics();//3.2.2011
            super.paint(g2);
            drawOverlayoj(g2);
            g.drawImage(offScreenImage, 0, 0, null);
        }
    }

    Graphics getOffscreenGraphics() {
        final int srcRectWidthMag = (int) (srcRect.width * magnification);
        final int srcRectHeightMag = (int) (srcRect.height * magnification);
        if (offScreenImage == null || offScreenWidth != srcRectWidthMag || offScreenHeight != srcRectHeightMag) {
            offScreenImage = createImage(srcRectWidthMag, srcRectHeightMag);
            offScreenWidth = srcRectWidthMag;
            offScreenHeight = srcRectHeightMag;
        }
        return offScreenImage.getGraphics();
    }

    public void makeFlattenedImage() {//19.10.2011
        if (ImageProcessorOJ.isFrontImageLinked()) {
            String name = "Flat-" + imp.getTitle();
            ImagePlus flatImp = NewImage.createRGBImage(name, imp.getWidth(), imp.getHeight(), 1, 0);
            BufferedImage bufferedImage = flatImp.getBufferedImage();

            Graphics g3 = bufferedImage.getGraphics();
            super.paint(g3);
            drawOverlayoj(g3);
            flatImp.setImage(bufferedImage);
            flatImp.show();
        } else {
            IJ.error("Front image is not linked");
        }
    }

    /**
     * offscreen to screen coordinate conversion
     */
    public int screenX(double x, double magnification) {
        return (int) Math.round((x - srcRect.x) * magnification);
    }

    /**
     * offscreen to screen coordinate conversion
     */
    public int screenY(double y, double magnification) {
        return (int) Math.round((y - srcRect.y) * magnification);
    }

    /**
     * screen to offscreen coordinate conversion
     */
    public double offScreenX(int x, double magnification) {
        return srcRect.x + x / magnification;
    }

    /**
     * screen to offscreen coordinate conversion
     */
    public double offScreenY(int y, double magnification) {
        return srcRect.y + y / magnification;
    }

    /**
     * Don't switch to ImageJ's cursors when an ObjectJ tool is selected (except
     * the hand tool)
     */
    @Override
    public void setCursor(int sx, int sy, int ox, int oy) {
        if (!OJ.isProjectOpen) {
            return;
        }
        if ((!IJ.spaceBarDown()) && (OJ.getToolStateProcessor().getToolState() != ToolStateProcessorOJ.STATE_NONE)) {
            //ImageCanvas uses these values as result for getCursorLocation function
            xMouse = ox;
            yMouse = oy;
        } else {
            super.setCursor(sx, sy, ox, oy);
        }
    }

    /**
     * ---
     */
    public void updateCanvas(ImageCanvas ic, ImagePlus imp) {
        if (ic == null || ic == this || imp == null) {
            return;
        }
        if (imp.getWidth() != imageWidth || imp.getHeight() != imageHeight) {
            return;
        }
        srcRect = new Rectangle(ic.getSrcRect().x, ic.getSrcRect().y, ic.getSrcRect().width, ic.getSrcRect().height);
        setMagnification(ic.getMagnification());
        setDrawingSize(ic.getPreferredSize().width, ic.getPreferredSize().height);
    }

    public void setSrcRect(int x, int y, int w, int h) {//n_ 26.3.2008
        this.srcRect.setBounds(x, y, w, h);
    }

    /**
     * calculates channel from stackindex (former slice number) (1-based)
     */
    public static int getChannel(ImagePlus imp, int stackIndex) {
        int[] dim = imp.getDimensions();//{width, height, nChannels, nSlices, nFrames}
        return ((stackIndex - 1) % dim[2]) + 1;
    }

    /**
     * calculates z-slice number from stackindex (1-based)
     */
    public static int getSlice(ImagePlus imp, int stackIndex) {
        int[] dim = imp.getDimensions();
        return (((stackIndex - 1) / dim[2]) % dim[3]) + 1;
    }

    /**
     * calculates frame number from stackindex (1-based)
     */
    public static int getFrame(ImagePlus imp, int stackIndex) {
        int[] dim = imp.getDimensions();
        return (((stackIndex - 1) / (dim[2] * dim[3])) % dim[4]) + 1;
    }

    /**
     * returns true if channel content is visible- even though it may be blended
     * with others
     */
    public static boolean isActiveChannel(ImagePlus imp, int channel) {
        boolean[] active = ((CompositeImage) imp).getActiveChannels();
        return active[channel];
    }

    /**
     * Draws non-destructive markers
     */
    public void drawOverlayoj(Graphics g) {//25.3.2012 made public

        if (g == null || image == null || dataOJ == null ||  !OJ.isProjectOpen) {
            return;//11.10.2012
        }
        if (IJ.isMacro() && OJ.getMacroProcessor().getTargetImage() != null) {
            return;
        }
         boolean allvisible = true;
        YtemDefsOJ ytemDefs = OJ.getData().getYtemDefs();
        if (ytemDefs != null) {
            allvisible = ytemDefs.isCellLayerVisible();
        }

        //       if (OJ.getData().getYtemDefs().isCellLayerVisible()) {
        if (allvisible) {
            int visRangeHigh = OJ.getData().getYtemDefs().getVisRangeHigh();//28.1.2009
            int visRangeLow = OJ.getData().getYtemDefs().getVisRangeLow();
            FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
            Rectangle rectangle = getSrcRect();
            double magnif = getMagnification();
            int firstCell = image.getFirstCell();
            int lastCell = image.getLastCell();
            int imageStackIndex = imp.getCurrentSlice();
            boolean show_cell_number = dataOJ.getYtemDefs().getShowCellNumber();
            int nCells = dataOJ.getCells().getCellsCount();
            if ((firstCell >= 0) && (lastCell >= firstCell)) {
                for (int i = firstCell; i <= lastCell; i++) {
                    boolean inRange = (i >= 0 && i < nCells);//14.11.2008
                    if (!inRange) {
                        IJ.log("Range error: index=" + i + "  nCells=" + nCells);
                    }
                    if (inRange) {
                        CellOJ cell = dataOJ.getCells().getCellByIndex(i);
                        if (cell == null) {
                            //IJ.showMessage("Null-pointer"); could happen after deleting oocyte cell 25.4.2011
                            return;
                        }
                        int ytemsCount = cell.getYtemsCount();
                        for (int ytemNo = 0; ytemNo < ytemsCount; ytemNo++) {
                            YtemOJ ytem = cell.getYtemByIndex(ytemNo);
                            if (ytem != null) {//11.7.2011
                                int homeSlice = ytem.getStackIndex();
                                if (homeSlice == 0) {
                                    homeSlice = cell.getStackIndex();
                                }
                                if ((imp instanceof CompositeImage) && (((CompositeImage) imp).getMode() == CompositeImage.COMPOSITE)) {
                                    int channel = getChannel(imp, homeSlice);
                                    int slice = getSlice(imp, homeSlice);
                                    int frame = getFrame(imp, homeSlice);
                                    if (!isActiveChannel(imp, channel - 1)) {
                                        continue;
                                    }
                                    if ((slice != imp.getSlice()) || (frame != imp.getFrame())) {
                                        continue;
                                    }
                                } else {
                                    int upperLimit = homeSlice + visRangeHigh;
                                    if (visRangeHigh == -1) {
                                        upperLimit = Integer.MAX_VALUE;
                                    }
                                    int lowerLimit = homeSlice - visRangeLow;
                                    if (visRangeLow == -1) {
                                        lowerLimit = 0;
                                    }

                                    boolean visible = (imageStackIndex >= lowerLimit) && (imageStackIndex <= upperLimit);//28.1.2009

                                    if (!visible) {
                                        continue;
                                    }
                                }
                                YtemDefOJ ydef = dataOJ.getYtemDefs().getYtemDefByName(ytem.getYtemDef());
                                if ((ydef != null) && ydef.isVisible()) {
                                    int ydefType = ydef.getLineType();
                                    g.setColor(ydef.getLineColor());
                                    double line_width = 1.0;
                                    switch (ydefType) {
                                        case YtemDefOJ.LINE_TYPE_ZEROPT:
                                            line_width = 0;
                                            break;
                                        case YtemDefOJ.LINE_TYPE_TWOPT:
                                            line_width = 2.0;
                                            break;
                                        case YtemDefOJ.LINE_TYPE_THREEPT:
                                            line_width = 3.0;
                                            break;
                                    }
                                    if (ydefType == YtemDefOJ.LINE_TYPE_DOTTED) {
                                        float[] dash = new float[]{3, 3};
                                        ((Graphics2D) g).setStroke(new BasicStroke((float) line_width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0));
                                    } else if (ydefType == YtemDefOJ.LINE_TYPE_LIGHT_DOTTED) {
                                        float[] dash = new float[]{3, 5};
                                        ((Graphics2D) g).setStroke(new BasicStroke((float) line_width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0));
                                    } else {
                                        ((Graphics2D) g).setStroke(new BasicStroke((float) line_width));
                                    }

                                    int[] x_array;
                                    int[] y_array;
                                    if (!((magnif == 1.0 && rectangle.x == 0 && rectangle.y == 0))) {
                                        x_array = ytem.toXArray(rectangle.x, magnif);
                                        y_array = ytem.toYArray(rectangle.y, magnif);
                                    } else {
                                        x_array = ytem.toXArray();
                                        y_array = ytem.toYArray();
                                    }

                                    if (x_array.length > 0) {
                                        switch (ytem.getType()) {
                                            case YtemDefOJ.YTEM_TYPE_SEGLINE:
                                            case YtemDefOJ.YTEM_TYPE_LINE:
                                            case YtemDefOJ.YTEM_TYPE_ANGLE:
                                                if (ydefType != YtemDefOJ.LINE_TYPE_ZEROPT) {
                                                    g.drawPolyline(x_array, y_array, x_array.length);
                                                }
                                                break;
                                            case YtemDefOJ.YTEM_TYPE_POLYGON:
                                            case YtemDefOJ.YTEM_TYPE_ROI:
                                                if (ydefType != YtemDefOJ.LINE_TYPE_ZEROPT) {



                                                    if (ytem.isOpen()) {
                                                        g.drawPolyline(x_array, y_array, x_array.length);//don't close shape while it is open, 8.7.2009
                                                    } else {
                                                        g.drawPolygon(x_array, y_array, x_array.length);
                                                    }
                                                }
                                                break;
                                            default:
                                        }

                                        if (ytem.getType() != YtemDefOJ.YTEM_TYPE_ROI) {
                                            for (int j = 0; j < x_array.length; j++) {
                                                drawMarker((Graphics2D) g, x_array[j], y_array[j], ydef.getMarkerType(), cell.isSelected());
                                            }
                                        } else {
                                            drawMarker((Graphics2D) g, x_array[0], y_array[0], ydef.getMarkerType(), cell.isSelected());
                                        }
                                    }

                                    if (!EventProcessorOJ.BlockEventsOnDrag && (x_array.length > 0) && show_cell_number && (ytemNo == 0)) {
                                        int x = x_array[0];
                                        int y = y_array[0];
                                        if (x < 8) {
                                            x = x + 10;
                                        } else {
                                            x = x - 10;
                                        }
                                        if (y < 8) {
                                            y = y + 10;
                                        } else {
                                            y = y - 4 - markerRad + 2;
                                        }
                                        if (cell.isOpen()) {
                                            if (cell.isQualified()) {
                                                TextLayout layout = new TextLayout("*" + Integer.toString(i + 1) + "*", fontArialItalic, frc);//15.3.2010
                                                layout.draw((Graphics2D) g, (float) x, (float) y);
                                            } else {
                                                g.setColor(Color.GRAY);
                                                TextLayout layout = new TextLayout("{* " + Integer.toString(i + 1) + "}", fontArialItalic, frc);
                                                layout.draw((Graphics2D) g, (float) x, (float) y);
                                            }
                                        } else {
                                            if (cell.isQualified()) {
                                                TextLayout layout = new TextLayout(Integer.toString(i + 1), fontArial, frc);
                                                layout.draw((Graphics2D) g, (float) x, (float) y);
                                            } else {
                                                g.setColor(Color.LIGHT_GRAY);
                                                TextLayout layout = new TextLayout("{" + Integer.toString(i + 1) + "}", fontArial, frc);
                                                layout.draw((Graphics2D) g, (float) x, (float) y); //31.8.2009
                                                g.setColor(Color.DARK_GRAY);
                                                layout.draw((Graphics2D) g, (float) x + 1, (float) y);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * draws one of the marker types plus, square, cross, dot, pixel, diamond
     */
    private void drawMarker(Graphics2D g, int xpos, int ypos, int markerType, boolean selected) {
        if (selected) {
            g.fillRect(xpos - markerRad - 1, ypos - markerRad - 1, 2 * markerSize - 1, 2 * markerSize - 1);
        } else {
            ((Graphics2D) g).setStroke(new BasicStroke((float) 1.0));
            switch (markerType) {
                case YtemDefOJ.MARKER_TYPE_CROSS:
                    g.drawLine(xpos - markerRad, ypos - markerRad, xpos + markerRad, ypos + markerRad);
                    g.drawLine(xpos - markerRad, ypos + markerRad, xpos + markerRad, ypos - markerRad);
                    break;
                case YtemDefOJ.MARKER_TYPE_DIAMOND:
                    g.drawLine(xpos, ypos - markerRad, xpos - markerRad, ypos);
                    g.drawLine(xpos - markerRad, ypos, xpos, ypos + markerRad);
                    g.drawLine(xpos, ypos + markerRad, xpos + markerRad, ypos);
                    g.drawLine(xpos + markerRad, ypos, xpos, ypos - markerRad);
                    break;
                case YtemDefOJ.MARKER_TYPE_DOT:
                    g.fillRect(xpos - markerRad, ypos - markerRad, markerSize, markerSize);
                    break;
                case YtemDefOJ.MARKER_TYPE_PIXEL:
                    g.drawLine(xpos, ypos, xpos, ypos);
                    break;
                case YtemDefOJ.MARKER_TYPE_PLUS:
                    g.drawLine(xpos, ypos - markerRad, xpos, ypos + markerRad);
                    g.drawLine(xpos - markerRad, ypos, xpos + markerRad, ypos);
                    break;
                case YtemDefOJ.MARKER_TYPE_SQUARE:
                    g.drawRect(xpos - markerRad, ypos - markerRad, markerSize, markerSize);
                    break;
                default:
                    g.drawRect(xpos - markerRad, ypos - markerRad, markerSize, markerSize);
            }
        }
    }

    public void ytemDefChanged(YtemDefChangedEventOJ evt) {
        repaint();
    }
    //removed 15.3.2010 BasicStroke bs = new BasicStroke((float) 1.0, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{2, 2}, 0);

    /**
     * Zooms by zoomFactor, but doesn't touch window size and position. If
     * possible, Pixel (x, y) is made to appear at top left. If window is larger
     * then zoomed content, which would result in white borders, returns without
     * action.
     */
    public void ojZoom(double zoomFactor, int x, int y) {

        int w = (int) Math.round(dstWidth / zoomFactor);
        if (w * zoomFactor < dstWidth) {
            w++;

        }
        int h = (int) Math.round(dstHeight / zoomFactor);
        if (h * zoomFactor < dstHeight) {
            h++;

        }
        if (w > imageWidth || h > imageHeight) {
            return;
        }
        Rectangle r = new Rectangle(x - w / 2, y - h / 2, w, h);//17.6.2009
        if (r.x < 0) {
            r.x = 0;
        }
        if (r.y < 0) {
            r.y = 0;
        }
        if (r.x + w > imageWidth) {
            r.x = imageWidth - w;
        }
        if (r.y + h > imageHeight) {
            r.y = imageHeight - h;
        }

        srcRect = r;
        setMagnification(zoomFactor);

        repaint();
    }
}
