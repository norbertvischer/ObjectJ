package oj.util;

import ij.IJ;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oj.OJ;

/**
 * For backward compatibility
 */
public class UtilsOJ {

    public static final int LINE_COLOR_RED = 1;
    public static final int LINE_COLOR_GREEN = 2;
    public static final int LINE_COLOR_BLUE = 3;
    public static final int LINE_COLOR_YELLOW = 4;
    public static final int LINE_COLOR_CYAN = 5;
    public static final int LINE_COLOR_MAGENTA = 6;
    public static final int LINE_COLOR_BLACK = 7;
    public static final int LINE_COLOR_WHITE = 8;
    public static final int COLUMN_COLOR_BLACK = 0;
    public static final int COLUMN_COLOR_RED = 1;
    public static final int COLUMN_COLOR_GREEN = 2;
    public static final int COLUMN_COLOR_BLUE = 3;
    public static final int COLUMN_COLOR_YELLOW = 4;
    public static final int COLUMN_COLOR_CYAN = 5;
    public static final int COLUMN_COLOR_MAGENTA = 6;
    public static final int COLUMN_COLOR_DKGREEN = 8;
    public static final int COLUMN_COLOR_RED16 = 31;
    public static final int COLUMN_COLOR_GREEN16 = 31 * 32;
    public static final int COLUMN_COLOR_BLUE16 = 31 * 1024;
    public static final int COLUMN_COLOR_YELLOW16 = 31 + 31 * 32;
    public static final int COLUMN_COLOR_CYAN16 = 31 * 32 + 31 * 1024;
    public static final int COLUMN_COLOR_MAGENTA16 = 31 + 31 * 1024;

    public static Color getColumnColor(int code) {
        switch (code) {
            case UtilsOJ.COLUMN_COLOR_BLUE:
            case UtilsOJ.COLUMN_COLOR_BLUE16:
                return Color.BLUE;
            case UtilsOJ.COLUMN_COLOR_CYAN:
            case UtilsOJ.COLUMN_COLOR_CYAN16:
                return Color.CYAN;
            case UtilsOJ.COLUMN_COLOR_GREEN:
            case UtilsOJ.COLUMN_COLOR_DKGREEN:
            case UtilsOJ.COLUMN_COLOR_GREEN16:
                return Color.GREEN;
            case UtilsOJ.COLUMN_COLOR_MAGENTA:
            case UtilsOJ.COLUMN_COLOR_MAGENTA16:
                return Color.MAGENTA;
            case UtilsOJ.COLUMN_COLOR_RED:
            case UtilsOJ.COLUMN_COLOR_RED16:
                return Color.RED;
            case UtilsOJ.COLUMN_COLOR_YELLOW:
            case UtilsOJ.COLUMN_COLOR_YELLOW16:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }

    public static Color getLineColor(int code) {
        switch (code) {
            case UtilsOJ.LINE_COLOR_RED:
                return Color.RED;
            case UtilsOJ.LINE_COLOR_GREEN:
                return Color.GREEN;
            case UtilsOJ.LINE_COLOR_BLUE:
                return Color.BLUE;
            case UtilsOJ.LINE_COLOR_YELLOW:
                return Color.YELLOW;
            case UtilsOJ.LINE_COLOR_CYAN:
                return Color.CYAN;
            case UtilsOJ.LINE_COLOR_MAGENTA:
                return Color.MAGENTA;
            case UtilsOJ.LINE_COLOR_BLACK:
                return Color.BLACK;
            case UtilsOJ.LINE_COLOR_WHITE:
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }

    public static Color getColor(int code) {
        switch (code) {
            case 0:
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.CYAN;
            case 5:
                return Color.MAGENTA;
            default:
                return Color.BLACK;
        }
    }

    public static void showException(Exception ex, String methodName) {
        if (ex == null) {
            ij.IJ.log("\"" + methodName + "\"method not found: Are you using the newest version of ObjectJ?");//4.3.2010
        } else {
            ex.printStackTrace();
        }
    }

    public static void writeStringToFile(String macroContent, File file) {
        try {
            java.io.FileWriter w = new java.io.FileWriter(file);
            w.write(macroContent);
            w.close();
        } catch (Exception e) {
        }
    }

    public static boolean inRange(int minValue, int maxValue, int value) {
        if (maxValue < minValue) {
            return false;
        }
        return (value >= minValue) && (value <= maxValue);
    }

    public static String stripExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index > 0) {
            return filename.substring(0, index);
        } else {
            return filename;
        }
    }

    public static String getNextValidImageName(String imageName) {
        boolean done;
        String image_name = imageName;
        int index = 1;
        do {
            done = true;
            for (int i = 0; i < OJ.getData().getImages().getImagesCount(); i++) {
                if (image_name.equals(OJ.getData().getImages().getImageByIndex(i).getName())) {
                    image_name = imageName + " Copy " + Integer.toString(index);
                    done = false;
                    index += 1;
                    break;
                }
            }
        } while (!done);
        return image_name;
    }

    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index);
        } else {
            return null;
        }
    }

    public static String getFullFilename(String path) {
        int index = path.lastIndexOf(File.separator);

        if (index > 0) {
            return path.substring(index + 1);
        } else {
            return path;
        }
    }

    public static String doubleToString(double value, int decimals) {
        if (Double.isInfinite(value)) {
            return "Infinity";
        }
        if (Double.isNaN(value)) {
            return "";//22.1.2009
            //return "This is not a number";//31.10.2008 just to check
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * column names must be unique, not contain * and ?, and start with a
     * letter. (We want to reserve other begin characters for system) Column
     * names are not case sensitive.
     *
     * @param name column title to be tested
     * @return true if conditions are met; false if conflicting.
     */
    public static boolean isValidColumnName(String name) {
        String[] colNames = OJ.getData().getResults().getColumns().columnNamesToArray();
        for (int i = 0; i < colNames.length; i++) {
            if (name.equalsIgnoreCase(colNames[i])) {
                return false;
            }
        }
        return true;
    }

    public static int convertShortcutToCode(String shortcut) {
        int code = 0;
        int len = shortcut.length();
        if (len == 2 && shortcut.charAt(0) == 'F') {
            code = KeyEvent.VK_F1 + (int) shortcut.charAt(1) - 49;
            if (code >= KeyEvent.VK_F1 && code <= KeyEvent.VK_F9) {
                return code;
            } else {
                return 0;
            }
        }
        if (len == 3 && shortcut.charAt(0) == 'F') {
            code = KeyEvent.VK_F10 + (int) shortcut.charAt(2) - 48;
            if (code >= KeyEvent.VK_F10 && code <= KeyEvent.VK_F12) {
                return code;
            } else {
                return 0;
            }
        }
        if (len == 2 && shortcut.charAt(0) == 'N') {
            // numeric keypad
            code = KeyEvent.VK_NUMPAD0 + (int) shortcut.charAt(1) - 48;
            if (code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9) {
                return code;
            }
            switch (shortcut.charAt(1)) {
                case '/':
                    return KeyEvent.VK_DIVIDE;
                case '*':
                    return KeyEvent.VK_MULTIPLY;
                case '-':
                    return KeyEvent.VK_SUBTRACT;
                case '+':
                    return KeyEvent.VK_ADD;
                case '.':
                    return KeyEvent.VK_DECIMAL;
                default:
                    return 0;
            }
        }
        if (len != 1) {
            return 0;
        }
        int c = (int) shortcut.charAt(0);
        if (c >= 65 && c <= 90) {
            //A-Z
            code = KeyEvent.VK_A + c - 65;
        } else if (c >= 97 && c <= 122) {
            //a-z
            code = KeyEvent.VK_A + c - 97;
        } else if (c >= 48 && c <= 57) {
            //0-9
            code = KeyEvent.VK_0 + c - 48;
        } else {
            switch (c) {
                case 43:
                    code = KeyEvent.VK_PLUS;
                    break;
                case 45:
                    code = KeyEvent.VK_MINUS;
                    break;
                //case 92: code = KeyEvent.VK_BACK_SLASH; break;
                default:
                    return 0;
            }
        }
        return code;
    }

    public static String convertCodeToShortcut(int code) {
        for (int i = KeyEvent.VK_F1; i <= KeyEvent.VK_F10; i++) {
            if (code == i) {
                return "F" + Integer.toString(i - KeyEvent.VK_F1 + 1);
            }
        }
        for (int i = KeyEvent.VK_NUMPAD0; i <= KeyEvent.VK_NUMPAD9; i++) {
            if (code == i) {
                return "N" + Integer.toString(i - KeyEvent.VK_NUMPAD0);
            }
        }
        for (int i = KeyEvent.VK_0; i <= KeyEvent.VK_9; i++) {
            if (code == i) {
                return Integer.toString(i - KeyEvent.VK_0);
            }
        }
        for (int i = KeyEvent.VK_A; i <= KeyEvent.VK_Z; i++) {
            if (code == i) {
                return Character.toString((char) i);
            }
        }
        return "";
    }

    public static String readStringFromFile(File file) {
        try {
            StringBuffer sb = new StringBuffer(10000);
            java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(file));
            while (true) {
                String s = r.readLine();
                if (s == null) {
                    break;
                } else {
                    sb.append(s + "\n");
                }
            }
            r.close();
            return new String(sb);
        } catch (Exception e) {
            return null;
        }
    }

    // replaces contents of comments with blanks 16.4.2014
    public static String maskComments(String text) {
        char[] chars = text.toCharArray();
        int n = chars.length;
        boolean inSlashSlashComment = false;
        boolean inSlashStarComment = false;
        for (int i = 0; i < n - 1; i++) {
            if (chars[i] == '/' && chars[i + 1] == '/') {
                inSlashSlashComment = true;
            }
            if (chars[i] == '\n') {
                inSlashSlashComment = false;
            }
            if (!inSlashSlashComment) {
                if (chars[i] == '/' && chars[i + 1] == '*') {
                    inSlashStarComment = true;
                }
                if (chars[i] == '*' && chars[i + 1] == '/') {
                    inSlashStarComment = false;
                }
            }
            if (inSlashSlashComment || inSlashStarComment) {
                chars[i] = ' ';
            }
        }
        text = String.valueOf(chars);
        return text;
    }

  
    public static void drawIcon(Graphics g, String icon, int x, int y) {
        if (g == null) {
            return;
        }
        int length = icon.length();
        int x1, y1, x2, y2;
        int pc = 0;
        while (true) {
            char command = icon.charAt(pc++);
            if (pc >= length) {
                break;
            }
            switch (command) {
                case 'B':
                    x += v(pc++, icon);
                    y += v(pc++, icon);
                    break;  // reset base
                case 'R':
                    g.drawRect(x + v(pc++, icon), y + v(pc++, icon), v(pc++, icon), v(pc++, icon));
                    break;  // rectangle
                case 'F':
                    g.fillRect(x + v(pc++, icon), y + v(pc++, icon), v(pc++, icon), v(pc++, icon));
                    break;  // filled rectangle
                case 'O':
                    g.drawOval(x + v(pc++, icon), y + v(pc++, icon), v(pc++, icon), v(pc++, icon));
                    break;  // oval
                case 'o':
                    g.fillOval(x + v(pc++, icon), y + v(pc++, icon), v(pc++, icon), v(pc++, icon));
                    break;  // filled oval
                case 'C':
                    g.setColor(new Color(v(pc++, icon) * 16, v(pc++, icon) * 16, v(pc++, icon) * 16));
                    break; // set color
                case 'L':
                    g.drawLine(x + v(pc++, icon), y + v(pc++, icon), x + v(pc++, icon), y + v(pc++, icon));
                    break; // line
                case 'D':
                    g.fillRect(x + v(pc++, icon), y + v(pc++, icon), 1, 1);
                    break; // dot
                case 'P': // polyline
                    x1 = x + v(pc++, icon);
                    y1 = y + v(pc++, icon);
                    while (true) {
                        x2 = v(pc++, icon);
                        if (x2 == 0) {
                            break;
                        }
                        y2 = v(pc++, icon);
                        if (y2 == 0) {
                            break;
                        }
                        x2 += x;
                        y2 += y;
                        g.drawLine(x1, y1, x2, y2);
                        x1 = x2;
                        y1 = y2;
                    }
                    break;
                case 'T': // text (one character)
                    x2 = x + v(pc++, icon);
                    y2 = y + v(pc++, icon);
                    int size = v(pc++, icon) * 10 + v(pc++, icon);
                    char[] c = new char[1];
                    c[0] = pc < icon.length() ? icon.charAt(pc++) : 'e';
                    g.setFont(new Font("SansSerif", Font.BOLD, size));
                    g.drawString(new String(c), x2, y2);
                    break;
                default:
                    break;
            }
            if (pc >= length) {
                break;
            }
        }
    }

    private static int v(int pc, String icon) {
        if (pc >= icon.length()) {
            return 0;
        }
        char c = icon.charAt(pc);
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
                return 10;
            case 'b':
                return 11;
            case 'c':
                return 12;
            case 'd':
                return 13;
            case 'e':
                return 14;
            case 'f':
                return 15;
            default:
                return 0;
        }
    }

    public static void showInFinderOrExplorer() {
        String dir = OJ.getData().getDirectory();
        String fullPath = dir + OJ.getData().getFilename();
        if (IJ.isMacOSX()) {
            fullPath = fullPath.replaceAll("/", ":");
            if (fullPath.startsWith(":")) {
                fullPath = fullPath.substring(1);
            }
        }
        if (IJ.isJava16()) {
            Desktop desktop = Desktop.getDesktop();
            File f = new File(dir);
            try {
                desktop.open(f);
                if (IJ.isMacOSX()) {
                    String s1 = "result = exec('osascript', '-e', '";

                    String appleScript = "";
                    appleScript += "tell application \"Finder\"\\n";
                    appleScript += "activate\\n";
                    appleScript += "set myFile to " + "\"" + fullPath + "\"" + " as alias\\n";
                    appleScript += "select myFile\\n";
                    appleScript += "end tell\\n";

                    String s3 = "');";
                    String macro = s1 + appleScript + s3;
                    IJ.runMacro(macro);
                }
            } catch (java.io.IOException ioexcept) {
                IJ.showMessage("An error ecccured while showing the project folder");
                return;
            }
        } else {
            showInFinderOrExplorerJava1_5(dir);
        }

    }

    public static void showInFinderOrExplorerJava1_5(String dir) {
        if (!ij.IJ.isWindows() && !ij.IJ.isMacOSX()) {
            return;
        }
        String macroLine = "";

        if (ij.IJ.isWindows()) {
            dir = dir.substring(0, dir.length() - 1);//exec on Windows doesn't want the trailing file separator for a folder
        }
        String path = "\"" + dir + "\"";//add quotes
        if (ij.IJ.isMacintosh()) {
            macroLine = "a_9 = exec(\"open\", " + path + ");\n";
        }

        if (ij.IJ.isWindows()) {
            char[] chars = path.toCharArray();// double backslashes
            String winPath = "";
            for (char cc : chars) {
                winPath += cc;
                if (cc == '\\') {
                    winPath += cc;
                }
            }
            macroLine = "a_9 = exec(\"cmd\", \"/c\", \"start\", \"explorer\", " + winPath + ");";
        }

        try {
            IJ.runMacro(macroLine);
        } catch (Throwable ee) {
            IJ.error("Could not open the Project Folder ");
            return;
        }
    }

    /**
     * converts cr to lf, (OS 9 case) or drops cr if it was followed by lf
     * (Windows case)
     */
    public static String fixLineFeeds(String txt) {

        StringBuilder sb = new StringBuilder();
        final char CR = 0xd;
        final char LF = 0xa;
        int len = txt.length();
        for (int jj = 0; jj < len; jj++) {
            char c = txt.charAt(jj);
            if (c == CR) {
                boolean isCRLF = (jj < len - 1) && ((txt.charAt(jj + 1) == LF));
                if (!isCRLF) {
                    sb.append(LF);
                }

            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String showInFinderOrExplorerStringOld(String dir) {//before java 1.6 ?
        String macroLine = "";

        if (ij.IJ.isWindows()) {
            dir = dir.substring(0, dir.length() - 1);//exec on Windows doesn't want the trailing file separator for a folder
        }
        String path = "\"" + dir + "\"";//add quotes
        if (ij.IJ.isMacintosh()) {
            macroLine = "a_9 = exec(\"open\", " + path + ");\n";
        }

        if (ij.IJ.isWindows()) {
            char[] chars = path.toCharArray();// double backslashes
            String winPath = "";
            for (char cc : chars) {
                winPath += cc;
                if (cc == '\\') {
                    winPath += cc;
                }
            }
            macroLine = "a_9 = exec(\"cmd\", \"/c\", \"start\", \"explorer\", " + winPath + ");";
        }
        return macroLine;
    }

    public static String getFileType(String directory, String filename) {

        File file = new File(directory, filename);
        InputStream is;
        byte[] buf = new byte[132];
        try {
            is = new FileInputStream(file);
            is.read(buf, 0, 4);
            is.close();
        } catch (IOException e) {
            return "";
        }
        if (buf[0] == 'P' && buf[1] == 'K' && buf[2] == 3 && buf[3] == 4) {
            return "isZipped";
        }
        if (buf[0] == 'o' && buf[1] == 'j' && buf[2] == 'j' && buf[3] == 0) {//7.4.2010
            return "isZipped-magic-ojj";
        }
        return "";
    }

    //http://www.idiom.com/~zilla/Xfiles/javasymlinks.html
    public static boolean isLink(File file) {//not pure yet
        try {
            if (!file.exists()) {
                return true;
            } else {
                String cnnpath = file.getCanonicalPath();
                String abspath = file.getAbsolutePath();
                return !abspath.equals(cnnpath);
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return true;
        }
    } //isLink

    public static String resolveAlias(String dir, String fName) {
        File f = new File(dir, fName);
        try {
            String path = f.getCanonicalPath();

            return path;

        } catch (IOException e) {
            return "An error occured while resolving alias";
        }
    }

    /**
     * From macro text, those lines are extracted that indicate macro, function
     * or bookmark. Each line starts with a constant header: line number (6
     * chars), kind('m', 'f', 'b') and caret position '*'
     *
     */
    public static String extractFunctions(String txt, int caretPos) {

        int caretLine = 0;
        for (int charPos = 0; charPos < txt.length(); charPos++) {
            if (txt.charAt(charPos) == '\n') {
                caretLine++;
                if (charPos > caretPos) {
                    break;
                }
            }
        }

        String functionNames = "";
        String thisLine = "";
        String[] lines = txt.split("\n");
        String fragment = "";
        char kind = ' ';
        char caret = ' ';
        for (int lineNo = 0; lineNo < lines.length; lineNo++) {
            String part1 = lines[lineNo];
            String part2 = part1.replaceAll(" ", "");
            kind = ' ';
            if (lineNo >= caretLine) {
                caret = '*';
            }
            if (part2.startsWith("macro\"")) {
                int rest = part1.indexOf("\"");
                String macroName = part1.substring(rest);
                macroName = macroName.replaceAll("\"", "");
                int bracket = macroName.indexOf("[");

                if (bracket > 0) {
                    macroName = macroName.substring(0, bracket);
                }
                int brace = macroName.indexOf("{");
                if (brace > 0) {
                    macroName = macroName.substring(0, brace);
                }
                fragment = macroName.trim();
                kind = 'm';
            }
            if (part2.startsWith("function")) {
                int rest = part1.indexOf("function") + 9;
                String functionName = part1.substring(rest);
                functionName = functionName.replaceAll("\"", "");
                functionName = functionName.replace('{', ' ');

                fragment = functionName.trim();
                kind = 'f';
            }
            if (kind == ' ') {
                String ss = "";
                int posA = part1.indexOf("//<<");//left part of line is bookmark
                int posB = part1.indexOf("//>>");//right part of line is bookmark
                if (posA > 0) {
                    ss = part1.substring(0, posA);
                } else if (posB >= 0) {
                    ss = part1.substring(posB + 4, part1.length());
                }
                if (ss.length() > 0) {
                    ss = ss.trim();

                    if (ss.length() > 32) {
                        ss = ss.substring(0, 32) + "...";
                    }

                    String bookMarkName = ss;
                    fragment = bookMarkName;
                    kind = 'b';
                }
            }
            if (kind != ' ') {
                thisLine = "" + lineNo + "         ";
                thisLine = thisLine.substring(0, 6);
                thisLine = thisLine + kind + caret + fragment + "\n";
                functionNames += thisLine;
                {
                    if (caret == '*') {
                        caretLine = Integer.MAX_VALUE;
                        caret = ' ';
                    }
                }
            }

        }
        return functionNames;
    }

    public static double[] arrayListTodouble(ArrayList<Double> doubles) {
        int len = doubles.size();
        double[] arr = new double[len];
        for (int jj = 0; jj < len; jj++) {
            arr[jj] = (double) doubles.get(jj);
        }
        return arr;
    }

    public static double getStatistics(double[] a, String what) {
        int n = a.length;
        double sum = 0.0, sum2 = 0.0, value;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double mean;
        for (int i = 0; i < n; i++) {
            value = a[i];
            sum += value;
            sum2 += value * value;
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        mean = sum / n;
        if (what.equals("sum")) {
            return sum;
        }
        if (what.equals("mean")) {
            return mean;
        }
        if (what.equals("min")) {
            return min;
        }
        if (what.equals("max")) {
            return max;
        }
        if (what.equals("std")) {
            double stdDev = (n * sum2 - sum * sum) / n;
            double std = Math.sqrt(stdDev / (n - 1.0));
            return std;
        }
        return Double.NaN;

    }

    /**
     * Converts the a hyperstack position into stackIndex
     *
     * @param dimensions[] width, height, nChannels, nSlices, nFrames
     * @param position[] channel, slice, frame (one-based)
     * @return stackIndex (one-based)
     */
    public static int convertPositionToIndex(int[] dimensions, int[] position) {
        int nChannels = dimensions[2];
        int nSlices = dimensions[3];
        int nFrames = dimensions[4];
        int channel = position[0];
        int slice = position[1];
        int frame = position[2];

        int stackIndex = (frame - 1) * nSlices * nChannels;
        stackIndex += (slice - 1) * nChannels;
        stackIndex += channel;
        return stackIndex;
    }

    /**
     * Converts the stack index into a hyperstack position
     *
     * @param dim width, height, nChannels, nSlices, nFrames
     * @param sIndex sliceIndex(one-based)
     * @return position[] consisting of channel, frame, slice
     */
    public static int[] convertIndexToPosition(int[] dim, int sIndex) {
        int[] position = new int[3];
        position[0] = ((sIndex - 1) % dim[2]) + 1;
        position[1] = (((sIndex - 1) / dim[2]) % dim[3]) + 1;
        position[2] = (((sIndex - 1) / (dim[2] * dim[3])) % dim[4]) + 1;
        return position;
    }
}
