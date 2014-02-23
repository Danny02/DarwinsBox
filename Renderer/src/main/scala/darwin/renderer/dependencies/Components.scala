package darwin.renderer.dependencies

import darwin.renderer.opengl.VBOComponent
import darwin.renderer.opengl.buffer.BufferObjectComponent
import darwin.renderer.GraphicComponent

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 08.11.13
 * Time: 01:33
 * To change this template use File | Settings | File Templates.
 */
trait Components {
  type VBO = VBOComponent with BufferObjectComponent with GraphicComponent
}
