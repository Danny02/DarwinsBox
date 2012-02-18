/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.resourcehandling;

/**
 *
 * @author daniel
 */
public interface ResourceBuilder<T extends Resource> {

    public ResourceState neededBaseState();

    public T createResource(Resource base) throws WrongBuildArgsException;
}
