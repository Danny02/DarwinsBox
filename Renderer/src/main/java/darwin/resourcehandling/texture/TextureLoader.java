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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import darwin.renderer.GraphicContext;
import darwin.resourcehandling.factory.ResourceFromHandle;
import darwin.resourcehandling.handle.ResourceHandle;

import com.jogamp.common.util.IOUtil;
import com.jogamp.opengl.util.texture.*;
import javax.inject.Inject;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class TextureLoader implements ResourceFromHandle<Texture> {

    public static final String TEXTURE_PATH_PREFIX = "resources/textures/";
    public static final Path TEXTURE_PATH = Paths.get(TEXTURE_PATH_PREFIX);
    private final GraphicContext gc;

    @Inject
    public TextureLoader(GraphicContext gc) {
        this.gc = gc;
    }

    @Override
    public Texture create(final ResourceHandle handle) throws IOException {
        final Texture[] a = new Texture[1];
        gc.invoke(true, new GLRunnable() {
            @Override
            public boolean run(GLAutoDrawable glad) {
                try {
                    a[0] = TextureIO.newTexture(handle.getStream(), true, IOUtil.getFileSuffix(handle.getName()));
                } catch (IOException ex) {
                }
                return true;
            }
        });
        return a[0];
    }

    @Override
    public void update(ResourceHandle changed, final Texture old) {
        TextureData a = null;
        try {
            a = TextureIO.newTextureData(gc.getGL().getGLProfile(), changed.getStream(),
                                         true, IOUtil.getFileSuffix(changed.getName()));
        } catch (IOException ex) {
        }
        
        final TextureData data = a;
        gc.invoke(false, new GLRunnable() {
            @Override
            public boolean run(GLAutoDrawable glad) {
                old.updateImage(glad.getGL(), data);
                return true;
            }
        });
    }

    @Override
    public Texture getFallBack() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
