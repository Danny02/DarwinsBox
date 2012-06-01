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

import java.io.Serializable;

import darwin.util.math.base.*;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.ModelMatrix;

/**
 *
 * @author dheinrich
 */
public interface TransformationContainer extends Serializable{
    /**
     * @return
     * position-vector in world coordinates
     */
    public ImmutableVector<Vector3> getPosition();

    /**
     * sets world-position
     * @param newpos
     * new position-vector
     */
    public void setPosition(ImmutableVector<Vector3> newpos);

    /**
     * shifts position in world space
     * @param p
     * shift vector
     */
    public void shiftWorldPosition(ImmutableVector<Vector3> delta);

    /**
     * sets the position in world space
     * @param pos
     */
    public void setWorldPosition(ImmutableVector<Vector3> pos);

    /**
     * shifts position in object space
     * @param p
     * shift vector
     */
    public void shiftRelativePosition(ImmutableVector<Vector3> delta);

    /**
     * rotate Object
     * @param delta
     * vector which discribes rotation for each axis
     */
    public void rotateEuler(ImmutableVector<Vector3> delta);

    /**
     * rotate Object
     * @param delta
     * rotationmatrix which will be applyed;
     */
    public void rotate(Matrix4 rotmat);

    public void rotate(Quaternion rotation);

    public Quaternion getRotation();

    public void setRotation(Quaternion rot);

    public void setRotation(Matrix4 rot);

    /**
     * rotate Object
     * @param delta
     * vector which discribes scale factor for each axis
     */
    public void scale(ImmutableVector<Vector3> delta);
    public void scale(float delta);

    /**
     * @return ModelMatrix of Object.
     * altering this matrix don't effect the object transformation
     */
    public ModelMatrix getModelMatrix();

    public void reset();
}
