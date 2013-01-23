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

import java.util.Collection;

import darwin.resourcehandling.relative.RelativeFileFactory;


/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public interface ResourceProcessor {

    /**
     *
     * @param resource The resources which should get processed
     * @param filer
     * @return a optional list of resources which aren't needed any more and can
     * be deleted from the build
     */
    public Collection<ResourceTupel> process(Collection<ResourceTupel> resource, RelativeFileFactory filer);

    /**
     *
     * @return list of resource types for which this processor should be used
     * null if for every type
     */
     
    public Class[] supportedResourceTypes();

    /**
     *
     * @return list of file extensions for which this processor should be used
     * null if for every extension
     */
     
    public String[] supportedFileExtensions();
}
