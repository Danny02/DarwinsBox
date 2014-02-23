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
package darwin.renderer.opengl.framebuffer

import darwin.renderer.opengl.GLResource
import javax.media.opengl._
import darwin.renderer.{GProfile, GraphicComponent, Bindable}

/**
 *
 * @author Daniel HeinrichTextureUtil
 */

trait MultiSampledRBufferComponent extends RenderBufferComponent {
  this: GraphicComponent with GProfile[GL2GL3] =>

  import context._


  /**
   * erstellt einen MultiSampled RenderBuffer <br>
   * <p/>
   * @param gl <br> Der GL Context in dem der RenderBuffer erzeugt wird <br>
   * @param samples <br> anzahl der Sample mit der der Renderbuffer
   *                Multisampled werden soll <br>
   * @param width <br> Breite = Anzahl der Pixel pro Zeile <br> muss als Wert
   *              2^n + 2 * border f�r n Integerwerte haben. <br>
   * @param height <br> H�he = Anzahl der Zeilen <br> muss als Wert 2^n + 2 *
   *               border f�r n Integerwerte haben <br>
   * @param texformat <br> eine der folgenden symbolischen Konstanten : <br>
   *                  GL_STENCIL_INDEX<1,4,8,16>_EXT, GL_COMPRESSED_RGB, GL_COMPRESSED_RGBA,
   *                  GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24,
   *                  GL_DEPTH_COMPONENT32, GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8,
   *                  GL_RGB10, GL_RGB12, GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1,
   *                  GL_RGBA8, GL_RGB10_A2, GL_RGBA12, GL_RGBA16
   *                  <p/>
   */
  def createRB(samples: Int, width: Int, height: Int, texformat: Int) {
    val samp = Math.min(samples, maxSamples)

    val rb = RenderBuffer(genId(), width, height, texformat, samp)

    rb.bind
    gl.glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samp, texformat, width, height)
  }
}

trait RenderBufferComponent {
  this: GraphicComponent =>

  import context._

  protected def genId() = {
    val ids = new Array[Int](1)
    gl.glGenRenderbuffers(1, ids, 0)
    ids(0)
  }

  def retrieveRbFrom(id: Int) {
    RenderBuffer(id, 0, 0, 0).bind

    val ret: Array[Int] = new Array[Int](1)
    gl.glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL.GL_RENDERBUFFER_WIDTH, ret, 0)
    val width = ret(0)
    gl.glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL.GL_RENDERBUFFER_HEIGHT, ret, 0)
    val height = ret(0)
    gl.glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL.GL_RENDERBUFFER_INTERNAL_FORMAT, ret, 0)
    val texformat = ret(0)

    val samples = if (gl.isGL2GL3) {
      gl.glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER, GL2GL3.GL_RENDERBUFFER_SAMPLES, ret, 0)
      ret(0)
    }
    else {
      1
    }

    RenderBuffer(id, width, height, texformat, samples)
  }

  /**
   * erstellt einen normalen RenderBuffer <br>
   * <p/>
   * @param gl <br> Der GL Context in dem der RenderBuffer erzeugt wird <br>
   * @param width <br> Breite = Anzahl der Pixel pro Zeile <br> muss als Wert
   *              2^n + 2 * border f�r n Integerwerte haben. <br>
   * @param height <br> H�he = Anzahl der Zeilen <br> muss als Wert 2^n + 2 *
   *               border f�r n Integerwerte haben <br>
   * @param texformat <br> eine der folgenden symbolischen Konstanten : <br>
   *                  GL_ALPHA, GL_ALPHA4, GL_ALPHA8, GL_ALPHA12, GL_ALPHA16,
   *                  GL_COMPRESSED_ALPHA, GL_COMPRESSED_LUMINANCE,
   *                  GL_COMPRESSED_LUMINANCE_ALPHA, GL_COMPRESSED_INTENSITY,
   *                  GL_COMPRESSED_RGB, GL_COMPRESSED_RGBA, GL_DEPTH_COMPONENT,
   *                  GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT32,
   *                  GL_LUMINANCE, GL_LUMINANCE4, GL_LUMINANCE8, GL_LUMINANCE12,
   *                  GL_LUMINANCE16, GL_LUMINANCE_ALPHA, GL_LUMINANCE4_ALPHA4,
   *                  GL_LUMINANCE6_ALPHA2, GL_LUMINANCE8_ALPHA8, GL_LUMINANCE12_ALPHA4,
   *                  GL_LUMINANCE12_ALPHA12, GL_LUMINANCE16_ALPHA16, GL_INTENSITY,
   *                  GL_INTENSITY4, GL_INTENSITY8, GL_INTENSITY12, GL_INTENSITY16,
   *                  GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8, GL_RGB10, GL_RGB12,
   *                  GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1, GL_RGBA8, GL_RGB10_A2,
   *                  GL_RGBA12, GL_RGBA16, GL_SLUMINANCE, GL_SLUMINANCE8, GL_SLUMINANCE_ALPHA,
   *                  GL_SLUMINANCE8_ALPHA8, GL_SRGB, GL_SRGB8, GL_SRGB_ALPHA oder
   *                  GL_SRGB8_ALPHA8.
   *                  <p/>
   */
  def createRB(width: Int, height: Int, texformat: Int) = RenderBuffer(genId(), width, height, texformat)

  case class RenderBuffer(id: Int, width: Int, height: Int, texformat: Int, samples: Int = 1) extends GLResource with Bindable {

    def bind {
      gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, id)
    }

    def unbind {
      gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, 0)
    }

    def delete {
      gl.glDeleteRenderbuffers(1, Array[Int](id), 0)
    }
  }

}