/*
 * ObjectProxyOJ.java
 */
package oj.processor.state.proxy;

import oj.OJ;
import oj.project.CellOJ;
import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;

public class YtemProxyOJ {

    private int ytemDefType;
    private int maxCloneCount;
    private int pointCount;
    private YtemOJ ytem;
    private CellOJ cell;
    private String ytemDefName;
    private boolean closed;

    /**
     * Creates an YtemProxyOJ and a new ytem
     */
    public YtemProxyOJ(CellOJ cell, String ytemDefName, int stackIndex) {
        this.cell = cell;
        this.closed = false;
        this.ytemDefName = ytemDefName;
        this.ytemDefType = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType();
        this.maxCloneCount = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getCloneMax();

        if (cell.getCloneCount(ytemDefName) < maxCloneCount) {
            createClone(cell, ytemDefName, stackIndex);
        }
    }

    /** Creates YtemProxyOJ for an existing ytem
     */
    public YtemProxyOJ(CellOJ cell, YtemOJ ytem) {
        if (ytem != null) {
            ytem.setOpen(false);
        }

        this.cell = cell;
        this.closed = false;
        this.ytem = ytem;
        this.ytemDefName = ytem.getYtemDef();
        this.pointCount = ytem.getLocationsCount();
        this.ytemDefType = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getYtemType();
        this.maxCloneCount = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getCloneMax();

        ytem.setOpen(true);
    }

    /**
     * 
     * @param ytem
     * @return true if suppled ytem is same proxy's ytem
     */
    public boolean isSameYtem(YtemOJ ytem) {
        return ((this.ytem != null) && (this.ytem.equals(ytem)));
    }

    /** If "owning" ytem is null, creates a now clone.
     * Then the location is added to this clone
     */
    public boolean addLocation(int stackIndex, double x, double y, double z) {
        if (ytem == null) {
            if (cell.getCloneCount(ytemDefName) < maxCloneCount) {
                createClone(cell, ytemDefName, stackIndex);
            } else {
                return false;
            }
        }
        if (!addLocationToClone(x, y, z)) {

            if ((OJ.getData().getYtemDefs().isComposite()) && (cell.getCloneCount(ytemDefName) < maxCloneCount)) {
                createClone(cell, ytemDefName, stackIndex);
                addLocationToClone(x, y, z);
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Closes the ytem to which this proxy is connected.
     * Removes ytem if required point number is not reached (e.g. 3 for polygon)
     * Then sets the proxy's ytem to null.
     */
    public void close() {
        if (ytem != null) {
            ytem.setOpen(false);
        }

        switch (ytemDefType) {
            case YtemDefOJ.YTEM_TYPE_LINE:
            case YtemDefOJ.YTEM_TYPE_SEGLINE:
            case YtemDefOJ.YTEM_TYPE_POLYGON:
                if (pointCount < 2) {
                    cell.removeYtem(ytem);
                }
                break;
            case YtemDefOJ.YTEM_TYPE_ANGLE:
                if (pointCount < 3) {
                    cell.removeYtem(ytem);
                }
                break;
            default:
                if (pointCount < 1) {
                    cell.removeYtem(ytem);
                }
        }

        closed = true;
        ytem = null;
    }

    /**
     * removes one point from the current ytem
     * @return true if succeeded
     */
    public boolean deleteLastLocation() {
        if ((ytem != null) && (ytem.getLocationsCount() > 0)) {
            pointCount -= 1;
            OJ.getDataProcessor().removeLocation(ytem, ytem.getLocationsCount() - 1);
            if (ytem.getLocationsCount() == 0) {
                close();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * First checks if max point number is reached: (e.g. point=1, line=2, angle, poly=3)
     * and if so, close the ytem.
     * @return true if the ytem is now closed
     */
    public boolean isClosed() {
        if (closed) {
            return true;
        }
        switch (ytemDefType) {
            case YtemDefOJ.YTEM_TYPE_POINT:
                if (pointCount == 1) {
                    if (ytem != null) {
                        ytem.setOpen(false);
                    }
                    return true;
                }
                break;
            case YtemDefOJ.YTEM_TYPE_LINE:
                if (pointCount == 2) {
                    if (ytem != null) {
                        ytem.setOpen(false);
                    }
                    return true;
                }
                break;
            case YtemDefOJ.YTEM_TYPE_ANGLE:
                if (pointCount == 3) {
                    if (ytem != null) {
                        ytem.setOpen(false);
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Checks if clone can accept one more point, via isClosed(),
     * and if OK adds that point.
     */
    private boolean addLocationToClone(double x, double y, double z) {
        if (!isClosed()) {//will it accept one more point?
            pointCount += 1;
            OJ.getDataProcessor().addLocation(ytem, new LocationOJ(x, y, z));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds an ytem of any type  to the indicated cell.
     * The term "clone" is misleading
     * @param objectDefName the type of the new ytem
     */
    private void createClone(CellOJ cell, String objectDefName, int stackIndex) {
        if (ytem != null) {
            ytem.setOpen(false);
        }
        ytem = OJ.getDataProcessor().createNewYtem(objectDefName);
        ytem.setStackIndex(stackIndex);
        ytem.setOpen(true);

        OJ.getDataProcessor().addYtem(cell, ytem);
        pointCount = 0;
        closed = false;
    }

    /**
     * @return number of locations of connected ytem 
     */
    public int getLocationsCount() {
        if (ytem != null) {
            return ytem.getLocationsCount();
        }
        return 0;
    }
}
