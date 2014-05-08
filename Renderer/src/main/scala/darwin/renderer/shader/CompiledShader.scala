/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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

import java.nio._
import com.jogamp.common.nio.Buffers
import javax.media.opengl._
import darwin.renderer.{GProfile, GraphicComponent}

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */

trait CompiledShaderComponent {
  this: ShaderProgrammComponent with GraphicComponent with GProfile[GL4ES3] =>

  import context._
  import GL._
  import GL2ES2._

  val EXTENSION_STRING: String = "GL_ARB_get_program_binary"

  def fromShader(prog: ShaderProgramm): CompiledShader = {
    val pid: Int = prog.id
    val hint = prog.property(GL4ES3.GL_PROGRAM_BINARY_RETRIEVABLE_HINT)
    if (hint == GL_FALSE) {
      throw new RuntimeException("The binary of this shader program is not retrievable(set PROGRAM_BINARY_RETRIEVABLE_HINT to true)!")
    }

    val length = prog.property(GL_PROGRAM_BINARY_LENGTH)

    val format: IntBuffer = Buffers.newDirectIntBuffer(1)
    val data: ByteBuffer = Buffers.newDirectByteBuffer(length)
    gl.glGetProgramBinary(pid, length.get(0), length, format, data)
    if (gl.glGetError == GL_INVALID_OPERATION) {
      throw new RuntimeException("The given shader program is not in a valid state(not linked correctly)")
    }
    new CompiledShader(format.get(0), data)
  }


  lazy val formats = {
    val len = get(GL_NUM_PROGRAM_BINARY_FORMATS)
    val c = Buffers.newDirectIntBuffer(len)
    gl.glGetIntegerv(GL_PROGRAM_BINARY_FORMATS, c)
    c.array
  }

  def isAvailable = gl.isExtensionAvailable(EXTENSION_STRING)

  case class CompiledShader(format: Int, data: ByteBuffer) {
    def toShader: Either[ShaderProgramm, String] = {
      val prog = ShaderProgramm()

      gl.glProgramBinary(prog.id, format, data, data.limit)
      if (GL.GL_INVALID_ENUM == gl.glGetError()) {
        Right("Invalid Enum")
      } else {
        val error = prog.verify
        error.toRight(prog)
      }
    }

    def isFormatSupported: Boolean = formats.contains(format)

    def getDataArray: Array[Byte] = {
      data.rewind
      val d: Array[Byte] = new Array[Byte](data.limit)
      data.get(d)
      return d
    }
  }

}