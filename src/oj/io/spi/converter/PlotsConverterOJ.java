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
import java.util.ArrayList;
import oj.project.results.PlotsOJ;

public class PlotsConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(PlotsOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        PlotsOJ plots = (PlotsOJ) value;
        ArrayList plotDefs = plots.getPlotDefs();
        for (int i = 0; i < plots.getPlotDefs().size(); i++) {
            writer.startNode("plotdef");
            context.convertAnother(plotDefs.get(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PlotsOJ plots = new PlotsOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("plotdef".equals(reader.getNodeName())) {
                try {
                    plots.plotDefs.add((String)( context.convertAnother(plots, PlotsOJ.class)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return plots;
    }
}