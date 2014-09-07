/* ActionToolAdapterOJ.java
 * fully documented
 */
package oj.gui.tools;

import javax.swing.Icon;

/** abstract class with pre-defined methods that conform to the interface
 */
public abstract class ActionToolAdapterOJ implements ActionToolOJ {

    private int id;

    public String getTooltip() {
        return "";
    }

    public String getName() {
        return "";
    }

    public Icon getIcon() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void actionPerformed() {

    }
}
