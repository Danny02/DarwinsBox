/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.unpacked;

import de.dheinrich.darwin.renderer.geometrie.data.*;
import java.io.*;
import java.util.*;

/**
 * H�lt VertexBuffern Indicies und Primitiv Typ eines Models
 * @author Daniel Heinrich
 */
public class Mesh implements Serializable
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
