/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.io.obj;

import java.io.*;
import java.util.*;
import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.geometrie.data.DataLayout.Format;
import darwin.geometrie.data.*;
import darwin.geometrie.io.ModelReader;
import darwin.geometrie.unpacked.Mesh;
import darwin.geometrie.unpacked.Model;
import darwin.annotations.ServiceProvider;

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
    private final Element[] elements;
    private static final Element position, texcoord, normal;

    static {
        position = new Element(new GenericVector(FLOAT, 3), "Position");
        texcoord = new Element(new GenericVector(FLOAT, 2), "TexCoord");
        normal = new Element(new GenericVector(FLOAT, 3), "Normal");
    }

    public ObjModelReader()
    {
        Collection<Element> ele = new LinkedList<>();
        ele.add(position);
        ele.add(texcoord);
        ele.add(normal);

        elements = new Element[ele.size()];
        ele.toArray(elements);
    }

    @Override
    public Model[] readModel(InputStream source) throws IOException
    {
        ObjFile obj = null;

        BufferedInputStream in = new BufferedInputStream(source);
        int serializationHeader = 16 + ObjFile.class.getName().length();
        in.mark(serializationHeader);

        try {
            ObjectInputStream oi = new ObjectInputStream(in);
            obj = (ObjFile) oi.readObject();
        } catch (ClassNotFoundException | ObjectStreamException e) {
            in.reset();
        }

        if (obj == null) {
            ObjFileParser ofp = new ObjFileParser(in);
            obj = ofp.loadOBJ();
        }

        return loadModels(obj);
    }

    @Override
    public boolean isSupported(String fileExtension)
    {
        switch (fileExtension.toLowerCase()) {
            case "obj":
            case "objbin":
                return true;
            default:
                return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Vollst�ndiges OBJFile in Models konvertieren">
    private Model[] loadModels(ObjFile obj)
    {
        Model[] mo = new Model[obj.getMaterials().size()];

        int i = 0;
        for (ObjMaterial mat : obj.getMaterials()) {
            mo[i++] =
                    new Model(loadMesh(obj, mat), mat.creatGameMaterial());
        }

        return mo;
    }

    private Mesh loadMesh(ObjFile obj, ObjMaterial mat)
    {
        Map<Integer, Integer> vmap = new HashMap<>();
        List<Face> faces = obj.getFaces(mat);

        int indexcount = 0;
        int vertexcount = 0;
        for (Face face : faces) {
            vertexcount += face.getVertCount();
            indexcount += face.getTriCount() * 3;
        }

        VertexBuffer vb = new VertexBuffer(new DataLayout(Format.INTERLEAVE32, elements), vertexcount);
        int[] indicies = new int[indexcount];

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
        double[] vert = null, nor = null, tex = null;

        int p = ids.getPosition();
        if (p != 0) {
            if (p < 0) {
                p = obj.getVerticies().size() + p + 1;
            }
            vert = Arrays.copyOf(obj.getVerticies().get(p - 1).getCoords(), 4);
            vert[3] = 1.;
        }

        int t = ids.getTexcoord();
        if (t != 0) {
            if (t < 0) {
                t = obj.getTexcoords().size() + t + 1;
            }
            tex = obj.getTexcoords().get(t - 1).getCoords();
            tex = new double[]{tex[0], tex[1]};
        }

        int n = ids.getNormal();
        if (n != 0) {
            if (n < 0) {
                n = obj.getNormals().size() + n + 1;
            }
            nor = obj.getNormals().get(n - 1).getCoords();
        }

        int hs = ids.hashCode();
        Integer vi = vmap.get(hs);
        if (vi == null) {
            vi = vb.addVertex();
            Vertex v = vb.getVertex(vi);
            if (vert != null) {
                v.setAttribute(position, double2Float(
                        vert));
            }
            if (nor != null) {
                v.setAttribute(normal, double2Float(nor));
            }
            if (tex != null) {
                v.setAttribute(texcoord, double2Float(tex));
            }
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
