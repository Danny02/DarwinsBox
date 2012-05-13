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
package darwin.resourcehandling.resmanagment.texture;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.jogamp.opengl.util.texture.*;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.GraphicContext;
import darwin.resourcehandling.io.TextureUtil;
import darwin.resourcehandling.resmanagment.ResourcesLoader;
import darwin.util.logging.InjectLogger;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class HeightMapLoadJob extends TextureLoadJob
{
    public interface HeightMapFactory
    {
        public HeightMapLoadJob create(String path);
    }

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final ResourcesLoader loader;

    @AssistedInject
    public HeightMapLoadJob(GraphicContext gc, ResourcesLoader loader,
            TextureUtil util, @Assisted String path)
    {
        super(gc, util, path, -1, -1);
        this.loader = loader;
    }

    @Override
    public Texture load()
    {
        Texture re = null;
        try (InputStream is = loader.getRessource(getPath());) {
            String[] suffix = getPath().split("\\.");
            TextureData td = TextureIO.newTextureData(gc.getGL().getGLProfile(), is,
                    GL2GL3.GL_LUMINANCE_FLOAT32_ATI,
                    GL2GL3.GL_LUMINANCE, true,
                    suffix[suffix.length - 1]);


            re = TextureIO.newTexture(td);
            util.setTexturePara(re, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
            tcontainer.setTexture(re);
        } catch (IOException ex) {
            logger.error("Heigthmap {} konnte nicht geladen werden.\n({})",
                    getPath(), ex.getLocalizedMessage());
        }

        return re;
    }
}
