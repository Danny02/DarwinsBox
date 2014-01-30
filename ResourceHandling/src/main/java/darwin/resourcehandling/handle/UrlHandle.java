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

import java.io.*;
import java.net.URL;
import java.util.Objects;

import darwin.resourcehandling.ResourceChangeListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;

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
    public ResourceHandle resolve(String subPath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals( Object obj) {
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
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException{
        URI u = new URI("jar:file:/J:/projecte/DarwinsBox/ResourceHandling/target/ResourceHandling-2.2-SNAPSHOT.jar!/META-INF/MANIFEST.mf");
        System.out.println(Paths.get(u));        
        System.out.println(u.getPath());        
        System.out.println(u.getRawPath());        
    }
}
