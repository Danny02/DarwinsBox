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
public class CubeMapJob extends TextureLoadJob
{
    public interface CubeMapFactory
    {
        public CubeMapJob create(String path);
    }
    private static final String texturepath = "resources/Textures/";
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private final ResourcesLoader loader;

    @AssistedInject
    public CubeMapJob(GraphicContext gc, TextureUtil util, ResourcesLoader loader,
            @Assisted String path)
    {
        super(gc, util, path, -1, -1);
        this.loader = loader;
    }

    @Override
    public Texture load()
    {
        Texture re = null;
//        try {
//            re = util.loadCubeMap(getPath());
//            tcontainer.setTexture(re);
//        } catch (IOException ex) {
//            logger.warn("CubeMap " + getPath() + " konnte nicht geladen werden.\n("
//                    + ex.getLocalizedMessage() + ")", ex);
//
//            re = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);
//            re.bind(gc.getGL());
//            try {
//                InputStream iss = loader.getRessource(texturepath + "error.dds");
//                TextureData data = TextureIO.newTextureData(gc.getGL().getGLProfile(),
//                        iss, false, TextureIO.DDS);
//                for (int i = 0; i < 6; ++i) {
//                    re.updateImage(gc.getGL(), data, GL.GL_TEXTURE_CUBE_MAP + 2 + i);
//                }
//            } catch (IOException ex1) {
//                logger.error("Keine Error Texturen gefunden.", ex1);
//                return null;
//            }
//
//            util.setTexturePara(re, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
//        }

        return re;
    }
}
