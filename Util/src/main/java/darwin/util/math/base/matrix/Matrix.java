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
package darwin.util.math.base.matrix;

/**
 *
 * @author daniel
 */
public abstract class Matrix<E extends Matrix<E>> extends ImmutableMatrix<E>
{

    public E loadIdentity()
    {
        int mod = getDimension() + 1;
        for (int i = 0; i < getArray().length; ++i) {
            getArray()[i] = i % mod == 0 ? 1f : 0f;
        }
        return (E)this;
    }

    /**
     * Multipliziert die GenericMatrix mit einem Scalaren Faktor
     */
    public E mult(float v)
    {
        for (int i = 0; i < getArray().length; i++) {
            getArray()[i] *= v;
        }

        return (E)this;
    }

    /**
     * Einfache Matrizen Multiplikation
     */
    //TODO schauen das es mit matrizen unterschiedlicher dimensionen geht?
    public E mult(ImmutableMatrix<E> multi)
    {
        float[] temp = new float[getDimension() * getDimension()];

        for (int i = 0; i < getDimension(); i++) {
            for (int j = 0; j < getDimension(); j++) {
                int jd = j * getDimension();
                int ijd = i + jd;
                for (int k = 0; k < getDimension(); k++) {
                    temp[ijd] += getArray()[i + k * getDimension()] * multi.getArray()[k + jd];
                }
            }
        }

        setMat(temp);

        return (E)this;
    }

    public E add(ImmutableMatrix<E> sum)
    {
        float[] su = sum.getArray();

        for (int i = 0; i < su.length; ++i) {
            getArray()[i] += su[i];
        }

        return (E)this;
    }

    public E sub(ImmutableMatrix<E> sum)
    {
        float[] su = sum.getArray();

        for (int i = 0; i < su.length; ++i) {
            getArray()[i] -= su[i];
        }

        return (E)this;
    }

    /**
     * Transponiert die GenericMatrix
     */
    public E transpose()
    {
        final float[] f = new float[getDimension() * getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            for (int j = 0; j < getDimension(); j++) {
                f[i + j * getDimension()] = getArray()[j + i * getDimension()];
            }
        }

        setMat(f);

        return (E)this;
    }

    public E adjunkt()
    {
        final float[] f = new float[getDimension() * getDimension()];
        for (int i = 0, n = 1; i < getDimension(); i++) {
            for (int j = 0; j < getDimension(); j++, n *= -1) {
                f[j + i * getDimension()] = getMinor(i, j).getDeterminate() * n;
            }
            if (getDimension() % 2 == 0) {
                n *= -1;
            }
        }

        setMat(f);
        transpose();

        return (E)this;
    }

    /**
     * Ersetzt die GenericMatrix mit ihrer Inversen
     */
    //TODO durch womoeglich schneller version ersetzten(unten auskommentierte)
    public E inverse()
    {
//        var inv = [];
//
//        inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] +
//            m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10];
//        inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] -
//            m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10];
//        inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] +
//            m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9];
//        inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] -
//            m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9];
//        inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] -
//            m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10];
//        inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] +
//            m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10];
//        inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] -
//            m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9];
//        inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] +
//            m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9];
//        inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] +
//            m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6];
//        inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] -
//            m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6];
//        inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] +
//            m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5];
//        inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] -
//            m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5];
//        inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] -
//            m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6];
//        inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] +
//            m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6];
//        inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] -
//            m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5];
//        inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] +
//            m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5];
//
//        /** @type {number} */
//        var det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];
//
//        if (det === 0) {
//            return false;
//        }
//
//        det = 1.0 / det;
//
//        for (var i = 0; i < 16; i = i + 1) {
//            invOut[i] = inv[i] * det;
//        }
        adjunkt();
        mult(1f / getDeterminate());

        return (E)this;
    }

    public abstract void setMat(float[] mat);
}
