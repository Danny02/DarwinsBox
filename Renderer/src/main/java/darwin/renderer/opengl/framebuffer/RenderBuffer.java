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
package darwin.renderer.opengl.framebuffer;

import darwin.renderer.GraphicContext;
import darwin.renderer.opengl.GLClientConstants;

import com.google.inject.assistedinject.*;
import javax.media.opengl.*;

/**
 *
 * @author Daniel HeinrichTextureUtil
 */
public class RenderBuffer {

    public interface RenderBufferFactory {

        public RenderBuffer create(int id);

        public RenderBuffer create(@Assisted("width") int width,
                                   @Assisted("height") int height,
                                   @Assisted("texformat") int texformat);

        public RenderBuffer create(@Assisted("samples") int samples,
                                   @Assisted("width") int width,
                                   @Assisted("height") int height,
                                   @Assisted("texformat") int texformat);
    }
    private int samples, width, height, texformat;
    private int id = -1;
    private final GraphicContext gcontext;

    @AssistedInject
    public RenderBuffer(GraphicContext gc, @Assisted int id) {
        gcontext = gc;
        GL2ES2 gl = gcontext.getGL().getGL2ES2();
        this.id = id;
        bind();
        int[] ret = new int[1];
        gl.glGetRenderbufferParameteriv(GL2ES2.GL_RENDERBUFFER,
                                        GL2ES2.GL_RENDERBUFFER_WIDTH, ret, 0);
        width = ret[0];
        gl.glGetRenderbufferParameteriv(GL2ES2.GL_RENDERBUFFER,
                                        GL2ES2.GL_RENDERBUFFER_HEIGHT, ret, 0);
        height = ret[0];
        gl.glGetRenderbufferParameteriv(GL2ES2.GL_RENDERBUFFER,
                                        GL2GL3.GL_RENDERBUFFER_SAMPLES, ret, 0);
        samples = ret[0];
        gl.glGetRenderbufferParameteriv(GL2ES2.GL_RENDERBUFFER,
                                        GL2ES2.GL_RENDERBUFFER_INTERNAL_FORMAT,
                                        ret, 0);
        texformat = ret[0];
    }

    /**
     * erstellt einen normalen RenderBuffer <br>
     * <p/>
     * @param gl <br> Der GL Context in dem der RenderBuffer erzeugt wird <br>
     * @param width <br> Breite = Anzahl der Pixel pro Zeile <br> muss als Wert
     * 2^n + 2 * border f�r n Integerwerte haben. <br>
     * @param height <br> H�he = Anzahl der Zeilen <br> muss als Wert 2^n + 2 *
     * border f�r n Integerwerte haben <br>
     * @param texformat <br> eine der folgenden symbolischen Konstanten : <br>
     * GL_ALPHA, GL_ALPHA4, GL_ALPHA8, GL_ALPHA12, GL_ALPHA16,
     * GL_COMPRESSED_ALPHA, GL_COMPRESSED_LUMINANCE,
     * GL_COMPRESSED_LUMINANCE_ALPHA, GL_COMPRESSED_INTENSITY,
     * GL_COMPRESSED_RGB, GL_COMPRESSED_RGBA, GL_DEPTH_COMPONENT,
     * GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT32,
     * GL_LUMINANCE, GL_LUMINANCE4, GL_LUMINANCE8, GL_LUMINANCE12,
     * GL_LUMINANCE16, GL_LUMINANCE_ALPHA, GL_LUMINANCE4_ALPHA4,
     * GL_LUMINANCE6_ALPHA2, GL_LUMINANCE8_ALPHA8, GL_LUMINANCE12_ALPHA4,
     * GL_LUMINANCE12_ALPHA12, GL_LUMINANCE16_ALPHA16, GL_INTENSITY,
     * GL_INTENSITY4, GL_INTENSITY8, GL_INTENSITY12, GL_INTENSITY16,
     * GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8, GL_RGB10, GL_RGB12,
     * GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1, GL_RGBA8, GL_RGB10_A2,
     * GL_RGBA12, GL_RGBA16, GL_SLUMINANCE, GL_SLUMINANCE8, GL_SLUMINANCE_ALPHA,
     * GL_SLUMINANCE8_ALPHA8, GL_SRGB, GL_SRGB8, GL_SRGB_ALPHA oder
     * GL_SRGB8_ALPHA8.
     * <p/>
     */
    @AssistedInject
    public RenderBuffer(GraphicContext gc, GLClientConstants constants,
                        @Assisted("width") int width,
                        @Assisted("height") int height,
                        @Assisted("texformat") int texformat) {
        this(gc, constants, 0, width, height, texformat);
    }

    /**
     * erstellt einen MultiSampled RenderBuffer <br>
     * <p/>
     * @param gl <br> Der GL Context in dem der RenderBuffer erzeugt wird <br>
     * @param samples <br> anzahl der Sample mit der der Renderbuffer
     * Multisampled werden soll <br>
     * @param width <br> Breite = Anzahl der Pixel pro Zeile <br> muss als Wert
     * 2^n + 2 * border f�r n Integerwerte haben. <br>
     * @param height <br> H�he = Anzahl der Zeilen <br> muss als Wert 2^n + 2 *
     * border f�r n Integerwerte haben <br>
     * @param texformat <br> eine der folgenden symbolischen Konstanten : <br>
     * GL_STENCIL_INDEX<1,4,8,16>_EXT, GL_COMPRESSED_RGB, GL_COMPRESSED_RGBA,
     * GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24,
     * GL_DEPTH_COMPONENT32, GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8,
     * GL_RGB10, GL_RGB12, GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1,
     * GL_RGBA8, GL_RGB10_A2, GL_RGBA12, GL_RGBA16
     * <p/>
     */
    @AssistedInject
    public RenderBuffer(GraphicContext gc, GLClientConstants constants,
                        @Assisted("samples") int samples, @Assisted("width") int width,
                        @Assisted("height") int height, @Assisted("texformat") int texformat) {
        this.gcontext = gc;
        this.samples = Math.min(samples, constants.getMaxSamples());
        this.width = width;
        this.height = height;
        this.texformat = texformat;

        int[] i = new int[1];
        GL2GL3 gl = gc.getGL().getGL2GL3();
        gl.glGenRenderbuffers(1, i, 0);
        id = i[0];
        bind();
        gl.glRenderbufferStorageMultisample(GL2ES2.GL_RENDERBUFFER, this.samples,
                                            texformat,
                                            width, height);
    }

    private void bind() {
        gcontext.getGL().glBindRenderbuffer(GL2ES2.GL_RENDERBUFFER, id);
    }

    /**
     * @return Gibt die H�he des Renderbuffers in pixel zur�ck.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Gibt den Index des Renderbuffers im Grafikspeicher zur�ck.
     */
    public int getRenderBufferID() {
        return id;
    }

    /**
     * @return Gibt die Anzahl der Samples zur�ck die genutzt werden.
     */
    public int getSamples() {
        return samples;
    }

    /**
     * @return Gibt das interne Textureformat des Renderbuffers zur�ck.
     */
    public int getTexformat() {
        return texformat;
    }

    /**
     * @return Gibt die Breite des Renderbuffers in pixel zur�ck
     */
    public int getWidth() {
        return width;
    }

    /**
     * L�scht den Renderbuffer aus den Grafikspeicher.
     */
    public void delete() {
        gcontext.getGL().glDeleteRenderbuffers(1, new int[]{id}, 0);
    }
}
