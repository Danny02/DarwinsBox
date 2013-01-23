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

import darwin.util.math.base.tupel.Tupel2;

/**
 *
 * @author daniel
 */
public final class Vector2 extends Vector<Vector2> implements Tupel2 {

    private float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(ImmutableVector v) {
        float[] c = v.getCoords();
        if (c.length > 0) {
            x = c[0];
        }
        if (c.length > 1) {
            y = c[1];
        }
    }

    public Vector2() {
    }

    public static Vector2 createPositionFromID(int id, int width) {
        return new Vector2(id % width, id / width);
    }

    public Vector2 getRotatedCCW() {
        return new Vector2(-getY(), getX());
    }

    public Vector2 getRotatedCW() {
        return new Vector2(getY(), -getX());
    }

    //converter methods
//    @Override
//    public Vector2 clone()
//    {
//        return new Vector2(x, y);
//    }
    @Override
    public Vector3 toVector3() {
        return new Vector3(x, y, 0);
    }

    @Override
    public float[] getCoords() {
        return new float[]{x, y};
    }

    //interface methods
    @Override
    public Vector2 add(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x += sum.x;
        y += sum.y;
        return this;
    }

    @Override
    public Vector2 add(float b) {
        x += b;
        y += b;
        return this;
    }

    @Override
    public Vector2 sub(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x -= sum.x;
        y -= sum.y;
        return this;
    }

    @Override
    public Vector2 sub(float b) {
        x -= b;
        y -= b;
        return this;
    }

    @Override
    public Vector2 mul(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x *= sum.x;
        y *= sum.y;
        return this;
    }

    @Override
    public Vector2 mul(float b) {
        x *= b;
        y *= b;
        return this;
    }

    @Override
    public Vector2 div(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x /= sum.x;
        y /= sum.y;
        return this;
    }

    @Override
    public Vector2 div(float b) {
        x /= b;
        y /= b;
        return this;
    }

    @Override
    public Vector2 min(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x = Math.min(x, sum.x);
        y = Math.min(y, sum.y);
        return this;
    }

    @Override
    public Vector2 min(float b) {
        x = Math.min(x, b);
        y = Math.min(y, b);
        return this;
    }

    @Override
    public Vector2 max(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        x = Math.max(x, sum.x);
        y = Math.max(y, sum.y);
        return this;
    }

    @Override
    public Vector2 max(float b) {
        x = Math.max(x, b);
        y = Math.max(y, b);
        return this;
    }

    @Override
    public float dot(ImmutableVector<Vector2> b) {
        Vector2 sum = b.clone();
        return x * sum.x + y * sum.y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Vector2 rotateCCW(int axis) {
        float tmp = x;
        x = -y;
        y = tmp;

        return this;
    }

    @Override
    public Vector2 rotateCW(int axis) {
        float tmp = y;
        y = -x;
        x = tmp;

        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.x);
        hash = 53 * hash + Float.floatToIntBits(this.y);
        return hash;
    }

    @Override
    public boolean equals( Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector2 other = (Vector2) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return true;
    }
}
