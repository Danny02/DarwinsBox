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

import darwin.util.math.base.tupel.Tupel3;

/**
 *
 * @author daniel
 */
public final class Vector3 extends Vector<Vector3> implements Tupel3
{
    private float x, y, z;

    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3()
    {
    }

//    @Override
//    public Vector3 clone()
//    {
//        return new Vector3(x, y, z);
//    }

    @Override
    public Vector3 toVector3()
    {
        return clone();
    }

    @Override
    public float[] getCoords()
    {
        return new float[]{x, y, z};
    }

    @Override
    public Vector3 cross(ImmutableVector<Vector3> b)
    {
        Vector3 mul = b.clone();
        return new Vector3(y * mul.z - z * mul.y,
                           z * mul.x - x * mul.z,
                           x * mul.y - y * mul.x);
    }

    @Override
    public Vector3 add(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        x += sum.x;
        y += sum.y;
        z += sum.z;
        return this;
    }

    @Override
    public Vector3 add(float b)
    {
        x -= b;
        y -= b;
        z -= b;
        return this;
    }

    @Override
    public Vector3 sub(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        x -= sum.x;
        y -= sum.y;
        z -= sum.z;
        return this;
    }

    @Override
    public Vector3 sub(float b)
    {
        x -= b;
        y -= b;
        z -= b;
        return this;
    }

    @Override
    public Vector3 mul(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        x *= sum.x;
        y *= sum.y;
        z *= sum.z;
        return this;
    }

    @Override
    public Vector3 mul(float b)
    {
        x *= b;
        y *= b;
        z *= b;
        return this;
    }

    @Override
    public Vector3 min(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        x = Math.min(x, sum.x);
        y = Math.min(y, sum.y);
        z = Math.min(z, sum.z);
        return this;
    }

    @Override
    public Vector3 min(float b)
    {
        x = Math.min(x, b);
        y = Math.min(y, b);
        z = Math.min(z, b);
        return this;
    }

    @Override
    public Vector3 max(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        x = Math.max(x, sum.x);
        y = Math.max(y, sum.y);
        z = Math.max(z, sum.z);
        return this;
    }

    @Override
    public Vector3 max(float b)
    {
        x = Math.max(x, b);
        y = Math.max(y, b);
        z = Math.max(z, b);
        return this;
    }

    @Override
    public float dot(ImmutableVector<Vector3> b)
    {
        Vector3 sum = b.clone();
        return x * sum.x + y * sum.y + z * sum.z;
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public float getZ()
    {
        return z;
    }
}
