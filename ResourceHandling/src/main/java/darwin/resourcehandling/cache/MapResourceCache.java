package darwin.resourcehandling.cache;

import java.util.*;
import darwin.resourcehandling.factory.*;
import darwin.resourcehandling.handle.*;
import darwin.resourcehandling.handle.ResourceBundle;

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
/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class MapResourceCache implements ResourceCache {

    private final Map<Object, Map> resources = new HashMap<>();
    private final ResourceFactory con = new ResourceFactory();

    @Override
    public <T> T get(ResourceFromBundle<T> factory, ResourceBundle bundle, boolean unique) {
        if (unique) {
            return con.createResource(factory, bundle);
        } else {
            Map m = resources.get(factory);
            if (m == null) {
                m = new HashMap();
                resources.put(factory, m);
            }
            T r = (T) m.get(bundle);
            if (r == null) {
                r = con.createResource(factory, bundle);
                m.put(bundle, r);
            }
            return r;
        }
    }

    @Override
    public <T> T get(ResourceFromHandle<T> factory, ResourceHandle bundle, boolean unique) {
        if (unique) {
            return con.createResource(factory, bundle);
        } else {
            Map m = resources.get(factory);
            if (m == null) {
                m = new HashMap();
                resources.put(factory, m);
            }
            T r = (T) m.get(bundle);
            if (r == null) {
                r = con.createResource(factory, bundle);
                m.put(bundle, r);
            }
            return r;
        }
    }
}
