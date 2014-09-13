/*
 * Copyright (C) 2014 Daniel Heinrich <Daniel.Heinrich@procon-it.de>
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
package darwin.renderer

import java.nio.IntBuffer

import com.jogamp.common.nio.Buffers
import darwin.util.blas._
import shapeless.nat._

package object opengl {
  trait SimpleGLResource extends GLResource with DeleteFunc with Bindable with BindFunc

  private val buf: IntBuffer = Buffers.newDirectIntBuffer(1)
  implicit def array2SingleFunc(f: (Int, IntBuffer) => _) = new {
    def apply(a: Int = 1): Int = {
      f(a, buf)
      buf.get(0)
    }
    def update(a: Int) {
      buf.put(0, a)
      f(1, buf)
    }
  }

  def apply(f: (Int, IntBuffer) => _, a: Int = 1) = f(a)
  def update(f: (Int, IntBuffer) => _)(a: Int) = f.update(a)

  sealed trait GLSL
  object GL_Float extends GLSL
  object GL_Int extends GLSL
  object GL_Bool extends GLSL

  object GL_Vec2 extends GLSL
  object GL_Vec3 extends GLSL
  object GL_Vec4 extends GLSL

  object GL_Mat2 extends GLSL
  object GL_Mat3 extends GLSL
  object GL_Mat4 extends GLSL

  object GLSL {
    type BOOL = Int
    type VEC2 = Vector[_2]
    type VEC3 = Vector[_3]
    type VEC4 = Vector[_4]
    type MAT2 = Matrix[_2, _2]
    type MAT3 = Matrix[_3, _3]
    type MAT4 = Matrix[_4, _4]
  }
}
