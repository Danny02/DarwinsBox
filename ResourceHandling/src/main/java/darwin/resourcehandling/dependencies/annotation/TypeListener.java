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

import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.watchservice.WatchServiceNotifier;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;

/**
 *
 * @author daniel
 */
public class TypeListener implements com.google.inject.spi.TypeListener {

    private final Provider<WatchServiceNotifier> provider;

    public TypeListener(Provider<WatchServiceNotifier> provider) {
        this.provider = provider;
    }

    @Override
    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {
        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            InjectResource anno = field.getAnnotation(InjectResource.class);
            if (field.getType() == ResourceHandle.class && anno != null) {
                aTypeEncounter.register(new MembersInjector<I>(field, anno.value(), provider.get()));
            }
        }
    }
}
