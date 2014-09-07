/**
 * ImageActionsOJ.java
 * fully documented 18.5.2010
 *
 * ImageActionsOJ supplies listener methods that are
 * connected to submenu items of ObjectJ>Linked Images
 * Not all action listeners are used
 */
package oj.gui.menuactions;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.io.Opener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import oj.OJ;
import oj.graphics.CustomCanvasOJ;
import oj.util.UtilsOJ;
import oj.project.ImageOJ;
import oj.io.OpenDialogOJ;
import oj.gui.settings.ProjectSettingsOJ;

public class ImageActionsOJ {

  public static ActionListener RemoveImageAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      ViewActionsOJ.SettingsAction.actionPerformed(e);
      ProjectSettingsOJ.getInstance().selectImageDefsPanel();
    }
  };
  public static ActionListener RemoveAllImagesAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      OJ.getData().getImages().removeAllImages(true);
    }
  };
  public static ActionListener CopyImagePlusAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      copyImageToProject(IJ.getImage());
    }
  };
  public static ActionListener LinkImagePlusAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      OJ.getImageProcessor().addImage(IJ.getImage());
    }
  };
  public static ActionListener LinkAllImagesAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      OJ.getImageProcessor().linkAllImages();
    }
  };
  public static ActionListener LinkImageFileAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      OpenDialogOJ ooj = new OpenDialogOJ("Link Image", OJ.getData().getDirectory());
      if (ooj.isApproved()) {
        try {
          OJ.getImageProcessor().addImage(ooj.getFilename(), true, true);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  };
  public static ActionListener PropagateScaleAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      OJ.getImageProcessor().propagateScale(-1);
    }
  };
  public static ActionListener FlattenAction = new ActionListener() {

    public void actionPerformed(ActionEvent e) {
      ImagePlus imp =  WindowManager.getCurrentImage();
      if(imp == null)return;
      CustomCanvasOJ myCanvas = new CustomCanvasOJ(imp, OJ.getData(), imp.getTitle());
      myCanvas.makeFlattenedImage();
    }
  };

  /**
   * not used
   */
  public static void copyImageFileToProject(File imageFile) {
    if (OJ.getData().getDirectory() != null) {
      String image_name = imageFile.getName();
      image_name = UtilsOJ.getNextValidImageName(image_name);
      ImagePlus imp = new Opener().openImage(imageFile.getPath());
      if (imp != null) {
        imp.setTitle(image_name);//8.7.2009
        FileSaver fs = new FileSaver(imp);
        fs.saveAsTiff(new File(OJ.getData().getDirectory(), imp.getTitle()).getAbsolutePath());
        OJ.getImageProcessor().addImage(imp);
      }
    }
  }

  public static void copyImageToProject(ImagePlus imp) {
    if (OJ.getData().getDirectory() != null) {
      String image_name = imp.getTitle();
      image_name = UtilsOJ.getNextValidImageName(image_name);
      if (imp != null) {
        imp.setTitle(image_name + UtilsOJ.getFileExtension(imp.getTitle()));
        FileSaver fs = new FileSaver(imp);
        fs.saveAsTiff(new File(OJ.getData().getDirectory(), imp.getTitle()).getAbsolutePath());
        ImageOJ imj = new ImageOJ(image_name, /*imp.getTitle(),*/ imp);//30.6.2012
        OJ.getImageProcessor().addImage(imp);
      }
    }
  }
}