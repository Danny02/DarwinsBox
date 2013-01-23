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

import static darwin.util.math.util.MathUtil.almostEqual;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Plane {

    private final ImmutableVector<Vector3> a, normal;
    private final float nDot; // in der Hesse Form(n1*x1 + n2*x2 + n3*x3 + n0 = 0) entspricht dies n0

    public Plane(ImmutableVector a, ImmutableVector normal) {
        this.a = a.toVector3();
        this.normal = normal.toVector3().normalize();
        nDot = -this.normal.dot(a);
    }

    public static <E extends Vector<E>> Plane fromLineAndDirection(
            Line<E> g, ImmutableVector<E> dir) {
        return new Plane(g.getStartingPoint(), dir.toVector3().cross(g.getDirection().toVector3()));
    }

    public static <E extends Vector<E>> Plane fromPointAndLine(
            ImmutableVector<E> start, Line<E> g) {
        return new Plane(start, start.clone().sub(g.getStartingPoint()).toVector3().cross(g.getDirection().toVector3()));
    }

    public static <E extends Vector<E>> Plane fromPoints(ImmutableVector<E> p1,
                                                         ImmutableVector<E> p2,
                                                         ImmutableVector<E> p3) {
        Vector<E> tmp = p2.clone().sub(p1);
        return new Plane(p1, tmp.toVector3().cross(p3.clone().sub(p1).toVector3()));
    }

    public static <E extends Vector<E>> Plane fromPointAndDirection(
            ImmutableVector start, ImmutableVector<E> dir1,
            ImmutableVector<E> dir2) {
        return new Plane(start, dir1.toVector3().cross(dir2.toVector3()));
    }

    public boolean isParallel(Line<Vector3> g) {
        return g.getDirection().dot(normal) == 0f;
    }

    public boolean isParallel(Plane e) {
        float v = Math.abs(e.getNormal().dot(normal));

        return almostEqual(v, 1f, 2);
    }

    public Line<Vector3> getSchnittKante(Plane e) {
        Vector3 dir = normal.toVector3().cross(e.normal);

        float dot = normal.dot(e.normal);
        float tmp = 1 / (1 - dot * dot);
        float c1 = (e.nDot * dot - nDot) * tmp;
        float c2 = (nDot * dot - e.nDot) * tmp;

        Vector3 a = normal.clone().mul(c1);
        a.add(e.normal.clone().mul(c2));

        return new Line(a, dir);
    }

    //TODO extrem slow
    public Vector3 getIntersection(Line<Vector3> g) {
        float NdotD = g.getDirection().dot(normal);
        if (NdotD == 0) {
            throw new IllegalArgumentException("There is no intersecting point when a line is parallel to a plane");
        }
        float delta = (-nDot - g.getStartingPoint().dot(normal)) / NdotD;
        return g.getStartingPoint().clone().add(g.getDirection().clone().mul(delta));
    }

    public float distanceTo(ImmutableVector<Vector3> p) {
        return Math.abs(normal.dot(p) + nDot);
    }

    public float distanceTo(Line<Vector3> g) {
        return distanceTo(g.getStartingPoint());
    }

    public float distanceTo(Plane e) {

        return Math.abs(nDot - e.nDot);
    }

    public ImmutableVector<Vector3> getAufpunkt() {
        return a;
    }

    public ImmutableVector<Vector3> getNormal() {
        return normal;
    }
}
