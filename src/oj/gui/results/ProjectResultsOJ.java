/*
 * ProjectResultsOJ.java
 */
package oj.gui.results;

import oj.gui.results.unlinked.UnlinkedTableModelOJ;
import oj.gui.results.unlinked.UnlinkedContentTableRendererOJ;
import oj.gui.results.unlinked.UnlinkedStatRowsRendererOJ;
import oj.gui.results.unlinked.UnlinkedStatRowsModelOJ;
import oj.gui.results.linked.LinkedContentTableRendererOJ;
import oj.gui.results.linked.LinkedStatRowsRendererOJ;
import oj.gui.results.linked.LinkedStatRowsModelOJ;
import oj.gui.results.linked.LinkedTableModelOJ;
import oj.gui.results.linked.LinkedHeaderRendererOJ;
import ij.IJ;
import ij.Menus;
import ij.WindowManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import oj.graphics.PlotOJ;
import oj.gui.KeyEventManagerOJ;
import oj.project.ResultsOJ;
import oj.project.results.ColumnDefOJ;
import oj.project.results.ColumnOJ;
import oj.project.results.ColumnsOJ;
import oj.gui.menuactions.ViewActionsOJ;
import oj.gui.results.unlinked.UnlinkedHeaderRendererOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.ImageChangedEventOJ;
import oj.processor.events.ImageChangedListener2OJ;
import oj.processor.events.YtemChangedEventOJ;
import oj.processor.events.YtemChangedListenerOJ;
import oj.processor.events.QualifierChangedEventOJ;
import oj.processor.events.QualifierChangedListenerOJ;
import oj.processor.events.ResultChangedEventOJ;
import oj.processor.events.ResultChangedListenerOJ;
import oj.processor.events.StatisticsChangedEventOJ;
import oj.processor.events.StatisticsChangedListenerOJ;
import oj.processor.state.DeleteCellStateOJ;
import oj.processor.state.SelectCellStateOJ;
import oj.gui.settings.ProjectSettingsOJ;
import oj.gui.settings.ColumnSettingsOJ;
import oj.project.results.statistics.StatisticsOJ;

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
    private UnlinkedHeaderRendererOJ unlinkedHeaderRenderer = new UnlinkedHeaderRendererOJ();
    private UnlinkedContentTableRendererOJ unlinkedContentRenderer = new UnlinkedContentTableRendererOJ();
    private UnlinkedStatRowsRendererOJ unlinkedStatRowsRenderer = new UnlinkedStatRowsRendererOJ();
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
        return dataResults;
    }

    public static void close() {
        if (dataResults != null) {
            WindowManager.removeWindow(dataResults);
            dataResults.setVisible(false);
            dataResults = null;
        }
    }

    /**
     * May be called fom one of the four tables: - Linked Statistics (=header) -
     * Linked values - Unlinked Statistics (=header) - Unlinked values
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
        } else if (event.getSource() == tblUnlinkedHeader.getColumnModel()) {
            TableColumn tableCol = (TableColumn) tblUnlinkedHeader.getColumnModel().getColumn(toIndex);
            if (toIndex <= (((UnlinkedStatRowsModelOJ) tblUnlinkedHeader.getModel()).getVisibleSize())) {
                ColumnOJ column = ((UnlinkedStatRowsModelOJ) tblUnlinkedHeader.getModel()).getVisibleElementAt(toIndex - 1);
                if (column != null) {
                    tableCol.setPreferredWidth(column.getColumnDef().getColumnWidth());
                    tableCol.setResizable(true);
                    tableCol.addPropertyChangeListener(this);
                }
            } else {
                tableCol.setResizable(false);
            }
            tableCol.setHeaderRenderer(unlinkedHeaderRenderer);
            tableCol.setCellRenderer(unlinkedStatRowsRenderer);
        } else if (event.getSource() == tblUnlinkedContent.getColumnModel()) {
            TableColumn sTableCol = (TableColumn) tblUnlinkedContent.getColumnModel().getColumn(toIndex);
            if (toIndex <= (((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).getVisibleSize())) {
                ColumnOJ column = ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).getVisibleElementAt(toIndex - 1);
                if (column != null) {
                    int column_width = OJ.getData().getResults().getColumns().getColumnByName(column.getName()).getColumnDef().getColumnWidth();
                    sTableCol.setResizable(true);
                    sTableCol.setPreferredWidth(column_width);
                    sTableCol.addPropertyChangeListener(this);
                }
            } else {
                sTableCol.setResizable(false);
            }
            sTableCol.setCellRenderer(unlinkedContentRenderer);
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
        //jScrollPane5.getViewport().setOpaque(false); 1.6.2010
        linkedScrollPane.getViewport().setOpaque(false);
        unlinkedScrollPane.getViewport().setOpaque(false);

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

        tblUnlinkedHeader.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {//added 13.2.2010

            public void mousePressed(java.awt.event.MouseEvent evt) {
                //if (ij.IJ.controlKeyDown())
                boolean ctrl = evt.isControlDown();
                boolean right = SwingUtilities.isRightMouseButton(evt);

                if (ctrl || right) {//25.1.2010
                    ProjectResultsOJ.getInstance().getColumnPopupMenu(tblUnlinkedHeader).show(tblUnlinkedHeader.getTableHeader(), evt.getX(), evt.getY());

                }
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt) && !evt.isControlDown()) {

                    int index = tblUnlinkedHeader.getTableHeader().getColumnModel().getColumnIndexAtX(evt.getX());
                    if (index > 0) {
                        int pos_x = 0;
                        for (int i = 0; i < index; i++) {
                            pos_x = pos_x + tblUnlinkedHeader.getColumnModel().getColumn(i).getWidth();
                        }
                        if (((evt.getX() - pos_x) > 14) || (evt.getY() > 14)) {
                            return;
                        }
                        if (ij.IJ.isWindows()) {
                            ProjectResultsOJ.getInstance().getColumnPopupMenu(tblUnlinkedHeader).show(tblUnlinkedHeader.getTableHeader(), evt.getX(), evt.getY());
                        } else {
                            if (ij.IJ.isMacOSX()) {
                                ij.IJ.showMessage("This triangle is obsolete. \nPlease click column title with  right button or Ctrl key down");//25.1.2010
                            } else {
                                ij.IJ.showMessage("This triangle is obsolete. \nPlease click column title with  right button");//25.1.2010
                            }
                        }
                    }
                }
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
                            if (ij.IJ.isMacOSX()) {
                                ij.IJ.showMessage("This triangle is obsolete. \nPlease click column title with  right button or Ctrl key down");//25.1.2010
                            } else {
                                ij.IJ.showMessage("This triangle is obsolete. \nPlease click column title with  right button");//25.1.2010
                            }
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

        JScrollBar unlinkedDummyBar = new JScrollBar() {

            @Override
            public void paint(Graphics g) {
            }
        };

        unlinkedDummyBar.setPreferredSize(unlinkedScrollPane.getVerticalScrollBar().getPreferredSize());
        unlinkedHeaderScrollPane.setVerticalScrollBar(unlinkedDummyBar);
        unlinkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        unlinkedScrollPane.getVerticalScrollBar().addComponentListener(
                new ComponentListener() {

                    public void componentResized(ComponentEvent arg0) {
                    }

                    public void componentMoved(ComponentEvent arg0) {
                    }

                    public void componentShown(ComponentEvent arg0) {
                        unlinkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    }

                    public void componentHidden(ComponentEvent arg0) {
                        unlinkedHeaderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                    }
                });

        unlinkedScrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent e) {
                unlinkedHeaderScrollPane.getHorizontalScrollBar().setValue(e.getValue());
            }
        });

        tblUnlinkedHeader.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt) && !evt.isControlDown()) {
                    int index = tblUnlinkedHeader.getTableHeader().getColumnModel().getColumnIndexAtX(evt.getX());
                    if (index >= 0) {
                        int pos_x = 0;
                        for (int i = 0; i < index; i++) {
                            pos_x = pos_x + tblUnlinkedHeader.getColumnModel().getColumn(i).getWidth();
                        }
                        if (((evt.getX() - pos_x) > 14) || (evt.getY() > 14)) {
                            return;
                        }
                        ProjectResultsOJ.getInstance().getColumnPopupMenu(tblLinkedHeader).show(tblUnlinkedHeader.getTableHeader(), evt.getX(), evt.getY());
                    }
                }
            }
        });
        tblUnlinkedContent.setRowSelectionAllowed(true);
        tblUnlinkedContent.setColumnSelectionAllowed(false);
        ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).fireTableStructureChanged();
        ((UnlinkedStatRowsModelOJ) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
        ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(true));

        tabbedResultsPane.setTitleAt(0, "Linked results");// + Integer.toString(OJ.getData().getCells().getQualifiedCellsCount()) + ")");//26.9.2009
        tabbedResultsPane.setTitleAt(1, "Unlinked results");//17.5.2010

        ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
        //((StatisticsListModelOJ) lstStatisticsSelector.getModel()).fireColumChanged();1.6.2010

        int ls = labelColumnSelector.getFontMetrics(labelColumnSelector.getFont()).stringWidth("Linked Columns");

        //disabling some popup menus functions
        mncHideOthers.setVisible(false);
        addKeyListener(KeyEventManagerOJ.getInstance());
        tblLinkedContent.addKeyListener(KeyEventManagerOJ.getInstance());
        tblUnlinkedContent.addKeyListener(KeyEventManagerOJ.getInstance());

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
        jSeparator3 = new javax.swing.JSeparator();
        mncNew = new javax.swing.JMenuItem();
        mncEdit = new javax.swing.JMenuItem();
        mncDelete = new javax.swing.JMenuItem();
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
        mncPlot = new javax.swing.JMenuItem();
        splitPaneBig = new javax.swing.JSplitPane();
        splitPaneLeft = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstColumnSelector = new javax.swing.JList(new ColumnListModelOJ());
        lstColumnSelector.setCellRenderer(new ColumnListRendererOJ());
        jPanel5 = new javax.swing.JPanel();
        labelColumnSelector = new javax.swing.JLabel();
        popupColumnSelector = new javax.swing.JLabel();
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
        unlinkedResultsPanel = new javax.swing.JPanel();
        unlinkedScrollPane = new javax.swing.JScrollPane();
        unlinkedScrollPane.getViewport().setBackground(Color.WHITE);
        tblUnlinkedContent = new javax.swing.JTable(new oj.gui.results.unlinked.UnlinkedTableModelOJ());
        tblUnlinkedContent.getColumnModel().addColumnModelListener(this);
        tblUnlinkedContent.getModel().addTableModelListener(tblUnlinkedContent);
        unlinkedHeaderScrollPane = new javax.swing.JScrollPane();
        tblUnlinkedHeader = new javax.swing.JTable(new oj.gui.results.unlinked.UnlinkedStatRowsModelOJ());
        tblUnlinkedHeader.getColumnModel().addColumnModelListener(this);
        tblUnlinkedHeader.getModel().addTableModelListener(tblUnlinkedContent);
        jPanel4 = new javax.swing.JPanel();

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
        popColumnsLeftList.add(jSeparator3);

        mncNew.setText("New..."); // NOI18N
        mncNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncNewActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncNew);

        mncEdit.setText("Edit..."); // NOI18N
        mncEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncEditActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncEdit);

        mncDelete.setText("Delete"); // NOI18N
        mncDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncDeleteActionPerformed(evt);
            }
        });
        popColumnsLeftList.add(mncDelete);

        popUpColumn.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                popUpColumnPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
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

        mncPlot.setText("Plot...");
        mncPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mncPlotActionPerformed(evt);
            }
        });
        popUpColumn.add(mncPlot);

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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstColumnSelectorMouseClicked(evt);
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
        jPanel5.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        labelColumnSelector.setBackground(new java.awt.Color(0, 102, 255));
        labelColumnSelector.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        labelColumnSelector.setForeground(new java.awt.Color(51, 51, 51));
        labelColumnSelector.setText("Linked columns"); // NOI18N
        labelColumnSelector.setToolTipText("Result columns where each row entry is connected to an object.  \\n\\r\\pDeleting an object will also delete the linked result.    hhh"); // NOI18N
        labelColumnSelector.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 0));
        jPanel5.add(labelColumnSelector, java.awt.BorderLayout.CENTER);

        popupColumnSelector.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/Triangle.gif"))); // NOI18N
        popupColumnSelector.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        popupColumnSelector.setComponentPopupMenu(popColumnsLeftList);
        popupColumnSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                popupColumnSelectorMouseClicked(evt);
            }
        });
        jPanel5.add(popupColumnSelector, java.awt.BorderLayout.WEST);

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

        tblLinkedContent.setFont(new java.awt.Font("SansSerif", 0, 12));
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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLinkedContentMouseClicked(evt);
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

        tblLinkedHeader.setFont(new java.awt.Font("SansSerif", 0, 12));
        tblLinkedHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblLinkedHeader.setDoubleBuffered(true);
        tblLinkedHeader.setFocusable(false);
        tblLinkedHeader.setOpaque(false);
        tblLinkedHeader.setRowSelectionAllowed(false);
        tblLinkedHeader.getTableHeader().setReorderingAllowed(false);
        linkedHeaderScrollPane.setViewportView(tblLinkedHeader);

        linkedResultsPanel.add(linkedHeaderScrollPane, java.awt.BorderLayout.NORTH);

        tabbedResultsPane.addTab("Linked Results", linkedResultsPanel);

        unlinkedResultsPanel.setLayout(new java.awt.BorderLayout());

        unlinkedScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unlinkedScrollPane.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unlinkedHeaderScrollPane, org.jdesktop.beansbinding.ELProperty.create("${columnHeader}"), unlinkedScrollPane, org.jdesktop.beansbinding.BeanProperty.create("columnHeader"));
        bindingGroup.addBinding(binding);

        tblUnlinkedContent.setBackground(new java.awt.Color(236, 233, 216));
        tblUnlinkedContent.setFont(new java.awt.Font("SansSerif", 0, 12));
        tblUnlinkedContent.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblUnlinkedContent.setDoubleBuffered(true);
        tblUnlinkedContent.setGridColor(new java.awt.Color(204, 204, 204));
        tblUnlinkedContent.setOpaque(false);
        tblUnlinkedContent.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblUnlinkedContent.setShowHorizontalLines(false);
        unlinkedScrollPane.setViewportView(tblUnlinkedContent);

        unlinkedResultsPanel.add(unlinkedScrollPane, java.awt.BorderLayout.CENTER);

        unlinkedHeaderScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unlinkedHeaderScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        unlinkedHeaderScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        unlinkedHeaderScrollPane.setOpaque(false);
        unlinkedHeaderScrollPane.setPreferredSize(new java.awt.Dimension(0, 20));
        unlinkedHeaderScrollPane.setWheelScrollingEnabled(false);

        tblUnlinkedHeader.setFont(new java.awt.Font("SansSerif", 0, 12));
        tblUnlinkedHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblUnlinkedHeader.setDoubleBuffered(true);
        tblUnlinkedHeader.setFocusable(false);
        tblUnlinkedHeader.setOpaque(false);
        tblUnlinkedHeader.setRowSelectionAllowed(false);
        unlinkedHeaderScrollPane.setViewportView(tblUnlinkedHeader);

        unlinkedResultsPanel.add(unlinkedHeaderScrollPane, java.awt.BorderLayout.NORTH);

        tabbedResultsPane.addTab("Unlinked Results", unlinkedResultsPanel);

        splitPaneBig.setRightComponent(tabbedResultsPane);

        getContentPane().add(splitPaneBig, java.awt.BorderLayout.CENTER);

        jPanel4.setPreferredSize(new java.awt.Dimension(10, 20));
        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-735)/2, (screenSize.height-540)/2, 735, 540);
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
            } else {
                OJ.getData().getResults().getColumns().setColumnUnlinkedSortFlag(sortFlag);
                ColumnOJ column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
                if (column != null) {
                    OJ.getData().getResults().getColumns().setColumnUnlinkedSortName(column.getName());
                    ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(true));
                    ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableDataChanged();
                }
            }
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

    private void mncNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncNewActionPerformed
        ViewActionsOJ.SettingsAction.actionPerformed(evt);
        ProjectSettingsOJ.getInstance().selectColumnsPanel();
        ((ColumnSettingsOJ) ProjectSettingsOJ.getInstance().getSettings()).newColumn();
    }//GEN-LAST:event_mncNewActionPerformed

    private void popUpColumnPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popUpColumnPopupMenuWillBecomeInvisible
        try {
            popupMenuXPos = (int) ((JPopupMenu) evt.getSource()).getLocationOnScreen().getX();
            popupMenuYPos = (int) ((JPopupMenu) evt.getSource()).getLocationOnScreen().getY();
        } catch (Exception e) {
            popupMenuXPos = -1;
            popupMenuYPos = -1;
        }
}//GEN-LAST:event_popUpColumnPopupMenuWillBecomeInvisible

    private void tblLinkedContentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLinkedContentMouseClicked
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            int index = tblLinkedContent.getSelectedRow();
            if (index >= 0) {
                if ((index < 0) || (index >= OJ.getData().getCells().getCellsCount())) {
                    return;
                }
                LinkedTableModelOJ.LinkedTableValueOJ value = (LinkedTableModelOJ.LinkedTableValueOJ) ((LinkedTableModelOJ) tblLinkedContent.getModel()).getValueAt(index, 0);
                try {
                    int cell_index = Integer.parseInt(value.content) - 1;
                    if (evt.getClickCount() == 2) {
                        OJ.getDataProcessor().showCell(cell_index);
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
}//GEN-LAST:event_tblLinkedContentMouseClicked

    private void mncDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDeleteActionPerformed
        int index = lstColumnSelector.getSelectedIndex();
        if (index >= 0) {
            ColumnOJ column = ((ColumnOJ) ((ColumnListModelOJ) lstColumnSelector.getModel()).getElementAt(index));
            if (column != null) {
                ResultsOJ results = OJ.getData().getResults();
                if (lstColumnSelector.getModel() instanceof ColumnListModelOJ) {
                    if ((results.getColumns().getColumnLinkedSortName() != null) && (results.getColumns().getColumnLinkedSortName().equals(column.getName()))) {
                        results.getColumns().setColumnLinkedSortFlag(ColumnsOJ.COLUM_SORT_FLAG_NONE);
                        results.getColumns().setColumnLinkedSortName("");
                        ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(results.getSortedIndexes(false));
                    }
                } else {
                    if ((results.getColumns().getColumnLinkedSortName() != null) && (results.getColumns().getColumnUnlinkedSortName().equals(column.getName()))) {
                        results.getColumns().setColumnUnlinkedSortFlag(ColumnsOJ.COLUM_SORT_FLAG_NONE);
                        results.getColumns().setColumnUnlinkedSortName("");
                        ((UnlinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(results.getSortedIndexes(true));
                    }
                }
                OJ.getData().getResults().getColumns().removeColumnByName(column.getName());
            }
        }
    }//GEN-LAST:event_mncDeleteActionPerformed

    private void mncDigits4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDigits4ActionPerformed

        columnDigitsAction(currentHeader(), 4);
    }//GEN-LAST:event_mncDigits4ActionPerformed

    private void mncDigits0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncDigits0ActionPerformed
        columnDigitsAction(currentHeader(), 0);
    }//GEN-LAST:event_mncDigits0ActionPerformed

    private void columnDigitsAction(final JTable table, int digits) {
        if (popupMenuXPos == -1) {
            return;
        }
        int xpos = popupMenuXPos - (int) ProjectResultsOJ.getInstance().getColumnPopupMenu(table).getInvoker().getLocationOnScreen().getX();
        int index = table.getColumnModel().getColumnIndexAtX(xpos) - 1;
        if (index >= 0) {
            if (table.getModel() instanceof LinkedStatRowsModelOJ) {
                ColumnOJ column = ((LinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
                if (column != null) {
                    if (column.getColumnDef().getColumnDigits() != digits) {
                        column.getColumnDef().setColumnDigits(digits);
                    }
                }
            } else {
                ColumnOJ column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
                if (column != null) {
                    if (column.getColumnDef().getColumnDigits() != digits) {
                        column.getColumnDef().setColumnDigits(digits);
                    }
                }
            }
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        }
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
        if (popupMenuXPos == -1) {
            return;
        }
        //int xpos = popupMenuXPos - (int) ProjectResultsOJ.getInstance().getColumnPopupMenu(table).getInvoker().getLocationOnScreen().getX();

        String name = table.getColumnName(1);
        name = name + "";
        JPopupMenu pop = ProjectResultsOJ.getInstance().getColumnPopupMenu(table);
        int thatX = (int) pop.getInvoker().getLocationOnScreen().getX();
        int diffX = popupMenuXPos - thatX;
        int xpos = popupMenuXPos - (int) ProjectResultsOJ.getInstance().getColumnPopupMenu(table).getInvoker().getLocationOnScreen().getX();
        int index = table.getColumnModel().getColumnIndexAtX(xpos) - 1;
        if (index >= 0) {
            if (table.getModel() instanceof LinkedStatRowsModelOJ) {
                ColumnOJ column = ((LinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
                if (column != null) {
                    if (column.getColumnDef().getColumnColor() != color) {
                        column.getColumnDef().setColumnColor(color);
                    }
                }
            } else {
                ColumnOJ column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
                if (column != null) {
                    if (column.getColumnDef().getColumnColor() != color) {
                        column.getColumnDef().setColumnColor(color);
                    }
                }
            }
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        }
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
            if (tab == 1) {
                column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
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
            if (tabbedResultsPane.getSelectedIndex() == 1) {
                column = ((UnlinkedStatRowsModelOJ) table.getModel()).getVisibleElementAt(index);
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

                if (column.getColumnDef().isUnlinked()) {
                    ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
                    ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
                } else {
                    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
                    ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
                }

                ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
            }

        }
    }//GEN-LAST:event_mncHideOthersActionPerformed

    private void mncHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncHideAllActionPerformed
        boolean isUnlinked = tabbedResultsPane.getSelectedIndex() == 1;
        int max_index = lstColumnSelector.getModel().getSize();
        for (int i = 0; i
                < max_index; i++) {
            if (((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)) != null) {
                ColumnDefOJ colDef = ((ColumnOJ) lstColumnSelector.getModel().getElementAt(i)).getColumnDef();
                if (!colDef.isHidden() && ((isUnlinked && colDef.isUnlinked()) || (!isUnlinked && !colDef.isUnlinked()))) {
                    colDef.setHidden(true);
                }

            }
        }

        if (isUnlinked) {
            ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
            ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
        } else {
            ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
            ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
        }

        ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
    }//GEN-LAST:event_mncHideAllActionPerformed

    private void mncShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncShowAllActionPerformed
        boolean isUnlinked = tabbedResultsPane.getSelectedIndex() == 1;
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

        if (isUnlinked) {
            ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
            ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
        } else {
            ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
            ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
        }

        ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
    }//GEN-LAST:event_mncShowAllActionPerformed

    private void mncEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncEditActionPerformed
        int index = lstColumnSelector.getSelectedIndex();
        if (index >= 0) {
            ViewActionsOJ.SettingsAction.actionPerformed(evt);
            ProjectSettingsOJ.getInstance().selectColumnsPanel();
            ((ColumnSettingsOJ) ProjectSettingsOJ.getInstance().getSettings()).editColumn(((ColumnOJ) lstColumnSelector.getSelectedValue()).getName());
        }
    }//GEN-LAST:event_mncEditActionPerformed

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

        }
    }//GEN-LAST:event_formWindowActivated

    private void lstColumnSelectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstColumnSelectorMouseClicked
        int index = ((JList) evt.getSource()).getSelectedIndex();
        if (index >= 0) {
            ColumnOJ column = (ColumnOJ) ((ColumnListModelOJ) ((JList) evt.getSource()).getModel()).getElementAt(index);
            Rectangle r = ((JList) evt.getSource()).getCellBounds(index, index);
            if ((evt.getX() > (r.x + 2)) && (evt.getX() < (r.x + 24)) && (evt.getY() > (r.y + 5)) && (evt.getY() < (r.y + 16))) {//5..16 ->2..24  1.11.2008
                if (column.isUnlinkedColumn()) {
                    int row = tblUnlinkedContent.getSelectedRow();
                    try {
                        if (column != null) {
                            column.getColumnDef().setHidden(!column.getColumnDef().isHidden());
                            ((ColumnListModelOJ) ((JList) evt.getSource()).getModel()).fireColumChanged();
                            ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
                            ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
                        }

                    } finally {
                        tblLinkedContent.getSelectionModel().setSelectionInterval(row, row);
                    }

                } else {
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
        }
}//GEN-LAST:event_lstColumnSelectorMouseClicked

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

    private void mncPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mncPlotActionPerformed

        columnPlotAction(currentHeader());     }//GEN-LAST:event_mncPlotActionPerformed

    private void resetStateMode() {
        stateMode = Mode.SELECT;
        setCursor(defaultCursor);
    }

    private void updateStateMode() {
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
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
    private javax.swing.JMenuItem mncDelete;
    private javax.swing.JMenuItem mncDigits0;
    private javax.swing.JMenuItem mncDigits2;
    private javax.swing.JMenuItem mncDigits4;
    private javax.swing.JMenuItem mncEdit;
    private javax.swing.JMenuItem mncEditThisColumn;
    private javax.swing.JMenuItem mncHideAll;
    private javax.swing.JMenuItem mncHideOthers;
    private javax.swing.JMenuItem mncHistogram;
    private javax.swing.JMenuItem mncNew;
    private javax.swing.JMenuItem mncPlot;
    private javax.swing.JMenuItem mncShowAll;
    private javax.swing.JMenuItem mncSortMaxTop;
    private javax.swing.JMenuItem mncSortMinTop;
    private javax.swing.JMenuItem mncUnsorted;
    private javax.swing.JMenuItem mniHideAll;
    private javax.swing.JMenuItem mniShowAll;
    private javax.swing.JPopupMenu popColumnsLeftList;
    private javax.swing.JPopupMenu popStatistics;
    private javax.swing.JPopupMenu popUpColumn;
    private javax.swing.JLabel popupColumnSelector;
    private javax.swing.JSplitPane splitPaneBig;
    private javax.swing.JSplitPane splitPaneLeft;
    private javax.swing.JTabbedPane tabbedResultsPane;
    private javax.swing.JTable tblLinkedContent;
    private javax.swing.JTable tblLinkedHeader;
    private javax.swing.JTable tblUnlinkedContent;
    private javax.swing.JTable tblUnlinkedHeader;
    private javax.swing.JScrollPane unlinkedHeaderScrollPane;
    private javax.swing.JPanel unlinkedResultsPanel;
    private javax.swing.JScrollPane unlinkedScrollPane;
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
                model = tblUnlinkedHeader.getColumnModel();
                for (int i = 0; i
                        < model.getColumnCount(); i++) {
                    if (((TableColumn) evt.getSource()).equals(model.getColumn(i))) {
                        String name = tblUnlinkedHeader.getColumnName(i);
                        ColumnOJ col = OJ.getData().getResults().getColumns().getColumnByName(name);
                        if (col != null) {
                            col.getColumnDef().setColumnWidth(width);
                            tblUnlinkedContent.getColumnModel().getColumn(i).setWidth(width);
                            tblUnlinkedContent.getColumnModel().getColumn(i).setPreferredWidth(width);
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
        ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
        ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
        ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableDataChanged();
    }

    public synchronized void columnChanged(ColumnChangedEventOJ evt) {//9.9.2009
        if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_EDITED) {
            if (OJ.getData().getResults().getColumns().getColumnByName(evt.getNewName()) != null) {
                if (OJ.getData().getResults().getColumns().getColumnByName(evt.getNewName()).getColumnDef().isUnlinked()) {
                    ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
                    ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
                } else {
                    ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
                    ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
                }

                ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();
            }

        } else if (evt.getOperation() == ColumnChangedEventOJ.COLUMN_DELETED) {

            ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();//30.5.2010
            ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();

            ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
            ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();

            ((ColumnListModelOJ) lstColumnSelector.getModel()).fireColumChanged();

        } else {
            if (OJ.getData().getResults().getColumns().getColumnByName(evt.getNewName()).getColumnDef().isUnlinked()) {
                ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(true));
                ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();
                ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
            } else {
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
                Rectangle rect = tblLinkedContent.getCellRect(row_index, 0, true);
                tblLinkedContent.scrollRectToVisible(rect);
            }

        } else {
            ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
        }

    }

    public synchronized void ytemChanged(YtemChangedEventOJ evt) {//9.9.2009
        ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
        ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
    }

    public void statisticsChanged(StatisticsChangedEventOJ evt) {//9.9.2009
        ((LinkedTableModelOJ) tblLinkedHeader.getModel()).fireTableDataChanged();
        ((UnlinkedTableModelOJ) tblUnlinkedHeader.getModel()).fireTableDataChanged();
    }

    public synchronized void resultChanged(ResultChangedEventOJ evt) {//9.9.2009
        ((UnlinkedTableModelOJ) tblUnlinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(true));
        ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableDataChanged();
        ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableDataChanged();
        ((LinkedTableModelOJ) tblLinkedContent.getModel()).setSortedIndexes(OJ.getData().getResults().getSortedIndexes(false));
        ((AbstractTableModel) tblLinkedContent.getModel()).fireTableDataChanged();
        ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableDataChanged();

    }

    public void updateResultsStatisticsView() {
        ((AbstractTableModel) tblLinkedHeader.getModel()).fireTableStructureChanged();
        ((AbstractTableModel) tblLinkedContent.getModel()).fireTableStructureChanged();
        ((AbstractTableModel) tblUnlinkedHeader.getModel()).fireTableStructureChanged();
        ((AbstractTableModel) tblUnlinkedContent.getModel()).fireTableStructureChanged();

        int resultsHeight = tblLinkedHeader.getRowCount() * tblLinkedHeader.getRowHeight() + tblLinkedHeader.getTableHeader().getHeight();
        linkedHeaderScrollPane.setSize(new Dimension((int) linkedHeaderScrollPane.getPreferredSize().getWidth(), resultsHeight));
        linkedHeaderScrollPane.setPreferredSize(new Dimension((int) linkedHeaderScrollPane.getPreferredSize().getWidth(), resultsHeight));

        int staticHeight = tblUnlinkedHeader.getRowCount() * tblUnlinkedHeader.getRowHeight() + tblUnlinkedHeader.getTableHeader().getHeight();
        unlinkedHeaderScrollPane.setSize(new Dimension((int) unlinkedHeaderScrollPane.getPreferredSize().getWidth(), staticHeight));
        unlinkedHeaderScrollPane.setPreferredSize(new Dimension((int) unlinkedHeaderScrollPane.getPreferredSize().getWidth(), staticHeight));
        tabbedResultsPane.repaint();
    }

    private JTable currentHeader() {
        int index = tabbedResultsPane.getSelectedIndex();
        if (index == 0) {
            return tblLinkedHeader;
        }
        return tblUnlinkedHeader;
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
            new PlotOJ().makeHistoFromColumn(column);
        }
    }

    private void columnPlotAction(final JTable table) {
        if (popupMenuXPos == -1) {
            return;
        }

        ColumnOJ column = rightClickedColumn(table);
        if (column != null) {
            //PlotDialogOJ pd = new PlotDialogOJ(ProjectSettingsOJ.getInstance(), true,column);

        }
    }

}
