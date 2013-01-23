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
package darwin.util.math.util;

import static java.lang.Math.abs;

/**
 *
 * @author daniel
 */
public class MathUtil
{
    /**
     * Compares two float values on almost equality. Characteristics:
     * <p/>- Measures whether two floats are ‘close’ to each other, where close is
     * defined by ulps, also interpreted as how many floats there are in-between
     * the numbers
     * <p/>- Treats infinity as being close to FLT_MAX
     * <p/>- Treats NANs as being four million ulps away from everything
     * (assuming the default NAN value for x87), except other NANs
     * <p/>- Accepts greater relative error as numbers gradually underflow to
     * subnormals
     * <p/>- Treats tiny negative numbers as being close to tiny positive numbers
     * <p/>
     * @param a
     * @param b
     * @param maxUlps
     * is one plus the number of representable floats between them. Range = [1, 4*1024*1024[
     * <p/>
     * @return true if the distance between a and b is <= maxUlps
     */
    public static boolean almostEqual(float a, float b, int maxUlps)
    {
        // Make sure maxUlps is non-negative and small enough that the
        // default NAN won't compare as equal to anything.
        assert (maxUlps > 0 && maxUlps < 4 * 1024 * 1024);
        int aInt = Float.floatToRawIntBits(a);
        // Make aInt lexicographically ordered as a twos-complement int
        if (aInt < 0) {
            aInt = 0x8000_0000 - aInt;
        }
        // Make bInt lexicographically ordered as a twos-complement int
        int bInt = Float.floatToRawIntBits(a);
        if (bInt < 0) {
            bInt = 0x8000_0000 - bInt;
        }
        int intDiff = abs(aInt - bInt);
        if (intDiff <= maxUlps) {
            return true;
        }
        return false;
    }

    /**
     * Compares two float values on almost equality. Characteristics:
     * <p/>- Measures whether two floats are ‘close’ to each other, where close is
     * defined by ulps, also interpreted as how many floats there are in-between
     * the numbers
     * <p/>- Treats infinity as being close to FLT_MAX
     * <p/>- Treats NANs as being four million ulps away from everything
     * (assuming the default NAN value for x87), except other NANs
     * <p/>- Accepts greater relative error as numbers gradually underflow to
     * subnormals
     * <p/>- Treats tiny negative numbers as being close to tiny positive numbers
     * <p/>
     * @param a
     * @param b
     * @param maxUlps
     * is one plus the number of representable floats between them. Range = [1, 4*1024*1024[
     * <p/>
     * @return true if the distance between a and b is <= maxUlps
     */
    public static boolean almostEqual(double a, double b, int maxUlps)
    {
        // Make sure maxUlps is non-negative and small enough that the
        // default NAN won't compare as equal to anything.
        assert (maxUlps > 0 && maxUlps < 4 * 1024 * 1024);
        long aLong = Double.doubleToRawLongBits(a);
        // Make aInt lexicographically ordered as a twos-complement int
        if (aLong < 0) {
            aLong = 0x8000_0000_0000_0000L - aLong;
        }
        // Make bInt lexicographically ordered as a twos-complement int
        long bLong = Double.doubleToRawLongBits(b);
        if (bLong < 0) {
            bLong = 0x8000_0000_0000_0000L - bLong;
        }
        long intDiff = abs(aLong - bLong);
        if (intDiff <= maxUlps) {
            return true;
        }
        return false;
    }
}
