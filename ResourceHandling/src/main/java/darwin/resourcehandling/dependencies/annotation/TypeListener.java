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

import darwin.resourcehandling.ResourceHandle;
import darwin.resourcehandling.handle.*;
import darwin.util.dependencies.SimpleMembersInjector;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;

/**
 *
 * @author daniel
 */
public class TypeListener implements com.google.inject.spi.TypeListener {

    private final FileHandlerFactory factory;

    public TypeListener(FileHandlerFactory factory) {
        this.factory = factory;
    }

    @Override
    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {
        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            InjectResource anno = field.getAnnotation(InjectResource.class);
            if (field.getType() == ResourceHandle.class && anno != null) {
                aTypeEncounter.register(new SimpleMembersInjector<I>(field, get(anno.prefix() + anno.value())));
            }

            InjectBundle anno2 = field.getAnnotation(InjectBundle.class);
            if (field.getType() == ResourceBundle.class && anno != null) {
                String pp = anno2.value();
                if (pp != null) {
                    String[] paths = pp.split(",");
                    ResourceHandle[] handles = new ResourceHandle[paths.length];
                    for (int i = 0; i < paths.length; ++i) {
                        handles[i] = get(anno2.prefix() + paths[i].trim());
                    }

                    ResourceBundle bundle = new ResourceBundle(handles);
                    aTypeEncounter.register(new SimpleMembersInjector<I>(field, bundle));
                }
            }
        }
    }

    private ClasspathFileHandler get(String p) {
        return factory.create(Paths.get(p));
    }
}
