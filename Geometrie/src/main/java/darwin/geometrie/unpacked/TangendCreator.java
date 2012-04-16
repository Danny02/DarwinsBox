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
package darwin.geometrie.unpacked;

import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import darwin.geometrie.data.*;
import darwin.util.math.base.Vec3;
import darwin.util.math.base.Vector;

import static darwin.geometrie.data.DataType.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class TangendCreator implements MeshModifier {

    private final Logger logger = Logger.getLogger(
            TangendCreator.class.getName());
    private final Element tangent = new Element(new GenericVector(FLOAT, 3), "Tangent");
    private final Element position = new Element(new GenericVector(FLOAT, 4), "Position");
    private final Element texcoord = new Element(new GenericVector(FLOAT, 2), "TexCoord");
    private final Element normal = new Element(new GenericVector(FLOAT, 3), "Normal");

    @Override
    public Mesh modifie(Mesh m) {
        VertexBuffer old = m.getVertices();
        DataLayout layout = old.layout;
        boolean hasattr = layout.hasElement(position)
                && layout.hasElement(normal)
                && layout.hasElement(texcoord);

        if (m.getPrimitiv_typ() != GL.GL_TRIANGLES || !hasattr) {
            logger.warn("Mesh can't be transformed,"
                    + " only Triangle Meshs allowed!");
            return m;
        }
        layout = new DataLayout(layout, tangent);
        VertexBuffer vb = new VertexBuffer(layout, old.getVcount());
        vb.copyInto(0, old);

        int[] indice = m.getIndicies();
        for (int i = 0; i < m.getIndexCount(); i += 3) {
            Vertex v0 = vb.getVertex(indice[i]);
            Vertex v1 = vb.getVertex(indice[i + 1]);
            Vertex v2 = vb.getVertex(indice[i + 2]);
            v0.setAttribute(tangent, calculateTangent(v0, v1, v2));
            v1.setAttribute(tangent, calculateTangent(v1, v2, v0));
            v2.setAttribute(tangent, calculateTangent(v2, v0, v1));
        }

        return new Mesh(m.getIndicies(), vb, m.getPrimitiv_typ());
    }

    private Float[] calculateTangent(Vertex v0, Vertex v1, Vertex v2) {

        Vec3 pos1 = getPosition(v0);
        Vec3 pos2 = getPosition(v1);
        Vec3 pos3 = getPosition(v2);

        Vector uv1 = getTexCoord(v0);
        Vector uv2 = getTexCoord(v1);
        Vector uv3 = getTexCoord(v2);

        Vec3 v2v1 = pos2.sub(pos1);
        Vec3 v3v1 = pos3.sub(pos1);

        double c2c1b = uv2.getCoords()[1] - uv1.getCoords()[1];
        double c3c1b = uv3.getCoords()[1] - uv1.getCoords()[1];

        Vec3 n = getNormal(v0);

        Vec3 t = new Vec3(c3c1b * v2v1.getX() - c2c1b * v3v1.getX(),
                c3c1b * v2v1.getY() - c2c1b * v3v1.getY(),
                c3c1b * v2v1.getZ() - c2c1b * v3v1.getZ());

        Vec3 b = n.cross(t);
        Vec3 smoothTangent = b.cross(n);
        smoothTangent.normalize(smoothTangent);

        return float2Float(smoothTangent.getCoordsF());
    }

    private Vec3 getNormal(Vertex v) {
        return new Vec3(getDouble(v.getAttribute(normal)));
    }

    private Vec3 getPosition(Vertex v) {
        double[] pos = getDouble(v.getAttribute(position));
        return new Vec3(pos[0], pos[1], pos[2]);
    }

    private Vector getTexCoord(Vertex v) {
        return new Vector(getDouble(v.getAttribute(texcoord)));
    }

    private double[] getDouble(Number[] nb) {
        double[] ret = new double[nb.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = nb[i].floatValue();
        }
        return ret;
    }

    private Float[] float2Float(float[] arr) {
        Float[] ret = new Float[arr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i];
        }
        return ret;
    }
}
