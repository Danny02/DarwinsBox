package darwin.renderer


/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
trait Bindable {
  def bind(): Unit

  def unbind(): Unit

  def use[T](f: => T) = {
    bind
    val t = f
    unbind
    t
  }
}
