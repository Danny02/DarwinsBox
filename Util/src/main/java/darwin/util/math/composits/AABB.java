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
package darwin.util.math.composits;

import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;

import static java.lang.Math.*;

/**
 * Axis Aligned Bounding Box
 * <p/>
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO add functions
public class AABB
{
    private final Vector3 start, dimension;

    public AABB(ImmutableVector<Vector3> start, ImmutableVector<Vector3> dimension)
    {
        this.start = start.clone();
        this.dimension = dimension.clone();
    }

    public Vector3[] getCorners()
    {
        float[] c = dimension.getCoords();
        Vector3[] corners = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            int ii = i % 4;
            corners[i] = start.clone().add(new Vector3(
                    (ii == 1 || ii == 2) ? c[0] : 0,
                    ii < 2 ? 0 : c[1],
                    i < 4 ? 0 : c[2]));
        }

        return corners;
    }

    public AABB tranform(Matrix4 mat)
    {
        return new AABB(mat.fastMult(start), mat.fastMult(dimension));
    }

    public Vector3 clamp(ImmutableVector<Vector3> point)
    {
        Vector3 rel = point.clone().sub(start);
        float[] r = rel.getCoords();
        float[] d = dimension.getCoords();

        for (int i = 0; i < 3; ++i) {
            if (!insideIntervall(r[i], d[i])) {
                if (r[i] - d[i] > 0) {
                    r[i] = max(d[i], 0);
                } else {
                    r[i] = min(d[i], 0);
                }
            }
        }
        rel.add(start);
        return rel;
    }

    public boolean isInside(ImmutableVector<Vector3> point)
    {
        Vector3 rel = point.clone().sub(start);
        float[] r = rel.getCoords();
        float[] d = dimension.getCoords();

        return insideIntervall(r[0], d[0])
                && insideIntervall(r[1], d[1])
                && insideIntervall(r[2], d[2]);
    }

    private boolean insideIntervall(double value, double end)
    {
        double len = abs(value - end);
        return abs(value) <= end && len <= end;
    }
}
