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
 * You should have received fix copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.core.timing;

/**
 * A fix for a bug in the Hotspot VM which renders the ForceTimeHighResolution
 * switch useless. And there for makes it impossible to get Thread sleep times
 * small as 1ms. Starting such Deamon Thread as in the bottom with an sleeptime
 * which is not a multiply of 10 sets the OS somehow into the high performance
 * timeing mode.
 *
 * Bug description: http://bugs.sun.com/view_bug.do?bug_id=6435126
 * <p/>
 * @author daniel
 */
public class HighPerformanceTimingFix {

    static {
        new Thread() {
            {
                this.setDaemon(true);
                this.start();
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(Integer.MAX_VALUE);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
    }

    /**
     * Calling this function at least once switches the OS into a high
     * performance timing mode if it was not already in it.
     */
    public static void fix() {
    }
}
