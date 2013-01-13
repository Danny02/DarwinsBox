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
public class GenHaltonSerie extends  HaltonSerie
{
    private final Halton[] haltons;
    private final int dim;
    private long base2;
    private float firstval;

    public GenHaltonSerie(int dimensions) {
        dim = dimensions;
        haltons = new Halton[dim - 1];
        for (int i = 0; i < dim - 1; ++i) {
            haltons[i] = new Halton(i + 2);
        }
    }

    public GenHaltonSerie(int ... dimesions) {
        dim = dimesions.length +1;
        haltons = new Halton[dimesions.length];
        for (int idx = 0; idx < dimesions.length; idx++) {
            haltons[idx] = new Halton(dimesions[idx]);
        }
    }

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
            if ((oldBase2 & 1) != 0) {
                firstval -= s;
            }
            else {
                firstval += s;
            }

            s *= 0.5f;

            diff = diff >> 1;
            oldBase2 = oldBase2 >> 1;
        } while (diff != 0);

        for (Halton h : haltons) {
            h.inc();
        }

        return base2; // return the index of this sequence point
    }

    @Override
    public float[] getValues() {
        float[] ret = new float[dim];
        ret[0] = firstval;
        for (int i = 0; i < haltons.length; i++) {
            ret[i + 1] = haltons[i].getValue();
        }
        return ret;
    }

    @Override
    public float[] getValues(long index) {
        float[] ret = new float[dim];
        ret[0] = getHalton(index, 2);
        for (int i = 0; i < haltons.length; i++) {
            ret[i + 1] = getHalton(index, haltons[i].getBase());
        }
        return ret;
    }

    /**
     * Move back to first point in the sequence (i.e. the origin.)
     */
    @Override
    public void reset() {
        base2 = 0;
        firstval = 0f;
        for (int i = 0; i < dim - 1; ++i) {
            haltons[i] = new Halton(i + 2);
        }
    }
}
