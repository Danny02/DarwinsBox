/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.renderer.opengl;

import darwin.geometrie.data.DataType;
import darwin.geometrie.data.VectorType;


/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public enum GLSLType implements VectorType{
    FLOAT(DataType.FLOAT, 1, false),
    VEC2(DataType.FLOAT, 2, false),
    VEC3(DataType.FLOAT, 3, false),
    VEC4(DataType.FLOAT, 4, false),
    MAT3(DataType.FLOAT, 9, true),
    MAT4(DataType.FLOAT, 16, true);

    private final int size;
    private final DataType datatype;
    private final boolean ismatrix;

    private GLSLType(DataType type, int size, boolean ismatrix) {
        this.datatype = type;
        this.size = size;
        this.ismatrix = ismatrix;
    }

    @Override
    public DataType getDataType()
    {
        return datatype;
    }

    @Override
    public int getElementCount()
    {
        return size;
    }

    @Override
    public int getByteSize()
    {
        return datatype.getByteSize() * size;
    }

    public boolean isMatrix()
    {
        return ismatrix;
    }
}
