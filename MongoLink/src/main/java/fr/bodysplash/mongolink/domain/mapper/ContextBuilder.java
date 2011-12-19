package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import fr.bodysplash.mongolink.MongoLinkError;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContextBuilder {

    public ContextBuilder(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    public MapperContext createContext() {
        MapperContext result = new MapperContext();
        try {
            Iterable<Class> classes = getCandidateClasses();
            for (Class currentClass : classes) {
                if (isAMap(currentClass)) {
                    ClassMap<?> mapping = (ClassMap<?>) currentClass.newInstance();
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
        return ClassMap.class.isAssignableFrom(currentClass) && !SubclassMap.class.isAssignableFrom(currentClass);
    }

    private Iterable<Class> getCandidateClasses() throws ClassNotFoundException, IOException {
        List<Class> classes = Lists.newArrayList();
        for (URL url : getResources()) {
            if(isAJar(url)) {
                classes.addAll(findClassesFromJar(url));
            } else {
                classes.addAll(findClassesFromDirectory(new File(url.getFile()), packageToScan));
            }
        }
        return classes;
    }

    private boolean isAJar(URL url) {
        return "jar".equals(url.getProtocol());
    }

    private List<URL> getResources() throws IOException {
        Enumeration<URL> resources = classLoader().getResources(packageToDirectory());
        List<URL> dirs = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(resource);
        }
        return dirs;
    }

    private String packageToDirectory() {
        return packageToScan.replace(".", File.separator);
    }

    private ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private List<Class> findClassesFromDirectory(File directory, String packageToScan) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClassesFromDirectory(file, packageToScan + "." + file.getName()));
            } else {
                if (file.getName().endsWith(CLASS_EXTENSION)) {
                    final String classPath = packageToScan + '.' + file.getName().substring(0, file.getName().length() - CLASS_EXTENSION.length());
                    classes.add(Class.forName(classPath));
                }
            }
        }
        return classes;
    }

    private List<Class> findClassesFromJar(URL url) throws ClassNotFoundException {
        try {
            return doFindClassFromJar(url);
        } catch (IOException e) {
            LOGGER.error("Can't load mapping from Jar", e);
        }
        return Lists.newArrayList();
    }

    private List<Class> doFindClassFromJar(URL url) throws IOException, ClassNotFoundException {
        final List<Class> result = Lists.newArrayList();
        JarURLConnection jarCon = (JarURLConnection) url.openConnection();
        final JarFile jarFile = jarCon.getJarFile();
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            result.addAll(extractClass(entries));
        }
        return result;
    }

    private List<Class<?>> extractClass(Enumeration<JarEntry> entries) throws ClassNotFoundException {
        JarEntry entry = entries.nextElement();
        String entryPath = entry.getName();
        if(entryPath.startsWith(packageToDirectory()) && entryPath.endsWith(CLASS_EXTENSION)) {
            final String classPath = entryPath.substring(0, entryPath.length() - CLASS_EXTENSION.length()).replace(File.separator, ".");
            return Lists.<Class<?>>newArrayList(Class.forName(classPath));
        }
        return Lists.newArrayList();
    }

    public static final String CLASS_EXTENSION = ".class";

    private static final Logger LOGGER = Logger.getLogger(ContextBuilder.class);

    private final String packageToScan;
}
