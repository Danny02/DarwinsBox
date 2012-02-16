/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.ressourcen.io.obj;

import java.io.*;
import org.apache.log4j.Logger;

import darwin.resourcehandling.resmanagment.RessourcesLoader;

/**
 *
 * @author Daniel Heinrich
 */
public class OBJFormatReader
{
    private static class Log
    {
        private static Logger ger = Logger.getLogger(OBJFormatReader.class);
    }
    private final String file;

    public OBJFormatReader(String file) {
        this.file = file;
    }

    public ObjFile loadOBJ() {
        ObjFile obj = null;

        InputStream is = RessourcesLoader.getRessource(file + ".bin");
        if (is != null)
            try {
                ObjectInputStream ois = new ObjectInputStream(is);
                obj = (ObjFile) ois.readObject();
            } catch (ClassNotFoundException ex) {
                Log.ger.fatal("Model \"" + file
                        + "\" ist beschädigt.", ex);
            } catch (IOException ex) {
                Log.ger.fatal("Beim laden des Models \"" + file
                        + "\" ist ein Fehler aufgetreten.", ex);
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
            }
        else {
            ObjFileParser ofp = new ObjFileParser(file);
            obj = ofp.loadOBJ();
        }

        return obj;
    }
}
