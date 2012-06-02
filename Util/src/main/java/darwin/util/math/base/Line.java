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

import darwin.util.math.base.vector.ImmutableVector;
import darwin.util.math.base.vector.Vector;
import darwin.util.math.base.vector.Vector2;
import darwin.util.math.base.vector.Vector3;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public final class Line<E extends Vector<E>>
{
    public enum Relationship
    {
        EQUAL, PARALLEL,
        SKEW, //Windschief
        INTERSECTING
    }
    private final ImmutableVector<E> a;
    private final ImmutableVector<E> dir;

    public  Line(ImmutableVector<E> a, ImmutableVector<E> dir)
    {
        if (dir.lengthQuad() == 0.) {
            throw new IllegalArgumentException("The direction vector must not have a length of null!");
        }
        this.a = a.copy();
        this.dir = dir.copy().normalize();
    }

    public static <E extends Vector<E>> Line<E> fromPoints(ImmutableVector<E> a, ImmutableVector<E> b)
    {
        return new Line<>(a, a.copy().sub(b));
    }

    public Relationship getRelationship(Line<E> g)
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

    public boolean isParallelTo(Line<E> g)
    {
        return g.dir.isParrallelTo(dir);
    }

    public boolean isEqualTo(Line<E> g)
    {
        return isParallelTo(g) && contains(g.a);
    }

    public boolean intersectsWith(Line<E> g)
    {
        return !isSkewTo(g);
    }

    public boolean isSkewTo(Line<E> g)
    {
        Vector<E> tmp = a.copy().sub(g.a);
        return !tmp.cross(dir).isParrallelTo(tmp.cross(g.dir));
    }

    public boolean contains(ImmutableVector<E> p)
    {
        return p.copy().sub(a).isParrallelTo(dir);
    }

    public Vector3 getIntersection(Line<E> g)
    {
        //TODO speed up this method
        Line<Vector3> t = new Line<>(a.toVector3(), dir.toVector3());
        Line<Vector3> o = new Line<>(g.a.toVector3(), g.dir.toVector3());

        Plane e = Plane.fromLineAndDirection(t, dir.cross(g.dir));
        return e.getIntersection(o);
    }    

    public double distanceTo(ImmutableVector<E> p)
    {
        Vector tmp = a.copy().sub(p);
        double lenquad = dir.cross(tmp).lengthQuad() / dir.lengthQuad();
        return Math.sqrt(lenquad);
    }

    public double distanceTo(Line<E> g)
    {
        if (isParallelTo(g)) {
            return distanceTo(g.a);
        } else {
            Vector3 n = dir.cross(g.dir).normalize();
            return Math.abs(a.copy().sub(g.a).toVector3().dot(n));
        }
    }

    public ImmutableVector<E> getAufpunkt()
    {
        return a;
    }

    public ImmutableVector<E> getDirection()
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
