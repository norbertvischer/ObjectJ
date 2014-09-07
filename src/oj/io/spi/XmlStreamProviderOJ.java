/*
 * XMLStreamProviderOJ.java
 * fully documented 19.3.2010
 *
 * one of the two providers
 * provides methods saving/loading project file in XML format
 */
package oj.io.spi;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import ij.IJ;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import oj.util.UtilsOJ;
import oj.project.DataOJ;
import oj.io.DataHeaderOJ;
import oj.io.InputOutputOJ.ProjectIOExceptionOJ;
import oj.io.spi.converter.CellConverterOJ;
import oj.io.spi.converter.CellsConverterOJ;
import oj.io.spi.converter.ColumnConverterOJ;
import oj.io.spi.converter.ColumnDefConverterOJ;
import oj.io.spi.converter.ColumnsConverterOJ;
import oj.io.spi.converter.DataConverterOJ;
import oj.io.spi.converter.DataHeaderConverterOJ;
import oj.io.spi.converter.ImageConverterOJ;
import oj.io.spi.converter.ImagesConverterOJ;
import oj.io.spi.converter.LocationConverterOJ;
import oj.io.spi.converter.YtemDefConverterOJ;
import oj.io.spi.converter.YtemDefsConverterOJ;
import oj.io.spi.converter.OperandConverterOJ;
import oj.io.spi.converter.QualifierConverterOJ;
import oj.io.spi.converter.QualifiersConverterOJ;
import oj.io.spi.converter.ResultsConverterOJ;
import oj.io.spi.converter.StatisticsProxyConverterOJ;
import oj.io.spi.converter.StatisticsProxysConverterOJ;

public class XmlStreamProviderOJ implements IIOProviderOJ {

    private final String name = "xmlstream";
    private final String version = "v1.2";

    private void initProjectXStream(XStream xs) {
        xs.alias("data", oj.project.DataOJ.class);
        xs.alias("image", oj.project.ImageOJ.class);
        xs.alias("macroSet", oj.macros.MacroSetOJ.class);
        xs.alias("object", oj.project.CellOJ.class);
        xs.alias("column", oj.project.results.ColumnOJ.class);
        xs.alias("roi", oj.project.shapes.RoiOJ.class);
        xs.alias("line", oj.project.shapes.LineOJ.class);
        xs.alias("angle", oj.project.shapes.AngleOJ.class);
        xs.alias("point", oj.project.shapes.PointOJ.class);
        xs.alias("polygon", oj.project.shapes.PolygonOJ.class);
        xs.alias("segline", oj.project.shapes.SeglineOJ.class);
        xs.alias("location", oj.project.LocationOJ.class);
        xs.alias("itemDef", oj.project.YtemDefOJ.class);

        xs.alias("statistic", oj.project.results.statistics.StatisticsCountOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsCvOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsMinimumOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsMaximumOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsMeanOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsSumOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsStDevOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsMacroOJ.class);
        xs.alias("statistic", oj.project.results.statistics.StatisticsProxyOJ.class);

        xs.aliasField("itemDefs", oj.project.DataOJ.class, "objectDefs");
        xs.aliasField("objects", oj.project.DataOJ.class, "cells");
        xs.aliasField("items", oj.project.CellOJ.class, "objects");

        xs.addImplicitCollection(oj.project.CellsOJ.class, "cells");
        xs.addImplicitCollection(oj.project.ImagesOJ.class, "images");
        xs.addImplicitCollection(oj.project.YtemOJ.class, "locations");
        xs.addImplicitCollection(oj.project.YtemDefsOJ.class, "objectDefs");
        xs.addImplicitCollection(oj.project.results.ColumnsOJ.class, "columns");
        xs.addImplicitCollection(oj.project.results.statistics.StatisticsOJ.class, "statistics");
        xs.addImplicitCollection(oj.project.results.statistics.StatisticsProxysOJ.class, "statistics");


        xs.registerConverter(new DataConverterOJ());
        xs.registerConverter(new CellConverterOJ());
        xs.registerConverter(new CellsConverterOJ());
        xs.registerConverter(new ImageConverterOJ());
        xs.registerConverter(new ImagesConverterOJ());
        xs.registerConverter(new ColumnConverterOJ());
        xs.registerConverter(new ColumnsConverterOJ());
        xs.registerConverter(new OperandConverterOJ());
        xs.registerConverter(new ResultsConverterOJ());
        xs.registerConverter(new LocationConverterOJ());
        xs.registerConverter(new ColumnDefConverterOJ());
        xs.registerConverter(new YtemDefConverterOJ());
        xs.registerConverter(new QualifierConverterOJ());
        xs.registerConverter(new YtemDefsConverterOJ());
        xs.registerConverter(new QualifiersConverterOJ());
        xs.registerConverter(new StatisticsProxyConverterOJ());
        xs.registerConverter(new StatisticsProxysConverterOJ());
    }

    /**
     * @return name of this provider, i.e. xmlstream
     */
    public String getName() {
        return name;
    }

    /**
     * @return version of this provider (v1.2)
     * version will have to change when switching to more effective roi description
     */
    public String getVersion() {
        return version;
    }

    public DataOJ loadProject(String directory, String filename) throws ProjectIOExceptionOJ {
        DataOJ data = null;
        {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(directory, filename));
                String dataVersion = dataVersion(directory, filename);
                XStream xs = new XStream(new DomDriver());
                initProjectXStream(xs);

                data = (DataOJ) xs.fromXML(fis);

                if (data != null) {
                    data.initAfterUnmarshalling();
                    data.setChanged(false);
                    data.setName(UtilsOJ.stripExtension(filename));
                    data.setDirectory(directory);
                    data.setFilename(filename);
                }
                return data;
            } catch (IOException ex) {
                throw new ProjectIOExceptionOJ("The project cannot be read. Exception: " + ex.getMessage());
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    throw new ProjectIOExceptionOJ(ex.getMessage());
                }

            }
        }
    }

    /**
     * uses an XML string to initialize entire project
     * @return
     */
    public DataOJ loadProjectFromXMLstring(String str) {//18.3.2010
        try {
            XStream xs = new XStream(new DomDriver());
            initProjectXStream(xs);
            DataOJ data = (DataOJ) xs.fromXML(str);





            return data;
        } catch (Exception e) {
            IJ.error("Error loading project (a): " + e.getMessage());
            return null;
        }
    }

    private String dataVersion(
            String directory, String filename) throws ProjectIOExceptionOJ {
        XStream xs = new XStream(new DomDriver());
        xs.alias("data", oj.io.DataHeaderOJ.class);
        xs.registerConverter(
                new DataHeaderConverterOJ());
        try {
            FileInputStream fis = new FileInputStream(new File(directory, filename));
            DataHeaderOJ header = (DataHeaderOJ) xs.fromXML(fis);
            return header.getVersion();
        } catch (Exception e) {
            throw new ProjectIOExceptionOJ("The project version cannot be read because the file is not XML format");
        }
    }

    /**
     * converts the entire project into an XML stream and saves it as .ojj file
     * new version:
     * we zip it together with the macro text.
     */
    public void saveProject(DataOJ data, String directory, String filename) throws ProjectIOExceptionOJ {

        FileOutputStream fos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XStream xs = new XStream(new DomDriver());
            initProjectXStream(xs);
            xs.toXML(data, baos);
            fos = new FileOutputStream(new File(directory, filename));
            baos.writeTo(fos);
            IJ.showStatus("writing project in binary format");

        } catch (IOException ex) {
            throw new ProjectIOExceptionOJ("The project cannot be saved. Exception: " + ex.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                throw new ProjectIOExceptionOJ(ex.getMessage());
            }
        }
        return;

    }

    public boolean isValidData(String directory, String filename) {
        try {
            dataVersion(directory, filename);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
