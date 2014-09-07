/*
 * LocationConverterOJ.java
* -- documented
 *
 * marshals and unmarshals a single LocationConverterOJ (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.LocationOJ;

public class LocationConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(LocationOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        LocationOJ location = (LocationOJ) value;
        writer.addAttribute("x", Float.toString((float)location.getX()));//16.3.2010 save float not double
        writer.addAttribute("y", Float.toString((float)location.getY()));
        writer.addAttribute("z", Float.toString((float)location.getZ()));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        LocationOJ location = new LocationOJ();
        location.setX(Double.parseDouble(reader.getAttribute("x")));
        location.setY(Double.parseDouble(reader.getAttribute("y")));
        location.setZ(Double.parseDouble(reader.getAttribute("z")));
        return location;
    }
}