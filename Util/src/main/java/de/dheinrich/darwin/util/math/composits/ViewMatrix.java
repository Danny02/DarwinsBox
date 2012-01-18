/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.util.math.composits;

import de.dheinrich.darwin.util.math.base.Matrix4;
import de.dheinrich.darwin.util.math.base.Vec3;

/**
 *
 * @author Daniel Heinrich
 */
public class ViewMatrix extends Matrix4
{
    private static final long serialVersionUID = -7461026157857152643L;

    public Vec3 getViewDirection() {
        return new Vec3(inverse().getColumn(2));
    }

    /**
     * Implementation der aus GLUT bekannten lookAt funktion
     */
    public void lookAt(Vec3 eye, Vec3 center, Vec3 up) {
        Vec3 forward, side, up2;
        Matrix4 matrix = new Matrix4();

        forward = center.sub(eye);
        forward.normalize(forward);

        side = forward.cross(up);
        side.normalize(side);

        up2 = side.cross(forward);

        float[] mat = matrix.getArray();

        mat[0] = (float) side.getX();
        mat[1] = (float) up2.getX();
        mat[2] = (float) -forward.getX();
        mat[3] = 0.0f;
        mat[4] = (float) side.getY();
        mat[5] = (float) up2.getY();
        mat[6] = (float) -forward.getY();
        mat[7] = 0.0f;
        mat[8] = (float) side.getZ();
        mat[9] = (float) up2.getZ();
        mat[10] = (float) -forward.getZ();
        mat[11] = 0.0f;
        mat[12] = 0.0f;
        mat[13] = 0.0f;
        mat[14] = 0.0f;
        mat[15] = 1.0f;

        loadIdentity();
        mult(matrix, this);
        translate(eye);
    }

    @Override
    public ViewMatrix clone() {
        ViewMatrix m = new ViewMatrix();
        m.setMat(getArray().clone());
        return m;
    }
}
