/* ImagePropertiesOJ.java
 * -- documented
 *
 * Replaces ImageJ's ImageProperties.
 *
 * First, ImageJProperties is called, where the user can change scaling, origin, stack organisation, fps etc.
 * Then, ObjectJ's ImageProcessor is called to forward these changes to the ojj file.
 */

package oj.plugin;

import ij.ImagePlus;
import ij.plugin.filter.ImageProperties;
import ij.process.ImageProcessor;
import java.lang.reflect.Field;
import oj.OJ;
import oj.project.ImageOJ;


public class ImagePropertiesOJ extends ImageProperties {
    
    public void run(ImageProcessor ip) {
        super.run(ip);
        if(OJ.isProjectOpen) {
            ImageOJ imoj = OJ.getData().getImages().getImageByName(getImagePlus().getTitle());
            if (imoj != null) {
                OJ.getImageProcessor().updateImageProperties(OJ.getData().getDirectory(), imoj);
            }
        }
    }
    
    private ImagePlus getImagePlus() {
        final Field[] fields = ImageProperties.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ("imp".equals(fields[i].getName())) {
                fields[i].setAccessible(true);
                try {
                    return (ImagePlus) fields[i].get((ImageProperties)this);
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