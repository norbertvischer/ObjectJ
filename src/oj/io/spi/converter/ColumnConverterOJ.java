/*
 * ColumnConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals a ColumnOJ (including its hierarchy)
 */
package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.ColumnDefOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.statistics.StatisticsProxysOJ;

public class ColumnConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ColumnOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ColumnOJ column = (ColumnOJ) value;
        writer.startNode("columnDef");
        context.convertAnother(column.getColumnDef());
        writer.endNode();
        writer.startNode("statistics");
        context.convertAnother(column.getStatistics());
        writer.endNode();
        if (column.isUnlinkedColumn() && (column.getResultCount() > 0)) {
            writer.startNode("rows");
            for (int i = 0; i < column.getResultCount(); i++) {
                writer.startNode("row");
                if (column.getColumnDef().isTextMode()) {
                    writer.addAttribute("value", column.getStringResult(i));
                } else {
                    writer.addAttribute("value", Double.toString(column.getDoubleResult(i)));
                }
                writer.endNode();
            }
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ColumnOJ column = new ColumnOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("statistics".equals(reader.getNodeName())) {
                try {
                    StatisticsProxysOJ statistics = (StatisticsProxysOJ) context.convertAnother(column, StatisticsProxysOJ.class);
                    column.statistics = statistics;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("columnDef".equals(reader.getNodeName())) {
                try {
                    ColumnDefOJ columnDef = (ColumnDefOJ) context.convertAnother(column, ColumnDefOJ.class);
                    column.columnDef = columnDef;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("rows".equals(reader.getNodeName())) {
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    if ("row".equals(reader.getNodeName())) {
                        String value = reader.getAttribute("value");
                        if (column.getColumnDef().isTextMode()) {
                            column.addStringResult(value);
                        } else {
                            try {
                                column.addDoubleResult(Double.parseDouble(value));
                            } catch (Exception ex) {
                                column.addDoubleResult(Double.NaN);
                            }
                        }
                    }
                    reader.moveUp();
                }
            }
            reader.moveUp();
        }
        for (int i = 0; i < column.getStatistics().getStatisticsCount(); i++) {
            column.getStatistics().getStatisticsByIndex(i).setColumnName(column.getName());
        }
        return column;
    }
}