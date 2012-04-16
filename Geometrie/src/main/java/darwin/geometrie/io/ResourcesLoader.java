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
package darwin.geometrie.io;

import java.io.IOException;
import java.io.InputStream;
import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author daniel
 */
/**
 *
 * @author dheinrich
 */
public class ResourcesLoader
{

    private static class Log
    {

        private static Logger ger = Logger.getLogger(ResourcesLoader.class);
    }

    public static InputStream getRessource(String path) throws IOException
    {
        InputStream is = getStream(path + ".lzma");
        if (is != null) {
            return new LzmaInputStream(is, new Decoder());
        } else {
            is = getStream(path);
            if (is == null) {
                throw new IOException("Resource couldn't be found: " + path);
            }
        }

        return is;
    }

    private static InputStream getStream(String path)
    {
        return ResourcesLoader.class.getResourceAsStream('/' + path);
    }
}
