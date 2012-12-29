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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class FileFactory implements RelativeFileFactory {

    private final Path root;

    public FileFactory(File root) {
        this.root = root.toPath();
    }

    public FileFactory(Path root) {
        this.root = root;
    }

    @Override
    public InputStream readRelative(String name) throws IOException {
        return Files.newInputStream(getRelative(name));
    }

    @Override
    public OutputStream writeRelative(String name) throws IOException {
        Path relative = getRelative(name);
        Path dir = relative.getParent();
        Files.createDirectories(dir);
                
        return Files.newOutputStream(relative);
    }

    private Path getRelative(String name) {
        if (Files.isDirectory(root)) {
            return root.resolve(name);
        } else {
            return root.getParent().resolve(name);
        }
    }
}
