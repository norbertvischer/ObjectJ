/*
 * QualifiersOJ.java
 */
package oj.project.results;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import oj.OJ;
import oj.project.BaseAdapterOJ;
import oj.project.IBaseOJ;
import oj.processor.events.ColumnChangedEventOJ;
import oj.processor.events.ColumnChangedListenerOJ;
import oj.processor.events.QualifierChangedEventOJ;

public class QualifiersOJ extends BaseAdapterOJ implements ColumnChangedListenerOJ {

    private static final long serialVersionUID = -3684502130731974820L;
    public static final int QUALIFY_METHOD_ALL = 0;
    public static final int QUALIFY_METHOD_NONE = 1;
    public static final int QUALIFY_METHOD_ARBITRARY = 2;
    public static final int QUALIFY_METHOD_IF = 3;
    public static final int QUALIFY_METHOD_UNLESS = 4;
    public static final int QUALIFY_METHOD_CHECK = 5;
    private int qualifyMethod = QualifiersOJ.QUALIFY_METHOD_ALL;
    private boolean qualifyInvert = false;
    private ArrayList qualifiers = new ArrayList();

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeInt(qualifyMethod);
        stream.writeBoolean(qualifyInvert);
        stream.writeObject(qualifiers);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        qualifyMethod = stream.readInt();
        qualifyInvert = stream.readBoolean();
        qualifiers = (ArrayList) stream.readObject();
    }

    /**
     * For converting Integers to Stings
     */
    public static String getQualifyMethodName(int method) {
        switch (method) {
            case QualifiersOJ.QUALIFY_METHOD_ALL:
                return "all";
            case QualifiersOJ.QUALIFY_METHOD_NONE:
                return "none";
            case QualifiersOJ.QUALIFY_METHOD_ARBITRARY:
                return "arbitrary";
            case QualifiersOJ.QUALIFY_METHOD_IF:
                return "if";
            case QualifiersOJ.QUALIFY_METHOD_UNLESS:
                return "unless";
            case QualifiersOJ.QUALIFY_METHOD_CHECK:
                return "check";
            default:
                return "none";
        }
    }

    /**
     * For converting Stings to Integers
     */
    public static int getQualifyMethod(String method) {
        if ("all".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_ALL;
        } else if ("none".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_NONE;
        } else if ("arbitrary".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_ARBITRARY;
        } else if ("if".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_IF;
        } else if ("unless".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_UNLESS;
        } else if ("check".equals(method)) {
            return QualifiersOJ.QUALIFY_METHOD_CHECK;
        } else {
            return QualifiersOJ.QUALIFY_METHOD_NONE;
        }
    }

    public void initAfterUnmarshalling(IBaseOJ parent) {
        super.initAfterUnmarshalling(parent);
        qualifyMethod = QualifiersOJ.QUALIFY_METHOD_ALL;
        qualifyInvert = false;
        if (qualifiers == null) {
            qualifiers = new ArrayList();
        } else {
            for (int i = 0; i < qualifiers.size(); i++) {
                QualifierOJ qualifier = (QualifierOJ) qualifiers.get(i);
                qualifier.initAfterUnmarshalling(this);
            }
        }
        OJ.getEventProcessor().addColumnChangedListener(this);
    }

    public boolean getChanged() {
        if (super.getChanged()) {
            return true;
        } else {
            for (int i = 0; i < qualifiers.size(); i++) {
                if (((QualifierOJ) qualifiers.get(i)).getChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setChanged(boolean changed) {
        super.setChanged(changed);
        for (int i = 0; i < qualifiers.size(); i++) {
            ((QualifierOJ) qualifiers.get(i)).setChanged(changed);
        }
    }

    public int getQualifyMethod() {
        return qualifyMethod;
    }

    public void setQualifyMethod(int qualifyMethod, boolean updateNeeded) {
        this.qualifyMethod = qualifyMethod;
        changed = true;
        if (updateNeeded)//21.8.2011
        {
            OJ.getEventProcessor().fireQualifierChangedEvent(null, QualifierChangedEventOJ.QUALIFIER_METHOD_CHANGED);
        }
    }

    public boolean addQualifier(QualifierOJ qualifier) {
        if (qualifiers.add(qualifier)) {
            qualifier.setParent(this);
            changed = true;
            OJ.getEventProcessor().fireQualifierChangedEvent(qualifier.getColumnName(), QualifierChangedEventOJ.QUALIFIER_ITEM_ADDED);
            return true;
        }
        return false;
    }

    public int getQualifiersCount() {
        return qualifiers.size();
    }

    public QualifierOJ getQualifierByIndex(int index) {
        if ((index >= 0) && (index < qualifiers.size())) {
            return (QualifierOJ) qualifiers.get(index);
        }
        return null;
    }

    public QualifierOJ getQualifierByName(String name) {
        for (int i = 0; i < qualifiers.size(); i++) {
            if (((QualifierOJ) qualifiers.get(i)).getColumnName().equals(name)) {
                /*;*/ // semicolon removed n_7.1.2007
                return (QualifierOJ) qualifiers.get(i);
            }
        }
        return null;
    }

    public QualifierOJ setQualifier(int index, QualifierOJ qualifier) {
        QualifierOJ old_qualifier = (QualifierOJ) qualifiers.get(index);
        qualifiers.set(index, qualifier);
        qualifier.setParent(this);
        changed = true;
        return old_qualifier;
    }

    public int indexOfQualifier(QualifierOJ qualifier) {
        return qualifiers.indexOf(qualifier);
    }

    public void removeQualifierByIndex(int index) {
        String columnName = getQualifierByIndex(index).getColumnName();
        qualifiers.remove(index);
        changed = true;
        OJ.getEventProcessor().fireQualifierChangedEvent(columnName, QualifierChangedEventOJ.QUALIFIER_ITEM_DELETED);
    }

    public void removeQualifier(QualifierOJ qualifier) {
        qualifiers.remove(qualifier);
        changed = true;
        OJ.getEventProcessor().fireQualifierChangedEvent(qualifier.getColumnName(), QualifierChangedEventOJ.QUALIFIER_ITEM_DELETED);
    }

    public QualifierOJ[] qualifiersToArray() {
        QualifierOJ[] result = new QualifierOJ[qualifiers.size()];
        System.arraycopy(qualifiers, 0, result, 0, qualifiers.size());
        return result;
    }

    public boolean getQualifyInvert() {
        return qualifyInvert;
    }

    public void setQualifyInvert(boolean qualifyInvert) {
        this.qualifyInvert = qualifyInvert;
        changed = true;
    }

    public void clearQualifiers() {
        qualifiers.clear();
        changed = true;
    }

    public void columnChanged(ColumnChangedEventOJ evt) {
        switch (evt.getOperation()) {
            case ColumnChangedEventOJ.COLUMN_DELETED:
                for (int i = getQualifiersCount() - 1; i >= 0; i--) {
                    if (getQualifierByIndex(i).getColumnName().equals(evt.getOldName())) {
                        removeQualifierByIndex(i);
                        OJ.getEventProcessor().fireQualifierChangedEvent(evt.getOldName(), QualifierChangedEventOJ.QUALIFIER_ITEM_DELETED);
                    }
                }
                break;
            case ColumnChangedEventOJ.COLUMN_EDITED:
                if (!evt.getNewName().equals(evt.getOldName())) {
                    for (int i = 0; i < getQualifiersCount(); i++) {
                        if (getQualifierByIndex(i).getColumnName().equals(evt.getOldName())) {
                            getQualifierByIndex(i).setColumnName(evt.getNewName());
                            OJ.getEventProcessor().fireQualifierChangedEvent(evt.getOldName(), QualifierChangedEventOJ.QUALIFIER_ITEM_EDITED);
                        }
                    }
                }
                break;
        }
    }
}
