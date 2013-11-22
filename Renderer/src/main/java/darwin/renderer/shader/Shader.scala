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
import darwin.renderer.opengl.ShaderProgramm
import darwin.renderer.shader.uniform.{ShaderMaterial, MatrixSetter}
import darwin.resourcehandling.shader.ShaderFile
import darwin.util.math.util.GenListener
import darwin.util.math.util.MatrixEvent
import javax.media.opengl.GL
import javax.media.opengl.GL2ES2
import darwin.renderer.{GProfile, GraphicComponent}
import darwin.renderer.geometrie.packed.Renderable
import scala.collection.JavaConversions._

/**
 *
 * * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
trait ShaderComponent {
  this: SamplerComponent with GraphicComponent with GProfile[GL2ES2] =>

  import context._

  type UniformSetter = () => Unit

  def createShader(sf: ShaderFile) = new Shader(sf.getAttributs, sf.getUniforms, sf.getSampler)

  class Shader(attributes: Seq[ShaderAttribute], uniforms: Seq[ShaderUniform], samplerNames: Seq[String]) extends GenListener[MatrixEvent] {
    val attributeMap = attributes.map(a => (a.element, a)).toMap
    val uniformMap = uniforms.map(su => (su.getName, su)).toMap

    private val matricen = new MatrixSetter
    uniforms.filter {
      su =>
        val bz = su.getElement.getBezeichnung
        (bz ne null) && (bz startsWith "Mat_")
    } foreach (matricen.addUniform(_))

    val samplerMap = (for ((name, ind) <- samplerNames.zipWithIndex) yield {
      (name, new Sampler(GL.GL_TEXTURE0 + ind, name))
    }).toMap

    private var programm: ShaderProgramm = null
    private var attrhash: Int = 0
    private var usetter = Seq[UniformSetter]()

    def ini(prog: ShaderProgramm): Shader = {
      programm = prog
      ini(attributeMap)
      ini(uniformMap)
      attrhash = buildAttrHash
      for (s <- samplerMap.values) {
        s.setShader(prog)
      }
      return this
    }

    private def ini(map: Map[_, _ <: ShaderElement]) {
      for (se <- map.values) {
        se.ini(programm)
      }
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

    def bind {
      assert(isInitialized)
      programm.use
    }

    def updateUniformData {
      bind
      matricen.set
      usetter foreach (_.apply())
      for (su <- uniformMap.values) {
        if (su.wasChanged) {
          gl.glUniform(su.getData)
        }
      }
    }

    def addUSetter(uss: UniformSetter) {
      usetter :+= uss
    }

    def isInitialized: Boolean = programm != null

    def getProgramm: ShaderProgramm = programm

    def getUniform(name: String) = uniformMap.get(name)

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

}