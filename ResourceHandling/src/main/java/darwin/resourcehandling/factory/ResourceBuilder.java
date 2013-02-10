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

import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.handle.*;

import javax.inject.Singleton;
import org.slf4j.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@Singleton
public class ResourceBuilder {

    private Logger logger = LoggerFactory.getLogger(ResourceBuilder.class);

    @SuppressWarnings("nullness")
    public <F extends ChangeableResource, T> T createResource(ResourceFrom<F, T> factory, F from) {
        T wrapper;
        try {
            long time = System.currentTimeMillis();
            wrapper = factory.create(from);
            time = System.currentTimeMillis() - time;
            logger.info("Loaded following resource in " + time + "ms (" + from.toString() + ')');
        } catch (Throwable ex) {
            ex.printStackTrace();
            wrapper = factory.getFallBack();
        }

        ResourceChangeListener l = null;
        if (factory instanceof ResourceFromBundle) {
            l = new ResourceBundleUpdater<>((ResourceFromBundle<T>) factory, wrapper, (ResourceBundle) from);
        } else if (factory instanceof ResourceFromHandle) {
            l = new ResourceHandleUpdater<>((ResourceFromHandle<T>) factory, wrapper);
        }
        if (l != null) {
            from.registerChangeListener(l);
        }
        return wrapper;
    }

    private class ResourceHandleUpdater<T> implements ResourceChangeListener {

        private final ResourceFromHandle<T> factory;
        private final T wrapper;

        public ResourceHandleUpdater(ResourceFromHandle<T> factory, T wrapper) {
            this.factory = factory;
            this.wrapper = wrapper;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            factory.update(handle, wrapper);
        }
    }

    private class ResourceBundleUpdater<T> implements ResourceChangeListener {

        private final ResourceFromBundle<T> factory;
        private final T wrapper;
        private final ResourceBundle bundle;

        public ResourceBundleUpdater(ResourceFromBundle<T> factory, T wrapper, ResourceBundle bundle) {
            this.factory = factory;
            this.wrapper = wrapper;
            this.bundle = bundle;
        }

        @Override
        public void resourceChanged(ResourceHandle handle) {
            factory.update(bundle, handle, wrapper);
        }
    }
}
