/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment;

import java.io.*;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

import darwin.geometrie.io.ModelReader;
import darwin.geometrie.io.WrongFileTypeException;
import darwin.geometrie.unpacked.Model;
import darwin.renderer.geometrie.ModelPacker;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderObjekt;

/**
 *
 * @author dheinrich
 */
public class ROLoadJob implements LoadJob<RenderModel[]>
{

    private static final Logger logger = Logger.getLogger(ROLoadJob.class);
    private final ObjConfig ljob;
    private List<RenderObjekt> mcontainer;

    public ROLoadJob(ObjConfig ljob)
    {
        this.ljob = ljob;
    }

    @Override
    public synchronized RenderModel[] load() throws IOException
    {
        String path = ljob.getPath();
        String ext = path.substring(path.lastIndexOf('.') + 1);

        InputStream in = new BufferedInputStream(ResourcesLoader.getRessource(path));
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

        RenderModel[] models = ModelPacker.packModel(mo, ljob.getShader());

        for (RenderObjekt ro2 : mcontainer) {
            ro2.setModels(models);
        }
        return models;
    }

    public synchronized void setConList(List<RenderObjekt> mcontainer)
    {
        synchronized (mcontainer) {
            this.mcontainer = mcontainer;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
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
