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
package darwin.renderer.opengl;

import darwin.geometrie.data.DataType;
import darwin.geometrie.data.VectorType;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class GLSLType implements VectorType
{

    public static final GLSLType FLOAT = new GLSLType(DataType.FLOAT, 1, false),
            VEC2 = new GLSLType(DataType.FLOAT, 2, false),
            VEC3 = new GLSLType(DataType.FLOAT, 3, false),
            VEC4 = new GLSLType(DataType.FLOAT, 4, false),
            MAT3 = new GLSLType(DataType.FLOAT, 9, true),
            MAT4 = new GLSLType(DataType.FLOAT, 16, true);
    private final int size;
    private final DataType datatype;
    private final boolean ismatrix;

    private GLSLType(DataType type, int size, boolean ismatrix)
    {
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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if(obj == this)
        	return true;
        if (!(obj instanceof VectorType)) {
            return false;
        }
        final VectorType other = (VectorType) obj;
        if (this.size != other.getElementCount()) {
            return false;
        }
        if (this.datatype != other.getDataType()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 47 * hash + this.size;
        hash = 47 * hash + (this.datatype != null ? this.datatype.hashCode() : 0);
        return hash;
    }
}
