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
package darwin.resourcehandling.relative;

import java.io.*;
import java.nio.file.*;

import darwin.resourcehandling.handle.ClasspathFileHandler;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class RelativeHandleFactory implements RelativeFileFactory {

    private final ClasspathFileHandler handle;

    public RelativeHandleFactory(ClasspathFileHandler handle) {
        this.handle = handle;
    }

    @Override
    public InputStream readRelative(String name) throws IOException {
        return handle.resolve(name).getStream();
    }

    @Override
    public OutputStream writeRelative(String name) throws IOException {
        return Files.newOutputStream(handle.resolve(name).getPath(), StandardOpenOption.WRITE);
    }
}
