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
package darwin.resourcehandling.cache;


import darwin.resourcehandling.factory.ChangeableResource;
import darwin.resourcehandling.factory.ResourceBuilder;
import darwin.resourcehandling.factory.ResourceFrom;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class MapResourceCache implements ResourceCache {
    private static class FHP<F extends ChangeableResource, T> {
        private final ResourceFrom<F, T> factory;
        private final F handle;

        private FHP(ResourceFrom<F, T> factory, F handle) {
            this.factory = factory;
            this.handle = handle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FHP fhp = (FHP) o;

            if (!factory.equals(fhp.factory)) return false;
            if (!handle.equals(fhp.handle)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = factory.hashCode();
            result = 31 * result + handle.hashCode();
            return result;
        }
    }

    private static class Wrapper<F extends ChangeableResource, T> {

        private final Map<FHP<F, T>, T> resources = new HashMap<>();
    }

    private final Wrapper wrapper = new Wrapper<>();
    private final ResourceBuilder builder = new ResourceBuilder();

    @Override
    public <F extends ChangeableResource, T> T get(ResourceFrom<F, T> factory, F from, boolean unique) {
        if (unique) {
            return builder.createResource(factory, from);
        } else {
            FHP<F, T> key = new FHP<>(factory, from);
            Wrapper<F, T> w = wrapper;
            T r = w.resources.get(key);
            if (r != null) {
                return r;
            } else {
                T rr = builder.createResource(factory, from);
                w.resources.put(key, rr);
                return rr;
            }
        }
    }
}
