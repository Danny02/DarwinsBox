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
package darwin.resourcehandling.dependencies;

import java.lang.reflect.Field;
import java.util.*;

import darwin.resourcehandling.cache.ResourceCache;
import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.factory.*;
import darwin.resourcehandling.handle.ResourceBundle;
import darwin.resourcehandling.handle.*;
import darwin.util.dependencies.*;

import com.google.inject.MembersInjector;
import javax.inject.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@Singleton
public class ResourceInjector {

    private final FileHandleCache factory;
    private final ResourceCache cache;
    private final Map<Class, Iterable<MembersInjector>> injectors = new HashMap<>();
    private static final Map<Class, ResourceFromBundleProvider> bundleProvider;
    private static final Map<Class, ResourceFromHandleProvider> handleProvider;

    static {
        HashMap<Class, ResourceFromBundleProvider> map = new HashMap<>();
        for (ResourceFromBundleProvider prov : ServiceLoader.load(ResourceFromBundleProvider.class)) {
            map.put(prov.getType(), prov);
        }
        HashMap<Class, ResourceFromHandleProvider> map1 = new HashMap<>();
        for (ResourceFromHandleProvider prov : ServiceLoader.load(ResourceFromHandleProvider.class)) {
            map1.put(prov.getType(), prov);
        }
        bundleProvider = Collections.unmodifiableMap(map);
        handleProvider = Collections.unmodifiableMap(map1);
    }

    @Inject
    public ResourceInjector(FileHandleCache factory, ResourceCache cache) {
        this.factory = factory;
        this.cache = cache;
    }

    public <T> void injectResources(T object) {
        Class clz = object.getClass();
        Iterable<MembersInjector> inj = injectors.get(clz);
        if (inj == null) {
            inj = retriveMemberInjectors(clz);
            injectors.put(clz, inj);
        }

        for (MembersInjector mi : inj) {
            mi.injectMembers(object);
        }
    }
    
    public <T> T get(ResourceFromHandle<T> loader, String resourceName)
    {
        ResourceHandle get = factory.get(resourceName);
        return cache.get(loader, get, false);
    }
    
    public <T> T get(ResourceFromBundle<T> loader, ResourceBundle bundle)
    {
        return cache.get(loader, bundle, false);
    }

    <T> Iterable<MembersInjector<T>> retriveMemberInjectors(Class<T> c) {
        ArrayList<MembersInjector<T>> list = new ArrayList<>();
        for (Field field : c.getDeclaredFields()) {
            retriveHandleInections(field, list);
            retriveBundleInjections(field, list);
        }
        return list;
    }

    public static ResourceFromBundle getBundleFactory(Class c) {
        ResourceFromBundleProvider prov = bundleProvider.get(c);
        if (prov == null) {
            throw new RuntimeException("No ResourceLoaderProvider found for the resource " + c);
        }
        return (ResourceFromBundle) prov.get();
    }

    public static ResourceFromHandleProvider getHandleFactory(Class c) {
        ResourceFromHandleProvider prov = handleProvider.get(c);
        if (prov == null) {
            throw new RuntimeException("No ResourceLoaderProvider found for the resource " + c);
        }
        return prov;
    }

    private <T> void retriveHandleInections(Field field, ArrayList<MembersInjector<T>> list) {
        final boolean unique = field.getAnnotation(Unique.class) != null;
        InjectResource anno = field.getAnnotation(InjectResource.class);
        if (anno != null) {
            final ResourceHandle handle = factory.get(anno.file());
            if (field.getType() == ResourceHandle.class) {
                list.add(new SimpleMembersInjector<T>(field, handle));
            } else {
                final ResourceFromHandle from = getHandleFactory(field.getType()).get(anno.options());
                if (from != null) {
                    list.add(new AsyncMemberInjector<>(field, new Provider<T>() {
                        @Override
                        public T get() {
                            return (T) cache.get(from, handle, unique);
                        }
                    }));
                }
            }
        }
    }

    private <T> void retriveBundleInjections(Field field, ArrayList<MembersInjector<T>> list) {
        final boolean unique = field.getAnnotation(Unique.class) != null;
        InjectBundle anno2 = field.getAnnotation(InjectBundle.class);
        if (anno2 != null) {
            String[] pp = anno2.files();
            if (pp != null) {
                ResourceHandle[] handles = new ResourceHandle[pp.length];
                for (int i = 0; i < pp.length; ++i) {
                    handles[i] = factory.get(anno2.prefix() + pp[i].trim());
                }

                final ResourceBundle bundle = new ResourceBundle(handles, anno2.options());
                if (field.getType() == ResourceBundle.class) {
                    list.add(new SimpleMembersInjector<T>(field, bundle));
                } else {
                    final ResourceFromBundle<T> from = getBundleFactory(field.getType());
                    if (from != null) {
                        list.add(new AsyncMemberInjector<>(field, new Provider<T>() {
                            @Override
                            public T get() {
                                return cache.get(from, bundle, unique);
                            }
                        }));
                    }
                }
            }
        }
    }
}
