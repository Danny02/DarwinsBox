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
package darwin.renderer.opengl.buffer

import java.nio._
import darwin.renderer.{GraphicComponent, Bindable}
import com.jogamp.common.nio.Buffers
import darwin.renderer.opengl._

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */


trait BufferObjectComponent {
  this: GraphicComponent =>

  import context._

  private def getGLConst(`type`: Type, usage: Usage): Int = {
    return `type`.glconst + usage.glEnumeration
  }

  def createIndexBuffer(indices: Array[Int]) = {
    val buff = createBuffer(Target.ELEMENT_ARRAY)
    val b: Buffer = Buffers.newDirectIntBuffer(indices)
    buff.use {
      buff.bufferData(b, Type.STATIC, Usage.DRAW)
    }
    buff
  }

  def createBuffer(target: Target) = new BufferObject(gl.glGenBuffers(), target)

  class BufferObject(val id: Int, val target: Target) extends SimpleGLResource {
    def bindFunc = gl.glBindBuffer(target.glvalue, _)
    def deleteFunc = gl.glDeleteBuffers

    private var psize: Int = 0

    /**
     * Writes data to the buffer and allocates as much memory as needed for the
     * data
     * <p/>
     * @param data  data which should be written to the buffer
     * @param usage Buffer usage type
     */
    def bufferData(data: Buffer, `type`: Type, usage: Usage) {
      val bsize: Int = Buffers.sizeOfBufferElem(data)
      data.rewind
      psize = bsize * data.limit
      gl.glBufferData(target.glvalue, psize, data, getGLConst(`type`, usage))
    }

    /**
     * same usage as bufferData(...), but it allocates the memory with
     * <b>null</b> and uploads the data with bufferSubData. This is a common
     * driver optimization for frequent data uploading to the same buffer.
     * <p/>
     * @param data param type param usage
     */
    def bufferDataOptimized(data: Buffer, `type`: Type, usage: Usage) {
      allocate(data.limit * Buffers.sizeOfBufferElem(data), `type`, usage)
      bufferSubData(0, data)
    }

    /**
     * allocate memory for the Buffer <br><br><i>glBufferData with null as
     * data</i></br></br>
     * <p/>
     * @param bsize memory size in bytes which should be allocated
     * @param usage Buffer usage type
     */
    def allocate(bsize: Int, `type`: Type, usage: Usage) {
      psize = bsize
      gl.glBufferData(target.glvalue, psize, null, getGLConst(`type`, usage))
    }

    /**
     * overwrites a specified data block, enough memory must already be
     * allocated in the buffer
     * <p/>
     * @param offset param data
     */
    def bufferSubData(offset: Int, data: Buffer) {
      val bsize: Int = Buffers.sizeOfBufferElem(data)
      data.rewind
      gl.glBufferSubData(target.glvalue, offset, bsize * data.limit, data)
    }

    def mapBuffer(access: Access): ByteBuffer = {
      return gl.glMapBuffer(target.glvalue, access.glvalue)
    }

    def mapRange(access: Access, offset: Int, length: Int): ByteBuffer = {
      return gl.getGL2.glMapBufferRange(target.glvalue, offset, length, access.glvalue)
    }

    def size(): Int = psize
  }

}