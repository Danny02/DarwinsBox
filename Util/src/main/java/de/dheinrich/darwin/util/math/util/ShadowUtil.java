/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.util.math.util;

import de.dheinrich.darwin.util.math.base.*;
import de.dheinrich.darwin.util.math.composits.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShadowUtil implements GenListener<MatrixEvent>
{
    private static final Vector[] pcorners = new Vector[8];
    private static final Matrix4 bias = new Matrix4();
    static {
        for (int i = 0; i < 8; i++) {
            int ii = i % 4;
            pcorners[i] = new Vector(
                    (ii == 1 || ii == 2) ? 1 : -1,
                    ii < 2 ? -1 : 1,
                    i < 4 ? -1 : 1,
                    1.);
        }

        bias.loadIdentity();
        bias.scale(0.5);
    }
    private final Vector[] scene;
    private Vector[] uncor = new Vector[8];

    public ShadowUtil(AABB scene) {
        assert scene != null;
        this.scene = scene.getCorners();
    }
//TODO auf Szenen ausmasse und sonnen rotation optimieren
    public Matrix calcShadowProjection(Vec3 dir) {
        Matrix4 light = new Matrix4();
        light.loadIdentity();

        Quaternion q = new Quaternion();
        q.mapVector(new Vec3(0, 0, 1), dir);
        light.rotate(q);
//        light.rotateEuler(0, 0, -45);

        light.inverse(light);

        Vector[] view = new Vector[8];
        Vector[] lscene = new Vector[8];
        for (int i = 0; i < 8; i++){
            view[i] = light.mult(uncor[i]);
            lscene[i] = light.mult(scene[i]);
        }

        Vector vmax = view[0].max(view[1]);
        Vector vmin = view[0].min(view[1]);
        Vector smax = lscene[0].max(lscene[1]);
        Vector smin = lscene[0].min(lscene[1]);

        for (int i = 2; i < 8; ++i) {
            vmax.max(view[i], vmax);
            vmin.min(view[i], vmin);
            smax.max(lscene[i], smax);
            smin.min(lscene[i], smin);
        }

        vmax.min(smax, vmax);
        vmin.max(smin, vmin);

        double[] mi = vmin.getCoords();
        double[] ma = vmax.getCoords();
        ProjectionMatrix lightp = new ProjectionMatrix();
        lightp.ortho(mi[0], mi[1], mi[2], ma[0], ma[1], ma[2]);
//        System.out.println("width:" + (ma[0] - mi[0])
//                + " height:" + (ma[1] - mi[1])
//                + " depth:" + (ma[2] - mi[2]));
        lightp.mult(light, lightp);
//        lightp.mult(bias, lightp);

        return lightp;
    }

    @Override
    public void changeOccured(MatrixEvent t) {
        switch (t.getType()) {
            case PROJECTION:
            case VIEW:
                Matrix inv = t.getSource().getViewProjectionInverse();
                unproject(inv, uncor, pcorners);
        }
    }

    private void unproject(Matrix inverse, Vector[] dst, Vector[] src) {
        for (int i = 0; i < 8; i++)
            dst[i] = inverse.mult(src[i]);

        double d = dst[0].getCoords()[3];
        assert d != 0;
        d = 1. / d;

        for (int i = 0; i < 8; i++)
            dst[i].mult(d, dst[i]);
    }
}
