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
package darwin.util.dependencies;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;
import javax.inject.Provider;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class AsyncMemberInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Provider<T> prov;

    public AsyncMemberInjector(Field field, Provider<T> prov) {
        this.field = field;
        field.setAccessible(true);
        this.prov = prov;
    }

    @Override
    public void injectMembers(T instance) {
        try {
            field.set(instance, prov.get());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
