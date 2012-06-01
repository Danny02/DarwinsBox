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
package darwin.util.math.base.tupel;

public class Tupel3Impl implements Tupel3
{
    private final float x, y, z;

    public Tupel3Impl(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Tupel3Impl()
    {
        x = y = z = 0;
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getZ()
    {
        return z;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public float[] getCoords()
    {
        return new float[]{x, y, z};
    }
}
