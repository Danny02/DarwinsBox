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
package darwin.geometrie.io.obj;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

import darwin.annotations.ServiceProvider;
import darwin.geometrie.unpacked.Model;
import darwin.resourcehandling.ResourceDependecyInspector;
import darwin.resourcehandling.handle.ClasspathFileHandler;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@ServiceProvider(ResourceDependecyInspector.class)
public class ObjDependencyInspector extends ResourceDependecyInspector {

    public ObjDependencyInspector() {
        super(new Model[0].getClass());
    }

    @Override
    public Iterable<Path> getDependencys(ClasspathFileHandler resource) {
        ArrayList<Path> dependencys = new ArrayList<>();
        if (getFileExtension(resource.getName()).equalsIgnoreCase("obj")) {
            try (InputStream stream = resource.getStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith(ObjFileParser.MATERIAL_LIB)) {
                        String path = line.substring(ObjFileParser.MATERIAL_LIB.length()).trim();
                        dependencys.add(resource.resolve(path).getPath());
                    }
                }
            } catch (IOException ex) {
            }
        }
        return dependencys;
    }

    private String getFileExtension(String fn) {
        String[] parts = fn.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        } else {
            return "";
        }
    }
}
