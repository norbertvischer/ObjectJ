/*
 * QualifierConverterOJ.java
* -- documented
 *
 * marshals and unmarshals a singel QualifierOJ (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.QualifierOJ;

public class QualifierConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(QualifierOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        QualifierOJ qualifier = (QualifierOJ) value;
        writer.addAttribute("column", qualifier.getColumnName());
        writer.startNode("operation");
        writer.setValue(QualifierOJ.getOperationName(qualifier.getOperation()));
        writer.endNode();
        writer.startNode("firstValue");
        writer.setValue(qualifier.getFirstStringValue());
        writer.endNode();
        writer.startNode("secondValue");
        writer.setValue(qualifier.getSecondStringValue());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        QualifierOJ qualifier = new QualifierOJ();
        qualifier.setColumnName(reader.getAttribute("column"));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("operation".equals(reader.getNodeName())) {
                qualifier.setOperation(QualifierOJ.getOperation(reader.getValue()));
            } else if ("firstValue".equals(reader.getNodeName())) {
                qualifier.setFirstStringValue(reader.getValue());
            } else if ("secondValue".equals(reader.getNodeName())) {
                qualifier.setSecondStringValue(reader.getValue());
            }
            reader.moveUp();
        }
        return qualifier;
    }
}