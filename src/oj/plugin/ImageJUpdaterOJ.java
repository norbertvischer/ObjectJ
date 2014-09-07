/*
 * ImageJUpdaterOJ.java
 * -- documented
 *
 * replaces ImageJ's updater, which handles "update menus" and "update ImageJ";
 * We add "update ObjectJ" and currently disable "update menus".
 */

package oj.plugin;

import ij.IJ;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.ImageJ_Updater;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import oj.OJ;
import oj.io.InputOutputOJ;

public class ImageJUpdaterOJ extends ImageJ_Updater {

    boolean saveJarAttempt;

    public void run(String arg) {
        if (arg.equals("menus")) {

            String msg = "With ObjectJ, you currently cannot Refresh Menus, you rather need to restart ImageJ";
            IJ.showMessage(msg);
            return;
        }

        if (IJ.getApplet() != null) {
            return;
        }

        int dialogResult = chooseImageJObjectJDialog("");

        if (dialogResult > 0 && OJ.getData() != null && OJ.getData().getChanged()) {//save project before updating //8.10.2009
            new InputOutputOJ().saveProject(OJ.getData(),true);//20.8.2010
            OJ.getData().setChanged(false);
            IJ.showStatus("ObjectJ: Project was saved before updating");
        }

        saveJarAttempt = false;
        if (dialogResult > 1) {
            boolean ok = updateObjectJ();

        }
        if ((dialogResult & 1) == 1) {
            super.run(arg);
        }

        if (dialogResult > 0 && saveJarAttempt) {
            System.exit(0);
        }

    }

    public boolean updateObjectJ() {

        String[] notes = openUrlAsList(OJ.URL + "/notes.txt");



        if ((notes == null) || (notes.length < 2)) {//second not executed if first is true
            error("Unable to connect to " + OJ.URL);
            return false;
        }

        String selectedJar = chooseObjectJVersionDialog(notes);
        if (selectedJar == null) {
            return false;//don't update
        }
        URL url = getClass().getResource("/oj/OJ.class");
        String oj_jar = url == null ? null : url.toString().replaceAll("%20", " ");
        if (oj_jar == null || !oj_jar.startsWith("jar:file:")) {
            error("Could not determine location of ij.jar");
            return false;
        }
        int exclamation = oj_jar.indexOf('!');
        oj_jar = oj_jar.substring(9, exclamation);

        File file = new File(oj_jar);
        if (!file.exists()) {
            error("File not found: " + file.getPath());
            return false;
        }
        if (!file.canWrite()) {
            String msg = "No write access: " + file.getPath();
            if (IJ.isVista()) {
                msg += Prefs.vistaHint;
            }
            error(msg);
            return false;
        }
        String simonUrl = "http://simon.bio.uva.nl/objectj/download/download_ObjectJ/" + selectedJar + "/objectj_.jar";

        byte[] jar = getJar(simonUrl, selectedJar);
        saveJar(file, jar);
        return true;
    }

    byte[] getJar(String address, String newestJar) {
        byte[] data;

        try {
            URL url = new URL(address);
            URLConnection uc = url.openConnection();
            int len = uc.getContentLength();
            IJ.showStatus("Downloading " + newestJar + IJ.d2s((double) len / 1048576, 1) + "MB)");
            InputStream in = uc.getInputStream();
            data = new byte[len];
            int n = 0;
            while (n < len) {
                int count = in.read(data, n, len - n);
                if (count < 0) {
                    throw new EOFException();
                }
                n += count;
            }
            in.close();
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    void saveJar(File f, byte[] data) {

        saveJarAttempt = true;
        if (data == null) {
            return;
        }
        int len = data.length;
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(data, 0, data.length);
            out.close();
        } catch (IOException e) {
        }
    }

    void error(String msg) {
        IJ.error("ImageJ Updater", msg);
    }

    public int chooseImageJObjectJDialog(String msg) {
        GenericDialog gd = new GenericDialog("Choose Updater");
        gd.addMessage(msg);
        gd.addCheckbox("Update ImageJ   (running: " + ij.IJ.getVersion() + ")", true);
        gd.addCheckbox("Update ObjectJ   (running: " + oj.OJ.releaseVersion + ")", false);
        gd.addMessage(" \n(Updating will quit ImageJ)");
        gd.showDialog();
        int result = 0;
        if (gd.getNextBoolean()) {
            result += 1;
        }
        if (gd.getNextBoolean()) {
            result += 2;
        }

        if (gd.wasCanceled()) {
            return 0;
        }
        return result;
    }

    String chooseObjectJVersionDialog(String[] list) {
        GenericDialog gd = new GenericDialog("Updating ObjectJ plugin");
        gd.addMessage("You are running ObjectJ " + oj.OJ.releaseVersion);
        gd.addChoice("Update ObjectJ to:", list, list[list.length - 2]);
        gd.addMessage(" \n(Updating will quit ImageJ)");
        gd.showDialog();
        if (gd.wasCanceled()) {
            return null;
        }
        int choice = gd.getNextChoiceIndex();
        return list[choice];
    }

    public String[] openUrlAsList(String address) {
        Vector v = new Vector();
        try {
            URL url = new URL(address);
            InputStream in = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                if (!line.equals("")) {
                    v.addElement(line);
                }
            }
            br.close();
        } catch (Exception e) {
        }
        String[] lines = new String[v.size()];
        v.copyInto((String[]) lines);
        return lines;
    }
}
