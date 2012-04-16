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

import darwin.geometrie.data.*;
import darwin.geometrie.factorys.Quad;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Cell {

    private final Quad[] quads;
    private final int[] corners;
    private final int[] midpoints;
    private final int tf;

    Cell(Quad[] quads, int tessfactor) {
        tf = tessfactor;
        this.quads = quads;
        int t1 = tf - 1;
        int t2 = tf * t1;
        corners = new int[]{quads[0].getVertice(3),
            quads[t2].getVertice(2),
            quads[tf * tf - 1].getVertice(1),
            quads[t1].getVertice(0)};

        int half = tessfactor / 2 - 1;
        midpoints = new int[]{quads[half].getVertice(0),
            quads[tf * half + t1].getVertice(1),
            quads[t2 + half].getVertice(1),
            quads[tf * half].getVertice(2),
            quads[tf * half + half].getVertice(1)};
    }

    public int[] getCorners() {
        return corners;
    }

    public int[] getMidpoints() {
        return midpoints;
    }

    public int[] getVertice() {
        int[] vertice = new int[quads.length * 4];
        for (int i = 0; i < quads.length; i++) {
            for (int j = 0; j < 4; j++) {
                vertice[i * 4 + j] = quads[i].getVertice(j);
            }
        }

        return vertice;
    }

    public int[] getTriangles() {
        int[] tris = new int[quads.length * 6];
        for (int i = 0; i < quads.length; i++) {
            int[] t = quads[i].getTriangles();
            System.arraycopy(t, 0, tris, i * 6, t.length);
        }
        return tris;
    }

    public int getTriCount() {
        return quads.length * 2;
    }

    public void interpolate(VertexBuffer vb) {
        for (Element a : vb.layout.getElements()) {
            Set<Integer> ids = new HashSet<>();
            for (int i : corners) {
                ids.add(i);
            }
            Number[] lbn = vb.getVertex(corners[0]).getAttribute(a);
            Number[] rbn = vb.getVertex(corners[1]).getAttribute(a);
            Number[] rtn = vb.getVertex(corners[2]).getAttribute(a);
            Number[] ltn = vb.getVertex(corners[3]).getAttribute(a);
            for (int x = 0; x < tf; ++x) {
                for (int y = 0; y < tf; ++y) {
//                    if (x % 2 == y % 2)
//                        continue;
                    Quad q = quads[tf * x + y];
                    for (int i = 0; i < 4; ++i) {
                        if (ids.add(q.getVertice(i))) {
                            int rx = x, ry = y;
                            if (i % 3 != 0) {
                                rx++;
                            }
                            if (i < 2) {
                                ry++;
                            }
                            double dx = (double) rx / tf;
                            double dy = (double) ry / tf;
                            Number[] l = lerp(a.getDataType(), ltn, lbn, dy);
                            Number[] r = lerp(a.getDataType(), rtn, rbn, dy);
                            Number[] res = lerp(a.getDataType(), r, l, dx);
                            Vertex v = vb.getVertex(q.getVertice(i));
                            v.setAttribute(a, res);
                        }
                    }
                }
            }
        }
    }

    public Number[] lerp(DataType t, Number[] a, Number[] b, double v) {
        Number[] c = t.mul(v, a);
        c = t.add(c, t.mul(1 - v, b));
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (!Arrays.equals(this.corners, other.corners)) {
            return false;
        }
        if (this.tf != other.tf) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Arrays.hashCode(this.corners);
        hash = 17 * hash + this.tf;
        return hash;
    }
}
