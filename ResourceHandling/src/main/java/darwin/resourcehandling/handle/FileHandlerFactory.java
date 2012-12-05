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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.Stage;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author some
 */
@Singleton
public class FileHandlerFactory {

    private final WatchServiceNotifier notifier;
    private final Stage stage;
    private Map<Path, ClasspathFileHandler> handle = new HashMap<>();

    @Inject
    public FileHandlerFactory(WatchServiceNotifier notifier, Stage stage) {
        this.notifier = notifier;
        this.stage = stage;
    }

    public ClasspathFileHandler create(String file) {
        return create(Paths.get(file));
    }

    public ClasspathFileHandler create(Path file) {
        ClasspathFileHandler fh = handle.get(file);
        if (fh == null) {
            fh = new ClasspathFileHandler(notifier, stage, file);
            handle.put(file, fh);
        }
        return fh;
    }
}
