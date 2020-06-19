/*
 * ImagesConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals the ImagesOJ array (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.ImageOJ;
import oj.project.ImagesOJ;

public class ImagesConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ImagesOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ImagesOJ images = (ImagesOJ) value;
        for (int i = 0; i < images.getImagesCount(); i++) {
            writer.startNode("image");
            context.convertAnother(images.getImageByIndex(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ImagesOJ images = new ImagesOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("image".equals(reader.getNodeName())) {
                try {
                    ImageOJ image = (ImageOJ) context.convertAnother(images, ImageOJ.class);
                    images.addImage(image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } 
            reader.moveUp();
        }
        return images;
    }
}