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

import static java.lang.Math.*;

/**
 * Konfiguriert eine 4x4 Matrix
 * @author Daniel Heinrich
 */
public class Matrix4 extends Matrix
{
    public enum Axis
    {
        X(0), Y(4), Z(8);
        private int offset;

        private Axis(int offset) {
            this.offset = offset;
        }
    }
    private static final long serialVersionUID = -5557326075977567526L;
    public static final double GRAD2RAD = 0.01745329251994329;//PI / 180;
    public static final double RAD2GRAD = 57.29577951308232;// 180 / PI;

    public Matrix4() {
        super(4);
    }

    /**
     * Builds a orthogonal Coordinate System, where the given Vector
     * represents the given Axis. Both other Axis are generated in a way that
     * no gurantee can be given how their orientation are.
     * @param dir
     * direction Vector of the given Axis
     * @param a
     * Axis of the given Vektor
     */
    public Matrix4(Vec3 dir, Axis a) {
        this();
        loadIdentity();

        Vec3 di = dir.normalize();
        setAxis(di, a);

        double[] d = di.getCoords();
        Vec3 dir2 = new Vec3(d[1], d[2], -d[0]);
        Vec3 dir3 = dir.cross(dir2);

        Axis[] vals = Axis.values();
        int a2 = a.ordinal();
        a2 = (1 << a2) & 3; //(ordinal+1)%3
        int a3 = (1 << a2) & 3;//(ordinal+2)%3
        setAxis(dir2, vals[a2]);
        setAxis(dir3, vals[a3]);
    }

    /**
     * Initialize a Matrix which will transform a Source Space to a
     * Destination Space.
     * @param src
     * Matrix which describes the Source Coordinate Space
     * @param dst
     * Matrix which describes the Destination Coordinate Space
     */
    public Matrix4(Matrix4 src, Matrix4 dst) {
        this();
        Matrix4 tmp = new Matrix4();
        src.inverse(tmp);
        tmp.mult(dst, this);
    }

    public void setAxis(Vec3 x, Vec3 y, Vec3 z) {
        setAxis(x, Axis.X);
        setAxis(y, Axis.Y);
        setAxis(z, Axis.Z);
    }

    @Override
    public Vector mult(double[] a) {
        double[] b;
        if (a.length < 4) {
            b = new double[4];
            b[3] = 1;
            for (int i = 0; i < a.length; ++i)
                b[i] = a[i];
        } else
            b = a;

        return super.mult(b);
    }

    /**
     * translate the matrix in world space
     * @param vec
     * translation vector
     * @return
     * the manipulated matrix
     */
    public Matrix4 worldTranslate(Vec3 vec) {
        double[] coords = vec.getCoords();
        return worldTranslate(coords[0], coords[1], coords[2]);
    }

    /**
     * translate the matrix in world space
     * @param x
     * x-axis translation
     * @param y
     * y-axis translation
     * @param z
     * z-axis translation
     * @return
     * the manipulated matrix
     */
    public Matrix4 worldTranslate(double x, double y, double z) {
        float[] m = getArray();
        m[12] += x * m[15];
        m[13] += y * m[15];
        m[14] += z * m[15];

        return this;
    }

    public Matrix4 setWorldTranslate(Vec3 vec) {
        double[] coords = vec.getCoords();
        return setWorldTranslate(coords[0], coords[1], coords[2]);
    }

    public Matrix4 setWorldTranslate(double x, double y, double z) {
        float[] m = getArray();
        m[12] = (float) x;
        m[13] = (float) y;
        m[14] = (float) z;

        return this;
    }

    /**
     * translate the matrix in matrix space
     * @param vec
     * translation vector
     * @return
     * the manipulated matrix
     */
    public Matrix4 translate(Vec3 vec) {
        double[] coords = vec.getCoords();
        return translate(coords[0], coords[1], coords[2]);
    }

    /**
     * translate the matrix in matrix space
     * @param x
     * x-axis translation
     * @param y
     * y-axis translation
     * @param z
     * z-axis translation
     * @return
     * the manipulated matrix
     */
    public Matrix4 translate(double x, double y, double z) {
        float[] m = getArray();
        m[12] += m[0] * x + m[4] * y + m[8] * z;
        m[13] += m[1] * x + m[5] * y + m[9] * z;
        m[14] += m[2] * x + m[6] * y + m[10] * z;
        m[15] += m[3] * x + m[7] * y + m[11] * z;
        return this;
    }

    /**
     * Rotates the Matrix with the given Eular Angles.
     * Rotation order is x,y,z.
     * @param angles
     * 3D Vector which holds the rotation angles for each axis
     * @return the same Matrix
     */
    public Matrix4 rotateEuler(Vec3 angles) {
        double[] c = angles.getCoords();
        return rotateEuler(c[0], c[1], c[2]);
    }

    /**
     * Rotates the Matrix with the given Eular Angles.
     * Rotation order is x,y,z.
     * @param x
     * @param y
     * @param z
     * @return the same Matrix
     */
    public Matrix4 rotateEuler(double x, double y, double z) {
        double xx = x * GRAD2RAD;
        double yy = y * GRAD2RAD;
        double zz = z * GRAD2RAD;

        float A = (float) cos(xx);
        float B = (float) sin(xx);
        float C = (float) cos(yy);
        float D = (float) sin(yy);
        float E = (float) cos(zz);
        float F = (float) sin(zz);
        float AD = A * D;
        float BD = B * D;

        Matrix4 res = new Matrix4();
        float[] mat = res.getArray();
        mat[0] = C * E;
        mat[4] = -C * F;
        mat[8] = D;

        mat[1] = BD * E + A * F;
        mat[5] = -BD * F + A * E;
        mat[9] = -B * C;
        mat[2] = -AD * E + B * F;
        mat[6] = AD * F + B * E;
        mat[10] = A * C;
        mat[15] = 1;

        this.mult(res, this);

        return this;
    }

    /**
     * Rotates the Matrix with the rotationen the given Quaternion describes
     * @param quat
     * @return the same Matrix
     */
    public Matrix4 rotate(Quaternion quat) {
        Matrix4 rot = quat.getRotationMatrix();
        this.mult(rot, this);
        return this;
    }

    /**
     * scales the matrix space
     * @param scale
     * scale factor vector
     * @return
     * the manipulated matrix
     */
    public Matrix4 scale(Vec3 scale) {
        double[] coords = scale.getCoords();
        return scale(coords[0], coords[1], coords[2]);
    }

    /**
     * scales the matrix space
     * @param scalex
     * x-axis scale factor
     * @param scaley
     * y-axis scale factor
     * @param scalez
     * z-axis scale factor
     * @return
     * the manipulated matrix
     */
    public Matrix4 scale(double scalex, double scaley, double scalez) {

        if (scalex == scaley && scalex == scalez)
            return scale(scalex);

        float[] m = getArray();

        if (scalex != 1)
            for (int i = 0; i < 3; ++i)
                m[i] *= scalex;
        if (scaley != 1)
            for (int i = 4; i < 7; ++i)
                m[i] *= scaley;
        if (scalez != 1)
            for (int i = 8; i < 11; ++i)
                m[i] *= scalez;

        return this;
    }

    /**
     * scales the matrix space evenly
     * @param scale
     * scale factor
     * @return
     * the manipulated matrix
     */
    public Matrix4 scale(double scale) {
        if (scale == 1)
            return this;

        float[] m = getArray();
        float s = (float) (1. / scale);
        m[12] *= s;
        m[13] *= s;
        m[14] *= s;
        m[15] *= s;

        return this;
    }

    /**
     * @return total translation of matrix space relativ to world space.
     * Can be used as position of the matrix in world space.
     */
    public Vec3 getTranslation() {
        float[] mat = getArray();
        return new Vec3(mat[12], mat[13], mat[14]);
    }

    /**
     * @return a axis vector of the object space represented by this matrix (not normalized).
     */
    public Vec3 getAxis(Axis a) {
        float[] mat = getArray();
        float[] vec = new float[3];
        System.arraycopy(mat, a.offset, vec, 0, 3);
        return new Vec3(vec);
    }

    public void setAxis(Vec3 axis, Axis a) {
        System.arraycopy(axis.getCoords(), 0, getArray(), a.offset, 3);
    }

    public Vec3 getEularAngles() {
        float[] mat = getArray();
        double[] angles = new double[3];

        angles[1] = asin(mat[8]);        /* Calculate Y-axis angle */

        double C = cos(angles[1]);
        if (abs(C) > 0.005) /* Gimball lock? */ {
            double trx = mat[10] / C;           /* No, so get X-axis angle */
            double tryy = -mat[9] / C;
            angles[0] = atan2(tryy, trx);
            trx = mat[0] / C;            /* Get Z-axis angle */
            tryy = -mat[4] / C;
            angles[2] = atan2(tryy, trx);
        } else /* Gimball lock has occurred */ {
            angles[0] = 0;                      /* Set X-axis angle to zero */
            double trx = mat[5];                 /* And calculate Z-axis angle */
            double tryy = mat[1];
            angles[2] = atan2(tryy, trx);
        }

        /* return only positive angles in [0,360] */
        for (int i = 0; i < 3; ++i) {
            angles[i] *= RAD2GRAD;
            if (angles[i] < 0)
                angles[i] += 360;
        }

        return new Vec3(angles);
    }

    public Quaternion getRotation() {
        // from http://www.euclideanspace.com/maths/geometry/rotations
        //           /conversions/matrixToQuaternion/index.htm

        /* The max( 0, ... ) is just a safeguard against rounding error.
         * copysign takes the sign from the second term and sets the sign
         * of the first without altering the magnitude,
         * I don't know of a java equivalent.
         *
         * quaternion.w = sqrt( max( 0, 1 + m00 + m11 + m22 ) ) / 2;
         * quaternion.x = sqrt( max( 0, 1 + m00 - m11 - m22 ) ) / 2;
         * quaternion.y = sqrt( max( 0, 1 - m00 + m11 - m22 ) ) / 2;
         * quaternion.z = sqrt( max( 0, 1 - m00 - m11 + m22 ) ) / 2;
         * Q.x = _copysign( Q.x, m21 - m12 )
         * Q.y = _copysign( Q.y, m02 - m20 )
         * Q.z = _copysign( Q.z, m10 - m01 )
         */

        float[] mat = getArray();
//
//        double w = Math.sqrt(Math.max(0, 1 + mat[0] + mat[5] + mat[10])) * 0.5;
//        double x = Math.sqrt(Math.max(0, 1 + mat[0] - mat[5] - mat[10])) * 0.5;
//        double y = Math.sqrt(Math.max(0, 1 - mat[0] + mat[5] - mat[10])) * 0.5;
//        double z = Math.sqrt(Math.max(0, 1 - mat[0] - mat[0] + mat[10])) * 0.5;
//        x = Math.copySign(x, mat[6] - mat[9]);
//        y = Math.copySign(y, mat[8] - mat[2]);
//        z = Math.copySign(z, mat[1] - mat[4]);
//

        double tr = mat[0] + mat[5] + mat[10];
        double w, x, y, z;

        if (tr > 0) {
            double S = sqrt(tr + 1.0) * 2; // S=4*w
            w = 0.25 * S;
            x = (mat[9] - mat[6]) / S;
            y = (mat[8] - mat[2]) / S;
            z = (mat[1] - mat[4]) / S;
        } else if ((mat[0] > mat[5]) && (mat[0] > mat[10])) {
            double S = sqrt(1.0 + mat[0] - mat[5] - mat[10]) * 2; // S=4*x
            w = (mat[9] - mat[6]) / S;
            x = 0.25 * S;
            y = (mat[4] + mat[1]) / S;
            z = (mat[8] + mat[2]) / S;
        } else if (mat[5] > mat[10]) {
            double S = sqrt(1.0 + mat[5] - mat[0] - mat[10]) * 2; // S=4*y
            w = (mat[8] - mat[2]) / S;
            x = (mat[4] + mat[1]) / S;
            y = 0.25 * S;
            z = (mat[6] + mat[9]) / S;
        } else {
            double S = sqrt(1.0 + mat[10] - mat[0] - mat[5]) * 2; // S=4*z
            w = (mat[1] - mat[4]) / S;
            x = (mat[8] + mat[2]) / S;
            y = (mat[6] + mat[9]) / S;
            z = 0.25 * S;
        }

        return new Quaternion(w, new Vec3(x, y, z));
    }

    public void setRotation(Matrix4 rot) {
        float[] r = rot.getArray();
        float[] m = getArray();

        m[0] = r[0];
        m[4] = r[4];
        m[8] = r[8];
        m[1] = r[1];
        m[5] = r[5];
        m[9] = r[9];
        m[2] = r[2];
        m[6] = r[6];
        m[10] = r[10];
    }
}
