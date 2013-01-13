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
package darwin.util.math.util;

import darwin.util.math.base.Quaternion;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ShadowUtil implements GenListener<MatrixEvent>
{
    private static final ImmutableVector<Vector3>[] pcorners = new ImmutableVector[8];
    private static final Matrix4 bias = new Matrix4();

    static {
        for (int i = 0; i < 8; i++) {
            int ii = i % 4;
            pcorners[i] = new Vector3(
                    (ii == 1 || ii == 2) ? 1 : -1,
                    ii < 2 ? -1 : 1,
                    i < 4 ? -1 : 1);
        }

        bias.loadIdentity();
        bias.scale(0.5f);
    }
    private final ImmutableVector<Vector3>[] scene;
    private ImmutableVector<Vector3>[] uncor = new ImmutableVector[8];

    public ShadowUtil(AABB scene)
    {
        assert scene != null;
        this.scene = scene.getCorners();
    }
//TODO auf Szenen ausmasse und sonnen rotation optimieren

    public Matrix4 calcShadowProjection(ImmutableVector<Vector3> dir)
    {
        Matrix4 light = new Matrix4();
        light.loadIdentity();

        Quaternion q = new Quaternion();
        q.mapVector(new Vector3(0, 0, 1), dir);
        light.rotate(q);
//        light.rotateEuler(0, 0, -45);

        light.inverse();

        Vector3[] view = new Vector3[8];
        Vector3[] lscene = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            view[i] = light.fastMult(uncor[i].clone());
            lscene[i] = light.fastMult(scene[i].clone());
        }

        Vector3 vmax = view[0].clone().max(view[1]);
        Vector3 vmin = view[0].clone().min(view[1]);
        Vector3 smax = lscene[0].clone().max(lscene[1]);
        Vector3 smin = lscene[0].clone().min(lscene[1]);

        for (int i = 2; i < 8; ++i) {
            vmax.max(view[i]);
            vmin.min(view[i]);
            smax.max(lscene[i]);
            smin.min(lscene[i]);
        }

        vmax.min(smax);
        vmin.max(smin);

        float[] mi = vmin.getCoords();
        float[] ma = vmax.getCoords();
        ProjectionMatrix lightp = new ProjectionMatrix();
        lightp.ortho(mi[0], mi[1], mi[2], ma[0], ma[1], ma[2]);
//        System.out.println("width:" + (ma[0] - mi[0])
//                + " height:" + (ma[1] - mi[1])
//                + " depth:" + (ma[2] - mi[2]));
        lightp.mult(light);
//        lightp.mult(bias, lightp);

        return lightp;
    }

    @Override
    public void changeOccured(MatrixEvent t)
    {
        switch (t.getType()) {
            case PROJECTION:
            case VIEW:
                Matrix4 inv = t.getSource().getViewProjectionInverse();
                unproject(inv, uncor, pcorners);
        }
    }

    private void unproject(Matrix4 inverse, ImmutableVector<Vector3>[] dst,
                           ImmutableVector<Vector3>[] src)
    {
        //TODO an neue vector klassen anpassen
        throw new UnsupportedOperationException();
//        for (int i = 0; i < 8; i++) {
//            dst[i] = inverse.mult(src[i].clone());
//        }
//
//        float d = dst[0].getCoords()[3];
//        assert d != 0;
//        d = 1f / d;
//
//        for (int i = 0; i < 8; i++) {
//            dst[i] = dst[i].clone().mul(d);
//        }
    }
}
