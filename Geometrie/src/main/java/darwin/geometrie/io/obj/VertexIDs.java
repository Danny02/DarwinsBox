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
package darwin.geometrie.io.obj;

/**
 *
 * @author Daniel Heinrich
 */
public class VertexIDs{
    int position, normal, texcoord;

    public VertexIDs(int[] vd) {
        position = vd[0];
        texcoord = vd[1];
        normal = vd[2];
    }

    public VertexIDs(int position, int texcoord, int normal) {
        this.position = position;
        this.normal = normal;
        this.texcoord = texcoord;
    }

    public int getNormal() {
        return normal;
    }

    public int getPosition() {
        return position;
    }

    public int getTexcoord() {
        return texcoord;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VertexIDs other = (VertexIDs) obj;
        if (this.position != other.position) {
            return false;
        }
        if (this.normal != other.normal) {
            return false;
        }
        if (this.texcoord != other.texcoord) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.position;
        hash = 89 * hash + this.normal;
        hash = 89 * hash + this.texcoord;
        return hash;
    }

}
