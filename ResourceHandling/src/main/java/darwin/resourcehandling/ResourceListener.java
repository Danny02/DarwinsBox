/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.resourcehandling;

/**
 *
 * @author daniel
 */
public interface ResourceListener<T extends Resource> {

    public void resourceLoaded(T resource);
}
