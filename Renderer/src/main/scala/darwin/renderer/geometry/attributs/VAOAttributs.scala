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
package darwin.renderer.geometry.attributs

import darwin.renderer.{GProfile, Bindable}
import darwin.renderer.opengl._
import darwin.renderer.shader.ShaderComponent
import javax.media.opengl.GL2GL3
import darwin.renderer.dependencies.Components

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
trait VAOComponent extends StdAttributesComponent {
  this: Components.VBO with GProfile[GL2GL3] with ShaderComponent=>

  import context._

  override def createAttributes(shader: Shader, vbuffers: Seq[VertexBO], indice: Option[BufferObject]) = {
    val i: Array[Int] = new Array[Int](1)
    gl.glGenVertexArrays(1, i, 0)

    val vao = VAOAttributs(i(0))
    val sa = super.createAttributes(shader, vbuffers, indice)

    vao.bind
    sa.bind
    vao.unbind
    sa.unbind

    vao
  }


  case class VAOAttributs(id: Int) extends SimpleGLResource {
    def bindFunc = gl glBindVertexArray _
    def deleteFunc = gl glDeleteVertexArrays.update
  }
}