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

package object opengl {
  trait SimpleGLResource extends GLResource with DeleteFunc with Bindable with BindFunc

  private val buf: IntBuffer = Buffers.newDirectIntBuffer(1)
  implicit def array2SingleFunc(f: (Int, IntBuffer) => _) = {
    def apply(): Int = {
      f(1, buf)
      buf.get(0)
    }
    def apply(a: Int): Int = {
      f(a, buf)
      buf.get(0)
    }
    def update(a: Int){
      buf.set(0, a)
      f(1, buf)
    }
  }
}
