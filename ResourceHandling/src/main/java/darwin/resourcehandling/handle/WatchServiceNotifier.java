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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import darwin.util.logging.InjectLogger;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 *
 * @author daniel
 */
public class WatchServiceNotifier implements Runnable {

    @InjectLogger
    private final static Logger logger = NOPLogger.NOP_LOGGER;
    private final Map<Path, FileChangeListener> callbacks = new HashMap<>();
    private final WatchService service;

    public WatchServiceNotifier() {
        WatchService a;
        try {
            a = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            logger.warn("Could not initialize watch service: " + ex.getLocalizedMessage());
            a = null;
        }
        service = a;
    }

    @Override
    public void run() {
        if (service == null) {
            return;
        }
        try {
            for (;;) {
                WatchKey watchKey = service.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();

                for (WatchEvent event : events) {
                    Path c = (Path) event.context();
                    Path file = ((Path) watchKey.watchable()).resolve(c);
                    FileChangeListener l = callbacks.get(file.toAbsolutePath());
                    if (l != null) {
                        l.fileChanged(event.kind());
                    }
                }

                if (!watchKey.reset()) {
                    watchKey.cancel();
                }
            }
        } catch (InterruptedException ex) {
            logger.error("Watchservice was suspended: " + ex.getLocalizedMessage());
        }
    }

    public Thread createNotifierThread() {
        Thread t = new Thread(this, "Filesystem watch service notifier thread");
        t.setDaemon(true);
        return t;
    }

    public void register(Path path, FileChangeListener callback) {
        if (service != null) {
            try {
                callbacks.put(path.toAbsolutePath(), callback);

                Path dir = path;
                if (!Files.isDirectory(dir)) {
                    dir = dir.getParent();
                }
                dir.register(service,
                             ENTRY_CREATE,
                             ENTRY_DELETE,
                             ENTRY_MODIFY);
            } catch (IOException ex) {
                logger.warn("Could not register path to watchlist(" + path
                            + ")\n Exception: " + ex.getLocalizedMessage());
            }
        }
    }
}
