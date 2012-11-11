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

import darwin.resourcehandling.watchservice.FileChangeListener;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 *
 * @author daniel
 */
//TODO enable creation of handles relative to others and Path variables like ($TEXTURES, $MODELS ...)
public class ClasspathFileHandler extends ListenerHandler {

    private final Path path;

    //TODO zu jeder datei soll immer nur eine handle existieren dürfen, muss gechached werden
    //TODO jede resourcen sollten auch gecached werden
    public static interface FileHandlerFactory {

        public ClasspathFileHandler create(Path file);
    }

    public ClasspathFileHandler(Path path) {
        this(null, path);
    }

    @AssistedInject
    public ClasspathFileHandler(WatchServiceNotifier notifier,
                                @Assisted Path file) {
        path = file;
        if (notifier != null) {
            notifier.register(path, new FileChangeListener() {
                @Override
                public void fileChanged(Kind kind) {
                    fireChangeEvent();
                }
            });
        }
    }

    public Path getPath() {
        return path;
    }

    @Override
    public InputStream getStream() throws IOException {

        if (Files.isReadable(path)) {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } else {
            if(path.isAbsolute())
            {                
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
