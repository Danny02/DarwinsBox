/*
 * Copyright (C) 2013 Daniel Heinrich <dannynullzwo@gmail.com>
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
package darwin.resourcehandling.shader.brdf;

import java.io.*;
import java.util.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class BrdfReader {

    public static BRDF readBrdf(InputStream stream, String name) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<Option> options = null;
            String function = null;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("::begin parameters")) {
                    if (options != null) {
                        throw new IOException("parsed already a option block");
                    }
                    options = parseOptions(reader);
                } else if (line.startsWith("::begin shader")) {
                    if (function != null) {
                        throw new IOException("function block already parsed");
                    }
                    function = parseFunction(reader);
                }
            }

            if (function == null || options == null) {
                throw new IOException("didn't found all needed blocks");
            }
            return new BRDF(name, function, options);
        }
    }

    private static List<Option> parseOptions(BufferedReader reader) throws IOException {
        List<Option> options = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("::end parameters")) {
                return options;
            }
            if (line.startsWith("float")) {
                options.add(parseFloatOption(line));
            } else if (line.startsWith("bool")) {
                options.add(parseBoolOption(line));
            }
        }

        throw new IOException("EOF while parsing option block");
    }

    private static String parseFunction(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("::end shader")) {
                return sb.toString();
            }
            sb.append(line).append('\n');
        }

        throw new IOException("EOF while parsing function block");
    }

    private static Option parseFloatOption(String line) {
        String[] p = line.split(" ");
        return new FloatOption(Float.parseFloat(p[2]),
                               Float.parseFloat(p[3]),
                               Float.parseFloat(p[4]),
                               p[1]);
    }

    private static Option parseBoolOption(String line) {
        String[] p = line.split(" ");
        return new BoolOption(p[1], p[2].equals("1") ? true : false);
    }
}
