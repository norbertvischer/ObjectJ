/*
 * OperandOJ.java
 */
package oj.project.results;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import oj.project.BaseAdapterOJ;

public class OperandOJ extends BaseAdapterOJ {

    private static final long serialVersionUID = 8877223348263168117L;
    private String ytemName;
    private int objectClone = 0;
    private int relPosition = 0;

    public OperandOJ() {
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeUTF(ytemName);
        stream.writeInt(objectClone);
        stream.writeInt(relPosition);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        ytemName = stream.readUTF();
        objectClone = stream.readInt();
        relPosition = stream.readInt();
    }

    public String getObjectName() {
        return ytemName;
    }

    public void setYtemName(String objectName) {
        this.ytemName = objectName;
        changed = true;
    }

    public int getYtemClone() {
        return objectClone;
    }

    public void setYtemClone(int objectClone) {
        this.objectClone = objectClone;
        changed = true;
    }

    public int getRelPosition() {
        return relPosition;
    }

    public void setRelPosition(int relPosition) {
        this.relPosition = relPosition;
        changed = true;
    }
}
