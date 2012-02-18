/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.resourcehandling;

/**
 *
 * @author daniel
 */
public interface ResourceState<T extends Resource>
{
    public ResourceBuilder<T> getBuilder();
}
