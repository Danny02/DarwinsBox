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
package darwin.resourcehandling.factory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.ResourceHandle;
import darwin.resourcehandling.handle.ResourceBundle;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceFactory {

    public <T> ResourceWrapper<T> createResource(ResourceFromHandle<T> factory, ResourceHandle handle) {
        try {
            ResourceWrapper<T> wrapper = new ResourceWrapper<>(factory.create(handle));
            handle.registerChangeListener(new ResourceUpdater(factory, wrapper));
            return wrapper;
        } catch (IOException ex) {
            Logger.getLogger(ResourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            return new ResourceWrapper<>(factory.getFallBack());
        }
    }

    public <T> ResourceWrapper<T> createResource(ResourceFromBundle<T> factory, ResourceBundle handle) {
        ResourceWrapper<T> wrapper;
        boolean fb = false;
        try {
            wrapper = new ResourceWrapper<>(factory.create(handle));
        } catch (IOException ex) {
            Logger.getLogger(ResourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            wrapper = new ResourceWrapper<>(factory.getFallBack());
            fb = true;
        }
        handle.registerChangeListener(new ResourceUpdaterBundle(factory, wrapper, handle, fb));
        return wrapper;
    }

    private class ResourceUpdater<T> implements ResourceChangeListener {

        private final ResourceFromHandle<T> factory;
        private final ResourceWrapper<T> wrapper;

        public ResourceUpdater(ResourceFromHandle<T> factory, ResourceWrapper<T> wrapper) {
            this.factory = factory;
            this.wrapper = wrapper;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            try {
                wrapper.set(factory.create(handle));
            } catch (IOException ex) {
                wrapper.set(factory.getFallBack());
            }
        }
    }

    private class ResourceUpdaterBundle<T> implements ResourceChangeListener {

        private final ResourceFromBundle<T> factory;
        private final ResourceWrapper<T> wrapper;
        private final ResourceBundle bundle;
        private boolean fallback;

        public ResourceUpdaterBundle(ResourceFromBundle<T> factory, ResourceWrapper<T> wrapper, ResourceBundle bundle, boolean fallback) {
            this.factory = factory;
            this.wrapper = wrapper;
            this.bundle = bundle;
            this.fallback = fallback;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            if (fallback) {
                try {
                    wrapper.set(factory.create(bundle));
                    fallback = false;
                } catch (IOException ex) {
                    wrapper.set(factory.getFallBack());
                    fallback = true;
                }
            } else {
                factory.update(bundle, handle, wrapper);
            }
        }
    }
}
