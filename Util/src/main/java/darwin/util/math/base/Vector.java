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

import java.io.Serializable;
import java.util.Arrays;

import static java.lang.Math.*;
/**
 * Repr�sentation eines 3 Dimensionalen Vektors
 * @author Daniel Heinrich
 */
public class Vector implements Serializable, Cloneable
{
    private static final long serialVersionUID = -5086769268702369258L;
    private final double[] coords;

    public Vector(int dimension) {
        coords = new double[dimension];
    }

//    public Vector(int dimension, Vector vec) {
//        coords = new double[dimension];
//        int min = vec.getDimension();
//        if (dimension < min)
//            min = dimension;
//
//        double[] c = vec.coords;
//        for (int i = 0; i < min; ++i)
//            coords[i] = c[i];
//    }
    public Vector(double... coord) {
        coords = coord;
    }

    public Vector(float[] coord) {
        coords = new double[coord.length];
        for (int i = 0; i < coord.length; i++)
            coords[i] = coord[i];
    }

    public double[] getCoords() {
        return coords;
    }

    public float[] getCoordsF() {
        final float[] cf = new float[coords.length];
        for (int i = 0; i < coords.length; i++)
            cf[i] = (float) coords[i];
        return cf;
    }

    public int getDimension() {
        return coords.length;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Vektor(");
        for (int i = 0; i < coords.length; ++i) {
            sb.append(coords[i]);
            sb.append(i != coords.length - 1 ? ", " : ')');

        }
        return sb.toString();
    }

    /**
     * normale Vektor addition
     * @param a
     * vektor der aufadiert werden soll
     * @param result
     * vektor in dem das ergebniss gespeichert werden soll
     * @return
     * zurückgabe des "result" vektors
     */
    public Vector add(Vector a, Vector result) {
        final double[] summant = a.getCoords();
        final double[] res = result.getCoords();
        assert res.length >= coords.length;
        assert summant.length >= coords.length;

        for (int i = 0; i < coords.length; ++i)
            res[i] = coords[i] + summant[i];

        return result;
    }

    /**
     * normale Vektor addition
     * @param a
     * vektor der aufadiert werden soll
     * @return
     * gibt einen neuen Vektor zurück der das ergebniss hält
     */
    public Vector add(Vector a) {
        return add(a, new Vector(coords.length));
    }

    /**
     * skalare vektor addition
     * @param a
     * summant
     * @param result
     * vektor in dem das ergebniss gespeichert werden soll
     * @return
     * zurückgabe des "result" vektors
     */
    public Vector add(double a, Vector result) {
        final double[] res = result.getCoords();
        assert res.length >= coords.length;

        for (int i = 0; i < coords.length; ++i)
            res[i] = coords[i] + a;

        return result;
    }

    public Vector sub(Vector a) {
        return sub(a, new Vector(coords.length));
    }

    /**
     * normale Vektor subtraktion
     * @param a
     * vektor der subtraiert werden soll
     * @param result
     * vektor in dem das ergebniss gespeichert werden soll
     * @return
     * zurückgabe des "result" vektors
     */
    public Vector sub(Vector a, Vector result) {
        final double[] summant = a.getCoords();
        final double[] res = result.getCoords();
        assert res.length >= coords.length;
        assert summant.length >= coords.length;

        for (int i = 0; i < coords.length; ++i)
            res[i] = coords[i] - summant[i];

        return result;
    }

    public double dot(Vector a) {
        final double[] summant = a.getCoords();
        assert summant.length >= coords.length;

         double res = 0;
        for (int i = 0; i < getDimension(); ++i)
            res += coords[i] * summant[i];

        return res;
    }

    public Vector mult(double a) {
        return mult(a, new Vector(coords.length));
    }

    public Vector mult(double a, Vector result) {
        final double[] res = result.getCoords();
        assert res.length >= coords.length;

        for (int i = 0; i < coords.length; ++i)
            res[i] = coords[i] * a;

        return result;
    }

    public Vector reflect(Vector normal) {
        return reflect(normal, new Vector(coords.length));
    }

    public Vector reflect(Vector normal, Vector result) {
        Vector sn = normal.mult(dot(normal) * 2);
        return sub(sn, result);
    }

    public Vector normalize() {
        return normalize(new Vector(coords.length));
    }

    public Vector normalize(Vector result) {
        final double len = lenght();
        if (len != 0)
            mult(1f / len, result);
        return result;
    }

    public double lenght() {
        return sqrt(lenquad());
    }

    public double lenquad() {
        double sum = 0f;
        for (double f : coords)
            sum += f * f;
        return sum;
    }

    public double dist(Vector a) {
        return sub(a).lenght();
    }

    public double getAngle(Vector b) {
        final Vector a = this.normalize();
        b = b.normalize();

        return a.getAngleBothNormalized(b);
    }

    public double getAngleBothNormalized(Vector b) {
        return acos(dot(b));
    }

    public Vector min(Vector comp) {
        return min(comp, new Vector(coords.length));
    }

    public Vector min(Vector comp, Vector dst) {
        final double[] c = comp.coords;
        final double[] d = dst.coords;

        for (int i = 0; i < coords.length; i++)
            d[i] = Math.min(coords[i], c[i]);
        return dst;
    }

    public Vector max(Vector comp) {
        return max(comp, new Vector(coords.length));
    }

    public Vector max(Vector comp, Vector dst) {
        final double[] c = comp.coords;
        final double[] d = dst.coords;

        for (int i = 0; i < coords.length; i++)
            d[i] = Math.max(coords[i], c[i]);
        return dst;
    }

    @Override
    public Vector clone() {
        return new Vector(coords.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Vector other = (Vector) obj;
        if (!Arrays.equals(this.coords, other.coords))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Arrays.hashCode(this.coords);
        return hash;
    }
}

