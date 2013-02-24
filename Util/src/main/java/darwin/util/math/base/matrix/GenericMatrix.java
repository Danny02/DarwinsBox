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

/**
 * Quadratische GenericMatrix beliebiger Dimension
 * <p/>
 * @author Daniel Heinrich
 */
public class GenericMatrix extends Matrix<GenericMatrix>
{
    private final float[] data;
    private transient final FloatBuffer buffer;
    private transient final int dimension;

    public GenericMatrix(int dim)
    {
        dimension = dim;
        data = new float[dim * dim];
        buffer = FloatBuffer.wrap(data);
    }

    @Override
    public final float[] getArray()
    {
        return data;
    }

    @Override
    public FloatBuffer getFloatBuffer()
    {
        return buffer;
    }

    @Override
    public void setMat(float[] mat)
    {
        assert (mat.length == dimension * dimension) :
                "Die Matrize kann nur mit einem Array mit der selben Elementen anzahl gesetzt werden!";
        System.arraycopy(mat, 0, data, 0, data.length);
    }

    @Override
    public GenericMatrix clone()
    {
        GenericMatrix a = super.clone();
        a.setMat(getArray().clone());
        return a;
    }


    @Override
    public int getDimension()
    {
        return dimension;
    }
}
