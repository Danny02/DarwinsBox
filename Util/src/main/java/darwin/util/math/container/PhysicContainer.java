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

import darwin.util.math.base.Vec3;

/**
 *
 * @author dheinrich
 */
public interface PhysicContainer extends TransformationContainer{
    /**
     * @return
     * velocity vector in world space
     */
    public Vec3 getVelocity();

    /**
     * sets
     * @param newvel
     */
    public void setVelocity(Vec3 newvel);

//    public Vec3f getRotVelocity();
//    public void setRotVelocity(Vec3f newvel);

    /**
     * @return
     * physical radius of object
     */
    public float getRadius();

    /**
     * @return
     * weigth/mass of object
     */
    public float getMass();
}
