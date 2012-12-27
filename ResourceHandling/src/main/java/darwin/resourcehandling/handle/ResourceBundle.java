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
package darwin.resourcehandling.handle;

import java.util.*;

import darwin.resourcehandling.ResourceChangeListener;
import darwin.util.misc.ArrayIterator;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceBundle implements Iterable<ResourceHandle> {

    private final ResourceHandle[] handles;
    private final String[] options;

    public ResourceBundle(ResourceHandle[] handles, String... options) {
        this.handles = handles;
        this.options = options;
    }
    
    public ResourceBundle merge(String... op)
    {
        String[] osp = Arrays.copyOf(options, options.length+op.length);
        System.arraycopy(op, 0, osp, options.length, op.length);
        return new ResourceBundle(handles, osp);
    }

    public void registerChangeListener(ResourceChangeListener listener) {
        for (ResourceHandle h : handles) {
            h.registerChangeListener(listener);
        }
    }

    public ResourceHandle get(int i) {
        return handles[i];
    }

    public int getCount() {
        return handles.length;
    }
    
    public String[] getOptions()
    {
        return Arrays.copyOf(options, options.length);
    }

    @Override
    public Iterator<ResourceHandle> iterator() {
        return new ArrayIterator(handles);
    }

    @Override
    public String toString() {
        String name = "";
        for (int i = 0; i < getCount(); i++) {
            name += get(i).getName() + "; ";
        }
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Arrays.deepHashCode(this.handles);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceBundle other = (ResourceBundle) obj;
        if (!Arrays.deepEquals(this.handles, other.handles)) {
            return false;
        }
        return true;
    }
}