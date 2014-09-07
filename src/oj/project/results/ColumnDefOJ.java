package oj.project.results;

import java.awt.Color;
import java.util.ArrayList;
import oj.OJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;

public class ColumnDefOJ extends BaseAdapterOJ {

  private static final long serialVersionUID = -3420957260654932177L;
  public static final int ALGORITHM_CALC_DISTANCE = 1;
  public static final int ALGORITHM_CALC_PATH = 2;
  public static final int ALGORITHM_CALC_ABS_PARTIAL_PATH = 3;
  public static final int ALGORITHM_CALC_REL_PARTIAL_PATH = 4;
  public static final int ALGORITHM_CALC_XPOS = 5;
  public static final int ALGORITHM_CALC_YPOS = 6;
  public static final int ALGORITHM_CALC_ZPOS = 7;
  public static final int ALGORITHM_CALC_ORIENTATION = 8;
  public static final int ALGORITHM_CALC_ANGLE = 9;
  public static final int ALGORITHM_CALC_COUNT = 10;
  public static final int ALGORITHM_CALC_AREA = 11;
  public static final int ALGORITHM_CALC_SLICE = 12;
  public static final int ALGORITHM_CALC_IMAGE = 13;
  public static final int ALGORITHM_CALC_INDEX = 14;
  public static final int ALGORITHM_CALC_ID = 15;
  public static final int ALGORITHM_CALC_EXISTS = 16;
  public static final int ALGORITHM_CALC_LENGTH = 17;
  public static final int ALGORITHM_CALC_FILE_NAME = 18;
  public static final int ALGORITHM_LAST_AUTOMATIC = 18;
  public static final int ALGORITHM_CALC_LINKED_NUMBER = 19;
  public static final int ALGORITHM_CALC_LINKED_TEXT = 20;
  public static final int ALGORITHM_CALC_UNLINKED_NUMBER = 21;
  public static final int ALGORITHM_CALC_UNLINKED_TEXT = 22;
  public static final int ALGORITHM_CALC_OFFROAD = 23;
  private String name;
  private int algorithm = ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER;
  private Color columnColor = Color.BLACK;
  private int columnDigits = 2;
  private int columnWidth = 80;
  private boolean hidden = false;
  private ArrayList operands = new ArrayList();
  private double histoXMin = Double.NaN;//2.10.2009
  private double histoXMax = Double.NaN;//2.10.2009
  private int histoYMax = -1;//2.10.2009
  private double histoBinWidth = Double.NaN;//2.10.2009
  private String plotProperties = "";//16.09.2011

  public static String getAlgorithmName(int algorithm) {
    switch (algorithm) {
      case ColumnDefOJ.ALGORITHM_CALC_COUNT:
        return "count";
      case ColumnDefOJ.ALGORITHM_CALC_EXISTS:
        return "exists";
      case ColumnDefOJ.ALGORITHM_CALC_FILE_NAME:
        return "file";
      case ColumnDefOJ.ALGORITHM_CALC_INDEX:
        return "index";
      case ColumnDefOJ.ALGORITHM_CALC_ID:
        return "id";
      case ColumnDefOJ.ALGORITHM_CALC_IMAGE:
        return "image";
      case ColumnDefOJ.ALGORITHM_CALC_SLICE:
        return "slice";
      case ColumnDefOJ.ALGORITHM_CALC_PATH:
        return "path";
      case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
        return "length";
      case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:
        return "distance";
      case ColumnDefOJ.ALGORITHM_CALC_XPOS:
        return "xpos";
      case ColumnDefOJ.ALGORITHM_CALC_YPOS:
        return "ypos";
      case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
        return "zpos";
      case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
        return "angle";
      case ColumnDefOJ.ALGORITHM_CALC_ORIENTATION:
        return "orientation";
      case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
        return "abspartialpath";
      case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:
        return "relpartialpath";
      case ColumnDefOJ.ALGORITHM_CALC_AREA:
        return "area";
      case ColumnDefOJ.ALGORITHM_CALC_OFFROAD:
        return "offroad";
      case ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER:
        return "anynumber";
      case ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT:
        return "anytext";
      case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER:
        return "anynumber";
      case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT:
        return "anytext";
      default:
        return "anynumber";
    }
  }

  /* Those algorithms we don't need to check for the pointNo,
   * but we collect all points
   */
  public static boolean variableLength(int alg) {
    return alg == ALGORITHM_CALC_PATH || alg == ALGORITHM_CALC_AREA || alg == ALGORITHM_CALC_ABS_PARTIAL_PATH || alg == ALGORITHM_CALC_REL_PARTIAL_PATH || alg == ALGORITHM_CALC_OFFROAD;
  }

  /* Those algorithms we need to check for the pointNo
   */
  public static boolean fixedLength(int alg) {
    return alg == ALGORITHM_CALC_DISTANCE || alg == ALGORITHM_CALC_ORIENTATION || alg == ALGORITHM_CALC_ANGLE;
  }

  public static int getAlgorithm(String name, String algorithm) {
    if ("count".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_COUNT;
    } else if ("exists".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_EXISTS;
    } else if ("file".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_FILE_NAME;
    } else if ("id".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_ID;
    } else if ("index".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_INDEX; //27.9.2009
    } else if ("image".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_IMAGE;
    } else if ("none".equalsIgnoreCase(algorithm)) {
    } else if ("slice".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_SLICE;
    } else if ("path".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_PATH;
    } else if ("length".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_LENGTH;
    } else if ("distance".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_DISTANCE;
    } else if ("xpos".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_XPOS;
    } else if ("ypos".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_YPOS;
    } else if ("zpos".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_ZPOS;
    } else if ("angle".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_ANGLE;
    } else if ("orientation".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_ORIENTATION;
    } else if ("abspartialpath".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH;
    } else if ("relpartialpath".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH;
    } else if ("offroad".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_OFFROAD;
    } else if ("area".equalsIgnoreCase(algorithm)) {
      return ColumnDefOJ.ALGORITHM_CALC_AREA;
    } else if ("anynumber".equalsIgnoreCase(algorithm)) {
      if (isUnlinked(name)) {
        return ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER;
      } else {
        return ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER;
      }
    } else if ("anytext".equalsIgnoreCase(algorithm)) {
      if (isUnlinked(name)) {
        return ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT;
      } else {
        return ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT;
      }
    }
    return ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER;
  }

  public static boolean isUnlinked(String name) {
    if (name == null) {
      return false;
    } else {
      return name.startsWith("_");
    }
  }

  public ColumnDefOJ() {
  }

  public ColumnDefOJ(String name) {
    this.name = name;
  }

  public boolean getChanged() {
    if (super.getChanged()) {
      return true;
    } else {
      for (int i = 0; i < operands.size(); i++) {
        if (((OperandOJ) operands.get(i)).getChanged()) {
          return true;
        }
      }
    }
    return false;
  }

  public void setChanged(boolean changed) {
    super.setChanged(changed);
    for (int i = 0; i < operands.size(); i++) {
      ((OperandOJ) operands.get(i)).setChanged(changed);
    }
  }

  public void initAfterUnmarshalling(IBaseOJ parent) {
    super.initAfterUnmarshalling(parent);
    for (int i = 0; i < operands.size(); i++) {
      ((OperandOJ) operands.get(i)).initAfterUnmarshalling(this);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if ((this.name == null) || (!this.name.equals(name))) {
      String oldName = this.name;
      this.name = name;
      changed = true;
      OJ.getEventProcessor().fireColumnChangedEvent(oldName, name, ColumnChangedEventOJ.COLUMN_EDITED);
    }
  }

  public boolean isTextMode() {
    return ((getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT) || (getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT) || (getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_FILE_NAME));
  }

  public boolean isHidden() {
    return hidden;
  }

  public boolean isUnlinked() {
    return ColumnDefOJ.isUnlinked(name);
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
    changed = true;
  }

  public void setColumnColor(Color columnColor) {
    this.columnColor = columnColor;
    changed = true;
    OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
  }

  public Color getColumnColor() {
    return columnColor;
  }

  public void setColumnDigits(int columnDigits) {
    this.columnDigits = columnDigits;
    changed = true;
  }

  public void setHistoBinWidth(double histoBinWidth) {
    this.histoBinWidth = histoBinWidth;
    changed = true;
  }

  public void setHistoXMin(double val) {
    this.histoXMin = val;
    changed = true;
  }

  public void setHistoXMax(double val) {
    this.histoXMax = val;
    changed = true;
  }

  public void setHistoYMax(int val) {
    this.histoYMax = val;
    changed = true;
  }

  public void setPlotProperties(String s) {
    plotProperties = s;
  }

  public int getHistoYMax() {
    return histoYMax;
  }

  public double getHistoXMin() {
    return histoXMin;
  }

  public double getHistoBinWidth() {
    return histoBinWidth;
  }

  public double getHistoXMax() {
    return histoXMax;
  }

  public int getColumnDigits() {
    return columnDigits;
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
    changed = true;
  }

  public int getColumnWidth() {
    return columnWidth;
  }

  public String getPlotProperties() {
    if (plotProperties == null) {
      plotProperties = "";
    }
    return plotProperties;
  }

  public void setAlgorithm(int algorithm) {
    this.algorithm = algorithm;
    switch (algorithm) {
      case ColumnDefOJ.ALGORITHM_CALC_COUNT:
      case ColumnDefOJ.ALGORITHM_CALC_EXISTS:
      case ColumnDefOJ.ALGORITHM_CALC_ID:
      case ColumnDefOJ.ALGORITHM_CALC_INDEX:
      case ColumnDefOJ.ALGORITHM_CALC_IMAGE:
      case ColumnDefOJ.ALGORITHM_CALC_SLICE:
        columnDigits = 0;
        break;
      case ColumnDefOJ.ALGORITHM_CALC_FILE_NAME:
      case ColumnDefOJ.ALGORITHM_CALC_PATH:
      case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
      case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:
      case ColumnDefOJ.ALGORITHM_CALC_XPOS:
      case ColumnDefOJ.ALGORITHM_CALC_YPOS:
      case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
      case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
      case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
      case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:
      case ColumnDefOJ.ALGORITHM_CALC_AREA:
        columnDigits = 2;
        break;
      case ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT:
      case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT:
        columnDigits = 0;
        break;
      case ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER:
      case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER:
        columnDigits = 2;
        break;
    }
    changed = true;
    OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
  }

  public int getAlgorithm() {
    return algorithm;
  }

  public boolean addOperand(OperandOJ operand) {
    if (operands.add(operand)) {
      operand.setParent(this);
      changed = true;
      OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
      return true;
    }
    return false;
  }

  public OperandOJ setOperand(int index, OperandOJ operand) {
    OperandOJ old_operand = (OperandOJ) operands.get(index);
    operands.set(index, operand);
    operand.setParent(this);
    changed = true;
    OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    return old_operand;
  }

  public OperandOJ removeOperand(int index) {
    OperandOJ operand = (OperandOJ) operands.remove(index);
    changed = true;
    OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
    return operand;
  }

  public boolean removeOperand(OperandOJ operand) {
    if (operands.remove(operand)) {
      changed = true;
      OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
      return true;
    }
    return false;
  }

  public OperandOJ getOperand(int index) {
    if (index < operands.size()) {
      return (OperandOJ) operands.get(index);
    } else {
      return null;
    }
  }

  public int getOperandCount() {
    return operands.size();
  }

  public void clearOperands() {
    operands.clear();
    changed = true;
    OJ.getEventProcessor().fireColumnChangedEvent(getName(), getName(), ColumnChangedEventOJ.COLUMN_EDITED);
  }
}
