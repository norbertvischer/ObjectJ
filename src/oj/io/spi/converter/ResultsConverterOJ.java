/*
 * ResultsConverter.java
 * -- documented
 *
 * marshals and unmarshals entire linked and unlinked results including statistics.
 */

package oj.io.spi.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import oj.project.ResultsOJ;
import oj.project.results.ColumnsOJ;
import oj.project.results.PlotsOJ;
import oj.project.results.QualifiersOJ;
import oj.project.results.statistics.IStatisticsOJ;
import oj.project.results.statistics.StatisticsMacroOJ;
import oj.project.results.statistics.StatisticsOJ;

public class ResultsConverterOJ implements Converter {

    public boolean canConvert(Class clazz) {
        return clazz.equals(ResultsOJ.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ResultsOJ results = (ResultsOJ) value;
        if ((results.getStatistics() != null) && shouldSave(results.getStatistics())) {
            writer.startNode("statistics");
            for (int i = 0; i < results.getStatistics().getStatisticsCount(); i++) {
                IStatisticsOJ statistic = results.getStatistics().getStatisticsByIndex(i);
                if (statistic.getClass() == StatisticsMacroOJ.class) {
                    writer.startNode("statistic");
                    writer.addAttribute("name", statistic.getName());
                    if (statistic.getEnabled()) {
                        writer.addAttribute("enabled", "true");
                    } else {
                        writer.addAttribute("enabled", "false");
                    }
                    writer.setValue(((StatisticsMacroOJ) statistic).getMacro());
                    writer.endNode();
                } else {
                    if (!statistic.getEnabled()) {
                        writer.startNode("statistic");
                        writer.addAttribute("name", statistic.getName());
                        writer.addAttribute("enabled", "false");
                        writer.endNode();
                    }
                }
            }
            writer.endNode();
        }
        if (results.getColumns() != null) {
            writer.startNode("columns");
            context.convertAnother(results.getColumns());
            writer.endNode();
        }
        if (results.getQualifiers() != null) {
            writer.startNode("qualifiers");
            context.convertAnother(results.getQualifiers());
            writer.endNode();
        }
        
        if (results.getPlots() != null) {
            writer.startNode("plots");
            context.convertAnother(results.getPlots());
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ResultsOJ results = new ResultsOJ();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("statistics".equals(reader.getNodeName())) {
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    String statistic_name = null;
                    boolean statistic_enabled = false;
                    BooleanConverter bc = new BooleanConverter();
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String attrib_name = reader.getAttributeName(i);
                        if (attrib_name.equals("name")) {
                            statistic_name = reader.getAttribute(attrib_name);
                        } else if (attrib_name.equals("enabled")) {
                            String enabled = reader.getAttribute(attrib_name);
                            statistic_enabled = "true".equals(enabled);
                        }
                    }

                    if (statistic_name != null) {
                        if ("Minimum".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("Minimum");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("Maximum".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("Maximum");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("Sum".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("Sum");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("SumOfSquares".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("SumOfSquares");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("Mean".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("Mean");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("StDev".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("StDev");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else if ("Cv".equals(reader.getNodeName())) {
                            IStatisticsOJ statistic = results.getStatistics().getStatisticsByName("Cv");
                            if (statistic != null) {
                                statistic.setEnabled(statistic_enabled);
                            }
                        } else {
                            StatisticsMacroOJ statistic_macro = new StatisticsMacroOJ();
                            statistic_macro.setName(reader.getAttribute("name"));
                            statistic_macro.setEnabled(statistic_enabled);
                            statistic_macro.setMacro(reader.getValue());
                            results.getStatistics().addStatistics(statistic_macro);
                        }
                    }
                }
                reader.moveUp();
            } else if ("columns".equals(reader.getNodeName())) {
                try {
                    ColumnsOJ columns = (ColumnsOJ) context.convertAnother(results, ColumnsOJ.class);
                    results.columns = columns;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("qualifiers".equals(reader.getNodeName())) {
                try {
                    QualifiersOJ qualifiers = (QualifiersOJ) context.convertAnother(results, QualifiersOJ.class);
                    results.qualifiers = qualifiers;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            else if ("plots".equals(reader.getNodeName())) {
                try {
                    PlotsOJ plots = (PlotsOJ) context.convertAnother(results, PlotsOJ.class);
                    results.plots = plots;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            
            reader.moveUp();
        }
        return results;
    }

    private boolean shouldSave(StatisticsOJ statistics) {
        for (int i = 0; i < statistics.getStatisticsCount(); i++) {
            IStatisticsOJ statistic = statistics.getStatisticsByIndex(i);
            if (statistic.getClass() == StatisticsMacroOJ.class) {
                return true;
            } else if (!statistic.getEnabled()) {
                return true;
            }
        }
        return false;
    }
}