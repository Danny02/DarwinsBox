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

import java.io._
import java.nio.charset.Charset
import java.nio.file._
import java.util.regex._
import javax.media.opengl._
import java.lang.Integer.parseInt
import darwin.renderer.{GProfile, GraphicComponent}
import darwin.renderer.opengl.GLResource

/**
 *
 * @author daniel
 */
trait ShaderObjektComponent {

  this: GraphicComponent with GProfile[GL2ES2] =>

  import context._

  case class ShaderObjekt(shaderType: ShaderType, id: Int) extends GLResource{
    def delete() {
      gl.glDeleteShader(id)
    }
  }

  def createShaderObject(`type`: ShaderType, shaderText: Array[String]): ShaderObjekt = {
    val glObjectID: Int = gl.glCreateShader(`type`.glConst)
    gl.glShaderSource(glObjectID, shaderText.length, shaderText, null)
    gl.glCompileShader(glObjectID)

    handleError(glObjectID, shaderText)

    ShaderObjekt(`type`, glObjectID)
  }

  private def handleError(shader: Int, sources: Array[String]) {
    val error: Array[Int] = Array[Int](-1)
    gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, error, 0)
    if (error(0) != GL.GL_TRUE) {
      val len: Array[Int] = new Array[Int](1)
      gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, len, 0)
      if (len(0) == 0) {
        return
      }
      val errormessage: Array[Byte] = new Array[Byte](len(0))
      gl.glGetShaderInfoLog(shader, len(0), len, 0, errormessage, 0)
      val tmp: String = new String(errormessage, 0, len(0) + 1)
      val errors: BufferedReader = new BufferedReader(new StringReader(tmp))
      val location: Pattern = Pattern.compile("(\\d):(\\d+)")
      val sb: StringBuilder = new StringBuilder("<")
      try {
        val texts: Array[Array[String]] = new Array(sources.length);
        {
          var i: Int = 0
          while (i < sources.length) {
            {
              texts(i) = sources(i).split("\n")
            }
            ({
              i += 1;
              i - 1
            })
          }
        }
        var line: String = null
        while ((({
          line = errors.readLine;
          line
        })) != null) {
          sb.append("-\t").append(line).append('\n')
          val er: Matcher = location.matcher(line)
          if (er.find) {
            try {
              val file: Int = parseInt(er.group(1))
              val fLine: Int = parseInt(er.group(2))
              val sline: String = texts(file)(fLine)
              sb.append("\t\t").append(sline).append('\n')
            }
            catch {
              case t: Throwable => {
              }
            }
          }
        }
      }
      catch {
        case ex: IOException => {
        }
      }
      sb.append(">")
      val file: String = writeSourceFile(sources)
      if (file != null) {
        sb.append(" source: ").append(writeSourceFile(sources))
      }
      throw new BuildException(sb.toString, BuildException.BuildError.CompileTime)
    }
  }

  private def writeSourceFile(sources: Array[String]): String = {
    try {
      import scala.collection.JavaConverters._
      val lines: Iterable[_ <: CharSequence] = sources.flatMap(_.split("\n")).toIterable
      val tmp: Path = Files.createTempFile(null, null)

      Files.write(tmp, lines.asJava, Charset.defaultCharset)
      return tmp.toAbsolutePath.toString
    }
    catch {
      case ex: IOException => {
      }
    }
    return null
  }
}