/*
 * OperandConverterOJ.java
* -- documented
 *
 * marshals and unmarshals an OperndOJ (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.OperandOJ;

public class OperandConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(OperandOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        OperandOJ operand = (OperandOJ) value;
        writer.addAttribute("itemName", operand.getObjectName());
        writer.startNode("objectClone");
        writer.setValue(Integer.toString(operand.getYtemClone()));
        writer.endNode();
        writer.startNode("relPosition");
        writer.setValue(Integer.toString(operand.getRelPosition()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        OperandOJ operand = new OperandOJ();
        operand.setYtemName(reader.getAttribute("itemName"));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("objectClone".equals(reader.getNodeName())) {
                operand.setYtemClone(Integer.parseInt(reader.getValue()));
            } else if ("relPosition".equals(reader.getNodeName())) {
                operand.setRelPosition(Integer.parseInt(reader.getValue()));
            }
            reader.moveUp();
        }
        return operand;
    }
}