/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oj.project.results;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import oj.project.BaseAdapterOJ;

/**
 *
 * @author norbert
 */
public class PlotsOJ extends BaseAdapterOJ {

    public ArrayList <String> plotDefs = new ArrayList();//array of CellOJs

    public PlotsOJ() {
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.writeObject(plotDefs);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        plotDefs = (ArrayList) stream.readObject();
    }

    public ArrayList getPlotDefs() {
        return plotDefs;
    }
}
