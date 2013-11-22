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
package darwin.resourcehandling.texture;

import darwin.annotations.ServiceProvider;
import darwin.resourcehandling.factory.ResourceFromHandleProvider;

import com.jogamp.opengl.util.texture.Texture;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@ServiceProvider(ResourceFromHandleProvider.class)
public class TextureLoaderProvider extends ResourceFromHandleProvider<Texture> {

    public TextureLoaderProvider() {
        super(Texture.class);
    }

    @Override
    public TextureLoader get(String[] options) {
        return ShaderLoaderProvider.getInjector().getInstance(TextureLoader.class);
    }
}
