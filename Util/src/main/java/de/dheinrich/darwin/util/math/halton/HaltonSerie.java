/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under the terms of the GNU General Public License as published by  *  the Free Software Foundation, either version 3 of the License, or  *  (at your option) any later version.  *   *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.util.math.halton;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public abstract class HaltonSerie
{
    public abstract float[] getValues();

    public abstract float[] getValues(long index);

    public abstract void reset();

    public abstract long inc();

    /**
     *
     * @param n
     * nummber
     * @param b
     * base
     * @return
     * return term i of the base b Halton sequence.
     * You can think of this as, for example, just a generalization of
     * Heckbert's bit-reversal distribution trick.
     * E.g., when b=3, write n as a base 3 number, digit 0 -> which third of
     * interval the sample is in, 1 -> which third of that, 2 -> which third of that, etc.
     */
    protected float getHalton(long n, int b) {
        float result = 0;
        float ip = 1.f / b;
        float p = ip;

        while (n > 0) {
            result += (n % b) * p;
            n = n / b;
            p *= ip;
        }

        return result;
    }
}
