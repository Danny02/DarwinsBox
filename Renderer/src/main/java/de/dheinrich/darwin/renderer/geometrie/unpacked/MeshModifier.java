/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.renderer.geometrie.unpacked;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public abstract class MeshModifier {

    private MeshModifier modifier;

    public MeshModifier() {
    }

    public MeshModifier(MeshModifier modifier) {
        this.modifier = modifier;
    }

    public Mesh modifie(Mesh m){
        if(modifier == null)
            return mod(m);
        else
            return mod(modifier.modifie(m));
    }

    protected abstract Mesh mod(Mesh m);

}
