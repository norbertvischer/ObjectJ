/*
 * ImagesOJ.java
 * fully documented 8.3.2010
 *
 */
package oj.project;

import ij.ImagePlus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import oj.OJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.ToolStateOJ;

/**
 * Contains a table of all linked images
 */
public class ImagesOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 3040918068273392705L;
    private Hashtable images = new Hashtable();//filename -> ImageOJ
    private ArrayList imagesKeys = new ArrayList();//filenames only

    /**
     * @return change flag, e.g. true if an image has been added
     */
    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < images.size(); i++) {
                if (getImageByIndex(i).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * propagate changed = true to all linked images (why?)
     */
    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < images.size(); i++) {
            getImageByIndex(i).setChanged(changed);
        }
    }

    /**
     * add an image to the list and update hashtable
     */
    public boolean addImage(ImageOJ imgd) {
        images.put(imgd.getFilename(), imgd);
        imagesKeys.add(imgd.getFilename());
        imgd.setParent(this);
        changed = true;
        OJ.getEventProcessor().fireImageChangedEvent(imgd.getName(), ImageChangedEventOJ.IMAGE_ADDED);
        return true;
    }

    /**
     * renaming an image: updating hashtable and arraylist of names
     */
    public void updateImageName(String oldName, String newName) {
        ImageOJ image = getImageByName(oldName);
        if (image != null) {
            images.remove(oldName);//10.4.2009
            images.put(newName, image);
            int index = imagesKeys.indexOf(oldName);
            imagesKeys.add(index, newName);
            imagesKeys.remove(oldName);
        }
    }

    /**
     * sort arraylist of filenames only
     */
    public void sortImages() {
        Collections.sort(imagesKeys);
    }

    /**
     * change sequence of arraylist of image names
     */
    public void swapImages(int firstImageIndex, int secondImageIndex) {
        Collections.swap(imagesKeys, firstImageIndex, firstImageIndex);
    }

    /**
     * @return number of linked images
     */
    public int getImagesCount() {
        return images.size();
    }

    /**
     * @return n-th ImageOJ, 0-based
     */
    public ImageOJ getImageByIndex(int index) {
        if (index < 0 || index >= imagesKeys.size()) {//28.10.2011
            return null;
        }
        return getImageByName((String) imagesKeys.get(index));
    }

    /**
     * @return index of ImageOJ, 0-based
     */
    public int getIndex(ImageOJ img) {
        if (img == null) {
            return -1;
        }
        for (int jj = 0; jj < images.size(); jj++) {
            if (getImageByIndex(jj) == img) {
                return jj;
            }
        }
        return -1;
    }

    /**
     * @return ImageOJ with this name
     */
    public ImageOJ getImageByName(String name) {
        return (ImageOJ) images.get(name);
    }

    /**
     * remove image from hashtable and list of image names
     */
    public void removeImageByName(String name) {

        if (OJ.getData() != null) {
            ToolStateOJ state = OJ.getToolStateProcessor().getToolStateObject();
            if (state != null && (state instanceof CreateCellStateOJ)) {
                ((CreateCellStateOJ) state).closeCell();//3.11.2013        
            }
        }

        ImageOJ img = (ImageOJ) images.get(name);
        if (img != null && img.getImagePlus() != null) {
            img.getImagePlus().close();//3.11.2013
        }
        imagesKeys.remove(name);
        images.remove(name);
        changed = true;
        OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_DELETED);
    }

    /**
     * removes all linked images from list
     */
    public void removeAllImages() {
        removeAllImages(false);
    }

    /**
     * removes unmarked or all images from list
     */
    public void removeAllImages(boolean unmarkedOnly) {
        for (int i = getImagesCount() - 1; i >= 0; i--) {
            ImageOJ img = getImageByIndex(i);
            if ((!unmarkedOnly) || (img.getLastCell() < 0)) {
                removeImageByName(img.getName());
            }
        }
    }

    /**
     * removes an image from the list
     */
    public void removeImage(ImageOJ img) {
        imagesKeys.remove(img.getName());
        removeImageByName(img.getName());
        changed = true;
    }

    private String[] imagesKeyToArray(Hashtable imagesHastable) {
        String[] result = new String[imagesHastable.size()];
        List keys = Collections.list(imagesHastable.keys());
        Collections.sort(keys);
        System.arraycopy(keys.toArray(), 0, result, 0, keys.size());
        return result;
    }

    /**
     * @return index of image, 0-based
     */
    public int getIndexOfImage(String name) {
        return imagesKeys.indexOf(name);
    }

    /**
     * @return true if @imp is linked
     */
    public boolean isLinked(ImagePlus imp) {
        if (imp == null) {
            return false;
        }
        if (imp.getOriginalFileInfo() == null) {//20.6.2014
            return false;
        }
        String dir = imp.getOriginalFileInfo().directory;
        if (dir.equals(OJ.getData().getDirectory())) {
            String name = imp.getTitle();
            ImageOJ img = getImageByName(name);
            if (img != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * hierachically propagates to all individual images
     */
    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        if (images == null) {
            images = new Hashtable();
            imagesKeys = new ArrayList();
        }
        for (int i = 0; i < images.size(); i++) {
            getImageByIndex(i).initAfterUnmarshalling(this);
        }
    }
}
