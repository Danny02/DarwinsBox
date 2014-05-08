package darwin.util.logging

import org.slf4j._
import scala.util.Try

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 08.11.13
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
trait LoggingComponent {
  lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def log[T](f: => T): Try[T] = {
    val t = Try(f)
    for(ex <- t.failed) ex match {
      case _: UnsupportedOperationException =>
      case ex => logger.warn(ex.getLocalizedMessage)
    }
    t
  }
}
