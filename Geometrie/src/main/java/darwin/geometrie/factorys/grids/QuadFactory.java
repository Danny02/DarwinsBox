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
package darwin.geometrie.factorys.grids;

import java.util.*;

import darwin.geometrie.factorys.Quad;
import darwin.util.math.base.Vector;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class QuadFactory
{
    private Map<Vector, Integer> vertice = new LinkedHashMap<>();
    private List<Quad> quads = new LinkedList<>();
    private Vector[] points;

    public QuadFactory(double size) {
        points = new Vector[3];
        points[0] = new Vector(0., -size);
        points[1] = new Vector(size, -size);
        points[2] = new Vector(size, 0.);
    }

    public Quad createQuad(Vector pos) {
        Quad q = new Quad(getVertIndex(pos.add(points[0])),
                          getVertIndex(pos.add(points[1])),
                          getVertIndex(pos.add(points[2])),
                          getVertIndex(pos));
        quads.add(q);
        return q;
    }

    public void precachePosition(Vector pos) {
        getVertIndex(pos);
    }

    private int getVertIndex(Vector v) {
        Integer i = vertice.get(v);
        if (i == null) {
            i = vertice.size();
            vertice.put(v, i);
        }
        return i;
    }

    public Vector[] getPositions() {
        Set<Vector> v = vertice.keySet();
        Vector[] vs = new Vector[v.size()];
        return v.toArray(vs);
    }

    public int getVertexCount() {
        return vertice.size();
    }

    public Quad[] getQuads() {
        Quad[] q = new Quad[quads.size()];
        return quads.toArray(q);
    }

    public int getQuadCount() {
        return quads.size();
    }
}
