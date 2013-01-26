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

import java.util.*;

import darwin.util.math.base.matrix.*;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.*;

import static darwin.util.math.util.MatType.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO add camera matrix
public class MatrixCache {

    private ModelMatrix model = new ModelMatrix();
    //TODO testen ob das überhaupt was bringt
    private int mhash;
    private ViewMatrix view;
    private final ProjectionMatrix projektion;
    private ImmutableVector<Vector3> light = new Vector3();
//    private ShadowUtil sutil;
     private Matrix4 vm, pv, pvi, pvm, s;
     private Matrix vn, n;
    private final Collection<GenListener<MatrixEvent>> listener;
    //TODO boeser boeser schneller umschalter für schatten umbedigt besser loesen
//    private boolean normal = true;

//    public void setNormal(boolean normal) {
//        this.normal = normal;
//    }
    public MatrixCache() {
        this(new ProjectionMatrix().loadIdentity());
    }

    public MatrixCache(ProjectionMatrix pro) {
        view = new ViewMatrix();
        view.loadIdentity();
        model.loadIdentity();
        projektion = pro;
        listener = new LinkedList<>();

        AABB scene = new AABB(new Vector3(), new Vector3());
//        sutil = new ShadowUtil(scene);
//        addListener(sutil);
//        sutil.changeOccured(new MatrixEvent(this, VIEW));
    }

    @SuppressWarnings("fallthrough")
    public void fireChange(MatType e) {
        switch (e) {
            case MODEL:
                vm = pvm = null;
                vn = n = null;
                break;
            case LIGHT:
                s = null;
                break;
            case VIEW:
                vn = null;
                vm = null; //no break intended
            case PROJECTION:
                pvm = pv = pvi = s = null;
        }
        fireEvent(e);
    }

    public final void addListener(GenListener<MatrixEvent> l) {
        listener.add(l);
    }

    public void removeListener(GenListener<MatrixEvent> l) {
        listener.remove(l);
    }

    private void fireEvent(MatType ev) {
        for (GenListener<MatrixEvent> cl : listener) {
            cl.changeOccured(new MatrixEvent(this, ev));
        }
    }

    public void setModel(ModelMatrix model) {
        if (mhash != model.hashCode()) {
            this.model = model;
            fireChange(MODEL);
        }
    }

    public ModelMatrix getModel() {
        return model;
    }

    public void setLight(ImmutableVector<Vector3> light) {
        this.light = light.clone();
        fireChange(LIGHT);
    }

//    public void setSceneAABB(AABB scene) {
//        removeListener(sutil);
//        sutil = new ShadowUtil(scene);
//        addListener(sutil);
//        sutil.changeOccured(new MatrixEvent(this, VIEW));
//    }
//
//    public Matrix4 getShadowProjection() {
//        if (s == null) {
//            s = sutil.calcShadowProjection(light);
//        }
//        return s;
//    }
    public ProjectionMatrix getProjektion() {
        return projektion;
    }

    public Matrix4 getModelViewProjection() {
        if (pvm == null) {
            pvm = getViewProjection().clone().mult(model);
        }
        return pvm;
    }

    public Matrix4 getViewProjection() {
        if (pv == null) {
            pv = projektion.clone().mult(view);
        }
        return pv;
//        return normal ? pv : getShadowProjection();
    }

    public Matrix4 getViewProjectionInverse() {
        if (pvi == null) {
            pvi = getViewProjection().clone().inverse();
        }
        return pvi;
    }

    public ViewMatrix getView() {
        return view;
    }

    public void setView(ViewMatrix view) {
        this.view = view;
        fireChange(VIEW);
    }

    public Matrix4 getModelView() {
        if (vm == null) {
            vm = view.clone().mult(model);
        }
        return vm;
    }

    public Matrix getNormalView() {
        if (vn == null) {
            vn = view.getMinor(3, 3).mult(getNormal());
        }
        return vn;
    }

    public Matrix getNormal() {
        if (n == null) {
            n = model.getNormalMatrix();
        }
        return n;
    }
}
