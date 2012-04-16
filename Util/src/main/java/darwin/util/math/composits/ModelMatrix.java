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
package darwin.util.math.composits;

import darwin.util.math.base.*;

/**
 * 4x4 Matrix mit ModelView speziellen Funktionen
 * @author Daniel Heinrich
 */
public class ModelMatrix extends Matrix4
{
    private static final long serialVersionUID = -8114833379706856265L;
    private boolean homogeneous = true;

    public ModelMatrix() {
        loadIdentity();
    }

    @Override
    public ModelMatrix scale(Vec3 scale) {
        homogeneous = false;
        return (ModelMatrix) super.scale(scale);
    }

    @Override
    public ModelMatrix scale(double scalex, double scaley, double scalez) {
        homogeneous = false;
        return (ModelMatrix) super.scale(scalex, scaley, scalez);
    }

    /**
     * @return matrix which transforms normal vectors correctly to the modelmatrix transformation
     */
    public Matrix getNormalMatrix(){
        Matrix m = getMinor(3, 3);
        if(!homogeneous)
            m.transpose(m).inverse(m);
        return m;
    }

    /**
     * @return if this matrix is homogeneous
     */
    public boolean isHomogeneous() {
        return homogeneous;
    }

    public void setHomogeneous(boolean homogeneous) {
        this.homogeneous = homogeneous;
    }
}
