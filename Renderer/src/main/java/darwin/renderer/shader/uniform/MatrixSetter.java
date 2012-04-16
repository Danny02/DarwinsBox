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
package darwin.renderer.shader.uniform;

import java.util.Collection;
import java.util.LinkedList;

import darwin.renderer.shader.ShaderUniform;
import darwin.util.math.util.*;

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
        String matType = uni.getElement().getBezeichnung().substring(4);
        switch (matType) {
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
