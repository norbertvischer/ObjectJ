package oj.plugin;

import ij.*;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ColorProcessor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import oj.OJ;
import oj.graphics.CustomCanvasOJ;
import oj.gui.KeyEventManagerOJ;
import oj.gui.MouseEventManagerOJ;
import oj.processor.events.*;
import oj.processor.state.CreateCellStateOJ;
import oj.processor.state.DeleteCellStateOJ;
import oj.processor.state.ToolStateOJ;
import oj.util.ImageJAccessOJ;

public class GlassWindowOJ extends JFrame implements CellChangedListenerOJ, DrawCellListenerOJ, YtemChangedListenerOJ {

    static int pressedX, pressedY, globalPressedX, globalPressedY;
    static public int backgroundColor = 0x0000ff10;
    static boolean resizing, moving;
    public static int robotCommand = 0;
    public static int left = 20, top = 80, width = 440, height = 330;
    public static int xx = -1, yy = -1;//for
    public Robot robot2;
    public static GlassWindowOJ glassWin;
    public ImagePlus connectedImp;
    public static final int BAR_HEIGHT = 22;
    public static final int CLOSE_BOX_X = 15;
    public static final int CLOSE_BOX_Y = 12;
    public static final int GRAB_TO_CURRENT = 1, GRAB_TO_NEW = 2;
    public static final int k12 = 12;

    public void run(String arg) {
    }

    public  boolean inBar(int x, int y) {
        if (inCloseBox(x, y)) return false;return y < BAR_HEIGHT;
    }
   public  boolean inCloseBox(int x, int y) {
        return (x > CLOSE_BOX_X-7 && x < CLOSE_BOX_X + 7 &&y > CLOSE_BOX_Y-7 && y< CLOSE_BOX_X + 7 );
    }

    public boolean inResizeBox(int x, int y) {
        return x > getWidth() - k12 && x > getHeight() - k12;
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        ij.IJ.showStatus(e.toString());
    }

    public void keyPressed(KeyEvent e) {
        ij.IJ.showStatus(e.toString());
    }

    public void cellChanged(CellChangedEventOJ evt) {

        if (showing()) {
            this.repaint();
        }
    }

    public void ytemChanged(YtemChangedEventOJ evt) {
        if (showing()) {
            IJ.wait(5);
            this.repaint();
        }
    }

    public void update() {
        if (showing()) {
            this.repaint();
        }
    }

    public void drawCell(DrawCellEventOJ evt) {

        if (showing()) {
            this.repaint();
        }
    }

    public static boolean exists() {
        return (glassWin != null);
    }

    public static boolean showing() {
        if (glassWin != null) {
            return glassWin.isShowing();
        }
        return false;
    }

    public static GlassWindowOJ getInstance() {//only one instance allowed

        if (IJ.isWindows() && !IJ.isJava17()) {
            IJ.error("Java 1.7 required for Windows OS");
            return null;
        }

        if (glassWin == null) {
            glassWin = new GlassWindowOJ();
            glassWin.setDefaultCloseOperation(HIDE_ON_CLOSE);

        }
        //glassWin.setVisible(true);5.11.2012
        return glassWin;
    }

    public void close() {
    }

    public void setImagePlus(ImagePlus imp) {
        connectedImp = imp;

    }

    public ImagePlus getImagePlus() {

        return connectedImp;
    }

    private GlassWindowOJ() {
        super("Glass");
        JFrame.setDefaultLookAndFeelDecorated(true);
        if (ij.IJ.isMacOSX()) {
            getRootPane().putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
        }
        setUndecorated(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }
        });

        addMouseListener(MouseEventManagerOJ.getInstance());
        addMouseMotionListener(MouseEventManagerOJ.getInstance());
        addKeyListener(KeyEventManagerOJ.getInstance());
        OJ.getEventProcessor().addYtemChangedListener(this);
        OJ.getEventProcessor().addCellChangedListener(this);
        OJ.getEventProcessor().addDrawCellListener(this);

        setBackground(new Color(0, 0, 0, 0));
        setSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                int ww = getParent().getWidth();
                int hh = getParent().getHeight();
                if (g instanceof Graphics2D) {

                    borrowOverlay(g);

                    Graphics2D g2d = (Graphics2D) g;

                    g2d.setColor(Color.LIGHT_GRAY);


                    int alpha = backgroundColor >> 24 & 255;
                    int red = backgroundColor >> 16 & 255;
                    int green = backgroundColor >> 8 & 255;
                    int blue = backgroundColor & 255;

                    g2d.setColor(new Color(alpha, red, green, blue));
                    g2d.fillRect(0, 0, ww, hh);


                    g2d.setColor(Color.cyan);
                    g2d.drawRect(1, 1, ww - 3, hh - 3);
                    g2d.drawRect(ww - k12, hh - k12, k12 - 2, k12 - 2);
                    g2d.drawRect(1, 1, k12 - 2, k12 - 2);

                    g2d.setColor(Color.blue);

                    g2d.drawRect(ww - k12 - 1, hh - k12 - 1, k12, k12);

                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(0, 0, ww, BAR_HEIGHT);

                    g2d.setColor(new Color(254, 0, 15));//special red: 0x0fe000f
                    g2d.fillOval(CLOSE_BOX_X - 7, CLOSE_BOX_Y - 7, 14, 14);//title bar
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setColor(Color.blue);
                    g2d.drawRect(0, 0, ww - 1, hh - 1);
                    g2d.drawString("Glass", ww / 2 - 50, 16);
                }
            }
        };



        OJ.getEventProcessor().addCellChangedListener(this);

        add(panel);
        pack();
        setVisible(true);

        new RobotThread("RobotThread").start();
    }

    public void borrowOverlay(Graphics g) {
        int ww = getWidth();
        g.clearRect(0, 0, getWidth(), getHeight());
        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp == null || !OJ.getImageProcessor().isLinked(imp)) {
            return;
        }
        CustomCanvasOJ myCanvas = new CustomCanvasOJ(imp, OJ.getData(), imp.getTitle());
        myCanvas.drawOverlayoj(g);
    }

    public void captureScreen() {
        Point corner = this.getLocationOnScreen();
        Component comp = this.getComponent(0);
        int hh = comp.getBounds().height;
        int ww = comp.getBounds().width;
        setVisible(false);
        ImagePlus imp = ij.IJ.getImage();
        if (imp == null) {
            return;
        }
        long freeMem = Runtime.getRuntime().freeMemory();
        if (freeMem - hh * ww < 1024 * 1024 * 20) {//20 MByte 
            IJ.showMessage("out of memory");
            return;
        }
        try {
            if (robot2 == null) {
                robot2 = new Robot();
            }
            Rectangle glassRect = new Rectangle(corner.x, corner.y, ww, hh);
            Rectangle pollRect = new Rectangle(corner.x, corner.y, 24, 24);
            if (glassRect.width != imp.getWidth() || glassRect.height != imp.getHeight()) {
                ImageJAccessOJ.InterpreterAccess.interpError("GlassWindow and connected Stack have different size");
            } else {
                robot2.waitForIdle();
                robot2.setAutoDelay(2);
                ColorProcessor cp = null;

                IJ.wait(10);
                Image img = robot2.createScreenCapture(glassRect);
                cp = null;
                if (img != null) {
                    //imp.lock();
                    cp = new ColorProcessor(img);
                }
                if (cp != null) {
                    imp.lock();

                    if (robotCommand == GRAB_TO_NEW) {
                        imp.getStack().addSlice(cp);
                        imp.setSlice(imp.getStackSize());
                    } else {
                        imp.setProcessor(cp);
                    }

                    imp.unlock();
                    imp.show();

                }
            }


        } catch (Exception e) {
            ij.IJ.showMessage("Robot exception:" + e.toString());
        }
        setVisible(true);
    }

    class RobotThread extends Thread {

        public RobotThread(String str) {
            super(str);
        }

        public void run() {
            while (true) {
                if (robotCommand > 0) {
                    captureScreen();
                    IJ.wait(2);
                    robotCommand = 0;
                }
                if (xx != -1 && yy != -1) {//14.12.2012
                    IJ.runMacro("ojSetMarker(" + xx + "," + yy + ");");
                    xx = -1;
                    yy = -1;
                }
                try {
                    sleep(2);
                } catch (InterruptedException e) {
                    IJ.showMessage("Thread interrupted");
                }
            }
        }
    }
}
