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
public class HaltonSerie2f extends HaltonSerie
{
    private static final float kOneOverThree = 1f / 3f;


    //Current sample point.
    private float x, y;
    private long base2, base3;

    /**
     * Advance to next point in the sequence. Returns the index of this point.
     */
    @Override
    public long inc() {
        /////////////////////////////////////
        // base 2

        long oldBase2 = base2;
        base2++;
        long diff = base2 ^ oldBase2;

        // bottom bit always changes, higher bits
        // change less frequently.
        float s = 0.5f;

        // diff will be of the form 0*1+, i.e. one bits up until the last carry.
        // expected iterations = 1 + 0.5 + 0.25 + ... = 2
        do {
            if ((oldBase2 & 1) != 0)
                x -= s;
            else
                x += s;

            s *= 0.5f;

            diff = diff >> 1;
            oldBase2 = oldBase2 >> 1;
        } while (diff != 0);


        /////////////////////////////////////
        // base 3: use 2 bits for each base 3 digit.

        long mask = 0x3;  // also the max base 3 digit
        long add = 0x1;  // amount to add to force carry once digit==3
        s = kOneOverThree;

        base3++;

        // expected iterations: 1.5
        while (true)
            if ((base3 & mask) == mask) {
                base3 += add;          // force carry into next 2-bit digit
                y -= 2 * s;

                mask = mask << 2;
                add = add << 2;

                s *= kOneOverThree;
            } else {
                y += s;     // we know digit n has gone from a to a + 1
                break;
            }

        return base2; // return the index of this sequence point
    }

    @Override
    public float[] getValues(){
        return new float[]{x,y};
    }

    @Override
    public float[] getValues(long index) {
        return new float[]{getHalton(index, 2),
                           getHalton(index, 3)};
    }

    /**
     * Move back to first point in the sequence (i.e. the origin.)
     */
    @Override
    public void reset() {
        x = y = 0f;
        base2 = base3 = 0;
    }
}
