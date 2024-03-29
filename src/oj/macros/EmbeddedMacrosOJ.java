package oj.macros;

import ij.IJ;
import ij.ImageJ;
import ij.WindowManager;
import ij.gui.Toolbar;
import ij.macro.Interpreter;
import ij.plugin.frame.Editor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import oj.OJ;
import oj.gui.tools.ToolManagerOJ;
import oj.processor.events.MacroChangedEventOJ;
import oj.project.DataOJ;
import oj.util.UtilsOJ;

public class EmbeddedMacrosOJ {

	static boolean showPopUp = false;
	static JPopupMenu macrosPopup = null;
	private static EmbeddedMacrosOJ instance;

	public static EmbeddedMacrosOJ getInstance() {
		if (instance == null) {
			instance = new EmbeddedMacrosOJ();
		}
		return instance;
	}
	public ActionListener LoadEmbeddedMacroAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Editor ed = OJ.editor;

			String theText = ed.getText();
			theText = UtilsOJ.fixLineFeeds(theText);//7.9.2010
			OJ.getData().setLinkedMacroText(theText);
			int caretPos = ed.getTextArea().getCaretPosition();
			ed.getTextArea().setText(theText);
			ed.getTextArea().setCaretPosition(caretPos);

			doInstall(theText);
			setEditorUnchanged(ed);
			ij.IJ.getInstance().setVisible(true);
		}
	};

	public ActionListener CancelPlotAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Editor ed = OJ.editor;
			ed.close();
		}
	};
	public ActionListener PreviewPlotAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Editor ed = OJ.editor;
			String theText = ed.getText();
			theText = UtilsOJ.fixLineFeeds(theText);
			IJ.runMacro(theText);
			ij.IJ.getInstance().setVisible(true);
		}
	};

	public ActionListener ConfirmPlotAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Editor ed = OJ.editor;
			String theText = ed.getText();
			theText = UtilsOJ.fixLineFeeds(theText);
			setEditorUnchanged(ed);
			ed.close();
		}

	};

	public EmbeddedMacrosOJ() {
		instance = this;
	}

	public void showEmbeddedMacros(int modifier) {

		showPopUp = ((modifier & KeyEvent.ALT_MASK) != 0);
		String macros_text = OJ.getData().getLinkedMacroText();
		if (macros_text == null) {
			macros_text = "";
		}
		Window theWindow = OJ.editorWindow;
		if (theWindow != null && theWindow.isShowing() && OJ.editor != null) {
			theWindow.setVisible(true);
			return;
		}

		String version = IJ.getFullVersion();
		Editor ed;
		if (version.compareToIgnoreCase("1.49i03") >= 0) {
			ed = new EditorOJ(16, 60, 0, Editor.MONOSPACED + Editor.MENU_BAR);
		} else {
			ed = new Editor(16, 60, 0, Editor.MONOSPACED + Editor.MENU_BAR);
		}
		ed.create("Embedded Macros", macros_text);
		boolean lessThan = ImageJ.VERSION.compareTo("1.52u")<0;
		if(!lessThan){
			String msg = "Embedded macros can only be installed \nvia button: \n'Install in ObjectJ menu'" ; 
			ed.setRejectMacrosMsg(msg); 
		}
		JButton loadButton = new JButton("Install in ObjectJ menu");
		loadButton.addActionListener(LoadEmbeddedMacroAction);

		TextArea ta = ed.getTextArea();
		ed.remove(ta);
		ed.setLayout(new BorderLayout());
		JPanel panel1 = new JPanel();

		panel1.setLayout(new FlowLayout());
		loadButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		panel1.add(loadButton);

		JLabel myLabel = new JLabel("Macros Overview");

		if (showPopUp) {
			panel1.add(myLabel);
		}
		myLabel.setForeground(Color.blue);
		myLabel.setAutoscrolls(true);

		macrosPopup = new javax.swing.JPopupMenu();

		myLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {

				refreshPopupItems();
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if ((evt.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
					ij.IJ.showStatus("Right-Click to navigate through macros"); //                            + "\n-"
				}                //                            + "\nYou also can enter bookmark tags into the macro text:"
				//                            + "\n //<<   bookmarks left part of line"
				//                            + "\n //>>   bookmarks right part of line");
			}
		});

		myLabel.setComponentPopupMenu(macrosPopup);

		ed.add(BorderLayout.NORTH, panel1);
		ed.add(BorderLayout.CENTER, ta);

		Font monoFont = new Font("Monospaced", Font.PLAIN, 14);
		ta.setFont(monoFont);
		OJ.editor = ed;
		Frame[] frames = WindowManager.getNonImageWindows();
		Frame frame = frames[frames.length - 1];
		//ij.IJ.log(WindowManager.getFrontWindow().getTitle());
		//ij.IJ.log(frame.getTitle());//"Embedded Macros"
		//ij.IJ.log("---");//"Embedded Macros"
		OJ.editorWindow = WindowManager.getFrontWindow();
		refreshPopupItems();
		loadButton.transferFocus();
	}

	public void refreshPopupItems() {
		if (!showPopUp) {
			return;
		}
		macrosPopup.removeAll();
		TextArea ta = OJ.editor.getTextArea();
		String macros_text = ta.getText();
		int caretPos = ta.getCaretPosition();
		String menuStrings = UtilsOJ.extractFunctions(macros_text, caretPos);
		String[] lines = menuStrings.split("\n");
		Font theFont = new java.awt.Font("MS Sans Serif", 0, 12); // NOI18N

		for (int jj = 0; jj < lines.length; jj++) {
			String line = lines[jj];
			if (line.length() > 5) {
				int lineNo = Integer.parseInt(line.substring(0, 5).trim());
				char kind = line.charAt(6);
				char caret = line.charAt(7);
				String title = line.substring(8, line.length());
				Color color = new Color(0, 0, 255);

				if (kind == 'f') {
					color = new Color(0, 100, 0);
					title = "    " + title;
				}
				if (kind == 'b') {
					color = new Color(200, 0, 80);
					title = "    " + title;
				}

				JMenuItem thisItem = new javax.swing.JMenuItem(title);
				thisItem.setAlignmentX((float) lineNo / 1000000);
				thisItem.setForeground(color);
				if (caret == '*') {
					thisItem.setBackground(new Color(255, 255, 188));
				}
				thisItem.setFont(theFont);
				macrosPopup.add(thisItem);
				thisItem.addActionListener(itemAction);
			}
		}
	}

	public void loadEmbeddedMacros() {
		boolean altDown = IJ.altKeyDown();
		boolean shiftDown = IJ.shiftKeyDown();
		if (shiftDown) {
			shiftDown = shiftDown && true;
		}
		if (IJ.debugMode) {
			IJ.log("alt=" + altDown + "   shift=" + shiftDown);
		}
		String macros_text = null;
		if (OJ.isProjectOpen) {
			DataOJ data = OJ.getData();
			//String project_name = data.getName();
			macros_text = OJ.getData().getLinkedMacroText();//18.3.2010
			if (macros_text == null)
				return;
			OJ.getData().setLinkedMacroText(macros_text);
			doInstall(macros_text);
			Editor ed = OJ.editor;
			if(ed != null && ed.getTextArea() !=null)
				ed.getTextArea().setCaretPosition(0);

		}
	}

	public void doInstall(String macros_text) {//Normal Load Project Macros  

		Interpreter intp = Interpreter.getInstance();
		if (intp != null) {
			Interpreter.getInstance().abortMacro();//11.8.2013
		}
		OJ.initMacroProcessor();//4.7.2013
		macros_text = UtilsOJ.fixLineFeeds(macros_text);
		String clean_macro_text = UtilsOJ.maskComments(macros_text);
		clean_macro_text = clean_macro_text.replaceAll("macro\"", "macro \"");//19.10.2010
		if (clean_macro_text == null) {
			clean_macro_text = "";//21.1.2018
		}

		boolean leadingPart = !clean_macro_text.startsWith("macro");
		String[] macros = clean_macro_text.split("macro ");//
		int numMacros = macros.length;
		if (leadingPart) {
			numMacros--;
		}
		String project_name = OJ.getData().getName();
		MacroSetOJ macroSet = new MacroSetOJ();
		macroSet.setName(project_name);

		macroSet.installText(macros_text);
		OJ.getData().setMacroSet(macroSet);
		OJ.getEventProcessor().fireMacroChangedEvent(project_name, MacroChangedEventOJ.MACROSET_EDITED);
		ij.IJ.showStatus("" + numMacros + " embedded macros have been loaded");
		ToolManagerOJ.getInstance().selectTool("");
		Toolbar.getInstance().setTool(0);
		ToolManagerOJ.getInstance().reload();
	

		//setEditorUnchanged(OJ.editor);

	}
	public ActionListener itemAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if ((e.getSource() instanceof JMenuItem)) {
				JMenuItem item = (JMenuItem) e.getSource();
				int lineNumber = Math.round(item.getAlignmentX() * 1000000);
				TextArea ta = OJ.editor.getTextArea();
				String txt = ta.getText();
				int lines = 0;
				int selBegin = 0;
				int selEnd = 0;
				for (int charPos = 0; charPos < txt.length(); charPos++) {
					if (txt.charAt(charPos) == '\n') {
						lines++;
						if (lines == (lineNumber + 1)) {
							selEnd = charPos;
							break;
						}
						selBegin = charPos;
					}
				}
				ij.IJ.showStatus("" + lineNumber);
				ta.select(selBegin + 1, selEnd + 1);
				IJ.wait(500);
				ta.setCaretPosition(selBegin + 1);
				ta.setVisible(true);
				ta.requestFocus();
				OJ.editor.setIsMacroWindow(true);
			}
		}
	};

	public void setEditorUnchanged(Editor ed) {
		final Field[] fields = Editor.class.getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if ("changes".equals(fields[i].getName())) {
				fields[i].setAccessible(true);
				try {
					fields[i].set(ed, false);
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
