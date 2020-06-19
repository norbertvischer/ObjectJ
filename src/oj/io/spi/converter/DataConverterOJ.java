/*
 * DataConverterOJ.java
 * -- documented
 *
 *
 * marshals and unmarshals entire data structure (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.CellsOJ;
import oj.project.DataOJ;
import oj.project.ImagesOJ;
import oj.project.YtemDefsOJ;
import oj.project.ResultsOJ;

public class DataConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(DataOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        DataOJ data = (DataOJ) value;
        writer.addAttribute("name", data.getName());
        writer.addAttribute("version", data.getVersion());
        writer.addAttribute("updated", data.getUpdated().toString());
        writer.startNode("description");
        writer.setValue(data.getDescription());
        writer.endNode();
        writer.startNode("objects");
        context.convertAnother(data.getCells());
        writer.endNode();
        writer.startNode("images");
        context.convertAnother(data.getImages());
        writer.endNode();
        writer.startNode("results");
        context.convertAnother(data.getResults());
        writer.endNode();
        writer.startNode("itemDefs");
        context.convertAnother(data.getYtemDefs());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DataOJ data = new DataOJ();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib_name = reader.getAttributeName(i);
            if (attrib_name.equals("name")) {
                data.setName(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("version")) {
                data.setVersion(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("filename")) {
                data.setFilename(reader.getAttribute(attrib_name));
            } else if (attrib_name.equals("updated")) {
            }
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("description".equals(reader.getNodeName())) {
                data.setDescription(reader.getValue());
            } else if ("objects".equals(reader.getNodeName())) {
                try {
                    CellsOJ cells = (CellsOJ) context.convertAnother(data, CellsOJ.class);
                    data.cells = cells;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("images".equals(reader.getNodeName())) {
                try {
                    ImagesOJ images = (ImagesOJ) context.convertAnother(data, ImagesOJ.class);
                    data.images = images;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("macros".equals(reader.getNodeName())) {

            } else if ("results".equals(reader.getNodeName())) {
                try {
                    ResultsOJ results = (ResultsOJ) context.convertAnother(data, ResultsOJ.class);
                    data.results = results;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("itemDefs".equals(reader.getNodeName())) {
                try {
                    YtemDefsOJ ytemDefs = (YtemDefsOJ) context.convertAnother(data, YtemDefsOJ.class);
                    data.ytemDefs = ytemDefs;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return data;
    }
}