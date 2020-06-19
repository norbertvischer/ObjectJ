/*
 * StatisticsMacroOJ.java
 */
package oj.project.results.statistics;

import ij.macro.Interpreter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import oj.project.ResultsOJ;
import oj.macros.MacroExtStrOJ;

public class StatisticsMacroOJ extends StatisticsAdapterOJ  {

    private static final long serialVersionUID = -3538845213569612843L;
    private String macro;
    private String description;

    public StatisticsMacroOJ() {
    }

    public StatisticsMacroOJ(String name) {
        this.name = name;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(description);
        stream.writeUTF(macro);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        description = stream.readUTF();
        macro = stream.readUTF();
    }

    public String getMacro() {
        return macro;
    }

    public String getDescription() {
        return description;
    }

    public void setMacro(String macro) {
        this.macro = macro;
        changed = true;
    }

    public void setDescription(String description) {
        this.description = description;
        changed = true;
    }

    public double recalculate(String columnName) {
        String ext_macro = preMacroContent(columnName) + macro + postMacroContent(columnName) + MacroExtStrOJ.macroExtensions();

        try {
            Interpreter interp = new Interpreter();
            interp.run(ext_macro);
            return ((ResultsOJ) parent.getParent()).getColumns().getColumnByName(columnName).getStatistics().getStatisticsValueByName(name);
        } catch (Exception e) {
            Interpreter.abort();
            return -99999;
        }
    }

    private String preMacroContent(String columnName) {
        String s = "column = \"" + columnName + "\";";
        return s;
    }

    private String postMacroContent(String columnName) {
        String s = "call(\"oj.macro.MacroOJ.ojSetStatisticValue\",\"" + columnName + "\",\"" + name + "\",value);";
        //define getValue() from column
        return s;
    }

    public static String getHelpText() {
        String s = "//// 'column' variable contains the name of the column \n";
        s = s + "//// 'value' variable contains the return value \n\n\n";
        s = s + "value = 0.0";

        return s;
    }
}
