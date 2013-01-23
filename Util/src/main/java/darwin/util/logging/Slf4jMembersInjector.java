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

import com.google.inject.MembersInjector;
import org.slf4j.*;

/**
 *
 * @author daniel
 */
public class Slf4jMembersInjector<T> implements MembersInjector<T>
{

    private final Field field;
    private final Logger logger;

    public Slf4jMembersInjector(Field aField)
    {
        field = aField;
        logger = LoggerFactory.getLogger(field.getDeclaringClass());
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T anArg0)
    {
        try {
            field.set(anArg0, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
