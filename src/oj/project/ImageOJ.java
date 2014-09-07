/*
 * ImageOJ.java
 * fully documented 8.3.2010
 */
package oj.project;

import ij.ImagePlus;
import oj.OJ;
import oj.processor.events.ImageChangedEventOJ;

/**
 * ImageOJ holds information about a linked image, such as width, height,
 * slices, hyperstack structure, scaling and name.
 */
public class ImageOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 556257904296924117L;
    //image info
    private String name;
    private String description = "";
    private transient boolean fileExists = true;
    private int nrSlices = 1;
    private int nrFrames = 1;
    private int nrChannels = 1;
    //image size
    private int width = 0;
    private int height = 0;
    //image calibration
    private double voxelSizeX = 1.0;
    private double voxelSizeY = 1.0;
    private double voxelSizeZ = 1.0;
    private String voxelUnitX = "";
    private String voxelUnitY = "";
    private String voxelUnitZ = "";
    //image stack
    private int frameInterval = 1;//30.7.2013 put this back to int: was this the mistake?
    private double frameIntervalD = 1;//30.7.2013 put this back to int: was this the mistake?
    private String frameRateUnit = "sec";
    private transient int ID = 0;//negative if image is open, zero if closed
    private transient ImagePlus imagePlus = null;

    public ImageOJ() {
        name = "";
        ID = 0;
    }

    public ImageOJ(String name, ImagePlus imp) {//30.6.2012
        if (imp != null) {//5.4.2013
            this.ID = imp.getID();
        }
        this.imagePlus = imp;
        this.name = name;
    }

    public int[] getDimensions() {
        int[] dimensions = {width, height, nrChannels, nrSlices, nrFrames};
        return dimensions;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setImagePlus(ImagePlus imp) {

        imagePlus = imp;
    }

    public ImagePlus getImagePlus() {

        return imagePlus;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        //return filename;
        return name;
    }

    public String getDescription() {
        return "";
    }

    /**
     * @return index of first cell (0-based) in this image, or -1 if unmarked
     */
    public int getFirstCell() {
        return OJ.getData().getCells().getFirstCellOnImage(name);
    }

    /**
     * @return index of last cell (0-based) in this image, or -1 if unmarked
     */
    public int getLastCell() {
        return OJ.getData().getCells().getLastCellOnImage(name);
    }

    /**
     * @return number of cells in this image
     */
    public int getCellCount() {
        if (getFirstCell() >= 0) {
            return getLastCell() - getFirstCell() + 1;
        } else {
            return 0;
        }
    }

    /**
     * @return array of cells in this image
     * not tested yet
     */
    public CellOJ[] getCells() {
        int count = getCellCount();
        CellOJ[] cells = new CellOJ[count];
        int kk = 0;
        for (int cc = getFirstCell(); cc < getFirstCell() + count; cc++) {
            cells[kk++] = OJ.getData().getCells().getCellByIndex(cc);
        }
        return cells;
    }

    /**
     * rename image
     */
    public void setName(String name) {
        if (!this.name.equals(name)) {
            String oldName = this.name;
            this.name = name;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, oldName, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * rename image - obsolete
     */
    public void setFilename(String filename) {
        if (!this.name.equals(filename)) {
            this.name = filename;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * ObjectJ stores the image width and height of a linked image - this is not
     * really used
     */
    public int getWidth() {
        return width;
    }

    /**
     * ObjectJ stores the image width and height of a linked image - this is not
     * really used
     */
    public void setWidth(int width) {
        if (this.width != width) {
            this.width = width;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * ObjectJ stores the image width and height of a linked image - this is not
     * really used
     */
    public int getHeight() {
        return height;
    }

    /**
     * ojj file stores the image width and height of a linked image - this is
     * not really used
     */
    public void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * ojj file stores voxel size
     */
    public double getVoxelSizeX() {
        return voxelSizeX;
    }

    public void setVoxelSizeX(double voxelSizeX) {
        if (this.voxelSizeX != voxelSizeX) {
            this.voxelSizeX = voxelSizeX;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public double getVoxelSizeY() {
        return voxelSizeY;
    }

    public void setVoxelSizeY(double voxelSizeY) {
        if (this.voxelSizeY != voxelSizeY) {
            this.voxelSizeY = voxelSizeY;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public double getVoxelSizeZ() {
        return voxelSizeZ;
    }

    public void setVoxelSizeZ(double voxelSizeZ) {
        if (this.voxelSizeZ != voxelSizeZ) {
            this.voxelSizeZ = voxelSizeZ;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public String getVoxelUnitX() {
        return voxelUnitX;
    }

    public void setVoxelUnitX(String voxelUnitX) {
        if (!this.voxelUnitX.equals(voxelUnitX)) {
            this.voxelUnitX = voxelUnitX;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public String getVoxelUnitY() {
        return voxelUnitY;
    }

    public void setVoxelUnitY(String voxelUnitY) {
        if (!this.voxelUnitY.equals(voxelUnitY)) {
            this.voxelUnitY = voxelUnitY;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public String getVoxelUnitZ() {
        return voxelUnitZ;
    }

    public void setVoxelUnitZ(String voxelUnitZ) {
        if (!this.voxelUnitZ.equals(voxelUnitZ)) {
            this.voxelUnitZ = voxelUnitZ;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * ojj file stores frame interval
     */
    public double getFrameInterval() {
        return frameIntervalD;
    }

    public void setFrameInterval(double frameInterval) {
        if (this.frameIntervalD != frameInterval) {
            this.frameIntervalD = frameInterval;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public String getFrameRateUnit() {
        return frameRateUnit;
    }

    public void setFrameRateUnit(String frameRateUnit) {
        if (!this.frameRateUnit.equals(frameRateUnit)) {
            this.frameRateUnit = frameRateUnit;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    /**
     * fill default values if incomplete
     */
    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);


        if (width < 0) {
            width = 0;
        }

        if (height < 0) {
            height = 0;
        }

        if (nrSlices < 1) {
            nrSlices = 1;
        }

        if (nrFrames < 1) {
            nrFrames = 1;
        }

        if (nrChannels < 1) {
            nrChannels = 1;
        }

        if (voxelSizeX <= 0) {
            voxelSizeX = 1.0;
        }

        if (voxelSizeY <= 0) {
            voxelSizeY = 1.0;
        }

        if (voxelSizeZ <= 0) {
            voxelSizeZ = 1.0;
        }

        if ((voxelUnitX == null) || (voxelUnitX.equals(""))) {
            voxelUnitX = "um";
        }

        if ((voxelUnitY == null) || (voxelUnitY.equals(""))) {
            voxelUnitY = "um";
        }

        if ((voxelUnitZ == null) || (voxelUnitZ.equals(""))) {
            voxelUnitZ = "um";
        }

        if (frameIntervalD < 0) {
            frameIntervalD = 1;
        }

        if ((frameRateUnit == null) || (frameRateUnit.equals(""))) {
            frameRateUnit = "s";
        }

    }

    /**
     * @return true if image is saved in project folder
     */
    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public int getNumberOfSlices() {
        return nrSlices;
    }

    public int getStackSize() {
        return nrSlices * nrFrames * nrChannels;
    }

    public void setNumberOfSlices(int nrSlices) {
        if (this.nrSlices != nrSlices) {
            this.nrSlices = nrSlices;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public int getNumberOfFrames() {
        return nrFrames;
    }

    public void setNumberOfFrames(int nrFrames) {
        if (this.nrFrames != nrFrames) {
            this.nrFrames = nrFrames;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }

    public int getNumberOfChannels() {
        return nrChannels;
    }

    public void setNumberOfChannels(int nrChannels) {
        if (this.nrChannels != nrChannels) {
            this.nrChannels = nrChannels;
            setChanged(true);
            OJ.getEventProcessor().fireImageChangedEvent(name, ImageChangedEventOJ.IMAGE_EDITED);
        }
    }
}
