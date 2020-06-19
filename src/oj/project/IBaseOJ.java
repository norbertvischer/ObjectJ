/*
 * IBaseOJ.java
 */

package oj.project;

/** Base interface for all classes oj the ObjectJ data structure.
 *
 * @author stelian
 */
public interface IBaseOJ {

    public void init();

    public boolean getChanged();

    public IBaseOJ getParent();

    public void setParent(IBaseOJ parent);

    public void setChanged(boolean changed);

    /** called after reading from disk. This call is sent to
    the top of the tree and broadcasted from there*/
    public void initAfterUnmarshalling(IBaseOJ parent);
}