/*
 * Copyright (C) 2012 some
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
 */
package darwin.resourcehandling.handle;

import java.nio.file.*;
import java.util.*;

import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import javax.inject.*;

/**
 *
 * @author some
 */
@Singleton
public class FileHandleCache {

    private final WatchServiceNotifier notifier;
    private final boolean devMode;
    private Map<Path, ClasspathFileHandler> handle = new HashMap<>();

    public static class Builder {

        private boolean withNotify, dev;

        public Builder withChangeNotification() {
            return withChangeNotification(true);
        }

        public Builder withChangeNotification(boolean w) {
            withNotify = w;
            return this;
        }

        public Builder withDevFolder() {
            return withDevFolder(true);
        }

        public Builder withDevFolder(boolean w) {
            dev = w;
            return this;
        }

        public FileHandleCache create() {
            WatchServiceNotifier n = null;
            if (withNotify) {
                n = new WatchServiceNotifier();
                n.createNotifierThread().start();
            }
            return new FileHandleCache(n, dev);
        }
    }

    public static Builder build() {
        return new Builder();
    }

    public FileHandleCache() {
        this(null, false);
    }

    @Inject
    public FileHandleCache(WatchServiceNotifier notifier, boolean devMode) {
        this.notifier = notifier;
        this.devMode = devMode;
    }

    public ClasspathFileHandler get(String file) {
        return get(Paths.get(file));
    }

    public ClasspathFileHandler get(Path file) {
        ClasspathFileHandler fh = handle.get(file);
        if (fh == null) {
            fh = new ClasspathFileHandler(devMode, notifier, file);
            handle.put(file, fh);
        }
        return fh;
    }
}
