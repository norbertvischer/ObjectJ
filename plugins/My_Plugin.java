
import ij.*;
import ij.plugin.*;
import org.micromanager.*;
import mmcorej.*;

public class My_Plugin implements PlugIn {
   public void run(String arg) {
      MMStudioMainFrame gui = MMStudioMainFrame.getInstance();
      CMMCore mmc = gui.getCore();
      String info = mmc.getVersionInfo();
      IJ.showMessage(info);
      gui.snapSingleImage();
   }  
} 
