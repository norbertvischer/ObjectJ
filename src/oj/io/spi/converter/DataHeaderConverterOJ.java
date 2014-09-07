/*
 * DataHeaderConverterOJ.java
 * -- documented
 *
 *
 * marshals and unmarshals the data header (including version, name, description)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.Date;
import oj.io.DataHeaderOJ;

public class DataHeaderConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(DataHeaderOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DataHeaderOJ header = new DataHeaderOJ();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib_name = reader.getAttributeName(i);
            if (attrib_name.equals("name")) {
                header.setName(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("version")) {
                header.setVersion(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("filename")) {
                header.setFilename(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("updated")) {
            }
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("name".equals(reader.getNodeName())) {
                header.setName(reader.getValue());
            } else if ("version".equals(reader.getNodeName())) {
                header.setVersion(reader.getValue());
            } else if ("filename".equals(reader.getNodeName())) {
                header.setFilename(reader.getValue());
            } else if ("description".equals(reader.getNodeName())) {
                header.setDescription(reader.getValue());
            } else if ("updated".equals(reader.getNodeName())) {
                header.setUpdated((Date) context.convertAnother(reader.getValue(),Date.class));
            }
            reader.moveUp();
        }
        return header;
    }
}