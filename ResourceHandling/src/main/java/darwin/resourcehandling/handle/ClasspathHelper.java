/*
 * Copyright (C) 2014 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.handle;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.jar.*;

import com.google.common.base.*;
import com.google.common.collect.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ClasspathHelper {

    public static final Predicate<URL> JarFile = new EndsWith("jar");
    public static final Predicate<URL> Folder = new EndsWith("/");

    public static URLClassLoader getClassLoader() {
        return (URLClassLoader) URLClassLoader.getSystemClassLoader();
    }

    public static List<URL> getClasspath() {
        return Arrays.asList(getClassLoader().getURLs());
    }

    public static List<URL> elementsOfFolder(String folder) throws IOException, URISyntaxException {
        List<URL> elements = new ArrayList<>();

        for (URL url : Iterables.filter(getClasspath(), JarFile)) {
            JarFile jar = new JarFile(url.getPath());
            for (JarEntry entry : Collections.list(jar.entries())) {
                String name = entry.getName();
                if (name.startsWith(folder)) {
                    elements.add(new URL("jar:" + url + "!/" + name));
                }
            }
        }
        for (URL url : Iterables.filter(getClasspath(), Folder)) {
            Path root = Paths.get(url.toURI());
            FileCollector collector = new FileCollector(root.resolve(folder));
            Files.walkFileTree(root, collector);
            elements.addAll(collector.collected);
        }

        return elements;
    }

    public static Collection<URL> elementsOfFolder(String folder, String postfix) throws IOException, URISyntaxException {
        return elementsOfFolder(folder, new EndsWith(postfix));
    }

    public static Collection<URL> elementsOfFolder(String folder, Predicate<URL> pred) throws IOException, URISyntaxException {
        return Collections2.filter(elementsOfFolder(folder), pred);
    }

    private static class EndsWith implements Predicate<URL> {

        private final String postfix;

        public EndsWith(String postfix) {
            this.postfix = postfix;
        }

        @Override
        public boolean apply(URL url) {
            return url.getPath().endsWith(postfix);
        }
    }

    private static class FileCollector extends SimpleFileVisitor<Path> {

        private final List<URL> collected = new ArrayList<>();
        private final Path root;

        private FileCollector(Path resolve) {
            root = resolve;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
            if (path.startsWith(root)) {
                System.out.println(path);
                collected.add(path.toUri().toURL());
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
