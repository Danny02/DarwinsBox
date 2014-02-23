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
import java.net.*;

import darwin.resourcehandling.cache.ResourceCache;
import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.factory.*;
import darwin.resourcehandling.handle.ResourceBundle;
import darwin.resourcehandling.handle.*;
import darwin.util.dependencies.*;

import com.google.common.base.*;
import com.google.inject.MembersInjector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.*;
import javax.inject.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@Singleton
public class ResourceInjector {

    private final FileHandleCache fileCache;
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
        this.fileCache = factory;
        this.cache = cache;
    }

    @SuppressWarnings("nullness")
    public void injectResources(Object object) {
        Class clz = object.getClass();
        Iterable<MembersInjector> inj = injectors.get(clz);
        if (inj == null) {
            inj = retriveMemberInjectors(clz);
            injectors.put(clz, inj);
        }

        for (MembersInjector<Object> mi : inj) {
            mi.injectMembers(object);
        }
    }

//    public <T> T get(ResourceFromHandle<T> loader, String resourcePath) {
//        try {
//            return get(loader, new URI(resourcePath));
//        } catch (URISyntaxException ex) {
//            throw new RuntimeException(ex);
//        }
//    }

    public <T> T get(ResourceFromHandle<T> loader, URI resource) {
        ResourceHandle get = fileCache.get(resource);
        return cache.get(loader, get, false);
    }

    public <T> T get(ResourceFromBundle<T> loader, ResourceBundle bundle) {
        return cache.get(loader, bundle, false);
    }

    Iterable<MembersInjector> retriveMemberInjectors(Class c) {
        ArrayList<MembersInjector> list = new ArrayList<>();
        for (Field field : c.getDeclaredFields()) {
            retriveHandleInections(field, list);
            retriveBundleInjections(field, list);
        }
        return list;
    }

    @SuppressWarnings("nullness")
    public static Optional<ResourceFromBundle<Object>> getBundleFactory(Class c) {
        Optional<ResourceFromBundleProvider> o = Optional.fromNullable(bundleProvider.get(c));
        return o.transform(new Function<ResourceFromBundleProvider, ResourceFromBundle<Object>>() {
            @Override
            public ResourceFromBundle apply(ResourceFromBundleProvider input) {
                //generic bullshit
                return ((ResourceFromBundleProvider<?>) input).get();
            }
        });
    }

    @SuppressWarnings("nullness")
    public static Optional<ResourceFromHandle<Object>> getHandleFactory(Class c, final String[] options) {
        Optional<ResourceFromHandleProvider> o = Optional.fromNullable(handleProvider.get(c));
        return o.transform(new Function<ResourceFromHandleProvider, ResourceFromHandle<Object>>() {
            @Override
            public ResourceFromHandle apply(ResourceFromHandleProvider input) {
                return input.get(options);
            }
        });
    }

    private void retriveHandleInections(Field field, ArrayList<MembersInjector> list) {
        final boolean unique = field.getAnnotation(Unique.class) != null;
        InjectResource anno = field.getAnnotation(InjectResource.class);
        if (anno != null) {
            final ResourceHandle handle = getHandleFromString(anno.file());
            if (field.getType() == ResourceHandle.class) {
                list.add(new SimpleMembersInjector(field, handle));
            } else {
                final Optional<ResourceFromHandle<Object>> factory = getHandleFactory(field.getType(), anno.options());
                if (factory.isPresent()) {
                    list.add(new AsyncMemberInjector(field, new Provider<Object>() {
                        @Override
                        public Object get() {
                            return cache.get(factory.get(), handle, unique);
                        }
                    }));
                }
            }
        }
    }

    private void retriveBundleInjections(Field field, ArrayList<MembersInjector> list) {
        final boolean unique = field.getAnnotation(Unique.class) != null;
        InjectBundle anno2 = field.getAnnotation(InjectBundle.class);
        if (anno2 != null) {
            String[] pp = anno2.files();
            ResourceHandle[] handles = new ResourceHandle[pp.length];
            for (int i = 0; i < pp.length; ++i) {
                handles[i] = getHandleFromString(anno2.prefix() + pp[i].trim());
            }

            @SuppressWarnings("nullness")
            final ResourceBundle bundle = new ResourceBundle(handles, anno2.options());

            if (field.getType() == ResourceBundle.class) {
                list.add(new SimpleMembersInjector(field, bundle));
            } else {
                final Optional<ResourceFromBundle<Object>> factory = getBundleFactory(field.getType());
                if (factory.isPresent()) {
                    list.add(new AsyncMemberInjector(field, new Provider<Object>() {
                        @Override
                        public Object get() {
                            return cache.get(factory.get(), bundle, unique);
                        }
                    }));
                }
            }

        }
    }

    private ResourceHandle getHandleFromString(String path) {
        try {
            return fileCache.get(new URI(path));
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
