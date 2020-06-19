/*
 * Processor.java
 * -- documented
 * - contains a number of Listener arrays (classes to be informed, e.g. all canvases)
 * - contains addListener methods (called by constructor of listening classes)
 * - contains the fire methods (called by e.g. by setMarker)
 *   (distributing "event" objects  to all listeners)

 *
 */
package oj.processor;

import java.util.ArrayList;
import oj.OJ;
import oj.project.IBaseOJ;
import oj.processor.events.*;

public class EventProcessorOJ {

  public static boolean BlockEventsOnDrag = false;
  public static boolean updateMarkers = true;
  //Collection of all listeners to which an event must be distributed
  protected ArrayList drawCellListeners = new ArrayList();
  protected ArrayList cellChangedListeners = new ArrayList();
  protected ArrayList imageChangedListeners = new ArrayList();
  protected ArrayList macroChangedListeners = new ArrayList();
  protected ArrayList ytemChangedListeners = new ArrayList();
  protected ArrayList columnChangedListeners = new ArrayList();
  protected ArrayList resultChangedListeners = new ArrayList();
  protected ArrayList ytemDefChangedListeners = new ArrayList();
  protected ArrayList qualifierChangedListeners = new ArrayList();
  protected ArrayList statisticsChangedListeners = new ArrayList();
  protected ArrayList ytemDefSelectionChangedListeners = new ArrayList();
  protected ArrayList cellProcessorStateChangedListeners = new ArrayList();

  /**
   * Of all eleven listener arrays, elements are removed if they are of type IBaseOJ
   * Unlinked columns missing?
   * Macro Listener removed
   */
  public void cleanListeners() {
    for (int i = drawCellListeners.size() - 1; i >= 0; i--) {
      if (drawCellListeners.get(i) instanceof IBaseOJ) {
        drawCellListeners.remove(i);
      }
    }
    for (int i = cellChangedListeners.size() - 1; i >= 0; i--) {
      if (cellChangedListeners.get(i) instanceof IBaseOJ) {
        cellChangedListeners.remove(i);
      }
    }
    for (int i = imageChangedListeners.size() - 1; i >= 0; i--) {
      if (imageChangedListeners.get(i) instanceof IBaseOJ) {
        imageChangedListeners.remove(i);
      }
    }
    for (int i = macroChangedListeners.size() - 1; i >= 0; i--) {
      if (macroChangedListeners.get(i) instanceof IBaseOJ) {
        macroChangedListeners.remove(i);
      }
    }
    for (int i = ytemChangedListeners.size() - 1; i >= 0; i--) {
      if (ytemChangedListeners.get(i) instanceof IBaseOJ) {
        ytemChangedListeners.remove(i);
      }
    }
    for (int i = columnChangedListeners.size() - 1; i >= 0; i--) {
      if (columnChangedListeners.get(i) instanceof IBaseOJ) {
        columnChangedListeners.remove(i);
      }
    }
    for (int i = resultChangedListeners.size() - 1; i >= 0; i--) {
      if (resultChangedListeners.get(i) instanceof IBaseOJ) {
        resultChangedListeners.remove(i);
      }
    }
    for (int i = ytemDefChangedListeners.size() - 1; i >= 0; i--) {
      if (ytemDefChangedListeners.get(i) instanceof IBaseOJ) {
        ytemDefChangedListeners.remove(i);
      }
    }
    for (int i = qualifierChangedListeners.size() - 1; i >= 0; i--) {
      if (qualifierChangedListeners.get(i) instanceof IBaseOJ) {
        qualifierChangedListeners.remove(i);
      }
    }
    for (int i = cellProcessorStateChangedListeners.size() - 1; i >= 0; i--) {
      if (cellProcessorStateChangedListeners.get(i) instanceof IBaseOJ) {
        cellProcessorStateChangedListeners.remove(i);
      }
    }
    for (int i = ytemDefSelectionChangedListeners.size() - 1; i >= 0; i--) {
      if (ytemDefSelectionChangedListeners.get(i) instanceof IBaseOJ) {
        ytemDefSelectionChangedListeners.remove(i);
      }
    }
    for (int i = statisticsChangedListeners.size() - 1; i >= 0; i--) {
      if (statisticsChangedListeners.get(i) instanceof IBaseOJ) {
        statisticsChangedListeners.remove(i);
      }
    }
  }

  /** include this listener if not yet included*/
  public void addDrawCellListener(DrawCellListenerOJ listener) {
    if (drawCellListeners.indexOf(listener) < 0) {
      drawCellListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addCellChangedListener(CellChangedListenerOJ listener) {
    if (cellChangedListeners.indexOf(listener) < 0) {
      cellChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addCellProcessorStateChangedListener(ToolStateProcessorChangedListenerOJ listener) {
    if (cellProcessorStateChangedListeners.indexOf(listener) < 0) {
      cellProcessorStateChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addColumnChangedListener(ColumnChangedListenerOJ listener) {
    if (columnChangedListeners.indexOf(listener) < 0) {
      columnChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addImageChangedListener(ImageChangedListener2OJ listener) {
    if (imageChangedListeners.indexOf(listener) < 0) {
      imageChangedListeners.add(listener);
    }
  }

  public void addMacroChangedListener(MacroChangedListenerOJ listener) {
    if (macroChangedListeners.indexOf(listener) < 0) {
      macroChangedListeners.add(listener);
    }
  }

  public void addResultChangedListener(ResultChangedListenerOJ listener) {
    if (resultChangedListeners.indexOf(listener) < 0) {
      resultChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addStatisticsChangedListener(StatisticsChangedListenerOJ listener) {
    if (statisticsChangedListeners.indexOf(listener) < 0) {
      statisticsChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addYtemChangedListener(YtemChangedListenerOJ listener) {
    if (ytemChangedListeners.indexOf(listener) < 0) {
      ytemChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addQualifierChangedListener(QualifierChangedListenerOJ listener) {
    if (qualifierChangedListeners.indexOf(listener) < 0) {
      qualifierChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addYtemDefSelectionChangedListener(YtemDefSelectionChangedListenerOJ listener) {
    if (ytemDefSelectionChangedListeners.indexOf(listener) < 0) {
      ytemDefSelectionChangedListeners.add(listener);
    }
  }

  /** include this listener if not yet included*/
  public void addYtemDefChangedListener(YtemDefChangedListenerOJ listener) {
    if (ytemDefChangedListeners.indexOf(listener) < 0) {
      ytemDefChangedListeners.add(listener);
    }
  }

  /** remove this listener if it still exists*/
  public void removeCellChangedListener(CellChangedListenerOJ listener) {
    int index = cellChangedListeners.indexOf(listener);
    if (index > 0) {
      cellChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeCellProcessorStateChangedListener(ToolStateProcessorChangedListenerOJ listener) {
    int index = cellProcessorStateChangedListeners.indexOf(listener);
    if (index > 0) {
      cellProcessorStateChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeColumnChangedListener(ColumnChangedListenerOJ listener) {
    int index = columnChangedListeners.indexOf(listener);
    if (index > 0) {
      columnChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeImageChangedListener(ImageChangedListener2OJ listener) {
    int index = imageChangedListeners.indexOf(listener);
    if (index > 0) {
      imageChangedListeners.remove(index);
    }
  }

  public void removeMacroChangedListener(MacroChangedListenerOJ listener) {
    int index = macroChangedListeners.indexOf(listener);
    if (index > 0) {
      macroChangedListeners.remove(index);
    }
  }

  public void removeResultChangedListener(ResultChangedListenerOJ listener) {
    int index = resultChangedListeners.indexOf(listener);
    if (index > 0) {
      resultChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeStatisticsChangedListener(StatisticsChangedListenerOJ listener) {
    int index = statisticsChangedListeners.indexOf(listener);
    if (index > 0) {
      statisticsChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeYtemChangedListener(YtemChangedListenerOJ listener) {
    int index = ytemChangedListeners.indexOf(listener);
    if (index > 0) {
      ytemChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeQualifierChangedListener(QualifierChangedListenerOJ listener) {
    int index = qualifierChangedListeners.indexOf(listener);
    if (index > 0) {
      qualifierChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeYtemDefSelectionChangedListener(YtemDefSelectionChangedListenerOJ listener) {
    int index = ytemDefSelectionChangedListeners.indexOf(listener);
    if (index > 0) {
      ytemDefSelectionChangedListeners.remove(index);
    }
  }

  /** remove this listener if it still exists*/
  public void removeYtemDefChangedListener(YtemDefChangedListenerOJ listener) {
    int index = ytemDefChangedListeners.indexOf(listener);
    if (index > 0) {
      ytemDefChangedListeners.remove(index);
    }
  }

  /** creates a new generic CellChangedEvent and sends it to all listeners*/
  public void fireCellChangedEvent() {
    if (OJ.isValidData()) {
      if (BlockEventsOnDrag || !updateMarkers) {
        return;
      }
      CellChangedEventOJ evt = new CellChangedEventOJ();
      for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
        OJ.getData().getResults().getColumns().getColumnByIndex(i).getStatistics().setStatisticsDirty();
      }
      for (int i = 0; i < cellChangedListeners.size(); i++) {
        ((CellChangedListenerOJ) cellChangedListeners.get(i)).cellChanged(evt);
      }
    }
  }

  /** creates a new CellChangedEvent depending on cellindex and operation, and sends it to all listeners*/
  public void fireCellChangedEvent(int cellIndex, int operation) {
    if (OJ.isValidData()) {
      if (BlockEventsOnDrag || !updateMarkers) {
        return;
      }
      CellChangedEventOJ evt = new CellChangedEventOJ(cellIndex, operation);
      for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
        OJ.getData().getResults().getColumns().getColumnByIndex(i).getStatistics().setStatisticsDirty();
      }
      for (int i = 0; i < cellChangedListeners.size(); i++) {
        ((CellChangedListenerOJ) cellChangedListeners.get(i)).cellChanged(evt);
      }
    }
  }

  /** creates a new ToolStateProcessorChangedEventOJ and executes all Listener's toolStateChanged  */
  public void fireCellProcessorStateChangedEvent(int state) {
    if (OJ.isValidData()) {
      ToolStateProcessorChangedEventOJ evt = new ToolStateProcessorChangedEventOJ(state);
      for (int i = 0; i < cellProcessorStateChangedListeners.size(); i++) {
        ((ToolStateProcessorChangedListenerOJ) cellProcessorStateChangedListeners.get(i)).toolStateChanged(evt);
      }
    }
  }

  /** creates a new ColumnChangedEventOJ and executes all Listener's columnChanged()  */
  public synchronized void fireColumnChangedEvent(String oldName, String newName, int operation) {//10.5.2010
    if (OJ.isValidData()) {
      ColumnChangedEventOJ evt = new ColumnChangedEventOJ(oldName, newName, operation);
      for (int i = 0; i < columnChangedListeners.size(); i++) {
        ((ColumnChangedListenerOJ) columnChangedListeners.get(i)).columnChanged(evt);
      }
    }
  }

  /** creates a new ResultChangedEventOJ and executes all Listener's resultChanged()  */
  public synchronized void fireResultChangedEvent(String columnName, int row, int operation) {//10.5.2010
    if (OJ.isValidData()) {
      ResultChangedEventOJ evt = new ResultChangedEventOJ(columnName, row, operation);
      for (int i = 0; i < resultChangedListeners.size(); i++) {
        ((ResultChangedListenerOJ) resultChangedListeners.get(i)).resultChanged(evt);
      }
    }
  }

  /** creates a new ImageChangedEventOJ and executes all Listener's imageChanged()  */
  public void fireImageChangedEvent(int operation) {
    if (OJ.isValidData()) {
      ImageChangedEventOJ evt = new ImageChangedEventOJ(operation);
      for (int i = 0; i < imageChangedListeners.size(); i++) {
        ((ImageChangedListener2OJ) imageChangedListeners.get(i)).imageChanged(evt);
      }
    }
  }

  /** creates a new ImageChangedEventOJ and executes all Listener's imageChanged()  */
  public void fireImageChangedEvent(String name, int operation) {
    if (OJ.isValidData()) {
      ImageChangedEventOJ evt = new ImageChangedEventOJ(name, operation);
      for (int i = 0; i < imageChangedListeners.size(); i++) {
        ((ImageChangedListener2OJ) imageChangedListeners.get(i)).imageChanged(evt);
      }
    }
  }

  /** creates a new ImageChangedEventOJ and executes all Listener's imageChanged()  */
  public void fireImageChangedEvent(String firstImageName, String secondImageName, int operation) {
    if (OJ.isValidData()) {
      ImageChangedEventOJ evt = new ImageChangedEventOJ(firstImageName, secondImageName, operation);
      for (int i = 0; i < imageChangedListeners.size(); i++) {
        ((ImageChangedListener2OJ) imageChangedListeners.get(i)).imageChanged(evt);
      }
    }
  }

  public void fireMacroChangedEvent(String name, int operation) {
    if (OJ.isValidData()) {
      MacroChangedEventOJ evt = new MacroChangedEventOJ(name, operation);
      for (int i = 0; i < macroChangedListeners.size(); i++) {
        ((MacroChangedListenerOJ) macroChangedListeners.get(i)).macroChanged(evt);
      }
    }
  }

  /** creates a new StatisticsChangedEventOJ and executes all Listener's statisticsChanged()  */
  public void fireStatisticsChangedEvent(String name, int operation) {
    if (OJ.isValidData()) {
      if (BlockEventsOnDrag || !updateMarkers) {
        return;
      }
      StatisticsChangedEventOJ evt = new StatisticsChangedEventOJ(name, operation);
      for (int i = 0; i < statisticsChangedListeners.size(); i++) {
        ((StatisticsChangedListenerOJ) statisticsChangedListeners.get(i)).statisticsChanged(evt);
      }
    }
  }

  /** Sets all columns dirty, creates a new YtemChangedEventOJ,
   *  executes all Listener's ytemChanged(), and updates open images  */
  public void fireYtemChangedEvent() {
    if (OJ.isValidData()) {
      if (BlockEventsOnDrag || !updateMarkers) {
        return;
      }
      YtemChangedEventOJ evt = new YtemChangedEventOJ();
      for (int i = 0; i < OJ.getData().getResults().getColumns().getAllColumnsCount(); i++) {
        OJ.getData().getResults().getColumns().getColumnByIndex(i).setValuesDirty();
      }
      for (int i = 0; i < ytemChangedListeners.size(); i++) {
        ((YtemChangedListenerOJ) ytemChangedListeners.get(i)).ytemChanged(evt);
      }
      OJ.getImageProcessor().updateOpenImages();
    }
  }

  /** creates a new QualifierChangedEventOJ and executes all Listener's qualifierChanged()  */
  public void fireQualifierChangedEvent(String name, int operation) {
    if (OJ.isValidData()) {
      QualifierChangedEventOJ evt = new QualifierChangedEventOJ(name, operation);
      for (int i = 0; i < qualifierChangedListeners.size(); i++) {
        ((QualifierChangedListenerOJ) qualifierChangedListeners.get(i)).qualifierChanged(evt);
      }
    }
  }

  /** creates a new YtemDefSelectionChangedEventOJ and executes all Listener's ytemDefSelectionChanged()  */
  public void fireYtemDefSelectionChangedEvent(String name) {
    if (OJ.isValidData()) {
      YtemDefSelectionChangedEventOJ evt = new YtemDefSelectionChangedEventOJ(name);
      for (int i = 0; i < ytemDefSelectionChangedListeners.size(); i++) {
        ((YtemDefSelectionChangedListenerOJ) ytemDefSelectionChangedListeners.get(i)).ytemDefSelectionChanged(evt);
      }
    }
  }

  /** creates a new YtemDefSelectionChangedEventOJ and executes all Listener's ytemDefSelectionChanged()  */
  public void fireYtemDefChangedEvent(String name, int operation) {
    if (OJ.isValidData()) {
      YtemDefChangedEventOJ evt = new YtemDefChangedEventOJ(name, operation);
      for (int i = 0; i < ytemDefChangedListeners.size(); i++) {
        ((YtemDefChangedListenerOJ) ytemDefChangedListeners.get(i)).ytemDefChanged(evt);
      }
    }
  }

  /** creates a new YtemDefSelectionChangedEventOJ and executes all Listener's ytemDefSelectionChanged()  */
  public void fireYtemDefChangedEvent(String oldName, String newName, int operation) {
    if (OJ.isValidData()) {
      YtemDefChangedEventOJ evt = new YtemDefChangedEventOJ(oldName, newName, operation);
      for (int i = 0; i < ytemDefChangedListeners.size(); i++) {
        ((YtemDefChangedListenerOJ) ytemDefChangedListeners.get(i)).ytemDefChanged(evt);
      }
    }
  }

  /** creates a new YtemDefSelectionChangedEventOJ and executes all Listener's ytemDefSelectionChanged()  */
  public void fireYtemDefChangedEvent(int operation) {
    if (OJ.isValidData()) {
      YtemDefChangedEventOJ evt = new YtemDefChangedEventOJ(operation);
      for (int i = 0; i < ytemDefChangedListeners.size(); i++) {
        ((YtemDefChangedListenerOJ) ytemDefChangedListeners.get(i)).ytemDefChanged(evt);
      }
    }
  }
}
