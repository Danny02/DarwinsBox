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
package darwin.renderer.geometry.packed

import javax.media.opengl._
import darwin.renderer.Bindable
import darwin.renderer.geometry.attributs.VertexAttributeComponent
import darwin.renderer.dependencies.Components
import darwin.renderer.shader.ShaderComponent

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
trait RenderMeshComponent {
  this: Components.VBO with VertexAttributeComponent with ShaderComponent =>

  import context._

  def createRenderMesh(shader: Shader, indices: Option[BufferObject] = None, primitivtype: Int = GL.GL_TRIANGLES)(vertexdata: VertexBO*) = {
    if (!shader.isInitialized) {
      throw new RuntimeException("Shader must be initialized")
    }
    val vertexcount = vertexdata(0).getVertexCount
    val attributs = createAttributes(shader, vertexdata, indices)

    new RenderMesh(attributs, vertexcount, indices, primitivtype)
  }

  class RenderMesh(attributes: Bindable, vertexCount: Int, indices: Option[BufferObject], primitivType: Int) extends Cloneable {

    private val isArray: Boolean = indices.isEmpty

    val indexCount = indices.map(_.size() / 4).getOrElse(vertexCount)

    def render {
      renderRange(0, indexCount)
    }

    def renderRange(offset: Int, length: Int) {
      attributes.use {
        if (isArray) {
          gl.glDrawArrays(primitivType, offset, length)
        }
        else {
          gl.glDrawElements(primitivType, length, GL.GL_UNSIGNED_INT, offset * 4L)
        }
      }
    }

    override def clone: RenderMesh = {
      try {
        return super.clone.asInstanceOf[RenderMesh]
      }
      catch {
        case ex: CloneNotSupportedException => {
          throw new RuntimeException(ex)
        }
      }
    }
  }

}