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

import darwin.util.math.container.TransformationContainer;


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
