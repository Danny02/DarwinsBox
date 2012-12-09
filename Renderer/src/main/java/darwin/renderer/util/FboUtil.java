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
package darwin.renderer.util;

import com.jogamp.opengl.util.texture.Texture;
import javax.inject.Inject;
import javax.media.opengl.*;

import darwin.renderer.opengl.FrameBuffer.*;
import darwin.renderer.opengl.FrameBuffer.RenderBuffer.RenderBufferFactory;
import darwin.resourcehandling.texture.TextureUtil;

/**
 *
 * @author Daniel Heinrich
 */
public class FboUtil
{

    private final RenderBufferFactory factory;
    private final TextureUtil util;

    @Inject
    public FboUtil(RenderBufferFactory factory, TextureUtil util)
    {
        this.factory = factory;
        this.util = util;
    }

    /**
     * Ein Factory methode um ein Frambuffer Objekt(FBO) zu erstellen. Dem FBO
     * wird Als Color Attachment eine standart R8G8B8A8 Textur zugewiesen und
     * als Depth Attachment ein simpler Renderbuffer.
     * <p/>
     * @param gl      Der GL context in dem das FBO erstellt werden soll
     * @param texsize Die seiten l�nge der dem FBO hinterlegten Attachments.
     *                texsize muss eine potentz von 2 sein (2^n). Wichtig alle
     *                Attachments m�ssen die selbe gr��e haben.
     * <p/>
     * @return das erstellte FBO wird zur�ckgegeben.
     */
//    public FrameBufferObject configureStandart(FrameBufferObject fbo, int width, int height)
//    {
//        Texture tex = util.newTexture(GL.GL_RGBA8, width, height, 0,
//                GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
//                true);
//        util.setTexturePara(tex, GL.GL_NEAREST, GL.GL_CLAMP_TO_EDGE);
//
//        RenderBuffer rb = factory.create(width, height, GL2ES2.GL_DEPTH_COMPONENT);
//
//        fbo.setColor_Attachment(0, tex);
//        fbo.setDepth_Attachment(rb);
//        return fbo;
//    }
//
//    public FrameBufferObject configureDefault(FrameBufferObject fbo,
//            int width, int height, int samples)
//    {
//        RenderBuffer col = factory.create(samples, width, height, GL2ES2.GL_RGBA8);
//        RenderBuffer dep = factory.create(samples, width, height, GL2ES2.GL_DEPTH_COMPONENT);
//
//        fbo.setColor_Attachment(0, col);
//        fbo.setDepth_Attachment(dep);
//        return fbo;
//    }
}
