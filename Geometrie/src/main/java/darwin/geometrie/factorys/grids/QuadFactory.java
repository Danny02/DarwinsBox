/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
