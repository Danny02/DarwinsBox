package darwin.renderer.geometry.attributs

import darwin.renderer.opengl.VBOComponent
import darwin.renderer.opengl.buffer.BufferObjectComponent
import darwin.renderer.Bindable
import darwin.renderer.shader.ShaderComponent

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */

trait VertexAttributeComponent{
  this: VBOComponent with BufferObjectComponent with ShaderComponent =>
  def createAttributes(shader: Shader, vbuffers: Seq[VertexBO], indice: Option[BufferObject]): Bindable
}



