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
package darwin.resourcehandling.core.impl;

import java.util.HashMap;
import java.util.Map;

import darwin.resourcehandling.*;
import darwin.resourcehandling.core.Resource;
import darwin.resourcehandling.core.ResourceHandle;



/**
 *
 * @author daniel
 */
public class BaseRepository implements ResourceRepository {

    private final Map<ResourceHandle, Map<ResourceState, Resource>> libary = new HashMap<>();

    @Override
    public <T extends Resource> T loadResource(ResourceState<T> state, ResourceHandle handle) {
        assert handle != null;
        if (state == null) {
            return null;
        }

        Map<ResourceState, Resource> resources = libary.get(handle);
        if (resources == null) {
            resources = new HashMap<>();
            iniHandle(handle, resources);
            libary.put(handle, resources);
        } else {
            Resource r = resources.get(state);
            if (r != null) {
                return (T) r;
            }
        }

        ResourceBuilder<T> builder = state.getBuilder();
        ResourceState base = builder.neededBaseState();

        Resource baseResource = loadResource(base, handle);

        T r = builder.createResource(baseResource);
        resources.put(state, r);
        return r;
    }

    private void iniHandle(ResourceHandle handle, Map<ResourceState, Resource> resources) {
//        resources.put(new BaseResourceState(handle), handle);
    }

    @Override
    public <T extends Resource> void loadResourceAsync(ResourceState<T> state, ResourceHandle handle, ResourceListener<T> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

