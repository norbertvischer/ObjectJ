/*
 * StatisticsProxysConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals a the StatisticsProxysOJ array
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.statistics.StatisticsProxyOJ;
import oj.project.results.statistics.StatisticsProxysOJ;

public class StatisticsProxysConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(StatisticsProxysOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        StatisticsProxysOJ statistics = (StatisticsProxysOJ) value;
        for (int i = 0; i < statistics.getStatisticsCount(); i++) {
            writer.startNode("statistic");
            context.convertAnother(statistics.getStatisticsByIndex(i));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        StatisticsProxysOJ statistics = new StatisticsProxysOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("statistic".equals(reader.getNodeName())) {
                try {
                    StatisticsProxyOJ statistic = (StatisticsProxyOJ) context.convertAnother(statistics, StatisticsProxyOJ.class);
                    statistics.addStatistic(statistic);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            reader.moveUp();
        }
        return statistics;
    }

}
