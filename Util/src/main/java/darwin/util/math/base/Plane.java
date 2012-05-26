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
package darwin.util.math.base;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Plane
{

    private final Vec3 a;
    private final Vec3 normal;
    private final double nDot; // in der Hesse Form(n1*x1 + n2*x2 + n3*x3 + n0 = 0) entspricht dies n0

    public Plane(Vec3 a, Vec3 normal)
    {
        this.a = a;
        this.normal = normal.normalize();
        nDot = -this.normal.dot(a);
    }

    public static Plane fromLineAndDirection(Line g, Vec3 dir)
    {
        return new Plane(new Vec3(g.getAufpunkt()), dir.cross(g.getDirection()));
    }

    public static Plane fromPointAndLine(Vec3 start, Line g)
    {
        return new Plane(start, start.sub(g.getAufpunkt()).cross(g.getDirection()));
    }

    public static Plane fromPoints(Vec3 p1, Vec3 p2, Vec3 p3)
    {
        Vec3 tmp = p2.sub(p1);
        return new Plane(p1, tmp.cross(p3.sub(p1)));
    }

    public static Plane fromPointAndDirection(Vec3 start, Vec3 dir1, Vec3 dir2)
    {
        return new Plane(start, dir1.cross(dir2));
    }

    public boolean isParallel(Line g)
    {
        return g.getDirection().dot(normal) == 0;
    }

    public boolean isParallel(Plane e)
    {
        return Math.abs(e.getNormal().dot(normal)) == 1;
    }

    public Line getSchnittKante(Plane e)
    {
        Vec3 dir = normal.cross(e.normal);

        double dot = normal.dot(e.normal);
        double tmp = 1 / (1 - dot * dot);
        double c1 = (e.nDot * dot - nDot) * tmp;
        double c2 = (nDot * dot - e.nDot) * tmp;

        Vec3 a = normal.mult(c1);
        a.add(e.normal.mult(c2), a);

        return new Line(a, dir);
    }

    //TODO extrem slow
    public Vector getIntersection(Line g)
    {
        double NdotD = g.getDirection().dot(normal);
        if (NdotD == 0) {
            throw new IllegalArgumentException("There is no intersecting point when a line is parallel to a plane");
        }
        double delta = (-nDot - g.getAufpunkt().dot(normal)) / NdotD;
        return g.getAufpunkt().add(g.getDirection().mult(delta));
    }

    public double distanceTo(Vector p)
    {
        return Math.abs(normal.dot(p) + nDot);
    }

    public double distanceTo(Line g)
    {
        return distanceTo(g.getAufpunkt());
    }

    public double distanceTo(Plane e)
    {

        return Math.abs(nDot - e.nDot);
    }

    public Vec3 getAufpunkt()
    {
        return a;
    }

    public Vec3 getNormal()
    {
        return normal;
    }
}
