/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.util.math.container;

import de.dheinrich.darwin.util.math.base.*;

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
