/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader.uniform;

import darwin.renderer.shader.*;
import darwin.util.math.util.*;
import java.util.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class MatrixSetter implements UniformSetter,
        GenListener<MatrixEvent> {

    private boolean m, v, p, l;
    private MatrixCache matricen;
    private final Collection<UniformSetter> setter = new LinkedList<>();

    public MatrixSetter() {
        m = v = p = true;
    }

    public void addUniform(final ShaderUniform uni) {
        switch (uni.getElement().bezeichnung) {
            case "M":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (m) {
                            uni.setData(matricen.getModel().getFloatBuffer());
                        }
                    }
                });
                break;
            case "MV":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (m || v) {
                            uni.setData(matricen.getModelView().getFloatBuffer());
                        }
                    }
                });
                break;
            case "MVP":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (m || v || p) {
                            uni.setData(matricen.getModelViewProjection().
                                    getFloatBuffer());
                        }
                    }
                });
                break;
            case "N":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (m) {
                            uni.setData(
                                    matricen.getNormal().getFloatBuffer());
                        }
                    }
                });
                break;
            case "NV":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (m || v) {
                            uni.setData(matricen.getNormalView().
                                    getFloatBuffer());
                        }
                    }
                });
                break;
            case "P":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (p) {
                            uni.setData(matricen.getProjektion().
                                    getFloatBuffer());
                        }
                    }
                });
                break;
            case "V":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (v) {
                            uni.setData(matricen.getView().getFloatBuffer());
                        }
                    }
                });
                break;
            case "VP":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (v || p) {
                            uni.setData(matricen.getViewProjection().
                                    getFloatBuffer());
                        }
                    }
                });
                break;
            case "S":
                setter.add(new UniformSetter() {

                    @Override
                    public void set() {
                        if (v || p || l) {
                            uni.setData(matricen.getShadowProjection().
                                    getFloatBuffer());
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void set() {
        if (matricen != null) {
            for (UniformSetter us : setter) {
                us.set();
            }
        }
        m = v = p = l = false;
    }

    @Override
    public void changeOccured(MatrixEvent t) {
        matricen = t.getSource();
        switch (t.getType()) {
            case MODEL:
                m = true;
                break;
            case PROJECTION:
                p = true;
                break;
            case VIEW:
                v = true;
                break;
            case LIGHT:
                l = true;
        }
    }

    public boolean isM() {
        return m;
    }

    public boolean isP() {
        return p;
    }

    public boolean isV() {
        return v;
    }
}
