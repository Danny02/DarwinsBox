/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.packed;

import de.dheinrich.darwin.util.math.util.MatrixCache;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class RenderWrapper implements Renderable
{
    private final RenderObjekt object;
    private final MatrixCache smatrices;

    public RenderWrapper(RenderObjekt object, MatrixCache smatrices) {
        this.object = object;
        this.smatrices = smatrices;
    }

    @Override
    public void render() {
        RenderModel[] rms = object.getModels();
        if (rms != null) {
            smatrices.setModel(object.getTransf().getModelMatrix());
            for (RenderModel rm : rms)
                rm.render();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RenderWrapper other = (RenderWrapper) obj;
        if (this.object != other.object && (this.object == null || !this.object.equals(other.object)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.object != null ? this.object.hashCode() : 0);
        return hash;
    }

}
