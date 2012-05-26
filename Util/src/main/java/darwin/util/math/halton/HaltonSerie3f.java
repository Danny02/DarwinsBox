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
 * Java Port of the "Incremental Halton Code Example" from Andrew Willmott.<br>
 * <a href="http://www.andrewwillmott.com/s2007">http://www.andrewwillmott.com/s2007</a>
 * @author Daniel Heinrich
 */
public class HaltonSerie3f extends HaltonSerie2f
{
    private static final float kOneOverFive = 1f / 5f;
    //Current sample point.
    private float z;
    private long base5;

    /**
     * Advance to next point in the sequence. Returns the index of this point.
     */
    @Override
    public long inc() {
        super.inc();

        /////////////////////////////////////
        // base 5: use 3 bits for each base 5 digit.
        long mask = 0x7;
        long add = 0x3;  // amount to add to force carry once digit==dmax
        long dmax = 0x5;  // max digit

        float s = kOneOverFive;

        base5++;

        // expected iterations: 1.25
        while (true)
            if ((base5 & mask) == dmax) {
                base5 += add;          // force carry into next 3-bit digit
                z -= 4 * s;

                mask = mask << 3;
                dmax = dmax << 3;
                add = add << 3;

                s *= kOneOverFive;
            } else {
                z += s;     // we know digit n has gone from a to a + 1
                break;
            }

        return base2; // return the index of this sequence point
    }

    @Override
    public float[] getValues() {
        return new float[]{x, y, z};
    }

    @Override
    public float[] getValues(long index) {
        return new float[]{getHalton(index, 2),
                           getHalton(index, 3),
                           getHalton(index, 5)};
    }

    /**
     * Move back to first point in the sequence (i.e. the origin.)
     */
    @Override
    public void reset() {
        x = y = z = 0f;
        base2 = base3 = base5 = 0;
    }
}
