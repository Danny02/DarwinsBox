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
package darwin.geometrie.unpacked;

import java.util.Arrays;

import darwin.geometrie.data.VertexBuffer;

/**
 * H�lt VertexBuffern Indicies und Primitiv Typ eines Models
 * @author Daniel Heinrich
 */
public class Mesh
{
    private int[] indicies;     // Primitve indicies
    private VertexBuffer vertices;  // Vertex Data
    private int primitiv_typ;

    public Mesh(int[] indicies, VertexBuffer vb, int primitiv_typ) {
        this.indicies = indicies;
        vertices = vb;
        this.primitiv_typ = primitiv_typ;
    }

    public int getVertexCount() {
        return getVertices().getVcount();
    }

    public int getIndexCount() {
        return getIndicies().length;
    }

    public int[] getIndicies() {
        return indicies;
    }

    public int getPrimitiv_typ() {
        return primitiv_typ;
    }

    public VertexBuffer getVertices() {
        return vertices;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash =
        19 * hash + (this.indicies != null ? Arrays.hashCode(indicies) : 0);
        hash =
        19 * hash + (this.vertices != null ? this.vertices.hashCode() : 0);
        hash = 19 * hash + this.primitiv_typ;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Mesh other = (Mesh) obj;
        if (!Arrays.equals(this.indicies, other.indicies))
            return false;
        if (this.vertices != other.vertices && (this.vertices == null || !this.vertices.
                equals(other.vertices)))
            return false;
        if (this.primitiv_typ != other.primitiv_typ)
            return false;
        return true;
    }
}
