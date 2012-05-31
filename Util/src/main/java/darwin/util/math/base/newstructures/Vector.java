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
package darwin.util.math.base.newstructures;

/**
 *
 * @author daniel
 */
public abstract class Vector<E extends Vector<E>> extends ImmutableVector<E>
{
    public abstract Vector<E> add(ImmutableVector<E> b);

    public abstract Vector<E> add(float b);

    public abstract Vector<E> sub(ImmutableVector<E> b);

    public abstract Vector<E> sub(float b);

    public abstract Vector<E> mul(ImmutableVector<E> b);

    public abstract Vector<E> mul(float b);

    public abstract Vector<E> min(ImmutableVector<E> b);

    public abstract Vector<E> min(float b);

    public abstract Vector<E> max(ImmutableVector<E> b);

    public abstract Vector<E> max(float b);

    public E reflect(ImmutableVector<E> normal)
    {
        E n = normal.copy();
        n.mul(dot(n) * 2);
        sub(n);
        return (E)this;
    }

    public E normalize()
    {
        float len = (float) length();
        if (len != 0) {
            mul(len);
        }
        return (E)this;
    }
}
