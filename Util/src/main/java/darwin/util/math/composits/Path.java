/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.util.math.composits;

import java.util.*;

import darwin.util.math.base.vector.Vector;

/**
 *
 * @author daniel
 */
public class Path<E extends Vector<E>> implements Iterable<E> {

    private final List<E> positions = new ArrayList<>();

    public void addPathElement(E position) {
        if (!positions.contains(position)) {
            positions.add(position);
        }
    }

    public int size() {
        return positions.size();
    }

    public Iterator<LineSegment<E>> getLineSegmentIterator() {
        return new Iterator<LineSegment<E>>() {
            Iterator<E> iter = positions.iterator();
             E prev;
            E next = iter.next();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public LineSegment<E> next() {
                prev = next;
                next = iter.next();
                return new LineSegment(prev, next);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    public Iterable<LineSegment<E>> getLineSegmentIterable() {
        return new Iterable<LineSegment<E>>() {
            @Override
            public Iterator<LineSegment<E>> iterator() {
                return getLineSegmentIterator();
            }
        };
    }

    public LineSegment<E> getClosingSegment() {
        return new LineSegment<>(positions.get(positions.size() - 1), positions.get(0));
    }

    @Override
    public Iterator<E> iterator() {
        return positions.iterator();
    }
}
