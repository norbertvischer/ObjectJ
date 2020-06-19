/*
 * ScaleDialogOJ.java
 * -- documented
 *
 * First runs ImageJ's ScaleDialog, then checks which image is involved,
 * and forwards this information to the ojj file
 *
 */
package oj.plugin;

import ij.ImagePlus;
import ij.plugin.filter.ScaleDialog;
import ij.process.ImageProcessor;
import java.lang.reflect.Field;
import oj.OJ;
import oj.project.ImageOJ;

public class ScaleDialogOJ extends ScaleDialog {

    public void run(ImageProcessor ip) {
        super.run(ip);
        if (OJ.isProjectOpen) {
            ImageOJ imoj = OJ.getData().getImages().getImageByName(getImagePlus().getTitle());
            if (imoj != null) {
                OJ.getImageProcessor().updateImageProperties(OJ.getData().getDirectory(), imoj);
            }
        }
    }

    private ImagePlus getImagePlus() {
        final Field[] fields = ScaleDialog.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("imp".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return (ImagePlus) fields[i].get((ScaleDialog) this);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
        return null;
    }
}
