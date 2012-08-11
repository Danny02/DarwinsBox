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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent.Kind;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 *
 * @author daniel
 */
public class ClasspathFileHandler extends ListenerHandler {

    private final Path path;

    public static interface FileHandlerFactory {

        public ClasspathFileHandler create(String file);
    }

    @AssistedInject
    public ClasspathFileHandler(WatchServiceNotifier notifier,
                                @Assisted String file) {
        path = Paths.get(file);
        notifier.register(path, new Callback());
    }

    @Override
    public InputStream getStream() throws IOException {

        if (Files.isReadable(path)) {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } else {
            String f = path.toString();
            if (!f.startsWith("/")) {
                f = "/" + f;
            }
            InputStream in = ClasspathFileHandler.class.getResourceAsStream(f);
            if (in == null) {
                throw new IOException("File could not be found! " + path);
            } else {
                return in;
            }
        }
    }

    private class Callback implements FileChangeListener {

        @Override
        public void fileChanged(Kind kind) {
            fireChangeEvent();
        }
    }
}
