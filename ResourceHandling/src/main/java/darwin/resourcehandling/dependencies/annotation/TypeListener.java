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

import java.lang.reflect.Field;
import java.nio.file.Paths;

import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;
import darwin.util.dependencies.SimpleMembersInjector;

import com.google.inject.*;
import com.google.inject.spi.TypeEncounter;

/**
 *
 * @author daniel
 */
public class TypeListener implements com.google.inject.spi.TypeListener {

    private final Provider<WatchServiceNotifier> provider;
    private final Stage stage;

    public TypeListener(Provider<WatchServiceNotifier> provider, Stage s) {
        this.provider = provider;
        stage = s;
    }

    @Override
    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {
        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            InjectResource anno = field.getAnnotation(InjectResource.class);
            if (field.getType() == ResourceHandle.class && anno != null) {
                aTypeEncounter.register(new SimpleMembersInjector<I>(field,
                                                                     new ClasspathFileHandler(provider.get(),
                                                                                              stage, Paths.get(anno.value()))));
            }
        }
    }
}
