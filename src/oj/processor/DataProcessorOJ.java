/**
 * DataProcessorOJ.java -- documented
 *
 * housekeeping of cells etc
 *
 */
package oj.processor;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import oj.OJ;
import oj.project.*;
import oj.project.shapes.*;
import oj.project.results.ColumnOJ;
import oj.project.results.QualifiersOJ;
import oj.project.results.statistics.StatisticsMacroOJ;
import oj.gui.DeleteCellsOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.QualifierChangedEventOJ;
import oj.processor.events.QualifierChangedListenerOJ;
import oj.processor.events.ResultChangedEventOJ;
import oj.processor.events.StatisticsChangedEventOJ;

public class DataProcessorOJ implements QualifierChangedListenerOJ {

    public DataProcessorOJ() {
        OJ.getEventProcessor().addQualifierChangedListener(this);
    }

    /**
     * Calculates amount of cells in current slice
     */
    public int getSliceCellsCount() {
        int count = 0;
        if (ImageProcessorOJ.getCurrentImage() != null) {
            ImageOJ img = OJ.getData().getImages().getImageByName(ImageProcessorOJ.getCurrentImageName());
            if ((img != null) && (img.getFirstCell() >= 0)) {
                for (int i = img.getFirstCell(); i <= img.getLastCell(); i++) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    if (cell.getStackIndex() == ImageProcessorOJ.getCurrentImage().getCurrentSlice()) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    public int getROICellsCount() {
        return 0;
    }

    /**
     * Deletes all cells in current image
     */
    private void deleteCellsImage() {
        if (ImageProcessorOJ.getCurrentImage() != null) {
            ImageOJ img = OJ.getData().getImages().getImageByName(ImageProcessorOJ.getCurrentImageName());
            if (img != null) {
                int firstCell = img.getFirstCell();
                if ((firstCell >= 0)) {//31.10.2008
                    for (int i = img.getLastCell(); i >= firstCell; i--) {
                        OJ.getData().getCells().removeCellByIndex(i);
                        OJ.getEventProcessor().fireCellChangedEvent();
                    }
                }
            }
        }
    }

    /**
     * Deletes all cells in current slice
     */
    private void deleteCellsInCurrentSlice() {
        if (ImageProcessorOJ.getCurrentImage() != null) {
            deleteCellsSlice(ImageProcessorOJ.getCurrentImage().getCurrentSlice());
        }
    }

    /**
     * Deletes all cells in a slice of current image
     */
    private void deleteCellsSlice(int slice) {
        if (ImageProcessorOJ.getCurrentImage() != null) {
            deleteCellsSlice(ImageProcessorOJ.getCurrentImageName(), slice, false);//31.8.2009
        }
    }

    /**
     *
     * @param imageName
     * @param slice
     * @param shiftCells true when slice will be deleted afterwards, but not
     * used yet
     */
    public void deleteCellsSlice(String imageName, int slice, boolean shiftCells) {
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();
            for (int i = last_cell; i >= first_cell; i--) {
                if (OJ.getData().getCells().getCellByIndex(i).getStackIndex() == slice) {
                    OJ.getData().getCells().removeCellByIndex(i);
                    OJ.getEventProcessor().fireCellChangedEvent();
                }
            }
            if (shiftCells) {
                first_cell = img.getFirstCell();
                last_cell = img.getLastCell();
                if (first_cell >= 0) {
                    for (int i = last_cell; i >= first_cell; i--) {
                        if (OJ.getData().getCells().getCellByIndex(i).getStackIndex() > slice) {
                            OJ.getData().getCells().getCellByIndex(i).setStackIndex(OJ.getData().getCells().getCellByIndex(i).getStackIndex() - 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Entire object will be deleted if one of its points lies on the deleted
     * slice.
     */
    public void deleteCellsAfterDeleteSlice(String imageName, int deletedSlice) {//11.4.2009
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();

            if (first_cell >= 0) {
                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    if (cell.getStackIndex() == deletedSlice) {
                        cell.setToBeKilled(true);
                    }
                    if (cell.getStackIndex() > deletedSlice) {
                        cell.setStackIndex(cell.getStackIndex() - 1);
                    }
                    int nYtems = cell.getYtemsCount();
                    for (int jj = 0; jj < nYtems; jj++) {
                        YtemOJ ytem = cell.getYtemByIndex(jj);

                        if (ytem.getStackIndex() == deletedSlice) {
                            cell.setToBeKilled(true);
                        }
                        if (ytem.getStackIndex() > deletedSlice) {
                            ytem.setStackIndex(ytem.getStackIndex() - 1);
                        }

                        int nPoints = ytem.getLocationsCount();
                        for (int pp = 0; pp < nPoints; pp++) {
                            LocationOJ thisLoc = ytem.getLocation(pp);
                            double zz = thisLoc.getZ();
                            if (Math.round(zz) == deletedSlice) {// kill whole if you would loose one point
                                cell.setToBeKilled(true);
                            }
                            if (Math.round(zz) >= deletedSlice) {
                                thisLoc.setZ(zz - 1);
                            }
                        }
                    }
                }
            }
        }
        OJ.getData().getCells().killMarkedCells();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    //deletes all objects that hit at least one of deleted slices
    //decreases zpositions by deletedSlices.length if z is > deletedSlices[0];
    public void adjustZPositions(String imageName, int[] deletedSlices ) {//11.4.2009
        int len = deletedSlices.length;
        int first = deletedSlices[0];
        int last = deletedSlices[len - 1];
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();

            if (first_cell >= 0) {
                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    int thisZ = cell.getStackIndex();
                    if (thisZ >= first && thisZ <= last) {
                        cell.setToBeKilled(true);
                    }

                    if (!cell.getToBeKilled()) {
                        int nYtems = cell.getYtemsCount();
                        for (int jj = 0; jj < nYtems; jj++) {
                            YtemOJ ytem = cell.getYtemByIndex(jj);
                            thisZ = ytem.getStackIndex();
                            if (thisZ >= first && thisZ <= last) {
                                cell.setToBeKilled(true);
                                break;
                            }
                        }
                    }
                }



                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    int thisZ = cell.getStackIndex();
                    if (thisZ > last) {
                        thisZ -= len;
                        cell.setStackIndex(thisZ);
                    }

                    int nYtems = cell.getYtemsCount();
                    for (int jj = 0; jj < nYtems; jj++) {
                        YtemOJ ytem = cell.getYtemByIndex(jj);
                        thisZ = ytem.getStackIndex();
                        if (thisZ > last) {
                            thisZ -= len;
                            ytem.setStackIndex(thisZ);
                        }
                        int nPoints = ytem.getLocationsCount();
                        for (int pp = 0; pp < nPoints; pp++) {
                            LocationOJ thisLoc = ytem.getLocation(pp);
                            double zz = thisLoc.getZ();
                            if (Math.round(zz) > last) {
                                thisLoc.setZ(zz - len);
                            }

                        }
                    }
                }
            }
        }

        OJ.getData().getCells().killMarkedCells();
        OJ.getEventProcessor().fireCellChangedEvent();
        OJ.getDataProcessor().recalculateResults();

    }

    /**
     * Entire object will be deleted if one of its points lies on the deleted
     * slice.
     */
    public void adjustZPositionsOld(String imageName, int oldZ, int newZ) {//11.4.2009
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();

            if (first_cell >= 0) {
                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    if (cell.getStackIndex() == oldZ) {
                        cell.setStackIndex(newZ);
                    }

                    int nYtems = cell.getYtemsCount();
                    for (int jj = 0; jj < nYtems; jj++) {
                        YtemOJ ytem = cell.getYtemByIndex(jj);

                        if (ytem.getStackIndex() == oldZ) {
                            ytem.setStackIndex(newZ);
                        }


                        int nPoints = ytem.getLocationsCount();
                        for (int pp = 0; pp < nPoints; pp++) {
                            LocationOJ thisLoc = ytem.getLocation(pp);
                            double zz = thisLoc.getZ();
                            if (Math.round(zz) == oldZ) {// kill whole if you would loose one point
                                thisLoc.setZ(zz - oldZ + newZ);
                            }

                        }
                    }
                }
            }
        }
        OJ.getData().getCells().killMarkedCells();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Earmark cells to be deleted that hold a point in that slice
     */
    public void earMarkCellsToDelete(String imageName, int deletedSlice) {//25.
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();

            if (first_cell >= 0) {
                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
                    if (cell.getStackIndex() == deletedSlice) {
                        cell.setToBeKilled(true);
                    }

                    int nYtems = cell.getYtemsCount();
                    for (int jj = 0; jj < nYtems; jj++) {
                        YtemOJ ytem = cell.getYtemByIndex(jj);

                        if (ytem.getStackIndex() == deletedSlice) {
                            cell.setToBeKilled(true);
                        }
                        int nPoints = ytem.getLocationsCount();
                        for (int pp = 0; pp < nPoints; pp++) {
                            LocationOJ thisLoc = ytem.getLocation(pp);
                            double zz = thisLoc.getZ();
                            if (Math.round(zz) == deletedSlice) {// kill whole if you would loose one point
                                cell.setToBeKilled(true);
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * Z positions of markers will be increased if affected by adding a slice.
     */
    public void updateCellsAfterAddSlice(String imageName, int slice) {//11.4.2009
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();


            if (first_cell >= 0) {
                for (int i = last_cell; i >= first_cell; i--) {
                    CellOJ cell = OJ.getData().getCells().getCellByIndex(i);

                    if (cell.getStackIndex() > slice) {
                        cell.setStackIndex(slice + 1);
                    }
                    int nYtems = cell.getYtemsCount();
                    for (int jj = 0; jj < nYtems; jj++) {
                        YtemOJ ytem = cell.getYtemByIndex(jj);
                        int nPoints = ytem.getLocationsCount();
                        for (int pp = 0; pp < nPoints; pp++) {
                            LocationOJ thisLoc = ytem.getLocation(pp);
                            double zz = thisLoc.getZ();

                            if (Math.round(zz) > slice) {
                                thisLoc.setZ(zz + 1);
                            }
                        }
                    }
                }
            }
        }
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    private void deleteCellsROI() {
    }

    /**
     * Deletes a range of cells
     */
    private void deleteCellsRange(int minValue, int maxValue) {
        if ((minValue >= 0) && (maxValue >= minValue)) {
            OJ.getData().getCells().removeCellsRange(minValue, maxValue);
        }
    }

    /**
     * Deletes all cells outside of this range
     */
    private void keepCellsRange(int minValue, int maxValue) {
        if ((minValue >= 0) && (maxValue >= minValue)) {
            for (int i = OJ.getData().getCells().getCellsCount() - 1; i > maxValue; i--) {
                OJ.getData().getCells().removeCellByIndex(i);
                OJ.getEventProcessor().fireCellChangedEvent();
            }
            for (int i = minValue - 1; i >= 0; i--) {
                OJ.getData().getCells().removeCellByIndex(i);
                OJ.getEventProcessor().fireCellChangedEvent();
            }
        }
    }

    /**
     * deletes a range of cells
     */
    public void removeCells(int deleteStatus, int minValue, int maxValue) {
        switch (deleteStatus) {
            case DeleteCellsOJ.DELETE_RANGE:
                deleteCellsRange(minValue, maxValue);
                break;
            case DeleteCellsOJ.KEEP_RANGE:
                keepCellsRange(minValue, maxValue);
                break;
            case DeleteCellsOJ.DELETE_IMAGE:
                deleteCellsImage();
                break;
            case DeleteCellsOJ.DELETE_SLICE:
                deleteCellsInCurrentSlice();
                break;
            case DeleteCellsOJ.DELETE_ROI:
                deleteCellsROI();
                break;
            case DeleteCellsOJ.DELETE_QUALIFIED:
                OJ.getData().getCells().deleteQualifiedCells();
                OJ.getEventProcessor().fireCellChangedEvent();
                break;
            case DeleteCellsOJ.DELETE_UNQUALIFIED:
                OJ.getData().getCells().deleteUnqualifiedCells();
                OJ.getEventProcessor().fireCellChangedEvent();
                break;
            default:
        }
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * closes cell if it was open, then selects it.
     */
    public void selectCell(int cellIndex) {
        int index = OJ.getData().getCells().getOpenCellIndex();
        if (index >= 0) {
            OJ.getData().getCells().getCellByIndex(index).setOpen(false);
            OJ.getEventProcessor().fireCellChangedEvent(index, CellChangedEventOJ.CELL_CLOSE_EVENT);
        }
        if (OJ.getData().getCells().getSelectedCellIndex() >= 0) {
            unselectCell();
            if (cellIndex < 0) {//15.5.2009
                return;
            }
        }

        if (cellIndex >= 0) {
            OJ.getData().getCells().getCellByIndex(cellIndex).setSelected(true);
        }
        OJ.getEventProcessor().fireCellChangedEvent(cellIndex, CellChangedEventOJ.CELL_SELECT_EVENT);

    }

    /**
     * Deselects a cell
     */
    public void unselectCell() {
        if (OJ.getData().getCells().getSelectedCellIndex() >= 0) {
            int cell_index = OJ.getData().getCells().getSelectedCellIndex();
            OJ.getData().getCells().getCellByIndex(cell_index).setSelected(false);
            OJ.getEventProcessor().fireCellChangedEvent(cell_index, CellChangedEventOJ.CELL_UNSELECT_EVENT);
        }
    }

    /**
     * swaps position of two linked images so they appear in different order.
     * Cell numbers are reordered so that lower image numbers always have lower
     * cell numbers
     */
    public void swapImages(int firstImageIndex, int secondImageIndex) {
        String first_image_name = OJ.getData().getImages().getImageByIndex(firstImageIndex).getName();
        String second_image_name = OJ.getData().getImages().getImageByIndex(secondImageIndex).getName();
        OJ.getData().getImages().swapImages(firstImageIndex, secondImageIndex);
        OJ.getEventProcessor().fireImageChangedEvent(first_image_name, second_image_name, ImageChangedEventOJ.IMAGES_SWAP);
        OJ.getData().getCells().sortCells();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Sorts linked image names alphabetically, then sorts cells (that carry the
     * image name)
     */
    public void sortImages() {
        OJ.getData().getImages().sortImages();
        OJ.getEventProcessor().fireImageChangedEvent(ImageChangedEventOJ.IMAGES_SORT);
        OJ.getData().getCells().sortCells();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Selects and shows cell upon underlying image and slice
     */
    public void showCell(int cellNumber) {
        CellsOJ cells = OJ.getData().getCells();
        if ((cellNumber >= 0) && (cellNumber < OJ.getData().getCells().getCellsCount())) {
            selectCell(cellNumber);
            CellOJ cell = cells.getCellByIndex(cellNumber);
            String imgName = cell.getImageName();
            ImageOJ img = OJ.getData().getImages().getImageByName(imgName);
            ImagePlus imp = img.getImagePlus();
            if (imp == null) {//wasn't open
                OJ.getImageProcessor().openImage(imgName);
                imp = IJ.getImage();

            }

//            ImagePlus imp2 = null;
//            //String image_name = OJ.getData().getImages().getImageByName(cell.getImageName()).getName();
//            String image_name = cell.getImageName();
//            ImageWindow imgw = OJ.getImageProcessor().getOpenLinkedImageWindow(image_name);
//            if (imgw == null) {
//                OJ.getImageProcessor().openImage(image_name);
//                imp2 = IJ.getImage();
//
//            } else {
//                imp2 = imgw.getImagePlus();
//            }
//            
//            


            if (imp != null) {
                if (imp.isHyperStack()) {
                    imp.setPosition(cell.getStackIndex());
                } else {
                    imp.setSlice(cell.getStackIndex());
                }

                IJ.selectWindow(imp.getID());
                OJ.getImageProcessor().addToOpenedImages(imp);
                ImageWindow thisImgW = imp.getWindow();
                if (thisImgW != null) {
                    WindowManager.toFront(thisImgW);//de-iconifies! 6.11.2011 
                    WindowManager.setCurrentWindow(thisImgW);
                }//6.1.2013}
            } else {
                IJ.showMessage("ShowCell failed: \nImage " + imgName + " not available!");
            }
        }
    }

    /**
     * Qualifies a cell
     */
    public void qualifyCell(int index, boolean qq) {
        if (qq) {
            OJ.getData().getCells().qualifyCell(index);
        } else {
            OJ.getData().getCells().unqualifyCell(index);
        }
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Qualifies a cell
     */
    public void qualifyCell(CellOJ cell, boolean qq) {
        cell.setQualified(qq);
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     *
     * adds a cell to the list of cells
     */
    public boolean addCell(CellOJ cell) {
        boolean added = OJ.getData().getCells().addCell(cell);
        qualifyCell(cell);
        OJ.getEventProcessor().fireCellChangedEvent();
        return added;
    }

    /**
     * Closes a cell so that no items can be added
     */
    public void closeCell() {
        if (OJ.getData().getCells().getOpenCellIndex() >= 0) {
            OJ.getData().getCells().getCellByIndex(OJ.getData().getCells().getOpenCellIndex()).setOpen(false);
            OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().getOpenCellIndex(), CellChangedEventOJ.CELL_CLOSE_EVENT);
        }
    }

    /**
     *
     * Opens an existing cell so that more items can be added
     */
    public void openCell(int index) {
        int open_index = OJ.getData().getCells().getOpenCellIndex();
        if ((open_index >= 0) && (open_index != index)) {
            OJ.getData().getCells().getCellByIndex(OJ.getData().getCells().getOpenCellIndex()).setOpen(false);
            OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().getOpenCellIndex(), CellChangedEventOJ.CELL_CLOSE_EVENT);
        }
        OJ.getData().getCells().getCellByIndex(index).setOpen(true);
        OJ.getEventProcessor().fireCellChangedEvent(OJ.getData().getCells().getOpenCellIndex(), CellChangedEventOJ.CELL_OPEN_EVENT);
    }

    /**
     * Replaces an existing cell object, and returns the old one
     */
    public CellOJ setCell(int index, CellOJ cell) {
        CellOJ old = OJ.getData().getCells().setCell(index, cell);
        qualifyCells();
        OJ.getEventProcessor().fireCellChangedEvent();
        return old;
    }

    /**
     * Deletes an existing cell
     */
    public void removeCellByIndex(int index) {
        OJ.getData().getCells().removeCellByIndex(index);
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     *
     * Moves a cell point to a different location and adjust stackIndex if
     * necessary
     */
    public void movePoint(int index, LocationOJ thisLoc) {
        CellOJ cell = OJ.getData().getCells().getSelectedCell();
        YtemOJ ytem = cell.getSelectedYtem();
        ytem.setLocation(index, thisLoc);
        ytem.setStackIndex((Math.round(thisLoc.z)));
        YtemOJ firstYtem = cell.getYtemByIndex(0);
        cell.setStackIndex(firstYtem.getStackIndex());

        OJ.getEventProcessor().fireYtemChangedEvent();
    }

    /**
     * Deletes a cell
     */
    public void removeCell(CellOJ cell) {
        OJ.getData().getCells().removeCell(cell);
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Deletes all cells
     */
    public void removeAllCells() {
        OJ.getData().getCells().removeAllCells();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Adds an Ytem to an existing cell
     */
    public boolean addYtem(CellOJ cell, YtemOJ ytem) {
        boolean added = cell.add(ytem);
        qualifyCell(cell);
        OJ.getEventProcessor().fireYtemChangedEvent();
        return added;
    }

    /**
     * Adds an Ytem to an existing cell
     */
    public boolean addYtem(int cellIndex, YtemOJ ytem) {
        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
        return addYtem(cell, ytem);
    }

    /**
     * Adds a location to an existing Ytem
     *
     * @return false if no success
     */
    public boolean addLocation(YtemOJ ytem, LocationOJ location) {
        boolean added = ytem.add(location);
        qualifyCell(ytem.getCell());
        OJ.getEventProcessor().fireYtemChangedEvent();
        return added;
    }

    /**
     * Creates an empty Ytem
     */
    public YtemOJ createNewYtem(int ytemDefIndex) {
        YtemDefOJ ytemDef = OJ.getData().getYtemDefs().getYtemDefByIndex(ytemDefIndex);
        return createNewYtem(ytemDef.getYtemDefName());
    }

    /**
     * Creates an empty Ytem
     */
    public YtemOJ createNewYtem(String ytemDefName) {
        YtemDefOJ ytmd = OJ.getData().getYtemDefs().getYtemDefByName(ytemDefName);
        switch (ytmd.getYtemType()) {
            case YtemDefOJ.YTEM_TYPE_ANGLE:
                return new AngleOJ(ytmd.getYtemDefName());
            case YtemDefOJ.YTEM_TYPE_LINE:
                return new LineOJ(ytmd.getYtemDefName());
            case YtemDefOJ.YTEM_TYPE_POINT:
                return new PointOJ(ytmd.getYtemDefName());
            case YtemDefOJ.YTEM_TYPE_POLYGON:
                return new PolygonOJ(ytmd.getYtemDefName());
            case YtemDefOJ.YTEM_TYPE_ROI:
                return new RoiOJ(ytmd.getYtemDefName());
            case YtemDefOJ.YTEM_TYPE_SEGLINE:
                return new SeglineOJ(ytmd.getYtemDefName());
            default:
                return null;
        }
    }

    /**
     * Replace an existing ytem, and returns the old one
     */
    public YtemOJ setYtem(CellOJ cell, int ytemIndex, YtemOJ ytem) {
        YtemOJ ytm = cell.setYtem(ytemIndex, ytem);
        qualifyCell(cell);
        OJ.getEventProcessor().fireYtemChangedEvent();
        return ytm;
    }

    /**
     * Replace an existing ytem, and returns the old one
     */
    public LocationOJ setLocation(YtemOJ ytem, int locationIndex, LocationOJ location) {
        LocationOJ loc = ytem.setLocation(locationIndex, location);
        qualifyCell(ytem.getCell());
        OJ.getEventProcessor().fireYtemChangedEvent();
        return loc;
    }

    /**
     * Replace an existing ytem, and returns the old one
     */
    public YtemOJ setYtem(int cellIndex, int ytemIndex, YtemOJ ytem) {
        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            YtemOJ ytm = cell.setYtem(ytemIndex, ytem);
            qualifyCell(cell);
            OJ.getEventProcessor().fireYtemChangedEvent();
            return ytm;
        } else {
            return null;
        }
    }

    /**
     * Removes all ytems of a cell that belong to a certain ytemtype
     *
     */
    public void removeYtemByDef(CellOJ cell, String ytemDef) {
        for (int i = cell.getYtemsCount() - 1; i >= 0; i--) {
            if (cell.getYtemByIndex(i).getYtemDef().equals(ytemDef)) {
                cell.removeYtemByIndex(i);
                OJ.getEventProcessor().fireYtemChangedEvent();
            }
        }
    }

    /**
     * Removes all ytems in all cells that belong to a certain ytemtype
     *
     */
    public void removeYtemByDef(String ytemDef) {
        for (int i = OJ.getData().getCells().getCellsCount() - 1; i >= 0; i--) {
            CellOJ cell = OJ.getData().getCells().getCellByIndex(i);
            removeYtemByDef(cell, ytemDef);
            if (cell.getYtemsCount() == 0) {
                removeCellByIndex(i);
            }
        }
    }

    /**
     * Removes one ytem in a cell, and removes cell if it becomes empty
     */
    public void removeYtemByIndex(CellOJ cell, int ytemIndex) {
        cell.removeYtemByIndex(ytemIndex);
        if (cell.getYtemsCount() == 0) {
            removeCell(cell);
        }
        qualifyCell(cell);
        OJ.getEventProcessor().fireYtemChangedEvent();
    }

    /**
     * Removes one ytem in a cell, and removes cell if it becomes empty
     */
    public void removeYtemByIndex(int cellIndex, int ytemIndex) {
        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            cell.removeYtemByIndex(ytemIndex);
            if (cell.getYtemsCount() == 0) {
                removeCell(cell);
            }
            qualifyCell(cell);
            OJ.getEventProcessor().fireYtemChangedEvent();
        }
    }

    /**
     * Removes one ytem in a cell, and removes cell if it becomes empty
     */
    public void removeYtem(CellOJ cell, YtemOJ ytm) {
        cell.removeYtem(ytm);
        if (cell.getYtemsCount() == 0) {
            removeCell(cell);
        }
        qualifyCell(cell);
        OJ.getEventProcessor().fireYtemChangedEvent();
    }

    /**
     * Removes one ytem in a cell, and removes cell if it becomes empty
     */
    public void removeYtem(int cellIndex, YtemOJ ytm) {
        CellOJ cell = OJ.getData().getCells().getCellByIndex(cellIndex);
        if (cell != null) {
            cell.removeYtem(ytm);
            qualifyCell(cell);
            OJ.getEventProcessor().fireYtemChangedEvent();
        }
    }

    /**
     * Removes a location from an ytem
     */
    public void removeLocation(YtemOJ ytem, int index) {
        ytem.removelocationByIndex(index);
        qualifyCells();
        OJ.getEventProcessor().fireYtemChangedEvent();
    }

    /**
     * Replaces a column and returns the old one
     */
    public ColumnOJ setColumn(int index, ColumnOJ column) {
        ColumnOJ old_column = OJ.getData().getResults().getColumns().setColumn(index, column);
        OJ.getEventProcessor().fireColumnChangedEvent(old_column.getName(), column.getName(), ColumnChangedEventOJ.COLUMN_EDITED);
        return old_column;
    }

    /**
     * Selects an Ytem definition (in ObjectJ Tools?) and returns old selection
     */
    public String setYtemDefSelected(String name) {
        String old_selection = OJ.getData().getYtemDefs().getSelectedYtemDefName();
        OJ.getData().getYtemDefs().setSelectedYtemDef(name);
        return old_selection;
    }

    /**
     * Returns numbe of cells in current image
     */
    public int getImageCellsCount() {
        if (ImageProcessorOJ.getCurrentImage() != null) {
            ImageOJ image = OJ.getData().getImages().getImageByName(ImageProcessorOJ.getCurrentImageName());
            if (image != null) {
                return image.getCellCount();
            }
        }
        return 0;
    }

    /**
     * qualifies a single cell
     */
    public void qualifyCell(CellOJ cell) {
        switch (OJ.getData().getResults().getQualifiers().getQualifyMethod()) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                cell.setQualified(true);
                break;
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                cell.setQualified(false);
                break;
            case QualifiersOJ.QUALIFY_METHOD_IF:
                int cell_index = OJ.getData().getCells().indexOfCell(cell);
                boolean qualified_cell = OJ.getData().getResults().getQualified(cell_index);
                cell.setQualified(qualified_cell);
                break;
        }
    }

    /**
     * Performs comparisons and sets qualifier flags of all cells
     */
    public void qualifyCells() {
        if (!OJ.isValidData()) {
            return;
        }

        switch (OJ.getData().getResults().getQualifiers().getQualifyMethod()) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                OJ.getData().getCells().qualifyAllCells();
                break;
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                OJ.getData().getCells().disqualifyAllCells();
                break;
            case QualifiersOJ.QUALIFY_METHOD_IF:
                boolean[] qualifierFlags = OJ.getData().getResults().qualifyCells();//
                for (int i = 0; i < qualifierFlags.length; i++) {
                    if (qualifierFlags[i]) {
                        OJ.getData().getCells().qualifyCell(i);
                    } else {
                        OJ.getData().getCells().unqualifyCell(i);
                    }
                }
                break;
        }
    }

    /**
     * Recalculates results
     */
    public void recalculateResults() {
        OJ.getData().getResults().recalculate();
        OJ.getEventProcessor().fireCellChangedEvent();
    }

    /**
     * Adds one more string result to the bottom of a unlinked column (used by
     * MacroProcessor). (Also used to fill the holes with "")
     */
    public void addStringResult(String columnName, String value) {
        OJ.getData().getResults().getColumns().getColumnByName(columnName).addStringResult(value);
        int row = OJ.getData().getResults().getColumns().getColumnByName(columnName).getResultCount() - 1;
        OJ.getEventProcessor().fireResultChangedEvent(columnName, row, ResultChangedEventOJ.RESULTS_ADDED);
    }

    /**
     * Adds one more numeric result to the bottom of a unlinked column (used by
     * MacroProcessor). (Also used to fill the holes with NaN)
     */
    public void addDoubleResult(String columnName, double value) {
        OJ.getData().getResults().getColumns().getColumnByName(columnName).addDoubleResult(value);
        int row = OJ.getData().getResults().getColumns().getColumnByName(columnName).getResultCount() - 1;
        OJ.getEventProcessor().fireResultChangedEvent(columnName, row, ResultChangedEventOJ.RESULTS_ADDED);
    }

    /**
     * Sets a string result into an unlinked or linked column
     */
    public void setStringResult(String columnName, int index, String value) {
        OJ.getData().getResults().getColumns().getColumnByName(columnName).setStringResult(index, value);
        OJ.getEventProcessor().fireResultChangedEvent(columnName, index, ResultChangedEventOJ.RESULTS_EDITED);
    }

    /**
     * Sets a numeric result into an unlinked or linked column
     */
    public void setDoubleResult(String columnName, int index, double value) {
        OJ.getData().getResults().getColumns().getColumnByName(columnName).setDoubleResult(index, value);
        OJ.getEventProcessor().fireResultChangedEvent(columnName, index, ResultChangedEventOJ.RESULTS_EDITED);
    }

    /**
     * Sets a macro to calculate exotic statistics (never tried
     */
    public void setStatisticsMacro(String name, String macro) {
        ((StatisticsMacroOJ) OJ.getData().getResults().getStatistics().getStatisticsByName(name)).setMacro(macro);
        for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
            ColumnOJ column = OJ.getData().getResults().getColumns().getColumnByIndex(i);
            column.getStatistics().getStatisticsByName(name).setDirty(true);
        }
        OJ.getEventProcessor().fireStatisticsChangedEvent(name, StatisticsChangedEventOJ.STATISTICS_EDITED);
    }

    /*
     * not used
     */
    public void updateYtemDefs() {
        OJ.getEventProcessor().fireYtemDefChangedEvent("", YtemDefChangedEventOJ.YTEMDEF_ADDED);
    }
    /*
     * we should kill this, it is not used. somehow tries to limit the slice
     * index
     */

    public void updateCellsSlice(String imageName, int slice) {
        ImageOJ img = OJ.getData().getImages().getImageByName(imageName);
        if ((img != null) && (img.getFirstCell() >= 0)) {
            int first_cell = img.getFirstCell();
            int last_cell = img.getLastCell();
            for (int i = last_cell; i >= first_cell; i--) {
                if (OJ.getData().getCells().getCellByIndex(i).getStackIndex() >= slice) {
                    OJ.getData().getCells().getCellByIndex(i).setStackIndex(OJ.getData().getCells().getCellByIndex(i).getStackIndex() + 1);
                    OJ.getEventProcessor().fireCellChangedEvent();
                }
            }
        }
    }

    /*
     * swaps slices together with theil metadata. Obsolete because Wayne has
     * this too meanwhile
     */
    public void swapSlices(int sliceA, int sliceB) {
        for (int i = 0; i < OJ.getData().getCells().getCellsCount(); i++) {
            if (OJ.getData().getCells().getCellByIndex(i).getStackIndex() == sliceA) {
                OJ.getData().getCells().getCellByIndex(i).setStackIndex(sliceB);
            } else if (OJ.getData().getCells().getCellByIndex(i).getStackIndex() == sliceB) {
                OJ.getData().getCells().getCellByIndex(i).setStackIndex(sliceA);
            }
        }
    }

    public void qualifierChanged(QualifierChangedEventOJ evt) {
        qualifyCells();
    }

    /*
     * Returns next Ytem Definition string
     */
    public static String getNextYtemDef(String ytemDefName) {
        int maxYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefsCount() - 1;
        int currentYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName);
        int newYtemDefIndex = getNextIndex(currentYtemDefIndex, maxYtemDefIndex);
        return OJ.getData().getYtemDefs().getYtemDefByIndex(newYtemDefIndex).getYtemDefName();
    }

    /*
     * Returns previous Ytem Definition string
     */
    public static String getPreviousYtemDef(String ytemDefName) {
        int maxYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefsCount() - 1;
        int currentYtemDefIndex = OJ.getData().getYtemDefs().getYtemDefIndexByName(ytemDefName);
        int newYtemDefIndex = getPreviousIndex(currentYtemDefIndex, maxYtemDefIndex);
        return OJ.getData().getYtemDefs().getYtemDefByIndex(newYtemDefIndex).getYtemDefName();
    }

    /*
     * General purpose range operation
     */
    public static int getNextIndex(int currentIndex, int maxIndex) {
        if (maxIndex < 0) {
            return -1;
        }
        if (currentIndex != maxIndex) {
            return currentIndex + 1;
        } else {
            return maxIndex;
        }
    }

    /*
     * General purpose range operation
     */ public static int getPreviousIndex(int currentIndex, int maxIndex) {
        if (maxIndex < 0) {
            return -1;
        }
        if (currentIndex != 0) {
            return currentIndex - 1;
        } else {
            return 0;
        }
    }

    /*
     * General purpose range operation (not used)
     */
    private static int getNextCircularIndex(int currentIndex, int maxIndex) {
        if (maxIndex < 0) {
            return -1;
        }
        if (currentIndex != maxIndex) {
            return currentIndex + 1;
        } else {
            return 0;
        }
    }

    /*
     * General purpose range operation (not used)
     */
    private static int getPreviousCircularIndex(int currentIndex, int maxIndex) {
        if (maxIndex < 0) {
            return -1;
        }
        if (currentIndex != 0) {
            return currentIndex - 1;
        } else {
            return maxIndex;
        }
    }
}
