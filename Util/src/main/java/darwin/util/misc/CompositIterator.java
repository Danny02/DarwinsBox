/*
 * Copyright (C) 2013 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
 */
package darwin.util.misc;

import java.util.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class CompositIterator<E> implements Iterator<E> {

    private final List<Iterator<E>> iterators = new LinkedList<>();
    private int accIter = 0;
    private Iterator<E> current;

    public void add(Iterator<E> iter) {
        iterators.add(iter);
    }

    @Override
    public boolean hasNext() {
        if (current == null || !current.hasNext()) {
            if (accIter < iterators.size() - 1) {
                current = iterators.get(++accIter);
            } else {
                return false;
            }
        }
        return current.hasNext();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return current.next();
    }

    @Override
    public void remove() {
        current.remove();
    }
}
