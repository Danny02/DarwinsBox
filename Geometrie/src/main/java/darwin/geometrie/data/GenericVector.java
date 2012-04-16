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
package darwin.geometrie.data;

/**
 *
 * @author daniel
 */
public class GenericVector implements VectorType
{

    private final DataType type;
    private final int elementCount;

    public GenericVector(DataType type, int elementCount)
    {
        this.type = type;
        this.elementCount = elementCount;
    }

    @Override
    public DataType getDataType()
    {
        return type;
    }

    @Override
    public int getElementCount()
    {
        return elementCount;
    }

    @Override
    public int getByteSize()
    {
        return type.getByteSize() * getElementCount();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VectorType)) {
            return false;
        }
        final VectorType other = (VectorType) obj;
        if (this.type != other.getDataType()) {
            return false;
        }
        if (this.elementCount != other.getElementCount()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 29 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 29 * hash + this.elementCount;
        return hash;
    }
}
