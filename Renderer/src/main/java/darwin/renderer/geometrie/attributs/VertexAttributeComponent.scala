package darwin.renderer.geometrie.attributs

import darwin.renderer.shader.Shader
import darwin.renderer.opengl.VBOComponent
import darwin.renderer.opengl.buffer.BufferObjectComponent
import darwin.renderer.Bindable

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */

trait VertexAttributeComponent {
  this: VBOComponent with BufferObjectComponent =>
  def createAttributes(shader: Shader, vbuffers: Seq[VertexBO], indice: Option[BufferObject]): Bindable
}



