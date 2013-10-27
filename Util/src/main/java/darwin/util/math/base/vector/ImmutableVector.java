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
public abstract class ImmutableVector<E extends Vector<E>> implements Tupel, Cloneable {
    
    public static final ImmutableVector<Vector3> POS_X = new Vector3(1, 0, 0);
    public static final ImmutableVector<Vector3> POS_Y = new Vector3(0, 1, 0);
    public static final ImmutableVector<Vector3> POS_Z = new Vector3(0, 0, 1);

    public abstract Vector3 toVector3();

    public abstract float dot(ImmutableVector<E> b);

    public double getAngle(ImmutableVector<E> b) {
        float normalizer = (float) (length() * b.length());
        return acos(dot(b) / normalizer);
    }

    @Override
    public E clone() {
        try {
            return (E) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public double getAngleBothNormalized(ImmutableVector<E> b) {
        return acos(dot(b));
    }

    public boolean hasSameDirectionAs(ImmutableVector<E> b) {
        return almostEqual(getAngle(b), 0., 2);
    }

    public boolean isParrallelTo(ImmutableVector<E> v) {
        double angle = getAngle(v);
        return almostEqual(angle, 0., 2) || almostEqual(angle, PI, 2);
    }

    public double length() {
        return sqrt(lengthQuad());
    }

    public double lengthQuad() {
        return dot(this);
    }

    public boolean isZero() {
        return lengthQuad() == 0.;
    }

    public double distance(ImmutableVector<E> v) {
        return sqrt(distanceQuad(v));
    }

    public double distanceQuad(ImmutableVector<E> v) {
        return clone().sub(v).lengthQuad();
    }

    //TODO weis ned ob das eine gute idee ist, dieser wrapper könnte einer funktion übergeben werden
    // die sich darauf verlässt das einzelne methoden aufrufe den internen state verändern
    /**
     * Creates a Vector instance which wraps this immutable wrapper, this
     * wrapper clones the immutable vector before each call to an method which
     * would change the internal state
     *
     * @return
     */
    public Vector<E> makeImmutable() {
        return new ImmutableVectorWrapper(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Vector(");
        for (float v : getCoords()) {
            builder.append(v).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.append(')').toString();
    }
}
