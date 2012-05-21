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
package darwin.renderer.geometrie.packed;

import darwin.util.math.util.MatrixCache;

/** @author Daniel Heinrich <DannyNullZwo@gmail.com>
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
        if(obj == this)
        	return true;
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
