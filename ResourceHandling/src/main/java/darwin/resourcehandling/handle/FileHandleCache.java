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

import java.net.*;
import java.util.*;

import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import javax.inject.*;

/**
 *
 * @author some
 */
@Singleton
public class FileHandleCache {

    private final WatchServiceNotifier notifier;
    private Map<URI, ResourceHandle> handles = new HashMap<>();

    public static class Builder {

        private boolean withNotify = false;//, dev = false;

        public Builder withChangeNotification() {
            return withChangeNotification(true);
        }

        public Builder withChangeNotification(boolean w) {
            withNotify = w;
            return this;
        }

        public FileHandleCache create() {
            WatchServiceNotifier n = null;
            if (withNotify) {
                n = new WatchServiceNotifier();
                n.createNotifierThread().start();
            }
            return new FileHandleCache(n);
        }
    }

    public static Builder build() {
        return new Builder();
    }

    @Inject
    public FileHandleCache(WatchServiceNotifier notifier) {
        this.notifier = notifier;
    }

    public ResourceHandle get(final URI file) {
        URI tmp = null;
        if (file.isAbsolute()) {
            FluentIterable<URI> folders = ClasspathHelper.getClasspathFolders();

            Optional<URI> match = folders.firstMatch(new Predicate<URI>() {
                @Override
                public boolean apply(URI input) {
                    return !input.relativize(file).equals(file);
                }
            });

            if (match.isPresent()) {
                tmp = match.get().relativize(file);
            }
        }
        if (tmp == null) {
            tmp = file;
        }

        ResourceHandle h = handles.get(tmp);
        if (h == null) {
            h = new ClasspathFileHandler(notifier, tmp);
            handles.put(tmp, h);
        }

        return h;
    }
}
