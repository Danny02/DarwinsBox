/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.container;

import darwin.util.math.base.*;
import darwin.util.math.composits.ModelMatrix;

/**
 *
 * @author dheinrich
 */
public class SimpleTransformation implements TransformationContainer
{
    private ModelMatrix matrix;

    public SimpleTransformation() {
        matrix = new ModelMatrix();
    }

    protected SimpleTransformation(ModelMatrix m) {
        matrix = m;
    }

    @Override
    public Vec3 getPosition() {
        return matrix.getTranslation();
    }

    @Override
    public void setPosition(Vec3 newpos) {
        newpos.sub(getPosition(), newpos);
        matrix.worldTranslate(newpos);
    }

    @Override
    public void shiftWorldPosition(Vec3 delta) {
        matrix.worldTranslate(delta);
    }

    @Override
    public void shiftRelativePosition(Vec3 delta) {
        matrix.translate(delta);
    }

    @Override
    public void rotateEuler(Vec3 delta) {
        matrix.rotateEuler(delta);
    }

    @Override
    public void rotate(Matrix4 rotmat) {
        matrix.rotateEuler(rotmat.getEularAngles());
    }

    @Override
    public void scale(Vec3 delta) {
        matrix.scale(delta);
    }

    @Override
    public void scale(float delta) {
        matrix.scale(delta);
    }

    //TODO muss es hier ein clone geben?
    @Override
    public ModelMatrix getModelMatrix() {
        return matrix;
    }

    @Override
    public void setWorldPosition(Vec3 pos) {
        matrix.setWorldTranslate(pos);
    }

    protected ModelMatrix getMatrix() {
        return matrix;
    }

    @Override
    public void rotate(Quaternion rotation) {
        matrix.rotate(rotation);
    }

    @Override
    public Quaternion getRotation() {
        return matrix.getRotation();
    }

    @Override
    public void setRotation(Quaternion rot) {
        setRotation(rot.getRotationMatrix());
    }

    @Override
    public void setRotation(Matrix4 rot) {
        matrix.setRotation(rot);
    }

    @Override
    public void reset() {
        matrix.loadIdentity();
    }
}
