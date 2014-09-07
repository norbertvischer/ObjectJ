/**
 * CellActionsOJ.java
 * fully documented 18.5.2010
 *
 * CellActionsOJ supplies listener methods that are
 * connected to submenu items of ObjectJ>Objects
 */
package oj.gui.menuactions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import oj.OJ;
import oj.gui.DeleteCellsOJ;
import oj.processor.state.SelectCellStateOJ;
import oj.project.CellOJ;
import oj.project.LocationOJ;
import oj.project.YtemDefOJ;
import oj.project.YtemOJ;
import oj.project.shapes.AngleOJ;
import oj.project.shapes.LineOJ;
import oj.project.shapes.PointOJ;
import oj.project.shapes.PolygonOJ;
import oj.project.shapes.RoiOJ;
import oj.project.shapes.SeglineOJ;
import oj.gui.settings.ProjectSettingsOJ;

public class CellActionsOJ {

    /**Shows the Delete Objects Dialog, triggered by menu
     */
    public static ActionListener DeleteCellsAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (OJ.getData().getCells().getCellsCount() > 0) {
                DeleteCellsOJ dod = new DeleteCellsOJ(ProjectSettingsOJ.getInstance(), true);
                dod.setVisible(true);
                if (dod.getCloseStatus() == DeleteCellsOJ.CLOSE_DELETE) {
                    OJ.getDataProcessor().removeCells(dod.getDeleteStatus(), dod.getMinValue(), dod.getMaxValue());
                }
            } else {
                ij.IJ.showMessage("Nothing to Delete");
            }
        }
    };
    /**Shows the Delete All Objects Dialog, triggered by menu
     */
    public static ActionListener DeleteAllCellsAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (OJ.getData() != null) {
                int count = OJ.getData().getCells().getCellsCount();
                if (count > 0) {



                    String deleteAllMsg = "Delete All " + count + " Objects?";

                    String forgetMsg = "Forget Linked Images";

                    GenericDialog gd = new GenericDialog("Delete Objects");
                    gd.addMessage(deleteAllMsg);
                    gd.addCheckbox(forgetMsg, false);

                    gd.showDialog();

                    boolean unlink = gd.getNextBoolean();

                    if (gd.wasCanceled()) {
                        return;
                    }
                    if (unlink) {
                        OJ.getData().getImages().removeAllImages(false);
                    } else {
                        OJ.getDataProcessor().removeCells(DeleteCellsOJ.DELETE_RANGE, 0, count - 1);
                    }
                }
            }
        }
    };
    /** Deletes selected object, triggered by menu
     */
    public static ActionListener DeleteSelectedCellAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            int cell_index = OJ.getData().getCells().getSelectedCellIndex();
            if (cell_index >= 0) {
                if (OJ.getToolStateProcessor().getToolStateObject() instanceof SelectCellStateOJ) {
                    OJ.getDataProcessor().unselectCell();
                }
                OJ.getDataProcessor().removeCellByIndex(cell_index);
            }
        }
    };
    /** Asks the user for an object number and shows that object
     * with underlying image (triggered by menu)
     */
    public static ActionListener ShowCellAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            final int one = 1;




            int cellToShow = 1;
            GenericDialog gd = new GenericDialog("Show Object");
            gd.addNumericField("Object to show", cellToShow, 0);
            gd.showDialog();
            if (gd.wasCanceled()) {
                return;
            }
            cellToShow = (int) gd.getNextNumber();
            OJ.getDataProcessor().showCell(cellToShow - one);
        }
    };
    /**Brings the qualifier panel to the front (triggered by menu)
     *
     */
    public static ActionListener QualifyCellsAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            ViewActionsOJ.SettingsAction.actionPerformed(e);
            ProjectSettingsOJ.getInstance().selectQualifiersPanel();
        }
    };
    /**
     * Converts current roi to an item. Not all conversions are possible, i.e.
     * a line selection cannot converted into a polygon item type etc.
     */
    public static ActionListener ROIToYtemAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            YtemDefOJ selectedYtem = OJ.getData().getYtemDefs().getSelectedObjectDef();
            if (OJ.getImageProcessor().getCurrentImageIndex() < 0) {
                return;
            }
            ImagePlus imp = IJ.getImage();
            if (imp == null) {
                return;
            }
            Roi roi = imp.getRoi();
            if (roi == null) {
                return;
            }
            if ((roi.getType() == Roi.TRACED_ROI) || (roi.getType() == Roi.POLYGON) || (roi.getType() == Roi.FREEROI) || (roi.getType() == Roi.LINE) || (roi.getType() == Roi.RECTANGLE) || (roi.getType() == Roi.POLYLINE) || (roi.getType() == Roi.ANGLE) || (roi.getType() == Roi.POINT) || (roi.getType() == Roi.OVAL)) {
                CellOJ cell = new CellOJ();
                cell.setImageName(imp.getTitle());
                cell.setStackIndex(imp.getCurrentSlice());
                YtemOJ ytem;
                String ytemDefName;
                switch (roi.getType()) {
                    case Roi.RECTANGLE:
                    case Roi.POLYGON:
                        ytem = new PolygonOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_POLYGON);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            if (selectedYtem != null && selectedYtem.getYtemType() == YtemDefOJ.YTEM_TYPE_ROI) {//also put rectangle into a roi object if it is selected
                                ytem = new RoiOJ();
                                ytem.setObjectDef(ytemDefName);
                                break;
                            } else {
                                IJ.showMessage("ROI to item", "There is no item definition of type polygon");
                                return;
                            }
                        }
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type polygon");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    case Roi.LINE:
                        ytem = new LineOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_LINE);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type line");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    case Roi.POLYLINE:
                        ytem = new SeglineOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_SEGLINE);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type polyline");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    case Roi.ANGLE:
                        ytem = new AngleOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_ANGLE);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type angle");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    case Roi.POINT:
                        ytem = new PointOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_POINT);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type point");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    case Roi.FREEROI:
                    case Roi.OVAL:
                    case Roi.TRACED_ROI:
                        ytem = new RoiOJ();
                        ytemDefName = CellActionsOJ.getYtemDefName(YtemDefOJ.YTEM_TYPE_ROI);
                        if ((ytemDefName == null) || (ytemDefName.equals(""))) {
                            IJ.showMessage("ROI to item", "There is no item definition of type ROI");
                            return;
                        }
                        ytem.setObjectDef(ytemDefName);
                        break;
                    default:
                        return;
                }
                Polygon poly = roi.getPolygon();
                //here to continue
                int[] xc = poly.xpoints;
                int[] yc = poly.ypoints;
                if (roi.getType() == Roi.LINE) {
                    ytem.add(new LocationOJ(xc[0], yc[0], cell.getStackIndex()));
                    ytem.add(new LocationOJ(xc[1], yc[1], cell.getStackIndex()));
                } else if (roi.getType() == Roi.POINT) {
                    ytem.add(new LocationOJ(xc[0], yc[0], cell.getStackIndex()));
                } else {
                    for (int i = 0; i < xc.length; i++) {
                        ytem.add(new LocationOJ(xc[i], yc[i], cell.getStackIndex()));
                    }
                }
                cell.add(ytem);
                OJ.getDataProcessor().addCell(cell);
            }
        }
    };

    private static String getYtemDefName(int ytemDefType) {
        if (OJ.getData() == null) {
            return null;
        }
        YtemDefOJ selectedYtem = OJ.getData().getYtemDefs().getSelectedObjectDef();
        if (selectedYtem!= null && selectedYtem.getYtemType() == ytemDefType) {
            return selectedYtem.getYtemDefName();
        }    
        for (int i = 0; i < OJ.getData().getYtemDefs().getYtemDefsCount(); i++) {
            YtemDefOJ ytemDef = OJ.getData().getYtemDefs().getYtemDefByIndex(i);
            if (ytemDef.getYtemType() == ytemDefType) {
                return ytemDef.getYtemDefName();
            }
        }
        return null;
    }
}
