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
 * You should have received a clone of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.composits;

import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;

/**
 *
 * @author Daniel Heinrich
 */
public class ViewMatrix extends Matrix4 {

    private static final long serialVersionUID = -7461026157857152643L;

    public Vector3 getViewDirection() {
        float[] f = inverse().getColumn(2).getCoords();
        return new Vector3(f[0], f[1], f[2]);
    }

    @Override
    public ViewMatrix inverse() {
        super.inverse();
        return this;
    }

    @Override
    public ViewMatrix translate(ImmutableVector<Vector3> vec) {
        super.translate(vec);
        return this;
    }

    @Override
    public ViewMatrix translate(float x, float y, float z) {
        super.translate(x, y, z);
        return this;
    }

    /**
     * Implementation der aus GLUT bekannten lookAt funktion
     */
    public void lookAt(ImmutableVector<Vector3> eye,
                       ImmutableVector<Vector3> center,
                       ImmutableVector<Vector3> up) {
        Vector3 forward, side, up2;
        Matrix4 matrix = new Matrix4();

        forward = center.clone().sub(eye).normalize();
        side = forward.clone().cross(up).normalize();
        up2 = side.clone().cross(forward);

        float[] mat = matrix.getArray();

        mat[0] = side.getX();
        mat[1] = up2.getX();
        mat[2] = -forward.getX();
        mat[3] = 0.0f;
        mat[4] = side.getY();
        mat[5] = up2.getY();
        mat[6] = -forward.getY();
        mat[7] = 0.0f;
        mat[8] = side.getZ();
        mat[9] = up2.getZ();
        mat[10] = -forward.getZ();
        mat[11] = 0.0f;
        mat[12] = 0.0f;
        mat[13] = 0.0f;
        mat[14] = 0.0f;
        mat[15] = 1.0f;

        loadIdentity();
        mult(matrix);
        translate(eye);
    }

    @Override
    public ViewMatrix clone() {
        ViewMatrix m = new ViewMatrix();
        m.setMat(getArray().clone());
        return m;
    }
}
