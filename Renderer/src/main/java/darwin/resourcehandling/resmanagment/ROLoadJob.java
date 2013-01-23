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
package darwin.resourcehandling.resmanagment;

import java.io.*;
import java.util.*;

import darwin.geometrie.io.*;
import darwin.geometrie.unpacked.Model;
import darwin.renderer.geometrie.ModelPacker;
import darwin.renderer.geometrie.packed.*;
import darwin.resourcehandling.handle.FileHandleCache;

import com.google.inject.assistedinject.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author dheinrich
 */
public class ROLoadJob implements LoadJob<RenderModel[]>
{

    public interface ROJobFactory
    {

        public ROLoadJob create(ObjConfig ljob);
    }
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final ModelPacker packer;
    private final ObjConfig ljob;
     private List<RenderObjekt> mcontainer;
    private final FileHandleCache factory;

    @AssistedInject
    public ROLoadJob(ModelPacker packer, FileHandleCache loader, @Assisted ObjConfig ljob)
    {
        this.packer = packer;
        this.factory = loader;
        this.ljob = ljob;
    }

    @Override
    public synchronized RenderModel[] load() throws IOException
    {
        String path = ljob.getPath();
        String ext = path.substring(path.lastIndexOf('.') + 1);

        InputStream in = new BufferedInputStream(factory.get(path).getStream());
        in.mark(1024);//one kilobyte should be enough to check if the file is of the right type

        Model[] mo = null;
        ServiceLoader<ModelReader> services = ServiceLoader.load(ModelReader.class);
        for (ModelReader mr : services) {
            if (mr.isSupported(ext)) {
                try {
                    mo = mr.readModel(in);
                    break;
                } catch (WrongFileTypeException ex) {
                    logger.warn("The choosen reader \"" + mr.getClass().getName()
                            + "\" for the fileformat \"" + ext
                            + "\" reports a wrong file type for \"" + path + "\"!");
                    in.reset();
                }
            }
        }

        if (mo == null) {
            throw new IOException("No model format reader found for the file \""
                    + path + "\"");
        }

        RenderModel[] models = packer.packModel(mo, ljob.getShaderResource());

        for (RenderObjekt ro2 : mcontainer) {
            ro2.setModels(models);
        }
        return models;
    }

    public synchronized void setConList(List<RenderObjekt> mcontainer)
    {
        this.mcontainer = mcontainer;

    }

    @Override
    public boolean equals( Object obj)
    {
        if (obj == null) {
            return false;
        }
        if(obj == this)
        	return true;
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ROLoadJob other = (ROLoadJob) obj;
        if (this.ljob != other.ljob && (this.ljob == null || !this.ljob.equals(
                other.ljob))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 17 * hash + (this.ljob != null ? this.ljob.hashCode() : 0);
        return hash;
    }
}
