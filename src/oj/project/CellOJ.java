/* CellOJ.java
 * fully documented 7.3.2010
 *
 *All characteristics of a cell
 *(In the user manual, a "cell" is called an "Object")
 */
package oj.project;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import oj.OJ;

public class CellOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = -6498400814936322322L;
    private String imageName;//owner image
    private int stackIndex;//"home slice", even though in 3D points mey distributed over several slices
    private boolean qualified = true;
    private ArrayList <YtemOJ> ytemList = new ArrayList();
    private int cellID = 0;//negative ID that is not affected by removing cells elsewhere
    public Properties properties = new Properties();//hash table results: column name + value (as text)
    private transient boolean open;//true while editing, i.e. adding more points or ytems
    private transient boolean selected;//true if cell is selected
    private transient int selectedYtemIndex = 0;
    private transient boolean toBeKilled = false;

    public CellOJ() {
        this.stackIndex = 1;
    }

    public CellOJ(String imageName, int imageSlice) {
        this.imageName = imageName;
        this.stackIndex = imageSlice;
    }

    public boolean getToBeKilled() {
        return toBeKilled;
    }

    public void setToBeKilled(boolean flag) {
        toBeKilled = flag;
    }

    /** @return true if one of the ytems has been changed */
    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < ytemList.size(); i++) {
                if (( ytemList.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** sets or resets all ytems' changed flag */
    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < ytemList.size(); i++) {
            ( ytemList.get(i)).setChanged(changed);
        }
    }

    /** @return the n-th clone of a certain ytem type, 0-based */
    public YtemOJ getClone(int index, String ytemDefName) {
        int count = 0;
        for (int i = 0; i < ytemList.size(); i++) {
            if (getYtemByIndex(i).getYtemDef().equals(ytemDefName)) {
                if (count == index) {
                    return getYtemByIndex(i);
                }
                count += 1;
            }
        }
        return null;
    }

    /** @return index of selected ytem, 0-based */
    public int getSelectedYtemIndex() {
        return selectedYtemIndex;
    }

    /** @return selected ytem */
    public YtemOJ getSelectedYtem() {
        return getYtemByIndex(selectedYtemIndex);
    }

    /** @return number of ytems that have this ytem name */
    public int getCloneCount(String ytemDefName) {
        int count = 0;
        for (int i = 0; i < ytemList.size(); i++) {
            if (getYtemByIndex(i).getYtemDef().equals(ytemDefName)) {
                count += 1;
            }
        }
        return count;
    }

    /** @return cell's qualified flag */
    public boolean isQualified() {
        return qualified;
    }

    /** sets cell's qualified flag */
    public void setQualified(boolean qualified) {
        if (this.qualified != qualified) {//5.2.2011
            this.qualified = qualified;
            changed = true;
            for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
                OJ.getData().getResults().getColumns().getColumnByIndex(i).getStatistics().setStatisticsDirty();//12.8.2011
            }
            //OJ.getEventProcessor().fireCellChangedEvent();//removed 25.7.2011
        }
    }

    /** adds an ytem to this cell and adjusts ytem's parent */
    public boolean add(YtemOJ ytem) {
        if (ytemList.add(ytem)) {
            ytem.setParent(this);
            changed = true;
            return true;
        }
        return false;
    }

    /** @return name of owner image */
    public String getImageName() {
        return imageName;
    }

    /** sets the name of the owner image */
    public void setImageName(String imageName) {
        this.imageName = imageName;
        changed = true;
    }

    /** @return cell's owner slice, 1-based? */
    public int getStackIndex() {
        return stackIndex;
    }

    /** sets cell's owner slice, 1-based? */
    public void setStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
        changed = true;
    }

    /** @return cell's total number of ytems */
    public int getYtemsCount() {
        return ytemList.size();
    }

    /** @return cell's total number of points */
    public int getTotalPointsCount() {
        int count = 0;
        for (int ytm = 0; ytm < getYtemsCount(); ytm++) {
            count += getYtemByIndex(ytm).getLocationsCount();
        }
        return count;
    }

    /** @return smallest rectangle containing all points of all ytems */
    public Rectangle getRectangle() {
        Rectangle rr = new Rectangle(0, 0, -1, -1);
        for (int i = 0; i < ytemList.size(); i++) {
            YtemOJ thisYtem =  ytemList.get(i);
            rr.add(thisYtem.getRectangle());
        }
        return rr;
    }

    /** @return n-th ytem, 0-based */
    public YtemOJ getYtemByIndex(int index) {
        if (index >= ytemList.size() || index < 0) {//24.4.2009);
            return null;
        }
        return  ytemList.get(index);
    }

    /** @return cell's owner slice, 1-based? */
    public YtemOJ setYtem(int index, YtemOJ ytem) {
        YtemOJ old_ytem =  ytemList.get(index);
        ytemList.set(index, ytem);
        ytem.setParent(this);
        changed = true;
        return old_ytem;
    }

    /** remove n-th ytem, 0-based */
    public void removeYtemByIndex(int index) {
        if (ytemList.size() > index && index >= 0) {//27.5.2011);
            ytemList.remove(index);
        }
        changed = true;
    }

    /** remove this ytem from cell */
    public void removeYtem(YtemOJ ytm) {
        ytemList.remove(ytm);
        changed = true;
    }

    /** return index of this ytem, 0-based */
    public int indexOfYtem(YtemOJ ytem) {
        return ytemList.indexOf(ytem);
    }

    /** select this cell, and select first ytem (i.e. ytem #0) */
    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedYtemIndex = 0;
        changed = true;
    }

    /** select n-th ytem, 0-based */
    public void selectYtem(int index) {
        selectedYtemIndex = index;
        changed = true;
    }

    /** @return cell's "selected" flag*/
    public boolean isSelected() {
        return selected;
    }

    /** sets the cell to be open, so that more ytems can be appended */
    public void setOpen(boolean open) {
        this.open = open;
        changed = true;
    }

    /** @return cell's owner slice, 1-based? */
    public boolean isOpen() {
        return open;
    }

    /** @return all YtemOJs of this cell as array  */
    public YtemOJ[] toArray() {
        YtemOJ[] result = new YtemOJ[ytemList.size()];
        System.arraycopy(ytemList, 0, result, 0, ytemList.size());
        return result;
    }

    /** Part of hierarchical unmarshalling, here properties are cleaned */
    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (properties == null) {
            properties = new Properties();
        } else {
            Enumeration enumer = properties.propertyNames();
            while (enumer.hasMoreElements()) {
                String prop_name = (String) enumer.nextElement();
                if ((properties.getProperty(prop_name) == null) || (properties.getProperty(prop_name).equals("NaN"))) {
                    properties.remove(prop_name);
                }
            }
        }
        for (int i = 0; i < ytemList.size(); i++) {
            ( ytemList.get(i)).initAfterUnmarshalling(this);
        }
    }

    /** adds or replaces a result */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        changed = true;
    }

    /** true if result column title is found among properties */
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    /** removes a key value pair */
    public void removeProperty(String key) {
        properties.remove(key);
        changed = true;
    }

    /** @return a result property as string */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /** sets the negaitve unique id number */
    public void setID(int cellID) {
        this.cellID = cellID;
    }

    /** gets the negaitve unique id number */
    public int getID() {
        return cellID;
    }

    /** @return index of ytem currently being edited, otherwise -1  (0-based) */
    public int getOpenYtemIndex() {
        for (int i = 0; i < ytemList.size(); i++) {
            if (( ytemList.get(i)).isOpen()) {
                return i;
            }
        }
        return -1;
    }

    /** @return ytem type as integer, e.g. 0 for "Axis", 1 for "Dia" in coli example  */
    public int getOpenYtemDefIndex() {
        for (int i = 0; i < ytemList.size(); i++) {
            if (( ytemList.get(i)).isOpen()) {
                String ytemDef = getYtemByIndex(i).getYtemDef();
                return OJ.getData().getYtemDefs().indexOfYtemDef(ytemDef);
            }
        }
        return -1;
    }

    /** rearranges the sequence of ytems  */
    public void repositionYtem(int fromItem, int toItem) {//0-based
        if (fromItem >= ytemList.size() || fromItem < 0 || toItem >= ytemList.size() || toItem < 0) {
            return;
        }
        YtemOJ movingYtem = getYtemByIndex(fromItem);
        ytemList.remove(fromItem);
        ytemList.add(toItem, movingYtem);
        changed = true;

    }

    /** @return ytem currently being edited, otherwise null */
    public YtemOJ getOpenYtem() {
        for (int i = 0; i < ytemList.size(); i++) {
            if (( ytemList.get(i)).isOpen()) {
                return  ytemList.get(i);
            }
        }
        return null;
    }
}
