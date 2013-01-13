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
package darwin.util.dependencies;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;

/**
 *
 * @author daniel
 */
public class SimpleMembersInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Object value;

    public SimpleMembersInjector(Field field, Object value) {
        this.field = field;
        this.value = value;
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T anArg0) {
        try {
            field.set(anArg0, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
