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
package darwin.renderer.opengl.FrameBuffer;

import com.jogamp.opengl.util.texture.Texture;
import javax.inject.Inject;
import javax.media.opengl.*;

import darwin.renderer.opengl.GLClientConstants;

/**
 *
 * @author Daniel Heinrich
 */
public class FrameBufferObject
{

    /**
     * Current bound Draw/Read Buffer
     */
    private static int currdraw = 0;
    private static int currread = 0;
    private final int id;
    private int width, height;
    private final Object[] color_attachment;
    private Object depth_attachment, stencil_attachment;
    private final int[] viewport = new int[4];
    private int prevdraw;
    private int prevread;
    protected final GLAutoDrawable drawable;

    /**
     * erstellt ein neues FrameBufferObjekt.
     * <p/>
     * @param gl der GL Context in dem das FBO erstellt werden soll.
     */
    @Inject
    public FrameBufferObject(GLAutoDrawable drawable, GLClientConstants constants)
    {
        this.drawable = drawable;
        color_attachment = new Object[constants.getMaxColorAttachments()];
        int[] ids = new int[1];
        drawable.getGL().glGenFramebuffers(1, ids, 0);
        id = ids[0];
    }

    FrameBufferObject(GLAutoDrawable drawable, int a)
    {
        this.drawable = drawable;
        color_attachment = null; 
        id = a;
        if (id != 0) {
            throw new UnsupportedOperationException("Other framebuffer then the Default(id=0) arn't supported yet!");
            //TODO höhe breite usw abfragen(attachments)
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Color-Attachment Setter/Getter">
    /**
     * Binded ein Renderbuffer auf ein bestimmtes Color Attachment des FBO. <br>
     * <p/>
     * @param nummber <br> Nummer des Color Attachments(0 bis
     *                GL_MAX_COLOR_ATTACHMENTS_EXT-1). <br>
     * @param rb      <br> Der Renderbuffer der gebunden werden soll.
     */
    public void setColor_Attachment(int nummber, RenderBuffer rb)
    {
        assert (rb != null);
        assert (currdraw == id);
        assert (checkSize(rb.getWidth(), rb.getHeight()));
        assert (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS);
        assert (id != 0);

        drawable.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
                GL.GL_COLOR_ATTACHMENT0 + nummber,
                GL.GL_RENDERBUFFER,
                rb.getRenderBufferID());
        color_attachment[nummber] = rb;
    }

    /**
     * Binded eine Textur auf ein bestimmtes Color Attachment des FBO. <br>
     * <p/>
     * @param nummber <br> Nummer des Color Attachments(0 bis
     *                GL_MAX_COLOR_ATTACHMENTS_EXT-1). <br>
     * @param tex <br> Die Textur die gebunden werden soll.
     */
    public void setColor_Attachment(int nummber, Texture tex)
    {
        assert (tex != null);
        assert (currdraw == id);
        assert (checkSize(tex.getWidth(), tex.getHeight()));
        assert ((tex.getTarget() == GL2GL3.GL_TEXTURE_2D || tex.getTarget() == GL2GL3.GL_TEXTURE_RECTANGLE));
        assert (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS);
        assert (id != 0);

        if (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS) {
            bind();
            drawable.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
                    GL.GL_COLOR_ATTACHMENT0 + nummber,
                    GL.GL_TEXTURE_2D, tex.getTextureObject(null),
                    0);
            color_attachment[nummber] = tex;
        }
    }

    /**
     * Gibt die Textur zur�ck die an ein bestimmtes Color Attechment gebunden
     * ist. <br>
     * <p/>
     * @param nummber <br> Nummer des Color Attachments(0 bis
     *                GL_MAX_COLOR_ATTACHMENTS_EXT-1). <br>
     * <p/>
     * @return Gibt die an das Color Attachtment gebundene Textur zur�ck. NULL
     *         wenn anstatt einer Textur ein Renderbuffer gebunden ist oder das
     *         Attachment noch frei ist.
     */
    public Texture getColorAttachmentTexture(int nummber)
    {
        assert (id != 0);
        assert (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS);
        return (Texture) color_attachment[nummber];
    }

    /**
     * Gibt den Renderbuffer zur�ck der an ein bestimmtes Color Attechment
     * gebunden ist. <br>
     * <p/>
     * @param nummber <br> Nummer des Color Attachments(0 bis
     *                GL_MAX_COLOR_ATTACHMENTS_EXT-1). <br>
     * <p/>
     * @return Gibt den an das Color Attachtment gebundenen Renderbuffer zur�ck.
     *         NULL wenn anstatt eines Renderbuffers eine Textur gebunden ist
     *         oder das Attachment noch frei ist.
     */
    public RenderBuffer getColorAttachmentBuffer(int nummber)
    {
        assert (id != 0);
        assert (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS);
        return (RenderBuffer) color_attachment[nummber];
    }

    public Class getColorAttachmentType(int nummber)
    {
        assert (id != 0);
        assert (nummber < GL2GL3.GL_MAX_COLOR_ATTACHMENTS);
        return color_attachment[nummber].getClass();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Depth-Attachment Setter/Getter">
    /**
     * Binded ein Renderbuffer als Depth Attachment des FBOs. <br>
     * <p/>
     * @param rb <br> Der Renderbuffer der gebunden werden soll.
     */
    public void setDepth_Attachment(RenderBuffer rb)
    {
        assert (rb != null);
        assert (currdraw == id);
        assert (checkSize(rb.getWidth(), rb.getHeight()));
        assert (id != 0);
        drawable.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
                GL.GL_DEPTH_ATTACHMENT,
                GL.GL_RENDERBUFFER, rb.getRenderBufferID());
        depth_attachment = rb;
    }

    /**
     * Binded eine Textur als Depth Attachment des FBOs. <br>
     * <p/>
     * @param tex <br> Die Textur die gebunden werden soll.
     */
    public void setDepth_Attachment(Texture tex)
    {
        assert (tex != null);
        assert (currdraw == id);
        assert (checkSize(tex.getWidth(), tex.getHeight()));
        assert ((tex.getTarget() == GL.GL_TEXTURE_2D || tex.getTarget() == GL2GL3.GL_TEXTURE_RECTANGLE));
        assert (id != 0);
        drawable.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
                GL.GL_DEPTH_ATTACHMENT,
                GL.GL_TEXTURE_2D, tex.getTextureObject(null),
                0);
        depth_attachment = tex;
    }

    /**
     * Gibt die Textur zur�ck die als Depth Attachment gebunden ist. <br>
     * <p/>
     * @return Gibt die als Depth Attachment gebundene Textur zur�ck. NULL wenn
     *         anstatt einer Textur ein Renderbuffer gebunden ist oder das
     *         Attachment noch frei ist.
     */
    public Texture getDepth_AttachmentTexture()
    {
        assert (id != 0);
        return getDepth_Attachment(Texture.class);
    }

    /**
     * Gibt den RenderBuffer zur�ck der als Depth Attachment gebunden ist. <br>
     * <p/>
     * @return Gibt den als Depth Attachment gebundenen RenderBuffer zur�ck.
     *         NULL wenn anstatt einer Textur ein Renderbuffer gebunden ist oder
     *         das Attachment noch frei ist.
     */
    public RenderBuffer getDepth_AttachmentBuffer()
    {
        assert (id != 0);
        return getDepth_Attachment(RenderBuffer.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getDepth_Attachment(Class<T> cl)
    {
        assert (id != 0);
        if (cl.isInstance(depth_attachment)) {
            return (T) depth_attachment;
        }
        return null;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Stencil-Attachment Setter/Getter">
    /**
     * Binded ein Renderbuffer als Stencil Attachment des FBOs. <br>
     * <p/>
     * @param rb <br> Der Renderbuffer der gebunden werden soll.
     */
    public void setStencil_Attachment(RenderBuffer rb)
    {
        assert (rb != null);
        assert (currdraw == id);
        assert (checkSize(rb.getWidth(), rb.getHeight()));
        assert (id != 0);
        drawable.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER,
                GL.GL_STENCIL_ATTACHMENT,
                GL.GL_RENDERBUFFER, rb.getRenderBufferID());
        stencil_attachment = rb;
    }

    /**
     * Binded eine Textur als Stencil Attachment des FBOs. <br>
     * <p/>
     * @param tex <br> Die Textur die gebunden werden soll.
     */
    public void setStencil_Attachment(Texture tex)
    {
        assert (tex != null);
        assert (currdraw == id);
        assert (checkSize(tex.getWidth(), tex.getHeight()));
        assert ((tex.getTarget() == GL.GL_TEXTURE_2D || tex.getTarget() == GL2GL3.GL_TEXTURE_RECTANGLE));
        assert (id != 0);
        drawable.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
                GL.GL_STENCIL_ATTACHMENT,
                GL.GL_TEXTURE_2D, tex.getTextureObject(null),
                0);
        stencil_attachment = tex;
    }

    /**
     * Gibt die Textur zur�ck die als Stencil Attachment gebunden ist. <br>
     * <p/>
     * @return Gibt die als Depth Attachment gebundene Textur zur�ck. NULL wenn
     *         anstatt einer Textur ein Renderbuffer gebunden ist oder das
     *         Attachment noch frei ist.
     */
    public Texture getStencil_AttachmentTexture()
    {
        assert (id != 0);
        return getStencil_Attachment(Texture.class);
    }

    /**
     * Gibt den RenderBuffer zur�ck der als Stencil Attachment gebunden ist.
     * <br>
     * <p/>
     * @return Gibt den als Depth Attachment gebundenen RenderBuffer zur�ck.
     *         NULL wenn anstatt einer Textur ein Renderbuffer gebunden ist oder
     *         das Attachment noch frei ist.
     */
    public RenderBuffer getStencil_AttachmentBuffer()
    {
        assert (id != 0);
        return getStencil_Attachment(RenderBuffer.class);
    }

    /**
     *
     * @param target on of: GL_COLOR_ATTACHMENT<0 - GL_MAX_COLOR_ATTACHMENTS>
     *               GL_DEPTH_ATTACHMENT GL_STENCIL_ATTACHMENT
     */
    public void detach(int target)
    {
        assert (id != 0);
        drawable.getGL().glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, target,
                GL.GL_RENDERBUFFER, 0);
    }

    @SuppressWarnings("unchecked")
    private <T> T getStencil_Attachment(Class<T> cl)
    {
        assert (id != 0);
        if (cl.isInstance(stencil_attachment)) {
            return (T) stencil_attachment;
        }
        return null;
    }// </editor-fold>

    /**
     * bindet das FBO an den GL Context.
     */
    public void bind()
    {
        drawable.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, id);
        currdraw = id;
        currread = id;
    }

    public void use()
    {
        pushBoundFBO();
        pushViewPort();
    }

    public void unuse()
    {
        popBoundFBO();
        popViewPort();
    }

    public void bindAsDrawBuffer()
    {
        bindDrawBuffer(id);
    }

    private void bindDrawBuffer(int id)
    {
        drawable.getGL().glBindFramebuffer(GL2GL3.GL_DRAW_FRAMEBUFFER, id);
        currdraw = id;
    }

    public void bindAsReadBuffer()
    {
        bindReadBuffer(id);
    }

    private void bindReadBuffer(int id)
    {
        drawable.getGL().glBindFramebuffer(GL2GL3.GL_READ_FRAMEBUFFER, id);
        currread = id;
    }

    public void copyColorTo(FrameBufferObject dst)
    {
        bindAsReadBuffer();
        dst.bindAsDrawBuffer();
        drawable.getGL().getGL2GL3().glBlitFramebuffer(0, 0, width, height,
                0, 0, dst.getWidth(), dst.getHeight(),
                GL.GL_COLOR_BUFFER_BIT, GL.GL_LINEAR);
    }

    /**
     * Falls das Color_Attachment0 eine Textur nutzt wird diese an den GL
     * Context gebunden.
     */
    public void bindColorAtt0Texture()
    {
        assert (id != 0);
        Texture tex = getColorAttachmentTexture(0);
        if (tex != null) {
            tex.bind(null);
        }
    }

    /**
     * L�scht das FBO aus dem Grafikspeicher.
     */
    public void delete()
    {
        assert (id != 0);
        drawable.getGL().glDeleteFramebuffers(1, new int[]{id}, 0);
    }

    /**
     * @return gibt die H�he des FBO in pixel zur�ck
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return gibt die Breite des FBO in pixel zur�ck
     */
    public int getWidth()
    {
        return width;
    }

    public int getStatus()
    {
        assert (currdraw == id);
        return drawable.getGL().glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
    }

    public boolean isComplete()
    {
        return getStatus() == GL.GL_FRAMEBUFFER_COMPLETE;
    }

    public String getStatusString()
    {
        switch (getStatus()) {
            case GL.GL_FRAMEBUFFER_COMPLETE:
                return "GL_FRAMEBUFFER_COMPLETE";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                return "INCOMPLETE_ATTACHMENT";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                return "INCOMPLETE_DIMENSIONS";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                return "INCOMPLETE_DRAW_BUFFER";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                return "INCOMPLETE_FORMATS";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_ARB:
                return "INCOMPLETE_LAYER_COUNT";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_ARB:
                return "INCOMPLETE_LAYER_TARGETS";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                return "INCOMPLETE_MISSING_ATTACHMENT";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                return "INCOMPLETE_MULTISAMPLE";
            case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                return "INCOMPLETE_READ_BUFFER";
            case GL2GL3.GL_FRAMEBUFFER_UNSUPPORTED:
                return "INCOMPLETE_UNSUPPORTED";
            default:
                return "ERROR: UNKNOWN STATUS";
        }
    }

    public int getId()
    {
        return id;
    }

    private boolean checkSize(int width, int height)
    {
        if (this.width == 0) {
            this.width = width;
        }

        if (this.height == 0) {
            this.height = height;
        }

        return this.width == width || this.height == height;
    }

    public void resetSize()
    {
        assert (id != 0);
        width = 0;
        height = 0;
    }

    /**
     * Setzt das ViewPort Attribut des GL Contextes auf die gr��e des FBO und
     * legt gleichzeitig die vorherige Einstellung auf den Attribut Stack ab.
     */
    public void pushViewPort()
    {
        //TODO get rid of any glGet call
        drawable.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        //TODO why the assert?
//        assert (viewport[2] != 0 && viewport[3] != 0);
        drawable.getGL().glViewport(0, 0, getWidth(), getHeight());
    }

    /**
     * laed die letzten Viewport einstellung.
     */
    public void popViewPort()
    {
        drawable.getGL().glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    /**
     * saves already bound FBO for rebinding later
     */
    public void pushBoundFBO()
    {
        prevdraw = currdraw;
        prevread = currread;
        bind();
    }

    /**
     * rebinds saved FBO, see pushBoundFBO()
     */
    public void popBoundFBO()
    {
        bindDrawBuffer(prevdraw);
        bindReadBuffer(prevread);
    }

//    public FrameBufferObject getPrevDrawBuffer()
//    {
//        return new FrameBufferObject(prevdraw);
//    }
//
//    public FrameBufferObject getPrevReadBuffer()
//    {
//        return new FrameBufferObject(prevread);
//    }
}
