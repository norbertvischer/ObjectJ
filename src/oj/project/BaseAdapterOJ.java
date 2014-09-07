/*
 * BaseAdapterOJ.java
 * fully documented on 14.3.2010
 *
 * To be extended by all classes in "projects" folder,
 * to define parent, init, getChanged
 */
package oj.project;

import java.io.Serializable;

public abstract class BaseAdapterOJ implements IBaseOJ, Serializable {

    private static final long serialVersionUID = 8212858594729858813L;
    protected transient boolean changed = false;
    protected transient IBaseOJ parent;

    public BaseAdapterOJ() {
    }

    public BaseAdapterOJ(IBaseOJ parent) {
        this.parent = parent;
    }

    public void init() {
    }

    public boolean getChanged() {
        return changed;
    }

    public IBaseOJ getParent() {
        return parent;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setParent(IBaseOJ parent) {
        this.parent = parent;
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        this.parent = parent;
        this.changed = false;
    }
}
