/*
 * YtemDefOJ.java
 *
 * defines properties of an ytem
 * fully documented 9.3.2010
 */
package oj.project;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import oj.OJ;
import oj.processor.events.YtemDefChangedEventOJ;

public class YtemDefOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -5201060761806552245L;
    public static final int YTEM_TYPE_NONE = 0;//not used
    public static final int YTEM_TYPE_POINT = 1;
    public static final int YTEM_TYPE_LINE = 3;
    public static final int YTEM_TYPE_SEGLINE = 4;
    public static final int YTEM_TYPE_POLYGON = 8;
    public static final int YTEM_TYPE_ROI = 9;
    public static final int YTEM_TYPE_ANGLE = 10;
    public static final int MARKER_TYPE_PLUS = 1;
    public static final int MARKER_TYPE_CROSS = 2;
    public static final int MARKER_TYPE_SQUARE = 3;
    public static final int MARKER_TYPE_DIAMOND = 4;
    public static final int MARKER_TYPE_DOT = 5;
    public static final int MARKER_TYPE_PIXEL = 6;
    public static final int LINE_TYPE_ONEPT = 1;
    public static final int LINE_TYPE_TWOPT = 2;
    public static final int LINE_TYPE_THREEPT = 3;
    public static final int LINE_TYPE_LIGHT_DOTTED = 4;
    public static final int LINE_TYPE_DOTTED = 5;
    public static final int LINE_TYPE_ZEROPT = 6;
    public static final int ADVANCE_TYPE_AUTOMATIC = 1;
    public static final int ADVANCE_TYPE_TABKEY = 2;
    private String ytemDefName;
    private String shortcut = "";//not used
    private int markerType = YtemDefOJ.MARKER_TYPE_DOT;
    private int ytemDefType = YtemDefOJ.YTEM_TYPE_LINE;//i.e. shapeIndex = 1
    private Color lineColor = Color.BLACK;
    private int lineType = YtemDefOJ.LINE_TYPE_ONEPT;
    private int advanceType = YtemDefOJ.ADVANCE_TYPE_AUTOMATIC;
    private int cloneMin = 1;
    private int cloneMax = 1;
    private transient boolean visible = true;

    public YtemDefOJ() {
    }

    /** create an ytem definition with this name */
    public YtemDefOJ(String ytemDefName) {
        this.ytemDefName = ytemDefName;
    }


    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        visible = true;
    }
    
    
    
    
    
            public static int getTypeIndex(int objectType) {
            switch (objectType) {
                case YtemDefOJ.YTEM_TYPE_ANGLE:
                    return 0;
                case YtemDefOJ.YTEM_TYPE_LINE:
                    return 1;
                case YtemDefOJ.YTEM_TYPE_POINT:
                    return 2;
                case YtemDefOJ.YTEM_TYPE_POLYGON:
                    return 3;
                case YtemDefOJ.YTEM_TYPE_ROI:
                    return 4;
                case YtemDefOJ.YTEM_TYPE_SEGLINE:
                    return 5;
            }
            return 0;
        }
            
            
            
            
             public static String getTypeName(int objectType) {
            switch (objectType) {
                case YtemDefOJ.YTEM_TYPE_ANGLE:
                    return "Angle";
                case YtemDefOJ.YTEM_TYPE_LINE:
                    return "Line";
                case YtemDefOJ.YTEM_TYPE_POINT:
                    return "Point";
                case YtemDefOJ.YTEM_TYPE_POLYGON:
                    return "Polygon";
                case YtemDefOJ.YTEM_TYPE_ROI:
                    return "Roi";
                case YtemDefOJ.YTEM_TYPE_SEGLINE:
                    return "Polyline";
            }
            return "";
        }

    /** @return line type as string */
    public static String getLineTypeName(int lineType) {
        switch (lineType) {
            case YtemDefOJ.LINE_TYPE_ONEPT:
                return "onept";
            case YtemDefOJ.LINE_TYPE_TWOPT:
                return "twopt";
            case YtemDefOJ.LINE_TYPE_THREEPT:
                return "threept";
            case YtemDefOJ.LINE_TYPE_LIGHT_DOTTED:
                return "lightdotted";
            case YtemDefOJ.LINE_TYPE_DOTTED:
                return "dotted";
            case YtemDefOJ.LINE_TYPE_ZEROPT:
                return "zeropt";
            default:
                return "onept";
        }
    }

    /** @return line type as integer */
    public static int getLineType(String line) {
        if ("onept".equals(line)) {
            return YtemDefOJ.LINE_TYPE_ONEPT;
        } else if ("twopt".equals(line)) {
            return YtemDefOJ.LINE_TYPE_TWOPT;
        } else if ("threept".equals(line)) {
            return YtemDefOJ.LINE_TYPE_THREEPT;
        } else if ("lightdotted".equals(line)) {
            return YtemDefOJ.LINE_TYPE_LIGHT_DOTTED;
        } else if ("dotted".equals(line)) {
            return YtemDefOJ.LINE_TYPE_DOTTED;
        } else if ("zeropt".equals(line)) {
            return YtemDefOJ.LINE_TYPE_ZEROPT;
        } else {
            return YtemDefOJ.LINE_TYPE_ONEPT;
        }
    }

    /** @return shape as string, e.g. "polygon" */
    public static String getShapeName(int objectType) {
        switch (objectType) {
            case YtemDefOJ.YTEM_TYPE_NONE:
                return "none";
            case YtemDefOJ.YTEM_TYPE_POINT:
                return "point";
            case YtemDefOJ.YTEM_TYPE_LINE:
                return "line";
            case YtemDefOJ.YTEM_TYPE_SEGLINE:
                return "segline";
            case YtemDefOJ.YTEM_TYPE_POLYGON:
                return "polygon";
            case YtemDefOJ.YTEM_TYPE_ROI:
                return "roi";
            case YtemDefOJ.YTEM_TYPE_ANGLE:
                return "angle";
            default:
                return "plus";
        }
    }

    /** @return index of shape */
    public static int getShapeIndex(String object) {
        if ("none".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_NONE;
        } else if ("point".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_POINT;
        } else if ("line".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_LINE;
        } else if ("segline".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_SEGLINE;
        } else if ("polygon".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_POLYGON;
        } else if ("roi".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_ROI;
        } else if ("angle".equals(object)) {
            return YtemDefOJ.YTEM_TYPE_ANGLE;
        } else {
            return YtemDefOJ.YTEM_TYPE_NONE;
        }
    }

    /** @return name of marker type, e.g. "plus" */
    public static String getMarkerTypeName(int markerType) {
        switch (markerType) {
            case YtemDefOJ.MARKER_TYPE_PLUS:
                return "plus";
            case YtemDefOJ.MARKER_TYPE_CROSS:
                return "cross";
            case YtemDefOJ.MARKER_TYPE_SQUARE:
                return "square";
            case YtemDefOJ.MARKER_TYPE_DIAMOND:
                return "diamond";
            case YtemDefOJ.MARKER_TYPE_DOT:
                return "dot";
            case YtemDefOJ.MARKER_TYPE_PIXEL:
                return "pixel";
            default:
                return "plus";
        }
    }

    /** @return index of marker type, e.g "cross" -> 2*/
    public static int getMarkerType(String marker) {
        if ("plus".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_PLUS;
        } else if ("cross".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_CROSS;
        } else if ("square".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_SQUARE;
        } else if ("diamond".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_DIAMOND;
        } else if ("dot".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_DOT;
        } else if ("pixel".equals(marker)) {
            return YtemDefOJ.MARKER_TYPE_PIXEL;
        } else {
            return YtemDefOJ.MARKER_TYPE_PLUS;
        }
    }

    /** @return "Advance" modus as string, e.g. "tabkey" */
    public static String getAdvanceTypeName(int advanceType) {
        switch (advanceType) {
            case YtemDefOJ.ADVANCE_TYPE_AUTOMATIC:
                return "automatic";
            case YtemDefOJ.ADVANCE_TYPE_TABKEY:
                return "tabkey";
            default:
                return "automatic";
        }
    }

    /** @return "Advance" modus as index */
    public static int getAdvanceType(String advance) {
        if ("automatic".equals(advance)) {
            return YtemDefOJ.ADVANCE_TYPE_AUTOMATIC;
        } else if ("tabkey".equals(advance)) {
            return YtemDefOJ.ADVANCE_TYPE_TABKEY;
        } else {
            return YtemDefOJ.ADVANCE_TYPE_AUTOMATIC;
        }
    }

    /** change name of this ytemDef */
    public void setYtemDefName(String ytemDefName) {
        String oldName = this.ytemDefName;
        this.ytemDefName = ytemDefName;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(oldName, ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);//1.12.2008
    }

    /** change marker type of this ytemDef , e.g 1= "plus marker*/
    public void setMarkerType(int markerType) {
        this.markerType = markerType;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set the shape type, e.g. 4 -> segline*/
    public void setYtemDefType(int ytemDefType) {
        this.ytemDefType = ytemDefType;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set color of this ytemdef */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set line type  of this ytemdef, e.g. 5 = dotted */
    public void setLineType(int lineType) {
        this.lineType = lineType;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set advance type of this ytemdef, e.g. tabkey = 2 */
    public void setAdvanceType(int advanceType) {
        this.advanceType = advanceType;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set minimum clone number of this ytemdef; eg all coli must have min 1 axis and min 0 constrictions */
    public void setCloneMin(int cloneMin) {
        this.cloneMin = cloneMin;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** set minimum clone number of this ytemdef; eg all coli must have max 1 axis */
    public void setCloneMax(int cloneMax) {
        this.cloneMax = cloneMax;
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDefName, YtemDefChangedEventOJ.YTEMDEF_EDITED);
    }

    /** shortcut currently not used */
    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
        changed = true;
    }

    /** @return name of this ytemdef; eg "Axis" */
    public String getYtemDefName() {
        return ytemDefName;
    }

    /** @return index of marker type, e.g 5 for "dot" */
    public int getMarkerType() {
        return markerType;
    }

    /** @return index of shape type, e.g 8 for polygon */
    public int getYtemType() {
        return ytemDefType;
    }

    /** @return index of advance type, e.g 2 for tabkey */
    public int getAdvanceType() {
        return advanceType;
    }

    /** @return color of this ytemDef */
    public Color getLineColor() {
        return lineColor;
    }

    /** @return index of linetype of this ytemDef, e.g. 5 for dotted line  */
    public int getLineType() {
        return lineType;
    }

    /** @return allowed minimum number of clones  */
    public int getCloneMin() {
        return cloneMin;
    }

    /** @return allowed max number of clones  */
    public int getCloneMax() {
        return cloneMax;
    }

    /** @return shortcut as string;-- not used  */
    public String getShortcut() {
        return shortcut;
    }

    /** @return visibility flag of this individual Ytemdef  */
    public boolean isVisible() {
        return visible;
    }

    /** set visibility flag of this individual Ytemdef  */
    public void setVisible(boolean visible) {
        this.visible = visible;
        OJ.getEventProcessor().fireYtemDefChangedEvent(getYtemDefName(), YtemDefChangedEventOJ.YTEMDEF_VISIBILITY_CHANGED);
    }
}
