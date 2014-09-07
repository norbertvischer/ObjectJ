/*
 * IIOProviderOJ.java
 * -- documented
 *
 */
package oj.io.spi;

import oj.project.DataOJ;
import oj.io.InputOutputOJ.ProjectIOExceptionOJ;

/**
 * Interface IOProvider to predefine load and saving project.
 * Implemented by:
 *  XMLStreamProviderOJ,
 *  JavaObjectProviderOJ
 */
public interface IIOProviderOJ {
    
    public String getName();
    
    public String getVersion();
    
    public boolean isValidData(String directory, String filename);

    public DataOJ loadProject(String directory, String filename) throws ProjectIOExceptionOJ ;

    public void saveProject(DataOJ data, String directory, String filename) throws ProjectIOExceptionOJ ;
}
