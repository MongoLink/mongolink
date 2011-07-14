package fr.bodysplash.mongolink.mapper;

import com.google.common.collect.Lists;
import fr.bodysplash.mongolink.MongoLinkError;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ContextBuilder {

    private static final Logger LOGGER = Logger.getLogger(ContextBuilder.class);

    private final String packageToScan;

    public ContextBuilder(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    public MapperContext createContext() {
        MapperContext result = new MapperContext();
        try {
            Iterable<Class> classes = getClasses();
            for (Class currentClass : classes) {
                if (isAMap(currentClass)) {
                    AbstractMap<?> mapping = (AbstractMap<?>) currentClass.newInstance();
                    mapping.buildMapper(result);
                }
            }
            LOGGER.debug("Done scanning package");
        } catch (Exception e) {
            throw new MongoLinkError("Error scanning package", e);

        }
        return result;
    }

    private boolean isAMap(Class currentClass) {
        return AbstractMap.class.isAssignableFrom(currentClass) && !SubclassMap.class.isAssignableFrom(currentClass);
    }

    private Iterable<Class> getClasses() throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageToScan.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class> classes = Lists.newArrayList();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageToScan));
        }

        return classes;
    }


    private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
