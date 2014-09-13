package darwin.renderer.opengl

import java.nio.IntBuffer

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */

trait GLResource {
  val id: Int
  def delete(): Unit

  override def hashCode: Int = {
    return id
  }

  override def equals(obj: Any) = obj match {
    case null => false
    case r: AnyRef if r eq this => true
    case bo: GLResource => bo.id == id
    case _ => false
  }
}

trait DeleteFunc {
  this: GLResource =>
  def deleteFunc: Int => Unit
  def delete() = deleteFunc(id)
}

trait Properties {
  this: GLResource =>

  def propertyFunc: (Int, Int, IntBuffer) => _
  def property(glconst: Int): Int = (propertyFunc(id, _:Int, _:IntBuffer)).apply(glconst)
}
