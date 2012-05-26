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
public class Line
{

    public enum Relationship
    {

        EQUAL, PARALLEL,
        SKEW, //Windschief
        INTERSECTING
    }
    private final Vector a;
    private final Vector dir;

    public Line(Vector a, Vector dir)
    {
        this.a = a;
        this.dir = dir.normalize();

        if (a.getDimension() != dir.getDimension()) {
            throw new IllegalArgumentException("the direction vector has not the same dimension as the starting point");
        }
        if (dir.lenquad() == 0) {
            throw new IllegalArgumentException("The direction vector must not have a length of null!");
        }
    }

    public static Line fromPoints(Vector a, Vector b)
    {
        return new Line(a, a.sub(b));
    }

    public Line transform(Matrix4 mat)
    {
        return new Line(mat.mult(a), mat.mult(dir));
    }

    public Relationship getRelationship(Line g)
    {
        if (isParallelTo(g)) {
            if (contains(g.a)) {
                return Relationship.EQUAL;
            } else {
                return Relationship.PARALLEL;
            }
        } else if (isSkewTo(g)) {
            return Relationship.SKEW;
        } else {
            return Relationship.INTERSECTING;
        }
    }

    public boolean isParallelTo(Line g)
    {
        return g.dir.isParrallelTo(dir);
    }

    public boolean isEqualTo(Line g)
    {
        return isParallelTo(g) && contains(g.a);
    }

    public boolean intersectsWith(Line g)
    {
        return !isSkewTo(g);
    }

    public boolean isSkewTo(Line g)
    {
        Vector tmp = a.sub(g.a);
        return !tmp.cross(dir).isParrallelTo(tmp.cross(g.dir));
    }

    public boolean contains(Vector p)
    {
        if (p.getDimension() != a.getDimension()) {
            throw new IllegalArgumentException("The vector has to have the same dimension as the " + Line.class.getSimpleName());
        }

        return p.sub(a).isParrallelTo(dir);
    }

    public Vector getIntersection(Line g)
    {
        //TODO speed up this method
        Plane e = Plane.fromLineAndDirection(this, new Vec3(dir).cross(g.dir));
        return e.getIntersection(g);
    }

    public double distanceTo(Vector p)
    {
        Vector tmp = a.sub(p);
        double lenquad = dir.cross(tmp).lenquad() / dir.lenquad();
        return Math.sqrt(lenquad);
    }

    public double distanceTo(Line g)
    {
        if (isParallelTo(g)) {
            return distanceTo(g.a);
        } else {
            Vec3 n = dir.cross(g.dir).normalize();
            return Math.abs(a.sub(g.a).dot(n));
        }
    }

    public Vector getAufpunkt()
    {
        return a;
    }

    public Vector getDirection()
    {
        return dir;
    }

    @Override
    public String toString()
    {
        return "Aufpunkt: " + a + "\nRichtung: " + dir;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Line other = (Line) obj;

        return isEqualTo(other);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        return hash;
    }
}
