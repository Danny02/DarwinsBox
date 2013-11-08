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

import darwin.geometrie.unpacked.Material
import darwin.renderer.shader._
import com.google.common.base.Optional
import com.jogamp.opengl.util.texture.Texture

/**
 *
 * @author daniel
 */
object ShaderMaterial {
  type UniformSetter = () => Unit

  def create(shader: Shader, material: Material, setter: Seq[UniformSetter] = Seq()): UniformSetter = {
    val s = Seq(("mat_diffuse", material.getDiffuse),
      ("mat_ambient", material.getAmbient),
      ("mat_specular", material.getSpecular)).map(t => createSetter(shader, t._1, t._2: _*)) ++
      Seq(("diffuse_sampler", material.diffuseTex),
        ("specular_sampler", material.specularTex),
        ("alpha_sampler", material.alphaTex)
      ).map(t => createSetter(shader, t._1, t._2)) :+
      createSetter(shader, "mat_spec_exp", material.specular_exponet)

    () => s.flatten.foreach(_.apply())
  }

  implicit def optional2option[T](o: Optional[T]) = Option(o.orNull())

  def create(s: Shader, texs: Array[Texture], unames: String*): UniformSetter = {
    val setter = (texs zip unames) map {
      t =>
        s.getSampler(t._2).map(s => () => s bindTexture t._1)
    } flatten

    () => setter foreach (_.apply())
  }


  private def createSetter(shader: Shader, name: String, values: Float*): Option[UniformSetter] = {
    shader.getUniform(name).map(u => () => u.setData(values: _*))
  }

  private def createSetter(shader: Shader, name: String, texturePath: String): Option[UniformSetter] = ???
}