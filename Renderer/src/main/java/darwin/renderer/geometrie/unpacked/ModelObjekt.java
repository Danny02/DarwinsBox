/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.unpacked;

import java.io.Serializable;
import java.util.Arrays;

/**
 * H�lt eine Liste von Modellen
 * @author Daniel Heinrich
 */
public class ModelObjekt implements Serializable
{
    private Model[] models;

    public ModelObjekt(Model[] models) {
        this.models = models;
    }

    public Model[] getModels() {
        return models;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ModelObjekt other = (ModelObjekt) obj;
        if (!Arrays.deepEquals(this.models, other.models))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Arrays.deepHashCode(this.models);
        return hash;
    }
}
