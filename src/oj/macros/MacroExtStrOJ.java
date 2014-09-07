/*
 * MacroExtStrOJ.java
 * -- documented
 */
package oj.macros;

import java.util.ArrayList;

/**
 * Composes a large piece of macro text containing all "oj" commands, that will
 * be invisibly appended to the user macros. Example: to be able to call
 * ojSetMarker(xPos, yPos); we need the invisible part: function ojSetMarker (x,
 * y){return call("oj.macros.ExtensionOJ.handleMacroExtension", "ojSetMarker",
 * "18", "33");}
 *
 * the 5 methods "getFunction()" are called after startup to compose the text
 * representation of macro functions with different signatures (0..4 string
 * arguments). So we can easily add a new macro to "macroExtensions" without
 * caring about syntax.
 */
public class MacroExtStrOJ {

    private static String getFunction(String functionName) {
        return "function " + functionName + "(){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\");}\n";
    }

    private static String getFunction(String functionName, String arg) {
        return "function " + functionName + "(" + arg + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg + ");}\n";
    }

    private static String getFunction(String functionName, String arg1, String arg2) {
        return "function " + functionName + "(" + arg1 + "," + arg2 + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + ");}\n";
    }

    private static String getFunction(String functionName, String arg1, String arg2, String arg3) {
        return "function " + functionName + "(" + arg1 + "," + arg2 + "," + arg3 + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + "," + arg3 + ");}\n";
    }

    private static String getFunction(String functionName, String arg1, String arg2, String arg3, String arg4) {
        return "function " + functionName + "(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ");}\n";
    }

    /*
     * if function's argument is an array, then the array needs first to be converted into a single string, where all array 
     * elements are separated by newLine "\n". This single string is passed to the "call" function
     */
    private static String getFloatArrayInputFunction(String functionName, String arg) {
        String s2 = "argString1734 = ojArrayToString(" + arg + ");\n";
        return "function " + functionName + "(" + arg + "){" + s2 + "return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + "argString1734" + ");}\n";
    }

    private static String getFloatArrayFunction(String functionName) {
        return "function " + functionName + "(){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"); J__A = ojStringToFloatArray(returnVal); return J__A;}\n";
    }

    private static String getFloatArrayFunction(String functionName, String arg) {
        return "function " + functionName + "(" + arg + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg + "); J__A = ojStringToFloatArray(returnVal); return J__A;}\n";
    }

    private static String getFloatFunction(String functionName, String arg) {
        return "function " + functionName + "(" + arg + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg + "); return parseFloat(returnVal);}\n";
    }

    private static String getIntFunction(String functionName) {
        return "function " + functionName + "(){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"); return parseInt(returnVal);}\n";
    }

    private static String getFloatFunction(String functionName) {
        return "function " + functionName + "(){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"); return parseFloat(returnVal);}\n";
    }

    private static String getFloatFunction(String functionName, String arg1, String arg2) {
        return "function " + functionName + "(" + arg1 + "," + arg2 + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + "); return parseFloat(returnVal);}\n";
    }

    private static String getIntFunction(String functionName, String arg) {
        return "function " + functionName + "(" + arg + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg + "); return parseInt(returnVal);}\n";
    }

    private static String getIntFunction(String functionName, String arg1, String arg2) {//5.11.2009
        return "function " + functionName + "(" + arg1 + "," + arg2 + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + "); return parseInt(returnVal);}\n";
    }

    private static String getIntFunction(String functionName, String arg1, String arg2, String arg3) {//5.11.2009
        return "function " + functionName + "(" + arg1 + "," + arg2 + "," + arg3 + "){returnVal = call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + "," + arg3 + "); return parseInt(returnVal);}\n";
    }

    private static String getStrFunction(String functionName) {
        return "function " + functionName + "(){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\");}\n";
    }

    private static String getStrFunction(String functionName, String arg) {
        return "function " + functionName + "(" + arg + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg + ");}\n";
    }

    private static String getStrFunction(String functionName, String arg1, String arg2) {//22.9.2009
        return "function " + functionName + "(" + arg1 + "," + arg2 + "){return call(\"oj.macros.ExtensionOJ.handleMacroExtension\",\"" + functionName + "\"," + arg1 + "," + arg2 + ");}\n";
    }

    /**
     * Creates a new instance of UserFunctionsOJ
     */
    public static String macroExtensions() {
        StringBuffer buf = new StringBuffer();
        buf.append(";\n ");
        //String s = ";\n ";

        buf.append(getFunction("ojActiveItemName"));
        buf.append(getFunction("ojAdvance"));
        buf.append(getFunction("ojClose", "pattern"));//17.3.2012
        buf.append(getFunction("ojCloseItem"));
        buf.append(getFunction("ojCloseResults"));
        buf.append(getFunction("ojCloseObject"));
        //buf.append(getFunction("ojCloseImages", "conditions")); not used
        buf.append(getIntFunction("ojColumnNumber", "columnName"));
        //buf.append(getFunction("ojCopyFromScreen", "left", "top", "width", "height"));//2.3.2012
        buf.append(getFunction("ojDeleteAllObjects"));
        buf.append(getFunction("ojDeleteColumn", "columnName"));
        buf.append(getFunction("ojDeleteItem", "itemType", "itemNumber"));
        buf.append(getFunction("ojDeleteObject", "objectIndex"));
        //buf.append(getFunction("ojDisposeAllImages"));26.9.2010
        //buf.append(getFunction("ojDisposeImage", "imageName"));
        buf.append(getFunction("ojExtendVisibilityDepth", "lowValue", "highValue"));
        buf.append(getIntFunction("ojFirstObject", "imageIndex"));
        buf.append(getFunction("ojFlatten"));//18.10.2011    

        buf.append(getFunction("ojSelectClosestItem", "x", "y", "tolerance"));//5.11.2009
        buf.append(getStrFunction("ojGetColumnTitle", "columnIndex"));//18.4.2009
        buf.append(getIntFunction("ojGetOpenObject"));
        buf.append(getStrFunction("ojGetItemName"));//6.3.2010
        buf.append(getStrFunction("ojGetItemNames"));//revalidate!12-2013
        buf.append(getFunction("ojGetPositions", "xArray", "yArray"));

        buf.append(getStrFunction("ojGetItemTypes"));//8.6.2011
        buf.append(getFloatFunction("ojGetImageValue", "n", "key"));
        buf.append(getStrFunction("ojGetProjectName"));
        buf.append(getStrFunction("ojGetProjectPath"));
        buf.append(getFloatFunction("ojGetStatistics", "columnName", "operation"));
        buf.append(getFloatFunction("ojGetStatistics", "columnName", "rowIndex"));
        buf.append(getFloatFunction("ojGetVoxelSize", "imageIndex", "xyz"));//17.9.2009
        buf.append(getFunction("ojHideResults"));//24.2.2009
        buf.append(getFloatArrayFunction("ojGetGlassDimensions"));
        buf.append(getIntFunction("ojImageLink"));

        buf.append(getIntFunction("ojIndexToRow", "index"));//14.7.2010
        buf.append(getIntFunction("ojIndexToRank", "index"));//21.8.2010
        buf.append(getFunction("ojInitColumn", "columnName"));
        buf.append(getFunction("ojInitTextColumn", "columnName"));
        buf.append(getFunction("ojItemToRoi"));

        buf.append(getIntFunction("ojLastObject", "imageIndex"));
        buf.append(getIntFunction("ojLastRow", "columnName"));
        buf.append(getIntFunction("ojLinkImage"));

        buf.append(getFunction("ojMakeBoundingRoi", "param"));
        buf.append(getIntFunction("ojMatches", "string", "pattern"));
        buf.append(getIntFunction("ojIndexToId", "index"));
        buf.append(getIntFunction("ojIdToIndex", "id"));

        buf.append(getFunction("ojMovePoint", "pointIndex", "xPos", "yPos", "zPos"));
        buf.append(getIntFunction("ojNImages"));
        buf.append(getIntFunction("ojNItems", "itemType"));
        buf.append(getIntFunction("ojNObjects"));
        buf.append(getIntFunction("ojNPoints"));
        buf.append(getIntFunction("ojNColumns"));
        buf.append(getIntFunction("ojObjectID", "cell"));
        buf.append(getIntFunction("ojNewestObject"));//15.4.2010

        buf.append(getFunction("ojOpenObject", "objectIndex"));

        buf.append(getIntFunction("ojOwnerIndex", "index"));
        buf.append(getStrFunction("ojOwnerName", "index"));
        buf.append(getStrFunction("ojGetTarget"));
        buf.append(getFunction("ojLineToPolygon", "linewidth", "roundCorners"));
        buf.append(getFunction("ojRenameImage", "oldName", "newName"));
        buf.append(getFunction("ojPutAlgorithm", "algorithm"));
        buf.append(getFunction("ojPutOperand", "itemType", "clone", "point"));
        buf.append(getFunction("ojSetTarget", "string"));
        buf.append(getIntFunction("ojQualified", "index"));
        buf.append(getFunction("ojQualify", "index", "flag"));
        buf.append(getIntFunction("ojRowToIndex", "row"));//14.7.2010
        buf.append(getIntFunction("ojRankToIndex", "rank"));//14.7.2010
        buf.append(getStrFunction("ojRequires", "version"));
        buf.append(getFloatFunction("ojResult", "columnName", "rowIndex"));
        buf.append(getStrFunction("ojResultString", "columnName", "rowIndex"));//22.9.2009
        buf.append(getFunction("ojRoiToItem"));
        buf.append(getFunction("ojRecalculate"));
        buf.append(getFunction("ojRepositionItem", "fromIndex", "toIndex"));
        buf.append(getFunction("ojSelectItem", "name", "index"));
        buf.append(getFloatFunction("ojGetItemLength"));

        buf.append(getIntFunction("ojSelectedObject"));
        buf.append(getStrFunction("ojSelectedItemName"));

        buf.append(getFunction("ojSelectObject", "objectIndex"));
        buf.append(getFunction("ojSetColumnProperty", "columnName", "propertyName", "propertyValue"));
        buf.append(getFunction("ojSetColumnProperties", "columnName", "properties"));
        buf.append(getFunction("ojSetComposite", "mode"));
        buf.append(getFunction("ojGlassWindow", "properties"));

        buf.append(getFunction("ojSetItemVisible", "itemType", "visible"));
        buf.append(getFunction("ojSetMarker", "xPos", "yPos"));
        buf.append(getFunction("ojSetPlotProperties", "ColumnTitle", "properties"));
        buf.append(getFunction("ojSetResult", "columnName", "rowIndex", "value"));
        buf.append(getFunction("ojSetTool", "tool"));
        buf.append(getFunction("ojSetUnchanged"));
        buf.append(getFunction("ojSetValue", "columnName", "rowIndex", "value"));
        buf.append(getFunction("ojOrderObjectsInZ", "imageNumber"));

        buf.append(getFunction("ojShowImage", "imageIndex"));
        buf.append(getFunction("ojGetImageName", "imageIndex"));

        buf.append(getFunction("ojSet3D", "d3"));
        buf.append(getFunction("ojSetLocation", "xPos", "yPos", "width", "height"));

        buf.append(getFunction("ojShowObject", "objectIndex"));
        buf.append(getFunction("ojShowProject"));
        buf.append(getFunction("ojShowResults"));
        buf.append(getFunction("ojSaveProject"));
        buf.append(getFunction("ojShowTools"));
        buf.append(getFunction("ojSwapSlices", "sliceA", "sliceB"));
        buf.append(getFunction("ojSwitchToItem", "itemName"));
        buf.append(getIntFunction("ojUpdateMarkers", "flag"));//10.2.2011

        buf.append(getFloatFunction("ojvCalculate", "operation"));//same as get result
        buf.append(getFunction("ojvInitStack", "dim"));
        buf.append(getIntFunction("ojvGetStackSize"));
//        buf.append(getFloatFunction("ojvGetResult", "algor"));
        buf.append(getFloatFunction("ojvGetVertexX", "index"));
        buf.append(getFloatFunction("ojvGetVertexY", "index"));
        buf.append(getFloatFunction("ojvGetVertexZ", "index"));

        buf.append(getFunction("ojvPushRoi"));
        buf.append(getFunction("ojvPushItem"));
        buf.append(getFunction("ojvPushVertex", "v"));
        buf.append(getFunction("ojvPushPoint", "index"));
        buf.append(getFunction("ojPluginTest", "v"));

        buf.append(getFloatFunction("ojXPos", "n"));
        buf.append(getFloatFunction("ojYPos", "n"));
        buf.append(getFloatFunction("ojZPos", "n"));
        buf.append(getFloatArrayFunction("ojXYZPos", "n"));

        buf.append(getFunction("ojZoom", "factor", "xHook", "yHook"));
        buf.append(getFunction("ojTest", "arg"));
        buf.append(getStrFunction("ojTmpFunction", "anything"));

        buf.append("function ojIntArray(){J__s = call(\"oj.macros.ExtensionOJ.ojIntArray\"); J__A = split(J__s, fromCharCode(10)); for (J__j = 0; J__j < lengthOf(J__A); J__j++) J__A[J__j] = parseInt(J__A[J__j]); return J__A;}\n");
        buf.append("function ojInvertFloatArray(xyz){J__s = call(\"oj.macros.ExtensionOJ.ojInvertFloatArray\", ojArrayToString(xyz)); J__A = ojStringToFloatArray(J__s); return J__A;}\n");
        buf.append("function ojStringArray(){return split(call(\"oj.macros.ExtensionOJ.ojStringArray\"),fromCharCode(10));}\n");
        buf.append("\n");
        buf.append("function ojArrayToString(ar){\n");
        buf.append("ss__oj = '';\n");
        buf.append("for(j__oj = 0; j__oj < lengthOf(ar); j__oj++){\n");
        buf.append("	if (j__oj >  0)\n");
        buf.append("		ss__oj = ss__oj + '\\n';\n");
        buf.append("	ss__oj = ss__oj + ar[j__oj];}\n");
        buf.append("return ss__oj;\n");
        buf.append("}\n");
        buf.append("\n");
        buf.append("function ojStringToFloatArray(ss__oj){\n");
        buf.append("	ar__oj = split(ss__oj, '\\n');\n");
        buf.append("	for (jj = 0; jj< lengthOf(ar__oj); jj++)\n");
        buf.append("		ar__oj[jj] = parseFloat(ar__oj[jj]);\n");
        buf.append("	return ar__oj;\n");
        buf.append("}\n");

        return buf.toString();
    }
}
