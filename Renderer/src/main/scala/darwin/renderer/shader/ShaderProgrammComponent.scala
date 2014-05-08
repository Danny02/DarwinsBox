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
import darwin.renderer.opengl._
import darwin.renderer.opengl.GLResource
import darwin.renderer.shader.BuildException.BuildError
import darwin.util.tag._

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
      //TODO sollte nie null übergeben werden, eingaben überprüfen
      if (so != null) {
        gl.glAttachShader(program.id, so.id)
      }
    }

    val max: Int = get(GL2ES2.GL_MAX_VERTEX_ATTRIBS)

    for (sa <- attributs) {
      val index: Int = sa.getIndex
      if (index >= 0 && index < max) {
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

  trait UniformID
  trait AttributeLocation

  case class ShaderProgramm(id: Int) extends SimpleGLResource with Properties{
    val deleteFunc = gl.glDeleteProgram
    val bindFunc = gl.glUseProgram
    val propertyFunc = gl.glGetProgramiv

    def uniform = new AnyVal {
      def apply(name: String): Int @@ UniformID = {
        val i = gl.glGetUniformLocation(id, name)

        assert(i != -1, s"Uniform with name \"$name\" doesn't exist in shader programm")

        tag(i)
      }
      def update[V](name: String, value: V)(implicit us: UniformSetter[V]) = update(apply(name), value)
      def update(i: Int @@ UniformID, value: V)(implicit us: UniformSetter[V]) = {
        bind()
        us.set(i, value)
      }
    }

    /**
     * @param name Name der Attribut Variable(wie im Vertex ShaderProgramm
     *             definiert).
     *             <p/>
     * @return Gibt den Index der Attribut Varibale im Grafikspeicher zur�ck.
     */
    def getAttrLocation(name: String): Int @@ AttributeLocation = tag(gl.glGetAttribLocation(id, name))

    def verify() = {
      gl.glValidateProgram(id)
      val error = property(GL2ES2.GL_LINK_STATUS)
      if (error != GL.GL_TRUE) {
        val len = property(GL2ES2.GL_INFO_LOG_LENGTH)
        if (len == 0) {
          None
        } else {
          val errormessage: Array[Byte] = new Array[Byte](len(0))
          gl.glGetProgramInfoLog(id, len(0), len, 0, errormessage, 0)
          Some(new String(errormessage, 0, len(0)))
        }
      } else {
        None
      }
    }

    def shaderObjects = {
      val amount = property(GL2ES2.GL_ATTACHED_SHADERS)
      val names = new Array[Int](amount)
      val count = new Array[Int](1)
      gl.glGetAttachedShaders(id, amount, count, 0, names, 0)

      val types = names.map(n => gl.glGetShaderiv(n, _, _)(GL2ES2.GL_SHADER_TYPE))
      .map(t => ShaderType.values.find(_.glConst == t).get)

      for((id, t) <- names zip types) yield ShaderObjekt(t, id)
    }

    def attach(so: ShaderObjekt) = gl.glAttachShader(id, so.id)
    def detach(so: ShaderObjekt) = gl.glDetachShader(id, so.id)
    def link() = gl.glLinkProgram(id)
  }

  trait UniformSetter[V] {
    def set(id: Int, v: V)
  }

  object UniformSetter {
    implicit val intSetter = new UniformSetter[Int] {
      def set(id: Int, v: Int) = gl.glUniform1i(id, v)
    }
  }

}