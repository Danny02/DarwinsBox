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
public final class Vector2 extends Vector<Vector2>
{
    private float x, y;

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2()
    {
    }

    //converter methods
    @Override
    public Vector2 copy()
    {
        return new Vector2(x, y);
    }

    @Override
    public Vector3 toVector3()
    {
        return new Vector3(x, y, 0);
    }

    @Override
    public float[] getCoords()
    {
        return new float[]{x, y};
    }

    //special util methods
    public Vector3 cross(ImmutableVector<Vector2> b)
    {
        Vector2 mul = b.copy();
        return new Vector3(0, 0, x * mul.y - y * mul.x);
    }

    //interface methods
    @Override
    public Vector2 add(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        x += sum.x;
        y += sum.y;
        return this;
    }

    @Override
    public Vector2 add(float b)
    {
        x -= b;
        y -= b;
        return this;
    }

    @Override
    public Vector2 sub(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        x -= sum.x;
        y -= sum.y;
        return this;
    }

    @Override
    public Vector2 sub(float b)
    {
        x -= b;
        y -= b;
        return this;
    }

    @Override
    public Vector2 mul(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        x *= sum.x;
        y *= sum.y;
        return this;
    }

    @Override
    public Vector2 mul(float b)
    {
        x *= b;
        y *= b;
        return this;
    }

    @Override
    public Vector2 min(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        x = Math.min(x, sum.x);
        y = Math.min(y, sum.y);
        return this;
    }

    @Override
    public Vector2 min(float b)
    {
        x = Math.min(x, b);
        y = Math.min(y, b);
        return this;
    }

    @Override
    public Vector2 max(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        x = Math.max(x, sum.x);
        y = Math.max(y, sum.y);
        return this;
    }

    @Override
    public Vector2 max(float b)
    {
        x = Math.max(x, b);
        y = Math.max(y, b);
        return this;
    }

    @Override
    public float dot(ImmutableVector<Vector2> b)
    {
        Vector2 sum = b.copy();
        return x * sum.x + y * sum.y;
    }
}
