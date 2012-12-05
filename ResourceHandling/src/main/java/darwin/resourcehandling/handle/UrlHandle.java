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
import java.util.Objects;

import darwin.resourcehandling.ResourceChangeListener;

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
    public String getName() {
        return url.toString();
    }

    @Override
    public InputStream getStream() throws IOException {
        return url.openStream();
    }

    @Override
    public void registerChangeListener(ResourceChangeListener listener) {
        //TODO if necessary, with http one could ask the server for the change date
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UrlHandle other = (UrlHandle) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }
}
