
import ij.*;
import ij.plugin.*;
import ij.util.Tools;
import org.micromanager.*;
import mmcorej.*;

public class BX_Control implements PlugIn {
    //2.9.2012 disabled serial

    String version = "BX_Control 6-sep-2012";
    double exposure = 2;
    double shutter = 0;
    double autoShutter = 0;
    String port1 = "/dev/tty.KeySerial1";
    String port2 = "/dev/tty.USA28Xb2P2.2";
    boolean doGrab = false;
    boolean doShutter = false;
    boolean doAutoShutter = false;
    boolean setExposure = false;
    boolean updateGUI = false;

    public void run(String arg) {
        MMStudioMainFrame gui = null;
        gui = MMStudioMainFrame.getInstance();
        if (gui == null) {
            IJ.wait(2000);
            gui = MMStudioMainFrame.getInstance();
            if (gui != null) {
                IJ.showMessage("Succeeded 2nd time to get gui=MMStudioMainFrame");
            }
        }
        if (gui == null) {
            IJ.showMessage("Error 1: gui == null");
            return;
        }
        try {
            gui.sleep(100);

        } catch (Exception e) {
            IJ.showMessage("Error 2: " + e);
            return;
        }
        CMMCore mmc = null;
        try {
            mmc = gui.getCore();
        } catch (Exception e) {
            IJ.showMessage("Error 3 (getCore): " + e);
            return;
        }

        try {
            gui.sleep(100);

        } catch (Exception e) {
            IJ.showMessage("Error 4: " + e);
            return;
        }


        String info = mmc.getVersionInfo();
        ij.IJ.showStatus(info);
        //IJ.showMessage(info);
        //gui.snapSingleImage();
        String options = Macro.getOptions();
        if ((options != null) && (options.length() != 0)) {
            if (parseOptions(options) == false) {
                return;
            }
        }


        if (doShutter) {
            if (shutter == 1) {
                try {
                    //mmc.setSerialPortCommand(port1, "A", "");
                    mmc.setShutterOpen(true);//30-aug-2012

                } catch (Exception e) {
                    IJ.showMessage("Error 5: " + e);
                    return;
                }
            }

            if (shutter == 0) {
                try {
                    //mmc.setSerialPortCommand(port2, "B", "");
                    mmc.setShutterOpen(false);//30-aug-2012

                } catch (Exception e) {
                    IJ.showMessage("Error 6: " + e);
                    return;
                }


            }
        }
        if (doAutoShutter) {
            if (autoShutter == 1) {
                try {
                    IJ.showStatus("autoshutter = on");
                    mmc.setAutoShutter(true);//30-aug-2012


                } catch (Exception e) {
                    IJ.showMessage("Error 15: " + e);
                    return;
                }
            }

            if (autoShutter == 0) {
                try {
                    IJ.showStatus("autoshutter = off");

                    mmc.setAutoShutter(false);//30-aug-2012
                } catch (Exception e) {
                    IJ.showMessage("Error 16: " + e);
                    return;
                }


            }
        }
        if (setExposure) {
            try {
                mmc.setExposure(exposure);
                gui.refreshGUI();
            } catch (Exception e) {
                IJ.showMessage("Error 7: " + e);
                return;
            }
        }
        if (updateGUI) {
            try {
                gui.updateGUI(true);//6.9.2012
            } catch (Exception e) {
                IJ.showMessage("Error 17: " + e);
                return;
            }
        }
        if (doGrab) {
            try {
                gui.snapSingleImage();
            } catch (Exception e) {
                IJ.showMessage("Error 8: " + e);
                return;
            }

        }
    }

    private boolean parseOptions(String options) {//parses the macro options
        //boolean ok = true;
        doShutter = false;
        setExposure = false;
        doGrab = false;
        updateGUI = false;
        String[] s = options.split(" ");
        String[] t;
        for (int i = 0; i < s.length; i++) {
            t = s[i].split("=");
            if (t[0].equalsIgnoreCase("About")) {
                ij.IJ.showMessage(version);
                return false;
            }
            if (t.length > 1) {
                if (t[0].equalsIgnoreCase("Exposure")) {
                    setExposure = true;
                    exposure = Tools.parseDouble(t[1], 2);
                    return true;
                }
                if (t[0].equalsIgnoreCase("Shutter")) {
                    doShutter = true;
                    shutter = Tools.parseDouble(t[1], 2);
                    return true;
                }
                if (t[0].equalsIgnoreCase("AutoShutter")) {
                    doAutoShutter = true;
                    autoShutter = Tools.parseDouble(t[1], 2);
                    return true;
                }
                if (t[0].equalsIgnoreCase("Grab")) {
                    doGrab = true;
                    return true;
                }
                if (t[0].equalsIgnoreCase("updateGUI")) {
                    updateGUI = true;
                    return true;
                }

            }
        } //i-loop
        return false;
    }
}
