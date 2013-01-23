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
package darwin.resourcehandling.watchservice;

import java.io.IOException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.*;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.TimeUnit;

import darwin.util.logging.InjectLogger;

import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 *
 * @author daniel
 */
@Singleton
public class WatchServiceNotifier implements Runnable {

    @InjectLogger
    private static Logger logger = NOPLogger.NOP_LOGGER;
    //a little pollint timeout, to merge duplicated events of the same operation. 
    //i.e. when a file is written by another app, this is sometimes done in chuncks. 
    //And each written chunck causes a file modified event.
    private static final int EVENT_MERGE_TIMEOUT = 100;
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
            Map<FileChangeListener, Set<Kind<?>>> listener = new HashMap<>();
            for (;;) {
                 WatchKey watchKey = service.poll(EVENT_MERGE_TIMEOUT, TimeUnit.MILLISECONDS);

                if (watchKey == null) {
                    for (Entry<FileChangeListener, Set<Kind<?>>> entry : listener.entrySet()) {
                        for (Kind<?> kind : entry.getValue()) {
                            entry.getKey().fileChanged(kind);
                        }
                        listener.remove(entry.getKey());
                    }
                    watchKey = service.take();
                }
                
                for (WatchEvent<?> event :  watchKey.pollEvents()) {
                    Path c = (Path) event.context();
                    Path file = ((Path) watchKey.watchable()).resolve(c);
                    FileChangeListener l = callbacks.get(file.toAbsolutePath());
                    if (l != null) {
                        Set<Kind<?>> eventTyps = listener.get(l);
                        if (eventTyps == null) {
                            eventTyps = new HashSet<>();
                            listener.put(l, eventTyps);
                        }
                        eventTyps.add(event.kind());
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
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    }

    public void register(Path path, FileChangeListener callback) {
        if (service != null) {
            try {
                path = path.toAbsolutePath();
                Object fcl = callbacks.put(path, callback);
                //TODO fixen durch handle changing
                assert fcl == null : "An error will occure, cause the first "
                                     + "File Handle won't recieve any update events anymore";

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
