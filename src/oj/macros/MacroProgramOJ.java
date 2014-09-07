package oj.macros;

import ij.macro.Interpreter;
import ij.macro.MacroConstants;
import ij.macro.Program;
import ij.macro.Symbol;
import ij.macro.Tokenizer;
import java.util.StringTokenizer;
import oj.gui.MenuManagerOJ;

public class MacroProgramOJ implements MacroConstants {

    private Program pgm;
    private String[] macroNames;
    private int[] macroStarts;

    public Program getProgram() {
        return pgm;
    }

    public int getMacroStarts(String name) {
        int index = indexOfMacro(name);
        if (index >= 0) {
            return macroStarts[index];
        }
        return 0;
    }

    private int indexOfMacro(String name) {
        for (int i = 0; i < macroNames.length; i++) {
            if (macroNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void install(String macroText) {
        if (macroText != null) {
            Tokenizer tok = new Tokenizer();
            pgm = tok.tokenize(macroText);
        }
    }

    public void install(String macroText, int macroCount) {
        MenuManagerOJ.getInstance().clearSubMenus();

        macroNames = new String[macroCount];
        macroStarts = new int[macroCount];

        if (macroText != null) {
            Tokenizer tok = new Tokenizer();
            pgm = tok.tokenize(macroText);
        }
        int[] code = pgm.getCode();

        Symbol[] symbolTable = pgm.getSymbolTable();

        int address;
        int token;
        int nextToken;
        int count = 0;
        Symbol symbol;
        String name;

        if (pgm.hasVars() && pgm.macroCount() > 0 && pgm.getGlobals() == null) {
            new Interpreter().saveGlobals(pgm);
        }
        for (int i = 0; i < code.length; i++) {
            token = code[i] & TOK_MASK;
            if (token == MACRO) {
                nextToken = code[i + 1] & TOK_MASK;
                if (nextToken == STRING_CONSTANT) {
                    address = code[i + 1] >> TOK_SHIFT;
                    symbol = symbolTable[address];
                    name = extractMacroName(symbol.str);
                    if (!name.startsWith("Unused")) {
                        macroStarts[count] = i + 2;
                        macroNames[count] = name;
                        count++;
                    }
                }
            } else if (token == EOF) {
                break;
            }
        }
    }

    private String extractMacroName(String macroExtName) {
        
        int toolIndex = macroExtName.indexOf("Tool");//25.9.2013
        int minusIndex = macroExtName.lastIndexOf("-");
        
        if (toolIndex > 0 && minusIndex > toolIndex) {
            return macroExtName.substring(0, toolIndex + 4);
        } else {
            StringTokenizer st = new StringTokenizer(macroExtName, "[", true);
            if (st.hasMoreTokens()) {
                return st.nextToken().trim();
            }
        }
        return null;
    }
}
