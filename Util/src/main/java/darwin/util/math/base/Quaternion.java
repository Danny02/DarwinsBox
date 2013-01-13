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

import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;

import static java.lang.Math.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Quaternion implements Cloneable
{
    private float w;
    private ImmutableVector<Vector3> vec;

    public Quaternion()
    {
        w = 1;
        vec = new Vector3();
    }

    public Quaternion(float w, ImmutableVector<Vector3> vec)
    {
        this.w = w;
        this.vec = vec.clone().normalize();
    }

    /**
     * initializes a Quaternion with a rotation axis and rotation angle.
     * <p/>
     * @param xyz rotation vektor
     * @param phi rotation angle in GRAD
     */
    public void setAxisAngle(ImmutableVector<Vector3> xyz, float phi)
    {
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
                                   double angle)
    {
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
     * @param yaw   Y axis angle in GRAD
     * @param roll  Z axis angle in GRAD
     */
    public void setEularAngles(double pitch, double yaw, double roll)
    {
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
     * @param old  Source Vector which gets mapped.
     * @param map2 Destination Vector which the source Vector gets mapped to.
     */
    public void mapVector(ImmutableVector<Vector3> old, ImmutableVector<Vector3> map2)
    {
        Vector3 o = old.clone().normalize();
        Vector3 m = map2.clone().normalize();

        float p = (float) acos(o.dot(m));
        ini(o.cross(m), p);
    }

    private void ini(ImmutableVector<Vector3> axis, float angle)
    {

        float[] c = axis.clone().normalize().getCoords();

        float half = angle * 0.5f;
        float sin_a = (float) sin(half);

        float x = c[0] * sin_a;
        float y = c[1] * sin_a;
        float z = c[2] * sin_a;

        vec = new Vector3(x, y, z);
        w = (float) cos(half);
    }

    public Quaternion conjugate()
    {
        return new Quaternion(w, vec.clone().mul(-1));
    }

    public float magnitute()
    {
        return (float) sqrt(magnituteSqr());//normaly conjungate . quaternion, but shortened version
    }

    public float magnituteSqr()
    {
        return vec.dot(vec) + w * w;//normaly conjungate . quaternion, but shortened version
    }

    public Quaternion normalize()
    {
        float mag = 1f / magnitute();
        return mult(mag);
    }

    public Quaternion inverse()
    {
        return conjugate();
//        return res.mul(1. / magnituteSqr(), res);
    }

    public Quaternion mult(float a)
    {
        return new Quaternion(w * a, vec.clone().mul(a));
    }

    public Quaternion mult(Quaternion q)
    {
        float[] q1 = vec.getCoords();
        float[] q2 = q.vec.getCoords();

        float ww = w * q.w - q1[0] * q.w - q1[1] * q2[1] - q1[2] * q2[2];
        float x = w * q2[0] + q1[0] * q.w + q1[1] * q2[2] - q1[2] * q2[1];
        float y = w * q2[1] + q1[1] * q.w + q1[2] * q2[0] - q1[0] * q2[2];
        float z = w * q2[2] + q1[2] * q.w + q1[0] * q2[1] - q1[1] * q2[0];

        return new Quaternion(ww, new Vector3(x, y, z));
    }

    public Vector3 mult(Vector3 a)
    {
        Quaternion b = new Quaternion(0, a);
        return mult(b).mult(inverse()).vec.clone();
    }

    public Quaternion getInterpolated(float delta)
    {
        Quaternion res = clone();
        Vector3 a = res.getRotationAxis();
        float phi = res.getPhi();
        res.ini(a, phi * delta);
        return res;
    }

    public float getPhi()
    {
        return (float) (acos(w) * 2);
    }

    public Vector3 getRotationAxis()
    {
        Quaternion q = normalize();

        double sin_a = sqrt(1. - w * w);
        if (abs(sin_a) < 0.0005) {
            sin_a = 1;
        }

        return q.vec.clone().mul((float) (1. / sin_a));
    }

    public Matrix4 getRotationMatrix()
    {

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

    @Override
    protected Quaternion clone()
    {
        return new Quaternion(w, vec);
    }

    @Override
    public boolean equals(Object obj)
    {
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
    public int hashCode()
    {
        int hash = 5;
        hash =
                37 * hash + (int) (Double.doubleToLongBits(this.w) ^ (Double.doubleToLongBits(this.w) >>> 32));
        hash = 37 * hash + (this.vec != null ? this.vec.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "Quaternion( Phi: " + getPhi() * Matrix4.RAD2GRAD + "°,  " + getRotationAxis() + ')';
    }
}
