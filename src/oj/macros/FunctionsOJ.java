/**
 * FunctionsOJ.java -- documented
 *
 * Contains oj functions that can be called from a macro. They only accept and
 * return Strings.
 */
package oj.macros;

import ij.WindowManager;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import static ij.macro.MacroExtension.ARG_NUMBER;
import static ij.macro.MacroExtension.ARG_OUTPUT;
import static ij.macro.MacroExtension.ARG_STRING;
import oj.OJ;
import oj.gui.results.ProjectResultsOJ;
import oj.plugin.SimpleCommandsOJ;
import oj.processor.EventProcessorOJ;
import oj.project.ImagesOJ;
import oj.util.ImageJAccessOJ;
import oj.util.WildcardMatchOJ;

import java.awt.*;
import ij.*;
import ij.gui.GenericDialog;
import ij.macro.Interpreter;
import ij.util.Tools;
import java.util.Arrays;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.ImageChangedEventOJ;

/**
 *
 * u
 */
public class FunctionsOJ implements MacroExtension {

	int one = 1;

	public String ojAdvance() {
		OJ.getMacroProcessor().advance();
		return null;
	}

	public String ojCloseItem() {
		OJ.getMacroProcessor().closeYtem();
		return null;
	}

	public String ojSetTarget(String s) {
		OJ.getMacroProcessor().setTarget(s);
		return null;
	}

	public String ojCloseObject() {
		OJ.getMacroProcessor().closeObject();
		return null;
	}

	public String ojCloseImages(String conditions) {//not used
		//OJ.getMacroProcessor().closeImages(conditions);
		return null;
	}

	public String ojColumnNumber(String columnName) {
		int columnNumber = OJ.getMacroProcessor().getColumnNumber(columnName);
		return Integer.toString(columnNumber);
	}

	public String ojActiveItemName() {
		return OJ.getMacroProcessor().activeYtemName();
	}

	public String ojCloseResults() {
		OJ.getMacroProcessor().closeResults();
		return null;
	}

	public String ojClose(String pattern) {
		OJ.getMacroProcessor().close(pattern);
		return null;
	}

	public String ojDeleteAllObjects() {
		OJ.getMacroProcessor().deleteAllObjects();
		return null;
	}

	public String ojDeleteColumn(String columnName) {
		OJ.getMacroProcessor().deleteColumn(columnName);
		return null;
	}

	public String ojDeleteItem(String itemType, String itemIndex) {
		OJ.getMacroProcessor().deleteYtem(itemType, MacroProcessorOJ.parseInt(itemIndex));
		return null;
	}

	public String ojDeleteItems(String itemType) {
		OJ.getMacroProcessor().deleteYtems(itemType);
		return null;
	}

	public String ojDeleteObject(String objectIndex) {
		OJ.getMacroProcessor().deleteCell(MacroProcessorOJ.parseInt(objectIndex));
		return null;
	}

	public String ojDisposeImage(String imageName) {
		OJ.getMacroProcessor().disposeImage(imageName);
		return null;
	}

	public String ojFindClosestPoint(String sx, String sy, String maxDistance, String ytemName) {
		int x = MacroProcessorOJ.parseInt(sx);
		int y = MacroProcessorOJ.parseInt(sy);
		int maxDist = MacroProcessorOJ.parseInt(maxDistance);
		int pointNo = OJ.getMacroProcessor().findClosestPoint(x, y, maxDist, ytemName);
		return "" + pointNo;
	}

	public String ojFlatten() {
		OJ.getMacroProcessor().flatten();
		return null;
	}

	public String ojFirstObject(String imageIndex) {
		int objectIndex = OJ.getMacroProcessor().getFirstCell(MacroProcessorOJ.parseInt(imageIndex));
		return Integer.toString(objectIndex);
	}

	/**
	 *
	 * @return length or perimeter of selected item (0 it it is a point)
	 */
	public String ojGetItemLength() {
		double val = OJ.getMacroProcessor().getYtemLength();

		return Double.toString(val);
	}

	public String ojGetItemColors() {
		return OJ.getMacroProcessor().getYtemColors();
	}

	public String ojGetImageValue(String n, String key) {
		double val = OJ.getMacroProcessor().getImageValue(MacroProcessorOJ.parseInt(n), key);
		return Double.toString(val);

	}

	public String ojLastObject(String imageIndex) {
		int objectIndex = OJ.getMacroProcessor().getLastCell(MacroProcessorOJ.parseInt(imageIndex));
		return Integer.toString(objectIndex);
	}

	public String ojGetOpenObject() {
		int index = OJ.getMacroProcessor().getOpenCell();
		return Integer.toString(index);
	}

	public String ojGetItemName() {
		String name = OJ.getMacroProcessor().getYtemName();
		return name;
	}

	public String ojGetItemNames() {
		String names = OJ.getMacroProcessor().getYtemNames();
		return names;
	}

	public String ojGetItemTypes() {
		String types = OJ.getMacroProcessor().getYtemTypes();
		return types;
	}

	public String ojGetProjectName() {
		String dir = OJ.getData().getName();
		return dir;
	}

	public String ojGetImageName(String imageIndex) {
		int index = MacroProcessorOJ.parseInt(imageIndex) - one;
		ImagesOJ images = OJ.getData().getImages();
		if (index < 0 || index >= images.getImagesCount()) {
			return ("");
		}
		String name = OJ.getData().getImages().getImageByIndex(index).getName();
		return name;
	}

	public String ojGetProjectPath() {
		return OJ.getData().getDirectory();
	}

	public String ojGetVirtual() {
		if (OJ.getData().getImages().getVirtualFlag()) {
			return "1";
		} else {
			return "0";
		}
	}

	public String ojGetStatistics(String columnName, String statisticsOperation) {
		double value = OJ.getMacroProcessor().getStatistics(columnName, statisticsOperation);
		return Double.toString(value);
	}

	public String ojGetValue(String columnName, String rowIndex) {
		return ojResult(columnName, rowIndex);
	}

	public String ojLineToPolygon(String lineWidth, String rounding) {
		double lw = MacroProcessorOJ.parseDouble(lineWidth);
		boolean roundFlag = MacroProcessorOJ.parseBoolean(rounding);
		OJ.getMacroProcessor().lineToPolygon(lw, roundFlag);
		return null;
	}

	public String ojGetVoxelSize(String imageIndex, String xyz) {
		int index = MacroProcessorOJ.parseInt(imageIndex);
		return OJ.getMacroProcessor().getVoxelSize(index, xyz);
	}

	public String ojHideResults() {
//	 if (ProjectResultsOJ.getInstance() == null) {//11.11.2018  16.11.2018
//            new ProjectResultsOJ();
//            ProjectResultsOJ.getInstance().setVisible(true);
//            ProjectResultsOJ.getInstance().setState(Frame.NORMAL);
//            ProjectResultsOJ.getInstance().toFront();
//	 }
		if (ProjectResultsOJ.getInstance() != null) {
			ProjectResultsOJ.close();
		} //24.2.2009
		return null;
	}

	public String ojIndexToRow(String index) {//obsolete
		int row = OJ.getMacroProcessor().indexToRank(MacroProcessorOJ.parseInt(index));
		return Integer.toString(row);
	}

	public String ojIndexToRank(String index) {
		int rank = OJ.getMacroProcessor().indexToRank(MacroProcessorOJ.parseInt(index));
		return Integer.toString(rank);
	}

	public String ojOrderObjectsInZ(String imageIndex) {
		OJ.getMacroProcessor().orderObjectsInZ(MacroProcessorOJ.parseInt(imageIndex));
		return null;
	}

//Example:
//ojRenumberObjects("1 3 8", "8 1 3")
//Object #1 will be labeled #8, #3 becomes #1 etc 

	public String ojRenumberObjects(String src, String dest) {//1-based
		String[] srcA = Tools.split(src);
		String[] destA = Tools.split(dest);
		int len = srcA.length;
		if (len != destA.length) {
			ImageJAccessOJ.InterpreterAccess.interpError("Not same length");
			return null;
		}
		int[] srcI = new int[len];
		int[] destI = new int[len];
		int[] srcTest = new int[len];//for testing same numbers
		int[] destTest = new int[len];
		for (int jj = 0; jj < len; jj++) {
			srcI[jj] = MacroProcessorOJ.parseInt(srcA[jj]) - 1;//to zero-based
			destI[jj] = MacroProcessorOJ.parseInt(destA[jj]) - 1;
			srcTest [jj]= srcI[jj];
			destTest[jj] = destI[jj];
		}
		Arrays.sort(srcTest);
		Arrays.sort(destTest);
		for (int jj = 0; jj < len; jj++) {
			if(srcTest [jj] != destTest[jj]){
				ImageJAccessOJ.InterpreterAccess.interpError("Both arrays must contain same numbers");
				return null;
			}
		}
		OJ.getMacroProcessor().renumberObjects(srcI, destI);
		OJ.getEventProcessor().fireCellChangedEvent(1, CellChangedEventOJ.CELL_SELECT_EVENT);
		return null;
	}

	public String ojGetTarget() {

		return OJ.getMacroProcessor().getTargetAsString();
	}

	public String ojResult(String columnName, String rowIndex) {
		return OJ.getMacroProcessor().getResult(columnName, MacroProcessorOJ.parseInt(rowIndex));
	}

	public String ojResultString(String columnName, String rowIndex) {
		return OJ.getMacroProcessor().getResult(columnName, MacroProcessorOJ.parseInt(rowIndex));
	}

	public String ojRecalculate() {
		OJ.getMacroProcessor().recalculate();
		return null;
	}

	public String ojNImages() {
		int count = OJ.getMacroProcessor().getImageCount();
		return Integer.toString(count);
	}

	public String ojImageLink() {
		int link = OJ.getMacroProcessor().getImageLink();
		return Integer.toString(link);
	}

	public String ojInitColumn(String columnName) {
		OJ.getMacroProcessor().initColumn(columnName, columnName.startsWith("_"), false);
		return null;
	}

	public String ojInitTextColumn(String columnName) {
		OJ.getMacroProcessor().initColumn(columnName, columnName.startsWith("_"), true);
		return null;
	}

	public String ojItemToRoi() {
		OJ.getMacroProcessor().ytemToRoi();
		return null;
	}

	public String ojLastRow(String columnName) {
		int ret = OJ.getMacroProcessor().lastRow(columnName);
		return Integer.toString(ret);
	}

	public String ojLinkImage() {
		OJ.getMacroProcessor().linkImage();
		return null;
	}

	public String ojMakeBoundingRoi(String param) {
		MacroProcessorOJ.makeBoundingRoi(param);
		return null;
	}

	public String ojMovePoint(String pointIndex, String xPos, String yPos, String zPos) {
		OJ.getMacroProcessor().movePoint(MacroProcessorOJ.parseInt(pointIndex), MacroProcessorOJ.parseDouble(xPos), MacroProcessorOJ.parseDouble(yPos), MacroProcessorOJ.parseDouble(zPos));
		return null;
	}

	public String ojNewestObject() {
		int value = OJ.getMacroProcessor().newestCell();
		return Integer.toString(value);
	}

	public String ojNItems(String itemName) {
		int value = OJ.getMacroProcessor().getNItems(itemName);
		return Integer.toString(value);
	}

	public String ojNObjects() {
		int count = OJ.getMacroProcessor().getNCells();
		return Integer.toString(count);
	}

	public String ojOwnerIndex(String objectIndex) {
		int imageIndex = OJ.getMacroProcessor().ownerIndex(MacroProcessorOJ.parseInt(objectIndex));
		return Integer.toString(imageIndex);
	}

	public String ojOwnerName(String objectIndex) {
		return OJ.getMacroProcessor().ownerName(MacroProcessorOJ.parseInt(objectIndex));
	}

	public String ojNPoints() {
		int value = OJ.getMacroProcessor().getNPoints();
		return Integer.toString(value);
	}

	public String ojNColumns() {
		int value = OJ.getMacroProcessor().getNColumns();
		return Integer.toString(value);
	}

	public String ojGetColumnTitle(String columnIndex) {
		String title = OJ.getMacroProcessor().getColumnTitle(MacroProcessorOJ.parseInt(columnIndex));
		return title;
	}

	public String ojMatches(String str, String pattern) {
		WildcardMatchOJ wm = new WildcardMatchOJ();
		wm.setCaseSensitive(false);
		if (wm.match(str, pattern)) {
			return ("1");
		} else {
			return "0";
		}
	}

	public String ojObjectID(String objectIndex) {//obsolete
		int value = OJ.getMacroProcessor().cellID(MacroProcessorOJ.parseInt(objectIndex));
		return Integer.toString(value);
	}

	public String ojIdToIndex(String objectId) {
		int value = OJ.getMacroProcessor().idToIndex(MacroProcessorOJ.parseInt(objectId));
		return Integer.toString(value);
	}

	public String ojIndexToId(String objectIndex) {
		int value = OJ.getMacroProcessor().cellID(MacroProcessorOJ.parseInt(objectIndex));
		return Integer.toString(value);
	}

	public String ojOpenObject(String objectIndex) {
		OJ.getMacroProcessor().openCell(MacroProcessorOJ.parseInt(objectIndex));
		return null;
	}

	public String ojPutOperand(String itemType, String clone, String point) {
		if (ProjectResultsOJ.getInstance() != null) {
			ProjectResultsOJ.close();
		}
		OJ.getMacroProcessor().putOperand(itemType, MacroProcessorOJ.parseInt(clone), MacroProcessorOJ.parseInt(point));
		return null;
	}

	public String ojPutAlgorithm(String algorithm) {
		if (ProjectResultsOJ.getInstance() != null) {
			ProjectResultsOJ.close();
		}

		OJ.getMacroProcessor().putAlgorithm(algorithm);
		return null;
	}

	public String ojQualify(String objectIndex, String flag) {
		OJ.getMacroProcessor().qualifyCell(MacroProcessorOJ.parseInt(objectIndex), MacroProcessorOJ.parseInt(flag));
		return null;
	}

	public String ojQualified(String objectIndex) {
		boolean qualified = OJ.getMacroProcessor().qualified(MacroProcessorOJ.parseInt(objectIndex));
		return qualified ? "1" : "0";
	}

	public String ojRowToIndex(String row) {//obsolete
		int index = OJ.getMacroProcessor().rankToIndex(MacroProcessorOJ.parseInt(row));
		return Integer.toString(index);
	}

	public String ojRankToIndex(String rank) {
		int index = OJ.getMacroProcessor().rankToIndex(MacroProcessorOJ.parseInt(rank));
		return Integer.toString(index);
	}

	public String ojRenameImage(String oldName, String newName) {
		String error = SimpleCommandsOJ.renameImageAndFile(oldName, newName);
		if (!error.equals("")) {
			ImageJAccessOJ.InterpreterAccess.interpError(error);
		}
		return "";
	}

	public String ojRequires(String neededVersion) {
		boolean upToDate = (OJ.releaseVersion.compareTo(neededVersion)) >= 0;
		if (!upToDate) {
			Interpreter instance = Interpreter.getInstance();
			if (instance != null) {
				instance.abortMacro();
			}
			GenericDialog gd = new GenericDialog("ObjectJ is not up-to-date");
			gd.addMessage("Required ObjectJ version: " + neededVersion);
			gd.addMessage("(You are running ObjectJ  " + OJ.releaseVersion + ")");
			gd.addMessage("Click 'Help' to browse to newest objectj_.jar", IJ.font10, Color.gray);
			gd.addHelp(OJ.URLcurrent);
			gd.showDialog();
		}
		return "";
	}

	public String ojRepositionItem(String fromIndex, String toIndex) {
		OJ.getMacroProcessor().repositionYtem(MacroProcessorOJ.parseInt(fromIndex), MacroProcessorOJ.parseInt(toIndex));
		return null;
	}

	public String ojRoiToItem() {
		OJ.getMacroProcessor().roiToYtem();
		return null;
	}

	public String ojSaveProject() {
		OJ.getMacroProcessor().saveProject();
		return null;
	}

	public String ojSelectItem(String itemName, String itemIndex) {
		double qq = MacroProcessorOJ.parseDouble2(itemName);
		if (Double.isNaN(qq)) {
			OJ.getMacroProcessor().selectYtem(itemName, MacroProcessorOJ.parseInt(itemIndex));
		} else {
			int jj = (int) Math.round(qq);
			OJ.getMacroProcessor().selectYtem(jj, MacroProcessorOJ.parseInt(itemIndex));

		}
		return null;
	}

	public String ojSelectedObject() {
		int ret = OJ.getMacroProcessor().selectedCell();
		return Integer.toString(ret);

	}

	public String ojSelectObject(String objectIndex) {
		OJ.getMacroProcessor().selectCell(MacroProcessorOJ.parseInt(objectIndex));
		return null;
	}

	public String ojSelectedItemName() {
		return OJ.getMacroProcessor().selectedYtemName();

	}

	public String ojSet3D(String d3) {
		OJ.getMacroProcessor().set3D(MacroProcessorOJ.parseBoolean(d3));
		return null;
	}

	public String ojSetColumnProperty(String colTitle, String property, String value) {
		OJ.getMacroProcessor().setColumnProperty(colTitle, property, value);//24.1.2010
		return null;
	}

	public String ojSetComposite(String mode) {
		int jj = MacroProcessorOJ.parseInt(mode);
		boolean a = jj == 1;
		boolean b = jj == 0;
		if (!a && !b) {
			ImageJAccessOJ.InterpreterAccess.interpError("true or false expected");
			return null;
		}
		OJ.getMacroProcessor().setComposite(a);
		return null;
	}

	public String ojSetItemVisible(String itemtype, String visible) {
		OJ.getMacroProcessor().setYtemDefVisible(itemtype, MacroProcessorOJ.parseBoolean(visible));
		return null;
	}

	public String ojSetMarker(String xpos, String ypos) {
		OJ.getMacroProcessor().setMarker(MacroProcessorOJ.parseDouble(xpos), MacroProcessorOJ.parseDouble(ypos));
		return null;
	}

	public String ojSetMarker5D(String x, String y, String z, String channel, String frame) {
		ImageJAccessOJ.InterpreterAccess.interpError("Not supported");
		//OJ.getMacroProcessor().setMarker5D(MacroProcessorOJ.parseDouble(x), MacroProcessorOJ.parseDouble(y), MacroProcessorOJ.parseDouble(z), MacroProcessorOJ.parseInt(channel), MacroProcessorOJ.parseInt(frame));
		return null;
	}

	public String ojSetPlotProperties(String title, String properties) {//17.9.2011
		OJ.getMacroProcessor().setPlotProperties(title, properties);
		return null;
	}

	//not used
	public String ojSetStatisticValue(String columnName, String statisticName, String value) {
		OJ.getMacroProcessor().setStatisticValue(columnName, statisticName, MacroProcessorOJ.parseDouble(value));
		return null;
	}

	public String ojSetTool(String tool) {
		OJ.getMacroProcessor().setTool(tool);
		return null;
	}

	public String ojSetResult(String column, String index, String value) {
		OJ.getMacroProcessor().setResult(column, MacroProcessorOJ.parseInt(index), value);
		return null;
	}

	public String ojSetUnchanged() {
		OJ.getData().setChanged(false);
		return null;
	}

	public String ojSetValue(String column, String index, String value) {
		ojSetResult(column, index, value);
		return null;
	}

	public String ojSetVirtualFlag(String virtualFlag) {
		boolean b = MacroProcessorOJ.parseBoolean(virtualFlag);
		OJ.getData().getImages().setVirtualFlag(b);
		OJ.getEventProcessor().fireImageChangedEvent(ImageChangedEventOJ.VIRTUAL_FLAG_CHANGED);
		return null;
	}

	public String ojShowImage(String imageIndex) {
		OJ.getMacroProcessor().showImage(MacroProcessorOJ.parseInt(imageIndex));
		return null;
	}

	public String ojShowObject(String objectIndex) {
		OJ.getMacroProcessor().showObject(MacroProcessorOJ.parseInt(objectIndex));
		return null;
	}

	public String ojShowProject() {
		OJ.getMacroProcessor().showProject();
		return null;
	}

	public String ojShowResults() {
		OJ.getMacroProcessor().showResults();
		return null;
	}

	public String ojShowTools() {
		OJ.getMacroProcessor().showTools();
		return null;
	}

	public String ojSwapSlices(String sliceA, String sliceB) {
		OJ.getMacroProcessor().swapSlices(MacroProcessorOJ.parseInt(sliceA), MacroProcessorOJ.parseInt(sliceB));
		return null;
	}

	public String ojSwitchToItem(String itemName) {
		double qq = MacroProcessorOJ.parseDouble2(itemName);
		if (Double.isNaN(qq)) {
			OJ.getMacroProcessor().switchToItem(itemName);
		} else {
			OJ.getMacroProcessor().switchToItem((int) Math.round(qq));
		}
		return null;
	}

	public String ojUpdateMarkers(String flagString) {
		int flag = MacroProcessorOJ.parseInt(flagString);
		if (flag != 0 && flag != 1) {
			ImageJAccessOJ.InterpreterAccess.interpError("true or false expected");
			return null;
		}
		EventProcessorOJ.updateMarkers = flag == 1;
		return null;
	}

	public String ojTmpFunction(String arg) {

		int[] wList = WindowManager.getIDList();
		String s = "";
		for (int jj = 0; jj < wList.length; jj++) {
			s += wList[jj] + "  ";
		}
		return s;
	}

	public String ojXPos(String index) {
		double value = OJ.getMacroProcessor().getXPos(MacroProcessorOJ.parseInt(index));
		return Double.toString(value);
	}

	public String ojYPos(String index) {
		double value = OJ.getMacroProcessor().getYPos(MacroProcessorOJ.parseInt(index));
		return Double.toString(value);
	}

	public String ojZPos(String index) {
		double value = OJ.getMacroProcessor().getZPos(MacroProcessorOJ.parseInt(index));
		return Double.toString(value);
	}

	public String arrayToString2(String[] a, String separator) {
		StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}

	//*** vertex functions
	public String ojvPushItem() {
		OJ.getMacroProcessor().pushYtem();
		return null;
	}

	public String ojvPushPoint(String index) {
		OJ.getMacroProcessor().pushPoint(MacroProcessorOJ.parseInt(index));
		return null;
	}

	public String ojvPushRoi() {
		OJ.getMacroProcessor().ojvPushRoi();
		return null;
	}

	public String ojvUndoPush() {
		OJ.getMacroProcessor().ojvUndoPush();
		return null;
	}

	String floatArrayToString(double[] fa) {
		String ss = "";
		for (int jj = 0; jj < fa.length; jj++) {
			if (jj > 0) {
				ss = ss + "\n";
			}
			ss = ss + Double.toString(fa[jj]);
		}
		return ss;
	}

	public String ojXYZPos(String n) {
		double[] val = OJ.getMacroProcessor().xYZPos(MacroProcessorOJ.parseInt(n));
		return floatArrayToString(val);
	}

	public String ojvInitStack(String dim) {
		OJ.getMacroProcessor().initVertexStack(dim);
		return null;
	}

	public String ojvGetResult(String str) {
		double val = OJ.getMacroProcessor().ojvGetResult(str);
		return Double.toString(val);

	}

	public String ojvGetStackSize() {
		int val = OJ.getMacroProcessor().ojvGetStackSize();
		return Integer.toString(val);

	}

	public String ojvCalculate(String str) {
		double val = OJ.getMacroProcessor().ojvGetResult(str);
		return Double.toString(val);

	}

	public String ojvGetVertexX(String index) {
		double x = OJ.getMacroProcessor().getVertexX(MacroProcessorOJ.parseInt(index));
		return Double.toString(x);
	}

	public String ojvGetVertexY(String index) {
		double y = OJ.getMacroProcessor().getVertexY(MacroProcessorOJ.parseInt(index));
		return Double.toString(y);
	}

	public String ojvGetVertexZ(String index) {
		double z = OJ.getMacroProcessor().getVertexZ(MacroProcessorOJ.parseInt(index));
		return Double.toString(z);
	}

	public String ojZoom(String factor, String xhook, String yhook) {
		OJ.getMacroProcessor().zoom(MacroProcessorOJ.parseDouble(factor), MacroProcessorOJ.parseInt(xhook), MacroProcessorOJ.parseInt(yhook));
		return null;
	}

	public String ojSetLocation(String xpos, String ypos, String width, String height) {
		OJ.getMacroProcessor().setLocation(MacroProcessorOJ.parseInt(xpos), MacroProcessorOJ.parseInt(ypos), MacroProcessorOJ.parseInt(width), MacroProcessorOJ.parseInt(height));
		return null;
	}

	public String ojExtendVisibilityDepth(String visRangeLow, String visRangeHigh) {
		OJ.getMacroProcessor().extendVisibilityDepth(MacroProcessorOJ.parseInt(visRangeLow), MacroProcessorOJ.parseInt(visRangeHigh));
		return null;
	}

	double[] stringToFloatArray(String ss) {
		String[] sss = ij.util.Tools.split(ss, "\n");
		double[] da = new double[sss.length];
		for (int jj = 0; jj < da.length; jj++) {
			try {
				da[jj] = MacroProcessorOJ.parseDouble(sss[jj]);
			} catch (Exception e) {
				da[jj] = Double.NaN;
			}
		}
		return da;
	}

	double[] stringToFloatArray2(String ss) {
		ss = ss.replaceAll("=", " = ");
		String[] sss = ij.util.Tools.split(ss, " ,");
		int len = sss.length;
		if ((len % 3 != 0) || (len == 0)) {
			ImageJAccessOJ.InterpreterAccess.interpError("Error ");
			return null;
		}
		int nParams = len / 3;
		double[] vertices = new double[3];
		for (int jj = 0; jj < nParams; jj++) {
			if (sss[jj * 3].equalsIgnoreCase("x") && sss[jj * 3 + 1].equals("=")) {
				try {
					vertices[0] = MacroProcessorOJ.parseDouble(sss[jj * 3 + 2]);
				} catch (Exception e) {
					vertices[0] = Double.NaN;
				}
			}
			if (sss[jj * 3].equalsIgnoreCase("y") && sss[jj * 3 + 1].equals("=")) {
				try {
					vertices[1] = MacroProcessorOJ.parseDouble(sss[jj * 3 + 2]);
				} catch (Exception e) {
					vertices[1] = Double.NaN;
				}
			}
			if (sss[jj * 3].equalsIgnoreCase("z") && sss[jj * 3 + 1].equals("=")) {
				try {
					vertices[2] = MacroProcessorOJ.parseDouble(sss[jj * 3 + 2]);
				} catch (Exception e) {
					vertices[2] = Double.NaN;
				}
			}

		}
		return vertices;
	}

	public String ojvPushVertex(String vertex) {
		OJ.getMacroProcessor().pushVertex(stringToFloatArray2(vertex));
		return null;
	}

	public String ojSetItemInfo(String arg) {//21.1. 2019
		OJ.getMacroProcessor().setItemInfo(arg);
		return "";
	}

	public String ojGetItemInfo() {//21.1. 2019
		String s = OJ.getMacroProcessor().getItemInfo();
		return s;
	}

	public String ojSetObjectInfo(String arg) {//6.2.2019
		OJ.getMacroProcessor().setObjectInfo(arg);
		return "";
	}

	public String ojGetObjectInfo() {//6.2.2019
		String s = OJ.getMacroProcessor().getObjectInfo();
		return s;
	}

	public String ojTest(String method, String str) {//5.1. 2018
		return ("abc");
	}

	public String ojTestNotUsed(String arg) {//5.1. 2018
		return ("abc");
	}

	private ExtensionDescriptor[] extensions = {//test, 13.5.2013
		ExtensionDescriptor.newDescriptor("getColumnData", this, ARG_STRING, ARG_OUTPUT + ARG_ARRAY),
		ExtensionDescriptor.newDescriptor("getItemPoints", this, ARG_OUTPUT + ARG_NUMBER, ARG_OUTPUT + ARG_NUMBER),};

	public String handleExtension(String name, Object[] args) {
		if (name.equals("getColumnData")) {//test, 13.5.2013
			((Double[]) args[0])[0] = Double.valueOf(3.3);
		}
		return "hoi;";
	}

	public ExtensionDescriptor[] getExtensionFunctions() {
		return extensions;
	}

	public static class ValidateExceptionOJ extends Exception {

		public ValidateExceptionOJ(String message) {
			super(message);
		}
	}
}
