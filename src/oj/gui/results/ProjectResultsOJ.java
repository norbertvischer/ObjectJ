/*
 * ProjectResultsOJ.java
 */
package oj.gui.results;

//import oj.gui.results.unlinked.UnlinkedTableModelOJ;
//import oj.gui.results.unlinked.UnlinkedContentTableRendererOJ;
//import oj.gui.results.unlinked.UnlinkedStatRowsRendererOJ;
//import oj.gui.results.unlinked.UnlinkedStatRowsModelOJ;
import oj.gui.results.linked.LinkedContentTableRendererOJ;
import oj.gui.results.linked.LinkedStatRowsRendererOJ;
import oj.gui.results.linked.LinkedStatRowsModelOJ;
import oj.gui.results.linked.LinkedTableModelOJ;
import oj.gui.results.linked.LinkedHeaderRendererOJ;
import ij.IJ;
import ij.ImagePlus;
import ij.Menus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Plot;
import ij.gui.PlotContentsDialog;
import ij.gui.PlotWindow;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.*;
import oj.OJ;
import oj.graphics.CustomCanvasOJ;
import oj.graphics.PlotOJ;
import oj.gui.KeyEventManagerOJ;
import oj.project.results.ColumnDefOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;
//import oj.gui.results.unlinked.UnlinkedHeaderRendererOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.ImageChangedListener2OJ;
import oj.processor.events.YtemChangedListenerOJ;
import oj.processor.events.QualifierChangedListenerOJ;
import oj.processor.events.ResultChangedListenerOJ;
import oj.processor.events.StatisticsChangedListenerOJ;
import oj.gui.settings.ProjectSettingsOJ;
import oj.gui.settings.ColumnSettingsOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.QualifierChangedEventOJ;
import oj.processor.events.ResultChangedEventOJ;
import oj.processor.events.StatisticsChangedEventOJ;
import oj.processor.events.YtemChangedEventOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.state.DeleteCellStateOJ;
import oj.processor.state.SelectCellStateOJ;
import oj.project.CellOJ;
import oj.project.CellsOJ;
import oj.project.results.QualifiersOJ;
import oj.project.results.statistics.StatisticsOJ;
import oj.util.UtilsOJ;

/**
 *
 * This big class manages the ObjectJ results table with its two panels: -
 * choosing visibility of columns (a list, bottom left) - showing results (a 2D
 * table, right)
 */
public class ProjectResultsOJ extends javax.swing.JFrame implements TableColumnModelListener, CellChangedListenerOJ, YtemChangedListenerOJ,
	StatisticsChangedListenerOJ, ImageChangedListener2OJ, QualifierChangedListenerOJ, ColumnChangedListenerOJ, PropertyChangeListener, ResultChangedListenerOJ {

    private LinkedHeaderRendererOJ linkedHeaderRenderer = new LinkedHeaderRendererOJ();
    private LinkedContentTableRendererOJ linkedContentRenderer = new LinkedContentTableRendererOJ();
    private LinkedStatRowsRendererOJ linkedStatRowsRenderer = new LinkedStatRowsRendererOJ();
    private Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private Cursor qualifyCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    //private Cursor deleteCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private static ProjectResultsOJ dataResults;
    private int popupMenuXPos = -1;
    private int popupMenuYPos = -1;

    private enum Mode {

	SELECT, QUALIFY, DELETE
    }
    private Mode stateMode = Mode.SELECT;

    public ProjectResultsOJ() {
	dataResults = this;
	try {
	    initComponents();
	} catch (Exception e) {
	    ij.IJ.log(e.toString());
	    ij.IJ.wait(1000);
	}//10.5.2010
	initExtraComponents();
	OJ.getEventProcessor().addCellChangedListener(this);
	OJ.getEventProcessor().addImageChangedListener(this);
	OJ.getEventProcessor().addResultChangedListener(this);
	OJ.getEventProcessor().addYtemChangedListener(this);
	OJ.getEventProcessor().addColumnChangedListener(this);
	OJ.getEventProcessor().addQualifierChangedListener(this);
	OJ.getEventProcessor().addStatisticsChangedListener(this);
    }

    public static ProjectResultsOJ getInstance() {//only one instance allowed

	if (dataResults == null) {
	    dataResults = new ProjectResultsOJ();//18.11.2018
	}
	return dataResults;
    }

    public static void kill() {
	dataResults = null;//18.11.2018
    }

    public static void close() {
	if (dataResults != null) {
	    dataResults.setVisible(false);
	}
    }

    /**
     * May be called from one of the four tables: - Linked Statistics (=header)
     * - Linked values - Unlinked Statistics (=header) - Unlinked values
     *
     * @param event
     */
    public void columnAdded(TableColumnModelEvent event) {
	int toIndex = event.getToIndex();

	if (event.getSource() == tblLinkedHeader.getColumnModel()) {
	    TableColumn tableCol = (TableColumn) tblLinkedHeader.getColumnModel().getColumn(toIndex);
	    if (toIndex <= (((LinkedStatRowsModelOJ) tblLinkedHeader.getModel()).getVisibleSize())) {
		ColumnOJ column = ((LinkedStatRowsModelOJ) tblLinkedHeader.getModel()).getVisibleElementAt(toIndex - 1);
		if (column != null) {
		    tableCol.setPreferredWidth(column.getColumnDef().getColumnWidth());
		    tableCol.setResizable(true);
		    tableCol.addPropertyChangeListener(this);
		}
	    } else {
		tableCol.setResizable(false);
	    }
	    tableCol.setHeaderRenderer(linkedHeaderRenderer);
	    tableCol.setCellRenderer(linkedStatRowsRenderer);
	} else if (event.getSource() == tblLinkedContent.getColumnModel()) {
	    TableColumn tableCol = (TableColumn) tblLinkedContent.getColumnModel().getColumn(toIndex);
	    if (toIndex <= (((LinkedTableModelOJ) tblLinkedContent.getModel()).getVisibleSize())) {
		ColumnOJ column = ((LinkedTableModelOJ) tblLinkedContent.getModel()).getVisibleElementAt(toIndex - 1);
		if (column != null) {
		    tableCol.setPreferredWidth(column.getColumnDef().getColumnWidth());
		    tableCol.setResizable(true);
		    tableCol.addPropertyChangeListener(this);
		}
	    } else {
		tableCol.setResizable(false);
	    }
	    tableCol.setCellRenderer(linkedContentRenderer);
	}
    }

    public void columnRemoved(TableColumnModelEvent event) {
    }

    /**
     * called if column order is moved with the mouse
     */
    public void columnMoved(TableColumnModelEvent event) {
    }

    /**
     * called if margin is moved with the mouse
     */
    public void columnMarginChanged(ChangeEvent event) {
    }

    public void columnSelectionChanged(ListSelectionEvent event) {
    }

    private void initExtraComponents() {
	jScrollPane1.getViewport().setOpaque(false);
	linkedScrollPane.getViewport().setOpaque(false);
	JScrollBar linkedDummyBar = new JScrollBar() {

	    @Override
	    public void paint(Graphics g) {
	    }
	};

	linkedDummyBar.setPreferredSize(linkedScrollPane.getVerticalScrollBar().getPreferredSize());
	linkedHeaderScrollPane.setVerticalScrollBar(linkedDummyBar);
	linkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

	linkedScrollPane.getVerticalScrollBar().addComponentListener(
		new ComponentListener() {

	    public void componentResized(ComponentEvent arg0) {
	    }

	    public void componentMoved(ComponentEvent arg0) {
	    }

	    public void componentShown(ComponentEvent arg0) {
		linkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    }

	    public void componentHidden(ComponentEvent arg0) {
		linkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	    }
	});

	linkedScrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

	    public void adjustmentValueChanged(AdjustmentEvent e) {
		linkedHeaderScrollPane.getHorizontalScrollBar().setValue(e.getValue());
	    }
	});

	tblLinkedHeader.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {

	    public void mousePressed(java.awt.event.MouseEvent evt) {
		int index = tblLinkedHeader.getTableHeader().getColumnModel().getColumnIndexAtX(evt.getX());
		boolean ctrl = evt.isControlDown();
		boolean right = SwingUtilities.isRightMouseButton(evt);
		if (ctrl || right) {
		    ProjectResultsOJ.getInstance().getColumnPopupMenu(tblLinkedHeader).show(tblLinkedHeader.getTableHeader(), evt.getX(), evt.getY());
		} else if (index == 0) {
		    ProjectResultsOJ.getInstance().getStatisticsPopupMenu().show(tblLinkedHeader.getTableHeader(), evt.getX(), evt.getY());
		}
	    }

	    public void mouseClicked(java.awt.event.MouseEvent evt) {
		if (SwingUtilities.isLeftMouseButton(evt) && !evt.isControlDown()) {

		    int index = tblLinkedHeader.getTableHeader().getColumnModel().getColumnIndexAtX(evt.getX());
		    if (index > 0) {
			int pos_x = 0;
			for (int i = 0; i < index; i++) {
			    pos_x = pos_x + tblLinkedHeader.getColumnModel().getColumn(i).getWidth();
			}
			if (((evt.getX() - pos_x) > 14) || (evt.getY() > 14)) {
			    return;
			}
			if (ij.IJ.isWindows()) {
			    ProjectResultsOJ.getInstance().getColumnPopupMenu(tblLinkedHeader).show(tblLinkedHeader.getTableHeader(), evt.getX(), evt.getY());
			} else {
			}
		    }
		}
	    }
	});
	tblLinkedContent.setRowSelectionAllowed(true);
	tblLinkedContent.setColumnSelectionAllowed(false);
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));

	tabbedResultsPane.setTitleAt(0, "Linked results");// + Integer.toString(OJ.getData().getCells().getQualifiedCellsCount()) + ")");//26.9.2009

	((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();

	int ls = labelColumnSelector.getFontMetrics(labelColumnSelector.getFont()).stringWidth("Linked Columns");

	mncHideOthers.setVisible(false);
	addKeyListener(KeyEventManagerOJ.getInstance());
	tblLinkedContent.addKeyListener(KeyEventManagerOJ.getInstance());
	itemCount.setSelected(false);
	itemMean.setSelected(false);
	itemStDev.setSelected(false);

	itemCv.setSelected(false);
	itemSum.setSelected(false);
	itemMin.setSelected(false);
	itemMax.setSelected(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popStatistics = new javax.swing.JPopupMenu();
        mniShowAll = new javax.swing.JMenuItem();
        mniHideAll = new javax.swing.JMenuItem();
        itemSep = new javax.swing.JPopupMenu.Separator();
        itemCount = new javax.swing.JCheckBoxMenuItem();
        itemMean = new javax.swing.JCheckBoxMenuItem();
        itemStDev = new javax.swing.JCheckBoxMenuItem();
        itemMin = new javax.swing.JCheckBoxMenuItem();
        itemMax = new javax.swing.JCheckBoxMenuItem();
        itemSum = new javax.swing.JCheckBoxMenuItem();
        itemCv = new javax.swing.JCheckBoxMenuItem();
        popColumnsLeftList = new javax.swing.JPopupMenu();
        mncShowAll = new javax.swing.JMenuItem();
        mncHideAll = new javax.swing.JMenuItem();
        mncHideOthers = new javax.swing.JMenuItem();
        popUpColumn = new javax.swing.JPopupMenu();
        mncColorRed = new javax.swing.JMenuItem();
        mncColorBlack = new javax.swing.JMenuItem();
        mncColorGreen = new javax.swing.JMenuItem();
        mncColorBlue = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        mncDigits0 = new javax.swing.JMenuItem();
        mncDigits2 = new javax.swing.JMenuItem();
        mncDigits4 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mncSortMaxTop = new javax.swing.JMenuItem();
        mncSortMinTop = new javax.swing.JMenuItem();
        mncUnsorted = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mncHistogram = new javax.swing.JMenuItem();
        mncEditThisColumn = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mncLabel = new javax.swing.JMenuItem();
        splitPaneBig = new javax.swing.JSplitPane();
        splitPaneLeft = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstColumnSelector = new javax.swing.JList(new ColumnListModelOJ());
        lstColumnSelector.setCellRenderer(new ColumnListRendererOJ());
        jPanel5 = new javax.swing.JPanel();
        labelColumnSelector = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCopyExport = new javax.swing.JButton();
        tabbedResultsPane = new javax.swing.JTabbedPane();
        linkedResultsPanel = new javax.swing.JPanel();
        linkedScrollPane = new javax.swing.JScrollPane();
        linkedScrollPane.getViewport().setBackground(Color.WHITE);
        tblLinkedContent = new javax.swing.JTable(new oj.gui.results.linked.LinkedTableModelOJ());
        tblLinkedContent.getColumnModel().addColumnModelListener(this);
        tblLinkedContent.getModel().addTableModelListener(tblLinkedContent);
        linkedHeaderScrollPane = new javax.swing.JScrollPane();
        tblLinkedHeader = new javax.swing.JTable(new oj.gui.results.linked.LinkedStatRowsModelOJ());
        tblLinkedHeader.getColumnModel().addColumnModelListener(this);
        tblLinkedHeader.getModel().addTableModelListener(tblLinkedContent);

        mniShowAll.setAction(oj.gui.results.StatisticsActionsOJ.ShowAll);
        mniShowAll.setToolTipText("");
        mniShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniShowAllActionPerformed(evt);
            }
        });
        popStatistics.add(mniShowAll);

        mniHideAll.setAction(oj.gui.results.StatisticsActionsOJ.HideAll);
        mniHideAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniHideAllActionPerformed(evt);
            }
        });
        popStatistics.add(mniHideAll);
        popStatistics.add(itemSep);

        itemCount.setSelected(true);
        itemCount.setText("Count");
        itemCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCountActionPerformed(evt);
            }
        });
        popStatistics.add(itemCount);

        itemMean.setSelected(true);
        itemMean.setText("Mean");
        itemMean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMeanActionPerformed(evt);
            }
        });
        popStatistics.add(itemMean);

        itemStDev.setSelected(true);
        itemStDev.setText("StDev");
        itemStDev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemStDevActionPerformed(evt);
            }
        });
        popStatistics.add(itemStDev);

        itemMin.setSelected(true);
        itemMin.setText("Min");
        itemMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMinActionPerformed(evt);
            }
        });
        popStatistics.add(itemMin);

        itemMax.setSelected(true);
        itemMax.setText("Max");
        itemMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMaxActionPerformed(evt);
            }
        });
        popStatistics.add(itemMax);

        itemSum.setSelected(true);
        itemSum.setText("Sum");
        itemSum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSumActionPerformed(evt);
            }
        });
        popStatistics.add(itemSum);

        itemCv.setSelected(true);
        itemCv.setText("Cv");
        itemCv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCvActionPerformed(evt);
            }
        });
        popStatistics.add(itemCv);

        mncShowAll.setText("Show All"); // NOI18N
        mncShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncShowAllActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncShowAll);

        mncHideAll.setText("Hide All"); // NOI18N
        mncHideAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncHideAllActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncHideAll);

        mncHideOthers.setText("Hide Others"); // NOI18N
        mncHideOthers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncHideOthersActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncHideOthers);

        popUpColumn.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                popUpColumnPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        mncColorRed.setText("Color red"); // NOI18N
        mncColorRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncColorRedActionPerformed(evt);
            }
        });
        popUpColumn.add(mncColorRed);

        mncColorBlack.setText("Color black"); // NOI18N
        mncColorBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncColorBlackActionPerformed(evt);
            }
        });
        popUpColumn.add(mncColorBlack);

        mncColorGreen.setText("Color green"); // NOI18N
        mncColorGreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncColorGreenActionPerformed(evt);
            }
        });
        popUpColumn.add(mncColorGreen);

        mncColorBlue.setText("Color blue"); // NOI18N
        mncColorBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncColorBlueActionPerformed(evt);
            }
        });
        popUpColumn.add(mncColorBlue);
        popUpColumn.add(jSeparator6);

        mncDigits0.setText("Precision 0 digits"); // NOI18N
        mncDigits0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncDigits0ActionPerformed(evt);
            }
        });
        popUpColumn.add(mncDigits0);

        mncDigits2.setText("Precision 2 digits"); // NOI18N
        mncDigits2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncDigits2ActionPerformed(evt);
            }
        });
        popUpColumn.add(mncDigits2);

        mncDigits4.setText("Precision 4 digits"); // NOI18N
        mncDigits4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncDigits4ActionPerformed(evt);
            }
        });
        popUpColumn.add(mncDigits4);
        popUpColumn.add(jSeparator2);

        mncSortMaxTop.setText("Sort (max at top)"); // NOI18N
        mncSortMaxTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncSortMaxTopActionPerformed(evt);
            }
        });
        popUpColumn.add(mncSortMaxTop);

        mncSortMinTop.setText("Sort (min at top)"); // NOI18N
        mncSortMinTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncSortMinTopActionPerformed(evt);
            }
        });
        popUpColumn.add(mncSortMinTop);

        mncUnsorted.setText("Unsort"); // NOI18N
        mncUnsorted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncUnsortedActionPerformed(evt);
            }
        });
        popUpColumn.add(mncUnsorted);
        popUpColumn.add(jSeparator1);

        mncHistogram.setText("Show Histogram");
        mncHistogram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncHistogramActionPerformed(evt);
            }
        });
        popUpColumn.add(mncHistogram);

        mncEditThisColumn.setText("Edit this column");
        mncEditThisColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncEditThisColumnActionPerformed(evt);
            }
        });
        popUpColumn.add(mncEditThisColumn);
        popUpColumn.add(jSeparator4);

        mncLabel.setText("Show as Label...");
        mncLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncLabelActionPerformed(evt);
            }
        });
        popUpColumn.add(mncLabel);

        setTitle("ObjectJ results"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        splitPaneBig.setBorder(null);
        splitPaneBig.setDividerLocation(140);
        splitPaneBig.setLastDividerLocation(140);

        splitPaneLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(28, 1, 1, 1));
        splitPaneLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(168, 160, 142));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 0));
        jScrollPane1.setOpaque(false);

        lstColumnSelector.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lstColumnSelector.setAlignmentX(0.0F);
        lstColumnSelector.setAlignmentY(0.0F);
        lstColumnSelector.setOpaque(false);
        lstColumnSelector.setSelectionBackground(java.awt.SystemColor.inactiveCaptionText);
        lstColumnSelector.setSelectionForeground(new java.awt.Color(0, 0, 0));
        lstColumnSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lstColumnSelectorMousePressed(evt);
            }
        });
        lstColumnSelector.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstColumnSelectorValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstColumnSelector);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel5.setBackground(javax.swing.UIManager.getDefaults().getColor("Desktop.background"));
        jPanel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        labelColumnSelector.setBackground(new java.awt.Color(0, 102, 255));
        labelColumnSelector.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelColumnSelector.setForeground(new java.awt.Color(51, 51, 51));
        labelColumnSelector.setText("Linked columns"); // NOI18N
        labelColumnSelector.setToolTipText("Result columns where each row entry is connected to an object.  \\n\\r\\pDeleting an object will also delete the linked result.    hhh"); // NOI18N
        labelColumnSelector.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 0));
        labelColumnSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                popupColumnSelectorMouseClicked(evt);
            }
        });
        jPanel5.add(labelColumnSelector, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, java.awt.BorderLayout.NORTH);

        splitPaneLeft.setRightComponent(jPanel1);
        splitPaneLeft.setLeftComponent(jPanel2);

        btnCopyExport.setText("Copy/Export...");
        btnCopyExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyExportActionPerformed(evt);
            }
        });
        splitPaneLeft.setTopComponent(btnCopyExport);

        splitPaneBig.setLeftComponent(splitPaneLeft);

        tabbedResultsPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 1, 1, 1));
        tabbedResultsPane.setOpaque(true);

        linkedResultsPanel.setLayout(new java.awt.BorderLayout());

        linkedScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        linkedScrollPane.setOpaque(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, linkedHeaderScrollPane, org.jdesktop.beansbinding.ELProperty.create("${columnHeader}"), linkedScrollPane, org.jdesktop.beansbinding.BeanProperty.create("columnHeader"));
        bindingGroup.addBinding(binding);

        tblLinkedContent.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tblLinkedContent.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblLinkedContent.setDoubleBuffered(true);
        tblLinkedContent.setFocusable(false);
        tblLinkedContent.setGridColor(new java.awt.Color(204, 204, 204));
        tblLinkedContent.setOpaque(false);
        tblLinkedContent.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblLinkedContent.setShowHorizontalLines(false);
        tblLinkedContent.setSurrendersFocusOnKeystroke(true);
        tblLinkedContent.getTableHeader().setReorderingAllowed(false);
        tblLinkedContent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblLinkedContentMousePressed(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblLinkedContentMouseEntered(evt);
            }
        });
        tblLinkedContent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLinkedContentKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLinkedContentKeyReleased(evt);
            }
        });
        linkedScrollPane.setViewportView(tblLinkedContent);

        linkedResultsPanel.add(linkedScrollPane, java.awt.BorderLayout.CENTER);

        linkedHeaderScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        linkedHeaderScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        linkedHeaderScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        linkedHeaderScrollPane.setOpaque(false);
        linkedHeaderScrollPane.setPreferredSize(new java.awt.Dimension(0, 20));
        linkedHeaderScrollPane.setWheelScrollingEnabled(false);

        tblLinkedHeader.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tblLinkedHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblLinkedHeader.setDoubleBuffered(true);
        tblLinkedHeader.setFocusable(false);
        tblLinkedHeader.setOpaque(false);
        tblLinkedHeader.setRowSelectionAllowed(false);
        tblLinkedHeader.getTableHeader().setReorderingAllowed(false);
        tblLinkedHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                tblLinkedHeaderMouseDragged(evt);
            }
        });
        linkedHeaderScrollPane.setViewportView(tblLinkedHeader);

        linkedResultsPanel.add(linkedHeaderScrollPane, java.awt.BorderLayout.NORTH);

        tabbedResultsPane.addTab("Linked Results", linkedResultsPanel);

        splitPaneBig.setRightComponent(tabbedResultsPane);

        getContentPane().add(splitPaneBig, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();

        setSize(new java.awt.Dimension(735, 540));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
//+++exception at bindingGroup.bind(); 4 lines above
    private void mncUnsortedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncUnsortedActionPerformed
	columnSortAction(currentHeader(), ColumnsOJ.COLUM_SORT_FLAG_NONE);
    }//GEN-LAST:event_mncUnsortedActionPerformed

    private void mncSortMaxTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncSortMaxTopActionPerformed
	columnSortAction(currentHeader(), ColumnsOJ.COLUM_SORT_FLAG_DESCENDING);
    }//GEN-LAST:event_mncSortMaxTopActionPerformed

    private void mncSortMinTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncSortMinTopActionPerformed
	columnSortAction(currentHeader(), ColumnsOJ.COLUM_SORT_FLAG_ASCENDING);
    }//GEN-LAST:event_mncSortMinTopActionPerformed

    private void columnSortAction(final JTable table, int sortFlag) {
	int xpos = popupMenuXPos - (int) ProjectResultsOJ.getInstance().getColumnPopupMenu(table).getInvoker().getLocationOnScreen().getX();
	int ypos = popupMenuYPos - (int) ProjectResultsOJ.getInstance().getColumnPopupMenu(table).getInvoker().getLocationOnScreen().getY();

	if ((popupMenuXPos == -1) || (popupMenuYPos == -1) || (ypos > 15)) {
	    IJ.showMessage("Cannot sort static columns");
	    return;
	}

	int index = table.getColumnModel().getColumnIndexAtX(xpos) - 1;
	if (index >= 0) {
	    if (table.getModel() instanceof LinkedStatRowsModelOJ) {
		OJ.getData().getResults().getColumns().setColumnLinkedSortFlag(sortFlag);
		ColumnOJ column = ((LinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
		if (column != null) {
		    OJ.getData().getResults().getColumns().setColumnLinkedSortName(column.getName());
		    ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
		    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
		}
	    }
//			else {
//				OJ.getData().getResults().getColumns().setColumnUnlinkedSortFlag(sortFlag);
//				ColumnOJ column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
//				if (column != null) {
//					OJ.getData().getResults().getColumns().setColumnUnlinkedSortName(column.getName());
//					((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(true));
//					((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableDataChanged();
//				}
//			}
	}
	ProjectResultsOJ.getInstance().updateResultsStatisticsView();//25.7.2011
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
	OJ.getEventProcessor().addCellChangedListener(this);
	OJ.getEventProcessor().addImageChangedListener(this);
	OJ.getEventProcessor().addYtemChangedListener(this);
	OJ.getEventProcessor().addColumnChangedListener(this);
	OJ.getEventProcessor().addResultChangedListener(this);
	OJ.getEventProcessor().addQualifierChangedListener(this);
	OJ.getEventProcessor().addStatisticsChangedListener(this);
    }//GEN-LAST:event_formWindowOpened

    private void popupColumnSelectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_popupColumnSelectorMouseClicked
	if (SwingUtilities.isLeftMouseButton(evt) && !evt.isControlDown()) {
	    popColumnsLeftList.show(jPanel1, evt.getX(), evt.getY());
}//GEN-LAST:event_popupColumnSelectorMouseClicked
    }

    private void popUpColumnPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popUpColumnPopupMenuWillBecomeInvisible
	try {
	    popupMenuXPos = (int) ((JPopupMenu) evt.getSource()).getLocationOnScreen().getX();
	    popupMenuYPos = (int) ((JPopupMenu) evt.getSource()).getLocationOnScreen().getY();
	} catch (Exception e) {
	    popupMenuXPos = -1;
	    popupMenuYPos = -1;
	}
}//GEN-LAST:event_popUpColumnPopupMenuWillBecomeInvisible

    private void mncDigits4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDigits4ActionPerformed

	columnDigitsAction(currentHeader(), 4);
    }//GEN-LAST:event_mncDigits4ActionPerformed

    private void mncDigits0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDigits0ActionPerformed
	columnDigitsAction(currentHeader(), 0);
    }//GEN-LAST:event_mncDigits0ActionPerformed

    private void columnDigitsAction(final JTable table, int digits) {
	ColumnOJ column = rightClickedColumn(table);
	if (column != null) {
	    if (column.getColumnDef().getColumnDigits() != digits) {
		column.getColumnDef().setColumnDigits(digits);
	    }
	}
	((AbstractTableModel) table.getModel()).fireTableDataChanged();
    }

    private void mncColorBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorBlueActionPerformed
	columnColorAction(currentHeader(), Color.BLUE);
    }//GEN-LAST:event_mncColorBlueActionPerformed

    private void mncColorGreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorGreenActionPerformed
	columnColorAction(currentHeader(), Color.GREEN);
    }//GEN-LAST:event_mncColorGreenActionPerformed

    private void mncColorRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorRedActionPerformed
	columnColorAction(currentHeader(), Color.RED);
    }//GEN-LAST:event_mncColorRedActionPerformed

    private void mncColorBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorBlackActionPerformed
	columnColorAction(currentHeader(), Color.BLACK);
    }//GEN-LAST:event_mncColorBlackActionPerformed

    private void columnColorAction(final JTable table, Color color) {
	ColumnOJ column = rightClickedColumn(table);
	if (column != null) {
	    if (column.getColumnDef().getColumnColor() != color) {
		column.getColumnDef().setColumnColor(color);
	    }
	}
	((AbstractTableModel) table.getModel()).fireTableDataChanged();
    }

    private ColumnOJ rightClickedColumn(final JTable table) {

	ProjectResultsOJ resultsoj = ProjectResultsOJ.getInstance();
	JPopupMenu menu = resultsoj.getColumnPopupMenu(table);
	Component invoker = menu.getInvoker();
	Point point = invoker.getLocationOnScreen();
	int xpos = popupMenuXPos - (int) point.getX();
	int index = table.getColumnModel().getColumnIndexAtX(xpos) - 1;
	ColumnOJ column = null;//, columnS;
	if (index >= 0) {
	    int tab = tabbedResultsPane.getSelectedIndex();
	    if (tab == 0) {
		column = ((LinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
	    }
	}
	return column;
    }

    private void editThisColumnAction(JTable table) {
	int tab = tabbedResultsPane.getSelectedIndex();
	if (tab == 1) {
	    ij.IJ.showMessage("not supported");
	    return;
	}
	if (popupMenuXPos == -1) {
	    return;
	}
	ProjectResultsOJ resultsoj = ProjectResultsOJ.getInstance();
	JPopupMenu menu = resultsoj.getColumnPopupMenu(table);
	Component invoker = menu.getInvoker();
	Point point = invoker.getLocationOnScreen();
	int xpos = popupMenuXPos - (int) point.getX();
	int index = table.getColumnModel().getColumnIndexAtX(xpos) - 1;
	ColumnOJ column = null;//, columnS;
	if (index >= 0) {
	    if (tabbedResultsPane.getSelectedIndex() == 0) {
		column = ((LinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
	    }
	    if (column != null) {
		if (!oj.OJ.isProjectOpen) {
		    return;
		}
		ProjectSettingsOJ prSettings = ProjectSettingsOJ.getInstance();
		if (prSettings != null) {
		    prSettings.setVisible(true);
		    prSettings.selectColumnsPanel();
		    ColumnSettingsOJ colSettings = prSettings.getColumnsPanel();
		    colSettings.selectPresentationTab();
		    colSettings.selectColumn(column.getName());//does copy-why?
		    prSettings.setVisible(true);
		    Rectangle rect = prSettings.getBounds();//13.6.2014
		    if (rect.height < 500) {
			rect.height = 500;
			prSettings.setBounds(rect);
		    }
		}
	    }
	}
    }

    private void mncHideOthersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncHideOthersActionPerformed
	int index = lstColumnSelector.getSelectedIndex();
	if (index >= 0) {
	    ColumnOJ column = ((ColumnOJ) lstColumnSelector.getModel().getElementAt(index));
	    if (column != null) {
		if (column.getColumnDef().isUnlinked()) {
		    tabbedResultsPane.setSelectedIndex(1);
		} else {
		    tabbedResultsPane.setSelectedIndex(0);
		}

		int max_index = lstColumnSelector.getModel().getSize();
		for (int i = 0; i
			< max_index; i++) {
		    if (((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)) != null) {
			ColumnDefOJ colDef = ((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)).getColumnDef();
			if (!colDef.isHidden() && ((column.getColumnDef().isUnlinked() && colDef.isUnlinked()) || (!column.getColumnDef().isUnlinked() && !colDef.isUnlinked())) && (!column.getName().equals(colDef.getName()))) {
			    colDef.setHidden(true);
			}

		    }
		}

		((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
		((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();

		((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
	    }

	}
    }//GEN-LAST:event_mncHideOthersActionPerformed

    private void mncHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncHideAllActionPerformed
	boolean isUnlinked = tabbedResultsPane.getSelectedIndex() == 1;
	isUnlinked = false;
	int max_index = lstColumnSelector.getModel().getSize();
	for (int i = 0; i < max_index; i++) {
	    if (((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)) != null) {
		ColumnDefOJ colDef = ((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)).getColumnDef();
		if (!colDef.isHidden() && ((isUnlinked && colDef.isUnlinked()) || (!isUnlinked && !colDef.isUnlinked()))) {
		    colDef.setHidden(true);
		}

	    }
	}

	((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();

	((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
    }//GEN-LAST:event_mncHideAllActionPerformed

    private void mncShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncShowAllActionPerformed
	//boolean isUnlinked = tabbedResultsPane.getSelectedIndex() == 1;

	boolean isUnlinked = false;

	int max_index = lstColumnSelector.getModel().getSize();
	for (int i = 0; i
		< lstColumnSelector.getModel().getSize(); i++) {
	    if (((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)) != null) {
		ColumnDefOJ colDef = ((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)).getColumnDef();
		if (colDef.isHidden() && (isUnlinked == colDef.isUnlinked())) {
		    colDef.setHidden(false);
		}

	    }
	}

	((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();

	((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
    }//GEN-LAST:event_mncShowAllActionPerformed

    private void mncDigits2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDigits2ActionPerformed
	columnDigitsAction(currentHeader(), 2);
    }//GEN-LAST:event_mncDigits2ActionPerformed

    /**
     * column names must be unique, not contain * and ?, and start with a
     * letter. (We want to reserve other begin characters for system) Column
     * names are not case sensitive.
     *
     * @param name column title to be tested
     * @return -1 if conditions are met; -2 wrong characters; >=0 conflicting
     * column index.
     */
    public int validColumnName(String name) {
	String[] colNames = OJ.getData().getResults().getColumns().columnNamesToArray();
	for (int jj = 0; jj
		< colNames.length; jj++) {
	    if (name.equalsIgnoreCase(colNames[jj])) {
		return jj;
	    }

	    if (name.contains("*") || name.contains("?")) {
		return -2;
	    }

	}
	return -1;
    }

    private boolean isAllStatisticEnabled() {
	boolean e = true;
	for (int i = 0; i
		< OJ.getData().getResults().getStatistics().getStatisticsCount(); i++) {
	    e = e && OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getVisible();
	}

	return e;
    }

    private boolean isAnyStatisticEnabled() {
	boolean e = false;
	for (int i = 0; i
		< OJ.getData().getResults().getStatistics().getStatisticsCount(); i++) {
	    e = e || OJ.getData().getResults().getStatistics().getStatisticsByIndex(i).getVisible();
	}

	return e;
    }

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
	if (IJ.isMacintosh() && IJ.getInstance() != null) {
	    IJ.wait(1); // needed for 1.4.1 on OS X
	    if (this.getMenuBar() != Menus.getMenuBar()) {
		this.setMenuBar(Menus.getMenuBar());
	    }
	    friendlyScroll();
	}
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
	OJ.getEventProcessor().removeCellChangedListener(this);
	OJ.getEventProcessor().removeImageChangedListener(this);
	OJ.getEventProcessor().removeYtemChangedListener(this);
	OJ.getEventProcessor().removeResultChangedListener(this);
	OJ.getEventProcessor().removeColumnChangedListener(this);
	OJ.getEventProcessor().removeQualifierChangedListener(this);
	OJ.getEventProcessor().removeStatisticsChangedListener(this);
	WindowManager.removeWindow(this);
	setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void tblLinkedContentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLinkedContentMouseEntered
	updateStateMode();
}//GEN-LAST:event_tblLinkedContentMouseEntered

    private void tblLinkedContentKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLinkedContentKeyReleased
	resetStateMode();
}//GEN-LAST:event_tblLinkedContentKeyReleased

    private void tblLinkedContentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLinkedContentKeyPressed
	updateStateMode();
}//GEN-LAST:event_tblLinkedContentKeyPressed

    private void mncHistogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncHistogramActionPerformed
	columnHistogramAction(currentHeader());//25.1.2010
}//GEN-LAST:event_mncHistogramActionPerformed

    private void mncEditThisColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncEditThisColumnActionPerformed
	editThisColumnAction(currentHeader());//9.2.2010
}//GEN-LAST:event_mncEditThisColumnActionPerformed

    private void mncColorMagentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorMagentaActionPerformed
	columnColorAction(currentHeader(), Color.MAGENTA);
    }//GEN-LAST:event_mncColorMagentaActionPerformed

    private void mncColorCyanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorCyanActionPerformed
	columnColorAction(currentHeader(), Color.CYAN);
    }//GEN-LAST:event_mncColorCyanActionPerformed

    private void mncColorOrangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorOrangeActionPerformed
	columnColorAction(currentHeader(), Color.ORANGE);
    }//GEN-LAST:event_mncColorOrangeActionPerformed

    private void mncColorDarkGreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncColorDarkGreenActionPerformed
	columnColorAction(currentHeader(), new Color(0, 170, 0));
    }//GEN-LAST:event_mncColorDarkGreenActionPerformed

    private void mniShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniShowAllActionPerformed
	showHideStatistics("*", true);
    }//GEN-LAST:event_mniShowAllActionPerformed

    private void itemCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCountActionPerformed
	showHideStatistics("count", itemCount.isSelected());
    }//GEN-LAST:event_itemCountActionPerformed

    private void itemCvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCvActionPerformed
	showHideStatistics("cv", itemCv.isSelected());
    }//GEN-LAST:event_itemCvActionPerformed

    private void itemMeanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMeanActionPerformed
	showHideStatistics("mean", itemMean.isSelected());
    }//GEN-LAST:event_itemMeanActionPerformed

    private void itemStDevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemStDevActionPerformed
	showHideStatistics("stdev", itemStDev.isSelected());
    }//GEN-LAST:event_itemStDevActionPerformed

    private void itemMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMinActionPerformed
	showHideStatistics("minimum", itemMin.isSelected());
    }//GEN-LAST:event_itemMinActionPerformed

    private void itemMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMaxActionPerformed
	showHideStatistics("maximum", itemMax.isSelected());
    }//GEN-LAST:event_itemMaxActionPerformed

    private void itemSumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSumActionPerformed
	showHideStatistics("sum", itemSum.isSelected());
    }//GEN-LAST:event_itemSumActionPerformed

    private void mniHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniHideAllActionPerformed
	showHideStatistics("*", false);
    }//GEN-LAST:event_mniHideAllActionPerformed

    private void lstColumnSelectorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstColumnSelectorValueChanged
	// TODO add your handling code here:
    }//GEN-LAST:event_lstColumnSelectorValueChanged

    private void btnCopyExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyExportActionPerformed
	oj.gui.menuactions.ResultsActionsOJ.exportResultsToText();
    }//GEN-LAST:event_btnCopyExportActionPerformed

    private void mncLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncLabelActionPerformed
	JTable table = currentHeader();
	ColumnOJ column = rightClickedColumn(table);
	if (column == null) {
	    return;
	}
	String title = column.getName();
	GenericDialog gd = new GenericDialog("Label");
	String radioTitle = "Show '" + title + "' values in Image";
	String[] items = "Hide Labels,Show As Label".split(",");
	gd.addRadioButtonGroup(radioTitle, items, 2, 1, "");
	gd.showDialog();
	if (gd.wasCanceled()) {
	    return;
	}
	String radio = gd.getNextRadioButton();
	boolean withLabel = radio.equals("Show As Label");
	boolean hideLabels = radio.equals("Hide Labels");
	if (withLabel || hideLabels) {
	    ColumnsOJ columns = OJ.getData().getResults().getColumns();
	    for (int jj = 0; jj < columns.getLinkedColumnsCount(); jj++) {
		ColumnOJ theColumn = columns.getColumnByIndex(jj);
		theColumn.getColumnDef().setLabel("");
	    }
	}
	if (withLabel) {
	    column.getColumnDef().setLabel("label");
	}
	OJ.getEventProcessor().fireYtemDefChangedEvent(YtemDefChangedEventOJ.LABEL_VISIBILITY_CHANGED);
    }//GEN-LAST:event_mncLabelActionPerformed

    private void tblLinkedContentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLinkedContentMousePressed
//		if (false) {
//			int whichButton = evt.getButton();
//			boolean abc = (evt.getModifiers() & MouseEvent.META_MASK) != 0;
//			if (abc) {
//				PopupMenu popupmenu = new PopupMenu();
//				MenuItem mi = new MenuItem("hello");
//				mi.addActionListener(IJ.getInstance());
//				popupmenu.show(this, evt.getX(), evt.getY());
//			}
//			IJ.log("Button=" + whichButton);
//		}
	if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
	    int index = tblLinkedContent.getSelectedRow();

	    if (IJ.debugMode) {//8.12.2018
		IJ.log("index = " + index);
	    }

	    if (index >= 0) {
		if ((index < 0) || (index >= OJ.getData().getCells().getCellsCount())) {
		    return;
		}
		LinkedTableModelOJ.LinkedTableValueOJ value = (LinkedTableModelOJ.LinkedTableValueOJ) ((LinkedTableModelOJ) tblLinkedContent.getModel()).getValueAt(index, 0);
		try {
		    int cell_index = Integer.parseInt(value.content) - 1;
		    if (evt.getClickCount() == 2) {
			if (IJ.debugMode) {//29.11.2018
			    IJ.log("---- second click in ObjectJ results row----");
			    IJ.beep();
			}
			OJ.getDataProcessor().showCell(cell_index);

			if (false) {//get cell hghlighted with a circle 9.1.2019

			    CellOJ cell = OJ.getData().getCells().getSelectedCell();
			    double x = cell.getYtemByIndex(0).getLocation(0).getX();
			    double y = cell.getYtemByIndex(0).getLocation(0).getY();

			    ImageWindow win = ij.WindowManager.getCurrentWindow();
			    if (win == null) {
				return;
			    }
			    Rectangle rr = new Rectangle();
			    ImagePlus imp = ij.WindowManager.getCurrentImage();
			    if (imp == null) {
				return;
			    }

			    ImageCanvas ic = imp.getCanvas();

			    if (ic != null && ic instanceof CustomCanvasOJ) {
				CustomCanvasOJ icoj = (CustomCanvasOJ) ic;
				IJ.makeOval(x - 10, y - 10, 20, 20);
				icoj.ojZoom(2, (int) x, (int) y);
				//ImagePlus imp = IJ.getImage();
//								imp.updateAndDraw();
//								IJ.wait(200);
//								IJ.run("Select None");

			    }
//		
//			IJ.OvalRoi(x-10, y-10, 20, 20);
//			IJ.getImage().updateAndDraw();
//			IJ.wait(999);
//			IJ.run("Select None");
//			IJ.getImage().updateAndDraw();
			}
		    } else {
			switch (stateMode) {
			    case QUALIFY:
				OJ.getData().getCells().getCellByIndex(cell_index).setQualified(!OJ.getData().getCells().getCellByIndex(cell_index).isQualified());
				break;
			    case DELETE:
				OJ.getData().getCells().removeCellByIndex(cell_index);
				break;
			    //insert here selection of a row, 2.11.2008
			    default:
				OJ.getDataProcessor().selectCell(cell_index);

			}
		    }
		} catch (NumberFormatException e) {
		    IJ.showMessage(e.getMessage());
		}
	    }
	    tblLinkedContent.setRowSelectionInterval(index, index);
	}
    }//GEN-LAST:event_tblLinkedContentMousePressed

    private void lstColumnSelectorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstColumnSelectorMousePressed

	int index = ((JList) evt.getSource()).getSelectedIndex();
	if (index >= 0) {
	    ColumnOJ column = (ColumnOJ) ((ColumnListModelOJ) ((JList) evt.getSource()).getModel()).getElementAt(index);
	    Rectangle r = ((JList) evt.getSource()).getCellBounds(index, index);
	    if ((evt.getX() > (r.x + 2)) && (evt.getX() < (r.x + 24)) && (evt.getY() > (r.y + 5)) && (evt.getY() < (r.y + 16))) {//5..16 ->2..24  1.11.2008

		int row = tblLinkedContent.getSelectedRow();
		try {
		    if (column != null) {
			column.getColumnDef().setHidden(!column.getColumnDef().isHidden());
			((ColumnListModelOJ) ((JList) evt.getSource()).getModel()).fireColumChanged();
			((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
			((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
		    }

		} finally {
		    tblLinkedContent.getSelectionModel().setSelectionInterval(row, row);//3.8.2010
		    ((JList) evt.getSource()).setSelectedIndex(index);
		}

	    }

	}


    }//GEN-LAST:event_lstColumnSelectorMousePressed

    private void tblLinkedHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLinkedHeaderMouseDragged
//IJ.beep();//13.5.2019
	// TODO add your handling code here:
    }//GEN-LAST:event_tblLinkedHeaderMouseDragged
    static String[] defaults = new String[]{"", ""};

    public void plotFromOjResult(boolean doAdd) {

	Plot frontPlot = null;
	if (WindowManager.getImageCount() > 0) {
	    ImagePlus imp = IJ.getImage();
	    if (imp.getWindow() instanceof PlotWindow) {
		frontPlot = ((PlotWindow) (imp.getWindow())).getPlot();
	    }
	}
	if (doAdd) {
	    if (frontPlot == null) {
		IJ.error("For adding a plot, a Plot window must be in front");
		return;
	    }
	}
	boolean includeAll = true;
	int nQualified = OJ.getData().getCells().getQualifiedCellsCount();
	int nAll = OJ.getData().getCells().getCellsCount();
	if (nAll != nQualified) {
	    GenericDialog gd = new GenericDialog("Qualification");
	    String msg = "Plot only qualified (" + nQualified + ")   or all objects (" + nAll + ") ?";

	    gd.addMessage(msg);
	    String[] items = new String[]{"All Objects", "Only Qualified"};
	    gd.enableYesNoCancel(items[0], items[1]);
	    gd.hideCancelButton();
	    gd.showDialog();
	    if (gd.wasCanceled()) {
		//IJ.log("User clicked 'Cancel'");
	    } else if (gd.wasOKed()) {
		//IJ.log("User clicked 'Yes'");
	    } else {
		//IJ.log("User clicked 'No'");
		includeAll = false;
	    }
	}
	String titlesStr = "";
	ColumnsOJ columns = OJ.getData().getResults().getColumns();
	ArrayList<float[]> data = new ArrayList<float[]>(0);
	for (int col = 0; col < columns.getLinkedColumnsCount(); col++) {
	    ColumnOJ column = columns.getColumnByIndex(col);
	    if (!column.getColumnDef().isTextMode()) {
		float[] dataF = column.getFloatArray(includeAll);
		titlesStr = titlesStr + column.getName() + "\t";
		data.add(dataF);
	    }
	}
	String[] titlesArr = titlesStr.split("\t");
	if (titlesArr.length > 1 && defaults[0] == "") {
	    defaults[0] = titlesArr[0];
	    defaults[1] = titlesArr[1];
	}
	PlotContentsDialog pgd = null;
	if (!doAdd) {
	    pgd = new PlotContentsDialog("Plot...", titlesArr, defaults, data);//New Plot
	} else {
	    defaults[0] = frontPlot.getLabel('x');
	    pgd = new PlotContentsDialog(frontPlot, titlesArr, defaults, data);//Existing Plot
	}
	pgd.noErrorBars();
	pgd.showDialog(null);
	if (!doAdd) {
	    if (WindowManager.getImageCount() == 0) {
		return;
	    }
	    ImagePlus imp = IJ.getImage();
	    if (imp != null && imp.getWindow() instanceof PlotWindow) {
		frontPlot = ((PlotWindow) (imp.getWindow())).getPlot();
		frontPlot.setLimits(Double.NaN, Double.NaN, Double.NaN, Double.NaN);//6.1.2021
		if (imp.getTitle().equalsIgnoreCase("Plot...")) {
		    String xLabel = frontPlot.getLabel('x');
		    String yLabel = frontPlot.getLabel('y');
		    imp.setTitle("Plot " + yLabel + " vs " + xLabel);
		    defaults[0] = xLabel;
		    defaults[1] = yLabel;
		}
	    }

	}
	IJ.selectWindow("ImageJ");//18.6.2020 make Plot window responsive
    }

    public void histogram() {

	IJ.showMessage("Histogram ");
	ColumnOJ axisColumn = OJ.getData().getResults().getColumns().getColumnByName("Axis");
	new PlotOJ().makeHistoFromColumn2(axisColumn);//22.5.2019
//	ImagePlus imp = IJ.getImage();
//	if (imp.getWindow() instanceof PlotWindow) {
//	    Plot plot = ((PlotWindow) (imp.getWindow())).getPlot();
//	    byte[] bytes = plot.toByteArray();
//	    InputStream myInputStream = new ByteArrayInputStream(bytes);
//	    //ImagePlus imp2 = (ImagePlus) imp.clone();
//	    IJ.runMacro("run('Duplicate...', 'title=hello');");
//	    ImagePlus imp2 = IJ.getImage();
//	    try {
//		Plot plot2 = new Plot(imp2, myInputStream);
//
////				if (fi.plot!=null) try {
////			Plot plot = new Plot(imp, new ByteArrayInputStream(fi.plot));
//		imp2.setProperty(Plot.PROPERTY_KEY, plot2);
//
//	    } catch (Exception e) {
//		IJ.error(e.toString());
//	    }
//	    imp2.show();
//	}
    }
    static double binWidth = Double.NaN;

    public void handleRoiInPlot() {
	ImagePlus imp = IJ.getImage();
	if (imp == null) {
	    return;
	}
	boolean isPlot = imp.getWindow() instanceof PlotWindow;
	if (!isPlot) {
	    IJ.showMessage("Front window must be a Plot");
	    return;
	}
	int nInside = 0;
	int nOutside = 0;
	int nObjects = OJ.getData().getCells().getCellsCount();
	if (imp != null && imp.getWindow() instanceof PlotWindow) {
	    Plot plot = ((PlotWindow) (imp.getWindow())).getPlot();
	    String[] dd = plot.getDataObjectDesignations();
	    boolean okay = (plot.getNumPlotObjects() == 1) || (dd.length == 2 && dd[1].contains("Binned"));

	    if (!okay) {
		IJ.showMessage("'Handle ROI in Plot' only works in single plots");
		return;
	    }
	    String[] style = plot.getPlotObjectStyle(0).split(",");
	    String thisStyle = style[3];

	    boolean ok = ("Circle,X,Box,Triangle,+,Dot,Diamond,Custom,".indexOf(thisStyle + ",") >= 0);
	    if (!ok) {
		IJ.showMessage("'Handle ROI in Plot' works only \nfor scatter plots (circle, dot, box etc)");
		return;
	    }
	    float[] xPlotValues = plot.getXValues();
	    float[] yPlotValues = plot.getYValues();

	    if (xPlotValues.length != nObjects) {
		IJ.showMessage("Cannot qualify this Plot (mismatch number of objects)");
		return;
	    }
	    Roi roi = imp.getRoi();
	    Color color = new Color(0, 255, 0, 80);
	    roi.setFillColor(color);

	    String xLabel = plot.getLabel('x');
	    String yLabel = plot.getLabel('y');

	    ColumnsOJ columns = OJ.getData().getResults().getColumns();
	    ColumnOJ xCol = columns.getColumnByName(xLabel);
	    ColumnOJ yCol = columns.getColumnByName(yLabel);
	    String err = "";
	    if (xCol == null) {
		err = xLabel + ": column not found\n";
	    }
	    if (yCol == null) {
		err += yLabel + ": column not found\n";
	    }
	    if (err != "") {
		IJ.error(err);
		return;
	    }
	    if (roi == null) {
		IJ.showMessage("Plot did not contain a ROI");
		return;
	    }
	    float[] xObjValues = xCol.getFloatArray(true);
	    float[] yObjValues = yCol.getFloatArray(true);
	    //int first = -1;
	    int topMost = 99999;
	    int topObj = -1;
	    int[] inside = new int[nObjects];
	    for (int obj = 0; obj < nObjects; obj++) {

		float x = xObjValues[obj];
		float y = yObjValues[obj];

		if (!Double.isNaN(x + y)) {
		    int valX = (int) plot.scaleXtoPxl(x);
		    int valY = (int) plot.scaleYtoPxl(y);
		    if (roi.contains(valX, valY)) {
			if ((int) valY < topMost) {
			    topMost = valY;
			    topObj = obj;
			}

			inside[obj] = 1;
			nInside++;
		    } else {
			inside[obj] = -1;
			nOutside++;
		    }
		}
	    }

	    OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
	    plot.updateImage();
	    int nPlotted = nInside + nOutside;
	    GenericDialog gd = new GenericDialog("Handle ROI in Plot...");
	    String msg = "";
	    msg += "Number of objects= " + nObjects + "\n";
	    msg += "  plotted points= " + nPlotted + "\n";
	    msg += "  points inside ROI= " + nInside + "\n";
	    msg += "* points outside ROI= " + nOutside + "\n";

	    String[] itemsArr = new String[3];
	    itemsArr[0] = "Show topmost object of ROI (#" + (topObj + 1) + ")";
	    itemsArr[1] = "Disqualify " + nOutside + " objects outside ROI*";
	    itemsArr[2] = "Delete " + nOutside + " objects outside ROI *";

	    gd.addRadioButtonGroup("", itemsArr, 3, 1, itemsArr[0]);

	    gd.addMessage(msg, new Font("SansSerif", Font.PLAIN, 9));
	    gd.showDialog();
	    boolean canceled = (gd.wasCanceled());
	    String s = gd.getNextRadioButton();
	    if (!canceled && s.equals(itemsArr[1])) {//disqualify
		OJ.getData().getResults().getQualifiers().setQualifyMethod(QualifiersOJ.QUALIFY_METHOD_ARBITRARY, true);
		for (int obj = 0; obj < nObjects; obj++) {
		    if (inside[obj] == -1) {
			OJ.getDataProcessor().qualifyCell(obj, false);
		    }
		}
	    }
	    if (!canceled && s.equals(itemsArr[2])) {//delete

		for (int obj = nObjects - 1; obj >= 0; obj--) {
		    if (inside[obj] == -1) {
			OJ.getDataProcessor().removeCellByIndex(obj);
		    }
		}
	    }
	    if (!canceled && s.equals(itemsArr[0]) && topObj >= 0) {//show object
		OJ.getDataProcessor().showCell(topObj);
	    }

	    roi.setFillColor(null);
	}
    }

    public void addErrorBars() {
	Plot plot = null;
	ImagePlus imp = IJ.getImage();
	if (imp == null) {
	    IJ.error("Plot window must be in front");
	    return;
	}

	if (imp.getWindow() instanceof PlotWindow) {
	    double leftLimit = Double.NaN;
	    double rightLimit = Double.NaN;
	    Roi roi = imp.getRoi();
	    plot = ((PlotWindow) (imp.getWindow())).getPlot();
	    if (plot == null) {
		IJ.error("Plot window must be in front");
		return;
	    }
	    if (roi != null) {
		leftLimit = roi.getBounds().x;
		rightLimit = leftLimit + roi.getBounds().width;
		leftLimit = plot.descaleX((int) leftLimit);
		rightLimit = plot.descaleX((int) rightLimit);
	    }
	    boolean good = plot.getNumPlotObjects() == 2 && plot.getPlotObjectLabel(1).endsWith("Binned");
	    good = good || plot.getNumPlotObjects() == 1;

	    if (!good) {
		IJ.showMessage("'Add Error Bars' only works in single plots");
		return;
	    }

	    double[] XX = floatToDouble(plot.getDataObjectArrays(0)[0]);
	    double[] YY = floatToDouble(plot.getDataObjectArrays(0)[1]);
	    int goodCount = 0;
	    for (int jj = 0; jj < XX.length; jj++) {
		if (!(Double.isNaN(XX[jj] + YY[jj]))) {
		    goodCount++;
		}
	    }
	    String msg = "xLabel=" + plot.getLabel('x');
	    msg += "\nyLabel=" + plot.getLabel('y');
	    msg += "\nnPoints=" + goodCount;
	    IJ.selectWindow("ImageJ");
	    GenericDialog gd = new GenericDialog("Add Error Bars", IJ.getInstance());
	    gd.addNumericField("BinWidth", binWidth);
	    gd.addNumericField("BinStart", 0);
	    gd.addChoice("Confidence", "90% 95% 98% 99%".split(" "), "95%");

	    gd.addMessage(msg, new Font("SansSerif", Font.PLAIN, 10), Color.darkGray);
	    gd.addCheckbox("Ignore bin centers outside ROI*", false);
	    if (Double.isNaN(leftLimit)) {
		msg = "* Plot does not contains a Roi";
	    } else {
		msg = "* ROI.left=   " + IJ.d2s(leftLimit, 3) + "\n   ROI.right= " + IJ.d2s(rightLimit, 3);
	    }
	    gd.addMessage(msg, new Font("SansSerif", Font.PLAIN, 10), Color.darkGray);
	    gd.showDialog();
	    binWidth = gd.getNextNumber();
	    double binStart = gd.getNextNumber();
	    String confidence = gd.getNextChoice();
	    int confidPercent = Integer.parseInt(confidence.substring(0, 2));
	    if (!(binWidth > 0)) {
		return;
	    }
	    double[][] eBars = UtilsOJ.calcErrorBars(XX, YY, binWidth, binStart, confidPercent);

	    if (gd.getNextBoolean()) {//remove  bins outside roi
		int midBins = 0;//means = 1; errs = 2;
		int size = eBars[midBins].length;
		boolean[] kill = new boolean[size];
		int newSize = size;
		for (int bar = 0; bar < size; bar++) {
		    double midBin = eBars[midBins][bar];
		    if (midBin < leftLimit || midBin > rightLimit) {
			kill[bar] = true;
			newSize--;
		    }
		}
		if (newSize < size) {
		    double[][] tmp = new double[3][newSize];
		    int kk = 0;
		    for (int jj = 0; jj < size; jj++) {
			if (!kill[jj]) {//create shortened arrays
			    tmp[0][kk] = eBars[0][jj];
			    tmp[1][kk] = eBars[1][jj];
			    tmp[2][kk] = eBars[2][jj];
			    kk++;
			}
		    }
		    eBars = tmp;
		}
	    }
	    plot.setLineWidth(3);
	    plot.setColor("red");
	    int count = plot.getNumPlotObjects();
	    String lastLabel = plot.getPlotObjectLabel(count - 1);
	    if ((lastLabel!=null) &&lastLabel.endsWith("Binned")) {
		plot.replace(count - 1, "connected", eBars[0], eBars[1]);
	    } else {
		plot.add("connected", eBars[0], eBars[1]);
	    }
	    plot.addErrorBars(eBars[2]);
	    count = plot.getNumPlotObjects();
	    plot.setLabel(count - 1, "Binned");
	    plot.show();
	    IJ.selectWindow("ImageJ");//1.6.2020
	}

    }

    double[] floatToDouble(float[] fArr) {
	int len = fArr.length;
	double[] dArr = new double[len];
	for (int jj = 0; jj < len; jj++) {
	    dArr[jj] = (double) fArr[jj];
	}
	return dArr;
    }

    private void resetStateMode() {
	stateMode = Mode.SELECT;
	setCursor(defaultCursor);
    }

    private void updateStateMode() {
	try {
	    if ((OJ.getToolStateProcessor().getToolStateObject() instanceof SelectCellStateOJ)
		    && (((SelectCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).isQualifyMode())) {
		setCursor(qualifyCursor);
		stateMode
			= Mode.QUALIFY;
	    } else if ((OJ.getToolStateProcessor().getToolStateObject() instanceof DeleteCellStateOJ)
		    && (((DeleteCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).isExDeleteCellMode())) {
		setCursor(qualifyCursor);
		stateMode
			= Mode.DELETE;
	    } else {
		stateMode = Mode.SELECT;
		setCursor(defaultCursor);
	    }
	} catch (Exception e) {
	    IJ.showMessage(e.toString() + "\nError 1254, click OK to continue");//14.6.2020
	}

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopyExport;
    private javax.swing.JCheckBoxMenuItem itemCount;
    private javax.swing.JCheckBoxMenuItem itemCv;
    private javax.swing.JCheckBoxMenuItem itemMax;
    private javax.swing.JCheckBoxMenuItem itemMean;
    private javax.swing.JCheckBoxMenuItem itemMin;
    private javax.swing.JPopupMenu.Separator itemSep;
    private javax.swing.JCheckBoxMenuItem itemStDev;
    private javax.swing.JCheckBoxMenuItem itemSum;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JLabel labelColumnSelector;
    private javax.swing.JScrollPane linkedHeaderScrollPane;
    private javax.swing.JPanel linkedResultsPanel;
    private javax.swing.JScrollPane linkedScrollPane;
    private javax.swing.JList lstColumnSelector;
    private javax.swing.JMenuItem mncColorBlack;
    private javax.swing.JMenuItem mncColorBlue;
    private javax.swing.JMenuItem mncColorGreen;
    private javax.swing.JMenuItem mncColorRed;
    private javax.swing.JMenuItem mncDigits0;
    private javax.swing.JMenuItem mncDigits2;
    private javax.swing.JMenuItem mncDigits4;
    private javax.swing.JMenuItem mncEditThisColumn;
    private javax.swing.JMenuItem mncHideAll;
    private javax.swing.JMenuItem mncHideOthers;
    private javax.swing.JMenuItem mncHistogram;
    private javax.swing.JMenuItem mncLabel;
    private javax.swing.JMenuItem mncShowAll;
    private javax.swing.JMenuItem mncSortMaxTop;
    private javax.swing.JMenuItem mncSortMinTop;
    private javax.swing.JMenuItem mncUnsorted;
    private javax.swing.JMenuItem mniHideAll;
    private javax.swing.JMenuItem mniShowAll;
    private javax.swing.JPopupMenu popColumnsLeftList;
    private javax.swing.JPopupMenu popStatistics;
    private javax.swing.JPopupMenu popUpColumn;
    private javax.swing.JSplitPane splitPaneBig;
    private javax.swing.JSplitPane splitPaneLeft;
    private javax.swing.JTabbedPane tabbedResultsPane;
    private javax.swing.JTable tblLinkedContent;
    private javax.swing.JTable tblLinkedHeader;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public JPopupMenu getColumnPopupMenu(final JTable table) {
	return popUpColumn;//same for linked and unlinked column
    }

    public JPopupMenu getStatisticsPopupMenu() {//2.6.2010
	return popStatistics;
    }

    /**
     * only handles changes in column width
     */
    public synchronized void propertyChange(PropertyChangeEvent evt) {//10.5.2010 synchronized
	if ("width".equals(evt.getPropertyName())) {
	    if (evt.getSource() instanceof TableColumn) {
		int width = ((TableColumn) evt.getSource()).getPreferredWidth();
		TableColumnModel model = tblLinkedHeader.getColumnModel();
		for (int i = 0; i < model.getColumnCount(); i++) {
		    if (((TableColumn) evt.getSource()).equals(model.getColumn(i))) {
			String name = tblLinkedHeader.getColumnName(i);
			ColumnOJ col = OJ.getData().getResults().getColumns().getColumnByName(name);
			if (col != null) {
			    col.getColumnDef().setColumnWidth(width);
			    int errID = 8657;
			    try {
				TableColumnModel theModel = tblLinkedContent.getColumnModel();
				errID = 8658;
				if (i < theModel.getColumnCount()) {//27.4.2010
				    TableColumn theColumn = theModel.getColumn(i);
				    errID = 8659;
				    theColumn.setWidth(width);
				    theColumn.setPreferredWidth(width);
				}
			    } catch (Exception ev) {
				if (ij.IJ.debugMode) {
				    ij.IJ.log("getColumnModel failed " + errID);
				}
			    }
			}

			return;
		    }
		}
	    }
	}
    }

    public synchronized void imageChanged(ImageChangedEventOJ evt) {//9.9.2009
	if (ProjectResultsOJ.getInstance() == null) {
	    return;//24.2.2009
	}
	//27.9.2020 ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableDataChanged();
    }

    public synchronized void columnChanged(ColumnChangedEventOJ evt) {//9.9.2009
	if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_EDITED) {
	    if (OJ.getData().getResults().getColumns().getColumnByName(evt.getNewName()) != null) {
		{
		    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
		    ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
		}

		((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
	    }

	} else if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_DELETED) {

	    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
	    ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();

	    ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();

	} else {
	    {
		tabbedResultsPane.setTitleAt(0, "Linked results (" + Integer.toString(OJ.getData().getCells().getQualifiedCellsCount()) + ")");
		((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
		((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
		((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
	    }

	    ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
	}

    }

    public synchronized void qualifierChanged(QualifierChangedEventOJ evt) {//9.9.2009
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
    }

    public synchronized void cellChanged(CellChangedEventOJ evt) {//9.9.2009
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	if (evt.getOperation() == CellChangedEventOJ.CELL_SELECT_EVENT) {
	    int row_index = ((LinkedTableModelOJ) tblLinkedContent.getModel()).getCellRowIndex(evt.getCellIndex());
	    if (row_index >= 0) {
		tblLinkedContent.setRowSelectionInterval(row_index, row_index);

		Rectangle visRect = tblLinkedContent.getVisibleRect();//don't scroll horizontally
		Rectangle rect = tblLinkedContent.getCellRect(row_index, 0, true);
		rect.x = visRect.x;
		rect.width = visRect.width;
		tblLinkedContent.scrollRectToVisible(rect);
	    }

	} else {
	    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
	}

    }

    public synchronized void friendlyScroll() {
	int cell = OJ.getData().getCells().getSelectedCellIndex();
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	int row_index = 0;
	if (cell >= 0) {
	    row_index = ((LinkedTableModelOJ) tblLinkedContent.getModel()).getCellRowIndex(cell);
	}

	tblLinkedContent.setRowSelectionInterval(row_index, row_index);

	Rectangle visRect = tblLinkedContent.getVisibleRect();//don't scroll horizontally
	int top = row_index - 8;//show 8 lines if possible
	if (top < 0) {
	    top = 0;//1.6.2020
	}
	Rectangle rect = tblLinkedContent.getCellRect(top, 0, true);
	rect.x = visRect.x;
	rect.width = visRect.width;
	tblLinkedContent.scrollRectToVisible(rect);

    }

    public synchronized void ytemChanged(YtemChangedEventOJ evt) {//9.9.2009
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
    }

    public void statisticsChanged(StatisticsChangedEventOJ evt) {//9.9.2009
	((LinkedTableModelOJ) tblLinkedHeader.getModel()).fireTableDataChanged();
	//((UnlinkedTableModelOJ) tblUnlinkedHeader.getModel()).fireTableDataChanged();
    }

    public synchronized void resultChanged(ResultChangedEventOJ evt) {//9.9.2009
	((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableDataChanged();

    }

    public void updateResultsStatisticsView() {
	((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
	((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();

	int resultsHeight = tblLinkedHeader.getRowCount() * tblLinkedHeader.getRowHeight() + tblLinkedHeader.getTableHeader().getHeight();
	linkedHeaderScrollPane.setSize(new Dimension((int) linkedHeaderScrollPane.getPreferredSize().getWidth(), resultsHeight));
	linkedHeaderScrollPane.setPreferredSize(new Dimension((int) linkedHeaderScrollPane.getPreferredSize().getWidth(), resultsHeight));

	tabbedResultsPane.repaint();
    }

    private JTable currentHeader() {
	int index = tabbedResultsPane.getSelectedIndex();
	//if (index == 0) {
	return tblLinkedHeader;
	//}
	//return tblUnlinkedHeader;
    }

    private void showHideStatistics(String itemName, boolean flag) {
	StatisticsOJ statistics = OJ.getData().getResults().getStatistics();
	for (int i = 0; i < statistics.getStatisticsCount(); i++) {
	    String statName = statistics.getStatisticsByIndex(i).getName();
	    if (itemName.equalsIgnoreCase(statName) || itemName.equals("*")) {
		statistics.getStatisticsByIndex(i).setVisible(flag);

	    }
	}
	if (itemName.equals("*")) {
	    itemCount.setState(flag);
	    itemMean.setState(flag);
	    itemStDev.setState(flag);
	    itemCv.setState(flag);
	    itemSum.setState(flag);
	    itemMin.setState(flag);
	    itemMax.setState(flag);

	}
	ProjectResultsOJ.getInstance().updateResultsStatisticsView();
    }

    public int getTab() {
	int tab = tabbedResultsPane.getSelectedIndex();
	return tab;
    }

    private void columnHistogramAction(final JTable table) {//26.1.2010
	if (popupMenuXPos == -1) {
	    return;
	}

	ColumnOJ column = rightClickedColumn(table);
	if (column != null) {
	    new PlotOJ().makeHistoFromColumn2(column);//22.5.2019
	}
    }
}
