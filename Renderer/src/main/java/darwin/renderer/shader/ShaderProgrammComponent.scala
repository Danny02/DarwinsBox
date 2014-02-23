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

import javax.media.opengl._
import darwin.renderer._
import darwin.renderer.opengl.GLResource
import darwin.renderer.shader.BuildException.BuildError

/**
 * CPU seitige Repr�sentation eines OpenGL Shader Programmes
 * <p/>
 * @author Daniel Heinrich
 */

trait ShaderProgrammComponent {
  this: ShaderObjektComponent with GraphicComponent with GProfile[GL2ES2] =>

  import context._

  def linkShaderProgramm(objects: Seq[ShaderObjekt], attributs: ShaderAttribute*) = {
    val program = ShaderProgramm()

    for (so <- objects) {
      if (so != null) {
        gl.glAttachShader(program.id, so.id)
      }
    }
    val max: Array[Int] = new Array[Int](1)
    gl.glGetIntegerv(GL2ES2.GL_MAX_VERTEX_ATTRIBS, max, 0)
    for (sa <- attributs) {
      val index: Int = sa.getIndex
      if (index >= 0 && index < max(0)) {
        gl.glBindAttribLocation(program.id, sa.getIndex, sa.getName)
      }
    }

    if (gl.isGL4ES3) {
      gl.getGL4ES3().glProgramParameteri(program.id, GL4ES3.GL_PROGRAM_BINARY_RETRIEVABLE_HINT, GL.GL_TRUE)
    }

    gl.glLinkProgram(program.id)

    val error = program.verify map (new BuildException(_, BuildError.LinkTime))

    error.toRight(program)
  }

  object ShaderProgramm {
    def apply() = new ShaderProgramm(gl.glCreateProgram())
  }

  case class ShaderProgramm(id: Int) extends GLResource with Bindable {

    def getUniformLocation(s: String) = gl.glGetUniformLocation(id, s)

    /**
     * @param name Name der Attribut Variable(wie im Vertex ShaderProgramm
     *             definiert).
     *             <p/>
     * @return Gibt den Index der Attribut Varibale im Grafikspeicher zur�ck.
     */
    def getAttrLocation(name: String) = gl.glGetAttribLocation(id, name)

    /**
     * Aktiviert das ShaderProgramm Programm im GL Context.
     */
    def bind() {
      gl.glUseProgram(id)
    }

    /**
     * Deaktiviert ShaderProgramm Programme im GL Context.
     */
    def unbind() {
      gl.glUseProgram(0)
    }

    def delete() {
      gl.glDeleteProgram(id)
    }

    def verify() = {
      gl.glValidateProgram(id)
      val error: Array[Int] = Array[Int](-1)
      gl.glGetProgramiv(id, GL2ES2.GL_LINK_STATUS, error, 0)
      if (error(0) != GL.GL_TRUE) {
        val len: Array[Int] = new Array[Int](1)
        gl.glGetProgramiv(id, GL2ES2.GL_INFO_LOG_LENGTH, len, 0)
        if (len(0) == 0) {
          None
        } else {
          val errormessage: Array[Byte] = new Array[Byte](len(0))
          gl.glGetProgramInfoLog(id, len(0), len, 0, errormessage, 0)
          Option(new String(errormessage, 0, len(0)))
        }
      } else {
        None
      }
    }
  }

}