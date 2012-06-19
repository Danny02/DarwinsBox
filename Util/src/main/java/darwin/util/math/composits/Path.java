/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.util.math.composits;

import darwin.util.math.composits.LineSegment;
import darwin.util.math.base.vector.ImmutableVector;
import darwin.util.math.base.vector.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Path<E extends Vector<E>> implements Iterable<LineSegment<E>> {

    private final List<ImmutableVector<E>> positions = new ArrayList<>(1 << 10);

    public void addPathElement(ImmutableVector<E> position) {
        positions.add(position.clone());
    }

    public int size() {
        return positions.size();
    }

    @Override
    public Iterator<LineSegment<E>> iterator() {
        return new Iterator<LineSegment<E>>() {
            Iterator<ImmutableVector<E>> iter = positions.iterator();
            ImmutableVector<E> prev, next = iter.next();

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

    public Iterator<ImmutableVector<E>> getVectorIterator() {
        return positions.iterator();
    }
}
