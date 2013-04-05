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

import darwin.util.math.base.matrix.Matrix4;

import static java.lang.Math.PI;
import static java.lang.Math.tan;

/**
 * 4x4 Matrix mit Projektion Matrix typischen Funktionen
 *
 * @author Daniel Heinrich
 */
public class ProjectionMatrix extends Matrix4 {

    private static final long serialVersionUID = -6583528848204388790L;

    public void perspective(double fovy, double aspect, double znear,
                            double zfar) {
        double top = tan(fovy * PI / 360.) * znear;
        double bottom = -top;
        double right = aspect * top;
        double left = -right;
        frustum(left, right, bottom, top, znear, zfar);
    }

    /**
     * implementierung der alten gl.frustum(...) Funktion
     */
    public void frustum(double left, double right, double bottom, double top,
                        double nearVal, double farVal) {
        assert nearVal > 0.0f && farVal > 0.0f :
                "zNear and zFar must be positive, and zNear>0";
        assert left != right && top != bottom :
                "top,bottom and left,right must not be equal";

        loadIdentity();
        float[] mat = getArray();

        // Frustum matrix:
        //  2*zNear/dx   0          A  0
        //  0            2*zNear/dy B  0
        //  0            0          C  D
        //  0            0         -1  0

        float zNear2 = (float) (2.0 * nearVal);
        float dx = (float) (right - left);
        float dy = (float) (top - bottom);
        float dz = (float) (farVal - nearVal);
        float A = (float) ((right + left) / dx);
        float B = (float) ((top + bottom) / dy);
        float C = (float) (-1.0f * (farVal + nearVal) / dz);
        float D = (float) (-2.0f * (farVal * nearVal) / dz);

        mat[0] = zNear2 / dx;
        mat[5] = zNear2 / dy;
        mat[10] = C;

        mat[8] = A;
        mat[9] = B;

        mat[14] = D;
        mat[11] = -1;
        mat[15] = 0;
    }

    public void ortho(double left, double bottom, double near,
                      double rigth, double top, double far) {
        float rl = (float) (rigth - left);
        float tb = (float) (top - bottom);
        float fn = (float) (far - near);

        loadIdentity();
        float[] mat = getArray();
        mat[0] = 2f / rl;
        mat[5] = 2f / tb;
        mat[10] = 2f / fn;
        mat[12] = (float) (-(rigth + left) / rl);
        mat[13] = (float) (-(top + bottom) / tb);
        mat[14] = (float) (-(far + near) / fn);
        mat[15] = 1f;
    }

    @Override
    public ProjectionMatrix loadIdentity() {
        return (ProjectionMatrix) super.loadIdentity();
    }
}
