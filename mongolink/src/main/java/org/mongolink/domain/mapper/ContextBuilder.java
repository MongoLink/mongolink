/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package org.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import org.mongolink.MongoLinkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ContextBuilder(String... packagesToScan) {
        this.packagesToScan.addAll(Lists.newArrayList(packagesToScan));
    }

    public MapperContext createContext() {
        LOGGER.debug("Scanning : {}", packagesToScan);
        MapperContext result = new MapperContext();
        try {
            Iterable<Class> classes = getCandidateClasses();
            for (Class currentClass : classes) {
                if (isAMap(currentClass)) {
                    ClassMap<?> mapping = (ClassMap<?>) currentClass.newInstance();
                    LOGGER.debug("Mapping : {}", currentClass);
                    mapping.buildMapper(result);
                }
            }

            LOGGER.debug("Done scanning : {}", packagesToScan);
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
        for (String packageToScan : packagesToScan) {
            for (URL url : getResourcesFrom(packageToScan)) {
                classes.addAll(getCandidateClasses(packageToScan, url));
            }
        }
        return classes;
    }

    private List<Class> getCandidateClasses(String packageToScan, URL url) throws ClassNotFoundException {
        if (isAJar(url)) {
            return findClassesFromJar(url, packageToScan);
        }
        return findClassesFromDirectory(new File(url.getFile()), packageToScan);
    }

    private boolean isAJar(URL url) {
        return "jar".equals(url.getProtocol());
    }

    private List<URL> getResourcesFrom(String packageToScan) throws IOException {
        Enumeration<URL> resources = classLoader().getResources(packageToDirectory(packageToScan));
        List<URL> dirs = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(resource);
        }
        return dirs;
    }

    private String packageToDirectory(String packageToScan) {
        return packageToScan.replace(".", "/");
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
            } else if (file.getName().endsWith(CLASS_EXTENSION)) {
                classes.add(loadClass(classPath(packageToScan, file)));
            }
        }
        return classes;
    }

    protected Class loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    private String classPath(String packageToScan, File file) {
        return packageToScan + '.' + file.getName().substring(0, file.getName().length() - CLASS_EXTENSION.length());
    }

    private List<Class> findClassesFromJar(URL url, String packageToScan) throws ClassNotFoundException {
        try {
            return doFindClassFromJar(url, packageToScan);
        } catch (IOException e) {
            LOGGER.error("Can't load mapping from Jar", e);
        }
        return Lists.newArrayList();
    }

    private List<Class> doFindClassFromJar(URL url, String packageToScan) throws IOException, ClassNotFoundException {
        final List<Class> result = Lists.newArrayList();
        JarURLConnection jarCon = (JarURLConnection) url.openConnection();
        final JarFile jarFile = jarCon.getJarFile();
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            result.addAll(extractClass(entries, packageToScan));
        }
        return result;
    }

    private List<Class<?>> extractClass(Enumeration<JarEntry> entries, String packageToScan) throws ClassNotFoundException {
        JarEntry entry = entries.nextElement();
        String entryPath = entry.getName();
        if (entryPath.startsWith(packageToDirectory(packageToScan)) && entryPath.endsWith(CLASS_EXTENSION)) {
            return Lists.<Class<?>>newArrayList(loadClass(classPath(entryPath)));
        }
        return Lists.newArrayList();
    }

    private String classPath(String entryPath) {
        return entryPath.substring(0, entryPath.length() - CLASS_EXTENSION.length()).replace(File.separator, ".");
    }

    public static final String CLASS_EXTENSION = ".class";

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextBuilder.class);

    private final List<String> packagesToScan = Lists.newArrayList();
}
