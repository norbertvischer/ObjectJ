/*CellsOJ.java
 * fully documented 13.2.2010
 *
 * accomodates an arry of all "cells".
 * in the source code, we use the term cell instead of "object"
 */
package oj.project;

import ij.IJ;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import oj.OJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.ImageChangedListener2OJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;

/**
 * collection of all cells
 */
public class CellsOJ extends BaseAdapterOJ implements ImageChangedListener2OJ, ColumnChangedListenerOJ, YtemDefChangedListenerOJ {

    private static final long serialVersionUID = 4163957104769927202L;
    private transient int highestID = 0;//"highest" means "most negative"  cellID
    private ArrayList<CellOJ> cells = new ArrayList();//array of CellOJs
    private transient int newestCell = -1;//cell number of last edited cell

    public CellsOJ() {
        super();
    }

    public CellsOJ(IBaseOJ parent) {
        super(parent);
    }

    public void disqualifyAllCells() {
        for (CellOJ cell: cells) {
            cell.setQualified(false);
        }
    }

    public void killMarkedCells() {
        for (int index = cells.size() - 1; index >= 0; index--) {
            //boolean flag = ( cell).getToBeKilled();
            if ((cells.get(index)).getToBeKilled()) {
                cells.remove(index);
            }
        }
        setNewestCellIndex(-1);

    }

    /**
     * Removes cells with zero ytems, and ytems with zero points
     */
    public void killBadCells() {
        int badCount = 0;
        int cellCount = getCellsCount();//25.4.2009
        for (int cellIndex = cellCount - 1; cellIndex >= 0; cellIndex--) {
            boolean good = true;//21.4.2009
            CellOJ cell = getCellByIndex(cellIndex);
            int nYtems = cell.getYtemsCount();
            if (nYtems < 1) {
                good = false;
            }
            if (good) {
                for (int ytm = nYtems - 1; ytm >= 0; ytm--) {//23.4.2009
                    YtemOJ ytem = cell.getYtemByIndex(ytm);
                    if (ytem != null) {//24.4.2009
                        if (ytem.getLocationsCount() == 0) {
                            cell.removeYtem(ytem);
                            if (cell.getYtemsCount() < 1) {
                                good = false;
                            }
                        }
                    }
                }
            }
            if (!good) {
                cell.setToBeKilled(true);
                badCount++;
            }
        }
        if (badCount > 0) {
            ij.IJ.showMessage("Killing " + badCount + " bad object(s).");
            killMarkedCells();
            setNewestCellIndex(-1);

        }
    }

    /**
     * counts ytems of a certain name across the entire project
     */
    public int getYtemCount(String ytemDefName) {
        int count = 0;
        for (int i = 0; i < getCellsCount(); i++) {
            for (int j = 0; j < getCellByIndex(i).getYtemsCount(); j++) {
                if (getCellByIndex(i).getYtemByIndex(j).getYtemDef().equals(ytemDefName)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /**
     * inverts all qualify flags
     */
    public void invertCellsQualification() {
        for (int i = 0; i < cells.size(); i++) {
            (cells.get(i)).setQualified(!(cells.get(i)).isQualified());
        }
    }

    public void qualifyAllCells() {
        for (int i = 0; i < cells.size(); i++) {
            (cells.get(i)).setQualified(true);
        }
    }

    /**
     * index of last edited Cell (0-based)
     */
    public void setNewestCellIndex(int jj) {//16.8.2009
        newestCell = jj;
    }

    /**
     * index of last edited Cell (0-based)
     */
    public int getNewestCellIndex() {//16.8.2009
        return newestCell;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(cells);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        cells = (ArrayList) stream.readObject();
    }

    /**
     * scans all cells to check if one has changed
     *
     * @return
     */
    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < cells.size(); i++) {
                if ((cells.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int nextID() {
        this.highestID--;
        return highestID;
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < cells.size(); i++) {
            (cells.get(i)).setChanged(changed);
        }
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (cells == null) {
            cells = new ArrayList();
        }
        setNewestCellIndex(-1);
        highestID = 0;
        boolean id_set = (cells.size() > 0) && (getCellByIndex(0).getID() < 0);
        for (int i = 0; i < cells.size(); i++) {
            getCellByIndex(i).initAfterUnmarshalling(this);
            if (id_set) {
                if (highestID > getCellByIndex(i).getID()) {
                    highestID = getCellByIndex(i).getID();
                }
            } else {
                getCellByIndex(i).setID(nextID());
            }
        }
        OJ.getEventProcessor().addImageChangedListener(this);
        OJ.getEventProcessor().addColumnChangedListener(this);
        OJ.getEventProcessor().addYtemDefChangedListener(this);
    }

    public int getCloneCount(String def) {
        int co = 0;
        for (int i = 0; i < cells.size(); i++) {
            co += (cells.get(i)).getCloneCount(def);
        }
        return co;
    }

    /**
     * Used after click in image to find the closest object point. Only points
     * that are in the specified sliceSet are taken into account. Normally,
     * sliceSet has only one element. In case of a composite image, e.g. if
     * three channels are superimposed, sliceSet[] would have three elements.
     * Returns an array containing CellOJ, YtemOJ, LocationOJ and distance. All
     * four elements are null if distance larger than 6 screen pixels. 16.6.2008
     */
    public Object[] closestPoint(int img, double xx, double yy, int sliceNo/*1-based*/) {

        ImageOJ imag = OJ.getData().getImages().getImageByIndex(img);
        int nSlices = imag.getNumberOfSlices() * imag.getNumberOfFrames() * imag.getNumberOfChannels();//27.3.2009
        int low = OJ.getData().getYtemDefs().getVisRangeLow();
        int high = OJ.getData().getYtemDefs().getVisRangeHigh();
        low = sliceNo - low;
        if (low < 1) {
            low = 1;
        }
        high = sliceNo + high;
        if (high >= nSlices) {
            high = nSlices;
        }

        int range = high - low + 1;
        int[] sliceSet = new int[range];//so far ,only normal stacks work ( not multichannel display and vis flags)
        for (int jj = 0; jj < range; jj++) {
            sliceSet[jj] = low + jj;//25.3.2009
        }

        int firstCell = OJ.getData().getImages().getImageByIndex(img).getFirstCell();//-1 if no cells are found
        int lastCell = OJ.getData().getImages().getImageByIndex(img).getLastCell();
        CellOJ thisCell = null;
        Object[] closePt = new Object[4];//returns cell, object, point
        if (firstCell >= 0) {
            double magnification = IJ.getImage().getWindow().getCanvas().getMagnification();
            final double R8 = 8.0; // cursor must be closer than 8 screen pixels
            double minRad = (R8 / magnification);
            double min = 1e33;
            for (int jj = firstCell; jj <= lastCell; jj++) { //lastcell is already in next image!
                thisCell = getCellByIndex(jj);
                int nObjects = thisCell.getYtemsCount();
                for (int obj = 0; obj < nObjects; obj++) {
                    YtemOJ thisYtem = thisCell.getYtemByIndex(obj);
                    int nPoints = thisYtem.getLocationsCount();
                    for (int pp = 0; pp < nPoints; pp++) {
                        LocationOJ thisLoc = thisYtem.getLocation(pp);
                        for (int slc = 0; slc < sliceSet.length; slc++) {
                            if (Math.round(thisLoc.getZ()) == sliceSet[slc]) {
                                double dx = thisLoc.getX() - xx;
                                double dy = thisLoc.getY() - yy;
                                double dist = Math.sqrt(dx * dx + dy * dy);
                                if (dist < min && dist < minRad) {
                                    min = dist;
                                    closePt[0] = (Object) thisCell;
                                    closePt[1] = (Object) thisYtem;
                                    closePt[2] = (Object) thisLoc;
                                    closePt[3] = (Double) dist;
                                }
                            }
                        }
                    }
                }
            }
        }
        return closePt;
    }

    public void qualifyCell(int index) {
        (cells.get(index)).setQualified(true);
        changed = true;
    }

    public void unqualifyCell(int index) {
        (cells.get(index)).setQualified(false);
        changed = true;
    }

    public void deleteQualifiedCells() {
        int count = 0;
        for (int i = getCellsCount(); i > 0; i--) {
            if (getCellByIndex(i - 1).isQualified()) {
                removeCellByIndex(i - 1);
                count += 1;
            }
        }
        if (count > 0) {
            changed = true;
        }
        setNewestCellIndex(-1);
    }

    public void deleteUnqualifiedCells() {
        int count = 0;
        for (int i = getCellsCount(); i > 0; i--) {
            if (!getCellByIndex(i - 1).isQualified()) {
                removeCellByIndex(i - 1);
                count += 1;
            }
        }
        if (count > 0) {
            changed = true;
        }
        setNewestCellIndex(-1);
    }

    public int getQualifiedCellsCount() {
        int count = 0;
        for (int i = 0; i < getCellsCount(); i++) {
            if (getCellByIndex(i).isQualified()) {
                count = count + 1;
            }
        }
        return count;
    }

    public int[] getIndexes0(boolean includeUnqualified) {
        int cellCount = getCellsCount();
        int[] indexes = new int[cellCount];
        int count = 0;
        for (int jj = 0; jj < cellCount; jj++) {
            if (getCellByIndex(jj).isQualified() || includeUnqualified) {
                indexes[count] = jj;//zero-based
                count = count + 1;
            }
        }
        int[] returnArr = new int[count];
        System.arraycopy(indexes, 0, returnArr, 0, count);
        return returnArr;
    }

    public int getUnqualifiedCellsCount() {
        int count = 0;
        for (int i = 0; i < getCellsCount(); i++) {
            if (!getCellByIndex(i).isQualified()) {
                count = count + 1;
            }
        }
        return count;
    }

    public boolean addCell(CellOJ cell) {
        if (cells.size() == 0) {
            this.highestID = 0;//6.6.2014
        }

        int last_cell_index = getLastCellOnImage(cell.getImageName());
        if (last_cell_index < 0) {
            cells.add(0, cell);
            Collections.sort(cells, new CellComparator());
        } else {
            if (last_cell_index < (cells.size() - 1)) {
                cells.add(last_cell_index + 1, cell);
                Collections.sort(cells, new CellComparator());
            } else {
                cells.add(cell);
            }
        }
        cell.setID(nextID());
        cell.setParent(this);
        changed = true;
        return true;
    }

    public int getCellsCount() {
        return cells.size();
    }

    /**
     * returns index of cell, or -1 if not found
     */
    public int getCellIndex(CellOJ cell) {
        if (cell == null) {
            return -1;
        }
        return cells.indexOf(cell);
    }

    /**
     * returns cell with this index, or null if index out of range
     */
    public CellOJ getCellByIndex(int index) {
        if (cells == null || index < 0 || index >= cells.size()) {
            return null;
        }
        return cells.get(index);//+++exception ArrayIndexOutOfBoundsException: -1
    }

    public CellOJ setCell(int index, CellOJ cell) {
        CellOJ old_cell = cells.get(index);
        cells.set(index, cell);
        cell.setParent(this);
        changed = true;
        return old_cell;
    }

    public int indexOfCell(CellOJ cell) {
        return cells.indexOf(cell);
    }

    public void removeCellByIndex(int index) {
        cells.remove(index);
        changed = true;
        setNewestCellIndex(-1);
    }

    public void removeCell(CellOJ cell) {
        cells.remove(cell);
        changed = true;
        setNewestCellIndex(-1);

    }

    public CellOJ[] cellsToArray() {
        CellOJ[] result = new CellOJ[cells.size()];
        System.arraycopy(cells, 0, result, 0, cells.size());
        return result;
    }

    public void removeAllCells() {
        cells.clear();
        changed = true;
        setNewestCellIndex(-1);

    }

    /**
     * both indexes are inclusive
     */
    public void removeCellsRange(int fromIndex, int toIndex) {
        for (int jj = toIndex; jj >= fromIndex; jj--) {
            cells.remove(jj);
        }
        changed = true;
        setNewestCellIndex(-1);
    }

    public int getSelectedCellIndex() {
        for (int i = 0; i < cells.size(); i++) {
            if ((cells.get(i)).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public CellOJ getSelectedCell() {
        for (int i = 0; i < cells.size(); i++) {
            if ((cells.get(i)).isSelected()) {
                return cells.get(i);
            }
        }
        return null;
    }

    public void selectCell(int index) {
        for (int i = 0; i < cells.size(); i++) {
            (cells.get(i)).setSelected(i == index);
        }
    }

    public int getOpenCellIndex() {
        for (int i = 0; i < cells.size(); i++) {
            if ((cells.get(i)).isOpen()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return index (0-based) of first cell in image, or -1 if image is not
     * marked
     */
    public int getFirstCellOnImage(String imageName) {
        boolean found = false;
        int firstCell = cells.size() - 1;
        for (int i = 0; i < cells.size(); i++) {
            String image_name = (cells.get(i)).getImageName();
            if ((image_name.equals(imageName)) && (i <= firstCell)) {
                firstCell = i;
                found = true;
            }
        }
        if (!found) {
            firstCell = -1;
        }
        return firstCell;
    }

    public int getLastCellOnImage(String imageName) {
        int lastCell = -1;
        for (int i = 0; i < cells.size(); i++) {
            String image_name = (cells.get(i)).getImageName();
            if ((image_name.equals(imageName)) && (i > lastCell)) {
                lastCell = i;
            }
        }
        return lastCell;
    }

    public int getLastSliceInImage(String imageName) {
        int lastSlice = -1;
        for (int i = 0; i < cells.size(); i++) {
            CellOJ cell = cells.get(i);
            String image_name = cell.getImageName();
            if ((image_name.equals(imageName)) && (cell.getStackIndex() > lastSlice)) {
                lastSlice = cell.getStackIndex();
            }
        }
        return lastSlice;
    }

    /**
     * re-arranges cells so that cell indices and image indices are both in
     * ascending order
     */
    public void sortCells() {
        Collections.sort(cells, new CellComparator());
    }

    /**
     * special class for comparison
     */
    class CellComparator implements Comparator {

        public int compare(Object firstCell, Object secondCell) {
            if ((OJ.getData() == null) || (OJ.getData().getImages() == null)) {
                return 0;
            }
            Integer fci = OJ.getData().getImages().getIndexOfImage(((CellOJ) firstCell).getImageName());
            Integer sci = OJ.getData().getImages().getIndexOfImage(((CellOJ) secondCell).getImageName());
            return fci.compareTo(sci);
        }
    }

    /**
     * a cell contains all the linked results as properties, so it needs to know
     * if a column has changed
     */
    public void columnChanged(ColumnChangedEventOJ evt) {
        switch (evt.getOperation()) {
            case ColumnChangedEventOJ.COLUMN_DELETED:
                for (int i = 0; i < getCellsCount(); i++) {
                    CellOJ cell = getCellByIndex(i);
                    if (cell.properties.containsKey(evt.getOldName())) {
                        cell.properties.remove(cell.properties.get(evt.getOldName()));
                    }
                }
                break;
            case ColumnChangedEventOJ.COLUMN_EDITED:
                if ((evt.getOldName() != null) && (!evt.getNewName().equals(evt.getOldName()))) {
                    for (int i = 0; i < getCellsCount(); i++) {
                        CellOJ cell = getCellByIndex(i);
                        if (cell.properties.containsKey(evt.getOldName())) {
                            Object value = cell.properties.get(evt.getOldName());
                            cell.properties.remove(cell.properties.get(evt.getOldName()));
                            cell.properties.put(evt.getNewName(), value);
                        }
                    }
                }
                break;
        }
    }

    /**
     * a cell holds the name of the owning window, so it needs to know if that
     * image has changed (e.g. renamed), or remove the cell if owner window is
     * removed
     */
    public void imageChanged(ImageChangedEventOJ evt) {
        if (evt.getOperation() == ImageChangedEventOJ.IMAGE_DELETED) {
            for (int i = getCellsCount() - 1; i >= 0; i--) {
                if (getCellByIndex(i).getImageName().equals(evt.getName())) {
                    cells.remove(i);
                }
            }
        } else if (evt.getOperation() == ImageChangedEventOJ.IMAGE_EDITED) {
            if (!evt.getNewImageName().equals(evt.getOldImageName())) {
                for (int i = getCellsCount() - 1; i >= 0; i--) {
                    if (getCellByIndex(i).getImageName().equals(evt.getOldImageName())) {
                        getCellByIndex(i).setImageName(evt.getNewImageName());
                    }
                }
            }
        }
    }

    /**
     * a cell holds the names of the ytems, so it has to change the ytem names
     * if the user renames them.
     */
    public void ytemDefChanged(YtemDefChangedEventOJ evt) {
        switch (evt.getOperation()) {
            case YtemDefChangedEventOJ.YTEMDEF_EDITED:
                if (!evt.getOldName().equals(evt.getNewName())) {
                    for (int i = 0; i < getCellsCount(); i++) {
                        CellOJ cell = getCellByIndex(i);
                        for (int j = 0; j < cell.getYtemsCount(); j++) {
                            YtemOJ ytem = cell.getYtemByIndex(j);
                            if (ytem.getYtemDef().equals(evt.getOldName())) {
                                ytem.setObjectDef(evt.getNewName());
                            }
                        }
                    }
                }
                break;
            case YtemDefChangedEventOJ.YTEMDEF_DELETED:
                for (int i = getCellsCount() - 1; i >= 0; i--) {
                    CellOJ cell = getCellByIndex(i);
                    for (int j = cell.getYtemsCount() - 1; j >= 0; j--) {
                        YtemOJ ytem = cell.getYtemByIndex(j);
                        if (ytem.getYtemDef().equals(evt.getName())) {
                            cell.removeYtemByIndex(j);
                        }
                    }
                    if (cell.getYtemsCount() == 0) {
                        removeCellByIndex(i);
                    }
                }
                break;
            default:
        }
    }
}
