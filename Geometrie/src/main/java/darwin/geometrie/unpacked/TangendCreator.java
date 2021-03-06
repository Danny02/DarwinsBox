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
 * You should have received a clone of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.geometrie.unpacked;

import darwin.geometrie.data.GenericVector;
import darwin.geometrie.data.*;
import darwin.util.logging.InjectLogger;
import darwin.util.math.base.vector.*;

import javax.media.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static darwin.geometrie.data.DataType.FLOAT;
import static darwin.geometrie.io.ModelReader.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class TangendCreator implements MeshModifier
{

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private static final Element tangent = new Element(new GenericVector(FLOAT, 3), TANGENT_ATTRIBUTE);
    private static final Element position = new Element(new GenericVector(FLOAT, 4), POSITION_ATTRIBUTE);
    private static final Element texcoord = new Element(new GenericVector(FLOAT, 2), TEXTURE_ATTRIBUTE);
    private static final Element normal = new Element(new GenericVector(FLOAT, 3), NORMAL_ATTRIBUTE);

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

        Vector3 pos1 = getPosition(v0);
        Vector3 pos2 = getPosition(v1);
        Vector3 pos3 = getPosition(v2);

        Vector2 uv1 = getTexCoord(v0);
        Vector2 uv2 = getTexCoord(v1);
        Vector2 uv3 = getTexCoord(v2);

        Vector3 v2v1 = pos2.clone().sub(pos1);
        Vector3 v3v1 = pos3.clone().sub(pos1);

        float c2c1b = uv2.getY() - uv1.getY();
        float c3c1b = uv3.getY() - uv1.getY();

        Vector3 n = getNormal(v0);

        Vector3 t = new Vector3(c3c1b * v2v1.getX() - c2c1b * v3v1.getX(),
                c3c1b * v2v1.getY() - c2c1b * v3v1.getY(),
                c3c1b * v2v1.getZ() - c2c1b * v3v1.getZ());

        Vector3 b = n.cross(t);
        Vector3 smoothTangent = b.cross(n).normalize();

        return float2Float(smoothTangent.getCoords());
    }

    private Vector3 getNormal(Vertex v) {
        float[] pos = getFloat(v.getAttribute(normal));
        return new Vector3(pos[0], pos[1], pos[2]);
    }

    private Vector3 getPosition(Vertex v) {
        float[] pos = getFloat(v.getAttribute(position));
        return new Vector3(pos[0], pos[1], pos[2]);
    }

    private Vector2 getTexCoord(Vertex v) {
        float[] pos = getFloat(v.getAttribute(texcoord));
        return new Vector2(pos[0], pos[1]);
    }

    private float[] getFloat(Number[] nb) {
        float[] ret = new float[nb.length];
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
