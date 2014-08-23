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

import java.lang.Integer.parseInt
import java.nio.charset.Charset
import java.nio.file._
import java.util.regex._
import javax.media.opengl._

import darwin.renderer.opengl._
import darwin.renderer.{GProfile, GraphicComponent}

import scala.util.Try

/**
 *
 * @author daniel
 */
trait ShaderObjektComponent {

  this: GraphicComponent with GProfile[GL2ES2] =>

  import context._

  case class ShaderObjekt(shaderType: ShaderType, id: Int) extends GLResource with DeleteFunc with Properties {
    def deleteFunc = gl.glDeleteShader

    def propertyFunc = gl.glGetShaderiv
  }

  def createShaderObject(`type`: ShaderType, shaderText: Array[String]): ShaderObjekt = {
    val glObjectID: Int = gl.glCreateShader(`type`.glConst)
    gl.glShaderSource(glObjectID, shaderText.length, shaderText, null)
    gl.glCompileShader(glObjectID)

    val so = ShaderObjekt(`type`, glObjectID)

    chekForError(so, shaderText)
  }

  private def chekForError(shader: ShaderObjekt, sources: Array[String]) {
    val error = shader.property(GL2ES2.GL_COMPILE_STATUS)
    if (error != GL.GL_TRUE) {
      val len = shader.property(GL2ES2.GL_INFO_LOG_LENGTH)
      if (len == 0) {
        return
      }
      val errormessage: Array[Byte] = new Array[Byte](len(0))
      gl.glGetShaderInfoLog(shader, len(0), len, 0, errormessage, 0)
      val errors = new String(errormessage, 0, len(0) + 1)

      val location: Pattern = Pattern.compile("(\\d):(\\d+)")
      val sb: StringBuilder = new StringBuilder("<")
      val texts = sources.map(_.split("\n"));
      for (err <- errors.split("\n")) yield {
        sb.append("-\t")
          .append(err)
          .append('\n')

        val er: Matcher = location.matcher(err)
        if (er.find) {
          Try {
            val file: Int = parseInt(er.group(1))
            val fLine: Int = parseInt(er.group(2))
            val sline: String = texts(file)(fLine)
            sb.append("\t\t").append(sline).append('\n')
          }
        }
      }
      sb.append(">")
      val file = writeSourceFile(sources)
      if (file.isSuccess) {
        sb.append(" source: ").append(writeSourceFile(sources))
      }
      throw new BuildException(sb.toString, BuildException.BuildError.CompileTime)
    }

    shader
  }

  private def writeSourceFile(sources: Array[String]): Try[String] = {
    Try {
      import scala.collection.JavaConverters._
      val lines = sources.flatMap(_.split("\n")).toIterable
      val tmp: Path = Files.createTempFile(null, null)

      Files.write(tmp, lines.asJava, Charset.defaultCharset)
      tmp.toAbsolutePath.toString
    }
  }
}