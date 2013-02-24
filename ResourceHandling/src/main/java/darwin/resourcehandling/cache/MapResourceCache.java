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


import darwin.resourcehandling.factory.*;

import com.google.common.collect.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class MapResourceCache implements ResourceCache {

    private static class Wrapper<F extends ChangeableResource, T> {

        private final Table<ResourceFrom<F, T>, F, T> resources = HashBasedTable.create();
    }
    private final Wrapper wrapper = new Wrapper<>();
    private final ResourceBuilder builder = new ResourceBuilder();

    @Override
    public <F extends ChangeableResource, T> T get(ResourceFrom<F, T> factory, F from, boolean unique) {
        if (unique) {
            return builder.createResource(factory, from);
        } else {
            Wrapper<F, T> w = wrapper;
            T r = w.resources.get(factory, from);
            if (r != null) {
                return r;
            } else {
                T rr = builder.createResource(factory, from);
                w.resources.put(factory, from, rr);
                return rr;
            }
        }
    }
}
