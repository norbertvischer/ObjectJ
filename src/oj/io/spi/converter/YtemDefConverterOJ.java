/*
 * ObjectDefConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals a ColumnOJ (including its hierarchy)
 */

package oj.io.spi.converter;

/**
 *
 * @author stelian
 */

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.awt.Color;
import oj.project.YtemDefOJ;

public class YtemDefConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(YtemDefOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        YtemDefOJ objectDef = (YtemDefOJ) value;
        writer.addAttribute("name", objectDef.getYtemDefName());
        writer.addAttribute("advance", YtemDefOJ.getAdvanceTypeName(objectDef.getAdvanceType()));
        writer.startNode("markerType");
        writer.setValue(YtemDefOJ.getMarkerTypeName(objectDef.getMarkerType()));
        writer.endNode();
        writer.startNode("objectType");
        writer.setValue(YtemDefOJ.getShapeName(objectDef.getYtemType()));
        writer.endNode();
        writer.startNode("lineType");
        writer.setValue(YtemDefOJ.getLineTypeName(objectDef.getLineType()));
        writer.endNode();
        writer.startNode("lineColor");
        writer.setValue(Integer.toHexString(objectDef.getLineColor().getRGB()).toUpperCase());
        writer.endNode();
        writer.startNode("cloneMin");
        writer.setValue(Integer.toString(objectDef.getCloneMin()));
        writer.endNode();
        writer.startNode("cloneMax");
        writer.setValue(Integer.toString(objectDef.getCloneMax()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        YtemDefOJ ytemDef = new YtemDefOJ();
        ytemDef.setYtemDefName(reader.getAttribute("name"));
        ytemDef.setAdvanceType(YtemDefOJ.getAdvanceType(reader.getAttribute("advance")));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("markerType".equals(reader.getNodeName())) {
                ytemDef.setMarkerType(YtemDefOJ.getMarkerType(reader.getValue()));
            } else if ("objectType".equals(reader.getNodeName())) {
                ytemDef.setYtemDefType(YtemDefOJ.getShapeIndex(reader.getValue()));
            } else if ("lineColor".equals(reader.getNodeName())) {
                ytemDef.setLineColor(new Color((int) Long.parseLong(reader.getValue(),16)));
            } else if ("lineType".equals(reader.getNodeName())) {
                ytemDef.setLineType(YtemDefOJ.getLineType(reader.getValue()));
            } else if ("cloneMin".equals(reader.getNodeName())) {
                ytemDef.setCloneMin(Integer.parseInt(reader.getValue()));
            } else if ("cloneMax".equals(reader.getNodeName())) {
                ytemDef.setCloneMax(Integer.parseInt(reader.getValue()));
            }
            reader.moveUp();
        }
        return ytemDef;
    }
}