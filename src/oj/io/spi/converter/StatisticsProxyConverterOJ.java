/*
 * StatisticsProxyConverterOJ.java
 * -- documented
 *
 * marshals and unmarshals a StatisticsProxyOJ
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.results.statistics.StatisticsProxyOJ;

public class StatisticsProxyConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(StatisticsProxyOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        StatisticsProxyOJ statistic = (StatisticsProxyOJ) value;
        writer.addAttribute("name", statistic.getName());
        writer.addAttribute("value", Double.toString(statistic.getValue()));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String statistic_name = reader.getAttribute("name");
        double statistic_value = Double.parseDouble(reader.getAttribute("value"));
        StatisticsProxyOJ statistic = new StatisticsProxyOJ(statistic_name, null);
        statistic.setValue(statistic_value);
        return statistic;
    }
}