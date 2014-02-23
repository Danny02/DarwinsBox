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

import java.io.IOException
import java.nio.file._
import darwin.renderer.GraphicComponent
import darwin.resourcehandling.factory.ResourceFromHandle
import darwin.resourcehandling.handle.ResourceHandle
import com.jogamp.common.util.IOUtil
import com.jogamp.opengl.util.texture._
import javax.inject.Inject
import scala.util.Try

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
object TextureLoader {
  final val TEXTURE_PATH_PREFIX = "resources/textures/"
  final val TEXTURE_PATH = Paths.get(TEXTURE_PATH_PREFIX)
}

class TextureLoader(@Inject val gc: GraphicComponent) extends ResourceFromHandle[Texture] {

  def create(handle: ResourceHandle): Texture = {
    var a: Texture = null

    gc.context.invoke(true, glad => {
      Try(
        a = TextureIO.newTexture(handle.getStream, true, IOUtil.getFileSuffix(handle.getName))
      ).isSuccess
    })

    if (a == null) throw new RuntimeException("Somehow the GLContext did not load this texture.")

    return a
  }

  def update(changed: ResourceHandle, old: Texture) {
    try {
      val data: TextureData = TextureIO.newTextureData(gc.context.gl.getGLProfile, changed.getStream, true, IOUtil.getFileSuffix(changed.getName))
      gc.context.invoke(false, glad => {
          old.updateImage(glad.getGL, data)
          true
      })
    }
    catch {
      case ex: IOException => {
      }
    }
  }

  def getFallBack: Texture = {
    throw new UnsupportedOperationException("Not supported yet.")
  }
}