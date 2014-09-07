/*
 * ResultsStatisticsActionsOJ.java
 * fully documented
 *
 * used to handle actions from  statistics visibility checkboxes
 */
package oj.gui.results;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import oj.OJ;
import oj.gui.results.ProjectResultsOJ;
import oj.project.results.statistics.StatisticsOJ;

public class StatisticsActionsOJ {

    /** Makes all statistics invisible
     */
    public static Action HideAll = new AbstractAction("Hide All") {

        public void actionPerformed(ActionEvent e) {
            StatisticsOJ statistics = OJ.getData().getResults().getStatistics();
            for (int i = 0; i < statistics.getStatisticsCount(); i++) {
                statistics.getStatisticsByIndex(i).setVisible(false);
            }
            ProjectResultsOJ.getInstance().updateResultsStatisticsView();
        }
    };
    /** Not used yet
     */
    public static Action HideOthers = new AbstractAction("Hide Others") {

        public void actionPerformed(ActionEvent e) {
            StatisticsOJ statistics = OJ.getData().getResults().getStatistics();
            for (int i = 0; i < statistics.getStatisticsCount(); i++) {
                statistics.getStatisticsByIndex(i).setVisible(!statistics.getStatisticsByIndex(i).getVisible());
            }
            ProjectResultsOJ.getInstance().updateResultsStatisticsView();
        }
    };
    /** Makes all statistics visible
     */
    public static Action ShowAll = new AbstractAction("Show All") {

        public void actionPerformed(ActionEvent e) {
            StatisticsOJ statistics = OJ.getData().getResults().getStatistics();
            for (int i = 0; i < statistics.getStatisticsCount(); i++) {
                statistics.getStatisticsByIndex(i).setVisible(true);
            }
            ProjectResultsOJ.getInstance().updateResultsStatisticsView();
        }
    };
}
