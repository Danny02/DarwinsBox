/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.geometrie.io;

import java.io.IOException;
import java.io.OutputStream;

import darwin.geometrie.unpacked.Model;

/**
 *
 * @author daniel
 */
public interface ModelWriter
{
    public void writeModel(OutputStream out, Model[] model) throws IOException;

    public String getDefaultFileExtension();
}
