package oj.project;

import ij.IJ;
import java.io.Serializable;
import java.util.Date;
//import oj.gui.settings.PlotManagerOJ;
import oj.macros.MacroSetOJ;

/**
 * The collection of all information (i.e. what is saved in the .ojj file.
 * Mainly housekeeping, getting and setting Cells, Images, Macros, Results,
 * YtemDefs, as well as reading and writing the entire data stream.
 */
public class DataOJ implements Cloneable, IBaseOJ, Serializable {

    public static final transient String xmlComment = "This text should not be visible. Close file and open it via menu ObjectJ:Open Project";
    private static final long serialVersionUID = -1078220105490684821L;
    public static String VERSION_0_9 = "ObjectJ v0.9";
    public static String VERSION_1_0 = "ObjectJ v1.0";
    public static String VERSION_1_1 = "ObjectJ v1.1";
    public static String VERSION_1_2 = "ObjectJ v1.2";
    private String name;
    private String version = VERSION_1_2;
    private Date updated;
    private String filename;
    public String description = xmlComment;
    public CellsOJ cells = new CellsOJ();
    public ImagesOJ images = new ImagesOJ();
    public transient MacroSetOJ macroSet = new MacroSetOJ();
    public ResultsOJ results = new ResultsOJ();
    public YtemDefsOJ ytemDefs = new YtemDefsOJ();
    private int labelFontSize;//3.10.2020
    private int markerRadius;
    private transient String directory = "";
    private transient boolean changed = false;
    private transient String linkedMacroText = null;
    private transient String linkedPlotText = null;
    //public static int labelFontSize = 14;

    public DataOJ(String name, String description) {
	this.setName(name);
	this.description = description;
	init();
    }

    public DataOJ(String name) {
	this.setName(name);
	init();
    }

    public DataOJ() {
	init();
    }

    public void setLabelFontSize(int size) {
	labelFontSize = size;
    }

    public int getLabelFontSize() {
	if (labelFontSize < 13) {
	    labelFontSize = 13;
	}
	return labelFontSize;
    }

    public void setLabelRad(int rad) {
	markerRadius = rad;
    }

    public int getMarkerRad() {
	if (markerRadius < 2) {
	    markerRadius = 2;
	}
	return markerRadius;
    }

    public String getLinkedMacroText() {//18.3.2010
	return linkedMacroText;
    }

    public void setLinkedMacroText(String txt) {
	linkedMacroText = txt;
    }

    public String getLinkedPlotText() {
	return linkedPlotText;
    }

    public void setLinkedPlotText(String txt) {
	linkedPlotText = txt;
    }

    public void init() {

	cells.setParent(this);
	images.setParent(this);
	//macros.setParent(this);
	results.setParent(this);
	ytemDefs.setParent(this);
    }

    public boolean getChanged() {
	if (changed) {
	    return true;
	} else if (cells.getChanged()) {
	    return true;
	} else if (images.getChanged()) {
	    return true;
//        } else if (macroSet!=null && macroSet.getChanged()) {//19.10.2010
//            return true;
	} else if (results.getChanged()) {
	    return true;
	} else if (ytemDefs.getChanged()) {
	    return true;
	} else {
	    return false;
	}
    }

    public void setChanged(boolean isChanged) {
	if (isChanged) {
	    this.changed = isChanged;//for breakpoint
	}
	this.changed = isChanged;
	cells.setChanged(isChanged);
	images.setChanged(isChanged);
	//macroSet.setChanged(isChanged);
	results.setChanged(isChanged);
	ytemDefs.setChanged(isChanged);
    }

    public void updateChangeDate() {
	updated = new Date();
    }

    public Date getUpdated() {
	updateChangeDate();
	return updated;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public String getVersion() {
	return version;
    }

    public YtemDefsOJ getYtemDefs() {
	return ytemDefs;
    }

    public void setYtemDefs(YtemDefsOJ ytemDefs) {
	this.ytemDefs = ytemDefs;
	changed = true;
    }

    public MacroSetOJ getMacroSet() {
	return macroSet;
    }

    public void setResults(ResultsOJ results) {
	this.results = results;
	setChanged(true);
    }

    public ResultsOJ getResults() {
	return results;
    }

    public void setMacroSet(MacroSetOJ macroset) {
	this.macroSet = macroset;
	setChanged(true);
    }

    public ImagesOJ getImages() {
	return images;
    }

    public void setImages(ImagesOJ images) {
	this.images = images;
	setChanged(true);
    }

    public CellsOJ getCells() {
	return cells;
    }

    public void setCells(CellsOJ cells) {
	this.cells = cells;
	setChanged(true);
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getDirectory() {
	return directory;
    }

    public void setDescription(String description) {
	this.description = description;
	changed = true;
    }

    public void setDirectory(String directory) {
	this.directory = directory;
	changed = true;
    }

    public void setName(String name) {
	this.name = name;
	changed = true;
    }

    /**
     * ?????
     *
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }

    public String toString() {
	return "File: ImageCount=" + getImages().getImagesCount() + ", CellsCount=" + getCells().getCellsCount();
    }

    public String getFilename() {
	return filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }

    public void initAfterUnmarshalling() {
	changed = false;
	version = VERSION_1_2;
	if (cells == null) {
	    cells = new CellsOJ();
	}
	if (images == null) {
	    images = new ImagesOJ();
	}

	if (results == null) {
	    results = new ResultsOJ();
	}
	if (ytemDefs == null) {
	    ytemDefs = new YtemDefsOJ();
	}
	cells.initAfterUnmarshalling(this);
	images.initAfterUnmarshalling(this);
	//macros.initAfterUnmarshalling(this);
	results.initAfterUnmarshalling(this);
	ytemDefs.initAfterUnmarshalling(this);
    }

    public IBaseOJ getParent() {
	return null;
    }

    public void setParent(IBaseOJ parent) {
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
    }
}
