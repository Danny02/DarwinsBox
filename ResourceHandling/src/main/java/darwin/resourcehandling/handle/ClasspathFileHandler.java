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

import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.watchservice.FileChangeListener;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.util.Objects;

/**
 * @author daniel
 */
public class ClasspathFileHandler extends ListenerHandler {

    private final WatchServiceNotifier notifier;
    private boolean registered = false;
    private final URI file;

    public ClasspathFileHandler(WatchServiceNotifier notifier, final URI file) {
        this.notifier = notifier;

        this.file = file;
    }

    @Override
    public void registerChangeListener(ResourceChangeListener listener) {
        super.registerChangeListener(listener);
        iniNotifier();
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public ClasspathFileHandler resolve(String subPath) {
        return new ClasspathFileHandler(notifier, file.resolve(subPath));
    }

    public URI getFile() {
        return file;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (file.isAbsolute()) {
            return file.toURL().openStream();
        } else {
            InputStream in = ClassLoader.getSystemResourceAsStream(file.getPath());
            if (in == null) {
                throw new IOException("Could not find file in classpath: " + file);
            }
            return in;
        }
    }

    private void iniNotifier() {
        if (!registered && notifier != null && !file.isAbsolute()) {
            ClasspathHelper.getClasspathFolders()
                    .map(f -> Paths.get(f.resolve(file)))
                    .distinct()
                    .forEach(p -> notifier.register(p, k -> fireChangeEvent()));

            registered = true;
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
