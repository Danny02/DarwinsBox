/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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
package darwin.resourcehandling.dependencies;

import java.nio.file.Path;
import java.nio.file.Paths;

import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.ClasspathFileHandler.FileHandlerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceProvider implements Provider<ClasspathFileHandler> {

    @Inject
    private FileHandlerFactory factory;
    private final Path path;

    public ResourceProvider(String file) {
        path = Paths.get(file);
    }

    @Override
    public ClasspathFileHandler get() {
        return factory.create(path);
    }
}