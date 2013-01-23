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

/**
 *
 * @author daniel
 */
//TODO enable creation of handles relative to others and Path variables like ($TEXTURES, $MODELS ...)
public class ClasspathFileHandler extends ListenerHandler {

    public static final Path DEV_FOLDER = Paths.get("src/main/resources");
    private final boolean useDevFolder;
     private final WatchServiceNotifier notifier;
    private boolean registered = false;
    private final Path path;

    public static class Factory implements ResourceHandleFactory {

        @Override
        public ResourceHandle createHandle(boolean useDevFolder,
                                            WatchServiceNotifier notifier,
                                           Path path) {
            return new ClasspathFileHandler(useDevFolder, notifier, path);
        }
    }

    public ClasspathFileHandler(Path path) {
        this(false, null, path);
    }

    public ClasspathFileHandler(boolean useDevFolder,  WatchServiceNotifier notifier, Path path) {
        this.useDevFolder = useDevFolder;
        this.notifier = notifier;
        this.path = path;
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

            notifier.register(path, fileChangeListener);
            if (useDevFolder) {
                notifier.register(DEV_FOLDER.resolve(path), fileChangeListener);
            }
            registered = true;
        }
    }

    @Override
    public String getName() {
        return path.toString();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public ClasspathFileHandler resolve(String subPath) {
         Path parent = path.getParent();
        if (parent == null) {
            parent = Paths.get(".");
        }
        return new ClasspathFileHandler(useDevFolder, notifier, parent.resolve(subPath));
    }

    @Override
    public InputStream getStream() throws IOException {
        Path devPath = DEV_FOLDER.resolve(path);
        if (Files.isReadable(path)) {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } else if (useDevFolder && Files.isReadable(devPath)) {
            return Files.newInputStream(devPath, StandardOpenOption.READ);
        } else {
            if (path.isAbsolute()) {
                throw new IOException("Could not find absolute file. " + path);
            }

            String f = path.toString();
            if (!f.startsWith("/")) {
                f = "/" + f;
            }
            InputStream in = ClasspathFileHandler.class.getResourceAsStream(f);
            if (in == null) {
                throw new IOException("Could not find file in classpath." + path);
            } else {
                return in;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals( Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClasspathFileHandler other = (ClasspathFileHandler) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }
}
