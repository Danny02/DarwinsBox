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

import darwin.resourcehandling.watchservice.*;

import com.google.inject.Stage;

/**
 *
 * @author daniel
 */
//TODO enable creation of handles relative to others and Path variables like ($TEXTURES, $MODELS ...)
public class ClasspathFileHandler extends ListenerHandler {

    private final Stage stage;
    private static final Path DEV_FOLDER = Paths.get("src/main/resources");
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
        path = file;
        if (notifier != null) {
            notifier.register(path, fileListener);
            if (stage == Stage.DEVELOPMENT) {
                notifier.register(DEV_FOLDER.resolve(path), fileListener);
            }
        }
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
}
