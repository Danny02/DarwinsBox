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

import java.util.concurrent.TimeUnit;
import darwin.util.math.base.Line;
import darwin.util.math.base.vector.*;

/**
 *
 * @author daniel
 */
public class LineSegment<E extends Vector<E>> {

    private final ImmutableVector<E> start, end;

    public LineSegment(ImmutableVector<E> start, ImmutableVector<E> end) {
        this.start = start.clone();
        this.end = end.clone();
    }

    public Line<E> asLine() {
        return Line.fromPoints(start, end);
    }

    public LineSegment<E> offset(ImmutableVector<E> offset) {
        return new LineSegment<>(start.clone().add(offset), end.clone().add(offset));
    }

    public ImmutableVector<E> getEnd() {
        return end;
    }

    public ImmutableVector<E> getStart() {
        return start;
    }

    public boolean isElement(ImmutableVector<E> point) {
        Vector<E> t = point.clone().sub(start);
        Vector<E> dir = end.clone().sub(start);
        if (t.isParrallelTo(dir)) {
            float[] dc = dir.getCoords();
            for (int i = 0; i < dc.length; i++) {
                if (dc[i] != 0) {
                    float mul = t.getCoords()[i] / dc[i];
                    return 0 <= mul && mul <= 1f;
                }
            }
        }
        return false;
    }

    public boolean isInsideInterval(ImmutableVector<E> point) {
        E fromStart = point.clone().sub(start);
        E fromEnd = point.clone().sub(end);

        return fromEnd.dot(start.clone().sub(end)) >= 0
               && fromStart.dot(end.clone().sub(start)) >= 0;
    }

    public boolean intersectsWith(LineSegment<E> g) {
        Line<E> a = asLine();
        Line<E> b = g.asLine();
        if (a.isParallelTo(b)) {
            return g.isElement(start) || isElement(g.start);
        } else if (a.intersectsWith(b)) {
            E x = a.getIntersection(b);
            return isElement(x) && g.isElement(x);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Linesegment(Start:" + start + " End:" + end + ')';
    }
}
