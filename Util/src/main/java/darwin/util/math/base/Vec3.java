/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.base;

/**
 * Repr�sentation eines 3 Dimensionalen Vektors
 * @author Daniel Heinrich
 */
public class Vec3 extends Vector implements Cloneable
{
    private static final long serialVersionUID = -7042039518932804461L;

    public Vec3() {
        super(3);
    }

    //TODO
    //arrays größer als 3 können probleme bei rechnungen bei vectoren mit kleiner anzahl bewirken
    public Vec3(double value) {
        super(value, value, value);
    }

    public Vec3(double... coords) {
        super(coords[0], coords[1], coords[2]);
    }

    public Vec3(float[] coords) {
        super(coords[0], coords[1], coords[2]);
    }

    public Vec3(Vector v) {
        this(v.getCoords());
    }

    public Vec3 cross(Vec3 a) {
        return cross(a, new Vec3());
    }

    public Vec3 cross(Vec3 a, Vec3 result) {
        assert a != result && this != result;
        final double[] mult = getCoords();
        final double[] res = result.getCoords();
        final double[] prod = a.getCoords();

        res[0] = mult[1] * prod[2] - mult[2] * prod[1];
        res[1] = mult[2] * prod[0] - mult[0] * prod[2];
        res[2] = mult[0] * prod[1] - mult[1] * prod[0];

        return result;
    }

    @Override
    public Vec3 add(Vector a) {
        return (Vec3) super.add(a, new Vec3());
    }

    @Override
    public Vec3 sub(Vector a) {
        return (Vec3) super.sub(a, new Vec3());
    }

    @Override
    public Vec3 mult(double a) {
        return (Vec3) super.mult(a, new Vec3());
    }

    public Vec3 reflect(Vec3 normal) {
        return (Vec3) reflect(normal, new Vec3());
    }

    @Override
    public Vec3 normalize() {
        return (Vec3) normalize(new Vec3());
    }

    public double getX() {
        return getCoords()[0];
    }

    public double getY() {
        return getCoords()[1];
    }

    public double getZ() {
        return getCoords()[2];
    }

    public void setCoords(double x, double y, double z) {
        final double[] c = getCoords();
        c[0] = x;
        c[1] = y;
        c[2] = z;
    }

    @Override
    public Vec3 clone() {
        return new Vec3(getCoords());
    }
}
