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
package darwin.util.math.base;

import darwin.util.math.base.vector.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO get rid of all the toVector3 converts and the using of cross products
public final class Line<E extends Vector<E>> {

    public enum Relationship {

        EQUAL, PARALLEL,
        SKEW, //Windschief
        INTERSECTING
    }
    private final ImmutableVector<E> a;
    private final ImmutableVector<E> dir;

    public Line(ImmutableVector<E> a, ImmutableVector<E> dir) {
        if (dir.lengthQuad() == 0.) {
            throw new IllegalArgumentException("The direction vector must not have a length of zero!");
        }
        this.a = a.clone();
        this.dir = dir.clone().normalize();
    }

    public static <E extends Vector<E>> Line<E> fromPoints(ImmutableVector<E> a,
                                                           ImmutableVector<E> b) {
        return new Line<>(a, a.clone().sub(b));
    }

    public Relationship getRelationship(Line<E> g) {
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

    public boolean isParallelTo(Line<E> g) {
        return g.dir.isParrallelTo(dir);
    }

    public boolean isEqualTo(Line<E> g) {
        return isParallelTo(g) && contains(g.a);
    }

    public boolean intersectsWith(Line<E> g) {
        return !isSkewTo(g);
    }

    public boolean isSkewTo(Line<E> g) {
        ImmutableVector<Vector3> tmp = a.clone().sub(g.a).toVector3();
        return !tmp.clone().cross(dir.toVector3()).isParrallelTo(tmp.clone().cross(g.dir.toVector3()));
    }

    public boolean contains(ImmutableVector<E> p) {
        return p.clone().sub(a).isParrallelTo(dir);
    }

    public E getIntersection(Line<E> g) {
        float[] d = dir.getCoords();
        if (d.length < 2) {
            throw new IllegalArgumentException("Can not intersect one dimesional Vectors!");
        }
        float[] t = g.dir.getCoords();
        float[] c = g.a.clone().sub(a).getCoords();

        int a = -1, b = -1;
        out:
        for (int i = 0; i < d.length; i++) {
            if (d[i] != 0) {
                a = i;
                for (int j = 0; j < t.length; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (t[j] != 0) {
                        b = j;
                        break out;
                    }
                }
            }
        }
        
        if(a==-1 || b==-1)
        {
            throw new IllegalArgumentException("The two lines do not intersect!");
        }

        float x2 = (c[a] * d[b] / (d[a] * t[b]) - c[b] / t[b]) / (1 - t[a] * d[b] / (d[a] * t[b]));
        return g.getPoint(x2);
    }

    public double distanceToSquared(ImmutableVector<E> p) {
        Vector<E> tmp = a.clone().sub(p);
        return tmp.lengthQuad() - dir.dot(tmp);
    }

    public double distanceTo(ImmutableVector<E> p) {
        return Math.sqrt(distanceTo(p));
    }

    public double distanceTo(Line<E> g) {
        if (isParallelTo(g)) {
            return distanceTo(g.a);
        } else {
            //TODO
            Vector3 n = dir.toVector3().cross(g.dir.toVector3()).normalize();
            return Math.abs(a.clone().sub(g.a).toVector3().dot(n));
        }
    }

    public ImmutableVector<E> getStartingPoint() {
        return a;
    }

    public ImmutableVector<E> getDirection() {
        return dir;
    }

    public E getPoint(float lerp) {
        return dir.clone().mul(lerp).add(a);
    }

    public boolean isElement(ImmutableVector<E> point) {
        return point.clone().sub(getStartingPoint()).isParrallelTo(getDirection());
    }

    @Override
    public String toString() {
        return "Aufpunkt: " + a + "\nRichtung: " + dir;
    }

    @Override
    public boolean equals(Object obj) {
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
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
