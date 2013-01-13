/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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
import java.util.ServiceLoader;

import darwin.geometrie.unpacked.Model;
import darwin.resourcehandling.factory.ResourceFromHandle;
import darwin.resourcehandling.handle.ResourceHandle;
import darwin.util.logging.InjectLogger;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ModelLoader implements ResourceFromHandle<Model[]> {

    @InjectLogger
    Logger logger = NOPLogger.NOP_LOGGER;

    @Override
    public Model[] create(ResourceHandle handle) throws IOException {
        int pos = handle.getName().lastIndexOf('.') + 1;
        if (pos > 0) {
            String ex = handle.getName().substring(pos);
            for (ModelReader reader : ServiceLoader.load(ModelReader.class)) {
                if (reader.isSupported(ex)) {
                    try {
                        return reader.readModel(handle.getStream());
                    } catch (WrongFileTypeException ex1) {
                    }
                }
            }
            throw new IOException("No model reader found for the file extension: " + ex);
        }
        throw new IOException("Can't find a model reader for a file without file extension");
    }

    @Override
    public Model[] getFallBack() {
        return new Model[0];
    }

    @Override
    public void update(ResourceHandle changed, Model[] old) {
        try {
            Model[] create = create(changed);
            if (create.length == old.length) {
                System.arraycopy(create, 0, create, 0, create.length);
            }else
            {
                logger.warn("Couldn't update model, because the submodel count differed!");
            }
        } catch (IOException ex) {
        }
    }
}
