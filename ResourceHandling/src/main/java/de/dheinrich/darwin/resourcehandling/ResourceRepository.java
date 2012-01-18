/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling;

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
