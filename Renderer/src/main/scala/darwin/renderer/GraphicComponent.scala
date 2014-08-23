package darwin.renderer

import java.nio.IntBuffer
import javax.media.opengl._

import com.jogamp.common.nio.Buffers
import com.jogamp.newt.opengl.GLWindow
import darwin.util.logging.LoggingComponent

import scala.concurrent.{Future, Promise}
import scala.util.Try

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */

trait GraphicComponent extends LoggingComponent {
  this: GProfile[_] =>

  def capabilities(c: GLCapabilities) {}

  val context = {
    val window = {
      GLProfile.initSingleton

      val capa = new GLCapabilities(profile)
      capabilities(capa)

      GLWindow.create(capa)
    }

    new GraphicContext(window)
  }

  val profile = if (profileName == null) {
    GLProfile.getMaximum(true)
  } else {
    GLProfile.get(profileName)
  }

  trait GLTypeGetter[E] {
    def get(glc: Int): E
  }

  object GLTypeGetter {

    implicit object IntGetter extends GLTypeGetter[Int] {
      private val buf: IntBuffer = Buffers.newDirectIntBuffer(1)

      def get(glc: Int) = {
        context.gl.glGetIntegerv(glc, buf)
        buf.get(0)
      }
    }
  }

  class GraphicContext(val window: GLWindow) {
    def gl = window.getGL

    def glContext = window.getContext

    def invoke(wait: Boolean)(f: GLAutoDrawable => Boolean) = window.invoke(wait, new GLRunnable {
      def run(d: GLAutoDrawable): Boolean = f(d)
    })

    def asyncInvoke(f: GLAutoDrawable => _) {
      invoke(false)(f.andThen(_ => true))
    }

    def futureInvoke[T](f: GLAutoDrawable => T): Future[T] = {
      val pro = Promise[T]

      invoke(false) { drawable =>
        pro.complete(Try(f(drawable)))
        true
      }

      pro.future
    }

    def directInvoke[T](f: GLAutoDrawable => T): T = {
      var t: Try[T] = null

      invoke(true) { drawable =>
        t = Try(f(drawable))
        true
      }

      t.get
    }
  }

  val maxSamples = get[Int](GL2ES3.GL_MAX_SAMPLES)

  def get[E](glConst: Int)(implicit g: GLTypeGetter[E]): E = g.get(glConst)
}

object GraphicComponent {
  implicit class GL2ES2Extras(gc: GraphicComponent with GProfile[GL2ES2]) {

    object Constants {
      val glslVersion = {
        var v: Int = 120
        try {
          val ver: String = gc.context.gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION)
          val s = ver.split(" ")(0).substring(0, 4)
          val d = s.substring(0, 4).toDouble
          v = Math.round(d * 100).toInt
        } catch {
          case ex: Throwable => {
            gc.logger.warn(ex.getLocalizedMessage)
          }
        }
        "#version " + v + '\n'
      }

      val maxColorAttachments = gc.get[Int](GL2ES2.GL_MAX_COLOR_ATTACHMENTS)
    }
  }
}

trait GProfile[+T <: GL] {
  val profileName: String

  implicit def toSpecific(gl: GL): T

  trait BaseProfile extends GProfile[GL] {
    val profileName = null

    implicit def toSpecific(gl: GL) = gl
  }

  trait GL2Profile extends GProfile[GL2] {
    val profileName = GLProfile.GL2

    implicit def toSpecific(gl: GL) = gl.getGL2
  }

  trait GL2GL3Profile extends GProfile[GL2GL3] {
    val profileName = GLProfile.GL2GL3

    implicit def toSpecific(gl: GL) = gl.getGL2GL3
  }

  trait GL2ES1Profile extends GProfile[GL2ES1] {
    val profileName = GLProfile.GL2ES1

    implicit def toSpecific(gl: GL) = gl.getGL2ES1
  }

  trait GL2ES2Profile extends GProfile[GL2ES2] {
    val profileName = GLProfile.GL2ES2

    implicit def toSpecific(gl: GL) = gl.getGL2ES2
  }

  trait GL3Profile extends GProfile[GL3] {
    val profileName = GLProfile.GL3

    implicit def toSpecific(gl: GL) = gl.getGL3
  }

  trait GL3bcProfile extends GProfile[GL3bc] {
    val profileName = GLProfile.GL3bc

    implicit def toSpecific(gl: GL) = gl.getGL3bc
  }

  trait GL4Profile extends GProfile[GL4] {
    val profileName = GLProfile.GL4

    implicit def toSpecific(gl: GL) = gl.getGL4
  }

  trait GL4bcProfile extends GProfile[GL4bc] {
    val profileName = GLProfile.GL4bc

    implicit def toSpecific(gl: GL) = gl.getGL4bc
  }

}

