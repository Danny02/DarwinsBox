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
package darwin.util.logging;

import java.lang.reflect.Field;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.*;
import org.slf4j.Logger;

/**
 *
 * @author daniel
 */
public class Slf4jTypeListener implements TypeListener
{

    @Override
    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter)
    {
        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            if (field.getType() == Logger.class
                    && field.isAnnotationPresent(InjectLogger.class)) {
                aTypeEncounter.register(new Slf4jMembersInjector<I>(field));
            }
        }
    }
}
