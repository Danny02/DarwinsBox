/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.io;

import java.io.IOException;
import java.io.InputStream;

import darwin.renderer.geometrie.unpacked.ModelObjekt;

/**
 * Abstrakte Klasse die von Parsern von unterschiedlichen Formaten von 3D
 * Modellen implementiert wird
 * <p/>
 * @author Daniel Heinrich
 */
public interface ModelReader
{

    public ModelObjekt readModel(InputStream source) throws IOException, WrongFileTypeException;

    /**
     * Checks if the Reader supports a given file extension
     * @param fileExtension
     * @return
     * true if the Reader things that he can read the named file extension
     */
    public boolean isSupported(String fileExtension);
}
