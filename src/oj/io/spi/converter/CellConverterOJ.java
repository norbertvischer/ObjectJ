/**
 * CellConverter.java
 * -- documented
 * 
 * marshals and unmarshals a cell (including ytems, points)
 */
package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.Properties;
import oj.project.shapes.AngleOJ;
import oj.project.CellOJ;
import oj.project.shapes.LineOJ;
import oj.project.LocationOJ;
import oj.project.YtemOJ;
import oj.project.shapes.PointOJ;
import oj.project.shapes.PolygonOJ;
import oj.project.shapes.RoiOJ;
import oj.project.shapes.SeglineOJ;

public class CellConverterOJ implements Converter {

  public boolean canConvert(Class clazz) {
    return clazz.equals(CellOJ.class);
  }

  public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    CellOJ cell = (CellOJ) value;
    writer.addAttribute("id", Integer.toString(cell.getID()));
    writer.addAttribute("image", cell.getImageName());
    writer.addAttribute("slice", Integer.toString(cell.getStackIndex()));
    if (cell.isQualified()) {
      writer.addAttribute("qualified", "true");
    } else {
      writer.addAttribute("qualified", "false");
    }
    if (cell.getYtemsCount() > 0) {
      writer.startNode("items");
      for (int i = 0; i < cell.getYtemsCount(); i++) {
        YtemOJ ytem = cell.getYtemByIndex(i);
        if (ytem instanceof RoiOJ) {
          writer.startNode("roi");
        } else if (ytem instanceof LineOJ) {
          writer.startNode("line");
        } else if (ytem instanceof AngleOJ) {
          writer.startNode("angle");
        } else if (ytem instanceof PointOJ) {
          writer.startNode("point");
        } else if (ytem instanceof PolygonOJ) {
          writer.startNode("polygon");
        } else if (ytem instanceof SeglineOJ) {
          writer.startNode("segline");
        }
        writer.addAttribute("slice", Integer.toString(ytem.getStackIndex()));
        writer.addAttribute("definition", ytem.getYtemDef());
        for (int j = 0; j < ytem.getLocationsCount(); j++) {
          writer.startNode("location");
          context.convertAnother(ytem.getLocation(j));
          writer.endNode();
        }



        //10.4.2010 test
//        writer.startNode("roipoints");
//        writer.addAttribute("dots", " 3 22 -34 22 44 12 32 21 32 34 -5 88 44 19");
//        writer.endNode();
//        writer.startNode("roipoints");
//        writer.addAttribute("dots", " 22 44 12 32 21 32 34 -5 88 3 22 -34 -24 -77");
//        writer.endNode();




        writer.endNode();
      }
      writer.endNode();
    }
    if (!cell.properties.isEmpty()) {
      writer.startNode("properties");
      context.convertAnother(cell.properties);
      writer.endNode();
    }
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    CellOJ cell = new CellOJ();
    String id = reader.getAttribute("id");
    if ((id != null) && (!id.equals(""))) {
      cell.setID(Integer.parseInt(reader.getAttribute("id")));
    }
    cell.setImageName(reader.getAttribute("image"));
    cell.setStackIndex(Integer.parseInt(reader.getAttribute("slice")));
    String qStr = reader.getAttribute("qualified");
    boolean qq = (qStr == null || qStr.equals("true"));//15.3.2010//missing = true
    cell.setQualified(qq);
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      if ("items".equals(reader.getNodeName())) {
        while (reader.hasMoreChildren()) {
          reader.moveDown();
          try {
            YtemOJ ytem = null;

            if ("roi".equals(reader.getNodeName())) {
              ytem = new RoiOJ();
            } else if ("line".equals(reader.getNodeName())) {
              ytem = new LineOJ();
            } else if ("angle".equals(reader.getNodeName())) {
              ytem = new AngleOJ();
            } else if ("point".equals(reader.getNodeName())) {
              ytem = new PointOJ();
            } else if ("polygon".equals(reader.getNodeName())) {
              ytem = new PolygonOJ();
            } else if ("segline".equals(reader.getNodeName())) {
              ytem = new SeglineOJ();
            }
            int imageSlice = 0;
            try {
              imageSlice = Integer.parseInt(reader.getAttribute("slice"));
            } catch (Exception ex) {
              imageSlice = cell.getStackIndex();
            }
            ytem.setStackIndex(imageSlice);
            ytem.setObjectDef(reader.getAttribute("definition"));

            if (ytem != null) {
              while (reader.hasMoreChildren()) {
                reader.moveDown();
                if ("location".equals(reader.getNodeName())) {
                  try {
                    LocationOJ location = (LocationOJ) context.convertAnother(ytem, LocationOJ.class);
                    ytem.add(location);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }

                reader.moveUp();
              }
              cell.add(ytem);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          reader.moveUp();
        }
      } else if ("properties".equals(reader.getNodeName())) {
        try {
          cell.properties = (Properties) context.convertAnother(cell, Properties.class);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      reader.moveUp();

    }
    return cell;
  }
}
