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

/**
 *
 * @author daniel
 */
public class MembersInjector<T> implements com.google.inject.MembersInjector<T>
{

    private final Field field;
    private final ResourceHandle handle;

    MembersInjector(Field field, String resourceString, WatchServiceNotifier notifier) {
        this.field = field;
        handle = new ClasspathFileHandler(notifier, Paths.get(resourceString));
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T anArg0)
    {
        try {
            field.set(anArg0, handle);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
