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
import darwin.resourcehandling.handle.ResourceBundle;
import darwin.resourcehandling.handle.ResourceHandle;

import javax.inject.Singleton;
import sun.org.mozilla.javascript.internal.xml.XMLLib.Factory;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@Singleton
public class ResourceFactory {

    public <T> T createResource(final ResourceFromHandle<T> factory, ResourceHandle handle) {
        try {
            final T r = factory.create(handle);
            handle.registerChangeListener(new ResourceChangeListener() {
                @Override
                public void resourceChanged(ResourceHandle handle) {
                    factory.update(handle, r);
                }
            });
            return r;
        } catch (IOException ex) {
            Logger.getLogger(ResourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            return factory.getFallBack();
        }
    }

    public <T> T createResource(ResourceFromBundle<T> factory, ResourceBundle handle) {
        T wrapper;
        boolean fb = false;
        try {
            wrapper = factory.create(handle);
        } catch (IOException ex) {
            Logger.getLogger(ResourceFactory.class.getName()).log(Level.SEVERE, null, ex);
            wrapper = factory.getFallBack();
            fb = true;
        }
        handle.registerChangeListener(new ResourceUpdater(factory, wrapper, handle, fb));
        return wrapper;
    }

    private class ResourceUpdater<T> implements ResourceChangeListener {

        private final ResourceFromBundle<T> factory;
        private final T wrapper;
        private final ResourceBundle bundle;
        private boolean fallback;

        public ResourceUpdater(ResourceFromBundle<T> factory, T wrapper, ResourceBundle bundle, boolean fallback) {
            this.factory = factory;
            this.wrapper = wrapper;
            this.bundle = bundle;
            this.fallback = fallback;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            if (fallback) {
                try {
                    factory.update(bundle, handle, factory.create(bundle));
                    fallback = false;
                } catch (IOException ex) {
                    factory.update(bundle, handle, factory.getFallBack());
                    fallback = true;
                }
            } else {
                factory.update(bundle, handle, wrapper);
            }
        }
    }
}
