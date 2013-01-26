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

import darwin.geometrie.data.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class GLSLType implements VectorType {

    public static final GLSLType FLOAT = new GLSLType(DataType.FLOAT, 1),
            BOOL = new GLSLType(DataType.INT, 1),
            INT = new GLSLType(DataType.INT, 1),
            VEC2 = new GLSLType(DataType.FLOAT, 2),
            VEC3 = new GLSLType(DataType.FLOAT, 3),
            VEC4 = new GLSLType(DataType.FLOAT, 4),
            MAT3 = new GLSLType(DataType.FLOAT, 3, 3),
            MAT4 = new GLSLType(DataType.FLOAT, 4, 4);
    private final int size;
    private int rows, columns;
    private final DataType datatype;

    public GLSLType(DataType type, int size) {
        this.datatype = type;
        this.size = size;
        this.rows = 0;
        this.columns = 0;
    }

    public GLSLType(DataType type, int columns, int rows) {
        this(type, columns * rows);
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public DataType getDataType() {
        return datatype;
    }

    @Override
    public int getElementCount() {
        return size;
    }

    @Override
    public int getByteSize() {
        return datatype.getByteSize() * size;
    }

    public boolean isMatrix() {
        return rows > 0;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
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
    public int hashCode() {
        return GenericVector.hash(datatype, size);
    }
}
