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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;

import darwin.resourcehandling.ResourceHandle;
import darwin.resourcehandling.dependencies.annotation.TypeListener;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.FileHandlerFactory;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.AbstractModule;
import com.google.inject.Stage;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 *
 * @author daniel
 */
public class ResourceHandlingModul extends AbstractModule {

    private final Class[] resourceDescriptionClasses;
    private final WatchServiceNotifier watcher = new WatchServiceNotifier();

    public ResourceHandlingModul(Class... resourceDescriptionClasses) {
        this.resourceDescriptionClasses = resourceDescriptionClasses;
    }

    @Override
    protected void configure() {
        //TODO introduce annotation processor for automatic factory interface creation of @AssistedInject constructors
        Class[] factoryClasses = new Class[]{};

        for (Class factory : factoryClasses) {
            install(new FactoryModuleBuilder().build(factory));
        }

        bindResourceClasses();

        boolean hotReload = Stage.DEVELOPMENT == currentStage();
        bind(boolean.class).annotatedWith(Names.named("HOT_RELOAD")).toInstance(hotReload);

        if (hotReload) {
            watcher.createNotifierThread().start();
        }
        bind(WatchServiceNotifier.class).toInstance(watcher);

        FileHandlerFactory factory = new FileHandlerFactory(watcher, currentStage());
        bind(FileHandlerFactory.class).toInstance(factory);


        //TODO a little hack to get the InjectResource/InjectBundle working, because while injection time otherwise
        //no watchservice instance is reachable  
        bindListener(Matchers.any(), new TypeListener(factory));
    }

    private ClasspathFileHandler getResource(String file) {
        return new ClasspathFileHandler(watcher, currentStage(), Paths.get(file));
    }

    private void bindResourceClasses() {
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
                                toInstance(getResource(resourcePath));
                    } catch (IllegalAccessException ex) {
                        //TODO log a waring here maybe
                    }
                }
            }
        }
    }
}
