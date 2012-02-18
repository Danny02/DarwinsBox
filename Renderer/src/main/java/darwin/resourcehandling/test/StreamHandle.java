/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.resourcehandling.test;

import darwin.resourcehandling.*;
import java.io.*;

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
