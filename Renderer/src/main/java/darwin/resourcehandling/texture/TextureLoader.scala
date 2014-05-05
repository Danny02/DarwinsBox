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
import com.jogamp.opengl.util.texture._
import com.jogamp.common.util.IOUtil
import java.io.IOException
import scala.util.Try
import darwin.renderer.{GProfile, GraphicComponent}
import javax.media.opengl.GL

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
object TextureLoader {
  final val TEXTURE_PATH_PREFIX = "resources/textures/"
  final val TEXTURE_PATH = Paths.get(TEXTURE_PATH_PREFIX)
}

trait TextureLoaderComponent {
  this: ResourceComponent with GraphicComponent =>

  implicit val textureLoader = new ResourceFromHandle[Texture] {
    def create(handle: ResourceFromHandle): Texture = {
      var a: Texture = null

      context.invoke(true, glad => {
        Try(
          a = TextureIO.newTexture(handle.getStream, true, IOUtil.getFileSuffix(handle.getName))
        ).isSuccess
      })

      if (a == null) throw new RuntimeException("Somehow the GLContext did not load this texture.")

      return a
    }

    def update(changed: ResourceFromHandle, old: Texture) {
      try {
        val data: TextureData = TextureIO.newTextureData(profile, changed.getStream, true, IOUtil.getFileSuffix(changed.getName))
        context.invoke(false, glad => {
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
}