/*
 * QualifyOJ.java
 */
package oj.project.results;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;

public class QualifierOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 4287534796167712480L;
    public static final int OPERATION_NONE = 0;
    public static final int OPERATION_LESS_THAN = 1;
    public static final int OPERATION_EQUAL = 2;
    public static final int OPERATION_GREATER_THAN = 3;
    public static final int OPERATION_LESS_OR_EQUAL = 4;
    public static final int OPERATION_NOT_EQUAL = 5;
    public static final int OPERATION_GREATER_OR_EQUAL = 6;
    public static final int OPERATION_WITHIN = 7;
    public static final int OPERATION_NOT_WITHIN = 8;
    public static final int OPERATION_EMPTY = 9;
    public static final int OPERATION_EXISTS = 10;
    private String columnName;
    private int operation = OPERATION_NONE;
    private String firstValue;
    private String secondValue;

    public QualifierOJ() {
    }

    public QualifierOJ(String columnName) {
        this.columnName = columnName;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(columnName);
        stream.writeInt(operation);
        stream.writeUTF(firstValue);
        stream.writeUTF(secondValue);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        columnName = stream.readUTF();
        operation = stream.readInt();
        firstValue = stream.readUTF();
        secondValue = stream.readUTF();
    }

    public static String getOperationText(int operation) {
        switch (operation) {
            case QualifierOJ.OPERATION_NONE:
                return " ";
            case QualifierOJ.OPERATION_LESS_THAN:
                return "<";
            case QualifierOJ.OPERATION_EQUAL:
                return "==";
            case QualifierOJ.OPERATION_GREATER_THAN:
                return ">";
            case QualifierOJ.OPERATION_LESS_OR_EQUAL:
                return "<=";
            case QualifierOJ.OPERATION_NOT_EQUAL:
                return "!=";
            case QualifierOJ.OPERATION_GREATER_OR_EQUAL:
                return ">=";
            case QualifierOJ.OPERATION_WITHIN:
                return "within";
            case QualifierOJ.OPERATION_NOT_WITHIN:
                return "notwithin";
            case QualifierOJ.OPERATION_EMPTY:
                return "empty";
            case QualifierOJ.OPERATION_EXISTS:
                return "exists";
            default:
                return "";
        }
    }

    public static String getOperationName(int operation) {
        switch (operation) {
            case QualifierOJ.OPERATION_NONE:
                return "none";
            case QualifierOJ.OPERATION_LESS_THAN:
                return "less";
            case QualifierOJ.OPERATION_EQUAL:
                return "equal";
            case QualifierOJ.OPERATION_GREATER_THAN:
                return "greater";
            case QualifierOJ.OPERATION_LESS_OR_EQUAL:
                return "lessorequal";
            case QualifierOJ.OPERATION_NOT_EQUAL:
                return "notequal";
            case QualifierOJ.OPERATION_GREATER_OR_EQUAL:
                return "greaterorequal";
            case QualifierOJ.OPERATION_WITHIN:
                return "within";
            case QualifierOJ.OPERATION_NOT_WITHIN:
                return "notwithin";
            case QualifierOJ.OPERATION_EMPTY:
                return "empty";
            case QualifierOJ.OPERATION_EXISTS:
                return "exists";
            default:
                return "none";
        }
    }

    public static int getOperation(String operation) {
        if ("none".equals(operation)) {
            return QualifierOJ.OPERATION_NONE;
        } else if ("less".equals(operation)) {
            return QualifierOJ.OPERATION_LESS_THAN;
        } else if ("equal".equals(operation)) {
            return QualifierOJ.OPERATION_EQUAL;
        } else if ("greater".equals(operation)) {
            return QualifierOJ.OPERATION_GREATER_THAN;
        } else if ("lessorequal".equals(operation)) {
            return QualifierOJ.OPERATION_LESS_OR_EQUAL;
        } else if ("notequal".equals(operation)) {
            return QualifierOJ.OPERATION_NOT_EQUAL;
        } else if ("greaterorequal".equals(operation)) {
            return QualifierOJ.OPERATION_GREATER_OR_EQUAL;
        } else if ("within".equals(operation)) {
            return QualifierOJ.OPERATION_WITHIN;
        } else if ("empty".equals(operation)) {
            return QualifierOJ.OPERATION_EMPTY;
        } else if ("notwithin".equals(operation)) {
            return QualifierOJ.OPERATION_NOT_WITHIN;
        } else if ("exists".equals(operation)) {
            return QualifierOJ.OPERATION_EXISTS;
        } else {
            return QualifierOJ.OPERATION_NONE;
        }
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFirstStringValue() {
        return firstValue;
    }

    public String getSecondStringValue() {
        return secondValue;
    }

    public double getFirstDoubleValue() {
        try {
            return Double.parseDouble(firstValue);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public double getSecondDoubleValue() {
        try {
            return Double.parseDouble(secondValue);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public int getOperation() {
        return operation;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        changed = true;
    }

    public void setFirstDoubleValue(double dvalue) {
        this.firstValue = Double.toString(dvalue);
        changed = true;
    }

    public void setSecondDoubleValue(double dvalue) {
        this.secondValue = Double.toString(dvalue);
        changed = true;
    }

    public void setFirstStringValue(String svalue) {
        this.firstValue = svalue;
        changed = true;
    }

    public void setSecondStringValue(String svalue) {
        this.secondValue = svalue;
        changed = true;
    }

    public void setOperation(int operation) {
        this.operation = operation;
        changed = true;
    }

    public static boolean qualify(String value, String minValue, String maxValue, final int operation) {
        switch (operation) {
            case OPERATION_WITHIN:
                return (value.compareTo(minValue) >= 0) && (value.compareTo(maxValue) <= 0);
            case OPERATION_NOT_WITHIN:
                return (value.compareTo(minValue) < 0) && (value.compareTo(maxValue) > 0);
            default:
                return true;
        }
    }

    public static boolean qualify(double value, double minValue, double maxValue, final int operation) {
        if (value == Double.NaN) {
            return false;
        }
        switch (operation) {
            case OPERATION_WITHIN:
                if ((minValue != Double.NaN) || (maxValue != Double.NaN)) {
                    if (minValue != Double.NaN) {
                        if (maxValue != Double.NaN) {
                            return (value >= minValue) && (value <= maxValue);
                        } else {
                            return value >= minValue;
                        }
                    } else {
                        return value <= maxValue;
                    }
                } else {
                    return true;
                }
            case OPERATION_NOT_WITHIN:
                if ((minValue != Double.NaN) || (maxValue != Double.NaN)) {
                    if (minValue != Double.NaN) {
                        if (maxValue != Double.NaN) {
                            return (value < minValue) || (value > maxValue);//10.12.2009
                        } else {
                            return value < minValue;
                        }
                    } else {
                        return value > maxValue;
                    }
                } else {
                    return true;
                }
            default:
                return true;
        }
    }

    public static boolean qualify(String value, String testValue, final int operation) {
        if ((value == null) || (testValue == null)) {
            return false;//17.8.2010
        }               // ij.IJ.showMessage("Null");
        switch (operation) {
            case OPERATION_LESS_THAN:
                return value.compareTo(testValue) < 0;
            case OPERATION_EQUAL:
                return value.compareTo(testValue) == 0;
            case OPERATION_GREATER_THAN:
                return value.compareTo(testValue) > 0;
            case OPERATION_LESS_OR_EQUAL:
                return value.compareTo(testValue) <= 0;
            case OPERATION_NOT_EQUAL:
                return value.compareTo(testValue) != 0;
            case OPERATION_GREATER_OR_EQUAL:
                return value.compareTo(testValue) >= 0;
            default:
                return true;
        }
    }

    public static boolean qualify(double value, double testValue, final int operation) {
        if (value == Double.NaN) {
            return false;
        }
        switch (operation) {
            case OPERATION_LESS_THAN:
                if (testValue != Double.NaN) {
                    return value < testValue;
                } else {
                    return true;
                }
            case OPERATION_EQUAL:
                if (testValue != Double.NaN) {
                    return value == testValue;
                } else {
                    return true;
                }
            case OPERATION_GREATER_THAN:
                if (testValue != Double.NaN) {
                    return value > testValue;
                } else {
                    return true;
                }
            case OPERATION_LESS_OR_EQUAL:
                if (testValue != Double.NaN) {
                    return value <= testValue;
                } else {
                    return true;
                }
            case OPERATION_NOT_EQUAL:
                if (testValue != Double.NaN) {
                    return value != testValue;
                } else {
                    return true;
                }
            case OPERATION_GREATER_OR_EQUAL:
                if (testValue != Double.NaN) {
                    return value >= testValue;
                } else {
                    return true;
                }
            default:
                return true;
        }
    }

    public static boolean qualify(String value, final int operation) {
        switch (operation) {
            case OPERATION_EMPTY:
                return value.equals("");
            case OPERATION_EXISTS:
                return value != null;
            default:
                return true;
        }
    }

    public static boolean qualify(double value, final int operation) {
        switch (operation) {
            case OPERATION_EMPTY:
                return Double.isNaN(value);
            case OPERATION_EXISTS:
                return !Double.isNaN(value);
            default:
                return true;
        }
    }
}
