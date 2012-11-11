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
import com.jogamp.opengl.util.texture.Texture;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import javax.media.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.GraphicContext;
import darwin.resourcehandling.io.TextureUtil;
import darwin.resourcehandling.resmanagment.LoadJob;
import darwin.resourcehandling.wrapper.TextureContainer;
import darwin.util.logging.InjectLogger;

/**
 *
 * @author dheinrich
 */
public class TextureLoadJob implements LoadJob<Texture>
{
    public interface TextureJobFactory
    {
        public TextureLoadJob create(String path,
                                      @Assisted("FILTERING") int filtering,
                                      @Assisted("WRAPPING") int wrapping);

        public TextureLoadJob create(File file,
                                      @Assisted("FILTERING") int filtering,
                                      @Assisted("WRAPPING") int wrapping);
    }
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private static final String texturepath = "resources/Textures/";
    private final String path;
    private final int filtering, wrapping;
    protected TextureContainer tcontainer;
    protected final TextureUtil util;
    protected final GraphicContext gc;

    @AssistedInject
    public TextureLoadJob(GraphicContext gc, TextureUtil util,
                          @Assisted String path,
                          @Assisted("FILTERING") int filtering,
                          @Assisted("WRAPPING") int wrapping)
    {
        this(texturepath + path, gc, util, filtering, wrapping);
    }

    @AssistedInject
    public TextureLoadJob(GraphicContext gc, TextureUtil util,
                          @Assisted File file,
                          @Assisted("FILTERING") int filtering,
                          @Assisted("WRAPPING") int wrapping)
    {
        this(file.getAbsolutePath(), gc, util, filtering, wrapping);
    }

    private TextureLoadJob(String path, GraphicContext gc, TextureUtil util,
                           int filtering, int wrapping)
    {
        this.path = path;
        this.gc = gc;
        this.util = util;
        this.filtering = filtering;
        this.wrapping = wrapping;
    }

    public int getFiltering()
    {
        return filtering;
    }

    public String getPath()
    {
        return path;
    }

    public int getWrapping()
    {
        return wrapping;
    }

    public void setCon(TextureContainer tcontainer)
    {
        this.tcontainer = tcontainer;
    }

    @Override
    public Texture load()
    {
        Texture texture = tcontainer.getTexture();
        if (texture != null) {
            texture.destroy(gc.getGL());
        }
//        try {
//            texture = util.loadTexture(path, filtering, wrapping);
//            if (filtering == GL.GL_LINEAR) {
//                texture.setTexParameteri(gc.getGL(), GL.GL_TEXTURE_MIN_FILTER,
//                                         GL.GL_LINEAR_MIPMAP_LINEAR);
//            }
//        } catch (IOException ex) {
//            logger.warn("Texture {} konnte nicht geladen werden.\n({})", path, ex.getLocalizedMessage());
//            try {
//                texture = util.loadTexture(texturepath + "error.dds",
//                                           GL.GL_NEAREST,
//                                           wrapping);
//            } catch (IOException ex1) {
//                logger.error("Keine Error Texturen gefunden.", ex1);
//            }
//        }
        tcontainer.setTexture(texture);
        return texture;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TextureLoadJob other = (TextureLoadJob) obj;
        if ((this.path == null) ? (other.path != null) : !this.path.equals(
                other.path)) {
            return false;
        }
        if (this.filtering != other.filtering) {
            return false;
        }
        if (this.wrapping != other.wrapping) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 53 * hash + this.filtering;
        hash = 53 * hash + this.wrapping;
        return hash;
    }
}
