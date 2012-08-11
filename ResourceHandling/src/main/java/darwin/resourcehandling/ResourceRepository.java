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
package darwin.resourcehandling;

import darwin.resourcehandling.core.Resource;
import darwin.resourcehandling.core.ResourceHandle;

/**
 *
 * @author daniel
 */
public interface ResourceRepository {

    public <T extends Resource> T loadResource(ResourceState<T> state,
            ResourceHandle handle);

    public <T extends Resource> void loadResourceAsync(ResourceState<T> state,
            ResourceHandle handle,
            ResourceListener<T> listener);
}
