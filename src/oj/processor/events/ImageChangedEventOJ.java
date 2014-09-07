/*
 * ImageChangedEvent.java
 * -- documented
 *
 * After a change, such an object is created and passed to the listener
 *
 */

package oj.processor.events;

public class ImageChangedEventOJ {

    public static final int IMAGE_ADDED = 1;
    public static final int IMAGE_EDITED = 2;
    public static final int IMAGE_DELETED = 3;
    public static final int IMAGES_SWAP = 4;
    public static final int IMAGES_SORT = 5;
    private String firstImageName;
    private String secondImageName;
    private int operation;

    /** Creates a new instance of ImageChangedEvent */
    public ImageChangedEventOJ(String firstImageName, String secondImageName, int operation) {
        this.operation = operation;
        this.firstImageName = firstImageName;
        this.secondImageName = secondImageName;
    }

    public ImageChangedEventOJ(String name, int operation) {
        this.firstImageName = name;
        this.secondImageName = name;
        this.operation = operation;
    }

    public ImageChangedEventOJ(int operation) {
        this.operation = operation;
    }

    public String getName() {
        return firstImageName;
    }

    public String getFirstImageName() {
        return firstImageName;
    }

    public String getSecondImageName() {
        return secondImageName;
    }

    public String getNewImageName() {
        return firstImageName;
    }

    public String getOldImageName() {
        return secondImageName;
    }

    public int getOperation() {
        return operation;
    }
}
