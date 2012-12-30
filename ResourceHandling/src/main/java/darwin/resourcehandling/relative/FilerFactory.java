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
import java.util.*;


import javax.annotation.processing.Filer;
import javax.tools.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class FilerFactory implements RelativeFileFactory {

    private final Filer filer;
    private final Map<String, FileObject> readFiles = new HashMap<>();

    public FilerFactory(Filer filer) {
        this.filer = filer;
    }

    @Override
    public InputStream readRelative(String name) throws IOException {
        return getReadFile(name).openInputStream();
    }

    @Override
    public OutputStream writeRelative(String name) throws IOException {
        return filer.createResource(StandardLocation.CLASS_OUTPUT, "", name).openOutputStream();
    }

    public void delete(String name) throws IOException {
        FileObject f = getReadFile(name);
        if (!f.delete()) {
            Path get = Paths.get(f.toUri());
            Files.deleteIfExists(get);
        }
    }

    private FileObject getReadFile(String name) throws IOException {
        FileObject file = readFiles.get(name);
        if (file == null) {
            try {
                file = filer.getResource(StandardLocation.SOURCE_PATH, "", name);
            } catch (IOException ex) {
                file = filer.getResource(StandardLocation.CLASS_OUTPUT, "", name);
            }
            readFiles.put(name, file);
        }
        return file;
    }
}
