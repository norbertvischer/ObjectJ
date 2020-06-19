/*
 * CellsConverterOJ.java
 * -- documented
 *
 * 
 * marshals and unmarshals a cellsOJ (including cells, ytems, points)
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.CellOJ;
import oj.project.CellsOJ;

public class CellsConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(CellsOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        CellsOJ cells = (CellsOJ) value;
        for (int i = 0; i < cells.getCellsCount(); i++) {
            writer.startNode("object");
            context.convertAnother(cells.getCellByIndex(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        CellsOJ cells = new CellsOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("object".equals(reader.getNodeName())) {
                try {
                    CellOJ cell = (CellOJ) context.convertAnother(cells, CellOJ.class);
                    cells.addCell(cell);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } 
            reader.moveUp();
        }
        return cells;
    }
}