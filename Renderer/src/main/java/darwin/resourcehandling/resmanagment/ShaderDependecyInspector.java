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
package darwin.resourcehandling.resmanagment;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;

import darwin.annotations.ServiceProvider;
import darwin.resourcehandling.ResourceDependecyInspector;
import darwin.resourcehandling.ResourceHandle;
import darwin.resourcehandling.io.ShaderUtil;

import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@ServiceProvider(ResourceDependecyInspector.class)
public class ShaderDependecyInspector implements ResourceDependecyInspector {

    private static String[] extensions = new String[]{"frag", "vert", "geom"};

    @Override
    public Iterable<Path> getDependencys(ResourceHandle resource) {
        ArrayList<Path> dependencys = new ArrayList<>();
        try (InputStream stream = resource.getStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(ShaderUtil.INCLUDE_PREFIX)) {
                    String path = line.substring(ShaderUtil.INCLUDE_PREFIX.length()).trim();
                    dependencys.add(ShaderUtil.SHADER_PATH.resolve(path));
                }
            }
        } catch (IOException ex) {
        }
        return dependencys;
    }

    @Override
    public String[] getSupportedFileTypes() {
        return Arrays.copyOf(extensions, extensions.length);
    }
}
