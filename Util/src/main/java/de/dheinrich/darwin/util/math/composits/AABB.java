/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.util.math.composits;

import de.dheinrich.darwin.util.math.base.*;

import static java.lang.Math.*;

/**
 * Axis Aligned Bounding Box
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO add functions
public class AABB
{
    private final Vec3 start, dimension;

    public AABB(Vec3 start, Vec3 dimension) {
        this.start = start;
        this.dimension = dimension;
    }

    public Vec3[] getCorners() {
        double[] c = dimension.getCoords();
        Vec3[] corners = new Vec3[8];
        for (int i = 0; i < 8; i++) {
            int ii = i % 4;
            corners[i] = start.add(new Vec3(
                    (ii == 1 || ii == 2) ? c[0] : 0,
                    ii < 2 ? 0 : c[1],
                    i < 4 ? 0 : c[2]));
        }

        return corners;
    }

    public AABB tranform(Matrix4 mat) {
        return new AABB(new Vec3(mat.mult(start)),
                        new Vec3(mat.mult(dimension)));
    }

    public Vec3 clamp(Vec3 point) {
        Vec3 rel = point.sub(start);
        double[] r = rel.getCoords();
        double[] d = dimension.getCoords();

        for (int i = 0; i < 3; ++i)
            if (!insideIntervall(r[i], d[i])) {
                if (r[i] - d[i] > 0)
                    r[i] = max(d[i], 0);
                else
                    r[i] = min(d[i], 0);
            }
        rel.add(start, rel);
        return rel;
    }

    public boolean isInside(Vec3 point) {
        Vec3 rel = point.sub(start);
        double[] r = rel.getCoords();
        double[] d = dimension.getCoords();

        return insideIntervall(r[0], d[0])
                && insideIntervall(r[1], d[1])
                && insideIntervall(r[2], d[2]);
    }

    private static final boolean insideIntervall(double value, double end) {
        double len = abs(value - end);
        return abs(value) <= end && len <= end;
    }
}
