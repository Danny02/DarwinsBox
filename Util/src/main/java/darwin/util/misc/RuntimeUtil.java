/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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
package darwin.util.misc;

import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class RuntimeUtil {

    public static final boolean IS_DEBUGGING;
    static{
        boolean a = false;
        Pattern p = Pattern.compile("-Xdebug|jdwp");
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (p.matcher(arg).find()) {
                a = true;
            }
        }
        IS_DEBUGGING = a;
    }
}
