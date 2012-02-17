/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.base;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Gerade
{
    private final Vec3 p1, p2;
    private final Vec3 dir;

    public Gerade(Vec3 p1, Vec3 p2) {
        this.p1 = p1;
        this.p2 = p2;
        dir = p2.sub(p1);
        dir.normalize(dir);
    }

    public Gerade transform(Matrix4 mat){
        Vec3 pn1 = new Vec3(mat.mult(p1));
        Vec3 pn2 = new Vec3(mat.mult(p2));
        return new Gerade(pn1, pn2);
    }

    public Vec3 isElementOf(Vec3 p){
        //TODO
        throw new UnsupportedOperationException();
    }

    public Vec3 isSchnittpunkt(Gerade g){
        //TODO
        throw new UnsupportedOperationException();
    }

    public Vec3 getSchnittpunkt(Gerade g){
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getDistance(Vec3 p){
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getDistance(Gerade g){
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getEbene(Gerade g){
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getEbene(Vec3 p){
        //TODO
        throw new UnsupportedOperationException();
    }

    public Vec3 getAufpunkt() {
        return p1;
    }

    public Vec3 getPoint2() {
        return p2;
    }

    public Vec3 getRichtung() {
        return dir;
    }

    @Override
    public String toString() {
        return "Aufpunkt: " + p1 + "\nRichtung: " + dir;
    }
}
