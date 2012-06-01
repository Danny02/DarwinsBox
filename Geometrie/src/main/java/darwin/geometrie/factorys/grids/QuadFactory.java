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
import darwin.util.math.base.vector.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class QuadFactory
{
    private Map<ImmutableVector<Vector2>, Integer> vertice = new LinkedHashMap<>();
    private List<Quad> quads = new LinkedList<>();
    private ImmutableVector<Vector2>[] points;

    public QuadFactory(float size)
    {
        points = new ImmutableVector[3];
        points[0] = new Vector2(0, -size);
        points[1] = new Vector2(size, -size);
        points[2] = new Vector2(size, 0);
    }

    public Quad createQuad(ImmutableVector<Vector2> pos)
    {
        Quad q = new Quad(getVertIndex(pos.copy().add(points[0])),
                          getVertIndex(pos.copy().add(points[1])),
                          getVertIndex(pos.copy().add(points[2])),
                          getVertIndex(pos.copy()));
        quads.add(q);
        return q;
    }

    public void precachePosition(ImmutableVector<Vector2> pos)
    {
        getVertIndex(pos);
    }

    private int getVertIndex(ImmutableVector<Vector2> v)
    {
        Integer i = vertice.get(v);
        if (i == null) {
            i = vertice.size();
            vertice.put(v, i);
        }
        return i;
    }

    public ImmutableVector<Vector2>[] getPositions()
    {
        Set<ImmutableVector<Vector2>> v = vertice.keySet();
        ImmutableVector<Vector2>[] vs = new ImmutableVector[v.size()];
        return v.toArray(vs);
    }

    public int getVertexCount()
    {
        return vertice.size();
    }

    public Quad[] getQuads()
    {
        Quad[] q = new Quad[quads.size()];
        return quads.toArray(q);
    }

    public int getQuadCount()
    {
        return quads.size();
    }
}
