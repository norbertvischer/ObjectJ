/*
 * ColumnsConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals the ColumnsOJ array (including its hierarchy)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;

public class ColumnsConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ColumnsOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ColumnsOJ columns = (ColumnsOJ) value;
        writer.addAttribute("sort", columns.getColumnLinkedSortName());
        writer.addAttribute("flag", ColumnsOJ.getSortFlagName(columns.getColumnLinkedSortFlag()));
        for (int i = 0; i < columns.getAllColumnsCount(); i++) {
            writer.startNode("column");
            context.convertAnother(columns.getColumnByIndex(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ColumnsOJ columns = new ColumnsOJ();
        columns.setColumnLinkedSortName(reader.getAttribute("sort"));
        columns.setColumnLinkedSortFlag(ColumnsOJ.getSortFlag(reader.getAttribute("flag")));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("column".equals(reader.getNodeName())) {
                try {
                    ColumnOJ column = (ColumnOJ) context.convertAnother(columns, ColumnOJ.class);
                    columns.addColumn(column, false);//15.3.2009 -the crucial one, don't initialize
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return columns;
    }
}