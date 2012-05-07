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
package darwin.renderer;

import com.jogamp.opengl.util.texture.Texture;
import javax.media.opengl.*;

import darwin.renderer.opengl.FrameBufferObject;
import darwin.renderer.opengl.RenderBuffer;
import darwin.resourcehandling.io.TextureUtil;

import static darwin.renderer.GraphicContext.*;

/**
 * Scene manager which renders deferred
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class DeferredScene extends BasicScene implements GLEventListener
{

    private FrameBufferObject fbo;

    public DeferredScene()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void customRender()
    {
        GL2GL3 gl = getGL().getGL2GL3();

        fbo.bind();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        int[] dbs = new int[]{GL2GL3.GL_COLOR_ATTACHMENT0,
            GL2GL3.GL_COLOR_ATTACHMENT1,
            GL2GL3.GL_COLOR_ATTACHMENT2};

        gl.glDrawBuffers(3, dbs, 0);

        renderObjects();

        FrameBufferObject.DEFAULT.bind();
//
//        gl.glDrawBuffer(GL2GL3.GL_FRONT);
//        squad.bindTexture(fbo.getColor_AttachmentTexture(2));
//        squad.render();

//        Vec3f light = new Vec3f(view.getMinor(3, 3).
//                mult(new Vec3f(0, 1, 1)));
//        light.normalize(light);
////        phong.setLight_dir(light);
//
//        Vec3f halfvector = new Vec3f(0, 0, 1);
//        halfvector.add(light, halfvector);
//        halfvector.normalize(halfvector);

//        phong.setHalfVector(halfvector);
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        super.init(drawable);

        fbo = new FrameBufferObject();
        int width = 800;
        int heigth = 600;

        Texture mat = TextureUtil.newTexture(GL.GL_RGBA8, width, heigth, 0,
                GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                false);
//        Texture depth = TextureUtil.newTexture(GL.GL_DEPTH_COMPONENT24, width, heigth, 0,
//                                 GL2GL3.GL_RED, GL.GL_UNSIGNED_BYTE,
//                                 false);
        RenderBuffer depth = new RenderBuffer(width, heigth,
                GL2GL3.GL_DEPTH_COMPONENT24);
        Texture normalSpecEx = TextureUtil.newTexture(GL2GL3.GL_RGB16F, width,
                heigth, 0,
                GL2GL3.GL_RGBA,
                GL.GL_UNSIGNED_BYTE,
                false);

        Texture compostion = TextureUtil.newTexture(GL.GL_RGB8, width, heigth, 0,
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                false);

        fbo.setColor_Attachment(0, mat);
        fbo.setColor_Attachment(1, normalSpecEx);
        fbo.setColor_Attachment(2, compostion);
        fbo.setDepth_Attachment(depth);
    }
}
