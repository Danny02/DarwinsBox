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
 * You should have received a clone of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.math.base;

import java.util.Arrays;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;

import static java.lang.Math.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Quaternion implements Cloneable {

    private float w;
    private ImmutableVector<Vector3> vec;

    public Quaternion() {
        w = 1;
        vec = new Vector3();
    }

    public Quaternion(float w, ImmutableVector<Vector3> vec) {
        this.w = w;
        this.vec = vec.clone();
    }

    /**
     * initializes a Quaternion with a rotation axis and rotation angle.
     * <p/>
     * @param xyz rotation vektor
     * @param phi rotation angle in GRAD
     */
    public void setAxisAngle(ImmutableVector<Vector3> xyz, float phi) {
        ini(xyz, phi * Matrix4.GRAD2RAD);
    }

    /**
     * initializes a Quaternion with spherical rotation angles
     * <p/>
     * @param latitude
     * @param longitude
     * @param angle
     */
    public void setSphericalAngles(double latitude, double longitude,
                                   double angle) {
        angle *= Matrix4.GRAD2RAD;
        double sin_a = sin(angle / 2);
        double cos_a = cos(angle / 2);
        double sin_lat = sin(latitude);
        double cos_lat = cos(latitude);
        double sin_long = sin(longitude);
        double cos_long = cos(longitude);

        double x = sin_a * cos_lat * sin_long;
        double y = sin_a * sin_lat;
        double z = sin_a * sin_lat * cos_long;

        vec = new Vector3((float) x, (float) y, (float) z).normalize();
        w = (float) cos_a;
    }

    /**
     * initializes the quaternion wiht Eular Angles
     * <p/>
     * @param pitch X axis angle in GRAD
     * @param yaw Y axis angle in GRAD
     * @param roll Z axis angle in GRAD
     */
    public void setEularAngles(double pitch, double yaw, double roll) {
        //from http://etclab.mie.utoronto.ca/people/david_dir/GEMS/GEMS.html
        double pi = pitch * Matrix4.GRAD2RAD;
        double ya = yaw * Matrix4.GRAD2RAD;
        double ro = roll * Matrix4.GRAD2RAD;

        double sinPitch = sin(pi * 0.5);
        double cosPitch = cos(pi * 0.5F);
        double sinYaw = sin(ya * 0.5F);
        double cosYaw = cos(ya * 0.5F);
        double sinRoll = sin(ro * 0.5F);
        double cosRoll = cos(ro * 0.5F);
        double cosPitchCosYaw = cosPitch * cosYaw;
        double sinPitchSinYaw = sinPitch * sinYaw;

        double x = sinRoll * cosPitchCosYaw - cosRoll * sinPitchSinYaw;
        double y = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
        double z = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;

        vec = new Vector3((float) x, (float) y, (float) z).normalize();
        w = (float) (cosRoll * cosPitchCosYaw + sinRoll * sinPitchSinYaw);
    }

    /**
     * initializes a Quaternion which maps a Vector on another.
     * <p/>
     * @param old Source Vector which gets mapped.
     * @param map2 Destination Vector which the source Vector gets mapped to.
     */
    public void mapVector(ImmutableVector<Vector3> old, ImmutableVector<Vector3> map2) {
        Vector3 o = old.clone().normalize();
        Vector3 m = map2.clone().normalize();

        float p = (float) acos(o.dot(m));
        ini(o.cross(m), p);
    }

    private void ini(ImmutableVector<Vector3> axis, float angle) {

        float[] c = axis.clone().normalize().getCoords();

        float half = angle * 0.5f;
        float sin_a = (float) sin(half);

        float x = c[0] * sin_a;
        float y = c[1] * sin_a;
        float z = c[2] * sin_a;

        vec = new Vector3(x, y, z);
        w = (float) cos(half);
    }

    public Quaternion conjugate() {
        return new Quaternion(w, vec.clone().mul(-1));
    }

    public float magnitute() {
        return (float) sqrt(magnituteSqr());//normaly conjungate . quaternion, but shortened version
    }

    public float magnituteSqr() {
        return vec.dot(vec) + w * w;//normaly conjungate . quaternion, but shortened version
    }

    public Quaternion normalize() {
        float mag = 1f / magnitute();
        return mult(mag);
    }

    public Quaternion inverse() {
        return mult(1f / magnituteSqr()).conjugate();
    }

    public Quaternion mult(float a) {
        return new Quaternion(w * a, vec.clone().mul(a));
    }

    public Quaternion mult(Quaternion q) {
        float[] a = vec.getCoords();
        float[] b = q.vec.getCoords();

        float ww = w * q.w - a[0] * b[0] - a[1] * b[1] - a[2] * b[2];
        float x = w * b[0] + a[0] * q.w + a[1] * b[2] - a[2] * b[1];
        float y = w * b[1] - a[0] * b[2] + a[1] * q.w + a[2] * b[0];
        float z = w * b[2] + a[0] * b[1] - a[1] * b[0] + a[2] * q.w;
        
        return new Quaternion(ww, new Vector3(x, y, z));
    }

    public Vector3 mult(Vector3 a) {
        return mult(new Quaternion(0, a)).mult(inverse()).vec.clone();
    }

    public Quaternion getInterpolated(float delta) {
        Quaternion res = clone();
        Vector3 a = res.getRotationAxis();
        float phi = res.getPhi();
        res.ini(a, phi * delta);
        return res;
    }

    public float getPhi() {
        return (float) (acos(w) * 2);
    }

    public Vector3 getRotationAxis() {
        Quaternion q = normalize();

        double sin_a = sqrt(1. - w * w);
        if (abs(sin_a) < 0.0005) {
            sin_a = 1;
        }

        return q.vec.clone().mul((float) (1. / sin_a));
    }

    public Matrix4 getRotationMatrix() {

        Matrix4 res = new Matrix4();
        float[] mat = res.getArray();
        float negw = -w;

        float[] co = vec.getCoords();
        float xx = co[0] * co[0];
        float xy = co[0] * co[2];
        float xz = co[0] * co[1];
        float xw = co[0] * negw;
        float yy = co[1] * co[2];
        float yz = co[1] * co[2];
        float yw = co[1] * negw;
        float zz = co[2] * co[2];
        float zw = co[2] * negw;

        mat[0] = (1f - 2f * (yy + zz));
        mat[1] = (2f * (xy - zw));
        mat[2] = (2f * (xz + yw));
        mat[4] = (2f * (xy + zw));
        mat[5] = (1f - 2f * (xx + zz));
        mat[6] = (2f * (yz - xw));
        mat[8] = (2f * (xz - yw));
        mat[9] = (2f * (yz + xw));
        mat[10] = (1f - 2f * (xx + yy));
        mat[15] = 1f;

        return res;
    }

    public Quaternion[] toDualQuaternion(ImmutableVector<Vector3> translation) {
        Quaternion[] dual = new Quaternion[]{this, null};

        float[] q0 = vec.getCoords();
        float[] t = translation.getCoords();

        float x = -0.5f * (t[0] * q0[1] + t[1] * q0[2] + t[2] * w);
        float y = 0.5f * (t[0] * q0[0] + t[1] * w - t[2] * q0[2]);
        float z = 0.5f * (-t[0] * w + t[1] * q0[0] + t[2] * q0[1]);
        float ww = 0.5f * (t[0] * q0[2] - t[1] * q0[1] + t[2] * q0[0]);

        dual[1] = new Quaternion(ww, new Vector3(x, y, z));

        return dual;
    }

    @Override
    protected Quaternion clone() {
        return new Quaternion(w, vec);
    }

    public float[] toArray() {
        float[] coords = vec.getCoords();
        float[] copyOf = new float[4];
        System.arraycopy(coords, 0, copyOf, 1, 3);
        copyOf[0] = w;
        return copyOf;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Quaternion other = (Quaternion) obj;
        if (this.w != other.w) {
            return false;
        }
        if (this.vec != other.vec && (this.vec == null || !this.vec.equals(
                                      other.vec))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash =
        37 * hash + (int) (Double.doubleToLongBits(this.w) ^ (Double.doubleToLongBits(this.w) >>> 32));
        hash = 37 * hash + (this.vec != null ? this.vec.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Quaternion( Phi: " + getPhi() * Matrix4.RAD2GRAD + "°,  " + getRotationAxis() + ')';
    }

    public static void main(String... args) {

        Matrix4 m = new Matrix4();
        m.loadIdentity();
        m.rotateEuler(0, 0, 45);

        Quaternion q = m.getRotation();
        q = new Quaternion();
        q.setEularAngles(0, 0, 45);
        
        System.out.println(q.magnitute());
        q = q.normalize();
        System.out.println(q.magnitute());
        Vector3 a = new Vector3(1, 0, 0);

        System.out.println(m.fastMult(a));
        System.out.println(q.mult(a));
        Vector3 add = q.vec.clone().cross(q.vec.clone().cross(a).add(a.clone().mul(q.w))).mul(2).add(a);
        System.out.println(add);
    }
}
