
package oj.io.spi.converter;


import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.YtemDefOJ;
import oj.project.YtemDefsOJ;

public class YtemDefsConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(YtemDefsOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        YtemDefsOJ ytemDefs = (YtemDefsOJ) value;
        if (ytemDefs.isComposite()) {
            writer.addAttribute("multiCollect", "true");
        } else {
            writer.addAttribute("multiCollect", "false");
        }
        if (ytemDefs.is3DYtems()) {
            writer.addAttribute("threeDObjects", "true");
        } else {
            writer.addAttribute("threeDObjects", "false");
        }
        if (ytemDefs.getShowCellNumber()) {
            writer.addAttribute("showCellNumber", "true");
        } else {
            writer.addAttribute("showCellNumber", "false");
        }
        if (ytemDefs.getYtemDefsCount() > 0) {
            for (int i = 0; i < ytemDefs.getYtemDefsCount(); i++) {
                writer.startNode("itemDef");
                context.convertAnother(ytemDefs.getYtemDefByIndex(i));
                writer.endNode();
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        YtemDefsOJ ytemDefs = new YtemDefsOJ();
        ytemDefs.setComposite("true".equals(reader.getAttribute("multiCollect")));
        ytemDefs.set3DYtems("true".equals(reader.getAttribute("threeDObjects")));
        ytemDefs.setShowCellNumber("true".equals(reader.getAttribute("showCellNumber")));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("itemDef".equals(reader.getNodeName())) {
                try {
                    YtemDefOJ ytemDef = (YtemDefOJ) context.convertAnother(ytemDefs, YtemDefOJ.class);
                    ytemDefs.addYtemDef(ytemDef);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return ytemDefs;
    }
}