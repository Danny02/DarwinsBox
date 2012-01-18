/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dheinrich.darwin.ressourcen.wrapper;

import com.jogamp.opengl.util.texture.Texture;


/**
 *
 * @author dheinrich
 */
public class TextureContainer {
    private Texture texture;

    public TextureContainer(Texture texture) {
        this.texture = texture;
    }

    public TextureContainer() {
    }

    public Texture getTexture(){
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
