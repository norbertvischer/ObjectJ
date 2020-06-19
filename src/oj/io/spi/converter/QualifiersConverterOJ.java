/*
 * QualifiersConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals the QualifiersOJ array (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.QualifierOJ;
import oj.project.results.QualifiersOJ;

public class QualifiersConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(QualifiersOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        QualifiersOJ qualifiers = (QualifiersOJ) value;
        writer.addAttribute("method", QualifiersOJ.getQualifyMethodName(qualifiers.getQualifyMethod()));
        if (qualifiers.getQualifyInvert()) {
            writer.addAttribute("invert", "true");
        } else {
            writer.addAttribute("invert", "false");
        }
        for (int i = 0; i < qualifiers.getQualifiersCount(); i++) {
            writer.startNode("qualifier");
            context.convertAnother(qualifiers.getQualifierByIndex(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        QualifiersOJ qualifiers = new QualifiersOJ();
        qualifiers.setQualifyMethod(QualifiersOJ.getQualifyMethod(reader.getAttribute("method")), true);
        qualifiers.setQualifyInvert("true".equals(reader.getAttribute("flag")));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("qualifier".equals(reader.getNodeName())) {
                try {
                    QualifierOJ qualifier = (QualifierOJ) context.convertAnother(qualifiers, QualifierOJ.class);
                    qualifiers.addQualifier(qualifier);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return qualifiers;
    }
}