/*
 * Copyright (C) 2012 daniel
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
package darwin.resourcehandling.dependencies.annotation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import darwin.resourcehandling.handle.ResourceHandle;
import darwin.resourcehandling.factory.*;
import darwin.resourcehandling.handle.FileHandlerFactory;
import darwin.resourcehandling.handle.ResourceBundle;
import darwin.util.dependencies.SimpleMembersInjector;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;

/**
 *
 * @author daniel
 */
public class TypeListener implements com.google.inject.spi.TypeListener {

    private final FileHandlerFactory factory;
    private final ResourceFactory rFactory = null;
    private static final Map<Class, ResourceFromBundleProvider> bundleProvider = new HashMap<>();
    private static final Map<Class, ResourceFromHandleProvider> handleProvider = new HashMap<>();

    static {
        for (ResourceFromBundleProvider prov : ServiceLoader.load(ResourceFromBundleProvider.class)) {
            bundleProvider.put(prov.getType(), prov);
        }
        for (ResourceFromHandleProvider prov : ServiceLoader.load(ResourceFromHandleProvider.class)) {
            handleProvider.put(prov.getType(), prov);
        }
    }

    public TypeListener(FileHandlerFactory factory) {
        this.factory = factory;
    }

    @Override
    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {
        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            InjectResource anno = field.getAnnotation(InjectResource.class);
            if (anno != null) {
                ResourceHandle handle = factory.create(anno.file());
                if (field.getType() == ResourceHandle.class) {
                    aTypeEncounter.register(new SimpleMembersInjector<I>(field, handle));
                } else {
                    ResourceFromHandle from = getHandleFactory(field.getType());
                    if (from != null) {
                        try {
                            aTypeEncounter.register(
                                    new SimpleMembersInjector<I>(field, from.create(handle)));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            InjectBundle anno2 = field.getAnnotation(InjectBundle.class);
            if (anno2 != null) {
                String[] pp = anno2.files();
                if (pp != null) {
                    ResourceHandle[] handles = new ResourceHandle[pp.length];
                    for (int i = 0; i < pp.length; ++i) {
                        handles[i] = factory.create(anno2.prefix() + pp[i].trim());
                    }

                    ResourceBundle bundle = new ResourceBundle(handles, anno2.options());
                    if (field.getType() == ResourceBundle.class) {
                        aTypeEncounter.register(new SimpleMembersInjector<I>(field, bundle));
                    } else {
                        ResourceFromBundle from = getBundleFactory(field.getType());
                        if (from != null) {
                            try {
                                aTypeEncounter.register(
                                        new SimpleMembersInjector<I>(field, from.create(bundle)));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public ResourceFromBundle getBundleFactory(Class c) {
        return (ResourceFromBundle) bundleProvider.get(c).get();
    }

    public ResourceFromHandle getHandleFactory(Class c) {
        return (ResourceFromHandle) handleProvider.get(c).get();
    }
}
