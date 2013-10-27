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
package darwin.resourcehandling.shader;

import java.lang.reflect.Method;

import darwin.annotations.ServiceProvider;
import darwin.renderer.shader.Shader;
import darwin.resourcehandling.factory.*;

import com.google.inject.Injector;
import javax.inject.Singleton;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@Singleton
@ServiceProvider(ResourceFromBundleProvider.class)
public class ShaderLoaderProvider extends ResourceFromBundleProvider<Shader> {

    public ShaderLoaderProvider() {
        super(Shader.class);
    }

    @Override
    public ResourceFromBundle<Shader> get() {
        return getInjector().getInstance(ShaderLoader.class);
    }

    @SuppressWarnings("nullness")
    public static Injector getInjector() {
        try {
            Class<?> client = ClassLoader.getSystemClassLoader().loadClass("darwin.core.gui.Client");
            Method declaredMethod = client.getDeclaredMethod("getInjector", boolean.class);
            return (Injector) declaredMethod.invoke(null, false);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException("ShaderLoaderProvider: can't find app injector!\n"+ex.getMessage());
        }
    }
}
