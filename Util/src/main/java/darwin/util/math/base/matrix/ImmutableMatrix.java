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
package darwin.util.math.base.matrix;

import java.nio.FloatBuffer;
import java.util.Arrays;

import darwin.util.math.base.tupel.Tupel;
import darwin.util.math.base.vector.*;

/**
 *
 * @author daniel
 */
public abstract class ImmutableMatrix<E extends Matrix<E>> implements Cloneable
{
    public Vector mult(Tupel tupel)
    {
        float[] a = tupel.getCoords();
        if (a.length < getDimension()) {
            throw new IllegalArgumentException("The Tupel you want to multiply "
                    + "with a Matrix has to have at a minimum the size of the matrix dimension");
        }

        final float[] result = new float[getDimension()];

        for (int i = 0; i < getDimension(); ++i) {
            for (int j = 0; j < getDimension(); ++j) {
                result[i] += a[j] * getArray()[i + j * getDimension()];
            }
        }

        return new GenericVector(result);
    }



    public float getDeterminate()
    {
        if (getDimension() > 1) {
            float det = 0;
            for (int j = 0, n = 1; j < getDimension(); j++, n *= -1) {
                det += getMinor(0, j).getDeterminate() * getArray()[j] * n;
            }
            return det;
        } else {
            return getArray()[0];
        }
    }

    /**
     * Der GenericMatrix wird jeweils eine Spalte und Zeile gestrichen und die
     * resultierende GenericMatrix zur�ckgegeben
     */
    public Matrix getMinor(int skipx, int skipy)
    {
        final int dim = getDimension() - 1;
        final GenericMatrix ret = new GenericMatrix(dim);
        float[] minor = ret.getArray();
        for (int i = 0; i < getDimension() - 1; i++) {
            int idim = i * dim;
            int x = i + (i >= skipx ? 1 : 0);
            int xdim = x * getDimension();
            for (int j = 0; j < getDimension() - 1; j++) {
                int y = j + (j >= skipy ? 1 : 0);
                minor[j + idim] = getArray()[y + xdim];
            }
        }
        return ret;
    }

    public Vector getRow(int index)
    {
        float[] coor = new float[getDimension()];
        for (int i = 0; i < getDimension(); ++i) {
            coor[i] = getArray()[index + getDimension() * i];
        }
        return new GenericVector(coor);
    }

    public Vector getColumn(int index)
    {
        final float[] coor = new float[getDimension()];
        System.arraycopy(getArray(), getDimension() * index, coor, 0, getDimension());
        return new GenericVector(coor);
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        for (int j = 0; j < getDimension(); j++) {
            for (int i = 0; i < getDimension(); i++) {
                buf.append(getArray()[j + getDimension() * i] + '\t');
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    @Override
    public E clone()
    {
        try {
            return (E)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean equals( Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericMatrix other = (GenericMatrix) obj;
        if (!Arrays.equals(getArray(), other.getArray())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 73 * hash + Arrays.hashCode(getArray());
        return hash;
    }

    public abstract int getDimension();

    public abstract float[] getArray();

    public abstract FloatBuffer getFloatBuffer();
}
