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

import java.io.Serializable;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class DataAttribut implements Serializable
{

    private static final long serialVersionUID = -617738618017984776L;
    public final int stride, offset;
//    private final Element element;

    public DataAttribut(int stride, int offset)
    {
        this.stride = stride;
        this.offset = offset;
//        this.element = element;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataAttribut other = (DataAttribut) obj;
        if (this.stride != other.stride) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 13 * hash + this.stride;
        hash = 13 * hash + this.offset;
        return hash;
    }
}
