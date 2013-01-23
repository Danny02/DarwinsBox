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
package darwin.resourcehandling;

import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceTupel {

    
    public final String className;
    public final Path path;

    public ResourceTupel(Path path,  String className) {
        this.path = path;
        this.className = className;
    }

    @Override
    @SuppressWarnings("nullness")
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.path);
        hash = 47 * hash + Objects.hashCode(this.className);
        return hash;
    }

    @Override
    @SuppressWarnings("nullness")
    public boolean equals( Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceTupel other = (ResourceTupel) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        return true;
    }
}
