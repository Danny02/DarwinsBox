/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.resmanagment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ServiceLoader;

import darwin.renderer.geometrie.ModelPacker;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderObjekt;
import darwin.renderer.geometrie.unpacked.ModelObjekt;
import darwin.resourcehandling.io.ModelReader;
import darwin.resourcehandling.io.WrongFileTypeException;

/**
 *
 * @author dheinrich
 */
public class ROLoadJob implements LoadJob<RenderModel[]>
{
//    private static class Static
//    {
//        private static final Element texcoord =
//                                     new Element(GLSLType.VEC2, "TexCoord");
//        private static final Element normal =
//                                     new Element(GLSLType.VEC3, "Normal");
//    }

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

        ModelReader reader = null;

        ServiceLoader<ModelReader> services = ServiceLoader.load(ModelReader.class);
        for (ModelReader mr : services) {
            if (mr.isSupported(ext)) {
                reader = mr;
            }
        }

        if (reader == null) {
            throw new IOException("No model format reader found for "
                    + "the extension of the file \"" + path + "\"");
        }

        InputStream in = ResourcesLoader.getRessource(path);

        ModelObjekt mo = null;
        try {
            mo = reader.readModel(in);
        } catch (WrongFileTypeException ex) {
            throw new IOException("The choosen reader \""
                    + reader.getClass().getName() + "\" for the fileformat \""
                    + ext + "\" is not compatible to the file \"" + path + "\"!");
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
