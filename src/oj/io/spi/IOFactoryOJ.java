/* IOFactoryOJ.java
 * -- documented
 *
 * registers two types of data formatting:
 * xml and binary (currently, only xml is used)
 */
package oj.io.spi;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IOFactoryOJ {

    private Hashtable registry = new Hashtable();
    private static IOFactoryOJ instance;

    public IOFactoryOJ() {
        instance = this;
        initRegistry();
    }

    public static IOFactoryOJ getFactory() {
        if (instance == null) {
            new IOFactoryOJ();
        }
        return instance;
    }

    /**
     * @return an IOProvider for xml or binary
     */
    public IIOProviderOJ getProvider(String type) {
        if (registry.containsKey(type)) {
            try {
                String className = (String) registry.get(type);
                return (IIOProviderOJ) getClass().getClassLoader().loadClass(className).newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(IOFactoryOJ.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getMessage());
                return null;
            } catch (IllegalAccessException ex) {
                Logger.getLogger(IOFactoryOJ.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getMessage());
                return null;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(IOFactoryOJ.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * we register two possible formats
     */
    private void initRegistry() {
        registry.clear();
        registry.put("xmlstream", "oj.io.spi.XmlStreamProviderOJ");
        registry.put("javaobject", "oj.io.spi.JavaObjectProviderOJ");
        //registry.put("objectimage", "oj.io.spi.ObjectImageProviderOJ");//19.3.2010
    }

    /**
     * not used
     */
    private static Class[] getClasses(String pckgname) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = '/' + pckgname.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }
}
