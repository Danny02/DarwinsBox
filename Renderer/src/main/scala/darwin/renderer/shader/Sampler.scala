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

import com.jogamp.opengl.util.texture.Texture
import javax.media.opengl.{ GL2ES2, GL }
import darwin.renderer.{ GProfile, GraphicComponent }
/**
 *
 * @author dheinrich
 */
trait SamplerComponent {
  this: GraphicComponent with GProfile[GL2ES2] with ShaderProgrammComponent =>

  import context._

  class Sampler(val textureUnit: Int, uniName: String) {

    def bindTexture(tex: Texture) {
      gl.glActiveTexture(textureUnit)
      tex.bind(gl)
    }

    def setShader(s: ShaderProgramm) {
      s.uniform(uniName) = textureUnit - GL.GL_TEXTURE0
    }
  }

}