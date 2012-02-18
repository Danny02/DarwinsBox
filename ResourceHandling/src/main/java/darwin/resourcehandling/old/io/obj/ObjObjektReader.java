/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.old.io.obj;

import java.util.*;
import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.renderer.geometrie.data.DataLayout.Format;
import darwin.renderer.geometrie.data.*;
import darwin.renderer.geometrie.unpacked.*;
import darwin.renderer.opengl.Element;
import darwin.renderer.opengl.GLSLType;
import darwin.renderer.shader.uniform.GameMaterial;
import darwin.resourcehandling.old.io.ObjektReader;
import darwin.resourcehandling.old.resmanagment.ObjConfig;
import darwin.resourcehandling.old.resmanagment.texture.ShaderDescription;


/**
 * Parser fuer das OBJ Modell Format
 * @author Daniel Heinrich
 */
//TODO the error handling of this file format reader is totaly wrong or not excistaned
public class ObjObjektReader implements ObjektReader
{
    // <editor-fold defaultstate="collapsed" desc="misc">
    public enum Scale
    {
        ABSOLUTE
        {
            @Override
            protected double scalefaktor(ObjFile obj, double scale) {
                return scale;
            }
        },
        HEIGHT
        {
            @Override
            protected double scalefaktor(ObjFile obj, double scale) {
                return scale / obj.getHeight();
            }
        },
        WIDTH
        {
            @Override
            protected double scalefaktor(ObjFile obj, double scale) {
                return scale / obj.getWidth();
            }
        },
        DEPTH
        {
            @Override
            protected double scalefaktor(ObjFile obj, double scale) {
                return scale / obj.getDepth();
            }
        };

        protected abstract double scalefaktor(ObjFile obj, double scale);
    }

    private static class Log
    {
        public static Logger ger = Logger.getLogger(ObjObjektReader.class);

    }// </editor-fold>
    private final Element[] elements;
    private final Element position, texcoord, normal;

    public ObjObjektReader() {
        position = new Element(GLSLType.VEC4, "Position");
        texcoord = new Element(GLSLType.VEC2, "TexCoord");
        normal = new Element(GLSLType.VEC3, "Normal");

        Collection<Element> ele = new LinkedList<>();
        ele.add(position);
        ele.add(texcoord);
        ele.add(normal);

        elements = new Element[ele.size()];
        ele.toArray(elements);
    }

    @Override
    public ModelObjekt loadObjekt(ObjConfig ljob) {
        OBJFormatReader ofr = new OBJFormatReader(ljob.getPath());
        ObjFile obj = ofr.loadOBJ();
        if (ljob.isCentered())
            obj.center();
        try {
            obj.rescale(ljob.getScaleType().scalefaktor(obj, ljob.getScale()));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        Model[] models = loadModels(obj, ljob.getShader());
        ModelObjekt mo = new ModelObjekt(models, ljob.getPath());
        int triscount = 0;
        int vertcount = 0;
        for (Model m : models) {
            triscount += m.getMesh().getIndexCount() / 3;
            vertcount += m.getMesh().getVertexCount();
        }
        Log.ger.info(ljob.getPath() + "  ...loaded! (triscount: "
                + triscount + ", vertcount: " + vertcount + ")");
        return mo;
    }

    // <editor-fold defaultstate="collapsed" desc="Vollst�ndiges OBJFile in Models konvertieren">
    private Model[] loadModels(ObjFile obj, ShaderDescription descr) {
        Model[] mo = new Model[obj.getMaterials().size()];

        int i = 0;
        for (ObjMaterial mat : obj.getMaterials())
            mo[i++] =
            new Model(loadMesh(obj, mat), new GameMaterial(mat, descr));

        return mo;
    }

    private Mesh loadMesh(ObjFile obj, ObjMaterial mat) {
        Map<Integer, Integer> vmap = new Hashtable<>();
        List<Face> faces = obj.getFaces(mat);

        Iterator<Face> iter = faces.iterator();
        int indexcount = 0;
        int vertexcount = 0;
        while (iter.hasNext()) {
            Face face = iter.next();
            vertexcount += face.getVertCount();
            indexcount += face.getTriCount() * 3;
        }

        VertexBuffer vb = new VertexBuffer(
                new DataLayout(Format.INTERLEAVE32, elements),
                vertexcount);
        int[] indicies = new int[indexcount];

        iter = faces.iterator();
        for (int j = 0; iter.hasNext();) {
            Face face = iter.next();
            int[] vi = new int[face.getVertCount()];
            for (int i = 0; i < vi.length; i++)
                vi[i] = getVertex(vb, face.getVertice()[i], obj, vmap);
            indicies[j++] = vi[0];
            indicies[j++] = vi[1];
            indicies[j++] = vi[2];
            for (int i = 3; i < vi.length; ++i) {
                indicies[j++] = vi[i - 1];
                indicies[j++] = vi[i];
                indicies[j++] = vi[0];
            }

        }

        return new Mesh(indicies, vb, GL.GL_TRIANGLES);
    }

    private int getVertex(VertexBuffer vb, VertexIDs ids, ObjFile obj,
                          Map<Integer, Integer> vmap) {
        double[] vert = null, nor = null, tex = null;

        int p = ids.getPosition();
        if (p != 0) {
            if (p < 0)
                p = obj.getVerticies().size() + p + 1;
            vert = Arrays.copyOf(obj.getVerticies().get(p - 1).getCoords(), 4);
            vert[3] = 1.;
        }

        int t = ids.getTexcoord();
        if (t != 0) {
            if (t < 0)
                t = obj.getTexcoords().size() + t + 1;
            tex = obj.getTexcoords().get(t - 1).getCoords();
            tex = new double[]{tex[0], tex[1]};
        }

        int n = ids.getNormal();
        if (n != 0) {
            if (n < 0)
                n = obj.getNormals().size() + n + 1;
            nor = obj.getNormals().get(n - 1).getCoords();
        }

        int hs = ids.hashCode();
        Integer vi = vmap.get(hs);
        if (vi == null) {
            vi = vb.addVertex();
            Vertex v = vb.getVertex(vi);
            if (vert != null)
                v.setAttribute(position, double2Float(
                        vert));
            if (nor != null)
                v.setAttribute(normal, double2Float(nor));
            if (tex != null)
                v.setAttribute(texcoord, double2Float(tex));
            vmap.put(hs, vi);
        }

        return vi;
    }

    private Float[] double2Float(double[] fs) {
        Float[] i = new Float[fs.length];
        for (int j = 0; j < i.length; j++)
            i[j] = (float) fs[j];
        return i;
    }// </editor-fold>
}
