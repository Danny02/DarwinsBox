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
package darwin.util.math.base;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Quadratische Matrix beliebiger Dimension
 * @author Daniel Heinrich
 */
public class Matrix implements Serializable, Cloneable
{
//    private static final Queue<Matrix> stack = new ArrayBlockingQueue<Matrix>(
//            100);
    private static final long serialVersionUID = -6066356639925185801L;
    private final float[] data;
    //vor aenderungen serialisierungs code in der "readObject" Methode beachten
    private transient final FloatBuffer buffer;
    private transient final int dimension;

    public Matrix(int dim) {
        dimension = dim;
        data = new float[dim * dim];
        buffer = FloatBuffer.wrap(data);
    }

    /**
     * Laed die Einheits Matrix
     */
    public Matrix loadIdentity() {
        int mod = dimension + 1;
        for (int i = 0; i < data.length; ++i)
            data[i] = i % mod == 0 ? 1f : 0f;
        return this;
    }

    /**
     * Multipliziert die Matrix mit einem Scalaren Faktor
     */
    public Matrix mult(float v) {
        return mult(v, new Matrix(dimension));
    }

    /**
     * Multipliziert die Matrix mit einem Scalaren Faktor
     */
    public Matrix mult(float v, Matrix result) {
        final int qdim = dimension * dimension;

        for (int i = 0; i < qdim; i++)
            result.data[i] = data[i] * v;

        return result;
    }

    public Vector mult(Vector mul) {
        return mult(mul.getCoords());
    }

    public Vector mult(double[] a) {
        final double[] result = new double[dimension];

        for (int i = 0; i < dimension; ++i)
            for (int j = 0; j < dimension; ++j)
                result[i] += a[j] * data[i + j * dimension];

        return new Vector(result);
    }

    public Matrix mult(Matrix multi) {
        return mult(multi, new Matrix(dimension));
    }

    /**
     * Einfache Matrizen Multiplikation
     */
    public Matrix mult(Matrix multi, Matrix result) {
        final float[] temp = new float[dimension * dimension];
        final float[] mult = multi.data;

        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++) {
                int jd = j * dimension;
                int ijd = i + jd;
                for (int k = 0; k < dimension; k++)
                    temp[ijd] += data[i + k * dimension] * mult[k + jd];
            }

        result.setMat(temp);

        return result;
    }

    public Matrix add(Matrix sum) {
        return add(sum, new Matrix(dimension));
    }

    public Matrix add(Matrix sum, Matrix result) {
        final float[] res = result.data;
        final float[] su = sum.data;

        for (int i = 0; i < dimension; ++i)
            res[i] = data[i] + su[i];

        return result;
    }

    public Matrix sub(Matrix subt) {
        return sub(subt, new Matrix(dimension));
    }

    public Matrix sub(Matrix subt, Matrix result) {
        final float[] res = result.data;
        final float[] su = subt.data;

        for (int i = 0; i < dimension; ++i)
            res[i] = data[i] - su[i];

        return result;
    }

    public Matrix pow(int power) {
        Matrix res = new Matrix(power);
        res.loadIdentity();
        Matrix m;

        if (power < 0) {
            power *= -1;
            m = this.inverse();
        } else
            m = this.clone();

        while (power > 0) {
            if ((power & 1) == 1)
                res.mult(m, res);
            power /= 2;
            if (power == 0)
                break;
            m.mult(m, m);
        }

        return res;
    }

    public Matrix transpose() {
        return transpose(new Matrix(dimension));
    }

    /**
     * Transponiert die Matrix
     */
    public Matrix transpose(Matrix result) {
        final float[] f = new float[dimension * dimension];
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                f[i + j * dimension] = data[j + i * dimension];

        result.setMat(f);

        return result;
    }

    public Matrix adjunkt() {
        return adjunkt(new Matrix(dimension));
    }

    public Matrix adjunkt(Matrix result) {
        final float[] f = new float[dimension * dimension];
        for (int i = 0, n = 1; i < dimension; i++) {
            for (int j = 0; j < dimension; j++, n *= -1)
                f[j + i * dimension] = getMinor(i, j).getDeterminate() * n;
            if (dimension % 2 == 0)
                n *= -1;
        }

        result.setMat(f);
        result.transpose(result);

        return result;
    }

    public Matrix inverse() {
        return inverse(new Matrix(dimension));
    }

    /**
     * Ersetzt die Matrix mit ihrer Inversen
     */
    //TODO durch womoeglich schneller version ersetzten(unten auskommentierte)
    public Matrix inverse(Matrix result) {
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
        adjunkt(result);
        result.mult(1f / getDeterminate(), result);

        return result;
    }

    public float getDeterminate() {
        if (dimension > 1) {
            float det = 0;
            for (int j = 0, n = 1; j < dimension; j++, n *= -1)
                det += getMinor(0, j).getDeterminate() * data[j] * n;
            return det;
        } else
            return data[0];
    }

    /**
     * Der Matrix wird jeweils eine Spalte und Zeile gestrichen und die
     * resultierende Matrix zur�ckgegeben
     */
    public Matrix getMinor(int skipx, int skipy) {
        final int dim = dimension - 1;
        final Matrix ret = new Matrix(dim);
        float[] minor = ret.data;
        for (int i = 0; i < dimension - 1; i++) {
            int idim = i * dim;
            int x = i + (i >= skipx ? 1 : 0);
            int xdim = x * dimension;
            for (int j = 0; j < dimension - 1; j++) {
                int y = j + (j >= skipy ? 1 : 0);
                minor[j + idim] = data[y + xdim];
            }
        }
        return ret;
    }

    public final float[] getArray() {
        return data;
    }

    public FloatBuffer getFloatBuffer() {
        return buffer;
    }

    public Vector[] getRows() {
        final Vector[] rows = new Vector[dimension];
        for (int i = 0; i < dimension; ++i)
            rows[i] = getRow(i);
        return rows;
    }

    public Vector[] getColumns() {
        final Vector[] columns = new Vector[dimension];
        for (int i = 0; i < dimension; ++i)
            columns[i] = getColumn(i);
        return columns;
    }

    public Vector getRow(int index) {
        final Vector res = new Vector(dimension);
        double[] coor = res.getCoords();
        for (int i = 0; i < dimension; ++i)
            coor[i] = data[index + dimension * i];
        return res;
    }

    public Vector getColumn(int index) {
        final float[] coor = new float[dimension];
        System.arraycopy(data, dimension * index, coor, 0, dimension);
        return new Vector(coor);
    }

    public void setMat(float[] mat) {
        assert (mat.length == dimension * dimension) :
                "Die Matrize kann nur mit einem Array mit der selben Elementen anzahl gesetzt werden!";
        initArray(mat);
    }

    private void initArray(float[] mat) {
        assert mat.length >= data.length;
        System.arraycopy(mat, 0, data, 0, data.length);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < dimension; j++) {
            for (int i = 0; i < dimension; i++)
                buf.append(data[j + dimension * i] + '\t');
            buf.append('\n');
        }
        return buf.toString();
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public Matrix clone() {
        Matrix ret = new Matrix(dimension);
        ret.initArray(data.clone());
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Matrix other = (Matrix) obj;
        if (!Arrays.equals(this.data, other.data))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Arrays.hashCode(this.data);
        return hash;
    }

    //Hack
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        try {
            in.readFields();
            Class<Matrix> c = Matrix.class;
            c.getField("buffer").set(this,
                                     FloatBuffer.wrap(data));
            c.getField("dimension").set(this,
                                        (int) Math.sqrt(data.length));
        } catch (IOException | ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
