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

import java.util.Arrays;

/**
 * Repr�sentation eines 3 Dimensionalen Vektors
 * <p/>
 * @author Daniel Heinrich
 */
public class GenericVector extends Vector<GenericVector>
{
    private static final long serialVersionUID = -5086769268702369258L;
    private final float[] coords;

    public GenericVector(int dimension)
    {
        if (dimension < 2) {
            throw new IllegalArgumentException("A vector with a dimension of " + dimension + " makes no sense!");
        }
        coords = new float[dimension];
    }

    public GenericVector(float... coord)
    {
        coords = coord;
    }

    @Override
    public float[] getCoords()
    {
        return Arrays.copyOf(coords, getDimension());
    }

    public int getDimension()
    {
        return coords.length;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Vektor(");
        for (int i = 0; i < coords.length; ++i) {
            sb.append(coords[i]);
            sb.append(i != coords.length - 1 ? ", " : ')');

        }
        return sb.toString();
    }

    /**
     * normale Vektor addition
     * <p/>
     * @param a      vektor der aufadiert werden soll
     * @param result vektor in dem das ergebniss gespeichert werden soll
     * <p/>
     * @return zurückgabe des "result" vektors
     */
    @Override
    public GenericVector add(ImmutableVector<GenericVector> a)
    {
        float[] summant = a.getCoords();
        assert summant.length >= coords.length;

        for (int i = 0; i < coords.length; ++i) {
            coords[i] += summant[i];
        }

        return this;
    }

    /**
     * skalare vektor addition
     * <p/>
     * @param a      summant
     * @param result vektor in dem das ergebniss gespeichert werden soll
     * <p/>
     * @return zurückgabe des "result" vektors
     */
    @Override
    public GenericVector add(float a)
    {
        for (int i = 0; i < coords.length; ++i) {
            coords[i] += a;
        }

        return this;
    }

    @Override
    public GenericVector sub(ImmutableVector<GenericVector> a)
    {
        float[] summant = a.getCoords();
        if (summant.length >= coords.length) {
            throw new IllegalArgumentException("The other vector for this arithmetic "
                    + "operation does not have at least the same dimesion!");
        }

        for (int i = 0; i < coords.length; ++i) {
            coords[i] -= summant[i];
        }

        return this;
    }

    @Override
    public Vector<GenericVector> sub(float b)
    {
        for (int i = 0; i < coords.length; ++i) {
            coords[i] -= b;
        }

        return this;
    }

    @Override
    public float dot(ImmutableVector<GenericVector> a)
    {
        float[] summant = a.getCoords();
        if (summant.length >= coords.length) {
            throw new IllegalArgumentException("The other vector for this arithmetic "
                    + "operation does not have at least the same dimesion!");
        }

        float res = 0;
        for (int i = 0; i < getDimension(); ++i) {
            res += coords[i] * summant[i];
        }

        return res;
    }

    @Override
    public Vector<GenericVector> mul(ImmutableVector<GenericVector> b)
    {
        float[] comp = b.getCoords();
        if (comp.length >= coords.length) {
            throw new IllegalArgumentException("The other vector for this arithmetic "
                    + "operation does not have at least the same dimesion!");
        }

        for (int i = 0; i < coords.length; ++i) {
            coords[i] *= comp[i];
        }

        return this;
    }

    @Override
    public GenericVector mul(float a)
    {
        for (int i = 0; i < coords.length; ++i) {
            coords[i] *= a;
        }

        return this;
    }

    @Override
    public GenericVector min(ImmutableVector<GenericVector> comp)
    {
        float[] c = comp.getCoords();
        if (c.length >= coords.length) {
            throw new IllegalArgumentException("The other vector for this arithmetic "
                    + "operation does not have at least the same dimesion!");
        }

        for (int i = 0; i < coords.length; i++) {
            coords[i] = Math.min(coords[i], c[i]);
        }
        return this;
    }

    @Override
    public Vector<GenericVector> min(float b)
    {
        for (int i = 0; i < coords.length; i++) {
            coords[i] = Math.min(coords[i], b);
        }
        return this;
    }

    @Override
    public GenericVector max(ImmutableVector<GenericVector> comp)
    {
        float[] c = comp.getCoords();
        if (c.length >= coords.length) {
            throw new IllegalArgumentException("The other vector for this arithmetic "
                    + "operation does not have at least the same dimesion!");
        }

        for (int i = 0; i < coords.length; i++) {
            coords[i] = Math.max(coords[i], c[i]);
        }
        return this;
    }

    @Override
    public Vector<GenericVector> max(float b)
    {
        for (int i = 0; i < coords.length; i++) {
            coords[i] = Math.max(coords[i], b);
        }
        return this;
    }

    @Override
    public GenericVector copy()
    {
        return new GenericVector(getCoords());
    }

    @Override
    public Vector3 toVector3()
    {
        int d = getDimension();;
        return new Vector3(coords[0], coords[1], getDimension() > 2 ? coords[2] : 0);
    }

    @Override
    public Vector3 cross(ImmutableVector<GenericVector> b)
    {
        return toVector3().cross(b.toVector3());
    }
}
