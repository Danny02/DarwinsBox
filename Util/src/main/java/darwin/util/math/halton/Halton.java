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
package darwin.util.math.halton;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Halton
{
    private static final int[] prims = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29,
                                                 31, 37, 41, 43, 47, 53, 59, 61,
                                                 67, 71, 73, 79, 83, 89, 97};
    private final long mask, step, add;
    private final float oneover;
    private long base;
    private float value;
    private final int dim, dmax;

    public Halton(int dimension) {
        assert !(dimension < 2 || dimension > 27) : "Only Dimensions from 2 - 27 supported";
        dim = dimension;

        int p = prims[dimension - 2];
        oneover = 1f / p;
        this.mask = nextHigher(p) - 1;
        this.dmax = p;  // max digit
        step = dmax - 1;  // max digit
        add = mask - dmax + 1;  // amount to add to force carry once
    }

    public void inc() {
        float s = oneover;
        long m = mask;
        long a = add;  // amount to add to force carry once digit==dmax
        long d = dmax;  // max digit
        base++;

        // expected iterations: 1.5
        while (true) {
            if ((base & m) == d) {
                base += a;          // force carry into next 2-bit digit
                value -= step * s;

                m = m << dim;
                d = d << dim;
                a = a << dim;

                s *= oneover;
            } else {
                value += s;     // we know digit n has gone from a to a + 1
                break;
            }
        }
    }

    private long nextHigher(long k) {
        k--;
        for (int i = 1; i < Long.SIZE - 1; i <<= 1) {
            k = k | k >> i;
        }
        return k + 1;
    }

    public int getBase() {
        return dmax;
    }

    public float getValue() {
        return value;
    }
}
