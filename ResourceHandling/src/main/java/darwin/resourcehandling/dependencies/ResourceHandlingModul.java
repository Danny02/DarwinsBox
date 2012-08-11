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

import darwin.resourcehandling.handle.ClasspathFileHandler.FileHandlerFactory;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 * @author daniel
 */
public class ResourceHandlingModul extends AbstractModule {

    @Override
    protected void configure() {
        //TODO introduce annotation processor for automatic factory interface creation of @AssistedInject constructors
        Class[] factoryClasses = new Class[]{
            FileHandlerFactory.class,};

        for (Class factory : factoryClasses) {
            install(new FactoryModuleBuilder().build(factory));
        }
        
        //TODO either do the thread starting in the DEBUG modul, or just start the thread manually
        //on an injected instance in the main class
        bind(WatchServiceNotifier.class).toProvider(IniWatchServiceProvider.class).in(Scopes.SINGLETON);
    }
}
