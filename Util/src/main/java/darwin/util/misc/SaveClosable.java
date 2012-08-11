/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.util.misc;

import java.io.Closeable;

/**
 *
 * @author daniel
 */
public interface SaveClosable extends Closeable {

    @Override
    public void close();
}
