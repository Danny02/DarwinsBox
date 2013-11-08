package darwin.renderer

import javax.media.opengl._
import com.jogamp.newt.opengl.GLWindow

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 07.11.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */


trait GraphicComponent {
  this: GProfile[_] =>

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
}


//  gles1
//  gles2
//  gl2
//  gl2gl3
//  gl3
//  gl3bc
//  GL4
//  gl4bc

