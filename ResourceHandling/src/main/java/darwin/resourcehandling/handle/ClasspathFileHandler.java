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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.Objects;

import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.watchservice.FileChangeListener;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.Stage;

/**
 *
 * @author daniel
 */
//TODO enable creation of handles relative to others and Path variables like ($TEXTURES, $MODELS ...)
public class ClasspathFileHandler extends ListenerHandler {

    public static final Path DEV_FOLDER = Paths.get("src/main/resources");
    private final Stage stage;
    private WatchServiceNotifier notifier;
    private final Path path;
    private final FileChangeListener fileListener = new FileChangeListener() {
        @Override
        public void fileChanged(Kind kind) {
            fireChangeEvent();
        }
    };

    public ClasspathFileHandler(Path path) {
        this(null, Stage.PRODUCTION, path);
    }

    public ClasspathFileHandler(WatchServiceNotifier notifier, Stage stage,
                                Path file) {
        this.stage = stage;
        this.notifier = notifier;
        path = file;
    }

    @Override
    public void registerChangeListener(ResourceChangeListener listener) {
        super.registerChangeListener(listener);

        if (notifier != null) {
            Path p = stage == Stage.DEVELOPMENT ? DEV_FOLDER.resolve(path) : path;
            notifier.register(p, fileListener);
            notifier = null;
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
    public InputStream getStream() throws IOException {
        Path devPath = DEV_FOLDER.resolve(path);
        if (Files.isReadable(path)) {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } else if (stage == Stage.DEVELOPMENT && Files.isReadable(devPath)) {
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
    public boolean equals(Object obj) {
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
