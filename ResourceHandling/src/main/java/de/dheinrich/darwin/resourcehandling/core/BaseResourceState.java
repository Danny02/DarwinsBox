/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.darwin.resourcehandling.core;

import de.dheinrich.darwin.resourcehandling.*;

/**
 *
 * @author daniel
 */
public class BaseResourceState<T extends ResourceHandle> implements ResourceState<T>, ResourceBuilder<T> {
    private final T handle;

    public BaseResourceState(T handle) {
        this.handle = handle;
    }

    @Override
    public ResourceBuilder<T> getBuilder() {
        return this;
    }

    @Override
    public ResourceState neededBaseState() {
        return null;
    }

    @Override
    public T createResource(Resource base) throws WrongBuildArgsException {
        return handle;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 5;
        return hash;
    }
}
