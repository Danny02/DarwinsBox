/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling.test;

import java.io.File;

import de.dheinrich.darwin.resourcehandling.ResourceHandle;
import java.io.InputStream;

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
