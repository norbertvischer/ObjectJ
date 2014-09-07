/*
 * ActionToolOJ.java
 * fully documented
 *
 */

package oj.gui.tools;

import javax.swing.Icon;

/** interface defining methods each tool must have
 */
public interface ActionToolOJ {

    public String getTooltip();
    public String getName();
    public Icon getIcon();
    
    public int getId();
    public void setId(int id);
    
    public void actionPerformed();
}
