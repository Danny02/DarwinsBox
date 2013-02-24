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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import com.google.common.collect.Lists;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class BRDF {

    private final static String HEADER = "#ifndef %1$s_BRDF%n#define %1$s_BRDF%n#pragma include includes/misc.h%n";
    private final static String FUNCTION_TEMPLATE = "vec3 %s( vec3 L, vec3 V, vec3 N );%n";
    private final String name, function;
    private final List<Option> options;

    public BRDF(String name, String function, List<Option> options) {
        this.name = name;
        this.function = function;
        this.options = options;
    }

    public String getFunctionTemplate() {
        return String.format(FUNCTION_TEMPLATE, name);
    }

    public String getCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(HEADER, name));

        appendOptions(sb);
        appendFunction(sb);

        sb.append("\n#endif");
        return sb.toString();
    }

    private void appendOptions(StringBuilder sb) {
        for (Option o : options) {
            sb.append(String.format("#ifndef %2$s_%3$s_VAL\n#define %2$s_%3$s_VAL %4$s\n#endif\n"
                                    + "uconst %1$s %2$s_%3$s = %2$s_%3$s_VAL;", o.getType(), name, o.getName(), o.getValue()))
                    .append('\n');
        }
    }

    private void appendFunction(StringBuilder sb) {
        String func = function.replace("BRDF", name).replace(", vec3 X, vec3 Y", "");
        for (Option o : options) {
            Pattern pattern = Pattern.compile("([^\\d\\w_])(" + o.getName() + ")([^\\d\\w_])");
            Matcher matcher = pattern.matcher(func);
            do {
                func = matcher.replaceAll("$1" + name + '_' + o.getName() + "$3");
                matcher = pattern.matcher(func);
            } while (matcher.find());
        }
        sb.append(func).append('\n');
    }

    public static void main(String[] args) throws IOException {

        InputStream stream = BRDF.class.getResourceAsStream("/resources/brdfs/cooktorrance.brdf");
        BRDF r = BrdfReader.readBrdf(stream, "cooktorrance");
        System.out.println(r.getCode());
    }
}
