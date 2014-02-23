package darwin.renderer

import javax.media.opengl._
import com.jogamp.newt.opengl.GLWindow
import scala.Predef.String
import darwin.util.logging.LoggingComponent

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */


trait GraphicComponent {
  this: GProfile[_] with LoggingComponent =>

  def capabilities(c: GLCapabilities) {}

  val context = {
    val window = {
      GLProfile.initSingleton

      val p = if (profile == null) {
        GLProfile.getMaximum(true)
      }
      else {
        GLProfile.get(profile)
      }

      val capa = new GLCapabilities(p)
      capabilities(capa)

      GLWindow.create(capa)
    }

    new GraphicContext(window)
  }

  class GraphicContext(val window: GLWindow) {
    def gl = window.getGL

    def glContext = window.getContext

    def invoke(wait: Boolean, f: GLAutoDrawable => Boolean) = window.invoke(wait, new GLRunnable {
      def run(d: GLAutoDrawable): Boolean = f(d)
    })
  }

  val glslVersion = {
    var v: Int = 120
    try {
      val ver: String = context.gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION)
      val s = ver.split(" ")(0).substring(0, 4)
      val d = s.substring(0, 4).toDouble
      v = Math.round(d * 100).toInt
    }
    catch {
      case ex: Throwable => {
        logger.warn(ex.getLocalizedMessage)
      }
    }
    "#version " + v + '\n'
  }

  val maxSamples = {
    val container = new Array[Int](1)
    context.gl.glGetIntegerv(GL2GL3.GL_MAX_SAMPLES, container, 0)
    container(0)
  }

  val maxColorAttachments = {
    val container = new Array[Int](1)
    context.gl.glGetIntegerv(GL2ES2.GL_MAX_COLOR_ATTACHMENTS, container, 0)
    container(0)
  }
}

trait GProfile[+T <: GL] {
  val profile: String

  implicit def toSpecific(gl: GL): T


  trait GL2Profile extends GProfile[GL2] {
    val profile = GLProfile.GL2
    implicit def toSpecific(gl: GL) = gl.getGL2
  }

  trait GL2GL3Profile extends GProfile[GL2GL3] {
    val profile = GLProfile.GL2GL3
    implicit def toSpecific(gl: GL) = gl.getGL2GL3
  }

  trait GL2ES1Profile extends GProfile[GL2ES1] {
    val profile = GLProfile.GL2ES1
    implicit def toSpecific(gl: GL) = gl.getGL2ES1
  }

  trait GL2ES2Profile extends GProfile[GL2ES2] {
    val profile = GLProfile.GL2ES2
    implicit def toSpecific(gl: GL) = gl.getGL2ES2
  }

  trait GL3Profile extends GProfile[GL3] {
    val profile = GLProfile.GL3
    implicit def toSpecific(gl: GL) = gl.getGL3
  }

  trait GL3bcProfile extends GProfile[GL3bc] {
    val profile = GLProfile.GL3bc
    implicit def toSpecific(gl: GL) = gl.getGL3bc
  }

  trait GL4Profile extends GProfile[GL4] {
    val profile = GLProfile.GL4
    implicit def toSpecific(gl: GL) = gl.getGL4
  }

  trait GL4bcProfile extends GProfile[GL4bc] {
    val profile = GLProfile.GL4bc
    implicit def toSpecific(gl: GL) = gl.getGL4bc
  }
}

