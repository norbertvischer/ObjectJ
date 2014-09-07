/*
 * YtemDefsOJ.java
 *
 * Definitions of all ytem types that appear in the ObjectJ tools window
 * fully documented 3.9.2010
 */
package oj.project;

import java.util.ArrayList;
import oj.OJ;
import oj.processor.events.YtemDefChangedEventOJ;

public class YtemDefsOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 4340672126729526935L;
    private boolean multicollect;//true to create composite objects
    private boolean threedytems;// true if an object is expected to have points in different slices
    private boolean showcellnumber;// visibility of number label
    private transient int visRangeLow;// >0 if visible in neighbor slices
    private transient int visRangeHigh;//>0 if visible in neighbor slices
    private ArrayList<YtemDefOJ> ytemDefList = new ArrayList();//contains YtemDefs
    private transient boolean cellLayerVisible = true;//show or hide markers 13.4.
    private transient String selectedYtemDefName = "";//
    private transient boolean ytemVisibilitySwitchEnabled = false;//maste switch to show/hide individual ytem types

    /**
     * Creates a new instance of YtemDefsOJ
     */
    public YtemDefsOJ() {
        this.multicollect = true;
        this.threedytems = false;
        this.showcellnumber = true;
        this.visRangeHigh = 0;
        this.visRangeLow = 0;
    }

    /**
     * >0 if visible in neighbor slices
     */
    public int getVisRangeLow() {
        return visRangeLow;
    }

    /**
     * >0 if visible in neighbor slices
     */
    public int getVisRangeHigh() {
        return visRangeHigh;
    }

    /**
     * >0 if visible in neighbor slices
     */
    public void setVisRange(int low, int high) {
        visRangeLow = low;
        visRangeHigh = high;

    }

    /**
     * @return true if list or one of the components has changed
     */
    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < ytemDefList.size(); i++) {
                if ((ytemDefList.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSelectedYtemDefName() {
        return selectedYtemDefName;
    }

    public YtemDefOJ getSelectedObjectDef() {
        if (selectedYtemDefName != null) {
            return getYtemDefByName(selectedYtemDefName);
        } else {
            return null;
        }
    }

    public void setSelectedYtemDef(String ytemDefName) {
        this.selectedYtemDefName = ytemDefName;
        YtemDefOJ selectedYtemDef = getYtemDefByName(ytemDefName);
        selectedYtemDef.setVisible(true);//10.11.2011
        OJ.getEventProcessor().fireYtemDefSelectionChangedEvent(ytemDefName);
    }

    /**
     * propagates 'changed' flag
     */
    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < ytemDefList.size(); i++) {
            (ytemDefList.get(i)).setChanged(changed);
        }
    }

    /**
     * clears the list and sets parameters to default
     */
    public void clear() {
        multicollect = true;
        threedytems = false;
        showcellnumber = true;
        ytemDefList.clear();
        changed = true;
    }

    /**
     * @return number of defined ytems
     */
    public int getYtemDefsCount() {
        return ytemDefList.size();
    }

    /**
     * append an ytem type to the end of the ytemDefs list
     */
    public boolean addYtemDef(YtemDefOJ ytemDef) {
        if (ytemDefList.add(ytemDef)) {
            ytemDef.setParent(this);
            changed = true;
            OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDef.getYtemDefName(), YtemDefChangedEventOJ.YTEMDEF_ADDED);
            return true;
        }
        return false;
    }

    public YtemDefOJ getYtemDefByIndex(int index) {
        return ytemDefList.get(index);
    }

    public int getYtemDefIndexByName(String name) {
        for (int i = 0; i < ytemDefList.size(); i++) {
            if (getYtemDefByIndex(i).getYtemDefName().equalsIgnoreCase(name)) {
                //n_5.3.2007
                return i;
            }
        }
        return -1;
    }

    public YtemDefOJ getYtemDefByName(String name) {
        for (int i = 0; i < ytemDefList.size(); i++) {
            if (getYtemDefByIndex(i).getYtemDefName().equalsIgnoreCase(name)) {
                //n_5.3.2007
                return getYtemDefByIndex(i);
            }
        }
        return null;
    }

    /**
     * replace old ytemDef by new one, and set new ytemDef's parent.
     *
     * @return old ytemDef
     */
    public YtemDefOJ setYtemDef(int index, YtemDefOJ newYtemDef) {
        YtemDefOJ old_ytemDef = ytemDefList.get(index);
        ytemDefList.set(index, newYtemDef);
        newYtemDef.setParent(this);
        changed = true;
        return old_ytemDef;
    }

    /**
     * @return index ytemDef
     */
    public int indexOfYtemDef(YtemDefOJ ytemDef) {
        return ytemDefList.indexOf(ytemDef);
    }

    /**
     * @return index ytemDef with this name, or -1 if not found - not case
     * sensitive
     */
    public int indexOfYtemDef(String name) {
        for (int i = 0; i < ytemDefList.size(); i++) {
            if (getYtemDefByIndex(i).getYtemDefName().equalsIgnoreCase(name)) {
                //n_5.3.2007
                return i;
            }
        }
        return -1;
    }

    /**
     * delete n-th ytem definition, 0-based
     */
    public void removeYtemDefByIndex(int index) {
        String name = getYtemDefByIndex(index).getYtemDefName();
        ytemDefList.remove(index);
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(name, YtemDefChangedEventOJ.YTEMDEF_DELETED);
    }

    /**
     * delete ytem definition with this name
     */
    public void removeYtemDefByName(String name) {
        ytemDefList.remove(indexOfYtemDef(name));
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(name, YtemDefChangedEventOJ.YTEMDEF_DELETED);
    }

    /**
     * remove this ytem definition from the list
     */
    public void removeYtemDef(YtemDefOJ ytemDef) {
        ytemDefList.remove(ytemDef);
        changed = true;
        OJ.getEventProcessor().fireYtemDefChangedEvent(ytemDef.getYtemDefName(), YtemDefChangedEventOJ.YTEMDEF_DELETED);
    }

    /**
     * @return ArrayList as Array of YtemDefs
     */
    public YtemDefOJ[] ytemDefsToArray() {
        YtemDefOJ[] result = new YtemDefOJ[ytemDefList.size()];
        System.arraycopy(ytemDefList, 0, result, 0, ytemDefList.size());
        return result;
    }

    /**
     * @return ArrayList as Array of names
     */
    public String[] ytemDefNamesToArray() {
        String[] result = new String[ytemDefList.size()];
        for (int i = 0; i < ytemDefList.size(); i++) {
            result[i] = (ytemDefList.get(i)).getYtemDefName();
        }
        return result;
    }

    /**
     * put n-th ytemDef to a different position in the list, 0-based
     */
    public void exchangeYtemDefPosition(int from, int to) {
        YtemDefOJ tmp = ytemDefList.get(from);
        ytemDefList.set(from, ytemDefList.get(to));
        ytemDefList.set(to, tmp);
        changed = true;
    }

    /**
     * set the "composite objects" flag
     */
    public void setComposite(boolean enabled) {
        this.multicollect = enabled;
        changed = true;
    }

    public boolean isComposite() {
        return multicollect;
    }

    /**
     * @return the "composite objects" flag
     */
    public void set3DYtems(boolean enabled) {
        this.threedytems = enabled;
        changed = true;
    }

    /**
     * @return the "3D" flag
     */
    public boolean is3DYtems() {
        return threedytems;
    }

    /**
     * set the "composite objects" flag
     */
    public void setShowCellNumber(boolean enabled) {
        this.showcellnumber = enabled;
        changed = true;
    }

    /**
     * @return the "Show Object Label" flag
     */
    public boolean getShowCellNumber() {
        return showcellnumber;
    }

    /**
     * Initialize and propagate
     */
    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (ytemDefList == null) {
            ytemDefList = new ArrayList();
        }
        for (int i = 0; i < ytemDefList.size(); i++) {
            (ytemDefList.get(i)).initAfterUnmarshalling(this);
        }
    }

    /**
     * @return status of ytem visibility master switch
     */
    public boolean isYtemVisibilitySwitchEnabled() {
        return ytemVisibilitySwitchEnabled;
    }

    /**
     * set status of ytem visibility master switch
     */
    public void setYtemVisibilitySwitchEnabled(boolean visibilitySwitchEnabled) {
        this.ytemVisibilitySwitchEnabled = visibilitySwitchEnabled;
    }

    /**
     * @return status of object visibility
     */
    public boolean isCellLayerVisible() {
        return cellLayerVisible;
    }

    /**
     * set status of object visibility
     */
    public void setCellLayerVisible(boolean cellLayerVisible) {
        this.cellLayerVisible = cellLayerVisible;
    }
}
