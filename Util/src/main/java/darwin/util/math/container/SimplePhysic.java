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

import darwin.util.math.base.vector.*;


/**
 *
 * @author dheinrich
 */
public class SimplePhysic extends SimpleTransformation implements PhysicContainer{

    private final float radius, mass;
    private ImmutableVector<Vector3> vel;

    public SimplePhysic(float radius, float mass) {
        this(radius, mass, new Vector3());
    }

    public SimplePhysic(float radius, float mass, ImmutableVector<Vector3> v) {
        this.radius = radius;
        this.mass = mass;
        vel = v.copy();
    }

    @Override
    public ImmutableVector<Vector3> getVelocity() {
        return vel;
    }

    @Override
    public void setVelocity(ImmutableVector<Vector3> newvel) {
        vel = newvel.copy();
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public float getMass() {
        return mass;
    }

}
