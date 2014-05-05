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

import com.google.common.base.*;
import com.google.common.collect.*;
import org.slf4j.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ClasspathHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathHelper.class);

    public static URLClassLoader getClassLoader() {
        return (URLClassLoader) URLClassLoader.getSystemClassLoader();
    }

    public static List<URI> getClasspath() {
        Function<URL, URI> func = new Function<URL, URI>() {
            @Override
            public URI apply(URL f) {
                try {
                    return f.toURI();
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        return Lists.transform(Arrays.asList(getClassLoader().getURLs()), func);
    }

    /**
     * Searches for files in classpath folders and files(i.e. zip, jar)
     *
     * @param glob define a search string for the file(syntax
     * {@link FileSystem#getPathMatcher definition})
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static List<URI> elementsOfFolder(String glob) {
        List<URI> elements = new ArrayList<>();

        for (URI url : getClasspath()) {
            Path path = Paths.get(url);

            //we have to discrimate between normal folders and i.e. zip/jar files
            boolean isDir = Files.isDirectory(path);
            try (FileSystem fs = isDir
                                 ? path.getFileSystem()
                                 : FileSystems.newFileSystem(path, null)) {

                PathMatcher matcher = fs.getPathMatcher("glob:" + glob);
                Path root = isDir ? path : fs.getPath("/");

                FileCollector collector = new FileCollector(matcher, elements);
                Files.walkFileTree(root, collector);
            } catch (UnsupportedOperationException ex) {
            } catch (Throwable t) {
                logger.info(String.format("Classpath element %s can not be searched!\n%s", url, t));
            }
        }

        return elements;
    }

    public static FluentIterable<URI> getClasspathFolders() {
        return FluentIterable.from(ClasspathHelper.getClasspath()).filter(new Predicate<URI>() {
            @Override
            public boolean apply(URI t) {
                return "file".equals(t.getScheme());
            }
        });
    }

    private static class FileCollector extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private final List<URI> collected;

        public FileCollector(PathMatcher matcher, List<URI> collected) {
            this.matcher = matcher;
            this.collected = collected;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
            if (matcher.matches(path)) {
                collected.add(path.toUri());
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
