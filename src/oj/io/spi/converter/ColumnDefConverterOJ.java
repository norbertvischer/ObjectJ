/*
 * ColumnDefConverter.java
 * -- documented
 *
 * marshals and unmarshals a ColumnDefOJ (including its hierarchy)
 */
package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.awt.Color;
import oj.project.results.ColumnDefOJ;
import oj.project.results.OperandOJ;

public class ColumnDefConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ColumnDefOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ColumnDefOJ columnDef = (ColumnDefOJ) value;
        writer.addAttribute("name", columnDef.getName());
        writer.addAttribute("type", ColumnDefOJ.getAlgorithmName(columnDef.getAlgorithm()));
        writer.addAttribute("hidden", Boolean.toString(columnDef.isHidden()));
        writer.startNode("columnColor");
        writer.setValue(Integer.toHexString(columnDef.getColumnColor().getRGB()).toUpperCase());
        writer.endNode();
        writer.startNode("columnDigits");
        writer.setValue(Integer.toString(columnDef.getColumnDigits()));
        writer.endNode();
        writer.startNode("columnWidth");
        writer.setValue(Integer.toString(columnDef.getColumnWidth()));
        writer.endNode();

        writer.startNode("histoBinWidth");//24.10.2010
        writer.setValue(Double.toString(columnDef.getHistoBinWidth()));
        writer.endNode();
        writer.startNode("histoXMin");
        writer.setValue(Double.toString(columnDef.getHistoXMin()));
        writer.endNode();
        writer.startNode("histoXMax");
        writer.setValue(Double.toString(columnDef.getHistoXMax()));
        writer.endNode();
        writer.startNode("histoYMax");
        writer.setValue(Integer.toString(columnDef.getHistoYMax()));
        writer.endNode();

        writer.startNode("plotProperties");
        writer.setValue(columnDef.getPlotProperties());
        writer.endNode();

        if (columnDef.getOperandCount() > 0) {
            writer.startNode("operands");
            for (int i = 0; i < columnDef.getOperandCount(); i++) {
                writer.startNode("operand");
                context.convertAnother(columnDef.getOperand(i));
                writer.endNode();
            }
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ColumnDefOJ columnDef = new ColumnDefOJ();
        columnDef.setName(reader.getAttribute("name"));
        String type = reader.getAttribute("type");
        columnDef.setAlgorithm(ColumnDefOJ.getAlgorithm(columnDef.getName(), type));
        columnDef.setHidden(Boolean.parseBoolean(reader.getAttribute("hidden")));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("columnColor".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setColumnColor(new Color((int) Long.parseLong(reader.getValue(), 16)));
            } else if ("columnDigits".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setColumnDigits(Integer.parseInt(reader.getValue()));
            } else if ("histoXMin".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setHistoXMin(Double.parseDouble(reader.getValue()));
            } else if ("histoXMax".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setHistoXMax(Double.parseDouble(reader.getValue()));
            } else if ("histoBinWidth".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setHistoBinWidth(Double.parseDouble(reader.getValue()));
            } else if ("histoYMax".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setHistoYMax(Integer.parseInt(reader.getValue()));
            } else if ("columnWidth".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setColumnWidth(Integer.parseInt(reader.getValue()));
            } else if ("plotProperties".equalsIgnoreCase(reader.getNodeName())) {
                columnDef.setPlotProperties(reader.getValue());
            } else if ("operands".equalsIgnoreCase(reader.getNodeName())) {
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    if ("operand".equalsIgnoreCase(reader.getNodeName())) {
                        try {
                            OperandOJ operand = (OperandOJ) context.convertAnother(columnDef, OperandOJ.class);
                            columnDef.addOperand(operand);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    reader.moveUp();
                }
            }
            reader.moveUp();
        }
        return columnDef;
    }
}