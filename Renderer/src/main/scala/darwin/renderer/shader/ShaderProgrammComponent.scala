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
    def deleteFunc = gl.glDeleteProgram
    def bindFunc = gl.glUseProgram
    def propertyFunc = gl.glGetProgramiv

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
    implicit object IntSetter extends UniformSetter[Int] {
      def set(id: Int, v: Int) = gl.glUniform1i(id, v)
    }
    implicit object Int2Setter extends UniformSetter[(Int, Int)] {
      def set(id: Int, v: (Int, Int)) = gl.glUniform2i(id, v._1, v._2)
    }
    implicit object Int3Setter extends UniformSetter[(Int, Int, Int)] {
      def set(id: Int, v: (Int, Int, Int)) = gl.glUniform3i(id, v._1, v._2, v._3)
    }
    implicit object Int4Setter extends UniformSetter[(Int, Int, Int, Int)] {
      def set(id: Int, v: (Int, Int, Int, Int)) = gl.glUniform4i(id, v._1, v._2, v._3, v._4)
    }

    implicit object FloatSetter extends UniformSetter[Float] {
      def set(id: Int, v: Float) = gl.glUniform1f(id, v)
    }

    implicit object Vec2Setter extends UniformSetter[Vector[_2]] {
      def set(id: Int, v: Vector[_2]) = gl.glUniform2f(id, v.x, v.y)
    }
    implicit object Vec3Setter extends UniformSetter[Vector[_3]] {
      def set(id: Int, v: Vector[_3]) = gl.glUniform3f(id, v.x, v.y, v.z)
    }
    implicit object Vec4Setter extends UniformSetter[Vector[_4]] {
      def set(id: Int, v: Vector[_4]) = gl.glUniform4f(id, v.x, v.y, v.z, v.w)
    }

    implicit object Matrix2Setter extends UniformSetter[Matrix[_2, _2]] {
      def set(id: Int, v: Matrix[_2, _2]) = gl.glUniformMatrix2fv(id, 1, false, v)
    }
    implicit object Matrix3Setter extends UniformSetter[Matrix[_3, _3]] {
      def set(id: Int, v: Matrix[_2, _2]) = gl.glUniformMatrix3fv(id, 1, false, v)
    }
    implicit object Matrix4Setter extends UniformSetter[Matrix[_4, _4]] {
      def set(id: Int, v: Matrix[_2, _2]) = gl.glUniformMatrix4fv(id, 1, false, v)
    }
  }

}