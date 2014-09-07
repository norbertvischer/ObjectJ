/*
 * ImageConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals one ImageOJ (including its hierarchy)
 */
package oj.io.spi.converter;


import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.ImageOJ;

public class ImageConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ImageOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ImageOJ image = (ImageOJ) value;
        writer.addAttribute("name", image.getName());
        writer.addAttribute("file", image.getName());//write for compatibility reasons, but don't read 8.3.2010
        writer.startNode("description");
        writer.setValue(image.getDescription());
        writer.endNode();
        writer.startNode("objects");
        writer.addAttribute("first", Integer.toString(image.getFirstCell() + 1));
        writer.addAttribute("last", Integer.toString(image.getLastCell() + 1));
        writer.endNode();
        writer.startNode("size");
        writer.addAttribute("x", Integer.toString(image.getWidth()));
        writer.addAttribute("y", Integer.toString(image.getHeight()));
        writer.addAttribute("z", Integer.toString(image.getNumberOfSlices()));
        writer.addAttribute("f", Integer.toString(image.getNumberOfFrames()));
        writer.addAttribute("c", Integer.toString(image.getNumberOfChannels()));
        writer.endNode();
        writer.startNode("voxel");
        writer.addAttribute("x", Double.toString(image.getVoxelSizeX()));
        writer.addAttribute("y", Double.toString(image.getVoxelSizeY()));
        writer.addAttribute("z", Double.toString(image.getVoxelSizeZ()));
        writer.endNode();
        writer.startNode("unit");
        writer.addAttribute("x", image.getVoxelUnitX());
        writer.addAttribute("y", image.getVoxelUnitY());
        writer.addAttribute("z", image.getVoxelUnitZ());
        writer.endNode();
        writer.startNode("frame");
        writer.addAttribute("interval", Double.toString(image.getFrameInterval()));
        writer.addAttribute("unit", image.getFrameRateUnit());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ImageOJ image = new ImageOJ();
        image.setName(reader.getAttribute("name"));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("image".equals(reader.getNodeName())) {
                //image.setDescription(reader.getValue());
            } else if ("objects".equals(reader.getNodeName())) {
//                image.setFirstCell(Integer.parseInt(reader.getAttribute("first")) - 1);
//                image.setLastCell(Integer.parseInt(reader.getAttribute("last")) - 1);
            } else if ("size".equals(reader.getNodeName())) {
                image.setWidth(Integer.parseInt(reader.getAttribute("x")));
                image.setHeight(Integer.parseInt(reader.getAttribute("y")));
                if (reader.getAttribute("z") != null) {
                    image.setNumberOfSlices(Integer.parseInt(reader.getAttribute("z")));
                }
                if (reader.getAttribute("f") != null) {
                    image.setNumberOfFrames(Integer.parseInt(reader.getAttribute("f")));
                }
                if (reader.getAttribute("c") != null) {
                    image.setNumberOfChannels(Integer.parseInt(reader.getAttribute("c")));
                }
            } else if ("voxel".equals(reader.getNodeName())) {
                image.setVoxelSizeX(Double.parseDouble(reader.getAttribute("x")));
                image.setVoxelSizeY(Double.parseDouble(reader.getAttribute("y")));
                image.setVoxelSizeZ(Double.parseDouble(reader.getAttribute("z")));
            } else if ("unit".equals(reader.getNodeName())) {
                image.setVoxelUnitX(reader.getAttribute("x"));
                image.setVoxelUnitY(reader.getAttribute("y"));
                image.setVoxelUnitZ(reader.getAttribute("z"));
            } else if ("frame".equals(reader.getNodeName())) {
                image.setFrameInterval((int) Double.parseDouble(reader.getAttribute("interval")));
                image.setFrameRateUnit(reader.getAttribute("unit"));
            }
            
            reader.moveUp();
        }
        return image;
    }
}