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
package darwin.renderer.opengl

import darwin.geometrie.data._
import darwin.renderer.opengl.buffer._

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
trait VBOComponent {
  this: BufferObjectComponent =>

  def createVBO(vb: VertexBuffer) = {
    val buffer = createBuffer(Target.ARRAY)
    buffer.use {
      buffer.bufferData(vb.buffer, Type.STATIC, Usage.DRAW)
    }
    VertexBO(buffer, vb.layout)
  }

  case class VertexBO(buffer: BufferObject, layout: DataLayout) {
    def getVertexCount: Int = buffer.size / layout.getBytesize
  }

}