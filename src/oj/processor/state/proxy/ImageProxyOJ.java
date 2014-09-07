package oj.processor.state.proxy;

import ij.IJ;
import ij.ImagePlus;
import oj.OJ;
import oj.project.CellOJ;

/**
 * An ImageProxyOJ is used for adding or modifying cells.
 * An ImageProxyOJ contains a CellProxyOJ which in turn contains an YtemProxyOJ.
 */
public class ImageProxyOJ {

    private CellProxyOJ cellProxy;
    private int stackIndex = 1;
    private String imageName;
    private String ytemDefName;

    /** Creates a new instance of ImageProxyOJ */
    public ImageProxyOJ(String imageName) {
        this.imageName = imageName;
    }
    
/**
 * Before adding a location to a linked image, cellproxy and ytemproxy
 * are verified and renewed if necessary
 * @param stackIndex
 * @param x
 * @param y
 * @param z
 */
    public void addLocation(int stackIndex, double x, double y, double z) {
        ImagePlus imp = IJ.getImage();
        //int currentSlice = imp.getSlice();
        int currentSlice = imp.getCurrentSlice();//10.11.2010
        if ((OJ.getData().getYtemDefs().is3DYtems()) || (currentSlice == stackIndex)) {
            if ((cellProxy == null) || (cellProxy.isClosed()) || (!cellProxy.addLocation(stackIndex, x, y, z))) {
                if ((cellProxy != null) && (!cellProxy.isClosed())) {
                    cellProxy.close();
                    if (OJ.getData().getYtemDefs().isComposite()) {//13.6.2011 not changed
                        resetYtemDef();
                    }
                }
                cellProxy = new CellProxyOJ(imageName, stackIndex);
                cellProxy.setYtemDefName(ytemDefName);
                cellProxy.addLocation(stackIndex, x, y, z);
            }
        } else {
            this.stackIndex = stackIndex;
            if ((cellProxy != null) && (!cellProxy.isClosed())) {
                cellProxy.close();
                if (OJ.getData().getYtemDefs().isComposite()) {
                    resetYtemDef();
                }
            }
            cellProxy = new CellProxyOJ(imageName, stackIndex);
            cellProxy.setYtemDefName(ytemDefName);
            cellProxy.addLocation(stackIndex, x, y, z);
        }
    }

    public String getImageName() {
        return imageName;
    }

    public CellProxyOJ getCellProxy() {
        return cellProxy;
    }

    public void openCell(CellOJ cell, int ytemIndex) {
        ytemDefName = cell.getYtemByIndex(ytemIndex).getYtemDef();
        if (cellProxy != null) {
            if (!cellProxy.isSameCell(cell)) {
                cellProxy.close();
                cellProxy = new CellProxyOJ(cell);
                cellProxy.setYtemDefName(ytemDefName);
            }
        } else {
            cellProxy = new CellProxyOJ(cell);
            cellProxy.openYtem(cell.getYtemByIndex(ytemIndex));
        }
        this.stackIndex = cell.getStackIndex();//9.12.2009
        cellProxy.setStackIndex(this.stackIndex);//7.12.2009

    }

    public void setYtemDefName(String ytemDefName) {
        if ((ytemDefName != null) && (!ytemDefName.equals(this.ytemDefName))) {
            this.ytemDefName = ytemDefName;
            if (OJ.getData().getYtemDefs().isComposite()) {
                if ((cellProxy != null) && (!cellProxy.isClosed())) {
                    cellProxy.setYtemDefName(ytemDefName);
                } else {
                    OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
                }
            } else {
                if ((cellProxy != null) && (!cellProxy.isClosed())) {
                    cellProxy.close();
                }
                cellProxy = null;
                OJ.getData().getYtemDefs().setSelectedYtemDef(ytemDefName);
            }
        }
    }

    public String getYtemDefName() {
        return ytemDefName;
    }

    public void closeCellProxy() {
        if (cellProxy != null) {
            cellProxy.close();
            cellProxy = null;
        }
    }

    private void resetYtemDef() {
        if (OJ.getData().getYtemDefs().getYtemDefsCount() > 0) {
            setYtemDefName(OJ.getData().getYtemDefs().getYtemDefByIndex(0).getYtemDefName());
        }
    }
}
