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
import java.util.LinkedList;
import java.util.List;

import darwin.util.math.base.*;
import darwin.util.math.composits.ModelMatrix;

/**
 *
 * @author dheinrich
 */
public final class TransformationStack implements TransformationContainer
{
    private static final long serialVersionUID = 6968947336110621695L;
    private List<ModelMatrix> transform;
    private int listsize = 0;
    private long matrizenhash = 0;
    private ModelMatrix packed, main;

    public TransformationStack() {
        packed = new ModelMatrix();
        transform = new LinkedList<>();
        main = addTLayer2Beginning();
    }

    public ModelMatrix getMainMatrix() {
        return main;
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
    public Vec3 getPosition() {
        return main.getTranslation();
    }

    @Override
    public void setPosition(Vec3 newpos) {
        newpos.sub(getPosition(), newpos);
        main.worldTranslate(newpos);
    }

    @Override
    public void shiftWorldPosition(Vec3 delta) {
        main.worldTranslate(delta);
    }

    @Override
    public void setWorldPosition(Vec3 pos) {
        main.setWorldTranslate(pos);
    }

    @Override
    public void shiftRelativePosition(Vec3 delta) {
        main.translate(delta);
    }

    @Override
    public void rotateEuler(Vec3 delta) {
        main.rotateEuler(delta);
    }

    @Override
    public void rotate(Matrix4 rotmat) {
        main.rotateEuler(rotmat.getEularAngles());
    }

    @Override
    public void rotate(Quaternion rotation) {
        main.rotate(rotation);
    }

    @Override
    public Quaternion getRotation() {
        return main.getRotation();
    }

    @Override
    public void setRotation(Quaternion rot) {
        setRotation(rot.getRotationMatrix());
    }

    @Override
    public void setRotation(Matrix4 rot) {
        main.setRotation(rot);
    }

    @Override
    public void scale(Vec3 delta) {
        main.scale(delta);
    }

    @Override
    public ModelMatrix getModelMatrix() {
        if (listsize == transform.size()) {
            long hash = 0;
            for (Matrix4 m : transform)
                hash = 23 * hash + m.hashCode();
            if (hash == matrizenhash)
                return packed;
            matrizenhash = hash;
        }
        listsize = transform.size();

        packed.loadIdentity();
        boolean h = true;
        for (ModelMatrix m : transform) {
            packed.mult(m, packed);
            h &= m.isHomogeneous();
        }
        packed.setHomogeneous(h);

        return packed;
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        transform = (List<ModelMatrix>) in.readObject();
        main = (ModelMatrix) in.readObject();
        packed = new ModelMatrix();
        listsize = 0;
        matrizenhash = 0;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(transform);
        out.writeObject(main);
    }

    @Override
    public void reset() {
        main.loadIdentity();
        matrizenhash = 0;
    }

    @Override
    public void scale(float delta) {
        main.scale(delta);
    }
}
