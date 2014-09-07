/* JavaObjectProviderOJ.java
 * -- documented
 *
 * one of the two providers
 * provides methods saving/loading project file in binary type
 */
package oj.io.spi;

import ij.IJ;
import ij.ImagePlus;
import ij.VirtualStack;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.RoiEncoder;
import ij.io.TiffEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import oj.project.DataOJ;
import oj.io.InputOutputOJ.ProjectIOExceptionOJ;
import oj.util.UtilsOJ;

public class JavaObjectProviderOJ implements IIOProviderOJ {

    private final String name = "javaobject";
    private final String version = "v1.0";

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void saveProject(DataOJ data, String directory, String filename) throws ProjectIOExceptionOJ {

        
        
       oj.OJ.addMagicBytes=!ij.IJ.altKeyDown();//23.12.2012
        
        
        FileInfo fi = null;
        DataOutputStream out = null;
        boolean testFlag = false;
        String title = null;

            Calendar date = Calendar.getInstance();
            String tmpName = "tmp-" + date.get(Calendar.HOUR_OF_DAY) + "-" + date.get(Calendar.MINUTE) + "-" + date.get(Calendar.SECOND) + ".ojj";
            File oldOjjFile = new File(directory, filename);
            File newOjjFile = new File(directory, tmpName);




            byte[] magicBytes = new byte[]{'o', 'j', 'j', 0};
            FileOutputStream fileOut = null;

            ZipOutputStream zipOut = null;
            BufferedOutputStream bufferedOut = null;

            ObjectOutputStream objOut = null;
            boolean ok = true;
            try {
                fileOut = new FileOutputStream(newOjjFile);

                bufferedOut = new BufferedOutputStream(fileOut);
                if (oj.OJ.addMagicBytes && !testFlag) {
                    bufferedOut.write(magicBytes);
                }

                zipOut = new ZipOutputStream(bufferedOut);
                zipOut.putNextEntry(new ZipEntry("objectj-data"));

                ByteArrayOutputStream byteArrOut = new ByteArrayOutputStream();

                // try {
                objOut = new ObjectOutputStream(byteArrOut);
//            } catch (IOException iOException) {
//                ok = false;
//                ij.IJ.showMessage("Error binary output (7678");//29.8.2010
//            }

                String mText = data.getLinkedMacroText();
                data.setLinkedMacroText(null);//temp remove macro, because we put it in a separate zip entry
                objOut.writeObject(data);
                data.setLinkedMacroText(mText);//--2.6.2010
                byteArrOut.writeTo(zipOut);

                ByteArrayOutputStream myMacroStream = new ByteArrayOutputStream();

                byte[] macroBytes = null;
                if (mText != null) {
                    zipOut.putNextEntry(new ZipEntry("objectj-macro"));
                    macroBytes = mText.getBytes();
                    myMacroStream.write(macroBytes);
                    myMacroStream.writeTo(zipOut);
                }


                if (testFlag) {
                    out = new DataOutputStream(new BufferedOutputStream(zipOut));
                    zipOut.putNextEntry(new ZipEntry(title));
                    TiffEncoder te = new TiffEncoder(fi);

                    te.write(out);

                    out.close();

                }

            } catch (IOException ex) {
                ok = false;
                throw new ProjectIOExceptionOJ("Can't save project (a):  " + ex.getMessage());

            } finally {
            }

            try {
                objOut.close();
            } catch (IOException ex) {
                ok = false;

                throw new ProjectIOExceptionOJ("Can't save project (c): " + ex.getMessage());
            }
            try {
                zipOut.close();
            } catch (IOException ex) {
                ok = false;

                throw new ProjectIOExceptionOJ("Can't save project (d): " + ex.getMessage());
            }
            try {
                fileOut.close();
            } catch (IOException ex) {
                ok = false;

                throw new ProjectIOExceptionOJ("Can't save project (e): " + ex.getMessage());
            }
            try {
                bufferedOut.close();
            } catch (IOException ex) {
                ok = false;
                throw new ProjectIOExceptionOJ("Can't save project (b): " + ex.getMessage());

            }
            if (ok) {
                oldOjjFile.delete();//1.10.2010 for Windows
                newOjjFile.renameTo(oldOjjFile);

            }

        //}
    }

    /**
     * Loads a zipped binary project file. binary type.
     */
    public DataOJ loadProject(String directory, String filename) throws ProjectIOExceptionOJ {
        DataOJ data = null;
        {
            String theType = UtilsOJ.getFileType(directory, filename);
            FileInputStream fis = null;
            try {
                File file = new File(directory, filename);
                double mBytes = file.length() / 1024 / 1024.0;
                fis = new FileInputStream(file);
                if (theType.contains("magic-ojj")) {
                    fis.skip(4);//skip 4 magic bytes
                }
                ZipInputStream zipInStream = null;
                ZipEntry ze = null;
                String macroText = null;
                try {
                    zipInStream = new ZipInputStream(fis);
                    ze = zipInStream.getNextEntry();
                    if (!ze.getName().equals("objectj-data")) {
                        throw new ProjectIOExceptionOJ("Project file cannot be loaded with this version of ObjectJ (687)");
                    }

                    data = loadZippedDataEntry(zipInStream);
                    if (data == null) {
                        IJ.showMessage("Could not load this project (size = " + IJ.d2s(mBytes, 2) + " MB");
                        return null;
                    }

                    ze = zipInStream.getNextEntry();
                    if (ze != null) {
                        if (!ze.getName().equals("objectj-macro")) {
                            throw new ProjectIOExceptionOJ("Project file cannot be loaded with this version of ObjectJ :" + oj.OJ.releaseVersion);
                        }

                        int size = 33 * 1024 * 1024;//33 MB
                        byte[] bytes = new byte[(int) size];
                        int loadedBytes = 0;
                        int chunk = 0;
                        while (((int) size - loadedBytes) > 0) {
                            chunk = zipInStream.read(bytes, loadedBytes, (int) size - loadedBytes);
                            if (chunk == -1) {
                                break;
                            }
                            loadedBytes += chunk;
                            if (loadedBytes >= size) {
                                IJ.showMessage("file is larger than " + (size / 1024 / 1024) + " MB");
                                return null;
                            }
                        }
                        byte[] bytesTrimmed = new byte[loadedBytes];
                        for (int jj = 0; jj < loadedBytes; jj++) {
                            bytesTrimmed[jj] = bytes[jj];
                        }
                        macroText = new String(bytesTrimmed);
                        macroText = macroText.replace("\r\n", "\r");
                        macroText = macroText.replace("\r", "\n");//21.6.2010 only use newLine as line terminator
                    }
                    if (data != null) {
                        data.initAfterUnmarshalling();
                        data.setChanged(false);
                        data.setName(UtilsOJ.stripExtension(filename));
                        data.setDirectory(directory);
                        data.setFilename(filename);
                        data.setLinkedMacroText(macroText);

                        data.getYtemDefs().setCellLayerVisible(true);//13.4.2010


                        //MacroInstaller mi = new MacroInstaller();
                        //mi.install(macroText);//23.4.2010


                    }
                } catch (IOException ex) {
                    throw new ProjectIOExceptionOJ("Cannot read project (f): " + ex.getMessage());
                } finally {
                    try {
                        zipInStream.close();
                    } catch (IOException ex) {
                        throw new ProjectIOExceptionOJ(ex.getMessage());
                    }
                }
            } catch (IOException ex) {
                throw new ProjectIOExceptionOJ("Can't save project (g) " + ex.getMessage());
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    throw new ProjectIOExceptionOJ(ex.getMessage());
                }
            }
        }
        return data;
    }

    /**
     * Loads the data part of zipped project; macro part is loaded elsewhere
     */
    private DataOJ loadZippedDataEntry(InputStream is) throws ProjectIOExceptionOJ {
        DataOJ data = null;

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            data = (DataOJ) ois.readObject();
            data.initAfterUnmarshalling();
            data.setChanged(false);
        } catch (ClassNotFoundException ex) {
            throw new ProjectIOExceptionOJ("Data part of project cannot be read (a). Exception= " + ex.getMessage());
        } catch (IOException ex) {
            throw new ProjectIOExceptionOJ("Data part of project cannot be read (b). Exception= " + ex.getMessage());
        } finally {
            return data;
        }
    }

    private String dataVersion(String directory, String filename) throws ProjectIOExceptionOJ {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(directory, filename));
            JarInputStream jis = null;
            try {
                jis = new JarInputStream(fis);
                jis.getNextJarEntry();
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(fis);
                    DataHeaderOJ dh = (DataHeaderOJ) ois.readObject();
                    return dh.version;
                } catch (Exception ex) {
                    throw new ProjectIOExceptionOJ("The project version cannot be read (c). Exception= " + ex.getMessage());
                } finally {
                    try {
                        ois.close();
                    } catch (IOException ex) {
                        throw new ProjectIOExceptionOJ(ex.getMessage());
                    }
                }
            } catch (IOException ex) {
                throw new ProjectIOExceptionOJ("The project version cannot be read (d). Exception= " + ex.getMessage());
            } finally {
                try {
                    jis.close();
                } catch (IOException ex) {
                    throw new ProjectIOExceptionOJ(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            throw new ProjectIOExceptionOJ("The project version cannot be read (e). Exception= " + ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                throw new ProjectIOExceptionOJ(ex.getMessage());
            }
        }
    }

    /**
     * @return true if version can be read without exception
     */
    public boolean isValidData(String directory, String filename) {
        try {
            dataVersion(directory, filename);
        } catch (Exception ex) {
            return false;
        }
        return true;


    }

    private class DataHeaderOJ implements Serializable {

        public String name;
        public String version;
        public Date updated;
        public String description = "";
    }
}
