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
package darwin.renderer.shader

/*
 *
 * * Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com> * * This
 * program is free software: you can redistribute it and/or modify * it under
 * dheinrich.own.engineails. * * You should have received a copy of the GNU
 * General Public License * along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

import darwin.renderer.opengl.ShaderProgramm
import com.jogamp.opengl.util.texture.Texture
import javax.media.opengl.{GL2ES2, GL}
import darwin.renderer.{GProfile, GraphicComponent}

/**
 *
 * @author dheinrich
 */
trait SamplerComponent {
  this: GraphicComponent with GProfile[GL2ES2] =>

  import context._

  class Sampler(textureUnit: Int, uniName: String) {
    private var active: Boolean = true
    private var uniform_pos: Int = -1

    protected def setActive(active: Boolean) {
      this.active = active
    }

    def getTextureUnit: Int = {
      return textureUnit
    }

    def bindTexture(tex: Texture) {
      gl.glActiveTexture(textureUnit)
      if (!isActive || tex == null) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0)
      }
      else {
        tex.bind(gl)
      }
    }

    def isActive: Boolean = {
      return uniform_pos != -1 && active
    }

    def setShader(s: ShaderProgramm) {
      uniform_pos = s.getUniformLocation(uniName)
      s.use
      gl.glUniform1i(uniform_pos, textureUnit - GL.GL_TEXTURE0)
    }
  }

}