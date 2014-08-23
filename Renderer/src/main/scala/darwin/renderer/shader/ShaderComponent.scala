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

import darwin.geometrie.data.Element
import darwin.renderer.shader.uniform.MatrixSetter
import darwin.resourcehandling.shader.ShaderFile
import darwin.util.math.util.GenListener
import darwin.util.math.util.MatrixEvent
import javax.media.opengl.GL
import javax.media.opengl.GL2ES2
import darwin.renderer.{ GProfile, GraphicComponent }
import scala.collection.JavaConversions._
import darwin.renderer.geometry.packed.Renderable

import scala.reflect.ClassTag

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
trait ShaderComponent {
  this: ShaderProgrammComponent with SamplerComponent with GraphicComponent with GProfile[GL2ES2] =>

  import context._

  def createShader(sf: ShaderFile) = new Shader(sf.getAttributs, sf.getUniforms, sf.getSampler)

  trait GLSLTypeChecker[T] extends (GLSLType => Boolean)

  object GLSLTypeChecker{
    import darwin.renderer.opengl.GLSL._

    implicit object FloatChecker extends GLSLTypeChecker[Float]{
      def apply(t: GLSLType) = t == GLSLType.FLOAT
    }
    implicit object IntBoolChecker extends GLSLTypeChecker[Int]{
      def apply(t: GLSLType) = t == GLSLType.INT || t == GLSLType.BOOL
    }
    implicit object Vec2Checker extends GLSLTypeChecker[VEC2]{
      def apply(t: GLSLType) = t == GLSLType.VEC2
    }
    implicit object Vec3Checker extends GLSLTypeChecker[VEC3]{
      def apply(t: GLSLType) = t == GLSLType.VEC3
    }
    implicit object Vec4Checker extends GLSLTypeChecker[VEC4]{
      def apply(t: GLSLType) = t == GLSLType.VEC4
    }
    implicit object Mat2Checker extends GLSLTypeChecker[MAT2]{
      def apply(t: GLSLType) = t == GLSLType.MAT2
    }
    implicit object Mat3Checker extends GLSLTypeChecker[MAT3]{
      def apply(t: GLSLType) = t == GLSLType.MAT3
    }
    implicit object Mat4Checker extends GLSLTypeChecker[MAT4]{
      def apply(t: GLSLType) = t == GLSLType.MAT4
    }
  }

  class Shader(attributes: Seq[ShaderAttribute], shaderUniforms: Seq[ShaderUniform], samplerNames: Seq[String]) extends GenListener[MatrixEvent] {
    val attributeMap = attributes.map(a => (a.element, a)).toMap
    val uniformMap = shaderUniforms.map(su => (su.getName, su)).toMap

    private val matricen = new MatrixSetter
    shaderUniforms.filter {
      su =>
        val bz = su.getElement.getBezeichnung
        (bz ne null) && (bz startsWith "Mat_")
    } foreach (matricen.addUniform(_))

    val uniforms = uniformMap.mapValues(Uniform(_.getName))

    val samplerMap = (for ((name, ind) <- samplerNames.zipWithIndex) yield {
      (name, new Sampler(GL.GL_TEXTURE0 + ind, name))
    }).toMap

    private var programm: ShaderProgramm = null
    private var attrhash: Int = 0
    private var usetter = Seq[UniformSetter]()

    def ini(prog: ShaderProgramm): Shader = {
      programm = prog

      attributeMap.values.foreach(_.ini(prog))
      uniforms.values.foreach(_.ini(prog))

      attrhash = buildAttrHash
      for (s <- samplerMap.values) {
        s.setShader(prog)
      }
      return this
    }

    private def buildAttrHash: Int = {
      var hash: Int = 9
      for (sa <- attributeMap.values) {
        if (sa.getIndex != -1) {
          hash = 97 * hash + sa.hashCode
        }
      }
      return hash
    }

    def bind() {
      assert(isInitialized)
      programm.use
    }

    def updateUniformData {
      bind()
      matricen()
      usetter foreach (_.apply())

      uniforms.values.foreach(_.trySetData)
    }

    def addUSetter(uss: UniformSetter) {
      usetter :+= uss
    }

    def isInitialized: Boolean = programm != null

    def getProgramm: ShaderProgramm = programm

    def getUniform[T](name: String)(implicit check: GLSLTypeChecker[T]) = {
      val su = uniformMap(name);
      val un = uniform(name)
      val ty = su.getElement.getVectorType

      assert(check(ty))

      un.asInstanceOf[Uniform[T]]
    }

    def getAttribut(ele: Element) = attributeMap.get(ele)

    def getAttributElements = attributeMap.keys

    def getSampler(name: String) = samplerMap.get(name)

    def getAttributsHash: Int = {
      return attrhash
    }

    def changeOccured(t: MatrixEvent) {
      matricen.changeOccured(t)
    }
  }

  trait Shaded extends Renderable {
    def getShader: Shader
  }

  case class Uniform[T](name: String) {
    private var id = -1
    private var dataSetter = (i: Int) => {}
    private var changed = false

    def ini(prog: ShaderProgramm) {
      id = prog.uniform(name)
      changed = true
    }

    def trySetData() {
      if (changed && id != -1) {
        dataSetter(id)
        changed = false
      }
    }

    def set(data: T)(implicit us: UniformSetter[T]) {
      dataSetter = us.set(_, data)
      changed = true
    }
  }

}