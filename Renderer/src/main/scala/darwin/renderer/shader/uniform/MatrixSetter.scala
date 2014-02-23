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
package darwin.renderer.shader.uniform

import darwin.renderer.shader.{ShaderComponent, ShaderUniform}
import darwin.util.math.util._
import darwin.util.math.base.matrix.{Matrix, Matrix4}

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
class MatrixSetter extends ShaderComponent#UniformSetter with GenListener[MatrixEvent] {

  private type US = ShaderComponent#UniformSetter

  private def fac(flag: => Boolean, f: MatrixCache => Matrix[_])(us: ShaderUniform) = () => if(flag) us.setData(f(matricen).getFloatBuffer)
  private val funcMap = Map(
    ("M", fac(m, _.getModel) _),
    ("MV", fac(m || v, _.getModelView) _),
    ("MVP", fac(m || v || p, _.getModelViewProjection) _),
    ("N", fac(m, _.getNormal) _),
    ("NV", fac(m || v, _.getNormalView) _),
    ("P", fac(p, _.getProjektion) _),
    ("V", fac(v, _.getView) _),
    ("VP", fac(v || p, _.getViewProjection) _)
  )

  private var m, v, p, l = true
  private var matricen: MatrixCache = _
  private var setter = Seq[US]()

  def addUniform(uni: ShaderUniform) {
    val matType: String = uni.getElement.getBezeichnung.substring(4)
    if(funcMap.isDefinedAt(matType))
        setter :+= funcMap(matType)(uni)
    else
        System.err.println("Unknown Matrix Type: " + matType)

  }

  def apply {
    if (matricen != null) {
      setter foreach(_.apply)
    }
    m = false
    v = false
    p = false
    l = false
  }

  def changeOccured(t: MatrixEvent) {
    import MatType._
    matricen = t.getSource
    t.getType match {
      case MODEL =>
        m = true
      case PROJECTION =>
        p = true
      case VIEW =>
        v = true
      case LIGHT =>
        l = true
    }
  }
}