/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
