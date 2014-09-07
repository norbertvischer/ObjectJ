/*
 * ObjectDefListOJ.java
 */
package oj.gui;

import ij.IJ;
import ij.Menus;
import ij.gui.GenericDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import oj.OJ;
import oj.project.YtemDefOJ;
import oj.processor.events.CellChangedEventOJ;
import oj.processor.events.CellChangedListenerOJ;
import oj.processor.events.YtemDefChangedEventOJ;
import oj.processor.events.YtemDefChangedListenerOJ;
import oj.processor.events.YtemDefSelectionChangedEventOJ;
import oj.processor.events.YtemDefSelectionChangedListenerOJ;
import oj.processor.state.CreateCellStateOJ;
import oj.gui.tools.ToolManagerOJ;
import oj.gui.tools.ToolOJ;
import oj.gui.tools.events.ToolListChangedEventOJ;
import oj.gui.tools.events.ToolListChangedListenerOJ;
import oj.gui.tools.events.ToolSelectionChangedEventOJ;
import oj.gui.tools.events.ToolSelectionChangedListenerOJ;

public class ToolsWindowOJ extends javax.swing.JPanel implements ToolListChangedListenerOJ, ToolSelectionChangedListenerOJ, /*ItemListIntfOJ,*/ KeyListener, CellChangedListenerOJ, YtemDefChangedListenerOJ, YtemDefSelectionChangedListenerOJ {

    private static ToolsWindowOJ ytemDefList;
    private static Window instance;
    private static final int WINDOW_TYPE = 0;
    private static final int JFRAME_TYPE = 1;
    private static final int JDIALOG_TYPE = 2;
    private static final int FLOATING_WINDOW_TYPE = 3;
    private static int instance_type = WINDOW_TYPE;
    private int dx;
    private int dy;
    public static int instance_width;
    public static int instance_height;
    public static int instance_xpos = 100;
    public static int instance_ypos = 100;
    private int macroToolsCount = 0;
    private Hashtable macroTools = new Hashtable();
    private Hashtable objectTools = new Hashtable();
    private ToolListener toolListener = new ToolListener();
    private ActionToolListener actionToolListener = new ActionToolListener();

    /**
     * Creates new form ObjectDefListOJ
     */
    public ToolsWindowOJ() {
        ytemDefList = this;
        initComponents();
        initComponentsExt();
        updateYtemDefList();

        ToolManagerOJ.getInstance().addToolListChangedListener(this);
        ToolManagerOJ.getInstance().addToolSelectionChangedListener(this);

        OJ.getEventProcessor().addCellChangedListener(this);
        OJ.getEventProcessor().addYtemDefChangedListener(this);
        OJ.getEventProcessor().addYtemDefSelectionChangedListener(this);
    }

    public static Window getInstance() {
        return instance;
    }

    public static ToolsWindowOJ getYtemDefListOJ() {
        return ytemDefList;
    }

    public static void setInstance(Window instance) {
        ToolsWindowOJ.instance = instance;
        if (ToolsWindowOJ.instance == null) {
            return;
        }
        ToolsWindowOJ.instance.setLocation(ToolsWindowOJ.instance_xpos, ToolsWindowOJ.instance_ypos);
        ToolsWindowOJ.instance.setPreferredSize(new Dimension(75, 240));
        ToolsWindowOJ.instance.setSize(new Dimension(75, 240));

        if (instance instanceof JFrame) {
            instance_type = ToolsWindowOJ.JFRAME_TYPE;
            ((JFrame) ToolsWindowOJ.instance).getContentPane().add(new ToolsWindowOJ());
        } else if (instance instanceof JDialog) {
            instance_type = ToolsWindowOJ.JDIALOG_TYPE;
            ((JDialog) ToolsWindowOJ.instance).getContentPane().add(new ToolsWindowOJ());
        } else {
            instance_type = ToolsWindowOJ.WINDOW_TYPE;
        }
    }

    public static void close() {
        if (instance != null) {
            OJ.getEventProcessor().removeYtemDefSelectionChangedListener(ytemDefList);
            OJ.getEventProcessor().removeYtemDefChangedListener(ytemDefList);
            OJ.getEventProcessor().removeCellChangedListener(ytemDefList);
            instance.setVisible(false);
            instance = null;
        }
    }

    public void updateVisibilityView() {
        pnlVisibility.setVisible(OJ.getData().getYtemDefs().isYtemVisibilitySwitchEnabled());
        updateVisibilityPanel();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpEditCell = new javax.swing.ButtonGroup();
        lblCorner = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        chkCollectMode =      chkCollectMode = new javax.swing.JCheckBox(){
            public javax.swing.JToolTip createToolTip() {
                return new oj.util.MultiLineToolTipOJ();
            }
        };
        ;
        buttCloseCell = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        pnlVisibility = new javax.swing.JPanel();
        listYtemDefs = new javax.swing.JList(new YtemDefCellModelOJ());
        listYtemDefs.setCellRenderer(new YtemDefCellRendererOJ());
        jPanel1 = new javax.swing.JPanel();
        pnlMacrosTools = new javax.swing.JPanel();
        pnlObjectTools = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlHorizontal = new javax.swing.JPanel();

        lblCorner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oj/gui/icons/Corner.gif")));
        lblCorner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblCornerMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblCornerMouseReleased(evt);
            }
        });
        lblCorner.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblCornerMouseDragged(evt);
            }
        });

        setMinimumSize(new java.awt.Dimension(112, 309));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(112, 309));
        setRequestFocusEnabled(false);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setFocusable(false);
        jPanel2.setOpaque(false);
        jPanel2.setRequestFocusEnabled(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setFocusable(false);
        jPanel3.setRequestFocusEnabled(false);
        jPanel3.setLayout(new java.awt.GridLayout(2, 0));

        chkCollectMode.setBackground(new java.awt.Color(0, 102, 102));
        chkCollectMode.setText("Composite");
        chkCollectMode.setToolTipText("If \"Composite objects\" is on, an object can accept |\nseveral items, all residing under the same object |\nnumber and being linked to a single row in the results.|\n\nIf \"Composite objects\" is off, an object can only |\nhold a single item. \nChanging this checkbox only affects the modus |\nof future point collection and doesn't touch existing objects.");
        chkCollectMode.setContentAreaFilled(false);
        chkCollectMode.setFocusPainted(false);
        chkCollectMode.setFocusable(false);
        chkCollectMode.setMargin(new java.awt.Insets(3, 2, 4, 2));
        chkCollectMode.setRequestFocusEnabled(false);
        chkCollectMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCollectModeActionPerformed(evt);
            }
        });
        jPanel3.add(chkCollectMode);

        buttCloseCell.setText("Close");
        buttCloseCell.setToolTipText("Close object");
        buttCloseCell.setEnabled(false);
        buttCloseCell.setFocusPainted(false);
        buttCloseCell.setFocusable(false);
        buttCloseCell.setRequestFocusEnabled(false);
        buttCloseCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttCloseCellActionPerformed(evt);
            }
        });
        jPanel3.add(buttCloseCell);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(4, 2, 2, 2), javax.swing.BorderFactory.createEtchedBorder()));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 120));
        jPanel5.setLayout(new java.awt.BorderLayout());

        pnlVisibility.setBackground(new java.awt.Color(255, 255, 255));
        pnlVisibility.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 4, 0));
        pnlVisibility.setToolTipText("Switch the marker visibility on/off");
        pnlVisibility.setLayout(new javax.swing.BoxLayout(pnlVisibility, javax.swing.BoxLayout.Y_AXIS));
        jPanel5.add(pnlVisibility, java.awt.BorderLayout.EAST);

        listYtemDefs.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 4, 0));
        listYtemDefs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        listYtemDefs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listYtemDefs.setAutoscrolls(false);
        listYtemDefs.setFocusable(false);
        listYtemDefs.setRequestFocusEnabled(false);
        listYtemDefs.setVisibleRowCount(4);
        listYtemDefs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listYtemDefsMouseReleased(evt);
            }
        });
        jPanel5.add(listYtemDefs, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(jPanel5);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlMacrosTools.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 6, 1));
        pnlMacrosTools.setFocusable(false);
        pnlMacrosTools.setOpaque(false);
        pnlMacrosTools.setRequestFocusEnabled(false);
        pnlMacrosTools.setLayout(new java.awt.GridLayout(0, 2));
        jPanel1.add(pnlMacrosTools, java.awt.BorderLayout.PAGE_END);

        pnlObjectTools.setFocusable(false);
        pnlObjectTools.setOpaque(false);
        pnlObjectTools.setRequestFocusEnabled(false);
        pnlObjectTools.setLayout(new java.awt.GridLayout(0, 2));
        jPanel1.add(pnlObjectTools, java.awt.BorderLayout.NORTH);

        jPanel4.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel4, java.awt.BorderLayout.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ObjectJ Tools");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 1, 1, 1));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLabel1KeyPressed(evt);
            }
        });
        add(jLabel1, java.awt.BorderLayout.NORTH);

        pnlHorizontal.setBackground(new java.awt.Color(255, 255, 255));
        pnlHorizontal.setFocusable(false);
        pnlHorizontal.setOpaque(false);
        pnlHorizontal.setRequestFocusEnabled(false);
        pnlHorizontal.setLayout(new java.awt.BorderLayout());
        add(pnlHorizontal, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void buttCloseCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttCloseCellActionPerformed
        setButtonOpenStatus(false);
        ((CreateCellStateOJ) OJ.getToolStateProcessor().getToolStateObject()).closeCell();
    }//GEN-LAST:event_buttCloseCellActionPerformed

    private void lblCornerMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCornerMouseDragged
        int size_x = instance.getWidth();
        int size_y = instance.getHeight();

        size_x = jPanel2.getLocation().x + pnlHorizontal.getLocation().x + lblCorner.getLocation().x + evt.getX() + instance.getWidth() - this.getWidth() + dx;
        size_y = jPanel2.getLocation().y + pnlHorizontal.getLocation().y + lblCorner.getLocation().y + evt.getY() + instance.getHeight() - this.getHeight() + dy;
        if (size_x < 120) {
            size_x = 120;
        }
        if (size_y < 130) {
            size_y = 130;
        }
        instance.setSize(size_x, size_y);
    }//GEN-LAST:event_lblCornerMouseDragged

    private void lblCornerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCornerMousePressed
        instance.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
        dx = lblCorner.getWidth() - evt.getX();
        dy = lblCorner.getHeight() - evt.getY();
    }//GEN-LAST:event_lblCornerMousePressed

    private void lblCornerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCornerMouseReleased
        instance.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        instance_width = (int) instance.getSize().getWidth();
        instance_height = (int) instance.getSize().getHeight();
    }//GEN-LAST:event_lblCornerMouseReleased

    private void chkCollectModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCollectModeActionPerformed
        int flags = evt.getModifiers();
        if ((flags & evt.ALT_MASK) > 0) {
            OJ.getData().getYtemDefs().setComposite(chkCollectMode.isSelected());
            OJ.getEventProcessor().fireYtemDefChangedEvent(null, YtemDefChangedEventOJ.COLLECT_MODE_CHANGED);
        } else {
            chkCollectMode.setSelected(!chkCollectMode.isSelected());//undo
            GenericDialog gd = new GenericDialog("Composite Objects", IJ.getInstance());
            gd.addMessage("Alt key must be down to change the 'Composite' checkbox");
            gd.addMessage("For more information, click 'Help'");
            gd.addHelp("http://simon.bio.uva.nl/objectj/3b-ManualTools.html");
            gd.showDialog();
        }

    }//GEN-LAST:event_chkCollectModeActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
    }//GEN-LAST:event_formFocusGained

    private void listYtemDefsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listYtemDefsMouseReleased
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            Object value = listYtemDefs.getSelectedValue();
            if (value != null) {
                if (!(OJ.getToolStateProcessor().getToolStateObject() instanceof CreateCellStateOJ)) {
                    ToolManagerOJ.getInstance().selectTool("Marker");
                    ToolManagerOJ.getInstance().selectTool("Marker");
                }
                YtemDefOJ obj = (YtemDefOJ) value;
                OJ.getData().getYtemDefs().setSelectedYtemDef(obj.getYtemDefName());
            }
        }
}//GEN-LAST:event_listYtemDefsMouseReleased

    private void jLabel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLabel1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1KeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        ij.IJ.showStatus("key in tool");
    }//GEN-LAST:event_formKeyPressed

    private void updateScrollPane() {
        listYtemDefs.setVisibleRowCount(OJ.getData().getYtemDefs().getYtemDefsCount() + 1);
        Dimension ld = listYtemDefs.getPreferredScrollableViewportSize();
        Dimension pd = new Dimension((int) ld.getWidth() + 20, (int) ld.getHeight());
        jPanel5.setPreferredSize(pd);
    }
    private Icon visibleIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Visible.gif"));
    private Icon invisibleIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Invisible.gif"));

    private void updateVisibilityPanel() {
        pnlVisibility.removeAll();
        for (int i = 0; i < OJ.getData().getYtemDefs().getYtemDefsCount(); i++) {
            JLabel lbl = new JLabel();
            if (OJ.getData().getYtemDefs().getYtemDefByIndex(i).isVisible()) {
                lbl.setIcon(visibleIcon);
            } else {
                lbl.setIcon(invisibleIcon);
            }
            lbl.setBorder(new EmptyBorder(6, 3, 5, 8));
            lbl.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent evt) {
                }

                public void mousePressed(MouseEvent evt) {
                    for (int i = 0; i < pnlVisibility.getComponentCount(); i++) {
                        if (pnlVisibility.getComponent(i) == evt.getComponent()) {
                            OJ.getData().getYtemDefs().getYtemDefByIndex(i).setVisible(!OJ.getData().getYtemDefs().getYtemDefByIndex(i).isVisible());
                        }
                    }
                }

                public void mouseReleased(MouseEvent evt) {
                }

                public void mouseEntered(MouseEvent evt) {
                }

                public void mouseExited(MouseEvent evt) {
                }
            });
            pnlVisibility.add(lbl);
        }
    }

    public void ytemDefChanged(YtemDefChangedEventOJ evt) {
        if (evt.getOperation() == YtemDefChangedEventOJ.COLLECT_MODE_CHANGED) {
            chkCollectMode.setSelected(OJ.getData().getYtemDefs().isComposite());
        } else {
            updateYtemDefList();
            updateScrollPane();
            pnlVisibility.setVisible(OJ.getData().getYtemDefs().isYtemVisibilitySwitchEnabled());//14.12.2011
            updateVisibilityPanel();
        }
    }
//

    public static void clearOJToolbarSelection() {
        ytemDefList.grpEditCell.setSelected(new DefaultButtonModel(), true);
    }

    public void keyTyped(KeyEvent e) {
        IJ.getInstance().keyTyped(e);
    }

    public void keyPressed(KeyEvent e) {
        IJ.getInstance().keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        IJ.getInstance().keyReleased(e);
    }

    public void updateYtemDefList() {
        ((YtemDefCellModelOJ) listYtemDefs.getModel()).fireContentsChanged();
        chkCollectMode.setSelected(OJ.getData().getYtemDefs().isComposite());
    }

    private void initComponentsExt() {
        addKeyListener(KeyEventManagerOJ.getInstance());

        if (instance_type == ToolsWindowOJ.FLOATING_WINDOW_TYPE) {
            lblCorner.setVisible(true);
        } else {
            lblCorner.setVisible(false);
            if (instance_type == ToolsWindowOJ.JFRAME_TYPE) {
                ((JFrame) ToolsWindowOJ.instance).setMenuBar(Menus.getMenuBar());
            }
        }

        updateScrollPane();
        reloadObjectTools();
        reloadMacroTools();

        updateVisibilityPanel();
        pnlVisibility.setVisible(OJ.getData().getYtemDefs().isYtemVisibilitySwitchEnabled());

        if (ToolManagerOJ.getInstance().getSelectedTool() != null) {
            updateToolButtonState(ToolManagerOJ.getInstance().getSelectedTool().getName());
        }
    }

    private void setButtonOpenStatus(boolean status) {
        buttCloseCell.setEnabled(status);
        ((YtemDefCellModelOJ) listYtemDefs.getModel()).fireContentsChanged();
    }

    public void ytemDefSelectionChanged(YtemDefSelectionChangedEventOJ evt) {
        listYtemDefs.setSelectedIndex(((YtemDefCellModelOJ) listYtemDefs.getModel()).getIndexOfElement(evt.getName()));
        ((YtemDefCellModelOJ) listYtemDefs.getModel()).fireContentsChanged();
    }

    public void cellChanged(CellChangedEventOJ evt) {
        switch (evt.getOperation()) {
            case CellChangedEventOJ.CELL_OPEN_EVENT:
                setButtonOpenStatus(true);
                break;
            case CellChangedEventOJ.CELL_CLOSE_EVENT:
                setButtonOpenStatus(false);
                break;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttCloseCell;
    private javax.swing.JCheckBox chkCollectMode;
    private javax.swing.ButtonGroup grpEditCell;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCorner;
    private javax.swing.JList listYtemDefs;
    private javax.swing.JPanel pnlHorizontal;
    private javax.swing.JPanel pnlMacrosTools;
    private javax.swing.JPanel pnlObjectTools;
    private javax.swing.JPanel pnlVisibility;
    // End of variables declaration//GEN-END:variables

    class YtemDefCellModelOJ extends AbstractListModel {

        public int getSize() {
            return OJ.getData().getYtemDefs().getYtemDefsCount();
        }

        public Object getElementAt(int index) {
            if (index < OJ.getData().getYtemDefs().getYtemDefsCount()) {
                return OJ.getData().getYtemDefs().getYtemDefByIndex(index);
            } else {
                return null;
            }
        }

        public int getIndexOfElement(String element) {
            for (int i = 0; i < getSize(); i++) {
                if (((YtemDefOJ) getElementAt(i)).getYtemDefName().equals(element)) {
                    return i;
                }
            }
            return -1;
        }

        public void fireContentsChanged() {
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }

    class YtemDefCellRendererOJ extends JLabel implements ListCellRenderer {

        private Border cellBorder = new EmptyBorder(3, 8, 3, 3);
        private Icon pointIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Point16x16.png"));
        private Icon lineIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Line16x16.png"));
        private Icon segLineIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Multiline16x16.png"));
        private Icon angleIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Angle16x16.png"));
        private Icon polygonIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Polygon16x16.png"));
        private Icon roiIcon = new ImageIcon(getClass().getResource(OJ.ICONS + "Roi16x16.png"));

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) {
            if (value == null) {
                return new JLabel();
            }
            setBorder(cellBorder);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            YtemDefOJ object_def = (YtemDefOJ) value;
            setForeground(object_def.getLineColor());
            setText(object_def.getYtemDefName());
            if (isSelected) {
                setBackground(new Color(33, 33, 33));//17.5.2010 javax.swing.UIManager.getDefaults().getColor("inactiveCaptionText").darker());
                //setBackground(list.getSelectionBackground());
            } else {
//                if (index % 2 == 0) {
                setBackground(Color.WHITE);
//                } else {
//                    setBackground(rowBackground);
//                }
            }
            switch (object_def.getYtemType()) {
                case YtemDefOJ.YTEM_TYPE_POINT:
                    setIcon(pointIcon);
                    break;
                case YtemDefOJ.YTEM_TYPE_LINE:
                    setIcon(lineIcon);
                    break;
                case YtemDefOJ.YTEM_TYPE_SEGLINE:
                    setIcon(segLineIcon);
                    break;
                case YtemDefOJ.YTEM_TYPE_ANGLE:
                    setIcon(angleIcon);
                    break;
                case YtemDefOJ.YTEM_TYPE_POLYGON:
                    setIcon(polygonIcon);
                    break;
                case YtemDefOJ.YTEM_TYPE_ROI:
                    setIcon(roiIcon);
                    break;
            }
            return this;
        }
    }

    public class ActionToolListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            ToolManagerOJ.getInstance().selectTool(((AbstractButton) evt.getSource()).getName());
        }
    }

    public class ToolListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            ToolManagerOJ.getInstance().selectTool(((AbstractButton) evt.getSource()).getName());
        }
    }

    public void toolListChanged(ToolListChangedEventOJ evt) {
        if ("macro".equals(evt.getGroup())) {
            reloadMacroTools();
        }
    }

    private void reloadObjectTools() {
        pnlObjectTools.removeAll();

        for (Enumeration e = ToolManagerOJ.getInstance().getObjectTools(); e.hasMoreElements();) {
            ToolOJ tool = (ToolOJ) e.nextElement();

            JToggleButton button = new javax.swing.JToggleButton() {
                public javax.swing.JToolTip createToolTip() {
                    return new oj.util.MultiLineToolTipOJ();
                }
            };
            Icon icon;
            if (ToolManagerOJ.getInstance().getSelectedTool() == tool) {
                icon = tool.getSelectedIcon();
            } else {
                icon = tool.getIcon();
            }
            button.setFocusPainted(false);
            button.setName(tool.getName());
            button.setIcon(icon);
            button.setToolTipText(tool.getTooltip());
            button.addActionListener(toolListener);
            grpEditCell.add(button);
            pnlObjectTools.add(button);
            objectTools.put(tool.getName(), button);
        }
    }

    private void reloadMacroTools() {
        macroToolsCount = 0;
        pnlMacrosTools.removeAll();

        for (Enumeration e = ToolManagerOJ.getInstance().getMacroTools(); e.hasMoreElements();) {
            ToolOJ tool = (ToolOJ) e.nextElement();
            JToggleButton button = new javax.swing.JToggleButton() {
                public javax.swing.JToolTip createToolTip() {
                    return new oj.util.MultiLineToolTipOJ();
                }
            };
            button.setFocusPainted(false);
            button.setName(tool.getName());
            button.setIcon(tool.getIcon());
            //button.setBackground(new Color(22, 77, 99));//3.2.2009
            button.setToolTipText(tool.getTooltip());
            button.addActionListener(toolListener);
            grpEditCell.add(button);
            pnlMacrosTools.add(button);
            macroTools.put(tool.getName(), button);
            macroToolsCount += 1;
        }

        instance_height = 205 + ((macroToolsCount + 1) / 2) * 28 + Math.min(12, Math.max((OJ.getData().getYtemDefs().getYtemDefsCount() + 1), 3)) * 28;
        ToolsWindowOJ.instance.setPreferredSize(new Dimension(75, instance_height));
        ToolsWindowOJ.instance.setSize(new Dimension(75, instance_height));
        ToolsWindowOJ.instance.pack();

    }

    public void toolSelectionChanged(ToolSelectionChangedEventOJ evt) {
        updateToolButtonState(evt.getName());
    }

    private void updateToolButtonState(String toolName) {
        for (Enumeration e = ToolManagerOJ.getInstance().getObjectTools(); e.hasMoreElements();) {
            ToolOJ tool = (ToolOJ) e.nextElement();
            Icon icon;
            if (tool.getName().equals(toolName)) {
                icon = tool.getSelectedIcon();
            } else {
                icon = tool.getIcon();
            }
            ((JToggleButton) objectTools.get(tool.getName())).setIcon(icon);
        }

        if (objectTools.containsKey(toolName)) {
            ((JToggleButton) objectTools.get(toolName)).setSelected(true);
        } else if (macroTools.containsKey(toolName)) {
            ((JToggleButton) macroTools.get(toolName)).setSelected(true);
        } else {
            clearOJToolbarSelection();
        }
    }
}
