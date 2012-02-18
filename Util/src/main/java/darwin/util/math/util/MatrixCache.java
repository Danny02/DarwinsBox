/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.util;

import java.util.Collection;
import java.util.LinkedList;

import darwin.util.math.base.Matrix;
import darwin.util.math.base.Vec3;
import darwin.util.math.composits.*;

import static darwin.util.math.util.MatType.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO add camera matrix
public class MatrixCache {

    private ModelMatrix model;
    //TODO testen ob das überhaupt was bringt
    private int mhash;
    private ViewMatrix view;
    private final ProjectionMatrix projektion;
    private Vec3 light = new Vec3();
    private ShadowUtil sutil;
    private Matrix vn, n, vm, pv, pvi, pvm, s;
    private final Collection<GenListener<MatrixEvent>> listener;
    //TODO boeser boeser schneller umschalter für schatten umbedigt besser loesen
    private boolean normal = true;

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public MatrixCache()
    {
        this(new ProjectionMatrix());
    }   

    public MatrixCache(ProjectionMatrix pro) {
        view = new ViewMatrix();
        view.loadIdentity();
        projektion = pro;
        listener = new LinkedList<>();

        AABB scene = new AABB(new Vec3(), new Vec3());
        sutil = new ShadowUtil(scene);
        addListener(sutil);
        sutil.changeOccured(new MatrixEvent(this, VIEW));
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

    public void setLight(Vec3 light) {
        this.light = light;
        fireChange(LIGHT);
    }

    public void setSceneAABB(AABB scene) {
        removeListener(sutil);
        sutil = new ShadowUtil(scene);
        addListener(sutil);
        sutil.changeOccured(new MatrixEvent(this, VIEW));
    }

    public Matrix getShadowProjection() {
        if (s == null) {
            s = sutil.calcShadowProjection(light);
        }
        return s;
    }

    public ProjectionMatrix getProjektion() {
        return projektion;
    }

    public Matrix getModelViewProjection() {
        if (pvm == null) {
            pvm = getViewProjection().mult(model);
        }
        return pvm;
    }

    public Matrix getViewProjection() {
        if (pv == null) {
            pv = projektion.mult(view);
        }
        return normal ? pv : getShadowProjection();
    }

    public Matrix getViewProjectionInverse() {
        if (pvi == null) {
            pvi = getViewProjection().inverse();
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

    public Matrix getModelView() {
        if (vm == null) {
            vm = view.mult(model);
        }
        return vm;
    }

    public Matrix getNormalView() {
        if (vn == null) {
            Matrix v = view.getMinor(3, 3);
            vn = v.mult(getNormal());
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
