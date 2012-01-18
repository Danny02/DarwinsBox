/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.unpacked;

import de.dheinrich.darwin.renderer.shader.uniform.GameMaterial;
import java.io.Serializable;

/**
 * H�lt ein Mesh und das dazugeh�rige Material
 * @author Daniel Heinrich
 */
public class Model implements Serializable
{
    private Mesh mesh;
    private GameMaterial mat;

    public Model(Mesh mesh, GameMaterial mat) {
        this.mesh = mesh;
        this.mat = mat;
    }

    public GameMaterial getMat() {
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
