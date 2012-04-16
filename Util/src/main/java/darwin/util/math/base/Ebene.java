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
package darwin.util.math.base;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Ebene
{
    private final Vec3 p1, p2, p3;
    private final Vec3 normal;

    public Ebene(Vec3 p1, Vec3 p2, Vec3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        Vec3 tmp = p3.sub(p1);
        normal = p2.sub(p1);
        normal.cross(tmp, normal);
        normal.normalize(normal);
    }

    public boolean isParallel(Gerade g) {
        return g.getRichtung().dot(normal) == 0.;
    }

    public boolean isParallel(Ebene e) {
        return Math.abs(e.getNormal().dot(normal)) == 1.;
    }

    public Gerade getSchnittKante(Ebene e) {
        //TODO
        throw new UnsupportedOperationException();
    }

    //TODO extrem slow
    public Vec3 getSchnittPunkt(Gerade e) {
        Vec3 a = e.getAufpunkt();
        Vec3 b = e.getPoint2();

        Vec3 left = a.sub(p1);
        float[] ml = a.sub(b).getCoordsF();
        float[] mm = p2.sub(p1).getCoordsF();
        float[] mr = p3.sub(p1).getCoordsF();

        Matrix mat = new Matrix(3);
        mat.setMat(new float[]{ml[0], ml[1], ml[2],
                               mm[0], mm[1], mm[2],
                               mr[0], mr[1], mr[2]});
        mat.inverse(mat);
        //if b-a, p2-p1, p3-p1 linear dependend(in einer ebenen) dann ist die gerade paralle zur ebene
        //if u,v € [0,1] and u+v<=1 then inter point in triangle of p1,p2,p3
        double t = mat.mult(left).getCoords()[0];// left(t,u,v)
        Vec3 ret = b.sub(a);
        ret.mult(t);
        ret.add(a, ret);
        return ret;
    }

    public double getDistance(Vec3 p) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getDistance(Gerade g) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public double getDistance(Ebene e) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public Vec3 getAufpunkt() {
        return p1;
    }

    public Vec3 getPoint2() {
        return p2;
    }

    public Vec3 getPoint3() {
        return p3;
    }

    public Vec3 getNormal() {
        return normal;
    }
}
