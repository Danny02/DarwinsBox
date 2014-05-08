/*
 * Copyright (C) 2014 Daniel Heinrich <Daniel.Heinrich@procon-it.de>
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

package darwin.renderer

import com.jogamp.opengl.util.texture.Texture
import javax.media.opengl.GL

trait TextureComponent {
  this: GraphicComponent =>

  import context._

  implicit class TextureOps(tex: Texture) {

    /**
     * Setzt die MIN und MAG Filterung der Textur sowie wie sie gewrapt werden
     * soll.
     * <p/>
     * @param tex       <br> Die Texture die bearbeitet werden soll. <br>
     * @param filtering <br> Es k�nnen nur folgende werte genutzt werden:
     *                  <br><br> GL_NEAREST <br> Liefert den Wert des
     *                  Texturelements, welches am n�chsten (in Manhattandistanz)
     *                  zum Zentrum des zu texturierenden Pixels liegt. <br><br>
     *                  GL_LINEAR <br> Liefert den gewichteten Mittelwert der
     *                  vier Teexturenelemente zur�ck, die dem Zentrum des zu
     *                  texturierenden Pixels am n�chsten sind. Dies kann
     *                  Randpixel-Elemente einschliessen, je nachdem wie
     *                  GL_TEXTURE_WRAP_S und GL_TEXTURE_WRAP_T eingestellt sind.
     *                  <br>
     * @param wrapstyle <br> Setzt die wrap funktion die f�r die Texture
     *                  Coordinaten S und T genutzt werden. Es k�nnen folgende
     *                  Symbole genutzt werden: <br><br> GL_CLAMP,
     *                  GL_CLAMP_TO_BORDER, GL_CLAMP_TO_EDGE, GL_REPEAT,
     *                  GL_MIRRORED_REPEAT
     */
    def setTexturePara(filtering: Int, wrapstyle: Int) {
      tex.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, filtering);
      tex.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, filtering);
      tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, wrapstyle);
      tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, wrapstyle);
    }
  }

}
