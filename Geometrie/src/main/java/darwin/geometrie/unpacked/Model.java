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


/**
 * H�lt ein Mesh und das dazugeh�rige Material
 * @author Daniel Heinrich
 */
public class Model
{
    private Mesh mesh;
    private Material mat;

    public Model(Mesh mesh, Material mat) {
        this.mesh = mesh;
        this.mat = mat;
    }

    public Material getMat() {
        return mat;
    }

    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Model other = (Model) obj;
        if (this.mesh != other.mesh && (this.mesh == null || !this.mesh.equals(other.mesh)))
            return false;
        if (this.mat != other.mat && (this.mat == null || !this.mat.equals(other.mat)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.mesh != null ? this.mesh.hashCode() : 0);
        hash = 83 * hash + (this.mat != null ? this.mat.hashCode() : 0);
        return hash;
    }
}
