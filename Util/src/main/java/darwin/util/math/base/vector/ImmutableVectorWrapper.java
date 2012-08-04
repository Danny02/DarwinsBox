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
package darwin.util.math.base.vector;

/**
 *
 * @author daniel
 */
class ImmutableVectorWrapper<E extends Vector<E>> extends Vector<E> {

    private final ImmutableVector<E> vec;

    public ImmutableVectorWrapper(ImmutableVector<E> vec) {
        this.vec = vec;
    }

    @Override
    public E add(ImmutableVector<E> b) {
        return vec.clone().add(b);
    }

    @Override
    public E add(float b) {
        return vec.clone().add(b);
    }

    @Override
    public E sub(ImmutableVector<E> b) {
        return vec.clone().sub(b);
    }

    @Override
    public E sub(float b) {
        return vec.clone().sub(b);
    }

    @Override
    public E mul(ImmutableVector<E> b) {
        return vec.clone().mul(b);
    }

    @Override
    public E mul(float b) {
        return vec.clone().mul(b);
    }

    @Override
    public E div(ImmutableVector<E> b) {
        return vec.clone().div(b);
    }

    @Override
    public E div(float b) {
        return vec.clone().div(b);
    }

    @Override
    public E min(ImmutableVector<E> b) {
        return vec.clone().min(b);
    }

    @Override
    public E min(float b) {
        return vec.clone().min(b);
    }

    @Override
    public E max(ImmutableVector<E> b) {
        return vec.clone().max(b);
    }

    @Override
    public E max(float b) {
        return vec.clone().max(b);
    }

    @Override
    public E rotateCCW(int axis) {
        return vec.clone().rotateCCW(axis);
    }

    @Override
    public E rotateCW(int axis) {
        return vec.clone().rotateCW(axis);
    }

    @Override
    public Vector3 toVector3() {
        return vec.toVector3();
    }

    @Override
    public float dot(ImmutableVector<E> b) {
        return vec.dot(b);
    }

    @Override
    public float[] getCoords() {
        return vec.getCoords();
    }

    @Override
    public E clone() {
        return vec.clone();
    }
}
