/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.io.obj;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.annotations.ServiceProvider;
import darwin.geometrie.data.DataLayout.Format;
import darwin.geometrie.data.*;
import darwin.geometrie.io.ModelReader;
import darwin.geometrie.unpacked.Mesh;
import darwin.geometrie.unpacked.Model;
import darwin.util.math.base.Vec3;

import static darwin.geometrie.data.DataType.*;

/**
 * Parser fuer das OBJ Modell Format
 * <p/>
 * @author Daniel Heinrich
 */
//TODO the error handling of this file format reader is totaly wrong or not excistaned
@ServiceProvider(ModelReader.class)
public class ObjModelReader implements ModelReader
{

    private static class Log
    {

        public static Logger ger = Logger.getLogger(ObjModelReader.class);
    }// </editor-fold>
    private static final Element[] elements;
    private static final Element position, texcoord, normal;

    static {
        position = new Element(new GenericVector(FLOAT, 3), "Position");
        texcoord = new Element(new GenericVector(FLOAT, 2), "TexCoord");
        normal = new Element(new GenericVector(FLOAT, 3), "Normal");
        elements = new Element[]{position, texcoord, normal};
    }

    @Override
    public Model[] readModel(InputStream source) throws IOException
    {
        return loadModels(new ObjFileParser().loadOBJ(source));
    }

    @Override
    public boolean isSupported(String fileExtension)
    {
        return fileExtension.toLowerCase().equals("obj");
    }

    // <editor-fold defaultstate="collapsed" desc="Vollst�ndiges OBJFile in Models konvertieren">
    private Model[] loadModels(ObjFile obj)
    {
        Model[] mo = new Model[obj.getMaterials().size()];

        int i = 0;
        for (ObjMaterial mat : obj.getMaterials()) {
            mo[i++] = new Model(loadMesh(obj, mat), mat.creatGameMaterial());
        }

        return mo;
    }

    private Mesh loadMesh(ObjFile obj, ObjMaterial mat)
    {
        List<Face> faces = obj.getFaces(mat);

        int indexcount = 0;
        int vertexcount = 0;
        for (Face face : faces) {
            vertexcount += face.getVertCount();
            indexcount += face.getTriCount() * 3;
        }

        VertexBuffer vb = new VertexBuffer(new DataLayout(Format.INTERLEAVE32, elements), vertexcount);
        int[] indicies = new int[indexcount];

        Map<Integer, Integer> vmap = new HashMap<>();
        Iterator<Face> iter = faces.iterator();
        for (int j = 0; iter.hasNext();) {
            Face face = iter.next();
            int[] vi = new int[face.getVertCount()];
            for (int i = 0; i < vi.length; i++) {
                vi[i] = getVertex(vb, face.getVertice()[i], obj, vmap);
            }

            indicies[j] = vi[0];
            indicies[++j] = vi[1];
            indicies[++j] = vi[2];
            for (int i = 3; i < vi.length; ++i) {
                indicies[++j] = vi[i - 1];
                indicies[++j] = vi[i];
                indicies[++j] = vi[0];
            }
            ++j;
        }

        return new Mesh(indicies, vb, GL.GL_TRIANGLES);
    }

    private int getVertex(VertexBuffer vb, VertexIDs ids, ObjFile obj,
            Map<Integer, Integer> vmap)
    {
        int hs = ids.hashCode();
        Integer vi = vmap.get(hs);
        if (vi == null) {
            Vertex v = vb.newVertex();

            int p = ids.getPosition();
            if (p != 0) {
                if (p < 0) {
                    p = obj.getVerticies().size() + p + 1;
                }
                Vec3 vec = obj.getVerticies().get(p - 1);
                v.setAttribute(position, double2Float(vec.getCoords()));
            }

            int t = ids.getTexcoord();
            if (t != 0) {
                if (t < 0) {
                    t = obj.getTexcoords().size() + t + 1;
                }
                darwin.util.math.base.Vector vec = obj.getTexcoords().get(t - 1);
                v.setAttribute(texcoord, double2Float(vec.getCoords()));
            }

            int n = ids.getNormal();
            if (n != 0) {
                if (n < 0) {
                    n = obj.getNormals().size() + n + 1;
                }
                Vec3 vec = obj.getNormals().get(n - 1);
                v.setAttribute(normal, double2Float(vec.getCoords()));
            }
            vi = v.ind;
            vmap.put(hs, vi);
        }

        return vi;
    }

    private Float[] double2Float(double[] fs)
    {
        Float[] i = new Float[fs.length];
        for (int j = 0; j < i.length; j++) {
            i[j] = (float) fs[j];
        }
        return i;
    }// </editor-fold>
}
