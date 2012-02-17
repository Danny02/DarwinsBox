/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.packed;

import darwin.util.math.container.*;


/**
 * H�lt mehrere RenderModell Objekte
 * @author Daniel Heinrich
 */
public class RenderObjekt
{
    private RenderModel[] models;
    private final TransformationContainer transf;

    public RenderObjekt(TransformationContainer t) {
        transf = t;
    }

    public RenderObjekt(TransformationContainer t,
                        RenderModel ... models) {
        this(t);
        this.models = models;
    }

    public TransformationContainer getTransf() {
        return transf;
    }

    public void setModels(RenderModel[] models) {
        this.models = models;
    }

    public RenderModel[] getModels() {
        return models;
    }
}
