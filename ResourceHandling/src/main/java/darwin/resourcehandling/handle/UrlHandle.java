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
import java.io.InputStream;
import java.net.URL;

import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.core.ResourceHandle;

/**
 *
 * @author daniel
 */
public class UrlHandle implements ResourceHandle {

    private final URL url;

    public UrlHandle(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getStream() throws IOException {
        return url.openStream();
    }

    @Override
    public void registerChangeListener(ResourceChangeListener listener) {
        //TODO if necessary, with http one could ask the server for the change date
    }
}