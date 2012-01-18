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
package de.dheinrich.darwin.ressourcen;

import com.jogamp.opengl.util.texture.Texture;
import de.dheinrich.darwin.resourcehandling.Resource;
import de.dheinrich.darwin.resourcehandling.ResourceHandle;
import de.dheinrich.darwin.resourcehandling.ResourceRepository;
import de.dheinrich.darwin.resourcehandling.test.StreamHandle;

/**
 *
 * @author daniel
 */
public class OpenGLTexture implements Resource {

    private final Texture tex;
    private final ResourceHandle handle;

    public OpenGLTexture(Texture t, ResourceHandle h) {
        tex = t;
        handle = h;
    }

    public Texture getTex() {
        return tex;
    }

    @Override
    public ResourceHandle getHandle() {
        return handle;
    }

    public static OpenGLTexture load(String path, ResourceRepository repository)
    {
        return repository.loadResource(new OpenGLTextureState(), new StreamHandle(path));
    }
}
