/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package darwin.util.math.container;

import darwin.util.math.base.*;

/**
 *
 * @author dheinrich
 */
public class SimplePhysic extends SimpleTransformation implements PhysicContainer{

    private final float radius, mass;
    private Vec3 vel;

    public SimplePhysic(float radius, float mass) {
        this(radius, mass, new Vec3());
    }

    public SimplePhysic(float radius, float mass, Vec3 v) {
        this.radius = radius;
        this.mass = mass;
        vel = v;
    }

    @Override
    public Vec3 getVelocity() {
        return vel;
    }

    @Override
    public void setVelocity(Vec3 newvel) {
        vel = newvel;
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
