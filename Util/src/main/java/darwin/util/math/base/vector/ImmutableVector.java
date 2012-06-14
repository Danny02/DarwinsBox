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

import darwin.util.math.base.tupel.Tupel;

import static darwin.util.math.util.MathUtil.almostEqual;
import static java.lang.Math.*;

/**
 *
 * @author daniel
 */
public abstract class ImmutableVector<E extends Vector<E>> implements Tupel, Cloneable
{
    public abstract Vector3 toVector3();

    public abstract float dot(ImmutableVector<E> b);

    public abstract Vector3 cross(ImmutableVector<E> b);

    public double getAngle(ImmutableVector<E> b)
    {
        double normalizer = length() * b.length();
        return acos(dot(b) / normalizer);
    }

    @Override
    public E clone()
    {
        try {
            return (E)super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public double getAngleBothNormalized(ImmutableVector<E> b)
    {
        return acos(dot(b));
    }

    public boolean hasSameDirectionAs(ImmutableVector<E> b)
    {
        return getAngle(b) == 0.;
    }

    public boolean isParrallelTo(ImmutableVector<E> v)
    {
        double angle = getAngle(v);
        return abs(angle) == 0. || almostEqual(angle, PI, 2);
    }

    public double length()
    {
        return sqrt(lengthQuad());
    }

    public double lengthQuad()
    {
        return dot(this);
    }

    public boolean isZero()
    {
        return lengthQuad() == 0.;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("Vector(");
        for(float v:getCoords())
        {
            builder.append(v).append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.append(')').toString();
    }
}
