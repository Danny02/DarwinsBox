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
package darwin.util.math.container;

import java.io.IOException;
import java.util.*;

import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.composits.ModelMatrix;

/**
 *
 * @author dheinrich
 */
public final class TransformationStack extends SimpleTransformation
{
    private static final long serialVersionUID = 6968947336110621695L;
    private List<ModelMatrix> transform;
    private int listsize = 0;
    private long matrizenhash = 0;
    private ModelMatrix packed;

    public TransformationStack() {
        packed = new ModelMatrix();
        transform = new ArrayList<>();
        matrix = addTLayer2Beginning();
    }

    public ModelMatrix getMainMatrix() {
        return matrix;
    }

    public ModelMatrix addTLayer2Beginning() {

        return addTLayer2Beginning(new ModelMatrix());
    }

    public ModelMatrix addTLayer2Beginning(ModelMatrix m) {
        transform.add(0, m);
        return m;
    }

    public ModelMatrix addTLayer2End() {
        return addTLayer2End(new ModelMatrix());
    }
    public ModelMatrix addTLayer2End(ModelMatrix m) {
        transform.add(m);
        return m;
    }

    @Override
    public ModelMatrix getModelMatrix() {
        if (listsize == transform.size()) {
            long hash = 0;
            for (Matrix4 m : transform) {
                hash = 23 * hash + m.hashCode();
            }
            if (hash == matrizenhash) {
                return packed;
            }
            matrizenhash = hash;
        }
        listsize = transform.size();

        packed.loadIdentity();
        boolean h = true;
        for (ModelMatrix m : transform) {
            packed.mult(m);
            h &= m.isHomogeneous();
        }
        packed.setHomogeneous(h);

        return packed;
    }

//    @SuppressWarnings("unchecked")
//    private void readObject(java.io.ObjectInputStream in)
//            throws IOException, ClassNotFoundException {
//        transform = (List<ModelMatrix>) in.readObject();
//        matrix = (ModelMatrix) in.readObject();
//        packed = new ModelMatrix();
//        listsize = 0;
//        matrizenhash = 0;
//    }
//
//    private void writeObject(java.io.ObjectOutputStream out)
//            throws IOException {
//        out.writeObject(transform);
//        out.writeObject(matrix);
//    }
}
