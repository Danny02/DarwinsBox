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
package darwin.util.math.base.vector;

import darwin.util.math.base.matrix.Matrix4.Axis;

/**
 *
 * @author daniel
 */
public abstract class Vector<E extends Vector<E>> extends ImmutableVector<E> {

    public abstract E add(ImmutableVector<E> b);

    public abstract E add(float b);

    public abstract E sub(ImmutableVector<E> b);

    public abstract E sub(float b);

    public abstract E mul(ImmutableVector<E> b);

    public abstract E mul(float b);

    public abstract E div(ImmutableVector<E> b);

    public abstract E div(float b);

    public abstract E min(ImmutableVector<E> b);

    public abstract E min(float b);

    public abstract E max(ImmutableVector<E> b);

    public abstract E max(float b);

    public abstract E rotateCCW(Axis axis);

    public abstract E rotateCW(Axis axis);

    public E reflect(ImmutableVector<E> normal) {
        E n = normal.clone();
        n.mul(dot(n) * 2);
        sub(n);
        return (E) this;
    }

    public E normalize() {
        float len = (float) length();
        if (len != 0) {
            mul(1f / len);
        }
        return (E) this;
    }

    public E invert() {
        return (E) mul(-1);
    }
}
