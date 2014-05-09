/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.texture

import java.nio.file._
import darwin.resourcehandling.ResourceComponent
import darwin.resourcehandling.factory.ResourceFromHandle
import darwin.util.tag
import com.jogamp.opengl.util.texture._
import com.jogamp.common.util.IOUtil
import java.io.IOException
import scala.util.Try
import darwin.renderer.{ GProfile, GraphicComponent }
import javax.media.opengl.GL

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
object TextureLoader {
  final val TEXTURE_PATH_PREFIX = "resources/textures/"
}

trait TextureLoaderComponent {
  this: ResourceComponent with GraphicComponent with TextureComponent =>

  trait TextureLoader[T <: Texture] extends ResourceFromHandle[T] {
    def loadData(handle: ResourceFromHandle): TextureData = {
      TextureIO.newTextureData(profile, handle.getStream, true, IOUtil.getFileSuffix(handle.getName))
    }

    def createTex(handle: ResourceFromHandle): Texture = {
      val data = loadData(handle)
      context directInvoke TextureIO.newTexture(_.getGL, data)
    }

    def update(changed: ResourceFromHandle, old: T) {
      log {
        val data = loadData(changed)
        context ansyncInvoke old.updateImage(_.getGL, data)
      }
    }

    lazy val fallback: Texture = resource(TEXTURE_PATH_PREFIX + "error.dds")
  }

  implicit object SimpleTextureLoader extends TextureLoader[Texture] {
    def create(handle: ResourceFromHandle) = createTex(handle)

    def getFallBack = tag(fallback)
  }

  trait HeightMap
  implicit object HeightMapLoader extends TextureLoader[Texture @@ HeightMap] {
    def loadData(handle: ResourceFromHandle): TextureData = {
      TextureIO.newTextureData(profile, handle.getStream,
        GL2GL3.GL_LUMINANCE32F_ARB,
        GL2GL3.GL_LUMINANCE, true,
        IOUtil.getFileSuffix(handle.getName))
    }

    override def create(handle: ResourceFromHandle): Texture @@ HeightMap = {
      val data = loadData(handle)
      val tex = context directInvoke { glad =>
        val t = TextureIO.newTexture(glad.getGL, data)
        t.setTexturePara(GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
        t
      }
      tag(tex)
    }

    lazy val getFallBack: Texture @@ HeightMap = resource(TEXTURE_PATH_PREFIX + "error.dds")
  }

  trait CubeMap
  implicit object CubeMapLoader extends TextureLoader[Texture @@ CubeMap] {

    private val postfixes = Array("_RT", "_LT", "_UP", "_DN", "_FT", "_BK");

    def loadCubeFaces(handle: ResourceFromHandle): Seq[TextureData] = {
      val files = postfixes.map(handle(handle.getName + _ + ".dds"))
      files map loadData
    }

    def createCubeTexture(data: Seq[TextureData]) = context directInvoke {
      glad =>
        val cubemap = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);
        for (i <- 0 to 6) {
          cubemap.updateImage(glad.getGL, data(i), GL.GL_TEXTURE_CUBE_MAP + 2 + i);
        }
        cubemap.setTexturePara(GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
        cubemap
    }

    override def create(handle: ResourceFromHandle): Texture @@ HeightMap = {
      val data = loadCubeFaces(handle)
      tag(createCubeTexture(data))
    }

    def update(changed: ResourceFromHandle, old: T) {
      log {
        val data = loadCubeFaces(changed)
        context ansyncInvoke {
          glad =>
            for (i <- 0 to 6) {
              old.updateImage(glad.getGL, data(i), GL.GL_TEXTURE_CUBE_MAP + 2 + i);
            }
        }
      }
    }

    lazy val getFallBack = {
      val data = Array.fill(6)(SimpleTextureLoader.getFallBack)
      tag(createCubeTexture(data))
    }

  }
}