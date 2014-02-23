package darwin.renderer.opengl


/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */


trait GLResource {
  val id: Int
  def delete() : Unit
}
