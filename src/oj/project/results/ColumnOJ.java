/*
 * ColumnOJ.java
 * fully documented on 17.4.2010
 */
package oj.project.results;

import oj.project.YtemOJ;
import oj.project.LocationOJ;
import java.util.ArrayList;
import oj.OJ;
import oj.geometry.TriangleOJ;
import oj.geometry.VertexCalculatorOJ;
import oj.project.BaseAdapterOJ;
import oj.project.CellOJ;
import oj.project.CellsOJ;
import oj.project.DataOJ;
import oj.project.IBaseOJ;
import oj.project.ImageOJ;
import oj.project.results.statistics.StatisticsProxysOJ;

/**
 * Contains mainly a column definition and a list of either floats or strings.
 * There is a difference in storage of results for linked and unlinked colulmns:
 * Linked columns have a set of properties, where each column title is a
 * key(string) that holds a numeric or string Unlinked columns hold arrayLists
 * that are as long as the last occupied row (ojSetResult will fill holes with
 * NaN or "");
 */
public class ColumnOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -2502125628637704454L;
    private transient boolean dirtyValues;
    public ColumnDefOJ columnDef = new ColumnDefOJ();
    public StatisticsProxysOJ statistics = new StatisticsProxysOJ();
    public ArrayList rows = new ArrayList();//can contain floats or strings
    private transient int histoID = 0;

    public ColumnOJ() {
        init();
    }

    public ColumnOJ(String name) {
        this.columnDef.setName(name);
        init();
    }

    public ColumnOJ(ColumnDefOJ columnDef) {
        this.columnDef = columnDef;
        init();
    }

    /**
     * Initializes column
     */
    public void init() {//10.5.2010synchronized
        statistics.setParent(this);
        columnDef.setParent(this);
        statistics.init(getName());
        setValuesDirty();
        clear();
    }

    /**
     *
     * @return true if column definition of statistics have changed
     */
    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            return columnDef.getChanged() || statistics.getChanged();
        }
    }

    /**
     * set the changed flag of column definition and statistics
     */
    public void setChanged(boolean changed) {
        super.setChanged(changed);
        columnDef.setChanged(changed);
        statistics.setChanged(changed);
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (rows == null) {
            rows = new ArrayList();
        }
        if (columnDef == null) {
            columnDef = new ColumnDefOJ();
        }
        columnDef.initAfterUnmarshalling(this);
        if (statistics == null) {
            statistics = new StatisticsProxysOJ();
        }
        statistics.initAfterUnmarshalling(this, getName());
    }

    /**
     * @return id of imagePlus to show if plot window can be reused
     */
    public int getHistoID() {
        return histoID;
    }

    /**
     * remember imageID into which HistoPlot was drawn
     *
     * @param id
     */
    public void setHistoID(int id) {
        histoID = id;
    }

    /**
     * converts 1-based index to 0-based index
     *
     * @param len length of an array
     * @param ii a 1-based index that can be negative to count from the back
     * @return 0-based index to the correct element, or -1 if invalid
     */
    public int convertIndex(int len, int ii) {
        if (ii == 0 || ii > len || -ii > len) {
            return -1;
        }
        if (ii > 0) {
            ii = ii - 1;
        } else {
            ii = len + ii;
        }
        return ii;
    }

    /**
     * Not yet used!! cell and column define row and column in the results table
     * for which a value has to be calculated.
     *
     * If alg is of type varLength, we only check if object exists and collect
     * all points of it.
     *
     * If alg is of type fixedLength, we check check both if object and point
     * exists.
     *
     * If first operand is varLength or fixedLength, we can send the collected
     * locations to the VertexCalculator.
     *
     * prepare locations for calculation. Location array always has correct
     * size, but in case of non-existing objects or points, we fill it with
     * NaNs. We assume that OperandOJ contains 1-based indices.
     *
     *
     * Well let us start again: we collect all locations depending on the
     * algorithm. So e.g. for segLine we collect all those points, and for
     * PartialPath we put the last point on the top. ++++ Finally, we simply
     * pass this to the VertexCalculator and read the result.
     *
     *
     * @param cell the cell to be calculated
     * @return an Arraylist holding locations. Well this must be changed
     */
    public ArrayList collectLocations(CellOJ cell) {
        double NaN = Double.NaN;
        ArrayList locs = new ArrayList();
        int nOps = columnDef.getOperandCount();
        int alg = columnDef.getAlgorithm();
        for (int jj = 0; jj < nOps; jj++) {
            boolean found = false;
            OperandOJ op = columnDef.getOperand(jj);
            String name = op.getObjectName();
            int clone = op.getYtemClone(); // 1-based
            int pointNo = op.getRelPosition(); // 1-based
            int nClones = cell.getCloneCount(name);
            if (clone < 0) {
                clone = nClones - clone + 1;
            }
            if (clone > 0 && clone <= nClones) {
                YtemOJ ytm = cell.getClone(clone - 1, name);
                int nLocs = ytm.getLocationsCount();
                if (pointNo < 0) {
                    pointNo = nLocs - pointNo + 1;
                }
                if (pointNo > 0 && pointNo <= nLocs) {
                    found = true;
                    locs.add((Object) ytm.getLocation(pointNo - 1));
                }
            }
            if (!found) {
                locs.add(new LocationOJ(NaN, NaN, NaN));
            }
        }
        return locs;
    }

    /**
     * @return this column's Columndefinition
     */
    public ColumnDefOJ getColumnDef() {
        return columnDef;
    }

    /**
     * @return this column's Statistics
     */
    public StatisticsProxysOJ getStatistics() {
        return statistics;
    }

    /**
     * set Values dirty e.g. after a point location has changed
     */
    public void setValuesDirty() {
        dirtyValues = true;
        statistics.setStatisticsDirty();
    }

    /**
     * clears the rows; for unlinked columns, rows is set to length=0 for linked
     * columns, values are filled with NaN, (or with empty string if it was a
     * string column)
     */
    public void clear() {
        if (columnDef.isUnlinked()) {
            rows.clear();
        } else {
            if ((parent != null) && (parent.getParent() != null) && (parent.getParent().getParent() != null)) {
                CellsOJ cells = ((DataOJ) parent.getParent().getParent()).getCells();
                if (columnDef.isTextMode()) {
                    for (int jj = 0; jj < cells.getCellsCount(); jj++) {
                        setStringResult(jj, "");
                    }
                } else {
                    for (int jj = 0; jj < cells.getCellsCount(); jj++) {
                        setDoubleResult(jj, Double.NaN);
                    }
                }
            }
        }
        statistics.setStatisticsDirty();
    }

    /**
     * Existing element of arraylist "rows" is replaced by Double
     */
    public void setDoubleResult(int index, double value) {
        if (isUnlinkedColumn()) {
            rows.set(index, new Float(value));//17.4.2010-float
        } else {
            CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index);
            if (cell == null) {
                return;
            }
            if (value == Double.NaN) {
                if ((cell.getProperty(getName()) == null) || (cell.getProperty(getName()).equals("NaN"))) {
                    cell.removeProperty(getName());
                }
            } else {
                cell.setProperty(getName(), Double.valueOf(value).toString());
            }
        }
        statistics.setStatisticsDirty();
    }

    /**
     * Existing element of arraylist "rows" is replaced by String
     */
    public void setStringResult(int index, String value) {
        if (isUnlinkedColumn()) {
            rows.set(index, value);
        } else {
            ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index).setProperty(getName(), value);
        }
        statistics.setStatisticsDirty();
    }

    /**
     * @return numeric result of n-th row (0-based)
     */
    public double getDoubleResult(int index) {
        if (dirtyValues) {
            updateValues();
        }
        if (isUnlinkedColumn()) {
            if ((index >= 0) && (index < rows.size())) {
                if (rows.get(index) != null) {
                    float fVal = (Float) rows.get(index);//17.4.2010-float
                    return fVal;//17.4.2010-float
                } else {
                    return Double.NaN;
                }
            } else {
                return Double.NaN;
            }
        } else {
            if (((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index).containsProperty(getName())) {
                return Double.valueOf(((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index).getProperty(getName())).doubleValue();
            } else {
                return Double.NaN;
            }
        }
    }

    /**
     * @return string result of n-th row (0-based)
     */
    public String getStringResult(int index) {
        if (dirtyValues) {
            updateValues();
        }
        if (isUnlinkedColumn()) {
            if ((index >= 0) && (index < rows.size())) {
                if (rows.get(index) != null) {
                    return (String) rows.get(index);
                } else {
                    return "";//4.9.2010
                }
            } else {
                return null;
            }
        } else {
            if (((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index).containsProperty(getName())) {
                return ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index).getProperty(getName());
            } else {
                return "";//4.9.2010
            }
        }
    }

    /**
     * @return number of cells if column is linked, or index of last row entry
     * if column is unlinked
     */
    public int getResultCount() {
        if (dirtyValues) {
            updateValues();
        }
        if (isUnlinkedColumn()) {
            return rows.size();
        } else {
            return ((DataOJ) parent.getParent().getParent()).getCells().getCellsCount();
        }
    }

    /**
     * @return name of column
     */
    public String getName() {
        return columnDef.getName();
    }

    /**
     * set column's name
     */
    public void setName(String name) {
        statistics.setColumnName(name);
        columnDef.setName(name);
    }

    /**
     * set algorithm to linked column such as "length" or "area"
     */
    public void setAlgorithm(int algorithm) {
        if (algorithm != columnDef.getAlgorithm()) {
            columnDef.setAlgorithm(algorithm);
            reset();
        }
    }

    /**
     * could be same as clear()
     */
    public void reset() {
        clear();
        dirtyValues = true;
        statistics.setStatisticsDirty();
    }

    /**
     * @return number of qualified and non-empty results
     */
    public int getValidResults() {
        if (dirtyValues) {
            updateValues();
        }
        return (int) statistics.getStatisticsValueByName("Count");
    }

    /**
     * @return true if column is unlinked
     */
    public boolean isUnlinkedColumn() {
        return columnDef.isUnlinked();
    }

    /**
     * true if result is qualified and not-empty
     */
    public boolean isValidResult(int index) {
        if (dirtyValues) {
            updateValues();
        }

        if (isUnlinkedColumn()) {
            if (columnDef.isTextMode()) {
                return getStringResult(index) != null;
            }
            double double_result = getDoubleResult(index);
            return !Double.isNaN(double_result);
        } else {
            CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(index);
            if (cell == null) {
                return false;
            }
            if (!cell.isQualified()) {
                return false;
            }

            if (columnDef.isTextMode()) {
                return getStringResult(index) != null;
            }
            return !Double.isNaN(getDoubleResult(index));
        }
    }

    /**
     * Returns an array of doubles, not including NaNs, and (if qualifiedOnly is
     * true) not including unqualified values
     */
//    public double[] getDoubleResults(boolean qualifiedOnly) {
//        return getDoubleResults(qualifiedOnly, false);// don't includeNaNs
//    }
//   public double[] getDoubleResults(boolean qualifiedOnly, boolean includeNaNs) {
//   return getDoubleArray(!qualifiedOnly,  includeNaNs);
//   }

    public double[] getDoubleArray(boolean includeUnquaified, boolean includeNaNs) {

        if (dirtyValues) {
            updateValues();
        }
        int count = getResultCount();

        double[] arr = new double[count];//must be trimmed if qualifiedOnly
        int jj = 0;
        for (int row = 0; row < getResultCount(); row++) {//no of cells, or max row in unlinked columns
            double val = getDoubleResult(row);
            if (!(Double.isNaN(val)) || includeNaNs) {
                if (isUnlinkedColumn() || includeUnquaified || OJ.getData().getCells().getCellByIndex(row).isQualified()) {

                    arr[jj] = val;
                    jj++;
                }
            }
        }
        if (count != jj) {
            double[] trimmedArr = new double[jj];
            for (int kk = 0; kk < jj; kk++) {
                trimmedArr[kk] = arr[kk];
            }
            return trimmedArr;//25.10.2010
        }
        return arr;
    }

    /**
     * @return array of strings of column that contains strings
     */
    public String[] getStringResults() {
        if (dirtyValues) {
            updateValues();
        }
        String[] r = new String[getResultCount()];
        for (int i = 0; i < getResultCount(); i++) {
            r[i] = getStringResult(i);
        }
        return r;
    }

    /**
     * Calculates column's results using the attached algorithm. calles doCalc()
     * for each row where necessary
     */
    private void updateValues() {
        LocationOJ loc = new LocationOJ();
        for (int i = 0; i < ((DataOJ) parent.getParent().getParent()).getCells().getCellsCount(); i++) {
            CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(i);
            LocationOJ voxelSize = new LocationOJ();
            ImageOJ im = ((DataOJ) parent.getParent().getParent()).getImages().getImageByName(cell.getImageName());
            if (im != null) {
                voxelSize.setX(im.getVoxelSizeX());
                voxelSize.setY(im.getVoxelSizeY());
                voxelSize.setZ(im.getVoxelSizeZ());
            }

            switch (columnDef.getAlgorithm()) {
                case ColumnDefOJ.ALGORITHM_CALC_COUNT:
                    setDoubleResult(i, cell.getCloneCount(columnDef.getOperand(0).getObjectName()));
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_EXISTS:
                    YtemOJ ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if ((ytm3 != null) && (ytm3.getLocationsCount() >= columnDef.getOperand(0).getRelPosition())) {
                        setDoubleResult(i, 1.0);
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_FILE_NAME:
                    setStringResult(i, ((DataOJ) parent.getParent().getParent()).getImages().getImageByName(cell.getImageName()).getFilename());
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_INDEX://21.9.2009
                    setDoubleResult(i, i + 1);
                    break;

                case ColumnDefOJ.ALGORITHM_CALC_ID:
                    setDoubleResult(i, (double) cell.getID());
                    break;


                case ColumnDefOJ.ALGORITHM_CALC_IMAGE:
                    setDoubleResult(i, ((DataOJ) parent.getParent().getParent()).getImages().getIndexOfImage(cell.getImageName()) + 1);
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_LINKED_NUMBER:
                case ColumnDefOJ.ALGORITHM_CALC_LINKED_TEXT:
                case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_NUMBER:
                case ColumnDefOJ.ALGORITHM_CALC_UNLINKED_TEXT:
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_SLICE:
                    setDoubleResult(i, cell.getStackIndex());
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_PATH:
                case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
                    ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if (ytm3 != null) {
                        LocationOJ[] locs = new LocationOJ[ytm3.getLocationsCount()];
                        for (int j = 0; j < locs.length; j++) {
                            locs[j] = new LocationOJ();
                            locs[j].setX(getScaledX(i, ytm3.getLocation(j).getX()));
                            locs[j].setY(getScaledY(i, ytm3.getLocation(j).getY()));
                            locs[j].setZ(getScaledZ(i, ytm3.getLocation(j).getZ()));
                        }
                        setDoubleResult(i, doCalc(locs, null, ColumnDefOJ.ALGORITHM_CALC_PATH));
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:
                    YtemOJ ytm1 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    YtemOJ ytm2 = cell.getClone(columnDef.getOperand(1).getYtemClone(), columnDef.getOperand(1).getObjectName());
                    if ((ytm1 == null) || (ytm2 == null)) {
                        setDoubleResult(i, Double.NaN);
                    } else {
                        LocationOJ[] locs = new LocationOJ[2];
                        locs[0] = new LocationOJ();
                        locs[0].setX(getScaledX(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getX()));
                        locs[0].setY(getScaledY(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getY()));
                        locs[0].setZ(getScaledZ(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getZ()));
                        locs[1] = new LocationOJ();
                        locs[1].setX(getScaledX(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getX()));
                        locs[1].setY(getScaledY(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getY()));
                        locs[1].setZ(getScaledZ(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getZ()));
                        setDoubleResult(i, doCalc(locs, null, ColumnDefOJ.ALGORITHM_CALC_DISTANCE));
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_XPOS:
                    ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if (ytm3 != null) {
                        setDoubleResult(i, getScaledX(i, ytm3.getLocation(columnDef.getOperand(0).getRelPosition()).getX()));
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_YPOS:
                    ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if (ytm3 != null) {
                        setDoubleResult(i, getScaledY(i, ytm3.getLocation(columnDef.getOperand(0).getRelPosition()).getY()));
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
                    ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if (ytm3 != null) {
                        setDoubleResult(i, getScaledZ(i, ytm3.getLocation(columnDef.getOperand(0).getRelPosition()).getZ()));
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_ORIENTATION:
                    ytm1 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    ytm2 = cell.getClone(columnDef.getOperand(1).getYtemClone(), columnDef.getOperand(1).getObjectName());
                    if ((ytm1 == null) || (ytm2 == null)) {
                        setDoubleResult(i, Double.NaN);
                    } else {
                        LocationOJ[] locs = new LocationOJ[2];
                        locs[0] = new LocationOJ();
                        locs[0].setX(getScaledX(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getX()));
                        locs[0].setY(getScaledY(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getY()));
                        locs[0].setZ(getScaledZ(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getZ()));
                        locs[1] = new LocationOJ();
                        locs[1].setX(getScaledX(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getX()));
                        locs[1].setY(getScaledY(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getY()));
                        locs[1].setZ(getScaledZ(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getZ()));
                        setDoubleResult(i, doCalc(locs, null, ColumnDefOJ.ALGORITHM_CALC_ORIENTATION));
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
                    ytm1 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    ytm2 = cell.getClone(columnDef.getOperand(1).getYtemClone(), columnDef.getOperand(1).getObjectName());
                    ytm3 = cell.getClone(columnDef.getOperand(2).getYtemClone(), columnDef.getOperand(2).getObjectName());
                    if ((ytm1 == null) || (ytm2 == null) || (ytm3 == null)) {//14.11.2010
                        setDoubleResult(i, Double.NaN);
                    } else {
                        LocationOJ[] locs = new LocationOJ[3];
                        locs[0] = new LocationOJ();
                        locs[0].setX(getScaledX(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getX()));
                        locs[0].setY(getScaledY(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getY()));
                        locs[0].setZ(getScaledZ(i, ytm1.getLocation(columnDef.getOperand(0).getRelPosition()).getZ()));
                        locs[1] = new LocationOJ();
                        locs[1].setX(getScaledX(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getX()));
                        locs[1].setY(getScaledY(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getY()));
                        locs[1].setZ(getScaledZ(i, ytm2.getLocation(columnDef.getOperand(1).getRelPosition()).getZ()));
                        locs[2] = new LocationOJ();
                        locs[2].setX(getScaledX(i, ytm3.getLocation(columnDef.getOperand(2).getRelPosition()).getX()));
                        locs[2].setY(getScaledY(i, ytm3.getLocation(columnDef.getOperand(2).getRelPosition()).getY()));
                        locs[2].setZ(getScaledZ(i, ytm3.getLocation(columnDef.getOperand(2).getRelPosition()).getZ()));
                        setDoubleResult(i, doCalc(locs, null, ColumnDefOJ.ALGORITHM_CALC_ANGLE));
                    }
                    break;
                case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
                case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:

                    ytm1 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    ytm2 = cell.getClone(columnDef.getOperand(1).getYtemClone(), columnDef.getOperand(1).getObjectName());
                    if (ytm1 == null || ytm2 == null) {
                        setDoubleResult(i, Double.NaN);
                        break;
                    }
                    LocationOJ thisLoc;
                    VertexCalculatorOJ vertcalc = new VertexCalculatorOJ();

                    for (int nLoc = 0; nLoc < ytm1.getLocationsCount(); nLoc++) {
                        //push locations of segline
                        thisLoc = ytm1.getLocation(nLoc);
                        vertcalc.pushLocation(thisLoc);
                    }
                    int jj = columnDef.getOperand(1).getRelPosition() + 1;
                    int len = ytm2.getLocationsCount();
                    int kk = convertIndex(len, jj);
                    if (kk == -1) {
                        setDoubleResult(i, Double.NaN);
                        break;
                    }

                    thisLoc = ytm2.getLocation(kk);
                    vertcalc.pushLocation(thisLoc);
                    vertcalc.setVertexScale(voxelSize);
                    double result = 0;
                    if (columnDef.getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH) {
                        vertcalc.calc("partialPath");
                        result = vertcalc.partialPath;
                    }
                    if (columnDef.getAlgorithm() == ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH) {
                        vertcalc.calc("relPartialPath");
                        result = vertcalc.relPartialPath;
                    }

                    setDoubleResult(i, result);

                    break;
                case ColumnDefOJ.ALGORITHM_CALC_AREA:
                    ytm3 = cell.getClone(columnDef.getOperand(0).getYtemClone(), columnDef.getOperand(0).getObjectName());
                    if (ytm3 != null) {
                        LocationOJ[] locs = new LocationOJ[ytm3.getLocationsCount()];
                        for (int j = 0; j < locs.length; j++) {
                            locs[j] = new LocationOJ();
                            locs[j].setX(getScaledX(i, ytm3.getLocation(j).getX()));
                            locs[j].setY(getScaledY(i, ytm3.getLocation(j).getY()));
                            locs[j].setZ(getScaledZ(i, ytm3.getLocation(j).getZ()));
                        }
                        setDoubleResult(i, doCalc(locs, null, ColumnDefOJ.ALGORITHM_CALC_AREA));
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
                    break;
                default:
                    if (columnDef.isTextMode()) {
                        setStringResult(i, null);
                    } else {
                        setDoubleResult(i, Double.NaN);
                    }
            }
        }
        changed = true;
        dirtyValues = false;
        statistics.setStatisticsDirty();
    }

    /**
     * @return x multiplied by voxelwidth
     */
    private double getScaledX(int cellIndex, double x) {
        CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            return x * ((DataOJ) parent.getParent().getParent()).getImages().getImageByName(cell.getImageName()).getVoxelSizeX();
        } else {
            return x;
        }
    }

    /**
     * @return y multiplied by voxelheight
     */
    private double getScaledY(int cellIndex, double y) {
        CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            return y * ((DataOJ) parent.getParent().getParent()).getImages().getImageByName(cell.getImageName()).getVoxelSizeY();
        } else {
            return y;
        }
    }

    /**
     * @return z multiplied by voxeldepth
     */
    private double getScaledZ(int cellIndex, double z) {
        CellOJ cell = ((DataOJ) parent.getParent().getParent()).getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            return z * ((DataOJ) parent.getParent().getParent()).getImages().getImageByName(cell.getImageName()).getVoxelSizeZ();
        } else {
            return z;
        }
    }

    /**
     * Calculates a result from a number of 3D points.
     *
     * @param locs holds all points of a line, segline, roi etc
     * @param loc holds the point being projected onto a segline etc
     * @param algorithm what operation to perform
     * @return the result value, which is NaN if calculation is not possible
     */
    private double doCalc(LocationOJ[] locs, LocationOJ loc, int algorithm) {
        switch (algorithm) {
            case ColumnDefOJ.ALGORITHM_CALC_PATH:
            case ColumnDefOJ.ALGORITHM_CALC_LENGTH:
                double len = 0.0;
                for (int i = 1; i < locs.length; i++) {
                    double dx = locs[i].getX() - locs[i - 1].getX();
                    double dy = locs[i].getY() - locs[i - 1].getY();
                    double dz = locs[i].getZ() - locs[i - 1].getZ();
                    len += Math.sqrt(dx * dx + dy * dy + dz * dz);
                }
                return len;
            case ColumnDefOJ.ALGORITHM_CALC_DISTANCE:
                if (locs.length <= 0) {
                    return 0;
                }
                double dx = locs[0].getX() - locs[locs.length - 1].getX();
                double dy = locs[0].getY() - locs[locs.length - 1].getY();
                double dz = locs[0].getZ() - locs[locs.length - 1].getZ();
                return Math.sqrt(dx * dx + dy * dy + dz * dz);
            case ColumnDefOJ.ALGORITHM_CALC_XPOS:
                if (locs.length <= 0) {
                    return 0;
                }
                return locs[0].getX();
            case ColumnDefOJ.ALGORITHM_CALC_YPOS:
                if (locs.length <= 0) {
                    return 0;
                }
                return locs[0].getY();
            case ColumnDefOJ.ALGORITHM_CALC_ZPOS:
                if (locs.length <= 0) {
                    return 0;
                }
                return locs[0].getZ();
            case ColumnDefOJ.ALGORITHM_CALC_ORIENTATION:
                if (locs.length <= 0) {
                    return 0;
                }
                dx = locs[locs.length - 1].getX() - locs[0].getX();
                dy = locs[locs.length - 1].getY() - locs[0].getY();
                double phi = -180 / Math.PI * Math.atan2(dy, dx);
                if (phi < 0) {
                    phi += 360;
                }
                return phi;
            case ColumnDefOJ.ALGORITHM_CALC_ANGLE:
                if (locs.length <= 0) {
                    return 0;
                }
                TriangleOJ tri = new TriangleOJ();
                tri.calcTriangle(locs[0], locs[2], locs[1]);
                return tri.getPhiC();
            case ColumnDefOJ.ALGORITHM_CALC_ABS_PARTIAL_PATH:
                if (locs.length <= 0) {
                    return 0;
                }
                tri = new TriangleOJ();
                for (int i = 0; i < (locs.length - 1); i++) {
                    tri.calcTriangle(locs[i], locs[i + 1], loc);
                }
                return tri.getMinCcaAccu();
            case ColumnDefOJ.ALGORITHM_CALC_REL_PARTIAL_PATH:
                if (locs.length <= 0) {
                    return 0;
                }
                tri = new TriangleOJ();
                for (int i = 0; i < (locs.length - 1); i++) {
                    tri.calcTriangle(locs[i], locs[i + 1], loc);
                }
                return tri.getMinCcaAccu() / tri.getCcAccu();

            case ColumnDefOJ.ALGORITHM_CALC_OFFROAD:
                if (locs.length <= 0) {
                    return 0;
                }
                tri = new TriangleOJ();
                for (int i = 0; i < (locs.length - 1); i++) {
                    tri.calcTriangle(locs[i], locs[i + 1], loc);
                }
                return tri.getMinLeftRight();

            case ColumnDefOJ.ALGORITHM_CALC_AREA:
                if (locs.length <= 0) {
                    return 0;
                }
                double sumAreas = 0.0;
                double sumVolumesX = 0.0;
                double sumVolumesY = 0.0;
                double sumX;
                double sumY;
                double deltaX;
                double deltaY;
                LocationOJ prevLoc = locs[locs.length - 1];
                for (int i = 0; i < locs.length; i++) {
                    sumX = locs[i].getX() + prevLoc.getX();
                    sumY = locs[i].getY() + prevLoc.getY();
                    deltaX = locs[i].getX() - prevLoc.getX();
                    deltaY = locs[i].getY() - prevLoc.getY();

                    sumVolumesX = sumVolumesX + sumX * sumX * deltaY; //Eq 3. p 489
                    sumVolumesY = sumVolumesY + sumY * sumY * deltaX; //Eq 3. p 489
                    sumAreas = sumAreas + sumX * deltaY; //Eq 4. p 490}
                    prevLoc = locs[i];
                }
                return Math.abs(sumAreas / 2);
            default:
                return 0;
        }
    }

    /**
     * updates column's results and statistics
     */
    public void recalculate() {
        updateValues();
        for (int i = 0; i < statistics.getStatisticsCount(); i++) {
            statistics.getStatisticsByIndex(i).recalculate();
        }
    }

    /**
     * unknown function
     */
    public void removeStatistic(String name) {
        for (int i = 0; i < statistics.getStatisticsCount(); i++) {
            if (name.equals(statistics.getStatisticsByIndex(i).getName())) {
                statistics.removeStatisticsByIndex(i);
                break;
            }
        }
    }

    /**
     * used to append stringresult to unlinked column to fill holes
     */
    public void addStringResult(String value) {
        if (isUnlinkedColumn()) {
            rows.add(value);
        }
        statistics.setStatisticsDirty();
    }

    /**
     * used to append double result to unlinked column to fill holes
     */
    public void addDoubleResult(double value) {
        if (isUnlinkedColumn()) {
            rows.add(new Float(value));//17.4.2010-float
        }
        statistics.setStatisticsDirty();
    }
}
