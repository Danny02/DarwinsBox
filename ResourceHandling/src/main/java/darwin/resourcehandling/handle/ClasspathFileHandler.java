/*
 * Copyright (C) 2012 daniel
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
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.Objects;

import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.factory.ResourceHandleFactory;
import darwin.resourcehandling.watchservice.*;
import java.net.URL;

/**
 *
 * @author daniel
 */
public class ClasspathFileHandler extends ListenerHandler {

//    public static final Path DEV_FOLDER = Paths.get("src/main/resources");
//    private final boolean useDevFolder;
    private final WatchServiceNotifier notifier;
    private boolean registered = false;
    private final URL file;

    public static class Factory implements ResourceHandleFactory {

        @Override
        public ResourceHandle createHandle(WatchServiceNotifier notifier,
                                           URL path) {
            return new ClasspathFileHandler(notifier, path);
        }
    }

    public ClasspathFileHandler(URL path) {
        this(null, path);
    }

    public ClasspathFileHandler(WatchServiceNotifier notifier, URL path) {
        this.notifier = notifier;
        this.file = path;
    }

    @Override
    public void registerChangeListener(ResourceChangeListener listener) {
        super.registerChangeListener(listener);

        if (!registered && notifier != null) {
            FileChangeListener fileChangeListener = new FileChangeListener() {
                @Override
                public void fileChanged(Kind kind) {
                    fireChangeEvent();
                }
            };

            notifier.register(file, fileChangeListener);
            if (useDevFolder) {
                notifier.register(DEV_FOLDER.resolve(file), fileChangeListener);
            }
            registered = true;
        }
    }

    @Override
    public String getName() {
        return file.toString();
    }

    @Override
    public ClasspathFileHandler resolve(String subPath) {
        Paths.get(file.getPath()
        
        Path parent = file.getParent();
        if (parent == null) {
            parent = Paths.get(".");
        }
        return new ClasspathFileHandler(notifier, parent.resolve(subPath));
    }

    @Override
    public InputStream getStream() throws IOException {
        Path devPath = DEV_FOLDER.resolve(file);
        if (Files.isReadable(file)) {
            return Files.newInputStream(file, StandardOpenOption.READ);
        } else if (useDevFolder && Files.isReadable(devPath)) {
            return Files.newInputStream(devPath, StandardOpenOption.READ);
        } else {
            if (file.isAbsolute()) {
                throw new IOException("Could not find absolute file: " + file);
            }

            String f = file.toString();
            if (!f.startsWith("/")) {
                f = "/" + f;
            }
            
            InputStream in = ClasspathFileHandler.class.getResourceAsStream(f);
            if (in == null) {
                throw new IOException("Could not find file in classpath: " + file);
            } else {
                return in;
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.file);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClasspathFileHandler other = (ClasspathFileHandler) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }
}
