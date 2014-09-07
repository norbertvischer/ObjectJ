package oj.processor.state.proxy;

import oj.OJ;
import oj.project.CellOJ;
import oj.project.YtemOJ;
import oj.processor.DataProcessorOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;

/**
 * A CellProxyOJ is an "unfolded" version of a cell, used e.g.
 * for adding/deleting/editing ytems.
 * An ImageProxyOJ contains a CellProxyOJ which in turn contains an YtemProxyOJ.
 */
public class CellProxyOJ implements YtemDefChangedListenerOJ {

    private YtemProxyOJ ytemProxy;
    private String ytemDefName;
    private int ytemIndex = -1;
    private int stackIndex = 1;
    private boolean closed;
    private CellOJ cell;

    /** Creates a new instance of CellProxyOJ */
    public CellProxyOJ(String imageName, int stackIndex) {
        this.closed = false;
        cell = new CellOJ(imageName, stackIndex);
        //may we should add the cell only after closing it
        OJ.getDataProcessor().addCell(cell);
        openCell(cell);
        OJ.getEventProcessor().addYtemDefChangedListener(this);
    }

    public CellProxyOJ(CellOJ cell) {
        this.cell = cell;
        this.closed = false;
        this.stackIndex = cell.getStackIndex();
        openCell(cell);
        OJ.getEventProcessor().addYtemDefChangedListener(this);
    }

    /**
     * returns true if owned YtemProxy points to the last Ytem of cell
     */
    public boolean isLastYtemProxy() {
        return ((ytemIndex == -1) || (ytemIndex == cell.getOpenYtemDefIndex()));
    }

    public void setStackIndex(int si) {//7.12.2009
        stackIndex = si;
    }

    /**
     * Fills ytemProxy with data of next ytem
     */
    public void openNextYtem() {
        openYtem(cell.getYtemByIndex(ytemIndex + 1));
        ytemDefName = cell.getYtemByIndex(ytemIndex + 1).getYtemDef();
        OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
    }

    /**
     * Fills ytemProxy with data of an existing ytem
     */
    public void openYtem(YtemOJ ytem) {
        ytemDefName = ytem.getYtemDef();
        if (ytemProxy != null) {
            if (!ytemProxy.isSameYtem(ytem)) {
                ytemProxy.close();
                ytemProxy = new YtemProxyOJ(cell, ytem);
            }
        } else {
            ytemProxy = new YtemProxyOJ(cell, ytem);
        }
        ytemIndex = cell.getOpenYtemDefIndex();
    }

    /**
     *Returns this CellProxy's YtemProxy
     */
    public YtemProxyOJ getYtemProxy() {
        return ytemProxy;
    }

    /**
     * true if cellProxy is owned by "cellOJ"
     */
    public boolean isSameCell(CellOJ cell) {
        return ((this.cell != null) && (this.cell.equals(cell)));
    }

    /**
     * Uses existing YtemProxy or creates a new one,
     * then adds location (i.e. a point) to the YtemProxy
     */
    public boolean addLocation(int sliceIndex, double x, double y, double z) {//8.12.2009
        if (this.stackIndex != sliceIndex) {
            this.stackIndex = sliceIndex;
            closeYtemProxy();
        }
        if (ytemProxy == null) {
            createYtemProxy();
        }
        if (ytemProxy == null) {
            return false;
        }
        if (!ytemProxy.addLocation(sliceIndex, x, y, z)) {
            if (!OJ.getData().getYtemDefs().isComposite()) {
                close();
                return false;
            } else {
                createYtemProxy();
                if (ytemProxy == null) {
                    return false;
                } else {
                    ytemProxy.addLocation(sliceIndex, x, y, z);
                    return true;
                }
            }
        } else {
            if (!OJ.getData().getYtemDefs().isComposite() && ytemProxy.isClosed()) {
                close();
            }
            updateYtemDef();
        }
        return true;
    }

    /**
     * Advances to next YtemType if appropriate
     */
    public void updateYtemDef() {
        if (!OJ.getData().getYtemDefs().isComposite()) {
            return;
        }
        // if (OJ.getData().getYtemDefs().isComposite()) {
        if (ytemProxy == null) {
            nextYtemDef();
            return;
        }
        if (!ytemProxy.isClosed()) {
            return;
        }
        int max = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getCloneMax();
        if (cell.getCloneCount(ytemDefName) == max) {
            nextYtemDef();
        }
    }

    /**
     * closes YtemProxy, then closes CellProxy (number label becomes non-italic)
     */
    public void close() {
        OJ.getEventProcessor().removeYtemDefChangedListener(this);
        if (ytemProxy != null) {
            ytemProxy.close();
        }
        if (cell != null) {
            cell.setOpen(false);
        }
        OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().indexOfCell(cell), CellChangedEventOJ.CELL_CLOSE_EVENT);
        if ((cell != null) && (cell.getYtemsCount() == 0)) {
            OJ.getDataProcessor().removeCell(cell);
        }
        ytemProxy = null;
        closed = true;
        cell = null;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * deletes last point of cell proxy (if necessary, opens an Ytemproxy)
     */
    public boolean deleteLastLocation() {
        if ((ytemProxy != null) && (!ytemProxy.isClosed())) {
            if (!ytemProxy.deleteLastLocation()) {
                if (ytemIndex > 0) {
                    openYtem(cell.getYtemByIndex(ytemIndex - 1));
                    OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
                    ytemProxy.deleteLastLocation();
                    return true;
                } else {
                    if (cell.getYtemsCount() > 0) {
                        openYtem(cell.getYtemByIndex(0));
                        OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
                        ytemProxy.deleteLastLocation();
                        return true;
                    } else {
                        close();
                        return false;
                    }
                }
            }
            return true;
        } else {

            int nYtems = cell.getYtemsCount();
            if (nYtems > 0) {
                openYtem(cell.getYtemByIndex(-1 + nYtems));
                OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
                ytemProxy.deleteLastLocation();
                if (cell.getYtemsCount() == 0) {
                    close();
                }

                return true;
            } else {
                close();
                return false;
            }
        }
    }

    /**
     * @return the number of clones of the current object definition
     */
    public int getCloneCount() {
        return cell.getCloneCount(ytemDefName);
    }

    /**
     * advance to next ytemDef
     */
    private void nextYtemDef() {
        int maxYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefsCount() - 1;
        int currentYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName);




        if (currentYtemDefIndex == maxYtemDefIndex) {
            close();
            ytemDefName = OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName();
            OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
            return;
        }
        int newYtemDefIndex = DataProcessorOJ.getNextIndex(currentYtemDefIndex, maxYtemDefIndex);
        ytemDefName = OJ.getData().getYtemDefs().getYtemDefByIndex(newYtemDefIndex).getYtemDefName();
        ytemProxy = null;//29.6.2011
        OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
    }

    /**
     * go to previous ytemDef (not used)
     */
    private void previousYtemDef() {
        int maxYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefsCount() - 1;
        int currentYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName);
        int newYtemDefIndex = DataProcessorOJ.getPreviousIndex(currentYtemDefIndex, maxYtemDefIndex);
        ytemDefName = OJ.getData().getYtemDefs().getYtemDefByIndex(newYtemDefIndex).getYtemDefName();
        OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
    }

    public void setYtemDefName(String ytemDefName) {
        if ((ytemDefName != null) && (!ytemDefName.equals(this.ytemDefName))) {
            this.ytemDefName = ytemDefName;
            OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
            closeYtemProxy();
            createYtemProxy();
        }
    }

    /**
     * creates an YtemProxy based on YtemDefName, cell index, and stack index
     */
    private void createYtemProxy() {
        if (OJ.getData().getYtemDefs().isComposite()) {
            ytemProxy = null;//garbage collector will dispose it
            if (OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName) < 0) {
                nextYtemDef();
            }
            do {
                int cloneMax = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName).getCloneMax();
                int cloneCount = cell.getCloneCount(ytemDefName);
                if (cloneCount < cloneMax) {
                    ytemProxy = new YtemProxyOJ(cell, ytemDefName, stackIndex);
                    ytemIndex = cell.getOpenYtemDefIndex();
                    return;
                } else {
                    nextYtemDef();
                }
            } while (OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName) < (OJ.getData().getYtemDefs().getYtemDefsCount() - 1));
        } else {//not composite
            ytemProxy = new YtemProxyOJ(cell, ytemDefName, stackIndex);
            ytemIndex = cell.getOpenYtemDefIndex();
        }
    }

    public String getYtemDefName() {
        return ytemDefName;
    }

    public void closeYtemProxy() {
        if (ytemProxy != null) {
            ytemProxy.close();
            ytemProxy = null;
        }
    }

    /**
     * First makes sure no cell is open or selected, then sets "cell" open
     *
     */
    private void openCell(CellOJ cell) {
        int index = OJ.getData().getCells().getSelectedCellIndex();
        if (index >= 0) {
            OJ.getData().getCells().getCellByIndex(index).setSelected(false);
            OJ.getEventProcessor().fireCellChangedEvent(index, CellChangedEventOJ.CELL_UNSELECT_EVENT);
        }
        if (OJ.getData().getCells().getOpenCellIndex() >= 0) {
            OJ.getData().getCells().getCellByIndex(OJ.getData().getCells().getOpenCellIndex()).setOpen(false);
        }
        cell.setOpen(true);
        OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().indexOfCell(cell), CellChangedEventOJ.CELL_OPEN_EVENT);
    }

    /**
     * If user changes object definitions, cell has to close, to avoid a mess
     */
    public void ytemDefChanged(YtemDefChangedEventOJ evt) {
        if (evt.getOperation() == YtemDefChangedEventOJ.COLLECT_MODE_CHANGED) {
            close();
        }
    }
}
