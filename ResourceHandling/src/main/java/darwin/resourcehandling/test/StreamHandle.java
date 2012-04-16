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
package darwin.resourcehandling.test;

import java.io.*;

import darwin.resourcehandling.ResourceHandle;

/**
 *
 * @author daniel
 */
public class StreamHandle implements ResourceHandle {

    private final InputStream stream;

    public StreamHandle(InputStream stream) {
        this.stream = stream;
    }

    public StreamHandle(String path) {
        stream = ClassLoader.getSystemResourceAsStream('/' + path);
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public ResourceHandle getHandle() {
        return this;
    }
}
