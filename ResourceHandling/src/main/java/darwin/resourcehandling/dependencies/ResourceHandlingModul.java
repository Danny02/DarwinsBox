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
package darwin.resourcehandling.dependencies;

import java.lang.reflect.*;
import java.nio.file.Paths;

import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.dependencies.annotation.TypeListener;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.ClasspathFileHandler.FileHandlerFactory;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 *
 * @author daniel
 */
public class ResourceHandlingModul extends AbstractModule {

    private Class[] resourceDescriptionClasses;
    private final Provider<WatchServiceNotifier> wsProvider = new SingeltonWatchServiceProvider();

    public ResourceHandlingModul(Class... resourceDescriptionClasses) {
        this.resourceDescriptionClasses = resourceDescriptionClasses;
    }

    @Override
    protected void configure() {
        //TODO introduce annotation processor for automatic factory interface creation of @AssistedInject constructors
        Class[] factoryClasses = new Class[]{
            FileHandlerFactory.class,};

        for (Class factory : factoryClasses) {
            install(new FactoryModuleBuilder().build(factory));
        }



        //TODO a little hack to get the InjectResource working, because while injection time otherwise
        //no watchservice instance is reachable        

        for (Class c : resourceDescriptionClasses) {
            for (Field f : c.getFields()) {
                int m = f.getModifiers();
                if ((m & Modifier.STATIC) > 0 && (m & Modifier.FINAL) > 0
                    && f.getType() == String.class) {
                    try {
                        String resourcePath = (String) f.get(null);
                        bind(ResourceHandle.class).annotatedWith(Names.named(resourcePath)).
                                toInstance(getResource(resourcePath));
                        bind(ClasspathFileHandler.class).annotatedWith(Names.named(resourcePath)).
//                                toProvider(null).in(Singleton.class);
                                toInstance(getResource(resourcePath));
                    } catch (IllegalAccessException ex) {
                        //TODO log a waring here maybe
                    }
                }
            }
        }

        //TODO either do the thread starting in the DEBUG modul, or just start the thread manually
        //on an injected instance in the main class
        bind(WatchServiceNotifier.class).toProvider(wsProvider).in(Scopes.SINGLETON);

        bindListener(Matchers.any(), new TypeListener(wsProvider));
    }

    private ClasspathFileHandler getResource(String file) {
        return new ClasspathFileHandler(wsProvider.get(), Paths.get(file));
    }
}
