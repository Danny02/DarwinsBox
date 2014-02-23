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

import darwin.renderer.shader._
import darwin.renderer._
import javax.media.opengl.GL2ES2
import scala.collection.JavaConversions._
import darwin.renderer.dependencies.Components
import darwin.renderer.opengl.VBOComponent
import darwin.renderer.opengl.buffer.BufferObjectComponent

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */

trait StdAttributesComponent extends VertexAttributeComponent {
  this: VBOComponent with BufferObjectComponent with GraphicComponent with GProfile[GL2ES2] with ShaderComponent=>
  import context._

  def createAttributes(shader: Shader, vbuffers: Seq[VertexBO], indices: Option[BufferObject]): Bindable = {
    new StdAttributes(shader, vbuffers, indices)
  }

  class StdAttributes(shader: Shader, vbuffers: Seq[VertexBO], indices: Option[BufferObject]) extends Bindable {
    require(shader.isInitialized, "Shader is not initialized!")

    val configs = {
      for (buf <- vbuffers) yield {
        val a = buf.layout.getElements.map(de => (shader.getAttribut(de), buf.layout.getAttribut(de)))
        val configs = for ((osa, la) <- a; sa <- osa) yield new AttributConfig(sa, la)

        new BufferConfigs(buf.buffer, configs.toSeq)
      }
    }

    def bind {
      configs foreach (_.bind)
      indices foreach (_.bind)
    }

    def unbind {
      configs foreach (_.unbind)
      indices foreach (_.unbind)
    }
  }

  case class BufferConfigs(buffer: BufferObject, configs: Seq[AttributConfig]) extends Bindable {

    def bind {
      buffer.use {
        for (ac <- configs) {
          gl.glEnableVertexAttribArray(ac.index)
          gl.glVertexAttribPointer(ac.index, ac.size, ac.glconst, false, ac.stride, ac.offset)
        }
      }
    }

    def unbind {
      for (ac <- configs) {
        gl.glDisableVertexAttribArray(ac.index)
      }
    }
  }

}