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
package darwin.renderer.geometry.packed

import darwin.geometrie.unpacked._
import darwin.renderer.shader._
import darwin.renderer.shader.uniform._
import com.jogamp.opengl.util.texture.Texture
import darwin.renderer.opengl.VBOComponent
import darwin.renderer.opengl.buffer.BufferObjectComponent

/**
 * Haelt alle Render relevanten Attribute eines 3D Modelles. Rendert ein Modell
 * nach diesen Attributen
 * <p/>
 * @author Daniel Heinrich
 */
trait RenderModelComponent {
  this: VBOComponent with BufferObjectComponent with RenderMeshComponent
    with ShaderMaterialComponent with ShaderComponent with SamplerComponent=>

  def create(model: Model, shader: Shader): RenderModel = {
    val material = model.getMat
    val m: Mesh = model.getMesh
    val vbo = createVBO(m.getVertices)
    val indices = Option(m.getIndicies).map(createIndexBuffer(_))

    val rbuffer = createRenderMesh(shader, indices, m.getPrimitiv_typ)(vbo)

    new RenderModel(rbuffer, shader, material)
  }

  def create(rbuffer: RenderMesh, shader: Shader, mat: Material): RenderModel = new RenderModel(rbuffer, shader, mat)

  class RenderModel(rbuffer: RenderMesh, var shader: Shader, material: Material) extends Shaded with Cloneable {
    private var uniforms = Seq[Apply]()

    setShader(shader)

    def render {
      if (shader.isInitialized) {
        uniforms foreach (_.apply())
        shader.updateUniformData
        rbuffer.render
      }
    }

    def setShader(shader: Shader) {
      this.shader = shader
      if (material != null) {
        uniforms = Seq[Apply]()
        uniforms :+= createMaterial(shader, material)
      }
    }

    def getShader = shader

    def addSamplerSetter(s: String, tc: Texture) {
      val sampler = shader.getSampler(s)
      sampler.foreach(s =>  uniforms :+= (() => s.bindTexture(tc)))
    }

    def addUniformSetter(us: Apply) {
      uniforms :+= us
    }

    override def clone: RenderModel = create(rbuffer.clone, shader, material)
  }

}