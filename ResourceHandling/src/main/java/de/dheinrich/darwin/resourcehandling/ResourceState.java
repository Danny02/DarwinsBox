/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling;

/**
 *
 * @author daniel
 */
public interface ResourceState<T extends Resource>
{
    public ResourceBuilder<T> getBuilder();
}
