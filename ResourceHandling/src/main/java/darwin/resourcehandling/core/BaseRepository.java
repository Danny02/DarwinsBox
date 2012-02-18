/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.resourcehandling.core;

import java.util.HashMap;
import java.util.Map;

import darwin.resourcehandling.*;



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
        resources.put(new BaseResourceState(handle), handle);
    }

    @Override
    public <T extends Resource> void loadResourceAsync(ResourceState<T> state, ResourceHandle handle, ResourceListener<T> listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
